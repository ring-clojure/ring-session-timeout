(ns ring.middleware.session-timeout)

(defn- current-time []
  (quot (System/currentTimeMillis) 1000))

(defn wrap-idle-session-timeout
  "Middleware that times out idle sessions after a specified number of seconds.
  If a session is timed out, the timeout-response option is returned. This is
  usually a redirect to the login page.

  The following options are accepted:

  :timeout          - the idle timeout in seconds
  :timeout-response - the response to send if an idle timeout occurs"
  [handler {:keys [timeout timeout-response] :or {timeout 600}}]
  {:pre [(integer? timeout) (map? timeout-response)]}
  (fn [request]
    (let [session  (:session request {})
          end-time (::idle-timeout session)]
      (prn session)
      (if (and end-time (< end-time (current-time)))
        (assoc timeout-response :session nil)
        (let [response (handler request)
              session  (:session response session)]
          (assoc-in response
                    [:session ::idle-timeout]
                    (+ (current-time) timeout)))))))
