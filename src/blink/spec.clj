(ns blink.spec
  (:require
   [clojure.spec.alpha :as s]))


(def tags #{:stop :default})


(s/def ::tag tags)

(s/def ::not-tag (complement tags))

(s/def ::doc string?)

(s/def ::doc-re (s/? ::doc))

(s/def ::code
  (s/coll-of ::not-tag))

(s/def ::code-re
  (s/* ::not-tag))

(s/def ::extra-re-item
  (s/cat
    :tag ::tag
    :code ::code-re))

(s/def ::extra-repp
  (s/* ::extra-re-item))

(s/def ::parse
  (s/cat
   :doc ::doc-re
   :start ::code-re
   :extra ::extra-re))


(s/def ::start
  (s/and
   ::code
   not-empty))

(s/def ::extra-item map?)

(s/def ::extra
  (s/coll-of ::extra-item))

(s/def ::validate
  (s/keys :req-un [::start]
          :opt-un [::doc
                   ::extra]))


(s/def ::blink
  (s/and ::parse ::validate))
