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

(def MAP_SIZE 300)
(def VIEWPORT_SIZE 15)
(def VIEWPORT_SIZE_HALF (int (/ VIEWPORT_SIZE 2)))

(def c (js/document.getElementById "canvas"))

(def ctx (tin/get-context c))

(defn load-image [src]
  (let [img (new js/Image)]
    (set! (.-src img) src)
    img))

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

(def knight-image (load-image "asset/img/knight.png"))

(def boat-image (load-image "asset/img/boat-right.png"))

(defn octave [x y z n denom]
  (* (/ n denom) (perlin/noise (* n x) (* n y) z)))

(def octave-vals [1 2 4 8 16])

(defn noise-at [x y z]
  (apply + (map (fn [n] (octave x y z n (* (last octave-vals) 2))) octave-vals)))

(defn pos-in-bounds? [pos]
  (and (>= (:x pos) 0)
       (>= (:y pos) 0)
       (< (:x pos) MAP_SIZE)
       (< (:y pos) MAP_SIZE)))

(defn noise-values-for-terrain-map []
  (let [z (js/Math.random)]
    (for [i (range MAP_SIZE)]
      (let [y (/ i (double MAP_SIZE))]
        (for [j (range MAP_SIZE)]
          (let [x (/ j (double MAP_SIZE))]
            (noise-at x y z)))))))

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
  (mapv gen-terrain-row (noise-values-for-terrain-map)))

(defn terrain-at [map-spec x y]
  (get (get map-spec y) x))

;; Initial dummy state
(def state (atom {:map nil :pos {:x 0 :y 0}}))

(defn draw-map [map-spec pos]
  (let [xmin (- (:x pos) VIEWPORT_SIZE_HALF)
        ymin (- (:y pos) VIEWPORT_SIZE_HALF)]
    ;(println "xmin=" xmin ", ymin=" ymin)
    (doall
     (for [x (range xmin (+ xmin VIEWPORT_SIZE))
           y (range ymin (+ ymin VIEWPORT_SIZE))]
       (if (pos-in-bounds? {:x x :y y})
         (let [c (terrain-at map-spec x y)
               img (get tile-images c)]
           (tin/draw-image ctx img (* (- x xmin) 32) (* (- y ymin) 32)))
         (do
           (tin/set-fill-style! ctx "#000")
           (tin/fill-rect ctx (* (- x xmin) 32) (* (- y ymin) 32) 32 32))))

     ))
  (let [c (terrain-at map-spec (:x pos) (:y pos))
        img (if (or (= c "w") (= c "W")) boat-image knight-image)]
    (tin/draw-image ctx img (* VIEWPORT_SIZE_HALF 32) (* VIEWPORT_SIZE_HALF 32))))

(defn choose-initial-pos [map-spec]
  (let [x (int (* (js/Math.random) MAP_SIZE))
        y (int (* (js/Math.random) MAP_SIZE))
        tval (terrain-at map-spec x y)]
    (if (= tval "g")
      {:x x :y y}
      (recur map-spec))))

(defn is-arrow-key [event]
  (contains? #{37 38 39 40} (.-keyCode event)))

(defn next-pos [cur-pos event]
  (condp = (.-keyCode event)
    37 (assoc cur-pos :x (dec (:x cur-pos))) ; left
    38 (assoc cur-pos :y (dec (:y cur-pos))) ; up
    39 (assoc cur-pos :x (inc (:x cur-pos))) ; right
    40 (assoc cur-pos :y (inc (:y cur-pos))) ; down
    (throw (js/Error. "Not an arrow key"))))

(defn handle-key-event [event]
  (let [key-code (.-keyCode event)]
    (if (is-arrow-key event)
      (let [next (next-pos (:pos @state) event)]
        ;(println "next.x=" (:x next) ", next.y=" (:y next))
        (if (pos-in-bounds? next)
          (do
            (swap! state assoc :pos next)
            (draw-map (:map @state) (:pos @state)))
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
    (swap! state assoc :map (gen-terrain))
    (swap! state assoc :pos (choose-initial-pos (:map @state)))
    (keyboard-events)
    (draw-map (:map @state) (:pos @state))
    (set-display! "loading" "none")
    (set-display! "instructions" "block")
    ))

(set! (.-onload js/window) (fn [] (start)))

(println "Hello world!")
