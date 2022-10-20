;; # 🌳 Clerk Garden Builder
(ns nextjournal.clerk.builder-state
  (:require [nextjournal.clerk :as clerk]
            [nextjournal.clerk.analyzer :as analyzer]
            [nextjournal.clerk.builder :as builder]
            [nextjournal.clerk.parser :as parser]
            [clojure.string :as str]))

;; ## Initial state
;; We start with an initial fileset from `deps.edn`.
(def paths
  (-> "deps.edn" slurp analyzer/read-string :aliases :nextjournal/clerk :exec-args :paths builder/expand-paths))

;; Our initial state is a seq of maps with only a `:file` key. This is the first state we want to visualize. Successive states should add to this map.
(def initial-state
  (mapv #(hash-map :file %) paths))

(first initial-state)

;; ## Parsing
;; We parse & hash all files at once to fail early.
(def parsed
  (mapv (comp parser/parse-file :file) initial-state))

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
  (mapv analyzer/build-graph parsed))

(def analyzed-state
  (mapv process-doc analyzed))

(assoc (first analyzed-state) :state :analyzed)

;; ## Execution Progress
;; As all the cells inside an analyzed notebook are executed, exec durations are collected
;; and a total is calculated. Execution count is updated as the cells complete.
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
;; Once all cells are done, the notebook is deemed :done as well.
(assoc (first parsed-state) :state :done)
