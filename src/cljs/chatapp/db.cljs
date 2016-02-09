(ns chatapp.db
  (:require [schema.core :as s :include-macros true]))

(def event-id s/Int)

(def event
  {:type    (s/enum "debug" "message")
   :message s/Str
   :key     event-id})

(def schema
  {:events        [event]
   :message-input {:text s/Str :focus s/Bool}})
