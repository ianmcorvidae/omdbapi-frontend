(ns omdbapi-frontend.util-test
  (:require [clojure.test :refer :all]
            [omdbapi-frontend.util :refer :all]))

(deftest test-util
  (testing "select-values"
     (is (= (select-values {} []) []) "select-values produces an empty list with empty arguments")
     (is (= (select-values {:b-key "b" :a-key "a" :c-key "c"} [:a-key :b-key :c-key]) ["a" "b" "c"]) "select-values produces expected values when all keys exist")
     (is (= (select-values {:a-key "a" :b-key "b"} [:a-key :b-key :c-key]) ["a" "b" nil]) "select-values produces nil values for nonexistent keys")))
