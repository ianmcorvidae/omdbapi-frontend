(ns omdbapi-frontend.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [clojure.data.json :as json]
            [omdbapi-frontend.handler :refer :all])
  (:use clj-http.fake))

(def frozen-query-response {"Search" [{"Title" "Frozen", "Year" "2013", "imdbID" "tt2294629", "Type" "movie"} {"Title" "The Frozen Ground", "Year" "2013", "imdbID" "tt2005374", "Type" "movie"} {"Title" "Pompeii: The Mystery of the People Frozen in Time", "Year" "2013", "imdbID" "tt2806964", "Type" "movie"} {"Title" "Frozen Wasteland", "Year" "2013", "imdbID" "tt2006743", "Type" "N/A"} {"Title" "Frozen in Time: Our History in 3D", "Year" "2013–", "imdbID" "tt2560010", "Type" "series"} {"Title" "Frozen Echoes", "Year" "2013", "imdbID" "tt2994336", "Type" "movie"} {"Title" "Frozen Turntable", "Year" "2013", "imdbID" "tt3112548", "Type" "N/A"} {"Title" "Throwing Shade: 'Frozen' and Viking Sex", "Year" "2013", "imdbID" "tt3289214", "Type" "movie"} {"Title" "Frozen Ground", "Year" "2013", "imdbID" "tt3401746", "Type" "movie"} {"Title" "Lazy Bonez: Frozen Love", "Year" "2013", "imdbID" "tt3531818", "Type" "movie"}]})

(def frozen-detail-response {"Country" "USA", "Type" "movie", "Director" "Chris Buck, Jennifer Lee", "Released" "27 Nov 2013", "Title" "Frozen", "imdbID" "tt2294629", "Runtime" "102 min", "Writer" "Jennifer Lee (screenplay), Hans Christian Andersen (inspired by the story \"The Snow Queen\" by), Chris Buck (story), Jennifer Lee (story), Shane Morris (story), Dean Wellins (additional story)", "Response" "True", "imdbRating" "7.7", "Rated" "PG", "imdbVotes" "345894", "Poster" "http://ia.media-imdb.com/images/M/MV5BMTQ1MjQwMTE5OF5BMl5BanBnXkFtZTgwNjk3MTcyMDE@._V1_SX300.jpg", "Metascore" "74", "Awards" "Won 2 Oscars. Another 76 wins & 53 nominations.", "Actors" "Kristen Bell, Idina Menzel, Jonathan Groff, Josh Gad", "Plot" "When the newly crowned Queen Elsa accidentally uses her power to turn things into ice to curse her home in infinite winter, her sister, Anna, teams up with a mountain man, his playful reindeer, and a snowman to change the weather condition.", "Year" "2013", "Language" "English, Icelandic", "Genre" "Animation, Adventure, Comedy"})

(def frozen-wdq-response {"status" {"error" "OK", "items" 1, "querytime" "122ms", "parsed_query" "STRING[345:'tt2294629']"}, "items" [246283]})

(def frozen-wd-response {"entities" {"Q246283" {"sitelinks" {"enwiki" {"site" "enwiki", "title" "Frozen (2013 film)", "badges" ["Q17437798"]}}}}})

(with-fake-routes
  {
   {:address "http://www.omdbapi.com/"
    :query-params {:s "Frozen" :y "2013" :type "movie" :plot "short" :r "json"}} (fn [req] {:status 200 :headers {} :body (json/write-str frozen-query-response :escape-unicode false :escape-slash false)})
   {:address "http://www.omdbapi.com/"
    :query-params {:t "tt2294629" :type "movie" :plot "short" :r "json"}} (fn [req] {:status 200 :headers {} :body (json/write-str frozen-detail-response :escape-unicode false :escape-slash false)})
   {:address "http://wdq.wmflabs.org/api"
    :query-params {:q "string[345:\"tt2294629\"]"}} (fn [req] {:status 200 :headers {} :body (json/write-str frozen-wdq-response :escape-unicode false :escape-slash false)})
   "https://wikidata.org/entity/:246283.json" (fn [req] {:status 200 :headers {} :body (json/write-str frozen-wd-response :escape-unicode false :escape-slash false)})
  }
  (deftest test-app
    (testing "main route"
      (let [response (app (mock/request :get "/"))]
        (is (= (:status response) 200))))

    (testing "search API"
      (let [response (app (mock/request :get "/api/search?q=Frozen&y=2013"))
            response-body-map (json/read-str (:body response))]
        (is (= (:status response) 200))
        (is (not= (get-in response-body-map ["omdb"]) nil))
        (is (= (first (get-in response-body-map ["omdb"])) ["Frozen" "2013" "tt2294629"]))))

    (testing "details API"
      (let [response (app (mock/request :get "/api/details?id=tt2294629"))
            response-body-map (json/read-str (:body response))]
        (is (= (:status response) 200))
        (is (not= (get-in response-body-map ["omdb"]) nil))
        (is (not= (get-in response-body-map ["wikidata"]) nil))))

    (testing "resources (css)"
      (let [response (app (mock/request :get "/css/style.css"))]
        (is (= (:status response) 200))))

    (testing "not-found route"
      (let [response (app (mock/request :get "/invalid"))]
        (is (= (:status response) 404))))))
