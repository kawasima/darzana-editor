(ns darzana-editor.blocks)

(defmacro defblock [block-name blocks & {:as block-defs}]
  `(aset ~blocks ~(name block-name)
         (cljs.core/clj->js
          {:init (fn []
                   (cljs.core/this-as this#
                     (.jsonInit this# (cljs.core/clj->js ~block-defs))))})))
