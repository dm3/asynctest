(ns asynctest.core
  (:require [net.cgrand.macrovich :as macro]
            [clojure.test])
  #? (:cljs (:require-macros
              [net.cgrand.macrovich :as macro]
              [cljs.test]))
  #? (:clj (:import (java.util.concurrent CountDownLatch))))

(macro/usetime

(defn exception
  "Creates a native exception with the given `msg`."
  [msg]
  #? (:clj  (Exception. msg)
      :cljs (js/Error. msg)))

(defn in*
  "Executions function `f` in `ms` milliseconds."
  [ms f]
  #? (:clj  (do (Thread/sleep ms) (f))
      :cljs (js/setTimeout f ms)))

(defn yield*
  "Schedules function `f` to execute during the next execution cycle.

  On the JVM - executes on a different thread."
  [f]
  #? (:clj  (future (f))
      :cljs (in* 0 f)))

(defn async*
  "Executes `f` in a `cljs.test/async` block when in a JS environment.
  The `async` form should be the last form in a `deftest`.

  On the JVM - executes function `f` in a different thread and waits
  until the `done` function is called.

    (async*
      (fn [done]
        (some-async-op
          (fn [result]
            (is (= :expected result))
            (done)))))"
  [f]
  #? (:clj  (let [^CountDownLatch latch (CountDownLatch. 1)]
              (doto (Thread. (fn []
                               (f (fn [] (.countDown latch)))))
                (.setDaemon true)
                (.start))
              (.await latch))
      :cljs (cljs.test/async done
              (f done))))

)

(macro/deftime

(defmacro in
  "Executions the `body` in `ms` milliseconds."
  [ms & body]
  `(in* ~ms (fn [] ~@body)))

(defmacro yield
  "Schedules function `f` to execute during the next execution cycle.

  On the JVM - executes on a different thread."
  [& body]
  `(yield* (fn [] ~@body)))

(defmacro async
  "Executes `body` in a `cljs.test/async` block when in a JS environment.
  The `async` form should be the last form in a `deftest`.

  On the JVM - executes `body` in a different thread and waits until the
  `done` function is called.

    (async done
      (some-async-op
        (fn [result]
          (is (= :expected result))
          (done))))"
  [done-fn-binding & body]
  `(async* (fn [~done-fn-binding]
             ~@body)))
)
