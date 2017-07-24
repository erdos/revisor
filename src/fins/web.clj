(ns fins.web
  (:require [mount.core :refer [defstate]]
            [clojure.pprint :refer [pprint]]
            [fins.model :as model]
            [org.httpkit.server :as s]
            [ring.middleware.params :as rmp]
            [fins.util :refer [handle-routes defreq]]
            [fins.ui]))

'
(defreq POST "/entity" [req succ err]
  ;; create new entity and maybe redirect to it?
  )

'
(defreq POST "/entity/:ref" [req succ err]
  ;; modify item at ref
  (succ {})
  )

(defreq POST "/entity/:ref/at/:time" [req succ err]
  ;; modify item with a given effective date
  (succ {}))

(defreq GET "/debug/echo" [req succ err] (succ {:status 200 :body req}))

;; get entity by uuid
(defreq GET "/entity" [req succ err]
  (let [uuid (get-in req [:query-params "uuid"])]
    (if (empty? uuid)
      (succ {:status 400 :body "Expected uuid parameter"})
      (if-let [object (model/get-object uuid)]
        (succ {:status 200 :body object})
        (succ {:status 404 :body "404 - Object Not found with UUID."})))))

(defreq GET "/entity/:ref" [req succ err]
  (succ
   {:status 200
    :body (model/get-object-by-ref (-> req :route-params :ref))}))

(defreq GET "/entity/:ref/at/:time" [req succ err]
  (succ
   {:status 200
    :body (model/get-object-by-ref (-> req :route-params :ref)
                                   (-> req :route-params :time))}))

(defreq GET "/entity/:ref/at/:time/asof/:asof" [req succ err]
  (succ
   {:status 200
    :body (model/get-object-by-ref (-> req :route-params :ref)
                                   (-> req :route-params :time)
                                   (-> req :route-params :asof))}))

(defreq GET "/entity/:ref/versions" [req succ err]
  (succ
   {:status 200
    :body (model/get-revision-uuids (-> req :route-params :ref))}))

(defreq GET "/entity/:ref/versions/asof/:asof" [req succ err]
  (succ
   {:status 200
    :body (model/get-revision-uuids (-> req :route-params :ref)
                                    (-> req :route-params :asof))}))

(defreq GET "/entity/:ref/history" [req succ err]
  (succ
   {:status 200
    :body (model/get-history (-> req :route-params :ref))}))


(defstate Handler :start
  (fn [req]
    (let [r (promise)]
      (handle-routes req (partial deliver r) (partial deliver r))
      (let [resp (deref r)]
        {:status (:status resp)
         :body (if (string? (:body resp))
                 (:body resp)
                 (with-out-str (pprint (:body resp))))}))))

(defstate WebServerConfig :start
  {:port 8080})

(defstate WebServer :start
  (s/run-server (rmp/wrap-params Handler) WebServerConfig)
  :stop (WebServer))
