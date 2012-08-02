(ns arnold.grumbles
  (:use [overtone.live]))

;; Inspired by an example in an early chapter of the SuperCollider book

(definst grumble [speed 6 freq-mul 1]
  (let [snd (mix (map #(* (sin-osc (* % freq-mul 100))
                          (max 0 (+ (lf-noise1:kr speed)
                                    (line:kr 1 -1 30 :action FREE))))
                      [1 (/ 2 3) (/ 3 2) 2]))]
    (pan2 snd (sin-osc:kr 50))))

(grumble :freq-mul 1)

(ctl grumble :speed 3000)

(volume (/  127  127))
