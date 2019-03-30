@All
Feature: Family creation

  User can be assigned only to 1 family. As a creator, user becomes an owner.

  Scenario: Create family for user which does not have family
    Given I'm authenticated with Basic Auth
    And user user1 doesn't own a family
    When I create a family with properties:
      | ownerId   | title |
      | {{user1}} | title |
    Then family is created

  Scenario: Create family for user who already has a family
    Given I'm authenticated with Basic Auth
    And user user1 already owns a family
    When I create a family with properties:
      | ownerId   | title |
      | {{user1}} | title |
    Then the operation is a failure

  Scenario: Unauthorized user can't create family
    Given I'm not authenticated
    When I create a family with properties:
      | ownerId   | title |
      | {{user1}} | title |
    Then the operation is unauthorized
