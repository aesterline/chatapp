(ns chatapp.core
  (:require [cljs-http.client :as http]
            [goog.dom :as dom]
            [reagent.core :as r]))

(def app-state (r/atom {:messages [{:style "init" :message "initial message"}]}))

(defn add-message! [style message]
  (swap! app-state (fn [state] (update-in state [:messages] conj {:style style :message message}))))

(defn onmessage [event]
  (add-message! "received" (str "<<< " (.-data event))))

(defn onerror [error]
  (add-message! "error" error))

(defn onopen [event]
  (let [current-target (.-currentTarget event)]
    (add-message! "opened" (str "Connected to " (.-url current-target)))))

(defn onclose [event]
  (add-message! "closed" (str "Disconnected: " (.-code event) " " (.-reason event))))

(def socket (atom nil))

(defn connect []
  (let [uri       (str "ws://" (.-host js/location) "/events")
        websocket (js/WebSocket. uri)]
    (set! (.-onerror websocket) onerror)
    (set! (.-onopen websocket) onopen)
    (set! (.-onmessage websocket) onmessage)
    (set! (.-onclose websocket) onclose)

    (reset! socket websocket)))

(defn sonclick [text event]
  (let [uri (str "http://" (.-host js/location) "/message")]
    (http/post uri {:json-params {:text text}})
    (add-message! "sent" (str ">>> " text))))

(defn conclick [event]
  (.close @socket 1000 "Close button clicked"))

(defn message [message]
  [:div
   [:span {:class (:style message)}
    (:message message)]])

(defn message-list []
  [:div
   (for [m (:messages @app-state)]
     (message m))])

(defn demo []
  (let [val (r/atom "")]
    (fn []
      [:div
       [:input {:type        "text"
                :value       @val
                :placeholder "Enter text to reverse!"
                :on-change   #(reset! val (-> % .-target .-value))}]
       [:button {:type     "button"
                 :on-click (partial sonclick @val)}
        "Send"]
       [:button {:type "button" :on-click conclick} "Close"]
       [message-list]])))

(defn start []
  (r/render-component
   [:div
    [:h1 "WebSocket Demo"]
    [demo]]
   (dom/getElement "root"))
  (connect))
