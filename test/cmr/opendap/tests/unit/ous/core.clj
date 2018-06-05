(ns cmr.opendap.tests.unit.ous.core
  "Note: this namespace is exclusively for unit tests."
  (:require
    [clojure.test :refer :all]
    [clojusc.twig :as logger]
    [cmr.opendap.ous.core :as core]
    [cmr.opendap.ous.variable :as variable]))

(logger/set-level! '[] :fatal)

(deftest bounding-infos->opendap-query
  (let [dims (array-map :Latitude 180 :Longitude 360)]
    (testing "No bounds ..."
     (is (= "?MyVar,Latitude,Longitude"
            (core/bounding-infos->opendap-query
             [{:name "MyVar"
               :dimensions dims
               :original-dimensions dims}])))
     (is (= "?MyVar1,MyVar2,Latitude,Longitude"
            (core/bounding-infos->opendap-query
             [{:name "MyVar1"
               :dimensions dims
               :original-dimensions dims}
              {:name "MyVar2"
               :dimensions dims
               :original-dimensions dims}]))))
    (testing "With bounds ..."
      (let [bounds [-27.421875 53.296875 18.5625 69.75]
            bounding-info [{:name "MyVar"
                            :bounds bounds
                            :dimensions dims
                            :original-dimensions dims
                            :opendap (variable/create-opendap-bounds
                                      dims bounds)}]]
       (is (= "?MyVar[20:1:37][152:1:199],Latitude[20:1:37],Longitude[152:1:199]"
              (core/bounding-infos->opendap-query bounding-info bounds)))))))
