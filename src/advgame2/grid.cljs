(ns advgame2.grid
  (:require [advgame2.pos :as pos]))

(defn create-grid-data [ncol nrow genfn]
  (vec (for [y (range nrow)]
         (vec (for [x (range ncol)]
                (genfn (pos/create x y)))))))

(defn create [ncol nrow genfn]
  {:ncol ncol
   :nrow nrow
   :data (create-grid-data ncol nrow genfn)})

(defn in-bounds? [g pos]
  (let [x (pos/get-x pos)
        y (pos/get-y pos)
        ncol (:ncol g)
        nrow (:nrow g)]
    (and (>= x 0) (>= y 0) (< x ncol) (< y nrow))))

(defn get-val [g pos]
  (let [x (pos/get-x pos)
        y (pos/get-y pos)
        data (:data g)
        row (get data y)]
    ;(println "Getting at x=" x ", y=" y ",pos=" pos ", result=" (get row x))
    (get row x)))
