(ns blink.core-test
  (:require [clojure.test :refer :all]
            blink))


(blink/defstate
  state

  "Some docstring"

  (println "Getting started...")

  (let [thread
        (Thread.
         (fn []
           (while true
             (Thread/sleep 1000))))]

    (.start ^Thread thread)
    thread)

  :stop

  (.stop ^Thread state)

  :default ::none)



(def thread? (partial instance? Thread))


(defn alive?
  [^Thread thread]
  (.isAlive thread))


(deftest test-thread
  (state-start)

  (is (thread? state))
  (is (alive? state))

  (let [thread state]

    (state-stop)

    (is (= state ::none))

    (thread? thread)
    (is (not (alive? thread)))))
