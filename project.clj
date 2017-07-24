(defproject fins "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.3.443"]
                 [com.taoensso/carmine "2.16.0"]
                 [prismatic/schema "1.1.6"]
                 [com.cerner/clara-rules "0.15.1"]
                 [ring "1.6.2"]
                 [mount "0.1.11"]
                 [http-kit "2.2.0"]]
  :source-paths ["src" "dev"])
