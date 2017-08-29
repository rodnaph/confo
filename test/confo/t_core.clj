(ns confo.t-core
  (:require [confo.core :refer [confo getenv]]
            [midje.sweet :refer :all])
  (:import (java.util Properties)))

(def env (fn []
           (doto (Properties.)
             (.put "FOO_BAR" "123")
             (.put "FOO_BAZ_ZLE" "foo")
             (.put "FOO_BOB" "dob")
             (.put "FOO_BOOL" "true")
             (.put "FOO_ARRAY" "one,two,three")
             (.put "FOO_BLANK" ""))))

(binding [getenv env]

  (fact "vars can be fetched from the enviroment"
        (:bar (confo :foo)) => "123"
        (:baz-zle (confo :foo)) => "foo")

  (fact "vars can have defaults"
        (:qwe (confo :foo :qwe 123)) => 123)

  (fact "defaults have types 'coerced'"
        (:bar (confo :foo :bar 456)) => 123
        (:bob (confo :foo :bob :keyword)) => :dob
        (:bool (confo :foo :bool false)) => true
        (:array (confo :foo :array [])) => ["one" "two" "three"]
        (:blank (confo :foo :blank nil)) => nil))
