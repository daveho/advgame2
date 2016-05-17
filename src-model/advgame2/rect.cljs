(ns advgame2.rect
  (:require [advgame2.pos :as pos]))

(defn create [upleft w h]
  "Create a rect from upleft pos, w(idth), and h(eight)"
  {:upleft upleft :w w :h h})

(defn get-upleft [rect]
  "Get the upleft pos of the given rect"
  (:upleft rect))

(defn get-w [rect]
  "Get the width of a rect"
  (:w rect))

(defn get-h [rect]
  "Get the height of a rect"
  (:h rect))

(defn contains-pos? [rect pos]
  "Determine whether pos is inside rect"
  (let [x (pos/get-x pos)
        y (pos/get-y pos)
        rx (pos/get-x (:upleft rect))
        ry (pos/get-y (:upleft rect))
        w (:w rect)
        h (:h rect)]
    (and (>= x rx)
         (>= y ry)
         (< x (+ rx w))
         (< y (+ ry h)))))
