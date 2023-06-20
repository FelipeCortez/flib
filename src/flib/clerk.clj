(ns flib.clerk)

(require '[nextjournal.clerk :as clerk])

;; start Clerk's built-in webserver on the default port 7777, opening the browser when done
(clerk/serve! {:browse true})
