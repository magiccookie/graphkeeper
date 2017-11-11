(ns graphkeeper.edn-test
  (:require  [clojure.test :refer :all]
             [graphkeeper.edn :refer :all]))

(deftest get-node-by-id
  (testing "return node by id"
    (is (=
         (str
          "START n=node(42) "
          "RETURN n;")
         (to-cypher '(:node/id 42))))))

(deftest get-node-attr-by-id
  (testing "return node attribute by id"
    (is (=
         (str
         "START n=node(42) "
         "RETURN n.firstName;")
         (to-cypher '(:node/id 42 :node/firstName))))))

(deftest get-node-by-attr
  (testing "get nodes by attribute"
      (is (=
            (str
             "START n=node(*) "
             "WHERE n.firstName = 'John' "
             "RETURN n;")
            (to-cypher '(:node/firstName "John"))))))

(deftest get-node-list-by-ids
  (testing "get list of nodes with IDs"
    (is (=
         (str
          "START n= node(1,2,3,4,5,6,7) "
          "RETURN n;")
         (to-cypher '(node/id [1 2 3 4 5 6 7]))))))

(deftest select-all-nodes
  (testing "return all nodes"
    (is (= "MATCH n RETURN n;"
           (to-cypher '([*]))))))

(deftest select-nodes-by-attr
  (testing "return nodes with particular attribute field"
    (is (=
         (str
         "START n=node(*) " ;; (non-indexed field)
         "WHERE n.name = 'Joe' "
         "RETURN n;")
         (to-cypher '(:node/name "Joe"))))))

(deftest match-complex-query
  (testing "generate complex query"
    (is (=
         (str
          "START root=node(0) "
          "MATCH root-[:HOSTS]->(agency)<-[:USER_BELONGS_TO]-(user)-[:USER_LINKED_TO_PROGRAM]->(program)<-[:HAS_PROGRAM]-(centre), "
          "(program)<-[:HAS_SUGGESTED_PROGRAM]-(referralDecisionsSection)<-[:REFERRAL_HAS_DECISIONS_SECTION]-(referral)-[:CREATED_BY]->(createdByUser), "
          "(referral)-[:REFERRAL_HAS_WHO_SECTION]->(whoSection)-[:HAS_PARTICIPANT]->(participant) "
          "WHERE (agency.Key? = romikoagency) AND (user.Username? = romiko.derbynew) AND (referral.Completed? = false) "
          "RETURN createdByUser.FamilyName? AS UserFamilyName, createdByUser.GivenName? AS UserGivenName, "
          "program.Name? AS Program, centre.Name? AS Centre, referral.UniqueId? AS ReferralId, whoSection.ReferralDate? AS ReferralDate, "
          "participant.Name? AS ParticipantName, participant.DisplayOrder? AS ParticipantDisplayOrder")
          (to-cypher nil))))) ;; edn pattern goes here
