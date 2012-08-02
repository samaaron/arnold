(ns arnold.core
  (:use [overtone.live]
        [arnold synths rhythm]))

(defn intro-noise
  [& args]
  (apply sample-player (sample (freesound-path 6859)) args))
