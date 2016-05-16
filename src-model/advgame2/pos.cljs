(ns advgame2.pos)

(defn create [x y]
  {:x x :y y})

(defn get-x [pos]
  (:x pos))

(defn get-y [pos]
  (:y pos))
