(ns arnold.voltap
  (:use [overtone.live]))

(def g (group))

(defsynth vol []
  (tap "system-vol" 60 (lag (abs (in:ar 0)) 0.1)))

(def v (vol :target g))
