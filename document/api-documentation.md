# WorkFlowGo API Documentation

This document outlines the API endpoints required for the WorkFlowGo interview tracking system. The backend is implemented using a RESTful API architecture.

## Base URL

All API endpoints are relative to: `/api`

## Authentication

Authentication will be implemented using JWT tokens. All authenticated endpoints require a valid JWT token in the Authorization header.

## Data Models

### Interview

```typescript
export type InterviewStatus = 'scheduled' | 'confirmed' | 'completed' | 'rejected' | 'cancelled'
export type InterviewType = 'remote' | 'onsite' | 'phone'
export type QuestionCategory = 'technical' | 'behavioral' | 'company' | 'role' | 'other'
export type DocumentType = 'resume' | 'cover_letter' | 'portfolio' | 'other'

export interface ContactPerson {
  name: string
  position: string
  email: string
  phone: string
}

export interface Question {
  id: string
  question: string
  answer: string
  category: QuestionCategory
  isImportant: boolean
}

export interface Document {
  id: string
  name: string
  type: DocumentType
  url: string
  contentType?: string
  size?: number
  createdAt?: string
}

export interface Interview {
  id: string
  companyName: string
  position: string
  date: string
  time: string
  type: InterviewType
  status: InterviewStatus
  location: string
  notes: string
  contactPerson: ContactPerson
  questions: Question[]
  documents: Document[]
  rating: number
  feedback: string
  createdAt: string
  updatedAt: string
  userId: string
}

export interface User {
  id: string
  name: string
  email: string
  preferences: {
    theme: {
      darkMode: boolean
      primaryColor: string
    }
    notifications: {
      enabled: boolean
      emailNotifications: boolean
      reminderTime: string
    }
    display: {
      defaultView: 'calendar' | 'list'
      compactMode: boolean
    }
  }
}

export interface Reminder {
  id: string
  interviewId: string
  time: string
  message: string
  isCompleted: boolean
}

export interface InterviewStatistics {
  totalInterviews: number
  upcomingInterviews: number
  completedInterviews: number
  rejectedInterviews: number
  byStatus: Record<InterviewStatus, number>
  byCompany: Array<{
    company: string
    count: number
  }>
  byMonth: Array<{
    month: string
    count: number
  }>
}
```

## API Endpoints

### Authentication

#### Register New User

- **URL**: `/auth/register`
- **Method**: `POST`
- **Description**: Register a new user with email, password, and display name
- **Request Body**:
  ```json
  {
    "email": "user@example.com",
    "password": "securePassword123",
    "displayName": "User Name"
  }
  ```
- **Response**: AuthResponse object with user data and JWT token
  ```json
  {
    "user": {
      "id": "user123",
      "email": "user@example.com",
      "displayName": "User Name",
      "photoURL": null,
      "authProvider": "password"
    },
    "token": "jwt_token_string"
  }
  ```
- **Error Responses**:
  - `400 Bad Request` - If email is already registered or validation fails
- **Frontend Implementation**: `authStore.register(email, password, displayName)`
- **Used in Components**: `Register.vue`

#### Login with Email and Password

- **URL**: `/auth/login`
- **Method**: `POST`
- **Description**: Authenticate user with email and password
- **Request Body**:
  ```json
  {
    "email": "user@example.com",
    "password": "securePassword123"
  }
  ```
- **Response**: AuthResponse object with user data and JWT token
  ```json
  {
    "user": {
      "id": "user123",
      "email": "user@example.com",
      "displayName": "User Name",
      "photoURL": null,
      "authProvider": "password"
    },
    "token": "jwt_token_string"
  }
  ```
- **Error Responses**:
  - `401 Unauthorized` - If credentials are invalid
- **Frontend Implementation**: `authStore.loginWithCredentials(email, password)`
- **Used in Components**: `Login.vue`

#### Google OAuth Login

- **URL**: `/auth/google`
- **Method**: `GET`
- **Description**: Redirect to Google OAuth login page
- **Response**: Redirects to Google authentication
- **Frontend Implementation**: `authStore.loginWithGoogle()`
- **Used in Components**: `Login.vue`

#### Google OAuth Callback

