(ns ring.middleware.session-timeout-test
  (:require [clojure.test :refer :all]
            [ring.middleware.session-timeout :as timeout]
            [ring.mock.request :as mock]))

(def ok-response
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "ok"})

(def timeout-response
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "timeout"})

(def timeout-options
  {:timeout 600
   :timeout-response timeout-response})

(def idle-handler
  (-> (constantly ok-response)
      (timeout/wrap-idle-session-timeout timeout-options)))

(defn timeout-handler [request]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (str "timeout on " (:uri request))})

(def idle-handler-with-timeout-handler
  (-> (constantly ok-response)
      (timeout/wrap-idle-session-timeout
       {:timeout 600
        :timeout-handler timeout-handler})))

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
      (is (= (:session response :empty) nil))))

  (testing "timed out with timeout handler"
    (let [request  (-> (mock/request :get "/fooxyz")
                       (assoc :session {::timeout/idle-timeout 1400000600}))
          response (with-time 1400000700 (idle-handler-with-timeout-handler request))]
      (is (= (:body response) "timeout on /fooxyz"))
      (is (= (:session response :empty) nil))))

  (testing "nil response"
    (let [handler (timeout/wrap-idle-session-timeout (constantly nil) timeout-options)]
      (is (nil? (handler (mock/request :get "/"))))))

  (testing "nil session"
    (let [handler  (-> (constantly (assoc ok-response :session nil))
                       (timeout/wrap-idle-session-timeout timeout-options))
          request  (-> (mock/request :get "/")
                       (assoc :session {:foo "bar"}))
          response (with-time 1400000000 (handler request))]
      (is (= (:body response) "ok"))
      (is (nil? (:session response :empty))))))

(def absolute-handler
  (-> (constantly ok-response)
      (timeout/wrap-absolute-session-timeout timeout-options)))

(deftest test-absolute-timeout
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
      (is (= (:session response :empty) nil))))

  (testing "nil response"
    (let [handler (timeout/wrap-absolute-session-timeout (constantly nil) timeout-options)]
      (is (nil? (handler (mock/request :get "/"))))))

  (testing "nil session"
    (let [handler  (-> (constantly (assoc ok-response :session nil))
                       (timeout/wrap-absolute-session-timeout timeout-options))
          request  (-> (mock/request :get "/")
                       (assoc :session {:foo "bar"}))
          response (with-time 1400000000 (handler request))]
      (is (= (:body response) "ok"))
      (is (nil? (:session response :empty))))))
