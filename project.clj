(defproject ring/ring-session-timeout "0.2.0"
  :description "Ring middleware for session timeouts"
  :url "https://github.com/ring-clojure/ring-session-timeout"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :plugins [[lein-codox "0.10.1"]]
  :codox
  {:project {:name "Ring-Session-Timeout"}
   :html    {:namespace-list :nested}
   :output-path "codox"
   :source-uri
   "https://github.com/ring-clojure/ring-session-timeout/blob/{version}/{filepath}#L{line}"}
  :aliases {"test-all" ["with-profile" "default:+1.6:+1.7:+1.8" "test"]}
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.5"]]}
   :1.6 {:dependencies [[org.clojure/clojure "1.6.0"]]}
   :1.7 {:dependencies [[org.clojure/clojure "1.7.0"]]}
   :1.8 {:dependencies [[org.clojure/clojure "1.8.0"]]}})
