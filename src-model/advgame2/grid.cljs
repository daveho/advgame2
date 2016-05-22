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

(defn get-w [g]
  (:ncol g))

(defn get-h [g]
  (:nrow g))

(defn get-val [g pos]
  (let [x (pos/get-x pos)
        y (pos/get-y pos)
        data (:data g)
        row (get data y)]
    ;(println "Getting at x=" x ", y=" y ",pos=" pos ", result=" (get row x))
    (get row x)))

(defn choose-random-pos [grid pred]
  "Return a random pos for which (pred grid pos) returns true"
  (let [w (get-w grid)
        h (get-h grid)
        choose-fn (fn []
                    (let [x (int (* (js/Math.random) w))
                          y (int (* (js/Math.random) h))
                          candidate (pos/create x y)]
                      (if (pred grid candidate)
                        candidate
                        (recur))))]
    (choose-fn)))
