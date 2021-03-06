(ns solanum.system.core
  "System-specific information and utilities."
  (:require
    [clojure.java.shell :as shell]
    [clojure.string :as str]
    [clojure.tools.logging :as log]))


(def os-info
  "Delayed map with the local operating system's `:name` and `:release`, as
  returned by `uname`."
  (delay
    (try
      (let [result (shell/sh "uname" "-sr")]
        (if (zero? (:exit result))
          (zipmap
            [:name :release]
            (-> (:out result)
                (str/trim-newline)
                (str/split #" " 2)))
          (log/warn "Failed to determine operating system information:"
                    (pr-str (:err result)))))
      (catch Exception ex
        (log/error ex "Error while determining operating system information")))))


(defn detect
  "Determine what mode to run in for compatibility with the local operating
  system."
  [source-type supported requested default]
  (let [mode (or requested (keyword (str/lower-case (:name @os-info))))]
    (if (contains? supported mode)
      mode
      (do (log/warnf "Unsupported %s source mode %s - falling back to %s"
                     source-type (pr-str mode) default)
          default))))
