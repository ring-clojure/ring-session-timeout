# Ring-Session-Timeout

[![Build Status](https://travis-ci.org/ring-clojure/ring-session-timeout.svg?branch=master)](https://travis-ci.org/ring-clojure/ring-session-timeout)

Ring middleware that provides idle and absolute timeouts for sessions.

## Installation

Add the following dependency to your `project.clj`:

    [ring/ring-session-timeout "0.1.0"]

## Usage

```clojure
(require '[ring.middleware.session-timeout :refer (wrap-idle-session-timeout)])
(require '[ring.middleware.session :refer (wrap-session)])

; your routes
(def app {})

(def timeout-response
  {:status 304
   :headers {"Content-Type" "text/plain"}
   :body "timeout"})

; wrap-idle-session-timeout shoule precede wrap-session
(-> app
   (wrap-idle-session-timeout {:timeout 600 :timeout-response timeout-response})
   (wrap-session {:cookie-name "foo"}))
```

## Documentation

* [API Docs](http://ring-clojure.github.io/ring-session-timeout/ring.middleware.session-timeout.html)

## License

Copyright © 2016 James Reeves

Distributed under the MIT License, the same as Ring.
