(defproject chatapp "0.1.0-SNAPSHOT"
  :description "Simple clojurescript chat app"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[cljs-http "0.1.39"]
                 [compojure "1.4.0"]
                 [environ "1.0.2"]
                 [http-kit "2.1.18"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.7.228" :scope "provided"]
                 [reagent "0.6.0-alpha"]
                 [ring/ring-core "1.4.0"]
                 [ring/ring-json "0.4.0"]]

  :plugins [[lein-cljsbuild "1.1.1"]
            [lein-pprint "1.1.1"]]

  :source-paths ["src/clj" "src/cljs" "dev"]
  :test-paths ["test/clj"]

  :clean-targets ^{:protect false} [:target-path :compile-path "resources/public/js/compiled"]

  :cljsbuild {:builds
              {:app
               {:source-paths ["src/cljs"]

                :figwheel     true
                ;; Alternatively, you can configure a function to run every time figwheel reloads.
                ;; :figwheel {:on-jsload "chatapp.core/on-figwheel-reload"}

                :compiler     {:main                 chatapp.core
                               :asset-path           "js/compiled/out"
                               :output-to            "resources/public/js/compiled/main.js"
                               :output-dir           "resources/public/js/compiled/out"
                               :source-map-timestamp true}}}}

  :figwheel {;; :http-server-root "public"       ;; serve static assets from resources/public/
             ;; :server-port 3449                ;; default
             ;; :server-ip "127.0.0.1"           ;; default
             :css-dirs       ["resources/public/css"] ;; watch and update CSS

             ;; Instead of booting a separate server on its own port, we embed
             ;; the server ring handler inside figwheel's http-kit server, so
             ;; assets and API endpoints can all be accessed on the same host
             ;; and port. If you prefer a separate server process then take this
             ;; out and start the server with `lein run`.
             :ring-handler   chatapp.core/routes

             ;; Start an nREPL server into the running figwheel process. We
             ;; don't do this, instead we do the opposite, running figwheel from
             ;; an nREPL process, see
             ;; https://github.com/bhauman/lein-figwheel/wiki/Using-the-Figwheel-REPL-within-NRepl
             ;; :nrepl-port 7888

             ;; To be able to open files in your editor from the heads up display
             ;; you will need to put a script on your path.
             ;; that script will have to take a file path and a line number
             ;; ie. in  ~/bin/myfile-opener
             ;; #! /bin/sh
             ;; emacsclient -n +$2 $1
             ;;
             ;; :open-file-command "myfile-opener"

             :server-logfile "log/figwheel.log"}

  :doo {:build "test"}

  :profiles {:dev
             {:dependencies [[figwheel "0.5.0-6"]
                             [figwheel-sidecar "0.5.0-6" :exclusions [http-kit]]
                             [com.cemerick/piggieback "0.2.1"]
                             [org.clojure/tools.nrepl "0.2.12"]]

              :plugins      [[lein-figwheel "0.5.0-6" :exclusions [[org.clojure/tools.reader]
                                                                   [org.clojure/clojure]]]
                             [lein-doo "0.1.6" :exclusions [[org.clojure/tools.reader]
                                                            [org.clojure/clojure]]]]

              :cljsbuild    {:builds
                             {:test
                              {:source-paths ["src/cljs" "test/cljs"]
                               :compiler
                               {:output-to     "resources/public/js/compiled/testable.js"
                                :main          'chatapp.test-runner
                                :optimizations :none}}}}}}

  :main ^:skip-aot chatapp.core
  :target-path "target/%s")