- **URL**: `/auth/google/callback`
- **Method**: `GET`
- **Description**: Handle Google OAuth callback
- **Query Parameters**: `code=[string]` - Authorization code from Google
- **Response**: AuthResponse object with user data and JWT token (or redirects with token in query parameter)
- **Frontend Implementation**: `authService.handleGoogleCallback(code)`
- **Used in Components**: `GoogleCallback.vue`

#### Validate Token

- **URL**: `/auth/validate`
- **Method**: `GET`
- **Description**: Validate JWT token
- **Headers**: `Authorization: Bearer {token}`
- **Response**: Status 200 if token is valid
- **Error Responses**:
  - `401 Unauthorized` - If token is invalid or expired
- **Frontend Implementation**: `authService.validateToken()`
- **Used in Components**: Router guard

#### Logout

- **URL**: `/auth/logout`
- **Method**: `POST`
- **Description**: Invalidate user token
- **Headers**: `Authorization: Bearer {token}`
- **Response**: Status 200
- **Frontend Implementation**: `authStore.logout()`
- **Used in Components**: `UserProfile.vue`

### Interviews

#### Get All Interviews

- **URL**: `/interviews`
- **Method**: `GET`
- **Description**: Retrieve all interviews for the authenticated user
- **Headers**: `Authorization: Bearer {token}`
- **Query Parameters**:
  - `status?: string` - Filter by status (optional)
  - `from?: string` - Filter by date range start (optional)
  - `to?: string` - Filter by date range end (optional)
  - `company?: string` - Filter by company name (optional)
  - `sort?: string` - Sort field (default: date)
  - `order?: 'asc' | 'desc'` - Sort order (default: asc)
- **Response**: Array of Interview objects
- **Frontend Implementation**: `interviewsApi.getAll(params)`
- **Used in Components**: `Interviews.vue`, `Home.vue`, `Calendar.vue`

#### Get Interview by ID

- **URL**: `/interviews/{id}`
- **Method**: `GET`
- **Description**: Retrieve a specific interview by ID
- **Headers**: `Authorization: Bearer {token}`
- **URL Parameters**: `id=[string]` - ID of the interview
- **Response**: Interview object
- **Error Responses**:
  - `404 Not Found` - If interview with the specified ID does not exist
  - `403 Forbidden` - If interview does not belong to the authenticated user
- **Frontend Implementation**: `interviewsApi.getById(id)`
- **Used in Components**: `InterviewDetail.vue`, `InterviewForm.vue` (edit mode)

#### Create Interview

- **URL**: `/interviews`
- **Method**: `POST`
- **Description**: Create a new interview
- **Headers**: `Authorization: Bearer {token}`
- **Request Body**: Interview object (without id, createdAt, updatedAt, and userId)
- **Response**: Created interview object with id, createdAt, updatedAt, and userId
- **Error Responses**:
  - `400 Bad Request` - If required fields are missing or invalid
- **Frontend Implementation**: `interviewsApi.create(interview)`
- **Used in Components**: `InterviewForm.vue` (create mode)

#### Update Interview

- **URL**: `/interviews/{id}`
- **Method**: `PUT`
- **Description**: Update an existing interview
- **Headers**: `Authorization: Bearer {token}`
- **URL Parameters**: `id=[string]` - ID of the interview to update
- **Request Body**: Interview object (partial updates allowed)
- **Response**: Updated interview object
- **Error Responses**:
  - `404 Not Found` - If interview with the specified ID does not exist
  - `403 Forbidden` - If interview does not belong to the authenticated user
  - `400 Bad Request` - If required fields are missing or invalid
- **Frontend Implementation**: `interviewsApi.update(id, interview)`
- **Used in Components**: `InterviewForm.vue` (edit mode)

#### Update Interview Status

- **URL**: `/interviews/{id}/status`
- **Method**: `PATCH`
- **Description**: Update only the status of an interview
- **Headers**: `Authorization: Bearer {token}`
- **URL Parameters**: `id=[string]` - ID of the interview
- **Request Body**: 
  ```json
  {
    "status": "scheduled | confirmed | completed | rejected | cancelled"
  }
  ```
- **Response**: Updated interview object
- **Error Responses**:
  - `404 Not Found` - If interview with the specified ID does not exist
  - `403 Forbidden` - If interview does not belong to the authenticated user
  - `400 Bad Request` - If status is invalid
