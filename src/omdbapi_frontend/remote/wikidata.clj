(ns omdbapi-frontend.remote.wikidata
  (:require [omdbapi-frontend.remote.util :refer [json-request wrap-key-missing-in]]
            [omdbapi-frontend.remote.wdq :refer [wdq-imdb-id-to-item]]))

(defn- wd-entity-request
  "Request from the WD API, returning a map."
  [id]
  (json-request (str "https://wikidata.org/entity/" id ".json")))

(defn- wrap-wikidata
  [response]
  {"wikidata" response})

(defn- filter-reformat-wikilanguages
  [response-map]
  (let [langs ["en" "de" "es" "fr"]]
         (filter identity (map (fn [lang]
                (let [title (get-in response-map [(str lang "wiki") "title"])]
                  (if title (list (str lang ":" title) (str "https://" lang ".wikipedia.org/wiki/" title)) nil)))
              langs))))

(defn- format-wd-entity
  "Reformat a Wikidata response for returning to the client."
  [response-map id]
  (->> (filter-reformat-wikilanguages (get-in response-map ["entities" id "sitelinks"]))
       (wrap-key-missing-in response-map ["entities" id "sitelinks"])
       (wrap-wikidata)))

(defn wd-details
  "Request JSON from Wikidata by IMDB ID and format for returning to the client."
  [id]
  (let [wikidata-id (wdq-imdb-id-to-item id)]
    (if wikidata-id
        (format-wd-entity (wd-entity-request wikidata-id) wikidata-id)
        (wrap-wikidata nil))))
