(ns advgame2.core
  (:require [clojure.browser.repl :as repl]
            [tincan.core :as tin]
            [perlin.core :as perlin]))

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

(def overworld-spec
  (str "WWWWWWwwWWWWWWW"
       "WWwwwwwwwwwwWWW"
       "WWWwgggggggwwWW"
       "WWwggppgggwwwWW"
       "WWggpffpggggwww"
       "Wwwgpfmfpggwwww"
       "wggpfmmffpggwww"
       "wwggpfmfpgggwww"
       "Wwwwgfppgggggww"
       "WWWwwgpggggwwww"
       "WWWWwwgggwwwwww"
       "WWWwwggwwwwwwwW"
       "WwwggFFgggwwwwW"
       "wwwwFFFFgwwwWWW"
       "wwwwwgFgwwwWWWW"
       )
  )

(defn noise-at [x y]
  (+ (* .066666 (perlin/noise x y 0.0))              ; 1/15
     (* .133333 (perlin/noise (* 2 x) (* 2 y) 0.0))  ; 2/15
     (* .266666 (perlin/noise (* 4 x) (* 4 y) 0.0))  ; 4/15
     (* .533333 (perlin/noise (* 8 x) (* 8 y) 0.0))  ; 8/15
     )
  )

(defn gen-terrain []
  (for [i (range 0 256)
        j (range 0 256)]
    (let [y (/ j 256.0)
          x (/ i 256.0)]
      (noise-at x y))))

(defn char-at [s idx]
  (subs s idx (+ idx 1)))

(enable-console-print!)

(defn start []
  (do
      (doall
        (for [x (range 15)
              y (range 15)]
          (let [c (char-at overworld-spec (+ (* y 15) x))
                img (get tile-images c)]
            (tin/draw-image ctx img (* x 32) (* y 32)))))
    
    ))

(set! (.-onload js/window) (fn [] (start)))

(println "Hello world!")
