(ns chatapp.test-runner
    (:require
     [doo.runner :refer-macros [doo-tests]]
     [chatapp.core-test]))

(enable-console-print!)

(doo-tests 'chatapp.core-test)
