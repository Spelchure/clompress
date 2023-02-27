(ns clompress.archivers
  (:require
    [clojure.java.io :as io]
    [clompress.compression :refer [with-compression]])
  (:import
    [org.apache.commons.compress.archivers.tar TarArchiveOutputStream]
    [org.apache.commons.compress.archivers.zip ZipArchiveOutputStream]
    [org.apache.commons.io IOUtils])
  (:gen-class))

(defn- write-file-to-archive [archive entry]
  (IOUtils/copy (io/input-stream entry) archive)) 

(defn- add-entry-to-archive [archive entry entry-name]
  (let [archive-entry (.createArchiveEntry archive entry entry-name)]
    (.putArchiveEntry archive archive-entry)
    (when (.isFile entry) (write-file-to-archive archive entry))
    (.closeArchiveEntry archive)))

(defn- add-path-to-archive [archive path get-entry-name-from-path]
  (let [entry (io/file path)]
    (if (.isDirectory entry) 
      (->> (file-seq entry)
           (map #(->> (.getPath %1)
                      (get-entry-name-from-path)
                      (add-entry-to-archive archive %1)))
           (doall))
      (add-entry-to-archive archive entry (get-entry-name-from-path path)))))

(defn- tar-archiver [outputstream get-entry-name-from-path & paths]
  (with-open [tar-archive (TarArchiveOutputStream. outputstream)]
    (.setLongFileMode tar-archive (TarArchiveOutputStream/LONGFILE_POSIX))
    (doseq [path paths]
      (add-path-to-archive tar-archive path get-entry-name-from-path))
    (.finish tar-archive)))

(defn- zip-archiver [outputstream get-entry-name-from-path & paths]
  (with-open [zip-archive (ZipArchiveOutputStream. outputstream)]
    (doseq [path paths]
      (add-path-to-archive zip-archive path get-entry-name-from-path))
    (.finish zip-archive)))

(defn- get-output-stream [{:keys [output-stream compression]}] 
    (if (nil? compression)
      output-stream
      (with-compression output-stream compression)))

(defn- default-entry-name-resolver [path]
  (case (first path)
    \/ (subs path 1)
    :else path))

(defn- get-entry-name-resolver [{:keys [entry-name-resolver]}]
  (or entry-name-resolver 
      default-entry-name-resolver))

(def ^:private get-archiver
  { "tar" tar-archiver
    "zip" zip-archiver})

(defn archive [options & paths]
  '"Archives specified files in paths."
  (if-let [archiver (get-archiver (options :archive-type))]
    (apply archiver 
           (get-output-stream options) 
           (get-entry-name-resolver options) 
           paths)
    (throw (IllegalArgumentException. "Archiver is not recognized: "))))

(comment archive {:archive-type "tar" 
                  :output-stream (io/output-stream "my-test.tar")} 
         "<absolute-path>")
