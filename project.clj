(defproject chatapp "0.1.0-SNAPSHOT"
  :description "Simple clojurescript chat app"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[compojure "1.4.0"]
                 [environ "1.0.2"]
                 [org.clojure/clojure "1.8.0"]
                 [org.immutant/web "2.1.2"]
                 [ring/ring-core "1.4.0"]]
  :main ^:skip-aot chatapp.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
