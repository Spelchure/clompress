(defproject org.clojars.alperenbayramoglu/clompress "0.1.0-SNAPSHOT"
  :description "Clompress is easy to use library for working with archives, compression and decompression in Clojure."
  :url "https://github.com/Spelchure/clompress"
  :license {:name "MIT"
            :url "https://opensource.org/license/mit/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.apache.commons/commons-compress "1.22"]
                 [commons-io/commons-io "2.10.0"]
                 [org.tukaani/xz "1.8"]]
  :repl-options {:init-ns clompress.core})
