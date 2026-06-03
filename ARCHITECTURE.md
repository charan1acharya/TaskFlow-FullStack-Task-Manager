# Task Manager Project Architecture

## 1. Overview

This project is a full-stack Task Manager application. It has a React frontend and a Spring Boot backend.

The application allows users to:

- Register an account
- Log in
- Store a JWT token in the browser
- Create, view, update, complete, search, filter, and delete tasks
- Generate task suggestions or topic guides using AI

High-level architecture:

```text
Browser
  |
  | React UI: task-frontend
  | Axios client with JWT from localStorage
  v
Spring Boot REST API: taskmanager, port 8081
  |
  | SecurityConfig + JwtFilter
  v
Controllers
  |
  v
Services
  |
  v
Repositories
  |
  v
MySQL by default, H2 optional

AI path:
React AiAssistant -> /ai/generate -> AiSuggestionService -> Grok or Ollama
```

## 2. Root Project Structure

```text
D:\task_manager
+-- package.json
+-- package-lock.json
+-- README.md
+-- walkthrough.md
+-- tailwind.config.js
+-- postcss.config.js
+-- task-frontend
|   +-- package.json
|   +-- public
|   +-- src
|       +-- App.js
|       +-- App.css
|       +-- App.test.js
|       +-- index.js
|       +-- index.css
|       +-- services
|       |   +-- api.js
|       +-- components
|           +-- Login.js
|           +-- AddTask.js
|           +-- TaskList.js
|           +-- AiAssistant.js
+-- taskmanager
    +-- pom.xml
    +-- mvnw
    +-- mvnw.cmd
    +-- src
        +-- main
        |   +-- java
        |   |   +-- com
        |   |       +-- taskmanager
        |   |           +-- TaskmanagerApplication.java
        |   |           +-- config
        |   |           +-- controller
        |   |           +-- model
        |   |           +-- repository
        |   |           +-- service
        |   +-- resources
        |       +-- application.properties
        |       +-- application-mysql.properties
        |       +-- application-h2.properties
        +-- test
            +-- java
                +-- com
                    +-- taskmanager
                        +-- TaskmanagerApplicationTests.java
```

## 3. Technology Stack

### Frontend

| Technology | Purpose |
| --- | --- |
| React | Builds the browser UI |
| React DOM | Renders React components |
| Create React App | Frontend build setup |
| Axios | Sends HTTP requests to backend |
| Tailwind CSS | Styling |
| Lucide React | UI icons |
| Jest / React Testing Library | Frontend tests |

### Backend

| Technology | Purpose |
| --- | --- |
| Java 17 | Backend language |
| Spring Boot 4.0.6 | Backend framework |
| Spring Web MVC | REST APIs |
| Spring Data JPA | Database access |
| Spring Security | API protection |
| JWT / JJWT | Token authentication |
| MySQL | Default database |
| H2 | Optional in-memory database |
| Maven | Backend build tool |
| RestTemplate | Calls AI providers |
| Jackson | JSON parsing |

## 4. Frontend Architecture

Frontend location:

```text
task-frontend
```

Main frontend files:

| File | Responsibility |
| --- | --- |
| `src/App.js` | Main dashboard, auth state, task state, filters, stats, edit flow, toast notifications |
| `src/services/api.js` | Axios API client and request helper functions |
| `src/components/Login.js` | Login and registration screen |
| `src/components/AddTask.js` | Create-task form |
| `src/components/TaskList.js` | Task card list with complete, edit, and delete actions |
| `src/components/AiAssistant.js` | AI task suggestion and topic guide interface |
| `src/index.js` | React application entry point |
| `src/index.css` | Tailwind and global styles |

Frontend runtime flow:

```text
User opens React app
  |
  v
App.js checks localStorage for token
  |
  +-- No token -> show Login.js
  |
  +-- Token exists -> call getTasks()
                     -> render dashboard
```

The frontend uses Axios with this base URL:

```javascript
baseURL: 'http://localhost:8081'
```

Every request includes the JWT token when it exists:

```text
Authorization: Bearer <token>
```

Frontend API functions:

```javascript
getTasks()
createTask(task)
deleteTask(id)
updateTask(id, task)
generateSuggestions(goal, mode)
login(username, password)
register(username, password)
```

## 5. Backend Architecture

Backend location:

```text
taskmanager
```

Backend runs on:

```text
http://localhost:8081
```

The backend follows a layered Spring Boot architecture:

```text
Controller layer
  Receives HTTP requests and returns HTTP responses

Service layer
  Contains application logic

Repository layer
  Performs database operations through Spring Data JPA

Model layer
  Defines entities and request/response objects
```

