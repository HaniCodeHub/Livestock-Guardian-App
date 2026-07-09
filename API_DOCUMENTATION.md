# API Documentation

## Base URL

```
https://api.livestock-guardian.com/v1
```

## Authentication

All API requests require a Bearer token in the Authorization header:

```
Authorization: Bearer <token>
```

## Endpoints

### Authentication

#### Register User
```
POST /auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "name": "John Doe"
}

Response: 201 Created
{
  "id": "user_123",
  "email": "user@example.com",
  "name": "John Doe",
  "token": "eyJ0eXAiOiJKV1QiLCJhbGc..."
}
```

#### Login
```
POST /auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}

Response: 200 OK
{
  "token": "eyJ0eXAiOiJKV1QiLCJhbGc...",
  "user": { ... }
}
```

### Livestock

#### Register Animal
```
POST /livestock/register
Content-Type: application/json

{
  "name": "Bessie",
  "species": "cattle",
  "breed": "Holstein",
  "birth_date": "2020-01-15",
  "color": "black and white"
}

Response: 201 Created
{
  "id": "animal_456",
  "owner_id": "user_123",
  ...
}
```

#### Get Animal
```
GET /livestock/{animal_id}

Response: 200 OK
{
  "id": "animal_456",
  "name": "Bessie",
  ...
}
```

#### List Animals
```
GET /livestock?owner_id={owner_id}&limit=10&offset=0

Response: 200 OK
{
  "total": 25,
  "items": [ ... ]
}
```

### Identification

#### Upload Muzzle Image
```
POST /identify/muzzle
Content-Type: multipart/form-data

file: <image_file>
animal_id: animal_456 (optional)

Response: 200 OK
{
  "match_id": "match_789",
  "confidence": 0.95,
  "matched_animal": { ... }
}
```

#### Get Identification Result
```
GET /identify/{match_id}

Response: 200 OK
{
  "id": "match_789",
  "animal_id": "animal_456",
  "confidence": 0.95,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## Error Responses

```
400 Bad Request
{
  "error": "Invalid request",
  "message": "Required field 'name' is missing"
}

401 Unauthorized
{
  "error": "Authentication failed",
  "message": "Invalid or expired token"
}

404 Not Found
{
  "error": "Resource not found",
  "message": "Animal with ID animal_456 not found"
}

500 Internal Server Error
{
  "error": "Server error",
  "message": "An unexpected error occurred"
}
```

## Rate Limiting

- 1000 requests per hour per user
- 10 image uploads per minute per user

## Status Codes

- 200: OK
- 201: Created
- 400: Bad Request
- 401: Unauthorized
- 403: Forbidden
- 404: Not Found
- 500: Internal Server Error
