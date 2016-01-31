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
