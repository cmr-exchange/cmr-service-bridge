(ns cmr.service.bridge.app.routes.site
  "This namespace defines the REST routes provided by this service.

  Upon idnetifying a particular request as matching a given route, work is then
  handed off to the relevant request handler function."
  (:require
   [cmr.http.kit.app.handler :as base-handler]
   [cmr.http.kit.site.pages :as base-pages]
   [cmr.service.bridge.app.handler.core :as core-handler]
   [cmr.service.bridge.components.config :as config]
   [cmr.service.bridge.health :as health]
   [cmr.service.bridge.site.pages :as pages]
   [reitit.ring :as ring]
   [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   CMR Service-Bridge Routes   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn main
  [httpd-component]
  [["/service-bridge" {
    :get (base-handler/dynamic-page
          httpd-component
          pages/home
          {:base-url (config/service-bridge-url httpd-component)})
    :head base-handler/ok}]])

(defn docs
  "Note that these routes only cover part of the docs; the rest are supplied
  via static content from specific directories (done in middleware)."
  [httpd-component]
  [["/service-bridge/docs" {
    :get (base-handler/dynamic-page
          httpd-component
          pages/service-bridge-docs
          {:base-url (config/service-bridge-url httpd-component)})}]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Static & Redirect Routes   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn redirects
  [httpd-component]
  [["/service-bridge/robots.txt" {
    :get (base-handler/permanent-redirect
          (str (config/get-search-url httpd-component)
               "/robots.txt"))}]])

(defn static
  [httpd-component]
  [;; Google verification files
   ["/service-bridge/googled099d52314962514.html" {
    :get (core-handler/text-file
          "public/verifications/googled099d52314962514.html")}]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Assembled Routes   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn all
  [httpd-component]
  (concat
   (main httpd-component)
   (docs httpd-component)
   (redirects httpd-component)
   (static httpd-component)))
