(ns ^:system cmr.service.bridge.tests.system.app.sizing.core
  "Note: this namespace is exclusively for system tests; all tests defined
  here will use one or more system test fixtures.

  Definition used for system tests:
  * https://en.wikipedia.org/wiki/Software_testing#System_testing"
  (:require
    [clojure.test :refer :all]
    [cmr.http.kit.request :as request]
    [cmr.service.bridge.testing.system :as test-system]
    [cmr.service.bridge.testing.util :as util]
    [org.httpkit.client :as httpc]
    [ring.util.codec :as codec]))

(use-fixtures :once test-system/with-system)

(deftest binary-size-with-no-sizing-metadata
  (let [collection-id "C1200267318-HMR_TME"
        granule-id "G1200267320-HMR_TME"
        variable-id "V1200267322-HMR_TME"
        options (-> {}
                    (request/add-token-header (util/get-sit-token)))]
    (testing "With gran and var ..."
      (let [response @(httpc/get
                       (format (str "http://localhost:%s"
                                    "/service-bridge/size-estimate/collection/%s"
                                    "?granules=%s"
                                    "&variables=%s")
                               (test-system/http-port)
                               collection-id
                               granule-id
                               variable-id)
                       options)]
        (is (= 200 (:status response)))
        (is (= "cmr-service-bridge.v2.1; format=json"
               (get-in response [:headers :cmr-media-type])))
        (is (= [{:bytes 6220800
                 :gb 0.005793571472167969
                 :mb 5.9326171875}]
               (util/parse-response response)))))
    (testing "With gran only, implicitly all variables ..."
      (let [response @(httpc/get
                       (format (str "http://localhost:%s"
                                    "/service-bridge/size-estimate/collection/%s"
                                    "?granules=%s")
                               (test-system/http-port)
                               collection-id
                               granule-id)
                       options)]
        (is (= 200 (:status response)))
        (is (= "cmr-service-bridge.v2.1; format=json"
               (get-in response [:headers :cmr-media-type])))
        (is (= [{:bytes 6220800
                 :gb 0.005793571472167969
                 :mb 5.9326171875}]
               (util/parse-response response)))))
    (testing "With no gran or variables; implicitly all collection granules and variables ..."
      (let [response @(httpc/get
                       (format (str "http://localhost:%s"
                                    "/service-bridge/size-estimate/collection/%s")
                               (test-system/http-port)
                               collection-id)
                       options)]
        (is (= 200 (:status response)))
        (is (= "cmr-service-bridge.v2.1; format=json"
               (get-in response [:headers :cmr-media-type])))
        (is (= [{:bytes 6220800
                 :gb 0.005793571472167969
                 :mb 5.9326171875}]
               (util/parse-response response)))))))
