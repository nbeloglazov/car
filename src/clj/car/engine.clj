(ns car.engine
  (:require [car.core :refer [set-engine!]]))

(set-engine! (fn [] :gas))
(set-engine! (fn [] :break))
(set-engine! (fn [] :none))
