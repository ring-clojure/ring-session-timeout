# Ring-Session-Timeout

[![Build Status](https://secure.travis-ci.org/ring-clojure/ring-session-timeout.png)](http://travis-ci.org/ring-clojure/ring-session-timeout)

Ring middleware that provides idle and absolute timeouts for sessions.

## Installation

Add the following dependency to your `project.clj`:

    [ring/ring-session-timeout "0.1.0"]


## Documentation

* [API Docs](http://ring-clojure.github.io/ring-session-timeout/ring.middleware.session-timeout.html)

* Basic usage:
```clojure
(def timeout-response
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "timeout"})

(-> app
   (wrap-idle-session-timeout {:timeout 600 :timeout-response timeout-response})
   (wrap-session {:cookie-name "foo"}))
```

## License

Copyright © 2014 James Reeves

Distributed under the MIT License, the same as Ring.
