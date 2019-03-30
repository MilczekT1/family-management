@All
Feature: Invitation deletion

  Invitation deletion.

  Scenario: Delete existing invitation
    Given I'm authenticated with Basic Auth
    And following invitations have been created:
      | familyId      | email          | invitationCode   | created | registered |
      | {{familyId1}} | test1@mail.com | {{invitCodeId1}} | {{now}} | false      |
    When I delete invitation with invitation code {{invitCodeId1}}
    Then invitation is deleted

  Scenario: Delete not existing invitation
    Given I'm authenticated with Basic Auth
    When I delete not existing invitation
    Then invitation is not found

  Scenario: Unauthorized user can't delete invitation
    Given I'm not authenticated
    When I delete invitation with invitation code {{random}}
    Then the operation is unauthorized
