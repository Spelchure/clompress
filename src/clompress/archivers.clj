(ns clompress.archivers
  (:require
    [clojure.java.io :as io])
  (:import
    [org.apache.commons.compress.archivers.tar TarArchiveOutputStream]
    [org.apache.commons.compress.archivers.zip ZipArchiveOutputStream]
    [org.apache.commons.compress.compressors CompressorStreamFactory]
    [org.apache.commons.io IOUtils])
  (:gen-class))

(defn- write-file-to-archive [archive entry]
  (IOUtils/copy (io/input-stream entry) archive))

(defn- add-entry-to-archive [archive path get-entry-name-from-path]
  (let [entry (io/file path)
        entry-name (get-entry-name-from-path path)
        archive-entry (.createArchiveEntry archive entry entry-name)]
    (.putArchiveEntry archive archive-entry)
    (when (.isFile entry) (write-file-to-archive archive entry))
    (.closeArchiveEntry archive)))


(defn- tar-archiver [outputstream get-entry-name-from-path & paths]
  (with-open [tar-archive (TarArchiveOutputStream. outputstream)]
    (.setLongFileMode tar-archive (TarArchiveOutputStream/LONGFILE_POSIX))
    (doseq [path paths]
      (add-entry-to-archive tar-archive path get-entry-name-from-path))
    (.finish tar-archive)))

(defn- zip-archiver [outputstream get-entry-name-from-path & paths]
  (with-open [zip-archive (ZipArchiveOutputStream. outputstream)]
    (doseq [path paths]
      (add-entry-to-archive zip-archive path get-entry-name-from-path))
    (.finish zip-archive)))


(def get-archiver
  { "tar" tar-archiver
    "zip" zip-archiver })
