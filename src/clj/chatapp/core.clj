(ns chatapp.core
  (:require
   [compojure.route          :as route]
   [environ.core             :refer (env)]
   [compojure.core           :refer (ANY GET defroutes)]
   [ring.util.response       :refer (response redirect content-type)]
   [org.httpkit.server :as server])
  (:gen-class))

(def clients (atom #{}))

(defn- message-received [msg]
  (doseq [client @clients]
    (server/send! client (apply str (reverse msg)))))

(defn handler [request]
  (server/with-channel request channel
    (swap! clients conj channel)
    (server/on-close channel   (fn [status] (swap! clients disj channel)))
    (server/on-receive channel (fn [data] ;; echo it back
                                 (future
                                   (Thread/sleep 5000)
                                   (message-received data))))))

(defroutes routes
  (GET "/ws" [] handler)
  (GET "/" {c :context} (redirect (str c "/index.html")))
  (route/resources "/"))

(defn -main
  [& args]
  (let [ip   (env :chat-host "0.0.0.0")
        port (Integer/parseInt (env :chat-port "5000"))]
    (server/run-server routes {:ip ip :port port})))
