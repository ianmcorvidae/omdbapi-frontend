(ns omdbapi-frontend.handler
  (:require [compojure.core :refer :all]
            [omdbapi-frontend.views :refer [homepage api-search api-details]]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [hiccup.middleware :refer [wrap-base-url]]))

(defroutes app-routes
  (GET "/" [] (homepage))

  (context "/api" []
    (GET "/search" {{q :q y :y} :params} (api-search q y))
    (GET "/details" {{id :id} :params} (api-details id)))

  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (wrap-base-url (wrap-defaults app-routes site-defaults)))
