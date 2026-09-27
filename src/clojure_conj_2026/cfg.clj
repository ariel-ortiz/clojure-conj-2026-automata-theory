(ns clojure-conj-2026.cfg
  "Context-Free Grammar (CFG) solution implemented in Clojure.

   This module defines a grammar using the `instaparse` library
   (`solution-cfg`) designed to recognize the alternating binary
   language L through syntactic parsing."
  (:require [clojure.test :refer [deftest is run-tests]])
  (:require [instaparse.core :refer [parser]])
  (:import (instaparse.gll Failure)))

(defn fails? [r] (instance? Failure r))
(defn succeeds? [r] (not (fails? r)))

(def solution-cfg (parser "A = '1' | '10' A"))

(deftest test-cfg
  (is (succeeds? (solution-cfg "1")))
  (is (succeeds? (solution-cfg "101")))
  (is (succeeds? (solution-cfg "101010101")))
  (is (fails? (solution-cfg "")))
  (is (fails? (solution-cfg "0")))
  (is (fails? (solution-cfg "01")))
  (is (fails? (solution-cfg "1010")))
  (is (fails? (solution-cfg "1001")))
  (is (fails? (solution-cfg "101100101"))))

(run-tests)
