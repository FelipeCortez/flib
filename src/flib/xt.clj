(ns flib.xt
  (:require [clojure.java.io :as io]
            [xtdb.api :as xt]))

(defn start-xtdb! []
  (letfn [(kv-store [dir]
            {:kv-store {:xtdb/module 'xtdb.rocksdb/->kv-store
                        :db-dir (io/file dir)
                        :sync? true}})]
    (xt/start-node
     {:xtdb/tx-log (kv-store "data/dev/tx-log")
      :xtdb/document-store (kv-store "data/dev/doc-store")
      :xtdb/index-store (kv-store "data/dev/index-store")})))

(defonce xtdb-node (start-xtdb!))

(defn stop-xtdb! []
  (.close xtdb-node))

(defn q [q] (xt/q (xt/db xtdb-node) q))
(defn submit-tx [tx-ops] (xt/submit-tx xtdb-node tx-ops))
(defn entity-history [id order] (xt/entity-history (xt/db xtdb-node) id order {:with-docs? true}))

(comment
  xtdb-node

  (xt/submit-tx xtdb-node [[::xt/put {:xt/id "hi2u" :user/name "zig"}]])

  (xt/submit-tx xtdb-node
                (mapv (fn [book] [::xt/put (assoc book :xt/id (java.util.UUID/randomUUID))]) flib.csv/decoded-books))

  (xt/q (xt/db xtdb-node) '{:find [(pull e [*])]
                            :where [[e :user/name "zig"]]} )

  (xt/q (xt/db xtdb-node)
        '{:find [(pull e [*])]
          :where [[e :xt/id #uuid "35a94c14-386a-4049-8eab-b7d412de7a98"]]})

  (xt/entity (xt/db xtdb-node) #uuid "35a94c14-386a-4049-8eab-b7d412de7a98")

  (xt/q (xt/db xtdb-node)
        '{:find [(pull e [*])]
          :where [[e :book/name]]})

  (require '[flib.csv])

  (+ 2 2)


  )
