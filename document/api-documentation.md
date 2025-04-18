# WorkFlowGo API Documentation

This document outlines the API endpoints required for the WorkFlowGo interview tracking system. The backend is implemented using a RESTful API architecture.

## Base URL

All API endpoints are relative to: `/api`

## Authentication

Authentication will be implemented in a future phase. Currently, the API is designed for personal use without authentication.

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

### Interviews

#### Get All Interviews

- **URL**: `/interviews`
- **Method**: `GET`
- **Description**: Retrieve all interviews
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
- **URL Parameters**: `id=[string]` - ID of the interview
- **Response**: Interview object
- **Error Responses**:
  - `404 Not Found` - If interview with the specified ID does not exist
- **Frontend Implementation**: `interviewsApi.getById(id)`
- **Used in Components**: `InterviewDetail.vue`, `InterviewForm.vue` (edit mode)

#### Create Interview

- **URL**: `/interviews`
- **Method**: `POST`
- **Description**: Create a new interview
- **Request Body**: Interview object (without id, createdAt, and updatedAt)
- **Response**: Created interview object with id, createdAt, and updatedAt
- **Error Responses**:
  - `400 Bad Request` - If required fields are missing or invalid
- **Frontend Implementation**: `interviewsApi.create(interview)`
- **Used in Components**: `InterviewForm.vue` (create mode)

#### Update Interview

- **URL**: `/interviews/{id}`
- **Method**: `PUT`
- **Description**: Update an existing interview
- **URL Parameters**: `id=[string]` - ID of the interview to update
- **Request Body**: Interview object (partial updates allowed)
- **Response**: Updated interview object
- **Error Responses**:
  - `404 Not Found` - If interview with the specified ID does not exist
  - `400 Bad Request` - If required fields are missing or invalid
- **Frontend Implementation**: `interviewsApi.update(id, interview)`
- **Used in Components**: `InterviewForm.vue` (edit mode)

#### Update Interview Status

- **URL**: `/interviews/{id}/status`
- **Method**: `PATCH`
- **Description**: Update only the status of an interview
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
  - `400 Bad Request` - If status is invalid
- **Frontend Implementation**: `interviewsApi.updateStatus(id, status)`
- **Used in Components**: `InterviewDetail.vue`

#### Delete Interview

- **URL**: `/interviews/{id}`
- **Method**: `DELETE`
- **Description**: Delete an interview
- **URL Parameters**: `id=[string]` - ID of the interview to delete
- **Response**: Status 204 (No Content)
- **Error Responses**:
  - `404 Not Found` - If interview with the specified ID does not exist
- **Frontend Implementation**: `interviewsApi.delete(id)`
- **Used in Components**: `Interviews.vue`, `InterviewDetail.vue`

### Documents

#### Get All Documents

- **URL**: `/documents`
- **Method**: `GET`
- **Description**: Retrieve all documents
- **Response**: Array of Document objects
- **Frontend Implementation**: `documentsApi.getAll()`
- **Used in Components**: `Documents.vue`

#### Upload Document

- **URL**: `/documents`
- **Method**: `POST`
- **Description**: Upload a new document
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
- **URL Parameters**: `id=[string]` - ID of the document
- **Response**: Document object
- **Error Responses**:
  - `404 Not Found` - If document with the specified ID does not exist
- **Frontend Implementation**: `documentsApi.getById(id)`
- **Used in Components**: `Documents.vue`

#### Delete Document

- **URL**: `/documents/{id}`
- **Method**: `DELETE`
- **Description**: Delete a document
- **URL Parameters**: `id=[string]` - ID of the document to delete
- **Response**: Status 204 (No Content)
- **Error Responses**:
  - `404 Not Found` - If document with the specified ID does not exist
- **Frontend Implementation**: `documentsApi.delete(id)`
- **Used in Components**: `Documents.vue`, `InterviewForm.vue`

### User Settings

#### Get User Settings

- **URL**: `/user/settings`
- **Method**: `GET`
- **Description**: Retrieve the current user's settings
- **Response**: User object
- **Frontend Implementation**: `userSettingsApi.get()`
- **Used in Components**: `Settings.vue`

#### Update User Settings

- **URL**: `/user/settings`
- **Method**: `PUT`
- **Description**: Update the current user's settings
- **Request Body**: Partial User object
  ```json
  {
    "name": "User Name",
    "email": "user@example.com",
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
- **Response**: Updated User object
- **Error Responses**:
  - `400 Bad Request` - If required fields are missing or invalid
- **Frontend Implementation**: `userSettingsApi.update(settings)`
- **Used in Components**: `Settings.vue`

#### Reset User Settings

- **URL**: `/user/settings/reset`
- **Method**: `POST`
- **Description**: Reset user settings to default values
- **Response**: User object with default settings
- **Frontend Implementation**: `userSettingsApi.resetSettings()`
- **Used in Components**: `Settings.vue`

#### Export User Data

- **URL**: `/user/export`
- **Method**: `GET`
- **Description**: Export all user data (interviews, documents, settings)
- **Response**: Binary file (JSON format)
- **Frontend Implementation**: `userSettingsApi.exportData()`
- **Used in Components**: `Settings.vue`

### Statistics

#### Get Interview Statistics

- **URL**: `/statistics/interviews`
- **Method**: `GET`
- **Description**: Get interview statistics
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

1. All mock data should be replaced with actual API calls to the backend.
2. The backend should implement all endpoints described in this document.
3. Error handling should be consistent across all endpoints.
4. For file uploads, consider implementing progress indicators and file size limits.
5. User settings should be persisted in the backend database.
6. Consider implementing caching strategies for frequently accessed data like user settings.
7. Ensure proper validation of all input data both on the frontend and backend.