- **Frontend Implementation**: `interviewsApi.updateStatus(id, status)`
- **Used in Components**: `InterviewDetail.vue`

#### Delete Interview

- **URL**: `/interviews/{id}`
- **Method**: `DELETE`
- **Description**: Delete an interview
- **Headers**: `Authorization: Bearer {token}`
- **URL Parameters**: `id=[string]` - ID of the interview to delete
- **Response**: Status 204 (No Content)
- **Error Responses**:
  - `404 Not Found` - If interview with the specified ID does not exist
  - `403 Forbidden` - If interview does not belong to the authenticated user
- **Frontend Implementation**: `interviewsApi.delete(id)`
- **Used in Components**: `Interviews.vue`, `InterviewDetail.vue`

### Documents

#### Get All Documents

- **URL**: `/documents`
- **Method**: `GET`
- **Description**: Retrieve all documents for the authenticated user
- **Headers**: `Authorization: Bearer {token}`
- **Response**: Array of Document objects
- **Frontend Implementation**: `documentsApi.getAll()`
- **Used in Components**: `Documents.vue`

#### Upload Document

- **URL**: `/documents`
- **Method**: `POST`
- **Description**: Upload a new document
- **Headers**: `Authorization: Bearer {token}`
- **Request Body**: Multipart form data with:
  - `file` - The document file
  - `name` - Document name
  - `type` - Document type (resume, cover_letter, portfolio, other)
- **Response**: Created document object with id and url
- **Error Responses**:
  - `400 Bad Request` - If required fields are missing or invalid
  - `413 Payload Too Large` - If file size exceeds the limit
- **Frontend Implementation**: `documentsApi.upload(file, name, type)`
- **Used in Components**: `Documents.vue`, `InterviewForm.vue`

#### Get Document

- **URL**: `/documents/{id}`
- **Method**: `GET`
- **Description**: Get document metadata by ID
- **Headers**: `Authorization: Bearer {token}`
- **URL Parameters**: `id=[string]` - ID of the document
- **Response**: Document object
- **Error Responses**:
  - `404 Not Found` - If document with the specified ID does not exist
  - `403 Forbidden` - If document does not belong to the authenticated user
- **Frontend Implementation**: `documentsApi.getById(id)`
- **Used in Components**: `Documents.vue`

#### Delete Document

- **URL**: `/documents/{id}`
- **Method**: `DELETE`
- **Description**: Delete a document
- **Headers**: `Authorization: Bearer {token}`
- **URL Parameters**: `id=[string]` - ID of the document to delete
- **Response**: Status 204 (No Content)
- **Error Responses**:
  - `404 Not Found` - If document with the specified ID does not exist
  - `403 Forbidden` - If document does not belong to the authenticated user
- **Frontend Implementation**: `documentsApi.delete(id)`
- **Used in Components**: `Documents.vue`, `InterviewForm.vue`

### User Settings

#### Get User Settings

- **URL**: `/user/settings`
- **Method**: `GET`
- **Description**: Retrieve the current user's settings
- **Headers**: `Authorization: Bearer {token}`
- **Response**: UserSettings object
- **Frontend Implementation**: `userSettingsApi.get()`
- **Used in Components**: `Settings.vue`

#### Update User Settings

- **URL**: `/user/settings`
- **Method**: `PUT`
- **Description**: Update the current user's settings
- **Headers**: `Authorization: Bearer {token}`
- **Request Body**: Partial UserSettings object
  ```json
  {
    "preferences": {
      "theme": {
        "darkMode": true,
        "primaryColor": "#6366F1"
      },
      "notifications": {
        "enabled": true,
        "emailNotifications": true,
        "reminderTime": "1day"
      },
      "display": {
        "defaultView": "calendar",
        "compactMode": false
      }
    }
  }
  ```
- **Response**: Updated UserSettings object
- **Error Responses**:
  - `400 Bad Request` - If required fields are missing or invalid
- **Frontend Implementation**: `userSettingsApi.update(settings)`
- **Used in Components**: `Settings.vue`

#### Reset User Settings

