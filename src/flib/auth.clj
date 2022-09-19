(ns flib.auth
  (:require [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [buddy.auth.backends :as buddy.backends]
            [buddy.sign.jwt :as buddy.jwt]
            [compojure.core :refer [defroutes GET PUT POST]]
            [compojure.route :as route]
            [ring.adapter.jetty9 :as jetty]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.json :refer [wrap-json-body]]
            [ring.middleware.params :refer [wrap-params]]

            [flib.dom :as dom]))

(def jwt-secret "hunter2")

(defroutes app
  (GET "/" request)
  (GET "/login" []
       (dom/page
        {:body [:form {:action "/login" :method "POST"}
                [:input {:name "user"}]
                [:input {:name "password"}]
                [:input {:type "submit"}]]}))
  (POST "/login" {{:strs [user password]} :params}
        (do (def request request)
            (dom/page {:body [:pre (str user "-" password "-"
                                        (buddy.jwt/sign {:user user}
                                                        jwt-secret))]})))
  (route/not-found (dom/page {:body [:div "Hi, world"]})))

(defn wrap-auth [handler]
  (fn [request]
    (if-not (authenticated? request)
      (throw-unauthorized)
      (handler request))))

(def jws-backend*
  (buddy.backends/jws {:secret jwt-secret}))

(defn server []
    (-> #'app
        (wrap-auth)
        (wrap-params)
        (wrap-cors :access-control-allow-origin [#".*"]
                   :access-control-allow-methods [:get :put :post :delete])
        (wrap-json-body {:keywords? true})
        (jetty/run-jetty {:port 3333 :join? false})))

(comment
  ; Auth guide at https://funcool.github.io/buddy-auth/latest/user-guide.html

  (def server-instance (server))
  (.start server)
  (.stop server-instance)

  (authenticated? r)

  nil)
