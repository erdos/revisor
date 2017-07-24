(ns fins.model
  (:require [fins.db :as db]))

(defn save-policy
  ([ref entity] (save-policy ref (System/currentTimeMillis) entity))
  ([ref version entity]
   (let [uuid  (db/->uuid)
         now   (System/currentTimeMillis)
         meta' (-> (db/get*   [:ref->meta ref]) first
                   (assoc-in  [:v-d-uuid version now]  uuid)
                   (assoc-in  [:d-v-uuid now version]  uuid))]
     (db/put* [:ref->meta ref]     meta'
              [:uuid->entity uuid] entity))))

(defn get-revision-uuids
  "Returns versions visible at a given datetime. Time machine functionality."
  ([ref] (get-revision-uuids  ref (System/currentTimeMillis)))
  ([ref as-of-time]
   (let [[meta] (db/get* [:ref->meta ref])]
     (reduce (fn [result [d v->uuid]]
               (if (<= d as-of-time)
                 (merge result v->uuid)
                 result)) {} (sort-by key (:d-v-uuid meta))))))

(defn get-history [ref]
  (let [[meta] (db/get* [:ref->meta ref])]
    (:d-v-uuid meta)))

(defn get-object-uuid-at
  ([ref]
   (let [t (System/currentTimeMillis)]
     (get-object-uuid-at ref t t)))
  ([ref effective-time]
   (get-object-uuid-at ref effective-time (System/currentTimeMillis)))
  ([ref effective-time as-of-time]
   (let [m (get-revision-uuids ref as-of-time)]
     (->> (keys m)
          (filter (partial >= effective-time))
          (apply max 0)
          (get m)))))

;; (get-object-uuid-at "007")

(defn get-object [uuid]
  (when uuid (first (db/get* [:uuid->entity uuid]))))

(def get-object-by-ref (comp get-object get-object-uuid-at))

#_
(defn get-object-current
  ([ref]
   (let [t (System/currentTimeMillis)]
     (get-object-current t t)))
  ([ref as-of-time]
   (get-object-current as-of-time as-of-time))
  ([ref as-of-time effective-time]
   (assert (<= as-of-time (System/currentTimeMillis))
           "Time machine can only go to the past.")
   ;; egy objektum adott ervenyessegu valtozatat adja, ahogyan ezt tortenetileg latni lehetett

   ))

(comment

  (save-policy "007" {:name "Bond"})

  (save-policy "007" {:name "James Bond"})

  (get-revision-uuids "007")

  )
