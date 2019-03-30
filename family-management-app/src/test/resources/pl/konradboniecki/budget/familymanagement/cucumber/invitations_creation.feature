@All
Feature: Invitation creation

  Scenario: Create invitation for user
    Given I'm authenticated with Basic Auth
    When I create invitation with following properties:
      | familyId      | email          | invitationCode   | created | registered |
      | {{familyId1}} | test1@mail.com | {{invitCodeId1}} | {{now}} | false      |
    Then invitation is created

  Scenario: Create multiple invitations for user
    Given I'm authenticated with Basic Auth
    When I create invitation with following properties:
      | familyId      | email          | invitationCode   | created | registered |
      | {{familyId1}} | test1@mail.com | {{invitCodeId1}} | {{now}} | false      |
      | {{familyId2}} | test2@mail.com | {{invitCodeId2}} | {{now}} | false      |
    Then invitation is created

  Scenario: Unauthorized user can't invitation family
    Given I'm not authenticated
    When I create invitation with following properties:
      | familyId      | email          | invitationCode   | created | registered |
      | {{familyId2}} | test1@mail.com | {{invitCodeId1}} | {{now}} | false      |
    Then the operation is unauthorized
