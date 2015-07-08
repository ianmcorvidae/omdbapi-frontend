(ns omdbapi-frontend.remote.wdq
  (:require [omdbapi-frontend.remote.util :refer [json-request]]))

(defn- wdq-request
  "Request from the WDQ API, returning a map."
  [params]
  (json-request "http://wdq.wmflabs.org/api"
                :params {:query-params params}))

(defn wdq-imdb-id-to-item
  "Resolve an IMDB ID into a Wikidata Item ID"
  [imdb-id]
  (let [wd-imdbid-property "345"
        wdq-response-map (wdq-request {:q (str "string[" wd-imdbid-property ":\"" imdb-id "\"]")})]
    (if (> (get-in wdq-response-map ["status" "items"]) 0)
        (str "Q" (first (get wdq-response-map "items")))
        nil)))
