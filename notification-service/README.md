## Notification Service

Spring Boot microservice responsible for sending transactional emails (welcome, password reset, account locked, email verification) using Thymeleaf templates and JavaMail. Async delivery with logging to `notification_logs`.

### Features
- Async email sending with `@Async` and thread pools
- Thymeleaf HTML templates
- Template validation and preview utilities
- Notification persistence via JPA (`notification_logs`)
- REST endpoints for triggering emails

### Requirements
- JDK 21
- Maven 3.9+
- PostgreSQL
- SMTP credentials (host/port/username/password)

### Environment
Create `.env` at repo root of this service (see `.env.example`):

```
SERVICE_NAME=notification-service

DB_HOST=localhost
DB_PORT=5432
DB_NAME=notification_db
DB_USERNAME=postgres
DB_PASSWORD=postgres
DB_DRIVER=org.postgresql.Driver

EMAIL_HOST=smtp.example.com
EMAIL_PORT=587
EMAIL_USERNAME=your_smtp_user
EMAIL_PASSWORD=your_smtp_password

SERVER_PORT=8085
EMAIL_FROM=noreply
EMAIL_FROM_NAME=E-Commerce
```

Application properties read these values from environment.

### Build
```
./mvnw -DskipTests package
```

### Run (local)
```
SPRING_CONFIG_IMPORT=optional:file:.env[.properties] ./mvnw spring-boot:run
```

If using Windows PowerShell:
```
$env:SPRING_CONFIG_IMPORT="optional:file:.env[.properties]"; .\mvnw spring-boot:run
```

Health check:
```
GET http://localhost:8085/actuator/health
```

### REST API
- POST `/api/notifications/welcome`
  - body: `{ "to": "email", "userName": "string" }`

- POST `/api/notifications/password-reset`
  - body: `{ "to": "email", "userName": "string", "resetUrl": "url", "token": "string" }`

- POST `/api/notifications/account-locked`
  - body: `{ "to": "email", "userName": "string", "unlockUrl": "url" }`

- POST `/api/notifications/email-verification`
  - query params: `to`, `userName`, `verificationUrl`

- POST `/api/notifications/push`
  - body: `{ "tokenOrTopic": "token-or-/topics/topic", "title": "string", "body": "string" }`

- POST `/api/notifications/send` (unified dispatcher)
  - body:
    ```json
    {
      "channel": "EMAIL|PUSH|IN_APP",
      "type": "WELCOME|PASSWORD_RESET|ACCOUNT_LOCKED|EMAIL_VERIFICATION|GENERIC",
      "to": "recipient",
      "templateName": "optional-template-name",
      "variables": {"userName":"..","resetUrl":"..","token":".."},
      "idempotencyKey": "optional",
      "metadata": {"any":"value"}
    }
    ```

Response shape:
```
{
  "success": true|false,
  "message": "string",
  "notificationId": "string|null"
}
```

### Templates
Located under `src/main/resources/templates/`:
- `welcome-email.html`
- `password-reset-email.html`
- `account-locked-email.html`
- `email-verification-email.html`

Common variables available: `currentYear`, `appName` plus template-specific fields.

### In-App Notifications
- Entity: `InAppNotification`
- Endpoints:
  - POST `/api/in-app/create?userId=1&title=...&body=...`
  - GET `/api/in-app/{userId}`
  - POST `/api/in-app/mark-read/{id}`

### Error Handling
- Centralized `GlobalExceptionHandler` returns structured JSON with appropriate HTTP status:
  - 503 for provider initialization failures
  - 502 for provider send errors
  - 400 for validation errors
  - 500 for other errors

### Providers & Config
- Email: SMTP via Spring Mail (synchronous send in `NotificationService`)
- Push (FCM): enable with env
  - `push.enabled=true`
  - `fcm.enabled=true`
  - `fcm.credentials.path=path/to/service-account.json`

Note: SMS support removed from this service.

### Persistence
- Entity: `NotificationLog` with status `PENDING|SENT|FAILED`
- Repository: `NotificationLogRepository`

### Notes
- Ensure valid SMTP to actually deliver messages; otherwise responses may be success but SMTP host rejects.
- For service discovery, configure Eureka in `.env` if registering with a registry.


