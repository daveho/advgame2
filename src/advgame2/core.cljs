(ns advgame2.core
  (:require [clojure.browser.repl :as repl]
            [monet.canvas :as canvas]))

(defonce conn
  (repl/connect "http://localhost:9000/repl"))

(def canvas-dom (.getElementById js/document "canvas"))

(def monet-canvas (canvas/init canvas-dom "2d"))

(canvas/add-entity monet-canvas :background
                   (canvas/entity {:x 0 :y 0 :w 600 :h 600} ; val
                                  nil                       ; update function
                                  (fn [ctx val]             ; draw function
                                    (-> ctx
                                        (canvas/fill-style "#191d21")
                                        (canvas/fill-rect val)))))

(enable-console-print!)

(println "Hello world!")
