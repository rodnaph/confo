
(ns confo.t-core
  (:use midje.sweet)
  (:require [confo.core :as core])
  (:import (java.util Properties)))

(def env (fn []
           (doto (Properties.)
             (.put "FOO_BAR" "123")
             (.put "FOO_BAZ_ZLE" "foo"))))

(binding [core/getenv env]

  (fact "vars can be fetched from the enviroment"
    (:bar (core/confo :foo)) => "123"
    (:baz-zle (core/confo :foo)) => "foo")

  (fact "vars can have defaults"
    (:qwe (core/confo :foo :qwe 123)) => 123)

  (fact "defaults have types coerced"
    (:bar (core/confo :foo :bar 456)) => 123))

