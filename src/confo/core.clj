
(ns confo.core
  (:use [clojure.string :only [lower-case]]))

(defn- to-hash-map [prefix entry]
  (hash-map (-> (.getKey entry)
                (subs (inc (count prefix)))
                (lower-case)
                (keyword))
            (.getValue entry)))

(defn- has-prefix [prefix string]
  (= 0 (.indexOf (lower-case string)
                 (str (lower-case prefix) "_"))))

(defn- with-type
  "Tries to coece a value to a type, if the k is present in options"
  [options k v]
  (if-let [option-val (get options k)]
    (cond
      (integer? option-val) {k (Integer/parseInt v)}
      :else {k v})
    {k v}))

(defn- typed
  "Coerce config values to matching types from options if they have a value specified there"
  [options config]
  (->> (map (partial with-type options)
            (keys config)
            (vals config))
       (apply merge)))

;; Public
;; ------

(defn confo [prefix & options]
  (let [pf (name prefix)
        config (->> (System/getenv)
                    (filter (partial has-prefix pf))
                    (map (partial to-hash-map pf)))]
    (merge (apply hash-map options)
           (typed options config))))

