(ns advgame2.core
  (:require [clojure.browser.repl :as repl]
            [tincan.core :as tin]
            [perlin.core :as perlin]
            [goog.events :as events])
  (:import [goog.events KeyHandler]
           [goog.events.KeyHandler EventType]))

(defonce conn
  (repl/connect "http://localhost:9000/repl"))

(enable-console-print!)

(def c (js/document.getElementById "canvas"))

(def ctx (tin/get-context c))

(defn load-image [src]
  (let [img (new js/Image)]
    (set! (.-src img) src)
    img))

(def tile-img (load-image "asset/img/Water.png"))

(def tile-images
  {"w" (load-image "asset/img/Water.png"),
   "W" (load-image "asset/img/DeepWater.png"),
   "g" (load-image "asset/img/Grass.png"),
   "p" (load-image "asset/img/Plateau.png"),
   "f" (load-image "asset/img/Foothills.png"),
   "m" (load-image "asset/img/Mountains.png"),
   "F" (load-image "asset/img/Forest.png")
   }
  )

(defn noise-at [x y]
  (+ (* .066666 (perlin/noise x y 0.0))              ; 1/15
     (* .133333 (perlin/noise (* 2 x) (* 2 y) 0.0))  ; 2/15
     (* .266666 (perlin/noise (* 4 x) (* 4 y) 0.0))  ; 4/15
     (* .533333 (perlin/noise (* 8 x) (* 8 y) 0.0))  ; 8/15
     )
  )

(def MAP_SIZE 64)

(defn noise-values-for-terrain-map []
  (for [i (range MAP_SIZE)]
    (let [y (/ i (double MAP_SIZE))]
      (for [j (range MAP_SIZE)]
        (let [x (/ j (double MAP_SIZE))]
          (noise-at x y)))))) 

(defn height-to-terrain [h]
  (cond
    (<= h -0.5) "W"
    (<= h 0.0) "w"
    (<= h 0.4) "g"
    (<= h 0.5) "F"
    (<= h 0.6) "p"
    (<= h 0.7) "f"
    (<= h 1.0) "m")
  )

;; Convert a sequence of noise values into vector of terrain characters
(defn gen-terrain-row [vals]
  (vec (map height-to-terrain vals)))

(defn gen-terrain []
  (mapv gen-terrain-row (noise-values-for-terrain-map))
  )

(def overworld-spec (gen-terrain))

(def state (atom
 {:map overworld-spec
  :pos {:x 0 :y 0}
  }))

(defn draw-map []
  (let [map-spec (:map @state)
        pos (:pos @state)]
    (doall
     (for [x (range (:x pos) (+ (:x pos) 32))
           y (range (:y pos) (+ (:y pos) 32))]
       (let [row (get map-spec y)
             c (get row x)
             img (get tile-images c)]
         (tin/draw-image ctx img (* (- x (:x pos)) 16) (* (- y (:y pos)) 16) 16 16))))
    )
  )


(defn is-arrow-key [event]
  (contains? #{37 38 39 40} (.-keyCode event)))

(defn next-pos [cur-pos event]
  (condp = (.-keyCode event)
    37 (assoc cur-pos :x (dec (:x cur-pos))) ; left
    38 (assoc cur-pos :y (dec (:y cur-pos))) ; up
    39 (assoc cur-pos :x (inc (:x cur-pos))) ; right
    40 (assoc cur-pos :y (inc (:y cur-pos))) ; down
    (throw (js/Error. "Not an arrow key"))))

(defn pos-in-bounds? [pos]
  (and (not (neg? (:x pos)))
       (not (neg? (:y pos)))
       (<= (+ (:x pos) 32) MAP_SIZE)
       (<= (+ (:y pos) 32) MAP_SIZE)))

(defn handle-key-event [event]
  (let [key-code (.-keyCode event)]
    (if (is-arrow-key event)
      (let [next (next-pos (:pos @state) event)]
        (println "next.x=" (:x next) ", next.y=" (:y next))
        (if (pos-in-bounds? next)
          (do
            (swap! state assoc :pos next)
            (draw-map))
          )
        ))))

(defn keyboard-events
  []
  (events/listen (KeyHandler. js/document) EventType.KEY handle-key-event))

(defn start []
  (do
    (keyboard-events)
    (draw-map)
    ))

(set! (.-onload js/window) (fn [] (start)))

(println "Hello world!")
