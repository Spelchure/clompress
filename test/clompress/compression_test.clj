(ns clompress.compression-test
  (:require [clojure.test :refer :all]
            [clompress.compression :refer :all]))

(defn compress-then-decompress-data [data compression]
    (with-open [input-stream (java.io.ByteArrayInputStream. (.getBytes data))
                output-stream (java.io.ByteArrayOutputStream.)]
      (compress input-stream output-stream compression)
      (with-open [decompressed-stream (java.io.ByteArrayOutputStream.)]
        (decompress (java.io.ByteArrayInputStream. (.toByteArray output-stream)) 
                    decompressed-stream compression)
        (.toString decompressed-stream))))

(deftest compression-decompression-tests
  (testing "[BZIP2] Compression decompression should work correctly."
    (let [data "Simple string data for testing compression."
          compressed-decompressed-data 
          (compress-then-decompress-data data "bzip2")]
      (is (= data compressed-decompressed-data))))
  (testing "[GZ] Compression decompression should work correctly."
    (let [data "Simple string data for testing compression."
          compressed-decompressed-data 
          (compress-then-decompress-data data "gz")]
      (is (= data compressed-decompressed-data))))
  (testing "[DEFLATE] Compression decompression should work correctly."
    (let [data "Simple string data for testing compression."
          compressed-decompressed-data 
          (compress-then-decompress-data data "deflate")]
      (is (= data compressed-decompressed-data)))))
 
(with-open [input-stream (java.io.ByteArrayInputStream. 
                           (.getBytes string))]
  (with-open [output-stream (java.io.ByteArrayOutputStream.)]
    (clompress.compression/compress input-stream output-stream "bzip2")))

