@All
Feature: Family searching

  Family search.

  Scenario: Searching for existing family
    Given I'm authenticated with Basic Auth
    And user user1 already owns a family
    When I get user1's family
    Then family is found

  @Owner
  Scenario: Searching for existing family by owner
    Given I'm authenticated with Basic Auth
    And user user1 already owns a family
    When I get user1's family by owner
    Then family is found

  Scenario: Searching for not existing family
    Given I'm authenticated with Basic Auth
    When I get random family
    Then family is not found

  @Owner
  Scenario: Searching for not existing family
    Given I'm authenticated with Basic Auth
    And user new_joiner doesn't own a family
    When I get new_joiner's family by owner
    Then family is not found

  Scenario: Unauthorized user can't find family
    Given I'm not authenticated
    When I get random family
    Then the operation is unauthorized

  @Owner
  Scenario: Unauthorized user can't find family
    Given I'm not authenticated
    When I get random family by owner
    Then the operation is unauthorized
