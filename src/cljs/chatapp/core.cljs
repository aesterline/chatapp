(ns chatapp.core)

(defn- element [name]
  (.getElementById js/document name))

(defn output [style text]
  (let [messages         (element "messages")
        current-messages (.-innerHTML messages)
        current-message  (str "<br/><span class='" style "'>" text "</span>")]
    (set! (.-innerHTML messages) (str current-messages current-message))))

(defn onmessage [event]
  (output "received" (str "<<< " (.-data event))))

(defn onerror [error]
  (output "error" error))

(defn onopen [event]
  (let [current-target (.-currentTarget event)]
    (output "opened" (str "Connected to " (.-url current-target)))))

(defn onclose [event]
  (output "closed" (str "Disconnected: " (.-code event) " " (.-reason event))))

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
    (output "sent" (str ">>> " text))))

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
