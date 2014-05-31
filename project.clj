(defproject ring/ring-session-timeout "0.1.0-SNAPSHOT"
  :description "Ring middleware for session timeouts"
  :url "https://github.com/ring-clojure/ring-session-timeout"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.3.0"]]
  :plugins [[codox "0.8.7"]]
  :codox {:project {:name "Ring-Session-Timeout"}}
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.5"]]}
   :1.4 {:dependencies [[org.clojure/clojure "1.4.0"]]}
   :1.5 {:dependencies [[org.clojure/clojure "1.5.1"]]}
   :1.6 {:dependencies [[org.clojure/clojure "1.6.0"]]}})
