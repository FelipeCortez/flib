(ns flib.books
  (:require [mundaneum.query :refer [search entity entity-data clojurize-claims describe label query *default-language*]]
            [mundaneum.properties :refer [wdt]]
            [flib.xt :as xt]
            [clj-http.client :as http]
            [jsonista.core :as j]
            [lambdaisland.deep-diff2 :as ddiff]
            [nextjournal.clerk :as clerk]))

(clerk/table
 (let [_run-at #inst "2023-06-20T23:03:10.772-00:00"]
   (map first (xt/q '{:find [(pull e [*])]
                      :where [[e :book/name]
                              [e :thing/tags :music]]}))))

(defn gbooks-search [term]
  (-> (http/get (str "https://www.googleapis.com/books/v1/volumes?q=" term))
      :body
      j/read-value))

(gbooks-search "discovery+of+slowness")

(comment
  (xt/submit-tx [[:xtdb.api/put
                  (assoc (ffirst (xt/q '{:find [(pull e [*])]
                                         :where [[e :xt/id #uuid "d24bc7e7-95bb-4754-962a-d36a2f76b4a9"]]}))
                         :book/pages 322
                         #_#_:book/notes "Recommended in the Netflix design TV show Abstract")]])

  (run! (fn [[a b]] (ddiff/pretty-print (ddiff/minimize (ddiff/diff a b))))
        (partition 2 1 (map :xtdb.api/doc (xt/entity-history #uuid "d24bc7e7-95bb-4754-962a-d36a2f76b4a9" :asc))))

  (search "The Discovery of Slowness")
  (clojurize-claims (entity-data :wd/Q911338))



  (-> (http/get "https://www.googleapis.com/books/v1/volumes?q=chord-chemistry")
      :body
      j/read-value)

  ;; (search "What the Dormouse Said")
  (clojurize-claims (entity-data :wd/Q7991611))

  (query `{:select [?pages]
           :where [[?pages ~(wdt :writing-language) ~(entity "Ancient Greek")]
                   [?person ~(wdt :occupation) ~(entity "philosopher")]]
           :limit 20})

  )
