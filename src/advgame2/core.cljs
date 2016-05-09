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
                                        (-> ctx
                                            (canvas/draw-image tile-img val)))))
    
    ))

(set! (.-onload js/window) (fn [] (start)))

(enable-console-print!)

(println "Hello world!")
