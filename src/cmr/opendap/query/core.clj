(ns cmr.opendap.query.core
  "This namespace defines records for the accepted URL query parameters or, if
  using HTTP POST, keys in a JSON payload. Additionall, functions for working
  with these parameters are defined here."
  (:require
   [clojure.set :as set]
   [clojure.string :as string]
   [cmr.opendap.query.const :as const]
   [cmr.opendap.query.impl.wcs :as wcs]
   [cmr.opendap.query.impl.cmr :as cmr]
   [cmr.opendap.ous.util.core :as util]
   [cmr.opendap.results.errors :as errors]
   [taoensso.timbre :as log])
  (:import
   (cmr.opendap.query.impl.cmr CollectionCmrStyleParams)
   (cmr.opendap.query.impl.wcs CollectionWcsStyleParams))
  (:refer-clojure :exclude [parse]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Initial Setup & Utility Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def params-keys
  "This function returns only the record fields that are unique to the
  WCS-style parameters. This is done by checking against a hard-coded set of
  shared fields (see the `const` namespace)."
  (set/difference
   (set (keys (wcs/map->CollectionWcsStyleParams {})))
   const/shared-keys))

(defn wcs-style?
  [raw-params]
  "This function checks the raw params to see if they have any keys that
  overlap with the WCS-style record."
  (seq (set/intersection
        (set (keys raw-params))
        params-keys)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Protocol Defnition   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defprotocol CollectionParamsAPI
  (->cmr [this]))

(extend CollectionCmrStyleParams
        CollectionParamsAPI
        cmr/collection-behaviour)

(extend CollectionWcsStyleParams
        CollectionParamsAPI
        wcs/collection-behaviour)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Constructor   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create
  ([raw-params]
    (create (cond (wcs-style? raw-params) :wcs
                  (:collection-id raw-params) :cmr
                  :else :unknown-parameters-type)
            raw-params))
  ([params-type raw-params]
    (case params-type
      :wcs (wcs/create raw-params)
      :cmr (cmr/create raw-params)
      :unknown-parameters-type {:errors [errors/invalid-parameter
                                         (str "Parameters: " raw-params)]})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   High-level API   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn parse
  "This is a convenience function for calling code that wants to create a
  collection params instance "
  [raw-params]
  (let [collection-params (create raw-params)]
    (if (errors/erred? collection-params)
      collection-params
      (->cmr collection-params))))
