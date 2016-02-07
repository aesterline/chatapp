(ns chatapp.core
  (:require
   [compojure.route          :as route]
   [environ.core             :refer (env)]
   [compojure.core           :refer (ANY GET POST defroutes)]
   [ring.util.response       :refer (response redirect content-type)]
   [ring.middleware.json :as json]
   [org.httpkit.server :as server])
  (:gen-class))

(def clients (atom #{}))

(defn- send-message [msg]
  (doseq [client @clients]
    (server/send! client msg)))

(defn handler [request]
  (server/with-channel request channel
    (swap! clients conj channel)
    (server/on-close channel   (fn [status] (swap! clients disj channel)))))

(defn create-message [request]
  (let [body (get-in request [:body :text])]
    (send-message (apply str (reverse body)))
    (response "Worked")))

(defroutes routes
  (POST "/message" [] (json/wrap-json-body create-message {:keywords? true :bigdecimals? true}))
  (GET "/events" [] handler)
  (GET "/" {c :context} (redirect (str c "/index.html")))
  (route/resources "/"))

(defn -main
  [& args]
  (let [ip   (env :chat-host "0.0.0.0")
        port (Integer/parseInt (env :chat-port "5000"))]
    (server/run-server routes {:ip ip :port port})))
