(ns cmr.service.bridge.app.middleware
  "Custom ring middleware for CMR Service-Bridge."
  (:require
   [clojusc.twig :refer [pprint]]
   [cmr.ous.util.http.request :as request]
   [cmr.http.kit.response :as response]
   [cmr.metadata.proxy.components.auth :as auth]
   [cmr.versioning.rest.middleware :as middleware]
   [reitit.ring :as ring]
   [taoensso.timbre :as log]))

(defn wrap-auth
  "Ring-based middleware for supporting the protection of routes using the CMR
  Access Control service and CMR Legacy ECHO support.

  In particular, this wrapper allows for the protection of routes by both roles
  as well as concept-specific permissions. This is done by annotating the routes
  per the means described in the reitit library's documentation."
  [handler system]
  (fn [req]
    (log/debug "Running perms middleware ...")
    (auth/check-route-access system handler req)))

(defn reitit-auth
  [system]
  "This auth middleware is specific to reitit, providing the data structure
  necessary that will allow for the extraction of roles and permissions
  settings from the request.

  For more details, see the docstring above for `wrap-auth`."
  {:data
    {:middleware [#(wrap-auth % system)]}})

(defn wrap-api-version-dispatch
  ""
  [site-routes route-data system]
  (middleware/wrap-api-version-dispatch
    site-routes
    route-data
    {:auth-wrapper reitit-auth}))
