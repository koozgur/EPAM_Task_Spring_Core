# Swagger UI Guide - GymCRM REST API

## Quick Start

Once the application is running, access the Swagger UI at:
```
http://localhost:8080/swagger-ui/index.html
```

## Features

### 1. Browse All Endpoints
The left sidebar displays all available endpoints organized by tags:
- **Authentication** - Login and password management
- **Trainees** - Trainee registration, profiles, and management
- **Trainers** - Trainer registration, profiles, and management
- **Trainings** - Add training records
- **Training Types** - View available training types

### 2. Authenticate with HTTP Basic Auth

#### Option A: Via the "Authorize" Button (Recommended)
1. Click the **Authorize** button (padlock icon) in the top-right
2. Enter your credentials:
   - **Username**: Your login username
   - **Password**: Your password
3. Click **Authorize**
4. Close the dialog

All subsequent requests will include your credentials automatically.

#### Option B: Manual Testing
When you click "Try it out" on any secured endpoint, Swagger UI will prompt for authorization before sending the request.

### 3. Test an Endpoint

1. **Click on an endpoint** to expand it
2. Click **"Try it out"** button
3. Fill in any required parameters:
   - **Path parameters** (e.g., `{username}`) - required
   - **Query parameters** (e.g., `periodFrom`, `periodTo`) - optional
   - **Request body** (for POST/PUT) - formats provided
4. Click **"Execute"**
5. View the response:
   - **Response code** (200, 401, 404, etc.)
   - **Response body** (JSON)
   - **Response headers** (includes `X-Transaction-Id` for tracking)

### 4. Understanding Response Codes

| Code | Meaning | Action |
|------|---------|--------|
| **200** | Success (GET, PUT) | Your request worked |
| **201** | Created (POST) | Resource successfully created |
| **400** | Bad Request | Check your input format (dates, email, etc.) |
| **401** | Unauthorized | Missing or invalid credentials |
| **404** | Not Found | Resource doesn't exist (user, trainee, trainer) |
| **409** | Conflict | Invalid state (e.g., activating already-active trainee) |
| **500** | Server Error | Unexpected error; check server logs |

### 5. Key Endpoints to Try First

#### Public (No Auth Required)
```
POST /trainees/register
POST /trainers/register
GET  /swagger-ui/index.html
```

#### After Authentication
```
GET    /trainees/{username}           - Get trainee profile
PUT    /trainees/{username}           - Update trainee info
GET    /trainers/{username}           - Get trainer profile
GET    /training-types                - View all training types
POST   /trainings                     - Add a training record
```

## Common Use Cases

### Register a New Trainee
1. POST `/trainees/register`
2. Provide: first name, last name, date of birth, address
3. Get back: username and temporary password (check response body)

### Update Trainee Profile (Requires Auth)
1. Click "Authorize" and login as the trainee
2. PUT `/trainees/{username}` with your new info
3. Confirm success (200 OK)

### View Your Personal Training History (Requires Auth)
1. Authorize as a trainee
2. GET `/trainees/{username}/trainings?periodFrom=2026-01-01&periodTo=2026-12-31`
3. See all your scheduled trainings

## Troubleshooting

| Problem | Solution |
|---------|----------|
| "401 Unauthorized" | Click Authorize button; re-enter credentials |
| "404 Not Found" | Check the username/ID exists; verify spelling |
| "400 Bad Request" | Review required fields; dates must be YYYY-MM-DD format |
| Swagger UI won't load | Ensure app is running; check `http://localhost:8080` is accessible |
| Swagger UI shows 0 endpoints | Wait 5-10 seconds for Spring to initialize; refresh page |

## Transaction Tracking

Every request includes a unique transaction ID in the response headers:
```
X-Transaction-Id: a1b2c3d4-e5f6-4g7h-8i9j-k0l1m2n3o4p5
```

Use this ID to correlate your request with server logs for debugging.

## Security Notes

- **Never share credentials** in request examples
- Passwords are transmitted Base64-encoded; use HTTPS in production
- Swagger UI is for development; restrict access to production deployments
- Credentials are NOT stored in browser cookies (stateless REST)

## Additional Resources

- **OpenAPI Spec**: `http://localhost:8080/v3/api-docs` (raw JSON)
- **Application Port**: Default `8080`
- **Context Path**: `/` (unless `server.servlet.context-path` is explicitly configured)
- **Server Logs**: Check logback output for `[txId=...]` correlation
