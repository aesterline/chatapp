(ns chatapp.core)

(defn ^:export output [style text]
  (let [messages         (.getElementById js/document "messages")
        current-messages (.-innerHTML messages)
        current-message  (str "<br/><span class='" style "'>" text "</span>")]
    (set! (.-innerHTML messages) (str current-messages current-message))))

(defn ^:export onmessage [event]
  (output "received" (str "<<< " (.-data event))))

(defn ^:export onerror [error]
  (output "error" error))

(defn ^:export onopen [event]
  (let [current-target (.-currentTarget event)]
    (output "opened" (str "Connected to " (.-url current-target)))))

(defn onclose [event]
  (output "closed" (str "Disconnected: " (.-code event) " " (.-reason event))))

(def socket (atom nil))

(defn ^:export oonclick [event]
  (output "click" "Button Clicked")

  (.preventDefault event)

  (let [uri       (str "ws://" (.-host js/location) "/ws")
        websocket (js/WebSocket. uri)]
    (set! (.-onerror websocket) onerror)
    (set! (.-onopen websocket) onopen)
    (set! (.-onmessage websocket) onmessage)
    (set! (.-onclose websocket) onclose)

    (reset! socket websocket)))

(defn ^:export sonclick [event]
  (let [input (.getElementById js/document "input")
        text  (.-value input)]
    (.send @socket text)
    (output "sent" (str ">>> " text))))

(defn ^:export conclick [event]
  (.close @socket 1000 "Close button clicked"))
