
(ns confo.core
  (:require [clojure.string :as s]))

(defn class-of [option value]
  (class option))

(defmulti coerce class-of)

(defmethod coerce Long
  [option value]
  (Integer/parseInt value))

(defmethod coerce IPersistentVector
  [option value]
  (s/split value #","))

(defmethod coerce :default
  [option value]
  value)

(defn- to-hash-map [prefix entry]
  (hash-map (-> (.getKey entry)
                (subs (inc (count prefix)))
                (s/lower-case)
                (s/replace "_" "-")
                (keyword))
            (.getValue entry)))

(defn- has-prefix [prefix string]
  (.startsWith (s/lower-case string)
               (str (s/lower-case prefix) "_")))

(defn- with-type
  "Tries to coerce a value to a type, if the k is present in options"
  [options k v]
  (hash-map k (coerce (get options k) v)))

(defn- typed
  "Coerce config values to matching types from options if they have a value specified there"
  [options config]
  (->> (map (partial with-type options)
            (keys config)
            (vals config))
       (apply merge)))

;; Public
;; ------

(defn ^:dynamic getenv []
  (System/getenv))

(defn confo [prefix & options]
  (let [pf (name prefix)
        opts (apply hash-map options)
        config (->> (getenv)
                    (filter (partial has-prefix pf))
                    (map (partial to-hash-map pf))
                    (apply merge))]
    (merge opts (typed opts config))))