- **URL**: `/user/settings/reset`
- **Method**: `POST`
- **Description**: Reset user settings to default values
- **Headers**: `Authorization: Bearer {token}`
- **Response**: UserSettings object with default settings
- **Frontend Implementation**: `userSettingsApi.resetSettings()`
- **Used in Components**: `Settings.vue`

#### Export User Data

- **URL**: `/user/export`
- **Method**: `GET`
- **Description**: Export all user data (interviews, documents, settings)
- **Headers**: `Authorization: Bearer {token}`
- **Response**: Binary file (JSON format)
- **Frontend Implementation**: `userSettingsApi.exportData()`
- **Used in Components**: `Settings.vue`

### Statistics

#### Get Interview Statistics

- **URL**: `/statistics/interviews`
- **Method**: `GET`
- **Description**: Get interview statistics for the authenticated user
- **Headers**: `Authorization: Bearer {token}`
- **Query Parameters**:
  - `from?: string` - Filter by date range start (optional)
  - `to?: string` - Filter by date range end (optional)
- **Response**: InterviewStatistics object
  ```json
  {
    "totalInterviews": 10,
    "upcomingInterviews": 3,
    "completedInterviews": 5,
    "rejectedInterviews": 2,
    "byStatus": {
      "scheduled": 2,
      "confirmed": 1,
      "completed": 5,
      "rejected": 2,
      "cancelled": 0
    },
    "byCompany": [
      { "company": "Google", "count": 3 },
      { "company": "Microsoft", "count": 2 }
    ],
    "byMonth": [
      { "month": "2023-01", "count": 3 },
      { "month": "2023-02", "count": 7 }
    ]
  }
  ```
- **Frontend Implementation**: `statisticsApi.getInterviewStats(params)`
- **Used in Components**: `Home.vue`

## Component-Specific Data Requirements

### Login.vue
- Requires authentication functionality for email/password and Google login
- Uses: `authStore.loginWithCredentials(email, password)`, `authStore.loginWithGoogle()`

### Register.vue
- Requires user registration functionality
- Uses: `authStore.register(email, password, displayName)`

### GoogleCallback.vue
- Handles Google OAuth callback
- Uses: `authService.handleGoogleCallback(code)`

### UserProfile.vue
- Displays current user information and logout functionality
- Uses: `authStore.user`, `authStore.logout()`

### Home.vue
- Requires interview statistics data
- Requires recent interviews list (limited to 5-10 items)
- Uses: `statisticsApi.getInterviewStats()`, `interviewStore.fetchInterviews()`

### Interviews.vue
- Requires full list of interviews with filtering and sorting capabilities
- Uses: `interviewStore.fetchInterviews()`, `interviewsApi.delete(id)`

### InterviewForm.vue
- Requires document upload functionality
- For edit mode: requires fetching existing interview data
- Uses: `interviewsApi.getById(id)`, `interviewsApi.create(interview)`, `interviewsApi.update(id, interview)`, `documentsApi.upload(file, name, type)`

### InterviewDetail.vue
- Requires detailed interview data including questions, documents, and contact information
- Uses: `interviewStore.getInterviewById(id)`, `interviewsApi.updateStatus(id, status)`, `interviewsApi.delete(id)`

### Documents.vue
- Requires list of all documents
- Requires document upload, view, and delete functionality
- Uses: `documentsApi.getAll()`, `documentsApi.upload(file, name, type)`, `documentsApi.delete(id)`

### Calendar.vue
- Requires all interviews to display in calendar format
- Uses: `interviewStore.fetchInterviews()`

### Settings.vue
- Requires user settings data
- Requires ability to update settings, reset to defaults, and export data
- Uses: `userSettingsApi.get()`, `userSettingsApi.update(settings)`, `userSettingsApi.resetSettings()`, `userSettingsApi.exportData()`

## Implementation Notes

1. All API endpoints require authentication except for `/auth/login`, `/auth/register`, and `/auth/google`.
2. JWT tokens should be included in the `Authorization` header as `Bearer {token}`.
3. The backend should validate the token for each authenticated request.
4. User data should be associated with the authenticated user's ID.
5. Error handling should be consistent across all endpoints.
6. For file uploads, consider implementing progress indicators and file size limits.
7. Environment-specific configuration should be managed through environment variables.
8. Ensure proper validation of all input data both on the frontend and backend.
9. Consider implementing refresh tokens for better security.
