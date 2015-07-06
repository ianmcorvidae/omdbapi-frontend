(defproject omdbapi-frontend "0.1.0-SNAPSHOT"
  :description "A basic Clojure frontend to the movies API provided by http://omdbapi.com"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/data.json "0.2.6"]
                 [compojure "1.3.1"]
                 [ring/ring-defaults "0.1.2"]
                 [hiccup "1.0.5"]
                 [clj-http "1.1.2"]]
  :plugins [[lein-ring "0.8.13"]]
  :ring {:handler omdbapi-frontend.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
