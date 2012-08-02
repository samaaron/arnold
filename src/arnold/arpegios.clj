(ns arnold.arpegios
  (:use [overtone.core]
        [arnold.synths]
        [overtone.inst.sampled-piano]
        [overtone.inst.synth]))


;; Inspired by Extempore code demonstrated by Andrew Sorensen
;; in http://vimeo.com/21956071 (found around the 10 minute mark)

(def chord-prog
  [#{[2 :minor7] [7 :minor7] [10 :major7]}
   #{[0 :minor7] [8 :major7]}])

(def beat-offsets [0 0.1 0.2 1/3  0.7 0.9])

(def metro (metronome 20))

(def root 40)
(def max-range 35)
(def range-variation 10)
(def range-period 8)

(defn play-note [note]
  (floaty note  :dur 5 :amp 0.0))

(defn beat-loop
  [metro beat chord-idx]
  (let [[tonic chord-name] (choose (seq (nth chord-prog chord-idx)))
        nxt-chord-idx      (mod (inc chord-idx) (count chord-prog))
        note-range         (cosr beat range-variation  max-range range-period)
        notes-to-play      (rand-chord (+ root tonic)
                                       chord-name
                                       (count beat-offsets)
                                       note-range)]
;;    (at (metro beat ) (tb303 :amp 2 :note (first notes-to-play) :decay 2))
;;    (at (metro (+ beat 0.3)) (tb303 :amp 1 :note (- (nth notes-to-play 2) (choose [0 12 -12])) :decay 2))
    (dorun
     (map (fn [note offset]
            (at (metro (+ beat offset)) (play-note note)))
          notes-to-play
          beat-offsets))
    (apply-at (metro (inc beat)) #'beat-loop [metro (inc beat) nxt-chord-idx])))


(beat-loop metro (metro) 0)

;(def beat-offsets [0 0.2 1/3  0.5 0.8])
;(def beat-offsets [0 0.2 0.4  0.6 0.8])
;(def beat-offsets [0 0.1 0.2  0.3 0.4])
;(def beat-offsets [0 0.1 0.11 0.13 0.15 0.17 0.2 0.4 0.5 0.55 0.6 0.8])
