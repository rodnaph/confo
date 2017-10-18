(ns confo.core
  (:require [clojure.string :as s]))

(defn kebab-case
  [s]
  (s/replace s "_" "-"))

(defn snake-case
  [s]
  (s/replace s "-" "_"))

(defn class-of [option _]
  (class option))

(defmulti coerce class-of)

(defmethod coerce java.lang.Boolean
  [_ value]
  (Boolean/parseBoolean value))

(defmethod coerce java.lang.Long
  [_ value]
  (Integer/parseInt value))

(defmethod coerce clojure.lang.IPersistentVector
  [_ value]
  (s/split value #","))

(defmethod coerce clojure.lang.Keyword
  [_ value]
  (keyword value))

(defmethod coerce :default
  [option value]
  (if (and (= "" value) (nil? option))
    nil
    value))

(defn- to-hash-map [prefix entry]
  (hash-map (-> (.getKey entry)
                (subs (inc (count prefix)))
                (s/lower-case)
                (kebab-case)
                (keyword))
            (.getValue entry)))

(defn- has-prefix [prefix string]
  (.startsWith (s/lower-case string)
               (str (s/lower-case (snake-case prefix)) "_")))

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
