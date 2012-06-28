(ns arnold.core
  (:use [overtone.live]))

;; Day 0 - This is the code I started with.


(defn- long?
  [val]
  (when (= Long (type val))
    val))

(defn- double?
  [val]
  (when (= Double (type val))
    val))

(defn- play-sample
  [samp time vol]
  (at time (stereo-player samp :vol vol)))

(defn determine-time
 [onset-time b-idx beat-dur num-beats]
 (+ onset-time (* b-idx beat-dur)))

(defn- extract-prob-and-vol
  [s]
  (let [prob (or (some double? s) 1.0)
        vol  (or (some long? s) 1)]
    [prob vol]))

(defn- schedule-all-beats
  [bar samp onset-time bar-dur]
  (let [num-beats (count bar)
        beat-dur  (/ bar-dur num-beats)]
    (doall
     (map-indexed (fn [idx beat]
                    (let [t (determine-time onset-time idx beat-dur num-beats)]
                      (cond
                        (= true beat)
                        (at t (samp {}))

                        (long? beat)
                        (at t (samp {:vol beat}))

                        (double? beat)
                        (when (< (rand) beat)
                          (at t (samp {})))

                        (set? beat)
                        (let [[prob vol] (extract-prob-and-vol beat)]
                          (when (< (rand) prob)
                            (at t (samp {:vol vol}))))

                        (map? beat)
                        (at t (samp beat))

                        (sequential? beat)
                        (schedule-all-beats beat
                                            samp
                                            (determine-time onset-time idx beat-dur num-beats)
                                            beat-dur))))
                  bar))))

(defn play-rhythm
  ([patterns* bar-dur*] (play-rhythm patterns* bar-dur* (+ 500 (now)) 0))
  ([patterns* bar-dur* start-time beat-num]
     (let [patterns @patterns*
           bar-dur  @bar-dur*]
       (doseq [[key [samp pat]] patterns]
         (let [idx (mod beat-num (count pat))]
           (schedule-all-beats (nth pat idx) samp start-time bar-dur)))
       (apply-at (+ start-time bar-dur) #'play-rhythm [patterns*
                                                       bar-dur*
                                                       (+ start-time bar-dur)
                                                       (inc beat-num)]))))



(def bar-dur (atom 1000))


(def _ nil)
(def X true)

(defonce clients* (atom {}))
(defonce msg-log* (atom []))
(def hard-house (sample (freesound-path 2618)))
(def ring-hat (sample (freesound-path 12912)))
(def boom (sample (freesound-path 33637)))
(def wind (sample (freesound-path 34338)))
(def kick  #(sample-player (sample (freesound-path 777)) % ))
(def snare #(sample-player (sample (freesound-path 26903) %)))


(kick {})

(def _ nil)
(def X true)
(def patterns* (atom   {:boom [boom [[_]]]
                        :wind [wind [[_]]]
                        :snare [snare [[_]]]
                        :kick [kick [[X X X X]]]
;;                        :snare [snare [[_]]]

                               }))

(play-rhythm patterns* bar-dur)


(defn update-pat!
  [key pat]
  (swap! patterns* (fn [patterns key new-pat]
                     (let [[samp pat] (get patterns key)]
                       (assoc patterns key [samp new-pat])))
         key pat))



(defn update-log!
  [log-msg]
  (swap! msg-log* conj log-msg)
  (dorun
   (for [[c-name c-info] @clients*]
     (do
       (osc-send (:osc-client c-info) "/log" (prn-str log-msg)))

)))

(defonce server (osc-server 7800))

(osc-handle server "/register-client"
            (fn [msg]
              (let [name (first (:args msg))
                    port (:src-port msg)
                    host (:src-host msg)
                    client (osc-client host port)]
                (swap! clients* assoc [host port] {:name name
                                                   :osc-client client}))))

(defn- get-client
  [host port]
  (let [clients @clients*]
    (get clients [host port])))

(defn- handle-msg
  [samp-name pat]
  (let [pat (read-string pat)]
    (update-pat! (keyword samp-name) pat)))

(osc-handle server "/new-pat" (fn [msg]
                                (let [samp-name (first (:args msg))
                                      pat       (second (:args msg))
                                      port      (:src-port msg)
                                      host      (:src-host msg)
                                      client    (get-client host port)
                                      log-msg   {(:name client) [:update-pat samp-name pat]}]
                                  (update-log! log-msg)
                                  (apply #'handle-msg [samp-name pat]))))

(def c (osc-client "localhost" 7800))

(osc-handle c "/log"
            (fn [msg]
              (println "Log: " (read-string (first (:args msg))))))

;;(osc-send c "/new-pat" "hi-piano" (prn-str [[2 5 7 1]]))

(defn pattern
  [c samp-name & pats]
  (osc-send c "/new-pat" (name samp-name) (prn-str pats))  )

;;(osc-send c "/register-client" "Sam")

(pattern c :wind [_])
(pattern c :boom (repeat 5 0.5))
(pattern c :snare [[_ 1] _ _ _])
(pattern c :kick [{:vol 0.1} X])

;;(Get @clients* ["localhost" 60597])

(stop)
