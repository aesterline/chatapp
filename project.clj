(defproject chatapp "0.1.0-SNAPSHOT"
  :description "Simple clojurescript chat app"
  :source-paths ["src-clj"]
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[compojure "1.4.0"]
                 [environ "1.0.2"]
                 [http-kit "2.1.18"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.7.228" :scope "provided"]
                 [ring/ring-core "1.4.0"]]

  :plugins [[lein-cljsbuild "1.1.2"]]

  :clean-targets ^{:protect false} [:target-path :compile-path "resources/public/js/main.js"]

  :hooks [leiningen.cljsbuild]

  :cljsbuild {:builds [{:source-paths ["src-cljs"]
                        :compiler {:output-to "resources/public/js/main.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]}
  :main ^:skip-aot chatapp.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
