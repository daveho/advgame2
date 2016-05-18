(ns advgame2.core-test
  (:require
   [cljs.test :refer-macros [deftest testing is]]
   ; Add imports for our namespaces here...
   [advgame2.pos :as pos]
   [advgame2.rect :as rect]
   [advgame2.grid :as grid]
   [advgame2.spcoll :as spcoll]
   ))

;; (deftest test-numbers
;;   (testing "will this work?"
;;     (is (= 1 3))))

(deftest pos-test
  (testing "pos data type"
    (is (= 3 (pos/get-x (pos/create 3 4))))
    (is (= 4 (pos/get-y (pos/create 3 4))))
    ))

(def rect1 (rect/create (pos/create 2 3) 5 6))

(deftest rect-test
  (testing "rect data type"
    (is (= 2 (pos/get-x (rect/get-upleft rect1))))
    (is (= 3 (pos/get-y (rect/get-upleft rect1))))
    (is (= 5 (rect/get-w rect1)))
    (is (= 6 (rect/get-h rect1)))
    (is (not (rect/contains-pos? rect1 (pos/create 1 3))))
    (is (not (rect/contains-pos? rect1 (pos/create 2 2))))
    (is (not (rect/contains-pos? rect1 (pos/create 7 8))))
    (is (not (rect/contains-pos? rect1 (pos/create 2 9))))
    (is (rect/contains-pos? rect1 (pos/create 6 3)))
    (is (rect/contains-pos? rect1 (pos/create 4 8)))
    ))

;; This grid is  [["A" "B"]
;;                ["C" "D"]]
(def grid1 (grid/create 2 2 (fn [p] (char (+ 65 (pos/get-x p) (* (pos/get-y p) 2))))))

(deftest grid-test
  (testing "grid data type"
    (is (= "A" (grid/get-val grid1 (pos/create 0 0))))
    (is (= "B" (grid/get-val grid1 (pos/create 1 0))))
    (is (= "C" (grid/get-val grid1 (pos/create 0 1))))
    (is (= "D" (grid/get-val grid1 (pos/create 1 1))))
    (is (not (grid/in-bounds? grid1 (pos/create -1 0))))
    (is (not (grid/in-bounds? grid1 (pos/create 2 0))))
    (is (not (grid/in-bounds? grid1 (pos/create 0 -1))))
    (is (not (grid/in-bounds? grid1 (pos/create 0 2))))
    (is (grid/in-bounds? grid1 (pos/create 0 0)))
    (is (grid/in-bounds? grid1 (pos/create 0 1)))
    (is (grid/in-bounds? grid1 (pos/create 1 0)))
    (is (grid/in-bounds? grid1 (pos/create 1 1)))
    ))

; Some spatial objects
(def obj1 {:id 'a :pos (pos/create 2 3) :val 42})
(def obj2 {:id 'b :pos (pos/create 11 27) :val 101})
(def obj3 {:id 'c :pos (pos/create -4 -10) :val 33})

; empty spcoll
(def spcoll-empty (spcoll/create))

; spcoll with just obj1 and obj2
(def spcoll12 (spcoll/create obj1 obj2))

; add obj3 to spcoll12
(def spcoll123 (spcoll/add spcoll12 obj3))

; contains? isn't defined for sequences
(defn seq-contains? [seq val]
  (not (empty? (filter #(= % val) seq))))

(def boundrect1 (rect/create (pos/create 0 0) 12 28))
(def boundrect2 (rect/create (pos/create 5 26) 9 4))

(deftest spcoll-test
  (testing "spcoll data type"
    (is (= obj1 (spcoll/find-by-id spcoll12 'a)))
    (is (= obj2 (spcoll/find-by-id spcoll12 'b)))

    (is (seq-contains? (spcoll/find-by-pos spcoll12 (pos/create 2 3)) obj1))
    (is (seq-contains? (spcoll/find-by-pos spcoll12 (pos/create 11 27)) obj2))

    (is (not (spcoll/find-by-pos spcoll12 (pos/create 2 4))))

    (is (= obj1 (spcoll/find-by-id spcoll123 'a)))
    (is (= obj2 (spcoll/find-by-id spcoll123 'b)))
    (is (= obj3 (spcoll/find-by-id spcoll123 'c)))

    (is (seq-contains? (spcoll/find-by-pos spcoll123 (pos/create 2 3)) obj1))
    (is (seq-contains? (spcoll/find-by-pos spcoll123 (pos/create 11 27)) obj2))
    (is (seq-contains? (spcoll/find-by-pos spcoll123 (pos/create -4 -10)) obj3))

    (is (seq-contains? (spcoll/find-all-in-rect spcoll123 boundrect1) obj1))
    (is (seq-contains? (spcoll/find-all-in-rect spcoll123 boundrect1) obj2))
    (is (not (seq-contains? (spcoll/find-all-in-rect spcoll123 boundrect1) obj3)))

    (is (not (seq-contains? (spcoll/find-all-in-rect spcoll123 boundrect2) obj1)))
    (is (seq-contains? (spcoll/find-all-in-rect spcoll123 boundrect2) obj2))
    (is (not (seq-contains? (spcoll/find-all-in-rect spcoll123 boundrect2) obj3)))

    ))
