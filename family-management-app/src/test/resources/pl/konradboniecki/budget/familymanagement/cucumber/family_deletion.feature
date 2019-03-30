@All
Feature: Family deletion

  Family deletion.

  Scenario: Delete existing family
    Given I'm authenticated with Basic Auth
    And user user1 already owns a family
    When I delete a user1's family
    Then family is deleted

  Scenario: Delete not existing family
    Given I'm authenticated with Basic Auth
    When I delete not existing family
    Then family is not found

  Scenario: Unauthorized user can't delete family
    Given I'm not authenticated
    When I delete not existing family
    Then the operation is unauthorized
