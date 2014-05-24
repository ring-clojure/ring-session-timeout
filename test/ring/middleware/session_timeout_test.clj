(ns ring.middleware.session-timeout-test
  (:use clojure.test
        ring.middleware.session-timeout)
  (:require [ring.middleware.session-timeout :as timeout]
            [ring.mock.request :as mock]))

(def ok-response
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "ok"})

(def timeout-response
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "timeout"})

(def idle-handler
  (-> (constantly ok-response)
      (wrap-idle-session-timeout
       {:timeout 600
        :timeout-response timeout-response})))

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
      (is (= (:body response) "timeout"))
      (is (= (:session response :empty) nil)))))

(def absolute-handler
  (-> (constantly ok-response)
      (wrap-absolute-session-timeout
       {:timeout 600
        :timeout-response timeout-response})))

(deftest test-idle-timeout
  (testing "timeout added to session"
    (let [response (with-time 1400000000 (absolute-handler (mock/request :get "/")))]
      (is (= (:body response) "ok"))
      (is (= (:session response) {::timeout/absolute-timeout 1400000600}))))

  (testing "not timed out"
    (let [request  (-> (mock/request :get "/")
                       (assoc :session {::timeout/absolute-timeout 1400000600}))
          response (with-time 1400000500 (absolute-handler request))]
      (is (= (:body response) "ok"))
      (is (not (contains? response :session)))))

  (testing "timed out"
    (let [request  (-> (mock/request :get "/")
                       (assoc :session {::timeout/absolute-timeout 1400000600}))
          response (with-time 1400000700 (absolute-handler request))]
      (is (= (:body response) "timeout"))
      (is (= (:session response :empty) nil)))))
