(ns blink.core-test
  (:require [clojure.test :refer :all]
            blink))


(def thread? (partial instance? Thread))


(defn alive?
  [^Thread thread]
  (.isAlive thread))


(defn sleep [sec]
  (Thread/sleep (* sec 1000)))


(blink/defstate
  state

  "Some docstring"

  (println "Starting the thread...")

  (let [thread
        (Thread.
         (fn []
           (while true
             (sleep 1))))]

    (.start ^Thread thread)
    (println "Thread has been started.")

    thread)

  :stop

  (println "Stopping the thread...")
  (.stop ^Thread state)

  (sleep 1)
  (println "Thread has been stopped.")

  :default ::none)



(deftest test-thread

  (testing "initial state"
    (is (= state ::none))
    (is (state-down?)))

  (state-start)

  (testing "doc"
    (let [doc (-> state var meta :doc)]
      (is (= doc "Some docstring"))))

  (testing "current state"
    (is (thread? state))
    (is (alive? state))
    (is (state-up?)))

  (let [thread state]

    (state-stop)

    (testing "after stop"
      (is (= state ::none))
      (is (state-down?))
      (thread? thread)
      (is (not (alive? thread))))))
