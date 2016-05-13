(ns advgame2.grid
  (:require [advgame2.pos :as pos])
  )

(defn create-terrain-vec [ncol nrow genfn]
  (vec (for [y (range 0 nrow)]
         (vec (for [x (range 0 ncol)]
                (genfn (pos/create x y)))))))

(defn create [ncol nrow genfn]
  {:ncol ncol
   :nrow nrow
   :data (create-terrain-vec ncol nrow genfn)})

(defn in-bounds? [m pos]
  (let [x (:x pos)
        y (:y pos)
        ncol (:ncol m)
        nrow (:nrow m)]
    (and (>= x 0) (>= y 0) (< x ncol) (< y nrow))))

(defn get-val [m pos]
  (let [x (:x pos)
        y (:y pos)
        data (:data m)
        row (get data y)]
    (get row x)))
