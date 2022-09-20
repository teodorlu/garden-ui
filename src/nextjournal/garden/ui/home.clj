;; # 🌳 Clerk Garden
(ns nextjournal.garden.ui.home
  {:nextjournal.clerk/visibility {:code :hide :result :hide}}
  (:require [nextjournal.clerk :as clerk]))

;; Welcome, friend. **clerk.garden** is a simple publishing space for Clerk notebooks.
;; Here is how it works:

;; ## 1️⃣ Add a `:nextjournal/clerk` alias to your `deps.edn` file.
;; It must contain, at least, the following values:

(defn code-listing [code]
  (clerk/html {::clerk/width :wide} [:div.viewer.viewer-code.not-prose (clerk/code code)]))

{:nextjournal.clerk/visibility {:code :hide :result :show}}



(code-listing
 "{:deps ,,,
 :aliases {:nextjournal/clerk {:exec-fn nextjournal.clerk/build-static-app!
                               :exec-args {:paths [\"src/nextjournal/garden/ui/builder.clj\" ]}}}}
                               ;; TODO: add all notebooks you want to have built ☝️")

;; ## 2️⃣  Try it out locally to make sure it works
;; Entering the following command into your Terminal should build your notebooks and open an
;; index page in your browser.

(code-listing "clj -X:nextjournal/clerk")

;; ## ✅  Push it to GitHub and done!
;; **clerk.garden** mirrors your GitHub URLs, so:
;; * after pushing your project to https://github.com/your-handle/your-project,
;; * you can simply visit https://github.clerk.garden/your-handle/your-project and we’ll build it for you! ✨
