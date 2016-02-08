(ns chatapp.db
  (:require [schema.core :as s :include-macros true]))

(def message-id s/Int)

(def message
  {:style   s/Str
   :message s/Str
   :key     message-id})

(def schema
  {:messages      [message]
   :message-input {:text s/Str :focus s/Bool}})
