(ns flib.server
  (:require [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [compojure.core :refer [defroutes GET PUT POST]]
            [compojure.route :as route]
            [ring.adapter.jetty9 :as jetty]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.json :refer [wrap-json-body]]
            [ring.middleware.params :refer [wrap-params]]))

(defroutes app
  (route/files "/" {:root "build/"})
  (route/not-found "<h1>Num achei!</h1>"))

(defn wrap-restrict [handler]
  (fn [request]
    (if-not (authenticated? request)
      (throw-unauthorized)
      (handler request))))

(comment
  (defn server []
    (-> #'app
        (wrap-params)
        (wrap-cors :access-control-allow-origin [#".*"]
                   :access-control-allow-methods [:get :put :post :delete])
        (wrap-json-body {:keywords? true})
        (jetty/run-jetty {:port 3333 :join? false})))

  (def server-instance (server))
  (.start server)
  (.stop server-instance)

  nil)
