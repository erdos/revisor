(ns fins.util)

(defonce routes {})

(defmacro defreq [method url [request success error] body]
  (assert ('#{GET POST PUT DELETE} method))
  (assert (string? url))
  (let [method (keyword (.toLowerCase (name method)))
        r (for [itm (.split (str url) "/"), :when (seq itm)]
            (if (.startsWith (str itm) ":")
              (keyword (.substring (str itm) 1))
              (str itm)))
        assoc-path (concat [method] (map (fn [a] (if (keyword? a) :* a)) r) [:end])
        ks (filter keyword? r)]
    `(alter-var-root #'routes assoc-in ~(vec assoc-path)
                     {:fn (fn [~request ~success ~error] ~body)
                      :ks ~(vec ks)})))

(defn- req-handler [req]
  (let [url (remove empty? (.split (str (:uri req)) "/"))]
    (loop [url url
           routes (get routes (:request-method req))
           params []]
      (if-let [[u & url] (seq url)]
        (cond
          (contains? routes u) (recur url (get routes u) params)
          (contains? routes :*) (recur url (get routes :*) (conj params u)))
        (if-let [end (:end routes)]
          {:handler (:fn end)
           :route-params (zipmap (:ks end) params)})))))

; (req-handler {:uri "/api/projects" :request-method :get})
;; (req-handler {:uri "/api/query/ABCDEF/do" :request-method :get})
(defn handle-routes
  ([req success error]
   (if-let [h (req-handler req)]
     ((:handler h) (assoc req :route-params (:route-params h)) success error)
     (success {:status 404 :body "Route Not Found"}))))

(defn handle-content-type [response]
  (let [ct (or (get-in response [:headers "Content-Type"])
               (get-in response [:headers "content-type"])
               (get-in response [:headers :content-type]))]

    ))
