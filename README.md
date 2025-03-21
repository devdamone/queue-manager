# Bureau Veritas - Senior Java Developer - Exercise

## Exercise

Develop a small Java application using Spring Boot that implements a basic process queue manager with the following features:

1. REST APIs for interacting with the queue:
   - Add message to the queue (`POST /enqueue`).
   - Get and remove a message from the queue (`GET /dequeue`).
   - Check current queue size (`GET /queue-size`).
2. Message Processing: 
   - Simulate message processing with a placeholder logic (e.g., a delay to mimic work).
3. Persistence (Optional):
   - Store queued messages in a lightweight persistence store of your choice.

## Prerequisites

* Java 21 or later
* Maven

## Building and Running the Application

1.  **Navigate to the project directory:**

    ```bash
    cd <project_directory>
    ```

2.  **Build the application:**

    ```bash
    ./mvnw clean install
    ```

3.  **Run the application:**

    ```bash
    ./mvnw spring-boot:run
    ```

The application will start on `http://localhost:8080`.

## Database Configuration

The application uses an embedded H2 database for persistence. The database is saved as a file in the `target/data` folder. No external database configuration is required.

## REST API Endpoints

This application provides two versions of the REST API:

### Version 1 (`/v1`) - Exercise Requirements

This version implements the API endpoints as specified in the exercise requirements.

* **Enqueue Message:** `POST /v1/enqueue`
    * Request body: `{"text": "message text", "dataSize": 1000}` (dataSize in milliseconds)
    * Response: `201 Created` with the enqueued message details.
* **Dequeue Message:** `GET /v1/dequeue`
    * Response: `200 OK` with the dequeued message details.
* **Get Queue Size:** `GET /v1/queue-size`
    * Response: `200 OK` with the queue size.

### Version 2 (`/v2`) - Enhanced RESTful API

This version provides a more RESTful implementation, including HATEOAS links and a more structured API design.

* **Get Queue Info:** `GET /v2/queue`
    * Response: `200 OK` with queue size and related links.
* **Enqueue Message:** `POST /v2/queue/messages`
    * Request body: `{"text": "message text", "dataSize": 1000}` (dataSize in milliseconds)
    * Response: `201 Created` with the enqueued message details.
* **Dequeue Message:** `DELETE /v2/queue/messages/first`
    * Response: `200 OK` with the dequeued message details.
* **Get Message by ID:** `GET /v2/queue/messages/{id}`
    * Response: `200 OK` with the message details, or `404 Not Found` if the message does not exist or has already been dequeued.

## Message Processing

The application includes a `QueueManager` component that runs a dedicated thread to process messages from the queue (database). The process works as follows:

1.  The processing thread continuously checks the queue for messages.
2.  When a message is found, it is dequeued and "processed."
    - Processing is simulated by pausing the thread for a duration specified by the message's `dataSize` field (in milliseconds).
3.  Subsequent messages are processed immediately after the previous one finishes.
4.  If the queue is empty, the processing thread waits until a new message is enqueued.
    - Processing is notified by a `MessageEnqueuedEvent` sent through the Spring `ApplicationEventPublisher` to the `@TransactionalEventListener` in the `QueueManager`
> **Note:** Messages dequeued directly through the REST API endpoints are not processed by the `QueueManager` thread.

It's worth noting that this implementation was the best interpretation of the requirements. An alternative approach could involve a separate service or application that interacts with the REST API to process messages, providing a more decoupled architecture. However, given the constraints, this implementation was chosen to maintain a single application. This approach renders the REST API `dequeue` endpoint, and to some extent, the `queue-size` endpoint, less useful in the context of the automated processing.

## Design Decisions and Class Descriptions

For more detailed information about design decisions and class descriptions, please refer to the `DESIGN.md` file.
