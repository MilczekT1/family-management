@All
Feature: Invitation searching

  Invitation search.

  Background:
    Given I'm authenticated with Basic Auth
    And following invitations have been created:
      | familyId      | email          | invitationCode   | created | registered |
      | {{familyId1}} | test1@mail.com | {{invitCodeId1}} | {{now}} | false      |
      | {{familyId2}} | test2@mail.com | {{invitCodeId2}} | {{now}} | false      |
      | {{familyId3}} | test2@mail.com | {{invitCodeId3}} | {{now}} | false      |
      | {{familyId4}} | test3@mail.com | {{invitCodeId4}} | {{now}} | false      |
      | {{familyId4}} | test4@mail.com | {{invitCodeId5}} | {{now}} | false      |

  @Single
  Scenario: Searching for existing invitation by id
    Given I'm authenticated with Basic Auth
    When I get invitation for family with invitation code {{invitCodeId1}} by id
    Then invitation is found
    And invitation contains following properties:
      | familyId      | email          | invitationCode   |
      | {{familyId1}} | test1@mail.com | {{invitCodeId1}} |

  @Single
  Scenario: Searching for not existing invitation
    Given I'm authenticated with Basic Auth
    When I get invitation for family with invitation code {{random}} by id
    Then invitation is not found

  @Many @Email
  Scenario: Searching for existing invitations by email
    Given I'm authenticated with Basic Auth
    When I get invitations to family for user with email test2@mail.com
    Then 2 invitations are found
    And invitations contains following properties:
      | email          |
      | test2@mail.com |

  @Many @Email
  Scenario: Searching for not existing invitations by email
    Given I'm authenticated with Basic Auth
    When I get invitations to family for user with email missing@mail.com
    Then 0 invitations are found

  @Many @FamilyId
  Scenario: Searching for existing invitations by familyId
    Given I'm authenticated with Basic Auth
    When I get invitations to family with id {{familyId4}}
    Then 2 invitations are found
    And invitations contains following properties:
      | familyId      |
      | {{familyId4}} |

  @Many @FamilyId
  Scenario: Searching for not existing invitations by familyId
    Given I'm authenticated with Basic Auth
    When I get invitations to family with id {{random}}
    Then 0 invitations are found

  @Many @FamilyId @Email
  Scenario: Searching for existing invitations by familyId and Email
    Given I'm authenticated with Basic Auth
    When I get invitations by family id {{familyId3}} and email test2@mail.com
    Then 1 invitations are found
    And invitations contains following properties:
      | familyId      | email          | invitationCode   |
      | {{familyId3}} | test2@mail.com | {{invitCodeId3}} |

  @Many
  Scenario: Unauthorized user can't find invitation
    Given I'm not authenticated
    When I get invitations to family with id {{random}}
    Then the operation is unauthorized

  @Single
  Scenario: Unauthorized user can't find invitation
    Given I'm not authenticated
    When I get random invitation
    Then the operation is unauthorized
