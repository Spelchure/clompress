(ns clompress.compression-test
  (:require [clojure.test :refer :all]
            [clompress.compression :refer :all]))

(defn- string->stream
  ([s] (string->stream s "UTF-8"))
  ([s encoding]
   (-> s
       (.getBytes encoding)
       (java.io.ByteArrayInputStream.))))

(let [outputstream (java.io.ByteArrayOutputStream. 512)
      otherstream (java.io.ByteArrayOutputStream. 512)]
  (compress (string->stream "test") outputstream "bzip2")
  (println (.toString outputstream)))
  ;(decompress (string->stream (.toString outputstream))  otherstream "bzip2")
  ;(println (.toString otherstream)))

(compress (string->stream "test"))

; TODO write tests !
(deftest compression-decompression-tests
  (testing "Compression decompression should work correctly."
    (is (= 0 1))))
