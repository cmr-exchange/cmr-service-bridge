(ns cmr.service.bridge.core
  (:require
   [clojusc.twig :as logger]
   [cmr.service.bridge.components.core :as components]
   [com.stuartsierra.component :as component]
   [trifl.java :as trifl])
  (:gen-class))

(logger/set-level! '[cmr] :info logger/no-color-log-formatter)

(defn -main
  [& args]
  (let [system (components/init)]
    (component/start system)
    (trifl/add-shutdown-handler #(component/stop system))))
