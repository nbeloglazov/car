(ns car.core
  (:require [quil.core :as q]))

(def width 500)
(def height 500)
(def rot-angle (/ q/TWO-PI 40))
(def max-speed 15)
(def reach 40)
(def delay 50)

(defn rand-target []
  [(+ reach (rand-int (- width reach reach)))
   (+ reach (rand-int (- height reach reach)))])

(def init-state {:pos [(/ width 5)
                        (/ height 2)]
                 :angle 0
                 :targets [[(/ width 1.2)
                            (/ height 2)]]
                 :score 0
                 :speed 0})

(def controls (atom {:wheel nil
                     :engine nil}))

(defn set-wheel! [fn]
  (swap! controls assoc :wheel fn))

(defn set-engine! [fn]
  (swap! controls assoc :engine fn))

(defonce state (atom nil))
(reset! state init-state)

(defn update-speed [state]
  (-> state
      (update-in [:speed] (case (:engine state)
                            :gas inc
                            :break #(- % 5)
                            dec))
      (update-in [:speed] q/constrain 0 max-speed)))

(defn update-targets [state]
  (let [[x y] (:pos state)
        [hit targets] (reduce (fn [[hit targets] [t-x t-y]]
                                (if (< (q/dist x y t-x t-y) reach)
                                  [(inc hit) (conj targets (rand-target))]
                                  [hit (conj targets [t-x t-y])]))
                              [0 []] (:targets state))]
    (-> state
        (update-in [:score] + hit)
        (assoc :targets targets))))

(defn update-angle [state]
  (let [angle (case (:wheel state)
                :left (- rot-angle)
                :right rot-angle
                0)]
    (update-in state [:angle] #(mod (+ angle %) q/TWO-PI))))

(defn run-safe [fn]
  (try (fn)
       (catch Exception e
         :none)))

(defn update-control [state]
  (assoc state
    :engine (run-safe (:engine @controls))
    :wheel (run-safe (:wheel @controls))))

(defn ensure-in-board [[x y]]
  [(mod (+ x width) width)
   (mod (+ y height) height)])

(defn update-pos [{:keys [speed pos angle] :as state}]
  (let [dist (map #(* speed %)
                  [(q/cos angle)
                   (q/sin angle)])]
    (-> state
        (update-in [:pos] #(map + % dist))
        (update-in [:pos] ensure-in-board))))

(defn update [state]
  (-> state
      update-control
      update-speed
      update-angle
      update-pos
      update-targets))

;(update init-state)

(defn draw []
  (q/background 240)
  (q/stroke-weight 5)
  (let [{:keys [dir pos]} @state
        dir (map #(* % 10) dir)
        a (map + pos dir)
        b (map - pos dir)]
    (q/line a b)))

(defn toggle [state key value]
  (assoc state key
         (if (= (state key) value)
           :none
           value)))

(defn start-loop []
  (future (while true (Thread/sleep delay)
                 (swap! state update))))

(declare fut)
(when (bound? #'fut)
  (future-cancel fut))

(def fut (start-loop))

@state
