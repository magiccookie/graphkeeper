(defproject graphkeeper "0.1.0-SNAPSHOT"
  :description "Service to manage Neo4j graphs"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [com.taoensso/timbre "4.8.0"]
                 [compojure "1.5.2"]
                 [ring "1.5.1"]
                 [ring/ring-defaults "0.2.3"]
                 [ring/ring-json "0.4.0"]
                 [ring-cors "0.1.9"]
                 [fogus/ring-edn "0.3.0"]
                 [cheshire "5.7.0"]
                 [magiccookie/neocons "3.2.0-SNAPSHOT"]]
  :main ^:skip-aot graphkeeper.cli
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev { :dependencies [[prone "1.1.4"]] }}
  :plugins [[lein-ring "0.10.0"]
            [lein-ancient "0.6.10"]
            [cider/cider-nrepl "0.14.0"]]
  :ring {:handler graphkeeper.handler/app
         :auto-reload? true
         :stacktrace-middleware prone.middleware/wrap-exceptions
         :port 8000
         :nrepl {:start? true}})
