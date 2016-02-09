(ns chatapp.handlers
  (:require [chatapp.db :as database]
            [cljs-http.client :as http]
            [re-frame.core :as re-frame]
            [schema.core :as schema]))

(def message-id (atom 1))

(defn check-and-throw
  "throw an exception if db doesn't match the schema."
  [db]
  (if-let [problems  (schema/check database/schema db)]
    (throw (js/Error. (str "schema check failed: " problems)))))

(def check-schema-middleware (re-frame/after check-and-throw))

;; Handlers
(re-frame/register-handler
 :initialise-db
 check-schema-middleware
 (fn [_ _]
   {:messages      []
    :message-input {:text "" :focus true}}))

(re-frame/register-handler
 :add-message
 check-schema-middleware
 (fn [db [_ message]]
   (let [keyed-message (assoc message :key (swap! message-id inc))]
     (update-in db [:messages] conj keyed-message))))

(re-frame/register-handler
 :send-message
 check-schema-middleware
 (fn [db _]
   (let [uri     (str "http://" (.-host js/location) "/message")
         message (get-in db [:message-input :text])]
     (http/post uri {:json-params {:text message}})
     (re-frame/dispatch [:add-message {:type "debug" :message (str ">>> " message)}])
     (re-frame/dispatch [:message-input-text ""]))
   db))

(re-frame/register-handler
 :message-input-text
 check-schema-middleware
 (fn [db [_ message-text]]
   (assoc-in db [:message-input :text] message-text)))

(re-frame/register-handler
 :message-input-key-press
 check-schema-middleware
 (fn [db [_ key-code]]
   (when (= 13 key-code)
     (re-frame/dispatch [:send-message]))
   db))
