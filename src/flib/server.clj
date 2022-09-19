(ns flib.server
  (:require [site.templates.home :refer [beba-page]]
            [site.templates.base :refer [*reloader?*]]
            [compojure.core :refer [defroutes GET PUT POST]]
            [compojure.route :as route]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.json :refer [wrap-json-body]]
            [ring.middleware.params :refer [wrap-params]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]))

(defroutes app
  (route/files "/" {:root "build/"})
  (route/not-found "<h1>Página não encontrada :(</h1>"))

(defn wrap-restrict [handler]
  (fn [request]
    (if-not (authenticated? request)
      (throw-unauthorized)
      (handler request))))


(defn -main [] (server))

(comment
  (defn server []
    (-> #'app
        #_(wrap-authentication auth/backend)
        #_(wrap-authorization auth/backend)
        (wrap-params)
        (wrap-cors :access-control-allow-origin [#".*"]
                   :access-control-allow-methods [:get :put :post :delete])
        (wrap-json-body {:keywords? true})
        (jetty/run-jetty {:port 3000 :join? false})))

  (def server-instance (server))
  (.start server)
  (.stop server-instance)

  nil)
