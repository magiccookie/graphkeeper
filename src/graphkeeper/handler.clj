(ns graphkeeper.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :as r]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.middleware.edn :refer [wrap-edn-params]]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.defaults :refer :all]
            [graphkeeper.edn :as edn]
            [cheshire.core :as json]))

;;
;; Routes
;;

(defroutes app-routes
  (GET "/" [] (r/resource-response "index.html" {:root "public"}))
  (route/resources "/" {:root "public"})
  (route/not-found "Page not found"))

(defroutes rest-routes
  (GET "/api" []
       (r/response {:somekey "somevalue"}))

  (POST "/api" request
        (println "POST query: " request)
        (r/response (:body request))))

(defn edn-query-handler [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/edn"}
   :body (pr-str data)})

(defroutes edn-route
  (GET "/edn" []
       (edn-query-handler {:hello :cleveland}))

  (PUT "/edn" [name]
       (edn-query-handler {:hello name})))


(defroutes query-routes
  (POST "/query" request
        (println "POST query: " (edn/read-edn (:body request)))
        (try
          (r/response (edn/read-edn (:body request)))
          (catch Exception e
            (prn (.getMessage e))))))

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
      (wrap-json-body)
      (wrap-common)))

(defn wrap-edn-custom [handler]
  (-> handler
      (wrap-plaintext)
      (wrap-common)))

(defn wrap-edn [handler]
  (-> handler
      (wrap-edn-params)
      (wrap-common)
      ))

;;
;; Application handler
;;

(def app
  (routes (-> rest-routes (wrap-routes wrap-rest))
          (-> query-routes (wrap-routes wrap-edn-custom))
          (-> edn-query-handler (wrap-routes wrap-edn))
          (-> app-routes (wrap-routes wrap-app))))
