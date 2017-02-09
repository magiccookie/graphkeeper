(ns graphkeeper.edn
  (:require [clojure.edn :as edn]))

(defn read-edn [s]
  (edn/read-string s))

(defn convert-to-edn [col]
  (prn-str col))
