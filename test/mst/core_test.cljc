(ns mst.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [mst.core :as mst]))

(deftest leading-zeros-matches-independent-reference
  (let [keys (concat ["app.bsky.feed.post/3jzfcijpj2z2a"
                      "app.bsky.feed.post/3jzfcijpj2z2b"
                      "app.bsky.actor.profile/self"
                      "app.bsky.graph.follow/abc"
                      "com.example.thing/0"]
                     (map #(str "app.bsky.feed.post/key" %) (range 256)))]
    (doseq [k keys]
      (is (= (mst/layer-reference k) (mst/leading-zeros-on-hash k))
          (str "layer mismatch for " k)))
    (testing "the corpus exercises layers above zero"
      (is (some #(pos? (mst/leading-zeros-on-hash %)) keys)))))

(deftest key-validity
  (is (mst/key-valid? "app.bsky.feed.post/3jzfcijpj2z2a"))
  (is (mst/key-valid? "app.bsky.actor.profile/self"))
  (is (not (mst/key-valid? "app.bsky.feed.post")) "needs collection/rkey")
  (is (not (mst/key-valid? "a//b")) "empty segment")
  (is (not (mst/key-valid? "a/b/c")) "three segments"))

(deftest common-prefix
  (is (= 0 (mst/common-prefix-len "abc" "xyz")))
  (is (= 4 (mst/common-prefix-len "app/1" "app/2")) "shared prefix is app/")
  (is (= 5 (mst/common-prefix-len "app/1" "app/1")))
  (is (= 2 (mst/common-prefix-len "ab" "abcdef"))))
