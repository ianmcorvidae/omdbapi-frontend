(ns omdbapi-frontend.views
  (:require [ring.util.response :as r]
            [clojure.data.json :as json]
            [omdbapi-frontend.remote.omdb :as omdb])
  (:use [hiccup core page]))

; Frontend views
(defn homepage
  "Render the main page with an explanation and search bar" []
  (html5
    [:head
      [:title "OMDB API Search"]
      (include-css "/css/style.css")]
    [:body
      [:h2 "Search OMDB"]
      [:p "Use this page to search the " [:a {:href "http://www.omdbapi.com"} "OMDB API"] " for movies."]]))

; API views
(defn- json-view
  "Format and return values as a JSON response"
  [response-map]
  (-> (r/response (json/write-str response-map :escape-unicode false :escape-slash false))
      (r/header "Content-Type" "application/json; charset=utf-8")))

(defn api-search
  "Return a search API response after querying OMDB"
  [query year]
  (json-view (omdb/omdb-query query year)))

(defn api-details
  "Return more exact details after querying OMDB"
  [id]
  (json-view (omdb/omdb-details id)))
