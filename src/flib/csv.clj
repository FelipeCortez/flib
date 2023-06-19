(ns flib.csv
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.set]))

(defn csv-data->maps [csv-data]
  (map zipmap
       (->> (first csv-data)
            (map (comp keyword str/lower-case str/trim)) repeat)
       (rest csv-data)))

(def Tags
  [:set {:decode/string #(clojure.string/split % #", ")} keyword?])

(def Book
  [:map
   [:book/name string?]
   [:thing/tags Tags]
   [:book/pages
    {:optional? true
     :decode/string #(if (str/blank? %) nil %)}
    pos-int?]

   [:book/notes
    {:optional? true
     :decode/string #(if (str/blank? %) nil %)}
    string?]])

(def Books [:sequential Book])

(require '[malli.transform :as mt])
(require '[malli.core :as m])

(def kt
  {:name :book/name
   :páginas :book/pages
   :obs :book/notes
   :tags :thing/tags})


(def books
  (->> (slurp "/Users/felipecortez/Downloads/wishlivros.csv")
       csv/read-csv
       csv-data->maps))

(def decoded-books
  (m/decode Books books
            (mt/transformer
             (mt/key-transformer {:decode kt})
             (mt/string-transformer))))
