(ns demo
  (:require [instaparse.core :refer [parser]])
  (:import (instaparse.gll Failure))
  (:import (java.io Writer)))

(defn fn-accepts?
  [input]
  (and (= \1
          (first input)
          (last input))
       (apply =
              [\1 \0]
              (partition 2 input))))

(defrecord DFA [initial-state
                accept-states
                transitions])

(defn dfa-accepts?
  [{:keys [initial-state accept-states transitions]} input]
  (loop [input         input
         current-state initial-state]
    (cond
      (nil? current-state) false
      (empty? input) (contains? accept-states current-state)
      :else (recur (rest input)
                   (get-in transitions [current-state (first input)])))))

(def dfa1 (->DFA :q0
                 #{:q1}
                 {:q0 {\0 :q2
                       \1 :q1}
                  :q1 {\0 :q0
                       \1 :q2}
                  :q2 {\0 :q2
                       \1 :q2}}))

(defn regex-accepts?
  [regex input]
  (boolean (re-matches regex input)))

(def regex1 #"1(01)*")

(defn fails? [r] (instance? Failure r))
(defn cfg-accepts? [r input] (not (fails? (r input))))
(def cfg1 (parser "A = '1' | '10' A"))

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

(defn tm-accepts
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

(def tm1 (->TM :q0
               #{:q2}
               {:q0 {\1 [\1 :right :q1]}
                :q1 {\0 [\0 :right :q0]
                     \_ [\_ :left :q2]}}))

;;; Adds 1 to a binary number
(def tm2 (->TM :q0
               #{:q3}
               {:q0 {\0 [\0 :right :q0]
                     \1 [\1 :right :q0]
                     \_ [\_ :left :q1]}
                :q1 {\0 [\1 :right :q2]
                     \1 [\0 :left :q1]
                     \_ [\1 :right :q2]}
                :q2 {\0 [\0 :right :q2]
                     \_ [\_ :left :q3]}}))
