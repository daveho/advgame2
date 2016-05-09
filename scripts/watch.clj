(require '[cljs.build.api :as b])

(b/watch "src"
  {:main 'advgame2.core
   :output-to "out/advgame2.js"
   :output-dir "out"})
