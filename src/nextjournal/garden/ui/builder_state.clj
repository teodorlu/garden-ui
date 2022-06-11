;; # 👷🏼 Garden Builder
(ns ^:nextjournal.clerk/no-cache nextjournal.garden.ui.builder-state
  (:require [nextjournal.clerk :as clerk]
            [nextjournal.clerk.hashing :as h]
            [clojure.string :as str]))

;; ## Initial state
;; We start with an initial fileset that’s defined in `deps.edn`.
(def paths
  (-> "deps.edn" slurp h/read-string :aliases :nextjournal/clerk :exec-args :paths))

;; Our initial state is a seq of maps with only a `:file` key. This is the first state we want to visualize. Successive states should add to this map.
(def initial-state
  (mapv #(hash-map :file %) paths))

(first initial-state)

;; ## Parsing
;; We parse & hash all files at once to fail early.
(def parsed
  (mapv (comp clerk/parse-file :file) initial-state))

(defn process-doc [{:as doc :keys [blocks graph]}]
  (cond-> (-> doc
            (select-keys [:file :title])
            (assoc :block-counts (frequencies (map :type blocks))))
    graph (assoc :code-blocks (mapv (fn [{:as block :keys [form]}]
                                      (cond-> (select-keys block [:var])
                                        form (assoc :text (pr-str form))))
                                (filter (comp #{:code} :type) blocks)))))

(def parsed-state
  (mapv process-doc parsed))

(assoc (first parsed-state) :state :parsed)

;; ## Analyzing
;; When then analyze the build graph and enrich the state with additional
;; information.

(def analyzed
  (mapv h/build-graph parsed))

(def analyzed-state
  (mapv process-doc analyzed))

(assoc (first analyzed-state) :state :analyzed)

;; ## Execution Progress

(defn process-exec-ratio [doc]
  (update doc :code-blocks
    (fn [code-blocks]
      (let [durations (->> code-blocks (map :exec-duration) (remove nil?))
            total (reduce + durations)]
        (map (fn [{:as b :keys [exec-duration]}]
               (if exec-duration
                 (assoc b :exec-ratio (/ exec-duration total))
                 b))
          code-blocks)))))

(-> (first analyzed-state)
  (assoc :state :executing)
  (assoc-in [:block-counts :code-executing] 3)
  (update :code-blocks
    (fn [code-blocks]
      (map-indexed
        (fn [i b]
          (if (< i 7)
            (assoc b :exec-state (if (< i 6) :done :executing)
                     :exec-duration (nth [251 788 2340 14251 303 40122 3129] i))
            b)) code-blocks)))
  process-exec-ratio)

;; ## Done

(-> (first analyzed-state)
  (assoc :state :done)
  (update :code-blocks
    (fn [code-blocks]
      (map-indexed
        (fn [i b]
          (assoc b :exec-state :done
                   :exec-duration (rand-int 40000))) code-blocks)))
  process-exec-ratio)
(assoc (first analyzed-state) :state :done)

