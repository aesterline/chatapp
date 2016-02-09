(ns chatapp.core
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [cljs-http.client :as http]
            [chatapp.handlers]
            [chatapp.websocket :as websocket]
            [chatapp.view :as view]
            [goog.dom :as dom]
            [reagent.core :as r]
            [re-frame.core :as re-frame]))

;; Subscriptions
(re-frame/register-sub
 :message-input
 (fn [db _]
   (reaction (:message-input @db))))

(re-frame/register-sub
 :messages
 (fn [db _]
   (let [messages (reaction (:messages @db))]
     (reaction (filter #(= "message" (:type %)) @messages)))))

;; Other stuff
(defn start []
  (re-frame/dispatch-sync [:initialise-db])
  (r/render-component
   view/chat-ui
   (dom/getElement "root"))
  (websocket/connect))
