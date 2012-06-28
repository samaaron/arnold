(ns arnold.distributed
  (:use [overtone.core]))

;; just thrown in here - needs work and tidying up.

(defonce clients* (atom {}))
(defonce msg-log* (atom []))


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
