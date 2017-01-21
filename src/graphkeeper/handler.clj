(ns graphkeeper.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :as r]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.defaults :refer :all]
            [clojure.edn :as edn]
            [cheshire.core :as json]))

(defn read-edn [s]
  (edn/read-string s))

(defn print-edn [col]
  (prn-str col))

;;
;; Routes
;;

(defroutes app-routes
  (GET "/" [] "<h1>Hello World</h1>")
  (GET "/text" [request]
       (println "GET query: " request)
       (r/response (pr-str "just a text")))

  (route/resources "/" {:root ""}))

(defroutes rest-routes
  (GET "/api" [request]
       (println "GET query: " request)
       (r/response {:somekey "somevalue"})))

(defroutes query-routes
  (POST "/query" [request]
        (println "POST query: " (read-edn request))
        (r/response (str (read-edn request)))))

(defroutes not-found
  (route/not-found "<h1>Page not found</h1>"))

;;
;; Custom middleware
;;

(defn wrap-content-type [handler content-type]
  (fn [request]
    (let [response (handler request)]
      (assoc-in response [:headers "Content-Type"] content-type))))

(defn wrap-plaintext [handler]
  (wrap-content-type handler "text/html"))

(defn wrap-edn-content [handler]
  (wrap-content-type handler "application/edn"))

(defn wrap-common [handler]
  (-> handler
      (wrap-cors :access-control-allow-origin [#"http://localhost:8080" #"http://.*"]
                 :access-control-allow-methods [:get :put :post :delete])
      (wrap-defaults api-defaults)))

(defn wrap-app [handler]
  (-> handler
      (wrap-plaintext)
      (wrap-common)))

(defn wrap-rest [handler]
  (-> handler
      (wrap-json-response)
      (wrap-common)))

(defn wrap-edn [handler]
  (-> handler
      (wrap-edn-content)
      (wrap-common)))

;;
;; Application handler
;;

(def app
  (routes (-> rest-routes (wrap-routes wrap-rest))
          (-> query-routes (wrap-routes wrap-edn))
          (-> app-routes (wrap-routes wrap-app))
          (-> not-found (wrap-routes wrap-app))))
