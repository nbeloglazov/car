(ns car.ui
  (:require [quil.core :as q :include-macros true]
            [goog.events :as events]
            [goog.dom :as dom]
            goog.events.EventType
            goog.net.WebSocket.EventType)
  (:import [goog.net WebSocket]))

(enable-console-print!)

(def width 500)
(def height 500)

(def state (atom nil))

(defn setup []
  (q/frame-rate 30)
  (q/image-mode :center)
  (q/set-state!
   :car (q/load-image "car.png")
   :target (q/load-image "clojure.png")))

(defn draw []
  (q/background 240)
  (when-not (nil? @state)
    (let [{:keys [angle pos targets score]} @state]
      (doseq [[x y] targets]
        (q/image (q/state :target)
                 x y 40 40))
      (q/with-translation pos
        (q/with-rotation [angle]
          (q/image (q/state :car)
                   0 0 72 44)))
      (q/fill 0)
      (q/text-size 30)
      (q/text (str score) 10 30))))


(defn init-sketch []
  (q/sketch
   :host (.querySelector js/document "canvas")
   :size [width height]
   :setup setup
   :draw draw))

(defn update-nrepl-port [port]
  (println port)
  (let [host js/location.hostname]
    (dom/setTextContent (.querySelector js/document "#connect-emacs")
                        (str "cider-connect " host " " port))
    (dom/setTextContent (.querySelector js/document "#connect-lighttable")
                        (str "Add Connection ⇒ Remote nRepl ⇒ " host ":" port))))

(defn state-updated [new-state]
  (reset! state new-state))

(defn on-load []
  (let [socket (WebSocket.)]
    (events/listen socket goog.net.WebSocket.EventType/OPENED
                   init-sketch)
    (events/listen socket goog.net.WebSocket.EventType/MESSAGE
                   (fn [event]
                     (let [message (js->clj (.parse js/JSON (.-message event))
                                            :keywordize-keys true)]
                       (if (number? message)
                         (update-nrepl-port message)
                         (state-updated message)))))
    (.open socket (str "ws://" js/location.host "/ws"))))

(events/listenOnce js/window "load" on-load)
