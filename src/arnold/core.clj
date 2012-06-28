(ns arnold.core
  (:use [overtone.live]
        [arnold.rhythm]))

(defonce bar-dur (atom 1000))
(def _ nil)
(def X true)

(defn freesound-sampler
  [freesound-id]
  (fn play-sample
    ([] (play-sample {}))
    ([args]
       (sample-player (sample (freesound-path freesound-id)) args))))


(def ring-hat (freesound-sampler 12912))
(def wind     (freesound-sampler 34338))
(def kick     (freesound-sampler 777))
(def snare    (freesound-sampler 26903))

(kick)
(snare {:vol 0.3})
(ring-hat)
(wind)

(def patterns* (atom   {:ring-hat [ring-hat [[_]]]
                        :wind [wind [[_]]]
                        :snare [snare [[_]]]
                        :kick [kick [[X X X _]]]}))

(play-rhythm patterns* bar-dur)


(defn update-pat!
  [key pat]
  (swap! patterns* (fn [patterns key new-pat]
                     (let [[samp pat] (get patterns key)]
                       (assoc patterns key [samp new-pat])))
         key pat))

(update-pat! :kick  [[X _ X _  X [_ X _]]])
(update-pat! :snare [[0.5 1 2 3 (repeat 1 4) 3 ]])
(update-pat! :ring-hat [[0.5 1 2 3 4 3 ]])


(reset! bar-dur 500)

(volume 3)
