(ns advgame2.core-test
  (:require
   [cljs.test :refer-macros [deftest testing is]]
   ; Add imports for our namespaces here...
   [advgame2.pos :as pos]
   ;[advgame2.rect :as rect]
   [advgame2.grid :as grid]
   ))

;; (deftest test-numbers
;;   (testing "will this work?"
;;     (is (= 1 3))))

(deftest pos-test
  (testing "pos data type"

    (is (= 3 (pos/get-x (pos/create 3 4))))
    (is (= 4 (pos/get-y (pos/create 3 4))))
    ))

;; (def rect1 (rect/create (pos/create 2 3) 5 6))

;; (deftest rect-test
;;   (testing "rect data type"
;;     (is (= 2 (pos/get-x (rect/get-upleft rect1))))
;;     (is (= 3 (pos/get-y (rect/get-upleft rect1))))
;;     ))

;; This grid is  [["A" "B"]
;;                ["C" "D"]]
(def grid1 (grid/create 2 2 (fn [p] (char (+ 65 (pos/get-x p) (* (pos/get-y p) 2))))))

(deftest grid-test
  (testing "grid data type"
    (is (= "A" (grid/get-val grid1 (pos/create 0 0))))
    (is (= "B" (grid/get-val grid1 (pos/create 1 0))))
    (is (= "C" (grid/get-val grid1 (pos/create 0 1))))
    (is (= "D" (grid/get-val grid1 (pos/create 1 1))))
    ))
