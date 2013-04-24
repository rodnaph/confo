
(ns confo.core
  (:require [clojure.string :as s]))

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
  (hash-map k
    (let [option (get options k)]
      (cond
        (integer? option) (Integer/parseInt v)
        (vector? option) (s/split v #",")
        :else v))))

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

