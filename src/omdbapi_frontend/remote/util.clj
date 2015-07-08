(ns omdbapi-frontend.remote.util
  (:require [clojure.data.json :as json]
            [clj-http.client :as client]
            [clojurewerkz.spyglass.client :as memcached]))

(def client-headers {"User-Agent" (str "omdbapi-frontend/" (System/getProperty "omdbapi-frontend.version") " clj-http")})

(defn json-request
  "Request JSON from a remote source and return as a map."
  [url & {:keys [params] :or {params {}}}]
  (json/read-str (:body
    (client/get url (conj params {:headers client-headers})))))

(defn memoize-memcached
  "Store the result of running a single-arity function in memcached for a configurable period, instead of requerying every time"
  [expire-time prefix f]
  (if-let [mc (memcached/text-connection "localhost:11211")]
    (fn [arg]
      (if-let [mc-val (try (memcached/get mc (str prefix ":" arg)) (catch net.spy.memcached.OperationTimeoutException e nil))]
              (json/read-str mc-val)
              (let [new-val (f arg)
                    _ (memcached/set mc (str prefix ":" arg) expire-time (json/write-str new-val))]
                   new-val)))
    f))

(defn wrap-key-missing-in
  [response-map test-key-vec cont]
  (if (= (get-in response-map test-key-vec) nil)
      nil
      cont))
