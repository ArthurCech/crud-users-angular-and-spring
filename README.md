# Dhaulagiri

## About the project

Dhaulagiri is an application developed in Spring Boot and Angular while I was doing some courses.

The purpose of this system is simple, it allows management of users. The operations allowed are: create, update, delete and find users.

The application was protected using Spring Security, JWT token. Some resources are protected by the role and authorities of the logged in user.

## Technologies

### Back end

- Java 17
- Spring Boot 3.0.1
- Auth0 JWT
- Spring Security
- JPA/Hibernate
- Maven
- MySQL

### Front end

- Angular 15
- TypeScript

## How to run

### First steps

- [OPTIONAL] Run the docker-compose (`docker-compose up`) if you don't have MySQL installed locally
- [OPTIONAL] If you want to enable the email service to send email for users, you have to uncomment some lines of `UserService` and configure the `EmailService` with your credentials
- [OPTIONAL] If you want to let the system generates the password for created users, you can uncomment the lines where the method to generate the passwords is called

### (1) Back end

Requirements: [Maven](https://maven.apache.org/) and [Java 17](https://www.oracle.com/java/technologies/downloads/)

1. clone the repository: `git clone https://github.com/ArthurCech/dhaulagiri-users-management`
2. access project folder
3. access backend folder: `cd backend`
4. execute the project: `./mvnw spring-boot:run`

### (2) Front end

Requirements: [node](https://nodejs.org/en/), [npm](https://www.npmjs.com/) and [Angular](https://angular.io/)

1. clone the repository: `git clone https://github.com/ArthurCech/dhaulagiri-users-management`
2. access project folder
3. access frontend folder: `cd frontend`
4. install dependencies: `npm install`
5. execute the project: `ng serve`
