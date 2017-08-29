(defproject rodnaph/confo "0.8.0"
  :description "Easy Ambient Config"
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.5.1"]
                                  [midje "1.6.0"]
                                  [cljfmt "0.5.6"]]
                   :plugins [[lein-cljfmt "0.5.6"]
                             [lein-midje "3.1.3"]]}})
