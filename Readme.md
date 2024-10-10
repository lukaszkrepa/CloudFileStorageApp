# Cloud File Storage App

A Spring Boot application that allows users to store and manage their files in the cloud using Amazon S3. The app includes user authentication, secure file sharing, and file management capabilities.

## Features

- **User Registration**: Register and manage users with secure authentication.
- **File Upload**: Upload files to Amazon S3 for cloud storage.
- **File Download**: Securely download stored files from the cloud.
- **File Sharing**: Share files with other users via unique links.

## Tech Stack

- **Java 17**
- **Spring Boot**
- **Spring Security** (for authentication)
- **Spring Data JPA** (for database interaction)
- **Thymeleaf** (for the front-end)
- **Amazon S3** (for cloud file storage)
- **H2 Database** (for testing)
- **AWS RDS MySQL** (for production database)
- **Maven** (for build management)

## Prerequisites

Before you begin, make sure you have the following installed:

- **Java 17**
- **Maven** (for building the project)
- **AWS Account** with full access to **S3**. (If hosted on **EC2**, ensure the instance has the necessary roles and permissions.)
- **MySQL Database** (RDS for production)

### Environment Variables

Set the following environment variables before running the application:

- `DB_URL` – The URL for your database.
- `DB_USERNAME` – Your database username.
- `DB_PASSWORD` – Your database password.
- `AWS_ACCESS_KEY_ID` – Your AWS access key.
- `AWS_SECRET_ACCESS_KEY` – Your AWS secret access key.
- `AWS_REGION` – AWS region where the S3 bucket is hosted.
- `BUCKET_NAME` – The name of your S3 bucket.

## Getting Started

### Build the Project

1. Clone the repository:
   ```bash
   git clone <your-repo-url>
   cd cloud-file-storage-app

2. Install dependencies and build the project:
    ```bash
   mvn clean install

3. Run the application
   ```bash
   mvn spring-boot:run
   
4. Run tests
   ```bash
   mvn test