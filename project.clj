(defproject car "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [lein-light-nrepl "0.1.0"]
                 [org.clojure/tools.reader "0.8.3"]
                 [quil "2.2.4"]
                 [http-kit "2.1.16"]
                 [compojure "1.2.1"]
                 [cheshire "5.3.1"]

                 [org.clojure/clojurescript "0.0-2356"]]

  :plugins [[lein-cljsbuild "1.0.3"]]
  :source-paths ["src/clj"]

  :repl-options {:nrepl-middleware [lighttable.nrepl.handler/lighttable-ops]}
  
  :cljsbuild
  {:builds
   [{:source-paths ["src/cljs"]
     :compiler
     {:output-to "public/main.js"
      :optimizations :advanced
      :pretty-print false
      :preamble ["processing.min.js"]
      :externs ["externs/processing.js"]}}]
   })
