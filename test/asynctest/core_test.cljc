(ns asynctest.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [stopwatch.clock :as clock]
            [asynctest.core :as sut])
  #? (:cljs (:require-macros [asynctest.core :as sut]
                             [stopwatch.clock :as clock])))

(defn- epsilon [n]
  (/ n 20))

(defn- is-around? [v test-v]
  (is (<= (- v (epsilon test-v)) test-v (+ v (epsilon test-v)))))

(deftest in-test
  (sut/async done
    (let [now (clock/epoch)]
      (sut/in 100
        (is-around? 100 (- (clock/epoch) now))
        (done)))))

(deftest yield-test
  (sut/async done
    (let [now (clock/epoch)
          !executed? (atom false)]

      (sut/yield
        (reset! !executed? true)
        (is (<= (- (clock/epoch) now) 5))
        (done))

      (is (not @!executed?)))))
