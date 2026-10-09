# SkillSwap — NITW Student Skill Exchange

A student skill-exchange platform built using Spring Boot, Java, Thymeleaf, HTML, CSS, JavaScript, and Supabase PostgreSQL.

## Features

* Student registration and login
* Email OTP verification
* Student dashboard and profiles
* Skills students can teach and want to learn
* Skill-help requests
* PostgreSQL database hosted on Supabase
* BCrypt password hashing

## Requirements

* Java 17 or newer
* Maven 3.8+
* A Supabase account
* Gmail SMTP credentials for email OTP delivery

## Configuration

Configure these environment variables before starting the application:

* `DB_PASSWORD` — Supabase database password
* `MAIL_USERNAME` — Gmail address used to send OTP emails
* `MAIL_APP_PASSWORD` — Gmail app password
* `ADMIN_EMAIL` — administrator email address

Never commit real passwords, app passwords, or other secrets to GitHub.

## Run Locally on Windows PowerShell

From the folder containing `pom.xml`, set your database password securely:

```powershell
$env:DB_PASSWORD = Read-Host "Enter your Supabase database password"
```

If email OTP delivery is required, configure your Gmail environment variables too. Then start the application:

```powershell
mvn spring-boot:run
```

Open http://localhost:8080 in your browser.

## Database

The application is configured to connect to Supabase PostgreSQL. Ensure the required environment variables are configured and the database is accessible.

## Deployment

To let friends access the same website and accounts, deploy the Spring Boot application to a hosting service and configure its environment variables. The application and Supabase database must both be reachable from the deployed service.

## Security

* Do not upload credentials to GitHub.
* Keep database passwords and Gmail app passwords in environment variables.
* Use HTTPS for the public website.
* Verify authentication, authorization, and OTP functionality before public release.
