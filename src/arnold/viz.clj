(ns arnold.viz
  (:require [arnold squares petals sphere lines])
  (:use [quil.core]))

(def viz-state* (atom {:squares false
                       :petals false
                       :sphere false
                       :lines false}))

(defn show
  [viz]
  (swap! viz-state* assoc viz true))

(defn hide
  [viz]
  (swap! viz-state* assoc viz false))

(defn setup []
  (arnold.lines/setup)
  (background 0))

(defn draw []
  (let [viz-state @viz-state*]
    (when-not (:petals viz-state)
      (background 0))
;;(frame-rate 5)
    (when (:squares viz-state)
      (arnold.squares/draw))

    (when (:petals viz-state)
      (arnold.petals/draw))
(frame-rate 24)
    (when (:sphere viz-state)
      (arnold.sphere/draw))
;;    (frame-rate 10)
    (when (:lines viz-state)
      (arnold.lines/draw))))

(defsketch sketch-name
  :title "My Beautiful Sketch"
  :setup setup
  :draw draw
  :size [(screen-width) (screen-height)]
  :renderer :opengl
  :decor false)

;;(hide :petals)
;;(hide :squares)
(show :petals)
(hide :lines)
(hide :sphere)


;;(hide :lines)
