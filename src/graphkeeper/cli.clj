(ns graphkeeper.cli
  (:require [graphkeeper.db :as db]
            [cheshire.core :refer :all :as json]
            [taoensso.timbre :as log]))

(defn verify-query [data])

(defn parse-json [input]
  (try
    (json/decode input true)
    (catch Exception e
      (log/warn "Invalid json" input))))

(defn parse-input [input]
  (let [data (parse-json input)]
    (if (not= nil data)
      (do (println data)))
    (verify-query data)))

(defn prompt-input
  ([]  (prompt-input ">"))
  ([s] (print s) (flush) (read-line)))

(defn start-cli [parser]
  (loop [input (prompt-input)]
    (if (= input "q")
      (println "quit")
      (do (parser input)
          (recur (prompt-input))))))

(defn -main [& args]
  (let [log-config  {:level :warn}]
    (log/merge-config! log-config)
    (start-cli parse-input)))
