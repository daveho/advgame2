(ns advgame2.overworld
  (:require [perlin.core :as perlin]
            [advgame2.pos :as pos]
            [advgame2.grid :as grid]))

(defn octave [x y z n denom]
  (* (/ n denom) (perlin/noise (* n x) (* n y) z)))

(def octave-vals [1 2 4 8 16])

(defn noise-at [x y z]
  (apply + (map (fn [n] (octave x y z n (* (last octave-vals) 2))) octave-vals)))

(defn height-to-terrain [h]
  (cond
    (<= h -0.1) "W"
    (<= h 0.08) "w"
    (<= h 0.2) "g"
;    (<= h 0.5) "F"
    (<= h 0.28) "p"
    (<= h 0.32) "f"
    (<= h 1.0) "m"))

(defn create [ncol nrow]
  "Create a random overworld grid"
  (let [z (js/Math.random)
        genfn (fn [pos]
                (let [x (/ (pos/get-x pos) ncol)
                      y (/ (pos/get-y pos) nrow)]
                  (height-to-terrain (noise-at x y z))))]
    (grid/create ncol nrow genfn)))
