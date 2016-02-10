(ns chatapp.view
  (:require [re-frame.core :as re-frame]))

(defn message [message]
  [:li {:class "list-group-item" :key (:key message)}
   (:message message)])

(defn message-list []
  (let [messages (re-frame/subscribe [:messages])]
    (fn []
      [:div {:class "row"}
       [:div {:class "col-sm-6"}
        [:ul {:class "list-group"}
         (for [m @messages]
           (message m))]]])))

(defn message-input []
  (let [input (re-frame/subscribe [:message-input])]
    (fn []
      [:input {:type         "text"
               :value        (:text @input)
               :placeholder  "Enter text to reverse!"
               :class        "form-control"
               :auto-focus   true
               :on-key-press #(re-frame/dispatch [:message-input-key-press (.-charCode %)])
               :on-change    #(re-frame/dispatch [:message-input-text (-> % .-target .-value)])}])))

(defn message-composer []
  [:div {:class "row"}
   [:div {:class "col-sm-6"}
    [:div {:class "input-group"}
     [message-input]
     [:span {:class "input-group-btn"}
      [:button {:type     "button"
                :class    "btn btn-default"
                :on-click #(re-frame/dispatch [:send-message])}
       "Send"]]]]])

(defn chat-ui []
  [:div {:class "container pink"}
   [:div {:class "row green"}
    [:div {:class "col-md-12"}
     [:h1 "ChatApp"]]]
   [:div {:class "row red"}
    [:div {:class "col-md-9"}
     [message-list]
     [message-composer]]]])
