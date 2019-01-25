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

  (is (= state ::none))

  (is (state-down?))

  (state-start)

  (let [doc (-> state var meta :doc)]
    (is (= doc "Some docstring")))

  (is (thread? state))
  (is (alive? state))

  (is (state-up?))

  (let [thread state]

    (state-stop)

    (is (= state ::none))

    (is (state-down?))

    (thread? thread)
    (is (not (alive? thread)))))
