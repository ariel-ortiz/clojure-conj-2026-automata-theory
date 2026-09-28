(ns clojure-conj-2026.functional
  "Pure functional predicate solution implemented in Clojure.

   This module defines a sequence-based validation predicate
   (`alternating-binary?`) designed to recognize the alternating
   binary language L using high-level collection functions like
   `partition` and `not-any?` without state machines."
  (:require [clojure.test :refer [deftest is run-tests]]))

(defn alternating-binary?
  [input]
  (and (= \1 (first input) (last input))
       (apply = [\1 \0] (partition 2 input))))

(deftest test-alternating-binary?
  (is (alternating-binary? "1"))
  (is (alternating-binary? "101"))
  (is (alternating-binary? "101010101"))
  (is (not (alternating-binary? "")))
  (is (not (alternating-binary? "0")))
  (is (not (alternating-binary? "01")))
  (is (not (alternating-binary? "1010")))
  (is (not (alternating-binary? "1001")))
  (is (not (alternating-binary? "101100101"))))

(run-tests)