(ns chatapp.core
  (:require [reagent.core :as r]))

(defn- element [name]
  (.getElementById js/document name))

(def app-state (r/atom {:messages [{:style "init" :message "initial message"}]}))

(defn add-message! [style message]
  (swap! app-state (fn [state] (update-in state [:messages] conj {:style style :message message}))))

(defn message [message]
  [:div
   [:span {:class (:style message)}
    (:message message)]])

(defn message-list []
  [:div
   (for [m (:messages @app-state)]
     (message m))])

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

(defn oonclick [event]
  (.preventDefault event)

  (let [uri       (str "ws://" (.-host js/location) "/ws")
        websocket (js/WebSocket. uri)]
    (set! (.-onerror websocket) onerror)
    (set! (.-onopen websocket) onopen)
    (set! (.-onmessage websocket) onmessage)
    (set! (.-onclose websocket) onclose)

    (reset! socket websocket)))

(defn sonclick [event]
  (let [input (element "input")
        text  (.-value input)]
    (.send @socket text)
    (add-message! "sent" (str ">>> " text))))

(defn conclick [event]
  (.close @socket 1000 "Close button clicked"))

(defn onload []
  (let [openBtn  (element "open")
        sendBtn  (element "send")
        closeBtn (element "close")]
    (set! (.-onclick openBtn) oonclick)
    (set! (.-onclick sendBtn) sonclick)
    (set! (.-onclick closeBtn) conclick)))

(set! (.-onload js/window) onload)

(r/render-component
 [message-list]
 (element "messages"))
