Feature: Upload resource

  Scenario: Upload mp3 resource and create song metadata
    Given user has a valid mp3 file
    When user uploads the mp3 file
    Then resource should be available
    And song metadata should be available