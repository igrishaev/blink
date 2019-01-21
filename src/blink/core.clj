(ns blink.core
  (:require
   blink.spec
   [clojure.pprint :refer [pprint]]
   [clojure.spec.alpha :as s]))


(def invalid :clojure.spec.alpha/invalid)

(def invalid? (partial = invalid))

(def into-map (partial into {}))


(def help-url "https://github.com/igrishaev/blink/blob/master/README.md")

(def help-usage

  '(defstate server
     "Jetty HTTP server running a Ring application."

     (log/infof "Starting HTTP server on port %s" 8080)
     (run-jetty ring-handler {:port 8080 :join? false})

     :stop

     (log/infof "Stopping HTTP server...")
     (.stop server)
     (log/infof "HTTP Server stopped.")

     :default :my.ns/undefined))

(def help-text
  (format
   "Wrong syntax when declaring a blink state. Usage:

%s

For more examples, see the official page:
%s"
   (with-out-str
     (pprint help-usage))

   help-url))


(defn extra-regroup
  [extra]
  (into-map
   (for [item extra
         :let [{:keys [tag code]} item]]
     [tag code])))


(defn bind-suffix
  [bind suffix]
  (symbol (str (name bind) suffix)))


(defn raise

  ([message]
   (throw (ex-info message {})))

  ([template & args]
   (raise (apply format template args))))


;; main top level ns
;; cljc support


(defmacro defstate
  [bind & body]

  (let [spec :blink.spec/blink
        result (s/conform spec body)]

    (when (invalid? result)
      (raise help-text))

    (let [{:keys [doc start extra]} result

          extra-map (extra-regroup extra)

          {:keys [stop default]} extra-map

          bind-start   (bind-suffix bind "-start")
          bind-stop    (bind-suffix bind "-stop")
          bind-restart (bind-suffix bind "-restart")
          bind-up?     (bind-suffix bind "-up?")
          bind-down?   (bind-suffix bind "-down?")]

      `(let [default# (do ~@default)]

         (defonce
           ~(vary-meta bind assoc
                       :doc doc
                       :dynamic true)
           default#)

         (defn ~bind-up?
           []
           (not= ~bind default#))

         (defn ~bind-down?
           []
           (not (~bind-up?)))

         (defn ~bind-start
           []
           (when (~bind-down?)
             (alter-var-root
              (var ~bind)
              (constantly
               (do ~@start)))))

         (defn ~bind-stop
           []
           (when (~bind-up?)
             ~@stop
             (alter-var-root
              (var ~bind)
              (constantly default#))))

         (defn ~bind-restart
           []
           (~bind-stop)
           (~bind-start))

         default#))))
