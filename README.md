
# Smart Laundry Facility Simulation

A Java-based concurrent programming project that simulates the operations of a smart self-service laundry facility.

## About the Project

SmartLaundry is an academic software development project developed using Java. It simulates a self-service laundromat where multiple customers access shared resources and complete washing, drying, and payment processes concurrently.

The application demonstrates Java multithreading, synchronization, resource management, and GUI-based simulation.

## Simulation Environment

- 50 customers
- 6 washing machines
- 4 dryers
- 2 payment kiosks

## Key Features

- Concurrent customer processing using Java threads.
- Washing machine and dryer resource management.
- Customer queuing when resources are occupied.
- Payment processing through multiple kiosks.
- Resource synchronization using semaphores and blocking queues.
- Simulated machine and payment kiosk failures.
- Automatic retry mechanisms.
- Payment congestion and owner notification scenario.
- GUI visualization of laundromat activities.
- Statistical tracking of customer completion times, machine utilization, failures, and retries.

## Technologies Used

- Java
- Java Swing
- Java2D
- Apache NetBeans IDE 31
- Apache Ant
- Java Concurrency API

## Concurrency Concepts

- Multithreading
- Semaphore
- BlockingQueue
- AtomicInteger and AtomicBoolean
- Synchronized blocks
- wait() and notifyAll()
- Thread communication
- Resource management

## Screenshots

Application screenshots are available in the `screenshots` folder.

## How to Run

1. Clone or download this repository.
2. Install a compatible Java JDK.
3. Open Apache NetBeans IDE.
4. Select File → Open Project.
5. Navigate to the downloaded SmartLaundry folder.
6. Build and run the project.

## Project Structure

```text
SmartLaundry/
├── src/
├── nbproject/
├── test/
├── screenshots/
├── build.xml
├── manifest.mf
├── README.md
└── .gitignore
```

## Purpose

Developed as part of my BSc in Information Technology coursework for Concurrent Programming.

This project provided practical experience in Java application development, multithreading, synchronization, and concurrent resource management.

## Developer

**Sumitra Gurung**

BSc in Information Technology