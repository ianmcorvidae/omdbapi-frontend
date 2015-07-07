(ns omdbapi-frontend.util)

(defn select-values [map ks]
  (reduce #(conj %1 (map %2)) [] ks))
