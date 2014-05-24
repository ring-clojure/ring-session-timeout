(ns ring.middleware.session-timeout-test
  (:use clojure.test
        ring.middleware.session-timeout)
  (:require [ring.middleware.session-timeout :as timeout]
            [ring.mock.request :as mock]))

(def ok-response
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "ok"})

(def idle-response
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "idle timeout"})

(def idle-handler
  (-> (constantly ok-response)
      (wrap-idle-session-timeout
       {:timeout 600
        :timeout-response idle-response})))

(defmacro with-time [time & body]
  `(with-redefs [timeout/current-time (constantly ~time)]
     ~@body))

(deftest test-idle-timeout
  (testing "timeout added to session"
    (let [response (with-time 1400000000 (idle-handler (mock/request :get "/")))]
      (is (= (:body response) "ok"))
      (is (= (:session response) {::timeout/idle-timeout 1400000600}))))

  (testing "not timed out"
    (let [request  (-> (mock/request :get "/")
                       (assoc :session {::timeout/idle-timeout 1400000600}))
          response (with-time 1400000500 (idle-handler request))]
      (is (= (:body response) "ok"))
      (is (= (:session response) {::timeout/idle-timeout 1400001100}))))

  (testing "timed out"
    (let [request  (-> (mock/request :get "/")
                       (assoc :session {::timeout/idle-timeout 1400000600}))
          response (with-time 1400000700 (idle-handler request))]
      (is (= (:body response) "idle timeout"))
      (is (= (:session response :empty) nil)))))