Backend package structure:

```text
com.taskmanager
+-- TaskmanagerApplication.java
+-- config
+-- controller
+-- model
+-- repository
+-- service
```

## 6. Backend Packages

### `config`

| File | Responsibility |
| --- | --- |
| `SecurityConfig.java` | Configures Spring Security, JWT filter, stateless sessions, CORS, and protected routes |
| `JwtFilter.java` | Reads the `Authorization` header, validates JWT, and sets authentication |
| `JwtUtil.java` | Generates, validates, and reads JWT tokens |
| `AppConfig.java` | Provides the `RestTemplate` bean used by AI services |

### `controller`

| File | Responsibility |
| --- | --- |
| `AuthController.java` | Handles user registration and login |
| `TaskController.java` | Handles task CRUD endpoints |
| `AiController.java` | Handles AI generation endpoint |
| `HelloController.java` | Simple backend test endpoint |

### `model`

| File | Responsibility |
| --- | --- |
| `User.java` | User JPA entity |
| `Task.java` | Task JPA entity |
| `AiGenerateRequest.java` | Request body for AI generation |
| `AiGenerateResponse.java` | Response body from AI generation |

### `repository`

| File | Responsibility |
| --- | --- |
| `UserRepository.java` | User database access |
| `TaskRepository.java` | Task database access |

### `service`

| File | Responsibility |
| --- | --- |
| `TaskService.java` | Task CRUD business logic |
| `AiSuggestionService.java` | Selects configured AI provider |
| `AiSuggestionProvider.java` | Common interface for AI providers |
| `GrokAiService.java` | Grok API integration |
| `OllamaAiService.java` | Local Ollama integration |
| `TaskSuggestionParser.java` | Parses AI response text into task suggestions |

## 7. API Architecture

### Authentication APIs

| Method | Endpoint | Description | Auth Required |
| --- | --- | --- | --- |
| `POST` | `/auth/register` | Creates a new user | No |
| `POST` | `/auth/login` | Logs in user and returns JWT token | No |

### Task APIs

| Method | Endpoint | Description | Auth Required |
| --- | --- | --- | --- |
| `GET` | `/tasks` | Gets all tasks | Yes |
| `POST` | `/tasks` | Creates a task | Yes |
| `PUT` | `/tasks/{id}` | Updates a task | Yes |
| `DELETE` | `/tasks/{id}` | Deletes a task | Yes |

### AI API

| Method | Endpoint | Description | Auth Required |
| --- | --- | --- | --- |
| `POST` | `/ai/generate` | Generates task suggestions or topic guide | Yes |

### Test API

| Method | Endpoint | Description | Auth Required |
| --- | --- | --- | --- |
| `GET` | `/hello` | Returns backend health text | Yes |

## 8. Authentication Flow

```text
1. User registers or logs in from React.
2. Frontend sends username and password to backend.
3. Backend validates credentials.
4. Backend generates JWT token.
5. Frontend stores token in localStorage.
6. Axios attaches token to protected requests.
7. JwtFilter validates token.
8. If valid, Spring Security allows the request.
9. If invalid or missing, protected APIs return unauthorized/forbidden responses.
```

Security configuration:

```text
/auth/** is public
OPTIONS /** is public
All other routes require authentication
```

JWT token lifetime:

```text
1 hour
```

## 9. Data Model

### User

```text
id: Long
username: String, unique
password: String
```

Database table:

```text
users
```

### Task

```text
id: Long
title: String
description: String
status: String
```

Current status values used by the frontend:

```text
Pending
Completed
```

### AI Request

```text
goal: String
mode: String
```

Supported frontend modes:

```text
suggestions
topic
```

### AI Response

```text
suggestions: List<String>
answer: String
rawText: String
provider: String
model: String
mode: String
```

## 10. Database Architecture

Default database profile:

```properties
spring.profiles.default=mysql
```

MySQL configuration:

```properties
spring.datasource.url=${MYSQL_URL:jdbc:mysql://localhost:3306/taskdb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC}
spring.datasource.username=${MYSQL_USER:root}
spring.datasource.password=${MYSQL_PASSWORD:}
```

H2 configuration:

```properties
spring.datasource.url=jdbc:h2:mem:taskdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
```

JPA schema setting:

```properties
spring.jpa.hibernate.ddl-auto=update
```

## 11. AI Architecture

AI request flow:

```text
AiAssistant.js
  |
  v
generateSuggestions(goal, mode)
  |
  v
POST /ai/generate
  |
  v
AiController
  |
  v
AiSuggestionService
  |
  +-- GrokAiService
  |
  +-- OllamaAiService
  |
  v
AiGenerateResponse
```

AI provider selection:

```properties
ai.provider=${AI_PROVIDER:grok}
```

### Grok

Configuration:

```properties
grok.api.key=${GROK_API_KEY:${XAI_API_KEY:}}
grok.api.model=grok-4.3
grok.api.base-url=https://api.x.ai/v1
```

Grok endpoint called by backend:

```text
{grok.api.base-url}/chat/completions
```

### Ollama

Configuration:

```properties
ollama.api.base-url=${OLLAMA_BASE_URL:http://localhost:11434}
ollama.api.model=${OLLAMA_MODEL:phi3:latest}
```

Ollama endpoint called by backend:

```text
{ollama.api.base-url}/api/chat
```

## 12. Main User Flow

```text
Open http://localhost:3000
  |
  v
Login or Register
  |
  v
Receive JWT from backend
  |
  v
Store JWT in localStorage
  |
  v
Load dashboard
  |
  v
Fetch tasks from /tasks
  |
  v
Create, edit, complete, delete, search, and filter tasks
  |
  v
Optionally call /ai/generate for suggestions or topic guide
```

## 13. Request Flow Examples

### Login

```text
Login.js
  -> api.login(username, password)
  -> POST /auth/login
  -> AuthController.login()
  -> UserRepository.findByUsername()
  -> JwtUtil.generateToken()
  -> token returned to React
  -> token saved in localStorage
```

### Get Tasks

```text
App.js
  -> getTasks()
  -> Axios adds Authorization header
  -> GET /tasks
  -> JwtFilter validates token
  -> TaskController.getTasks()
  -> TaskService.getAllTasks()
  -> TaskRepository.findAll()
  -> task list returned to React
```

### Create Task

```text
AddTask.js or AiAssistant.js
  -> App.js handleAddTask()
  -> createTask(task)
  -> POST /tasks
  -> JwtFilter validates token
  -> TaskController.addTask()
  -> TaskService.saveTask()
  -> TaskRepository.save()
  -> saved task returned
```

### Generate AI Suggestions

```text
AiAssistant.js
  -> generateSuggestions(goal, mode)
  -> POST /ai/generate
  -> JwtFilter validates token
  -> AiController.generate()
  -> AiSuggestionService.generate()
  -> GrokAiService or OllamaAiService
  -> TaskSuggestionParser
  -> AiGenerateResponse returned to React
```

## 14. Run Commands

Start frontend:

```powershell
cd D:\task_manager
npm start
```

Start backend with default MySQL profile:

```powershell
cd D:\task_manager
npm run backend
```

Start backend with explicit MySQL profile:

```powershell
cd D:\task_manager
npm run backend:mysql
```

Start backend with H2 profile:

```powershell
cd D:\task_manager
npm run backend:h2
```

Run frontend tests:

```powershell
cd D:\task_manager
npm test
```

Run backend tests:

```powershell
cd D:\task_manager\taskmanager
.\mvnw.cmd test
```

Build frontend:

```powershell
cd D:\task_manager\task-frontend
npm run build
```

Build backend:

```powershell
cd D:\task_manager\taskmanager
.\mvnw.cmd clean package
```

## 15. Current Limitations

- Passwords are currently stored as plain text.
- JWT secret is hard-coded in `JwtUtil.java`.
- Tasks are not linked to users.
- Any authenticated user can access the shared task list.
- The frontend has due-date display logic, but the backend `Task` entity does not currently have a due-date field.
- There is no refresh-token flow.
- There are no database migration scripts.
- Backend test coverage is minimal.

## 16. Suggested Improvements

- Add BCrypt password hashing.
- Move JWT secret to an environment variable.
- Link tasks to users with a relationship such as `Task.owner`.
- Restrict task APIs so users only access their own tasks.
- Add validation for user and task input.
- Add due date, priority, and category fields.
- Add backend controller and service tests.
- Add database migrations with Flyway or Liquibase.
- Add deployment configuration.
- Add refresh tokens for longer sessions.

## 17. Final Summary

This project uses a clear full-stack architecture:

```text
React frontend
  -> Axios API client
  -> JWT authentication
  -> Spring Boot REST controllers
  -> Service layer
  -> Spring Data JPA repositories
  -> MySQL or H2 database
  -> Optional Grok or Ollama AI integration
```

It is a good portfolio-style full-stack project because it demonstrates authentication, REST API development, database persistence, frontend state management, dashboard UI design, and AI API integration.
