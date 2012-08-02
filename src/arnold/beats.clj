(ns arnold.beats
  (:use [overtone.core]
        [arnold rhythm synths]))

(defonce beats-bus (audio-bus 2))

;;(kill (:id synths))


(do
  (def mixers (group  "beat-mixers" :tail (main-synth-group)))
  (def synths (group "beat-synths" :head (main-synth-group)))
  (def beat-mixer (arnold-mixer :tgt mixers beats-bus 0)))

(ctl 38 :amp 0.5)

(defn kill-beats
  []
  (kill (:id synths))
  (kill (:id mixers)))

;;(kill-beats)


(defonce bar-dur (atom 2000))
(def _ nil)
(def X true)



(defn freesound-sampler
  [freesound-id]
  (fn play-sample
    ([] (play-sample {}))
    ([args]
       (sample-player (sample (freesound-path freesound-id)) :tgt synths  (assoc args :out-bus beats-bus)))))

(def ring-hat (freesound-sampler 12912))
(def wind     (freesound-sampler 34338))
(def kick     (freesound-sampler 777))
(def kick     (freesound-sampler 30669))
(def snare    (freesound-sampler 26903))
(def tom      (freesound-sampler 147418))
(def boom     (freesound-sampler 33637))
(def subby    (freesound-sampler 25649))
(def lp (freesound-sampler 39711))
(def crash (freesound-sampler 26884) )
(def stick (freesound-sampler 437))
(def short-snare (freesound-sampler 816))

;;(short-snare)
;;(stick)
;;(crash)
;;(lp)
;;(subby)
;;(boom {:vol 5})
;;(wind)
;;(ring-hat)
;;(kick )
;;(tom)
;;(wind)

(def patterns* (atom   {:ring-hat [ring-hat [[_]]]
                        :wind [wind [[_]]]
                        :tom [tom [[_]]]
                        :kick [kick [[_]]]
                        :s-snare [short-snare [[_]]]
                        :stick [stick [[_]]]
                        :crash [crash [[_]]]
                        :lp    [lp [[_]]]
                        :subby [subby [[_]]]
                        :boom  [boom [[_]]]}))

(defn update-pat!
  [key pat]
  (swap! patterns* (fn [patterns key new-pat]
                     (let [[samp pat] (get patterns key)]
                       (assoc patterns key [samp new-pat])))
         key pat))

(comment
  (update-pat! :kick  [[X _ _ _ [_ X] _  [X]  _]])
  (update-pat! :kick  [[_]])

  (update-pat! :subby [[0.6 _ 0.5 _]])
  (update-pat! :crash [[_  [_ 5 5] 0.8 15]])
  (update-pat! :wind [[_ ]])
  (update-pat! :lp [[_]])
  (update-pat! :boom [[X [_] _ _]])
  (update-pat! :s-snare [[X _ (vec (repeat 64 0.8)) [0.4 0.8 0.9 0.4 0.3 0.7]]])
  (update-pat! :s-snare [[_]])
  (update-pat! :kick  [[_]])
  (update-pat! :crash  [[_]])
  (update-pat! :tom [[X X X X]])
  (update-pat! :ring-hat [(repeat 16 #{0.8 0})])

  (play-rhythm patterns* bar-dur)
  (reset! bar-dur 250)



  (volume 3)
  (kick)
  (tom)

  (ctl (:id beat-mixer) :amp 0)

  (def wwii (sample-player (sample (freesound-path 43807)) :loop? true))
  (def windy (sample-player (sample (freesound-path 17553)) :loop? true))

  (ctl wwii :rate 0.5 :vol 1)
  (ctl windy :rate 0.8 :vol 1))
