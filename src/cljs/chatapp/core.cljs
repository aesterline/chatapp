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
   (update-in db [:messages] conj message)))

(re-frame/register-handler
 :send-message
 (fn [db _]
   (let [uri     (str "http://" (.-host js/location) "/message")
         message (:message-text db)]
     (http/post uri {:json-params {:text message}})
     (re-frame/dispatch [:add-message {:style "sent" :message (str ">>> " message)}]))
   db))

(re-frame/register-handler
 :new-message-text
 (fn [db [_ message-text]]
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
  [:li {:class "list-group-item"}
   (:message message)])

(defn message-list []
  (let [messages (re-frame/subscribe [:messages])]
    (fn []
      [:ul {:class "list-group"}
       (for [m @messages]
         (message m))])))

(defn demo []
  (let [val (re-frame/subscribe [:message-text])]
    (fn []
      [:div {:class "container-fluid"}
       [:div
        [message-list]]
       [:div {:class "row"}
        [:div {:class "col-lg-6"}
         [:div {:class "input-group"}
          [:input {:type        "text"
                   :value       @val
                   :placeholder "Enter text to reverse!"
                   :class       "form-control"
                   :on-change   #(re-frame/dispatch [:new-message-text (-> % .-target .-value)])}]
          [:span {:class "input-group-btn"}
           [:button {:type     "button"
                     :class    "btn btn-default"
                     :on-click #(re-frame/dispatch [:send-message])}
            "Send"]]
          ]]]])))

(defn start []
  (re-frame/dispatch-sync [:initialise-db])
  (r/render-component
   [:div
    [:h1 "WebSocket Demo"]
    [demo]]
   (dom/getElement "root"))
  (websocket/connect))
