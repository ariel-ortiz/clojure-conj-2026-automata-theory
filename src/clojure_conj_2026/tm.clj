(ns clojure-conj-2026.tm
  "Turing Machine (TM) simulator implemented in Clojure.

   This module defines a custom tape structure, a generic TM execution
   loop, and instantiates a specific TM solution (`solution-tm`)
   designed to recognize the alternating binary language L through
   tape manipulation and state transitions."
  (:require [clojure.test :refer [deftest is run-tests]])
  (:import (java.io Writer)))

(defrecord TM [initial-state accept-states transitions])

(defrecord Tape [left head right]
  Object
  (toString [_] (format "%s[%s]%s" left head right)))

(defmethod print-method Tape
  [self ^Writer writer]
  (.write writer (str self)))

(defn make-tape
  ([s]
   (let [result (drop-while #(= % \_) s)]
     (make-tape "" (if (empty? result) \_ (first result)) (rest result))))
  ([left head right]
   (let [new-left (drop-while #(= % \_) left)
         new-right (reverse (drop-while #(= % \_) (reverse right)))]
     (->Tape (apply str new-left)
             head
             (apply str new-right)))))

(defn write-tape
  [{:keys [left right]} value]
  (make-tape left value right))

(defn shift-head
  [{:keys [left head right]} direction]
  (case direction
    :left (make-tape (or (butlast left) ())
                     (or (last left) \_)
                     (str head right))
    :right (make-tape (str left head)
                      (or (first right) \_)
                      (rest right))
    (throw (ex-info (str "Bad direction: " direction) {}))))

(defn accepts
  [{:keys [initial-state accept-states transitions]} input]
  (loop [tape (make-tape input)
         current-state initial-state]
    (if (contains? accept-states current-state)
      tape
      (if-let [[write-symbol direction new-state]
               ((transitions current-state) (.head tape))]
        (recur (shift-head (write-tape tape write-symbol) direction)
               new-state)
        nil))))

(def solution-tm (->TM :q0
                       #{:q2}
                       {:q0 {\1 [\1 :right :q1]}
                        :q1 {\0 [\0 :right :q0]
                             \_ [\_ :left :q2]}}))

(deftest test-tm
  (is (accepts solution-tm "1"))
  (is (accepts solution-tm "101"))
  (is (accepts solution-tm "101010101"))
  (is (nil? (accepts solution-tm "")))
  (is (nil? (accepts solution-tm "0")))
  (is (nil? (accepts solution-tm "01")))
  (is (nil? (accepts solution-tm "1010")))
  (is (nil? (accepts solution-tm "1001")))
  (is (nil? (accepts solution-tm "101100101"))))

(run-tests)