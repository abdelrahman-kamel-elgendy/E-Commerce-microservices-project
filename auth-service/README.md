# Auth Service

This is the Authentication service for the E-Commerce microservices project.

It provides:
- User registration (with email verification)
- Login (JWT access token issuance, per-session storage)
- Refresh token endpoint (stateless JWT refresh tokens)
- Logout / logout-all (session invalidation)
- Forgot password / Reset password (JWT reset tokens emailed via Notification service)
- Account lockout after repeated failed logins, with unlock via emailed token
- Endpoints to get current user (`/me`) and to resend verification emails

Table of contents
- Requirements
- Configuration (environment / application.properties)
- Important endpoints
- Token model and flows
- Development: build & run
- Testing & troubleshooting
- Notes

Requirements
- Java 17+ (project uses Java 17+ features)
- Maven
- A running PostgreSQL database and appropriate JDBC URL configured
- Notification microservice available and reachable (Feign client)

Configuration
The app reads properties from `application.properties` or environment variables. Important properties:

- `app.jwt.secret` - HMAC secret for signing JWTs
- `app.jwt.expiration` - access token TTL in milliseconds
- `app.jwt.refresh-expiration` - refresh/verification token TTL in milliseconds
- `app.jwt.reset-expiration` - password reset token TTL in milliseconds

- `app.security.max-sessions-per-user` - maximum concurrent sessions a user may have
- `app.security.max-failed-login-attempts` - attempts before account lock
- `app.security.lock-duration-minutes` - lock duration when threshold reached
- `app.security.password-reset.reset-url` - URL base sent in password reset emails
- `app.email.verification.verify-url` - URL base used in email verification links

- `app.security.allowed-origins` - CORS allowed origin list

Also configure standard Spring datasource properties for PostgreSQL, logging, and any Discovery / Feign settings for Notification service.

Important endpoints
Base path: `/api/auth`

- POST /register
  - Body: RegisterUser (email, password, firstName, lastName)
  - Creates a disabled user and sends email verification link

- POST /verify-email?token=<token>
  - Validates verification token and enables the account

- POST /login
  - Body: LoginUser (email, password)
  - Returns `AuthResponse` containing `token` (access token) and `session` info

- POST /refresh-token
  - Accepts the refresh token (Bearer Authorization header or `token` parameter)
  - Returns new access token and session info

- POST /logout
  - Invalidates current session (token used in Authorization header)

- POST /forgot-password?email=<email>
  - Sends password reset link

- POST /reset-password
  - Body: ResetPasswordRequest { token, newPassword }
  - Resets the password after validating token

- POST /unlock-account?token=<token>
  - Unlocks a locked account using the emailed unlock token

- GET /me
  - Returns current user info (requires access token)

- POST /resend-verification?email=<email>
  - Resends verification email if user pending verification

Token model & flows
- Access tokens are JWTs with claims:
  - `sub` (subject) = user email
  - `sessionId` = database session id (UserSession)
  - `iat`, `exp`

  Access tokens are enforced by the `JwtTokenFilter`, which checks session active status and that the token was issued after any recorded logout timestamp.

- Refresh tokens are stateless JWTs with a claim `purpose=refresh`. Refresh tokens are NOT tied to sessions. The `/refresh-token` endpoint expects a refresh token, validates it, and issues a new access token (and new server-side session).

- Password reset, email verification, and account-unlock tokens are JWTs with special `purpose` claims (e.g. `password_reset`, `email_verification`, `account_unlock`). These are validated by the service when relevant endpoints are called.

Development: build & run

From the `auth-service` folder:

```powershell
# Linux/Windows (PowerShell) mvnw script included
./mvnw -DskipTests package
# Run the jar
java -jar target/auth-service-0.0.1-SNAPSHOT.jar
```

Testing & troubleshooting
- If refresh returns `Token is not a refresh token`:
  1. Ensure you're sending the *refresh* token (the token issued as a refresh token) to `/api/auth/refresh-token`.
  2. The refresh token contains claim `purpose: refresh` — decode on jwt.io to inspect claims and `exp`.
  3. The endpoint accepts the token in Authorization: Bearer <token> header or `?token=<token>` query param.

- If you get `Token was issued before logout` when using a token after logout, that's expected (token has been invalidated by session logout).

- If failed-login attempts are not persisted, ensure the database transaction settings allow REQUIRES_NEW propagation for the login attempt updater (the project uses a small service that writes failed attempts in a separate transaction so they persist when auth fails).

Notes
- Consider rotating refresh tokens to make them single-use if you need maximum security. Currently refresh tokens are stateless JWTs and can be reused until they expire.
- The `Notification` microservice must accept the same payloads used by the Feign client. Adjust NotificationClient or the Notification service contract if emails aren't being delivered.

Contact & contributing
- Repo: https://github.com/abdelrahman-kamel-elgendy/E-Commerce-microservices-project
- Feel free to open issues or PRs for improvements.
