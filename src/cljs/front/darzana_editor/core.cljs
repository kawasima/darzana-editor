(ns darzana-editor.core
  (:require [reagent.core :as r]
            [goog.dom :as gdom])
  (:require-macros [darzana-editor.blocks :as b]))

(enable-console-print!)
(defonce blockly (js/require "node-blockly/browser"))
(defonce remote  (.-remote (js/require "electron")))
(defonce Dialog  (.-dialog remote))

(b/defblock route blockly.Blocks
  :message0 "route path %1 method %2"
  :args0 [{:type :field_input :name "path" :text ""}
          {:type :field_dropdown :name "method"
           :options [["GET" "GET"]
                     ["POST" "POST"]
                     ["PUT" "PUT"]
                     ["DELETE" "DELETE"]
                     ["PATCH" "PATCH"]]}]
  :message1 "%1"
  :args1 [{:type :input_statement :name "commands"}]
  :colour 120)

(b/defblock call-api blockly.Blocks
  :message0 "call API %1"
  :args0 [{:type "input_value" :name "VALUE"}]
  :nextStatement nil
  :previousStatement nil
  :colour 15)

(b/defblock api blockly.Blocks
  :message0 "id %1 path %2 method %3"
  :args0 [{:type :field_input :name "id" :text ""}
          {:type :field_input :name "path" :text ""}
          {:type :field_dropdown :name "method"
           :options [["GET" "GET"]
                     ["POST" "POST"]
                     ["PUT" "PUT"]
                     ["DELETE" "DELETE"]
                     ["PATCH" "PATCH"]]}]
  :output "Array"
  :colour 30)

(b/defblock if-success blockly.Blocks
  :message0 "if success"
  :message1 "success %1"
  :args1 [{:type :input_statement :name "success"}]
  :message2 "failure %1"
  :args2 [{:type :input_statement :name "failure"}]
  :colour 230
  :nextStatement nil
  :previousStatement nil)

(b/defblock render blockly.Blocks
  :message0 "render %1"
  :args0 [{:type "input_value" :name "VALUE"}]
  :inputsInline true
  :previousStatement nil
  :colour 60)

(b/defblock template blockly.Blocks
  :message0 "template %1"
  :args0 [{:type :field_input :name "VALUE" :text ""}]
  :output true
  :colour 60)

(b/defblock read-value blockly.Blocks
  :message0 "assign %1 to %2"
  :args0 [{:type :input_value :name "from"}
          {:type :input_value :name "to"}]
  :inputsInline true
  :previousStatement nil
  :nextStatement nil
  :colour 60)

(b/defblock bean-class blockly.Blocks
  :message0 "bean %1"
  :args0 [{:type :field_input :name "VALUE" :text ""}]
  :output true
  :colour 60)

(b/defblock scope-var blockly.Blocks
  :message0 "var %1 in scope %2"
  :args0 [{:type :field_input :name "var" :text ""}
          {:type :field_dropdown :name "scope"
           :options [["params" "params"]
                     ["session" "session"]]}]
  :output true
  :colour 60)

(defn open-dialog [e]
  (.showOpenDialog Dialog
                   nil
                   (clj->js {:properties ["openFile"]
                             :title "Open"})))

(defn save-dialog [workspace]
  (.showSaveDialog Dialog
                   nil
                   (clj->js {:title "Save"
                             :filters [{:name "clj" :extensions [".clj"]}]})
                   (fn [f]
                     (println (blockly.Xml.workspaceToDom workspace)))))
;; BFF Action component
(defn app []
  (let [workspace (r/atom nil)
        xml (blockly.Xml.textToDom "<xml xmlns=\"http://www.w3.org/1999/xhtml\"></xml>")]
    (r/create-class
     {:component-did-mount
      (fn []
        (let [ws (blockly.inject "blockly"
                                 #js {:toolbox (gdom/getElement "toolbox")})]
          (reset! workspace ws)
          (blockly.Xml.domToWorkspace xml ws)))
      :reagent-render
      (fn []
        [:div.window
         [:header.toolbar.toolbar-header
          [:h1.title "Darzana Editor"]
          [:div.toolbar-actions
           [:div.btn-group
            [:button.btn.btn-default
             {:type "button" :on-click open-dialog}
             [:span.icon.icon-folder]]
            [:button.btn.btn-default
             {:type "button" :on-click #(save-dialog @workspace)}
             [:span.icon.icon-floppy]]]]]
         [:div.window-content
          [:div#blockly {:style {:width "640px" :height "480px"}}]
          [:xml#toolbox {:style {:display "none"}}
           [:category {:name "Control"}
            [:block {:type "route"}]
            [:block {:type "if-success"}]]
           [:category {:name "API"}
            [:block {:type "call-api"}]
            [:block {:type "lists_create_with"}]
            [:block {:type "api"}]]
           [:category {:name "Mapper"}
            [:block {:type "read-value"}]
            [:block {:type "bean-class"}]
            [:block {:type "scope-var"}]]
           [:category {:name "Output"}
            [:block {:type "render"}]
            [:block {:type "template"}]]]]
         [:footer.toolbar.toolbar-footer
          [:button.btn.btn-primary "save"]
          ]])})))

(r/render [app]
  (gdom/getElement "app"))
