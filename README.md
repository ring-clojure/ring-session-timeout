# Ring-Session-Timeout

[![Build Status](https://github.com/ring-clojure/ring-session-timeout/actions/workflows/test.yml/badge.svg)](https://github.com/ring-clojure/ring-session-timeout/actions/workflows/test.yml)

Ring middleware that provides idle and absolute timeouts for sessions.

## Installation

Add the following dependency to your `project.clj`:

    [ring/ring-session-timeout "0.2.0"]

## Usage

Place the timeout middleware inside that of your session middleware,
and supply a `:timeout-response` or `:timeout-handler` to be used when
the session times out.

```clojure
(-> handler
    (wrap-idle-session-timeout {:timeout-response redirect-to-login})
    (wrap-session))
```

For more information, see the API docs linked below.

## Documentation

* [API Docs](http://ring-clojure.github.io/ring-session-timeout/ring.middleware.session-timeout.html)

## License

Copyright © 2023 James Reeves

Distributed under the MIT License, the same as Ring.
