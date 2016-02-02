(ns chatapp.core
  (:require
   [compojure.route          :as route]
   [environ.core             :refer (env)]
   [compojure.core           :refer (ANY GET defroutes)]
   [ring.util.response       :refer (response redirect content-type)]
   [org.httpkit.server :as server])
  (:gen-class))

(defn handler [request]
  (server/with-channel request channel
    (server/on-close channel   (fn [status] (println "channel closed: " status)))
    (server/on-receive channel (fn [data] ;; echo it back
                                 (server/send! channel (apply str (reverse data)))))))

(defroutes routes
  (GET "/ws" [] handler)
  (GET "/" {c :context} (redirect (str c "/index.html")))
  (route/resources "/"))

(defn -main
  [& args]
  (let [ip   (env :chat-host "0.0.0.0")
        port (Integer/parseInt (env :chat-port "5000"))]
    (server/run-server routes {:ip ip :port port})))
