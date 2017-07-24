(ns fins.ui
  (:require [fins.util :refer [defreq]]))

;; todo: parse date as date object (not string)

(defreq POST "/app/entity" [req succ err]
  ;; create new entity and redirect to it
  (succ {}))

(defreq GET "/app/entity/:ref" [req succ err]
  (succ {:status 200
         :headers {"Content-Type" "text/html"}
         :body (slurp (clojure.java.io/resource "document.html"))}))
