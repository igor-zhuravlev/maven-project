# Testing strategy

The project follows a layered testing strategy based on the testing pyramid.
Most business logic is covered by fast and isolated unit tests.
Integration tests verify infrastructure behavior.
Component tests cover business scenarios inside one service.
Contract tests protect REST and messaging communication.
E2E tests check only the most important API flow.

## Unit tests

Unit tests are used to verify isolated business logic without starting the full Spring context.
They are applied to service classes in resource-service, song-service and resource-processor.
Dependencies are replaced with mocks, which makes these tests fast and focused.

Examples:
- Resource metadata processing service
- Resource service business logic
- Song service business logic

## Integration tests

Integration tests verify communication with real or emulated infrastructure.
They are applied to S3 integration and tested using Spring Boot, Testcontainers, and LocalStack.
This ensures that upload, download, and delete operations work with an S3 storage API.

## Component tests

Component tests verify a business scenario inside a single service.
The resource processor component test checks on:
- resource uploaded event is received
- the processor downloads the MP3 resource
- metadata is extracted
- sending the metadata to the song service
External services are mocked, so the test focuses on the behavior of the component.

## Contract tests

Contract tests are used for service boundaries.
Spring Cloud Contract is used to generate and consume stubs.
This allows consumers to test against provider contracts without running the real provider service.

## E2E tests

E2E tests verify the main user flow through the public API layer.
The E2E scenario uploads an MP3 file through the gateway:
- checks that the resource is available
- verifies that song metadata was created
This test validates that the main ms process works in the system.
