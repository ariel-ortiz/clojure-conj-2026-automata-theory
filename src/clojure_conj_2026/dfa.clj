(ns clojure-conj-2026.dfa
  "Deterministic Finite Automaton (DFA) simulator implemented in Clojure.

   This module defines a generic DFA record and an evaluation function
   (`accepts?`), then instantiates a specific DFA solution (`solution-dfa`)
   designed to recognize the binary language L where strings must start/end
   with 1 and alternate with 0."
  (:require [clojure.test :refer [deftest is run-tests]]))

(defrecord DFA [initial-state
                accept-states
                transitions])

(defn accepts?
  [{:keys [initial-state accept-states transitions]} input]
  (loop [input          input
         current-state  initial-state]
    (if (empty? input)
      (contains? accept-states current-state)
      (recur (rest input)
             ((transitions current-state) (first input))))))

(def solution-dfa (->DFA :q0
                         #{:q1}
                         {:q0 {\0 :q2
                               \1 :q1}
                          :q1 {\0 :q0
                               \1 :q2}
                          :q2 {\0 :q2
                               \1 :q2}}))

(deftest test-dfa
  (is (accepts? solution-dfa "1"))
  (is (accepts? solution-dfa "101"))
  (is (accepts? solution-dfa "101010101"))
  (is (not (accepts? solution-dfa "")))
  (is (not (accepts? solution-dfa "0")))
  (is (not (accepts? solution-dfa "01")))
  (is (not (accepts? solution-dfa "1010")))
  (is (not (accepts? solution-dfa "1001")))
  (is (not (accepts? solution-dfa "101100101"))))

(run-tests)