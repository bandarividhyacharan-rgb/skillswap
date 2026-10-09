# SkillSwap — NITW Student Skill Exchange

A runnable Spring Boot + MySQL starter project for students to exchange skills.

## Requirements
- Java 17 or newer
- Maven 3.8+
- MySQL 8+

## 1. Create the database
Open MySQL Workbench or the MySQL command line and run:

```sql
CREATE DATABASE skillswap CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## 2. Configure environment variables (recommended)
The app reads database/mail settings from environment variables. Defaults are for local development only.

**Windows PowerShell:**
```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/skillswap"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="YOUR_MYSQL_PASSWORD"
$env:ADMIN_EMAIL="skillswap22343739@gmail.com"
```

For real email OTP delivery, set a Gmail account that will SEND email and a Google App Password:
```powershell
$env:MAIL_USERNAME="your-sending-gmail@gmail.com"
$env:MAIL_APP_PASSWORD="your-16-character-google-app-password"
```
Do not use your normal Gmail password. Do not share your app password. If mail variables are not configured, OTP is printed in the server terminal for local testing.

## 3. Run the project
Open PowerShell in this folder:
```powershell
mvn spring-boot:run
```
Then open http://localhost:8080

## 4. Give friends access
Sending the ZIP lets friends run their own local copy. Their local databases will be separate.
For all friends to share the same accounts and data, deploy the app and a MySQL database to a server and share the deployed URL. Do not expose your laptop or database directly to the public internet.

## Current included functionality
- Student registration with official student email validation and OTP verification
- B.Tech roll number format check (9 alphanumeric characters)
- Branch, year, and course dropdowns
- Login/logout with BCrypt password hashes and server-side session
- Add teach/learn skills
- Browse students and send skill-help requests
- Accept/reject incoming requests
- MySQL persistence for users, skills and requests
- Admin email setting for future/admin tooling; the email address alone does not grant an admin role
- Optional background music button (browser may require a user click)

## Notes
- The email regex expects `@student.nitw.ac.in`; verify the exact official address format for your programme before using it outside B.Tech testing.
- The OTP is time-limited and kept in memory in this starter version. Restarting the app clears pending OTPs. For production, use persistent hashed OTP records, rate limiting, HTTPS, CSRF protection, account recovery, and proper role-based admin provisioning.
- Never commit passwords or mail app passwords to GitHub.
