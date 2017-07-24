(ns fins.db
  (:require [mount.core :refer [defstate]]))

;; batch read-write

(def store (atom {}))

; (clojure.pprint/pprint store)

(defn ->uuid [] (java.util.UUID/randomUUID))

(defn put* [& kvs]
  (swap! store (fn [x] (apply assoc x kvs)))
  nil)

(defn get* [& keys] (mapv @store keys))
