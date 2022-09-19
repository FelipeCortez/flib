(ns flib.auth
  (:require [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [buddy.auth.backends :as buddy.backends]
            [buddy.sign.jwt :as buddy.jwt]
            [compojure.core :refer [defroutes GET PUT POST]]
            [compojure.route :as route]
            [ring.adapter.jetty9 :as jetty]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.json :refer [wrap-json-body]]
            [ring.middleware.params :refer [wrap-params]]

            [flib.dom :as dom]
            [flib.time :as time]))

(comment
  (def jwt-secret "hunter2")

  (def signed
    (buddy.jwt/sign {:user "felipecortez"}
                    jwt-secret))

  (buddy.jwt/unsign signed "something-else")
  (buddy.jwt/unsign signed jwt-secret)

  (def signed'
    (buddy.jwt/sign {:user "felipecortez"
                     :iat (time/unix-time)
                     :exp (time/unix-time-plus-minutes 60)}
                    jwt-secret))

  (buddy.jwt/unsign signed' jwt-secret)

  nil)

(def !accounts (atom {}))

(defn with-auth-cookie [request username]
  (assoc-in request ["headers" "Set-Cookie"]
            (str "jwt="
                 (buddy.jwt/sign {:user username
                                  :iat (time/unix-time)
                                  :exp (time/unix-time-plus-minutes 60)}
                                 jwt-secret)
                 "; HttpOnly")))

;; in practice
(defroutes app
  (GET "/" request "home")
  (GET "/dbg" request
       (str @(def request request)))

  (GET "/register" []
       (dom/page
        {:body [:form {:action "/register" :method "POST"}
                [:input {:name "user"}]
                [:input {:name "password"}]
                [:input {:type "submit"}]]}))
  (POST "/register" {{:strs [user password]} :params}
        (swap! !accounts assoc user password)
        (with-auth-cookie {:status 301, :headers {"Location" "/"}}
          "felipecortez"))

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
        (wrap-params)
        (wrap-cookies)
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
