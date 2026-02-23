API Reliability Monitor

A command-line tool to track web APIs for internal monitoring and error validation.

Status: Under active development


goal:

-Write a system that monitors API root and endpoint uptime in a scalable and efficient manner.

-Application gives an overview of an API's overall health for user reference.

Features:

-Command-Line Interface (CLI)

-HTTPS requests to a set of websites to check status code

-background request loop and active UI


Roadmap:

- [ ] Measure APIs and their endpoints

- [ ] Next.JS frontend

- [ ] Neon PostgreSQL database
  
- [ ] Semantic analysis

Technical Highlights:

-utilizes CompletableFutures to allow for concurrent out going HTTP requests

-Custom class for heterogeneous data storage and future JSON export


Requirements:

-java 17+

-git


Installation:

git clone https://github.com/rjprima/API-Reliability-Monitor.git

cd API-Reliability-Monitor

javac *.java

java core
