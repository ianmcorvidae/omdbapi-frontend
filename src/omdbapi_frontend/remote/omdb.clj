(ns omdbapi-frontend.remote.omdb
  (:require [omdbapi-frontend.remote.util :refer [json-request wrap-key-missing-in memoize-memcached]]
            [omdbapi-frontend.util :refer [select-values]]))

(defn- omdb-request
  "Request from the OMDB API given query params to include, returning a map."
  [params]
  (json-request "http://www.omdbapi.com/"
                :params {:query-params (conj {:type "movie" :plot "short" :r "json"} params)}))

(defn- wrap-omdb
  [response]
  {"omdb" response})

(defn- wrap-response-failed
  [response-map cont]
  (if (= (get response-map "Response") "False")
      nil
      cont))

(defn- format-omdb-query
  "Reformat an OMDB Search API response for returning to the client."
  [response-map]
  (->> (replace {"N/A" nil} (map #(select-values % ["Title" "Year" "imdbID"]) (get response-map "Search")))
       (wrap-key-missing-in response-map ["Search"])
       (wrap-response-failed response-map)
       (wrap-omdb)))

(defn- format-omdb-details
  "Reformat an OMDB API response for returning to the client."
  [response-map]
  (->> (replace {"N/A" nil} (select-values response-map ["Title" "Year" "imdbID" "Rated" "Released" "Runtime" "Genre" "Director" "Writer" "Actors" "Plot" "Language" "Country" "Awards" "Poster"]))
       (wrap-key-missing-in response-map ["imdbID"])
       (wrap-response-failed response-map)
       (wrap-omdb)))

; Memoize details but not query, since query is much more dynamic (and has two arguments)
(defn omdb-query
  "Request JSON from the OMDB Search API and format for returning to the client."
  [query year]
  (format-omdb-query (omdb-request {:s query :y year})))

(defn- omdb-details-raw
  "Request JSON from the OMDB API by IMDB ID and format for returning to the client. (unmemoized version)"
  [id]
  (format-omdb-details (omdb-request {:i id})))

(def omdb-details
  "Request JSON from the OMDB API by IMDB ID and format for returning to the client."
  (memoize-memcached 86400 "omdb-details" omdb-details-raw))
