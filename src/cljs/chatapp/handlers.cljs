(ns chatapp.handlers
  (:require [cljs-http.client :as http]
            [re-frame.core :as re-frame]))

(def message-id (atom 1))

;; Handlers
(re-frame/register-handler
 :initialise-db
 (fn [_ _]
   {:messages      []
    :message-input {:text "" :focus true}}))

(re-frame/register-handler
 :add-message
 (fn [db [_ message]]
   (let [keyed-message (assoc message :key (swap! message-id inc))]
     (update-in db [:messages] conj keyed-message))))

(re-frame/register-handler
 :send-message
 (fn [db _]
   (let [uri     (str "http://" (.-host js/location) "/message")
         message (get-in db [:message-input :text])]
     (http/post uri {:json-params {:text message}})
     (re-frame/dispatch [:add-message {:style "sent" :message (str ">>> " message)}])
     (re-frame/dispatch [:message-input-text ""]))
   db))

(re-frame/register-handler
 :message-input-text
 (fn [db [_ message-text]]
   (assoc-in db [:message-input :text] message-text)))

(re-frame/register-handler
 :message-input-key-press
 (fn [db [_ key-code]]
   (when (= 13 key-code)
     (re-frame/dispatch [:send-message]))
   db))
