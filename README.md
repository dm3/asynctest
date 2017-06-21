# asynctest

[![Build Status](https://travis-ci.org/dm3/asynctest.png?branch=master)](https://travis-ci.org/dm3/asynctest)

Clojure/script async test utilities.

## Usage

Add the following dependency to your project.clj or build.boot:

```clojure
[dm3/asynctest "0.1.0"]
```

then require the namespace:

```clojure
(require '[asynctest.core :as asynctest])
;; In cljs
(require-macros '[asynctest.core :as asynctest])
```

Using from tests, e.g. in `my_tests.cljc`:

```clojure
(ns my-tests
  (:require [clojure.test :refer [deftest testing is]]
            [asynctest.core :as at])
  #? (:cljs (:require-macros [asynctest.core :as at])))

(deftest my-test
  (at/async done
    (let [p (async-operation-returns-promise)]
      (at/yield
        (is (= :expected-result @p))
        (done)))))
```

## License

Copyright © 2017 Vadim Platonov

Distributed under the MIT License.
