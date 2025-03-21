# Design Decisions and Class Descriptions

This document provides detailed information about the design decisions and class descriptions for the queue-manager application.

## Design Decisions

### Layered Architecture

The application follows a layered architecture to separate concerns and improve maintainability.

* **Data Layer:** (Database)
    * The database layer consists of a simple `Message` JPA entity.
    * The `Message` entity is designed with minimal fields:
        * `id`: A UUID identifier for the message.
        * `text`: A simple description of the message.
        * `dataSize`: A mock representation of the amount of data associated with the message.
            * Used by the placeholder processing logic as a delay in milliseconds.
        * `timestamp`: An `Instant` timestamp to record when the message was created.
            * Used for sorting messages in the queue, ensuring FIFO (First-In, First-Out) processing.
    * `Message` entities are accessed and manipulated using the `MessageRepository` (which just implements a `JpaRepository`).
* **Service Layer:** (Business Logic)
    * The service layer (`MessageService`) encapsulates the core business logic of the "queue".
    * It interacts directly with the domain/entity objects via the `JpaRepository`.
    * It accepts and returns Data Transfer Objects (DTOs) represented as Java `record`s, ensuring clear data transfer boundaries.
    * Conversion between Entity objects and DTO objects is handled by the Spring `ConversionService`, utilizing the registered `MessageToMessageRecordConverter` for seamless data transformation.
    * **Processing Sub-Layer:**
        * This sub-layer, comprising `QueueManager` and `QueueMessageProcessor`, handles asynchronous background message processing.
        * `QueueManager` orchestrates the processing loop, delegating message operations to the `MessageService` for queue interaction.
        * `QueueMessageProcessor` represents a placeholder component simulating message processing, designed to operate on individual messages sequentially.
* **Representation Layer:** (REST API)
    * The controller layer acts as the application's entry point for REST API requests, implemented using Spring `@RestController`s.
    * It employs Java `record` classes as DTOs for request and response payloads, promoting immutability and data clarity.
    * It delegates all business logic execution to the service layer, maintaining a separation of concerns.
    * The `/v1` API directly returns DTO objects from the service layer as responses, adhering to the exercise's basic requirements.
    * The `/v2` API enhances responses with Spring HATEOAS `RepresentationModel`s (specifically `EntityModel<MessageRecord>`), providing hypermedia links for API discoverability.
        * The `MessageModelAssembler` is used to transform DTOs into `EntityModel` instances, enriching responses with relevant links and facilitating API navigation.

### Background Processing

To ensure REST API requests are non-blocking and message processing is asynchronous, a dedicated background thread is utilized. This thread, managed by the `QueueManager` bean, is initiated via a `@PostConstruct` hook and gracefully terminated using a `@PreDestroy` hook. The processing loop employs a simple `while` loop, controlled by a `running` flag, to continuously dequeue and process messages. When the queue is empty, the thread enters a wait state using a basic `Object.wait()` mechanism, resuming upon notification of a `MessageEnqueuedEvent`. The `MessageEnqueuedEvent` is handled by a `TransactionalEventListener` to ensure message processing is triggered only after the message is successfully committed to the database.

### Error Handling

Custom application-specific exceptions are used to represent error conditions, avoiding the use of `null` values. A `GlobalControllerExceptionHandler` (annotated with `@ControllerAdvice`) provides consistent and appropriate error response codes to clients when these exceptions are thrown.

## Core Components

* **`Message`:**
    * Represents a message within the queue.
    * JPA `@Entity` with:
        * `@Id`: A `UUID` generated using the `uuid` strategy.
        * `timestamp`: An `Instant` representing the message's enqueued time.
        * `text`: A `String` containing arbitrary message text.
        * `dataSize`: A mock representation of message data size, used as a processing delay (milliseconds).
* **`MessageRepository`:**
    * Extends `JpaRepository` for basic JPA operations.
    * Provides `getFirstByOrderByTimestampAsc()` to retrieve the oldest message in the queue.
* **`EnqueueMessageRecord`:**
    * Immutable `record` representing data required to create a `Message` upon enqueueing.
* **`MessageRecord`:**
    * Immutable `record` representing a `Message` for data transfer.
* **`QueueRecord`:**
    * Immutable `record` representing queue status information.
* **`MessageService`:**
    * Manages core message queue operations (enqueue, dequeue, get by ID, get size).
    * Publishes `MessageEnqueuedEvent` after successful message enqueueing.
    * Ensures data consistency through transactional methods.
* **`MessageEnqueuedEvent`:**
    * Event published upon successful message enqueueing.
    * Triggers message processing in `QueueManager`.
* **`QueueManager`:**
    * Manages the background message processing thread.
    * Uses `Object.wait()`/`Object.notify()` for thread synchronization during empty queue scenarios.
    * Reacts to `MessageEnqueuedEvent` to resume processing.
* **`QueueMessageProcessor`:**
    * Simulates message processing with a delay based on `message.dataSize()`.
    * Handles `InterruptedException` during processing.
* **`V1QueueManagerController`:**
    * Provides the API version matching the original exercise requirements (non-HATEOAS).
    * Returns raw DTO objects.
* **`QueueController`:**
    * Provides RESTful endpoint for queue status.
    * Returns `EntityModel<QueueRecord>` with:
        * `size` property.
        * Links to `enqueue` and `dequeue` operations (handled by `MessageController`).
* **`MessageController`:**
    * Provides RESTful message operations under the `/v2/queue` endpoint.
    * Uses `MessageModelAssembler` for HATEOAS responses.
    * Returns `EntityModel<MessageRecord>` objects.
* **`MessageModelAssembler`:**
    * Converts `MessageRecord` DTOs to `EntityModel` with HATEOAS links:
        * Self-link based on `message.id()`.
        * Link to the parent queue resource.
* **`GlobalControllerExceptionHandler`:**
    * Provides centralized exception handling for consistent API responses.
    * Handles `NoSuchMessageException` and `MessageEnqueueException`.
* **`QueueManagerApplication`:**
    * The main Spring Boot application class.
    * Annotated with `@SpringBootApplication` to enable Spring Boot auto-configuration and component scanning.
    * Annotated with `@EnableHypermediaSupport` to enable Spring HATEOAS support.

## Future Enhancements

* **Enhanced Testing:**
    * Expand unit and integration test coverage, particularly focusing on verifying HTTP error code handling.
* **Robust Message Processing:**
    * Implement more resilient error handling and retry mechanisms for message processing.
    * Explore strategies for handling processing failures, such as:
        * Maintaining messages in the database during processing to allow for retries.
        * Implementing a "requeue" mechanism upon processing failure, acknowledging the potential impact on FIFO ordering.
* **Expanded API Functionality:**
    * Introduce paginated collection endpoints to facilitate browsing the message queue.
    * Enable the ability to delete individual messages, bypassing FIFO processing.
    * Add a "dequeue" link to messages that are at the front of the queue, providing direct access to dequeue functionality.