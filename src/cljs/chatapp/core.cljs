(ns chatapp.core
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [cljs-http.client :as http]
            [chatapp.websocket :as websocket]
            [goog.dom :as dom]
            [reagent.core :as r]
            [re-frame.core :as re-frame]))

(def app-state (r/atom {}))

;; Handlers
(re-frame/register-handler
 :initialise-db
 (fn [_ _]
   {:messages     [{:style "init" :message "initial message"}]
    :message-text ""}))

(re-frame/register-handler
 :add-message
 (fn [db [_ message]]
   (.log js/console (clj->js message))
   (update-in db [:messages] conj message)))

(re-frame/register-handler
 :send-message
 (fn [db _]
   (let [uri     (str "http://" (.-host js/location) "/message")
         message (:message-text db)]
     (http/post uri {:json-params {:text message}})
     (re-frame/dispatch [:add-message {:style "sent" :message (str ">>> " message)}]))))

(re-frame/register-handler
 :new-message-text
 (fn [db [_ message-text]]
   (.log js/console message-text)
   (assoc db :message-text message-text)))

;; Subscriptions
(re-frame/register-sub
 :message-text
 (fn [db _]
   (reaction (:message-text @db))))

(re-frame/register-sub
 :messages
 (fn [db _]
   (reaction (:messages @db))))

;; Other stuff
(defn message [message]
  [:div
   [:span {:class (:style message)}
    (:message message)]])

(defn message-list []
  (let [messages (re-frame/subscribe [:messages])]
    (fn []
      [:div
       (for [m @messages]
         (message m))])))

(defn demo []
  (let [val (re-frame/subscribe [:message-text])]
    (fn []
      [:div
       [:input {:type        "text"
                :value       @val
                :placeholder "Enter text to reverse!"
                :on-change   #(re-frame/dispatch [:new-message-text (-> % .-target .-value)])}]
       [:button {:type     "button"
                 :on-click #(re-frame/dispatch [:send-message])}
        "Send"]
       [message-list]])))

(defn start []
  (re-frame/dispatch-sync [:initialise-db])
  (r/render-component
   [:div
    [:h1 "WebSocket Demo"]
    [demo]]
   (dom/getElement "root"))
  (websocket/connect))
