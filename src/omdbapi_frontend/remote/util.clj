(ns omdbapi-frontend.remote.util
  (:require [clojure.data.json :as json]
            [clj-http.client :as client]))

(def client-headers {"User-Agent" (str "omdbapi-frontend/" (System/getProperty "omdbapi-frontend.version") " clj-http")})

(defn json-request
  "Request JSON from a remote source and return as a map."
  [url & {:keys [params] :or {params {}}}]
  (json/read-str (:body
    (client/get url (conj params {:headers client-headers})))))

(defn wrap-key-missing-in
  [response-map test-key-vec cont]
  (if (= (get-in response-map test-key-vec) nil)
      nil
      cont))
