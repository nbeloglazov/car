(ns car.server
  (:require [org.httpkit.server :as s]
            [compojure.route :as route]
            [compojure.core :as c]
            [cheshire.core :as json]
            [car.core :refer [state]]))

(def nrepl-port (Integer/parseInt (slurp ".nrepl-port")))

(def listeners (atom []))

(defn notify-listeners [state]
  (let [state (json/generate-string state)]
      (doseq [listener @listeners]
        (s/send! listener state))))

(add-watch state :listeners
           (fn [_ _ _ state]
             (notify-listeners state)))

(defn ws-handler [request]
  (s/with-channel request channel
    (swap! listeners conj channel)
    (println "Total listeners" (count @listeners))
    (s/on-close channel (fn [status]
                          (swap! listeners #(remove (fn [ch] (= ch channel))
                                                    %))
                          (println "Total listeners" (count @listeners))
                          (println "channel closed: " status)))
    (s/on-receive channel (fn [data] ;; echo it back
                            (s/send! channel data)))
    (s/send! channel (str nrepl-port))))

(c/defroutes all-routes
  (c/GET "/" [] (slurp "public/index.html"))
  (c/GET "/ws" req (ws-handler req))
  (route/files "/")
  (route/not-found "<p>Page not found.</p>"))

(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))

(defn start-server []
  (reset! server (s/run-server #'all-routes {:port 8080})))

(comment

  (start-server)

  (stop-server)

  )

