(ns flib.repl)

(def dev? (some #(= "clojure.main$repl" (.getClassName %))
                (.getStackTrace (Thread/currentThread))))

(defn politely-spit [f contents] (io/make-parents f) (spit f contents))
