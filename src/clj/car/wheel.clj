(ns car.wheel
  (:require [car.core :refer [set-wheel!]]))

(set-wheel! (fn [] :left))
(set-wheel! (fn [] :right))
(set-wheel! (fn [] :none))
