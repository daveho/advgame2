(ns advgame2.core
  (:require [clojure.browser.repl :as repl]
            [tincan.core :as tin]
            [perlin.core :as perlin]
            [goog.events :as events])
  (:import [goog.events KeyHandler]
           [goog.events.KeyHandler EventType]))

(defonce conn
  (repl/connect "http://localhost:9000/repl"))

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

;; (def overworld-spec
;;   (str "WWWWWWwwWWWWWWW"
;;        "WWwwwwwwwwwwWWW"
;;        "WWWwgggggggwwWW"
;;        "WWwggppgggwwwWW"
;;        "WWggpffpggggwww"
;;        "Wwwgpfmfpggwwww"
;;        "wggpfmmffpggwww"
;;        "wwggpfmfpgggwww"
;;        "Wwwwgfppgggggww"
;;        "WWWwwgpggggwwww"
;;        "WWWWwwgggwwwwww"
;;        "WWWwwggwwwwwwwW"
;;        "WwwggFFgggwwwwW"
;;        "wwwwFFFFgwwwWWW"
;;        "wwwwwgFgwwwWWWW"
;;        )
;;   )

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

;; Convert a sequence of noise values into terrain characters
(defn gen-terrain-row [vals]
  (apply str (map height-to-terrain vals))
  )

(defn gen-terrain []
  (mapv gen-terrain-row (noise-values-for-terrain-map))
  )

(def overworld-spec (gen-terrain))

(defn char-at [s idx]
  (subs s idx (+ idx 1)))

(defn log-event [event]
  (.log js/console event))

(defn keyboard-events
  []
  (events/listen (KeyHandler. js/document) EventType.KEY log-event))

(defn draw-map []
  (doall
   (for [x (range 15)
         y (range 15)]
     (let [row (get overworld-spec y)
           c (char-at row x)
           img (get tile-images c)]
       (tin/draw-image ctx img (* x 32) (* y 32)))))
  )

(enable-console-print!)

(defn start []
  (do
    (keyboard-events)
    (draw-map)
    ))

(set! (.-onload js/window) (fn [] (start)))

(println "Hello world!")
