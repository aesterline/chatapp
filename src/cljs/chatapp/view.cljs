(ns chatapp.view
  (:require [re-frame.core :as re-frame]))

(defn message [message]
  [:li {:class "list-group-item"}
   (:message message)])

(defn message-list []
  (let [messages (re-frame/subscribe [:messages])]
    (fn []
      [:div {:class "row"}
       [:div {:class "col-lg-6"}
        [:ul {:class "list-group"}
         (for [m @messages]
           (message m))]]])))

(defn message-input []
  (let [input (re-frame/subscribe [:message-input])]
    [:div {:class "row"}
     [:div {:class "col-lg-6"}
      [:div {:class "input-group"}
       [:input {:type        "text"
                :value       (:text @input)
                :placeholder "Enter text to reverse!"
                :class       "form-control"
                :on-change   #(re-frame/dispatch [:message-input-text (-> % .-target .-value)])}]
       [:span {:class "input-group-btn"}
        [:button {:type     "button"
                  :class    "btn btn-default"
                  :on-click #(re-frame/dispatch [:send-message])}
         "Send"]]
       ]]]))

(defn chat-ui []
  [:div {:class "container-fluid"}
   [:h1 "ChatApp"]
   [message-list]
   [message-input]])
