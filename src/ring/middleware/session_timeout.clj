(ns ring.middleware.session-timeout
  "Middleware for managing session timeouts.")

(defn- current-time []
  (quot (System/currentTimeMillis) 1000))

(defn wrap-idle-session-timeout
  "Middleware that times out idle sessions after a specified number of seconds.

  If a session is timed out, the timeout-response option is returned. This is
  usually a redirect to the login page.

  The following options are accepted:

  :timeout          - the idle timeout in seconds (default 600 seconds)
  :timeout-response - the response to send if an idle timeout occurs"
  [handler {:keys [timeout timeout-response] :or {timeout 600}}]
  {:pre [(integer? timeout) (map? timeout-response)]}
  (fn [request]
    (let [session  (:session request {})
          end-time (::idle-timeout session)]
      (if (and end-time (< end-time (current-time)))
        (assoc timeout-response :session nil)
        (let [response (handler request)
              end-time (+ (current-time) timeout)
              session  (-> (:session response session)
                           (assoc ::idle-timeout end-time))]
          (assoc response :session session))))))

(defn wrap-absolute-session-timeout
  "Middleware that times out sessions after a specified number of seconds,
  regardless of whether the session is being used or idle. This places an upper
  limit on how long a compromised session can be exploited.

  If a session is timed out, the timeout-response option is returned. This is
  usually a redirect to the login page.

  The following options are accepted:

  :timeout          - the absolute timeout in seconds
  :timeout-response - the response to send if an idle timeout occurs"
  [handler {:keys [timeout timeout-response]}]
  {:pre [(integer? timeout) (map? timeout-response)]}
  (fn [request]
    (let [session  (:session request {})
          end-time (::absolute-timeout session)]
      (if (and end-time (< end-time (current-time)))
        (assoc timeout-response :session nil)
        (let [response (handler request)
              session  (:session response session)]
          (if (or (nil? session) (and end-time (not (contains? response :session))))
            response
            (let [end-time (or end-time (+ (current-time) timeout))
                  session  (assoc session ::absolute-timeout end-time)]
              (assoc response :session session))))))))
