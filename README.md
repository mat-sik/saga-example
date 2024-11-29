# **Saga Orchestrator Example**

This project demonstrates how to manage distributed transactions using the **Saga Pattern**, focusing on the
orchestrator-based approach. The implementation utilizes **RabbitMQ** and **Spring Boot** to achieve an event-driven
architecture with clear state management through tasks and events.

---

## **Overview**

The Saga pattern is employed to manage distributed transactions across multiple microservices. This example implements
the **Orchestrator Saga** approach, where a central orchestrator coordinates transactions between services. It showcases
both **happy** and **unhappy** paths to demonstrate how compensating transactions are handled.

### **Design Details**

- The application follows **Event-Driven Architecture** principles, using **tasks** and **events** for communication:
    - **Tasks**: Represent actions that request state changes in a service.
    - **Events**: Represent results of actions, enabling other services to react to state changes.
- **Compensating Transactions**: Used to revert state changes in case of transaction failures.
- **Message Replay**: This parallel approach requires that client messages can be replayed if the orchestrator dies
  between publishing tasks. Clients must place their messages on a queue to ensure reliability.
  See [Workflow 3.](#workflow)

---

## **Microservices**

This example includes two services:

1. **User-Service**:
    - Manages general information about the user.
2. **Auth-Service**:
    - Manages user credentials such as `username`, `email`, and `password`.

---

## **Workflow**

1. A client places a message on a queue, ensuring replay capability.
2. The **Orchestrator** picks up the client message and publishes parallel tasks to the `Auth-Service` and
   `User-Service`.
3. If a failure occurs in `Auth-Service` (e.g., `username` or `email` already exists), the orchestrator triggers
   compensating tasks in `User-Service` to roll back changes.

---

## **Happy Path vs. Unhappy Path**

The workflow has two possible outcomes:

1. **Happy Path**:
    - Both `Auth-Service` and `User-Service` successfully process their respective tasks.
    - The transaction completes without any need for compensation.

2. **Unhappy Path**:
    - `Auth-Service` fails to process the task (e.g., due to duplicate username/email).
    - The orchestrator compensates by rolling back changes in `User-Service`.

---

## **Key Concepts**

### **Topology**

- **Exchanges**
    - One global **Exchange** for different tasks called `task.direct.exchange`
    - One global **Exchange** for different events called `event.direct.exchange`
- **Queues**
    - I follow the convention of having a single **Queue** for each **Message type**, whether it's an **Event** or a
      **Task**.

### **Tasks and Events**

Message types I distinguish.

- **Tasks**:
    - Represent actions that modify a service's state.
    - Example: `CreateUserTask`, `CreateAuthTask`.
- **Events**:
    - Represent results of tasks that notify other services.
    - Example: `UserCreatedEvent`, `AuthCreationFailedEvent`.

### **Naming Convention**

- **Partition Key**: Defines task and event routing keys.
    - Format: `task.[action].[entity-name]` / `event.[entity-name].[action].[result]`.
    - Example: `task.create.user` or `event.user.create.failed`.
- **Exchange**: Determines the message exchange type.
    - Format: `[task/event].[type].exchange`.
    - Example: `task.direct.exchange`.
- **Queue**: Defines the queue name for consumers. Queue name represents queue usage.
    - Format: `queue.[entity-name].[action]`.
    - Note`action` can be multi word like `creation-compensation`.
    - Example:
        - `queue.user.creation`
        - `queue.user.creation-compensation`

### **Purpose of Naming**

- Task names show what action is requested to change a service's state.
- Event names communicate state changes to other services.
- Entity names determine which services should handle tasks or events.

### **Event notification pattern**

- The **event.direct.exchange** is an example of the **event notification pattern**, where events are created in a
  decoupled manner.
- This allows any consumer to inspect and react to them as needed.

---

## **Diagram**

The architecture and workflow are illustrated in the diagram below:

![Saga Orchestrator Diagram](./diagrams/two_node_parallel_orchestrator_saga.drawio.png)

---

## **Orchestrator vs. Choreography**

### **Orchestrator Saga** (used in this project)

- Centralized control via an orchestrator service.
- The orchestrator directs the workflow by publishing tasks and reacting to events.
    - More Code
    - More architecture to manage - whole additional service
    - More scalable - works better when there are many services. Only one service and orchestrator service interact.
    - Easier to collaborate between teams, every team needs to additionally think only about orchestrator service.

### **Choreography Saga**

- Decentralized control where services react to events and trigger actions independently.
    - Less Code
    - No additional architecture
    - Harder to maintain, interactions can be scattered between many different services.
    - Harder collaborate between teams, because every service can possibly influence every other service.

---

## **Technology Stack**

- **Java**: Spring Boot framework for building microservices.
- **RabbitMQ**: Message broker for managing communication between services.
- **Draw.io**: Used to create the system design diagram.
