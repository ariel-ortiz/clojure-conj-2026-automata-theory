(ns clojure-conj-2026.regex
  "Regular Expression solution implemented in Clojure.

   This module defines a native regular expression solution (`solution-regex`)
   designed to recognize the alternating binary language L using Clojure’s
   built-in pattern matching capabilities."
  (:require [clojure.test :refer [deftest is run-tests]]))

(def solution-regex #"(10)*1")

(deftest test-regex
  (is (re-matches solution-regex "1"))
  (is (re-matches solution-regex "101"))
  (is (re-matches solution-regex "101010101"))
  (is (not (re-matches solution-regex "")))
  (is (not (re-matches solution-regex "0")))
  (is (not (re-matches solution-regex "01")))
  (is (not (re-matches solution-regex "1010")))
  (is (not (re-matches solution-regex "1001")))
  (is (not (re-matches solution-regex "101100101"))))

(run-tests)

