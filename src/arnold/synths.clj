(ns arnold.synths
  (:use [overtone.core]))

(defsynth arnold-mixer [in-bus 0 out-bus 0 amp 1]
  (out out-bus (* amp (in in-bus 2))))

(defsynth floaty
  [note 50 t 4 amt 0.3 amp 0.8 dur 3 lpf-cutoff 1000 lpf-peak 0.5 out-bus 0]
  (let [freq       (midicps note)
        f-env      (env-gen (perc t t) 1 1 0 1 FREE)
        src        (saw [freq (* freq 1.01)])
        signal     (rlpf (* 0.3 src)
                         (+ (* 0.6 freq) (* f-env 2 freq)) 0.2)
        k          (/ (* 2 amt) (- 1 amt))
        distort    (/ (* (+ 1 k) signal) (+ 1 (* k (abs signal))))
        gate       (pulse (* 2 (+ 1 (sin-osc:kr 0.05))))
        compressor (compander distort gate 0.01 1 0.5 0.01 0.01)
        dampener   (+ 1 (* 0.5 (sin-osc:kr 0.5)))
        reverb     (free-verb compressor 0.5 0.5 dampener)
        echo       (comb-n reverb 0.4 0.3 0.5)]
    (line 0 1 dur :action FREE)
    (out out-bus (pan2 (* amp reverb)))))
