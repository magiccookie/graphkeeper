(ns graphkeeper.db
  (:require [clojurewerkz.neocons.rest :as nr]
            [clojurewerkz.neocons.rest.nodes :as nn]
            [clojurewerkz.neocons.rest.relationships :as nrl]))

(def db-url (System/getenv "NEO4J_URL"))

(defn tx-single-write
  ([node] (let [conn (nr/connect db-url)]
            (nn/create conn node)))
  ([node-1 node-2 rel] (let [conn (nr/connect db-url)]
                         (nrl/create conn
                                     (nn/create conn node-1)
                                     (nn/create conn node-2)
                                     rel))))

(defn tx-batch-write [& args])
