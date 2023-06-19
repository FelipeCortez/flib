(ns flib.books
  (:require [mundaneum.query :refer [search entity entity-data clojurize-claims describe label query *default-language*]]
            [mundaneum.properties :refer [wdt]]
            [flib.xt :as xt]
            [clj-http.client :as http]
            [jsonista.core :as j]
            [lambdaisland.deep-diff2 :as ddiff]))

(comment
  (xt/q '{:find [(pull e [*])]
          :where [[e :book/name]]})

  (xt/submit-tx [[:xtdb.api/put
                  (assoc (ffirst (xt/q '{:find [(pull e [*])]
                                         :where [[e :xt/id #uuid "d24bc7e7-95bb-4754-962a-d36a2f76b4a9"]]}))
                         :book/pages 322
                         #_#_:book/notes "Recommended in the Netflix design TV show Abstract")]])

  (run! (fn [[a b]] (ddiff/pretty-print (ddiff/minimize (ddiff/diff a b))))
        (partition 2 1 (map :xtdb.api/doc (xt/entity-history #uuid "d24bc7e7-95bb-4754-962a-d36a2f76b4a9" :asc))))

  (search "The Discovery of Slowness")
  (clojurize-claims (entity-data :wd/Q911338))

  (-> (http/get "https://www.googleapis.com/books/v1/volumes?q=discovery+of+slowness")
      :body
      j/read-value)

  (-> (http/get "https://www.googleapis.com/books/v1/volumes?q=chord-chemistry")
      :body
      j/read-value)

  ;; (search "What the Dormouse Said")
  (clojurize-claims (entity-data :wd/Q7991611))

  (query `{:select [?pages]
           :where [[?pages ~(wdt :writing-language) ~(entity "Ancient Greek")]
                   [?person ~(wdt :occupation) ~(entity "philosopher")]]
           :limit 20}))
