(ns flib.dom
  (:require [hiccup.core :as h]
            [hiccup.page :refer [doctype include-css include-js]]
            [hiccup.util]))

(defn page [{:keys [body]}]
  (h/html
   {:mode :html}
   (doctype :html5)
   [:html
    [:head
     [:title "Hello"]
     [:meta {"charset" hiccup.util/*encoding*}]
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1"}]
     [:meta {:name "author"
             :content "Qui"}]
     [:link {:rel "stylesheet"
             :href "https://fonts.googleapis.com/css2?family=Source+Serif+Pro&display=swap"}]]
    [:body body]]))

(comment
  (page {:body [:div "Hi, world"]})
  nil)
