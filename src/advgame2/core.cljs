(ns advgame2.core
  (:require ;[clojure.browser.repl :as repl]
            [tincan.core :as tin]
            [goog.events :as events]
            [advgame2.pos :as pos]
            [advgame2.grid :as grid]
            [advgame2.overworld :as overworld]
            )
  (:import [goog.events KeyHandler]
           [goog.events.KeyHandler EventType]))

;(defonce conn
;  (repl/connect "http://localhost:9000/repl"))

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

;; Initial dummy state
(def state (atom {:grid nil :pos (pos/create 0 0)}))

(defn draw-map [map-grid pos]
  (let [xmin (- (:x pos) VIEWPORT_SIZE_HALF)
        ymin (- (:y pos) VIEWPORT_SIZE_HALF)]
    (doall
     (for [y (range ymin (+ ymin VIEWPORT_SIZE))
           x (range xmin (+ xmin VIEWPORT_SIZE))]
       (let [tpos (pos/create x y)]
         (if (grid/in-bounds? map-grid tpos)
           ; draw terrain icon
           (let [c (grid/get-val map-grid tpos)
                 img (get tile-images c)]
             (tin/draw-image ctx img (* (- x xmin) 32) (* (- y ymin) 32)))
           ; out of bounds, fill with black
           (do
             (tin/set-fill-style! ctx "#000")
             (tin/fill-rect ctx (* (- x xmin) 32) (* (- y ymin) 32) 32 32))))))))

(defn draw-player-sprite [map-grid pos]
  (let [c (grid/get-val map-grid pos)
        img (if (or (= c "w") (= c "W")) boat-image knight-image)]
    (tin/draw-image ctx img (* VIEWPORT_SIZE_HALF 32) (* VIEWPORT_SIZE_HALF 32))))

(defn redraw [map-grid pos]
  (draw-map map-grid pos)
  (draw-player-sprite map-grid pos))

(defn choose-initial-pos [map-grid]
  (let [x (int (* (js/Math.random) MAP_SIZE))
        y (int (* (js/Math.random) MAP_SIZE))
        tval (grid/get-val map-grid (pos/create x y))]
    (if (= tval "g")
      (pos/create x y)
      (recur map-grid))))

(defn is-arrow-key? [event]
  (contains? #{37 38 39 40} (.-keyCode event)))

(defn next-pos [cur-pos event]
  (condp = (.-keyCode event)
    37 (assoc cur-pos :x (dec (:x cur-pos))) ; left
    38 (assoc cur-pos :y (dec (:y cur-pos))) ; up
    39 (assoc cur-pos :x (inc (:x cur-pos))) ; right
    40 (assoc cur-pos :y (inc (:y cur-pos))) ; down
    (throw (js/Error. "Not an arrow key"))))

(defn handle-key-event [event]
  (let [key-code (.-keyCode event)
        map-grid (:grid @state)
        ]
    (if (is-arrow-key? event)
      (let [next (next-pos (:pos @state) event)]
        ;(println "next.x=" (:x next) ", next.y=" (:y next))
        (if (grid/in-bounds? map-grid next)
          (do
            (swap! state assoc :pos next)
            (redraw (:grid @state) (:pos @state))))))))

(defn keyboard-events
  []
  (events/listen (KeyHandler. js/document) EventType.KEY handle-key-event))

(defn set-display! [id dispval]
  (let [elt (js/document.getElementById id)
        style (.-style elt)]
    (aset style "display" dispval)))

(defn start []
  (do
    ;(swap! state assoc :map (gen-terrain))
    (println "Creating overworld...")
    (swap! state assoc :grid (overworld/create MAP_SIZE MAP_SIZE))
    (println "done")
    (println "Choosing initial pos...")
    (swap! state assoc :pos (choose-initial-pos (:grid @state)))
    (println "done, initial pos=" (:pos @state))
    (keyboard-events)
    (redraw (:grid @state) (:pos @state))
    (set-display! "loading" "none")
    (set-display! "instructions" "block")
    ))

(set! (.-onload js/window) start)

(println "Hello world!")
