(ns advgame2.core
  (:require [clojure.browser.repl :as repl]
            [monet.canvas :as canvas]))

;;(defonce conn
;;  (repl/connect "http://localhost:9000/repl"))

(def canvas-dom (.getElementById js/document "canvas"))

(def monet-canvas (canvas/init canvas-dom "2d"))

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

(defn char-at [s idx]
  (subs s idx (+ idx 1)))

(enable-console-print!)

(defn start []
  (do
    (canvas/add-entity monet-canvas :background
                       (canvas/entity {:x 0 :y 0 :w 640 :h 480} ; val
                                      nil                       ; update function
                                      (fn [ctx val]             ; draw function
                                        (-> ctx
                                            (canvas/fill-style "#00ff00")
                                            (canvas/fill-rect val)))))

    (canvas/add-entity monet-canvas :water
                       (canvas/entity {:x 0 :y 0}
                                      nil
                                      (fn [ctx val]
                                        (doall
                                          (for [x (range 15)
                                                y (range 15)]
                                            (let [c (char-at overworld-spec (+ (* y 15) x))
                                                  img (get tile-images c)]
                                              ;(println (str "c=" c))
                                              (canvas/draw-image ctx img {:x (* x 32) :y (* y 32)})))) )))
    
    ))

(set! (.-onload js/window) (fn [] (start)))

(println "Hello world!")
