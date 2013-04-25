(ns confo.core
  (:require [clojure.string :as s]))

(defn class-of [option value]
  (class option))

(defmulti coerce class-of)

(defmethod coerce java.lang.Long
  [option value]
  (Integer/parseInt value))

(defmethod coerce clojure.lang.IPersistentVector
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

(defn- typed
  "Coerce config values to matching types from options if they have a value specified there"
  [options config]
  (->> (map #(hash-map %1 (coerce (get options %1) %2))
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

