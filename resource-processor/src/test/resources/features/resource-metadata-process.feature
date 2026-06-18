Feature: Resource metadata process

Scenario: Process uploaded resource and create song metadata
  Given resource service contains uploaded mp3 resource with id <id>
  When resource uploaded event with id <id> is received
  Then resource processor should request resource with id <id> from resource service
  And song metadata should be created in song service

  Examples:
  | id |
  | 1  |
  | 5  |