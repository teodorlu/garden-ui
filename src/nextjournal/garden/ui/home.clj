(ns nextjournal.garden.ui.home
  {:nextjournal.clerk/visibility {:code :hide :result :hide}}
  (:require [nextjournal.clerk :as clerk]
            [nextjournal.clerk.viewer :as v]))

(defn code-listing [code]
  (clerk/html {::clerk/width :wide} [:div.viewer.viewer-code.not-prose (clerk/code code)]))

(defn step [indicator md]
  (clerk/html
   [:div.flex.items-center.font-sans.font-bold.step
    [:div.mr-2.rounded-full.border-2.border-green-800.bg-green-100.flex.items-center.justify-center.text-lg.leading-none
     {:class "w-[34px] h-[34px]"}
     indicator]
    (clerk/md md)]))

{:nextjournal.clerk/visibility {:code :hide :result :show}}

(clerk/html
 [:style ".step .viewer-markdown p { margin-bottom: 0; font-size: 1.3rem; }"])

(v/html
 [:div.pointer-events-none.fixed.bottom-0.left-0.right-0.opacity-20
  {:class "h-[250px]"
   :style {:background "linear-gradient(transparent, rgba(88, 199, 0, 1.000) 95%)"}}])

(v/html
 [:div.text-center.pt-24.not-prose
  [:div.absolute.opacity-100
   {:class "text-[200px] left-1/2 top-0 -translate-x-1/2"
    :style {:z-index -1}}
   "🌱"]
  [:div.absolute.bottom-0.left-0.w-full
   {:class "h-[100px]"
    :style {:background "linear-gradient(transparent, white 70%)"
            :z-index -1}}]
  [:h1.text-4xl "Clerk Garden"]
  [:p.text-xl.font-sans.font-bold.mt-2.text-green-800
   "A simple publishing space for Clerk notebooks"]])

(v/with-viewer
  {:render-fn
   '(fn []
      (v/html [:div.text-center.not-prose.font-sans
               (let [navigate-to-repo! (fn [] (let [input (.getElementById js/document "build-input")
                                                    repo (.-value input)
                                                    path (str "/" repo)]
                                                (set! (.-location js/window) path)))]
                 [:div
                  [:label.text-sm.font-bold {:for "build-input"} "Enter a GitHub repo to build and publish"]
                  [:div.relative.max-w-lg.mx-auto
                   [:input.rounded-full.text-center.border.shadow.block.flex-auto.px-4.py-2.text-lg.w-full.my-2
                    {:id "build-input" :placeholder "e.g. nextjournal/clerk-demo"
                     :on-key-down (fn [e] (when (= "Enter" (.-key e))
                                            (navigate-to-repo!)))}]
                   [:button.ml-2.rounded-full.absolute.bg-green-700.text-white.top-0.right-0.flex.items-center.justify-center.hover:bg-green-600.transition-all
                    {:class "w-[34px] h-[34px] mr-[8px] mt-[8px]"
                     :on-click navigate-to-repo!}
                    "→"]]
                  [:p.text-sm
                   "or click here to try an example: "
                   [:a.text-green-800.hover:underline.font-bold {:href "/nextjournal/clerk-demo"} "nextjournal/clerk-demo"]]])]))} nil)

(v/html
 [:div.flex.items-end.justify-center
  [:div.text-xl.mr-1 "🍁"]
  [:div.text-3xl "🍁"]
  [:div.text-xl.ml-1 "🍁"]])

(v/html
 [:div.text-center.not-prose.font-sans
  [:h2 "How does it work?"]])

(step 1 "Add a `:nextjournal/clerk` alias to your `deps.edn` file.")

;; It must contain, at least, the following values:

(code-listing
 "{:deps ,,,
 :aliases {:nextjournal/clerk {:exec-fn nextjournal.clerk/build-static-app!
                               :exec-args {:paths [\"src/nextjournal/garden/ui/builder.clj\" ]}}}}
                               ;; TODO: add all notebooks you want to have built ☝️")

(step 2 "Try it out locally to make sure it works")

;; Entering the following command into your Terminal should build your notebooks and open an
;; index page in your browser.

(code-listing "clj -X:nextjournal/clerk")

(step
 "✓"
 "Push it to GitHub and done!")

;; **clerk.garden** mirrors your GitHub URLs, so:
;; * after pushing your project to https://github.com/your-handle/your-project,
;; * you can simply visit https://github.clerk.garden/your-handle/your-project and we’ll build it for you! ✨
