@All
Feature: Family update

  Background:
    Given I'm authenticated with Basic Auth
    And user familyOwner already owns a family

  Scenario: Update existing family by assigning budget
    Given I'm authenticated with Basic Auth
    When I update familyOwner's family with properties:
      | ownerId         | budgetId                             | title         |
      | {{familyOwner}} | 2eb31cdb-2375-4f51-b109-4895c63abe52 | default title |
    Then family is updated
    When I get familyOwner's family
    And response contains family with following properties:
      | ownerId         | budgetId                             | title         |
      | {{familyOwner}} | 2eb31cdb-2375-4f51-b109-4895c63abe52 | default title |

  Scenario: Update family with inconsistent family id in path and body
    Given I'm authenticated with Basic Auth
    When I update familyOwner's family with properties:
      | id         | ownerId         | budgetId                             |
      | {{random}} | {{familyOwner}} | 2eb31cdb-2375-4f51-b109-4895c63abe52 |
    Then the operation is a failure

  Scenario: Update not existing family
    Given I'm authenticated with Basic Auth
    When I update not_existing_user's family with properties:
      | ownerId               | budgetId                             | title         |
      | {{not_existing_user}} | 2eb31cdb-2375-4f51-b109-4895c63abe52 | default title |
    Then family is not found

  Scenario: Unauthorized user can't update family
    Given I'm not authenticated
    When I update familyOwner's family with properties:
      | ownerId         | budgetId                             |
      | {{familyOwner}} | 00000000-0000-0000-0000-000000000000 |
    Then the operation is unauthorized
