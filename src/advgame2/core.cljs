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

(def z (js/Math.random))

(defn octave [x y n denom]
  (* (/ n denom) (perlin/noise (* n x) (* n y) z)))

(def octave-vals [1 2 4 8 16])

(defn noise-at [x y]
  (apply + (map (fn [n] (octave x y n 32)) octave-vals)))

(def MAP_SIZE 300)
(def VIEWPORT_SIZE 32)

(defn noise-values-for-terrain-map []
  (for [i (range MAP_SIZE)]
    (let [y (/ i (double MAP_SIZE))]
      (for [j (range MAP_SIZE)]
        (let [x (/ j (double MAP_SIZE))]
          (noise-at x y)))))) 

(defn height-to-terrain [h]
  (cond
    (<= h -0.1) "W"
    (<= h 0.08) "w"
    (<= h 0.2) "g"
;    (<= h 0.5) "F"
    (<= h 0.28) "p"
    (<= h 0.32) "f"
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
     (for [x (range (:x pos) (+ (:x pos) VIEWPORT_SIZE))
           y (range (:y pos) (+ (:y pos) VIEWPORT_SIZE))]
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
       (<= (+ (:x pos) VIEWPORT_SIZE) MAP_SIZE)
       (<= (+ (:y pos) VIEWPORT_SIZE) MAP_SIZE)))

(defn handle-key-event [event]
  (let [key-code (.-keyCode event)]
    (if (is-arrow-key event)
      (let [next (next-pos (:pos @state) event)]
        ;(println "next.x=" (:x next) ", next.y=" (:y next))
        (if (pos-in-bounds? next)
          (do
            (swap! state assoc :pos next)
            (draw-map))
          )
        ))))

(defn keyboard-events
  []
  (events/listen (KeyHandler. js/document) EventType.KEY handle-key-event))

(defn set-display! [id dispval]
  (let [elt (js/document.getElementById id)
        style (.-style elt)
        ]
    ;(println "style=" style)
    (aset style "display" dispval)))

(defn start []
  (do
    (keyboard-events)
    (draw-map)
    (set-display! "loading" "none")
    (set-display! "instructions" "block")
    ))

(set! (.-onload js/window) (fn [] (start)))

(println "Hello world!")
