(ns chatapp.core
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [cljs-http.client :as http]
            [chatapp.websocket :as websocket]
            [chatapp.view :as view]
            [goog.dom :as dom]
            [reagent.core :as r]
            [re-frame.core :as re-frame]))

(def app-state (r/atom {}))

;; Handlers
(re-frame/register-handler
 :initialise-db
 (fn [_ _]
   {:messages      [{:style "init" :message "initial message"}]
    :message-input {:text "" :focus true}}))

(re-frame/register-handler
 :add-message
 (fn [db [_ message]]
   (update-in db [:messages] conj message)))

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

;; Subscriptions
(re-frame/register-sub
 :message-input
 (fn [db _]
   (reaction (:message-input @db))))

(re-frame/register-sub
 :messages
 (fn [db _]
   (reaction (:messages @db))))

;; Other stuff
(defn start []
  (re-frame/dispatch-sync [:initialise-db])
  (r/render-component
   view/chat-ui
   (dom/getElement "root"))
  (websocket/connect))
