(ns advgame2.spcoll
  (:require [advgame2.pos :as pos]
            [advgame2.rect :as rect]))

;; Collection of spatially-indexed objects.
;; Each object is a map which is assumed to have:
;;    an :id key yielding a unique id
;;    a :pos key yielding a pos(ition) with :x and :y keys
;;
;; Eventually, we should use a proper spatial data structure,
;; but for now, we just use this:
;;   {:idmap <map of ids to objects>
;;    :posmap <map of pos to <map of id to objects>>}

;; Add an object to posmap
(defn- add-to-posmap [posmap obj]
  (let [id (:id obj)
        pos (:pos obj)
        bucket (get posmap pos {})]
    (assoc posmap pos (assoc bucket id obj))))

;; Replace given object in posmap
(defn- replace-in-posmap [posmap obj updated-obj]
  (let [id (:id obj)             ; id of object
        pos (:pos obj)           ; pos of object
        bucket (get posmap pos)] ; current bucket for object's pos
    ; replace the bucket for object's pos with one
    ; which associates the object's id with the updated object
    (assoc posmap pos (assoc bucket id updated-obj))))

(declare add)

(defn create [& objs]
  "Create collection of spatially-indexed objects initialized with (optional) arguments"
  (reduce add {:idmap {} :posmap {}} objs))

(defn add [spcoll obj]
  "Add a spatially-indexed object obj to the collection spcoll"
  {:idmap (assoc (:idmap spcoll) (:id obj) obj)
   :posmap (add-to-posmap (:posmap spcoll) obj)})

(defn find-by-id [spcoll id]
  "Find object with given id in spcoll, nil if no such object"
  (get (:idmap spcoll) id))

(defn find-by-pos [spcoll pos]
  "Find collection of objects with given pos, empty collection if no objects at specified pos"
  (get (:posmap spcoll) pos #{}))

(defn find-all-in-rect [spcoll rect]
  "Return sequence of all objects within given rect"
  (filter (fn [obj] (rect/contains-pos? rect (:pos obj))) (vals (:idmap spcoll))))

(defn update-obj [spcoll obj f]
  "Update obj by transforming it with function f"
  (let [updated-obj (f obj)]
    {:idmap (assoc (:idmap spcoll) (:id obj) updated-obj)
     :posmap (replace-in-posmap (:posmap spcoll) obj updated-obj)}))
