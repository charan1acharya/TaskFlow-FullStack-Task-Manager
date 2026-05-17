# Task Manager Full-Stack Project Report

## 1. Project Overview

This project is a full-stack Task Manager web application. It allows users to register, log in, manage tasks, track completion progress, and generate task suggestions using Grok AI or local Ollama.

The application has two main parts:

- Backend: Spring Boot REST API
- Frontend: React dashboard interface

The backend handles authentication, JWT security, task storage, and AI suggestion generation. The frontend provides the user interface for login, registration, task management, filtering, progress tracking, and AI-powered suggestions.

## 2. Main Features

- User registration
- User login
- JWT-based authentication
- Protected task APIs
- Create tasks
- View tasks
- Edit tasks
- Delete tasks
- Mark tasks as completed
- Search tasks by title or description
- Filter tasks by status
- Dashboard statistics
- AI task suggestion generator using Grok or Ollama
- Toast notifications for user actions
- Responsive modern UI using Tailwind CSS

## 3. Tech Stack Used

### Frontend

| Technology | Purpose |
| --- | --- |
| React | Builds the user interface |
| React DOM | Renders React components in the browser |
| Create React App | Frontend build and development setup |
| Tailwind CSS | Utility-first styling framework |
| Axios | Sends HTTP requests to the backend |
| Lucide React | Provides icons used in buttons, cards, and dashboard UI |
| React Testing Library | Tests frontend components |
| Jest | Runs frontend tests |
| Web Vitals | Optional frontend performance reporting |

### Backend

| Technology | Purpose |
| --- | --- |
| Java 17 | Backend programming language |
| Spring Boot 4.0.6 | Main backend framework |
| Spring Web MVC | Builds REST API endpoints |
| Spring Data JPA | Handles database operations through repositories |
| Spring Security | Protects backend routes |
| JWT / JJWT | Generates and validates JSON Web Tokens |
| H2 Database | In-memory development database |
| Maven | Backend dependency management and build tool |
| RestTemplate | Calls the configured AI provider |
| Jackson | Parses JSON responses from AI providers |

### AI Integration

| Technology | Purpose |
| --- | --- |
| Grok API | Cloud task suggestion provider |
| Ollama | Local task suggestion provider |
| `AI_PROVIDER` | Selects `grok` or `ollama` |
| `GROK_API_KEY` | Environment variable used for Grok authentication |

## 4. Project Folder Structure

```text
D:\task_manager
+-- README.md
+-- walkthrough.md
+-- package.json
+-- package-lock.json
+-- tailwind.config.js
+-- postcss.config.js
+-- taskmanager
|   +-- pom.xml
|   +-- mvnw
|   +-- mvnw.cmd
|   +-- src
|       +-- main
|       |   +-- java
|       |   |   +-- com
|       |   |       +-- taskmanager
|       |   |           +-- TaskmanagerApplication.java
|       |   |           +-- config
|       |   |           +-- controller
|       |   |           +-- model
|       |   |           +-- repository
|       |   |           +-- service
|       |   +-- resources
|       |       +-- application.properties
|       +-- test
|           +-- java
|               +-- com
|                   +-- taskmanager
|                       +-- TaskmanagerApplicationTests.java
+-- task-frontend
    +-- package.json
    +-- tailwind.config.js
    +-- public
    +-- src
        +-- App.js
        +-- App.test.js
        +-- index.js
        +-- index.css
        +-- components
        |   +-- AddTask.js
        |   +-- AiAssistant.js
        |   +-- Login.js
        |   +-- TaskList.js
        +-- services
            +-- api.js
```

## 5. Backend Details

The backend project is located in:

```text
taskmanager
```

It is a Spring Boot REST API running on:

```text
http://localhost:8081
```

### Backend Configuration

Configuration file:

```text
taskmanager/src/main/resources/application.properties
```

Current important settings:

```properties
spring.application.name=taskmanager
spring.datasource.url=jdbc:h2:mem:taskdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false
spring.jpa.show-sql=false
spring.h2.console.enabled=true
server.port=8081
ai.provider=${AI_PROVIDER:grok}
grok.api.key=${GROK_API_KEY:${XAI_API_KEY:}}
grok.api.model=grok-4.3
grok.api.base-url=https://api.x.ai/v1
ollama.api.base-url=${OLLAMA_BASE_URL:http://localhost:11434}
ollama.api.model=${OLLAMA_MODEL:phi3:latest}
```

Important note: the current application uses H2 in-memory database, not MySQL. Because it is in-memory, task and user data may be lost when the backend restarts.

## 6. Backend Packages

### `config`

Contains backend configuration classes.

| File | Purpose |
| --- | --- |
| `SecurityConfig.java` | Configures Spring Security, CORS, JWT filter, and protected routes |
| `JwtUtil.java` | Generates, validates, and reads JWT tokens |
| `JwtFilter.java` | Reads the `Authorization` header and authenticates requests |
| `AppConfig.java` | Creates the `RestTemplate` bean used for external API calls |

### `controller`

Contains REST API controllers.

| File | Purpose |
| --- | --- |
| `AuthController.java` | Handles registration and login |
| `TaskController.java` | Handles task CRUD operations |
| `AiController.java` | Handles AI task suggestion requests |
| `HelloController.java` | Simple backend health/test endpoint |

### `model`

Contains data models.

| File | Purpose |
| --- | --- |
| `Task.java` | Task entity stored in database |
| `User.java` | User entity stored in database |
| `AiGenerateRequest.java` | Request body for AI suggestion generation |
| `AiGenerateResponse.java` | Response body for AI suggestions |

### `repository`

Contains Spring Data JPA repositories.

| File | Purpose |
| --- | --- |
| `TaskRepository.java` | Database operations for tasks |
| `UserRepository.java` | Database operations for users |

### `service`

Contains business logic.

| File | Purpose |
| --- | --- |
| `TaskService.java` | Task CRUD service logic |
| `AiSuggestionService.java` | Selects the configured AI provider |
| `GrokAiService.java` | Grok API integration |
| `OllamaAiService.java` | Local Ollama integration |
| `TaskSuggestionParser.java` | Parses structured or line-based AI suggestions |

## 7. Backend API Endpoints

### Authentication

| Method | Endpoint | Description | Auth Required |
| --- | --- | --- | --- |
| `POST` | `/auth/register` | Registers a new user | No |
| `POST` | `/auth/login` | Logs in user and returns JWT token | No |

### Tasks

| Method | Endpoint | Description | Auth Required |
| --- | --- | --- | --- |
| `GET` | `/tasks` | Gets all tasks | Yes |
| `POST` | `/tasks` | Creates a new task | Yes |
| `PUT` | `/tasks/{id}` | Updates an existing task | Yes |
| `DELETE` | `/tasks/{id}` | Deletes a task | Yes |

### AI

| Method | Endpoint | Description | Auth Required |
| --- | --- | --- | --- |
| `POST` | `/ai/generate` | Generates task suggestions from a goal | Yes |

### Test Endpoint

| Method | Endpoint | Description | Auth Required |
| --- | --- | --- | --- |
| `GET` | `/hello` | Returns `Backend Working!` | Yes |

## 8. Authentication Flow

1. User registers with username and password.
2. Backend stores the user.
3. User logs in with username and password.
4. Backend validates credentials.
5. Backend generates a JWT token.
6. Frontend stores the token in `localStorage`.
7. Axios adds the token to every API request as:

```text
Authorization: Bearer <token>
```

8. Backend JWT filter validates the token before allowing protected API access.

## 9. Frontend Details

The frontend project is located in:

```text
task-frontend
```

It runs on:

```text
http://localhost:3000
```

### Important Frontend Files

| File | Purpose |
| --- | --- |
| `src/App.js` | Main dashboard, authentication state, task state, filters, stats, edit flow |
| `src/components/Login.js` | Login and registration form |
| `src/components/AddTask.js` | Form for creating tasks |
| `src/components/TaskList.js` | Displays task cards and actions |
| `src/components/AiAssistant.js` | AI suggestion interface |
| `src/services/api.js` | Axios API client and backend request functions |
| `src/index.css` | Tailwind CSS setup and global styles |
| `src/App.test.js` | Frontend tests |

## 10. Frontend API Client

The frontend uses Axios with this base URL:

```javascript
baseURL: 'http://localhost:8081'
```

The API client automatically reads the JWT token from browser `localStorage` and attaches it to requests.

Main API functions:

```javascript
getTasks()
createTask(task)
deleteTask(id)
updateTask(id, task)
generateSuggestions(goal)
login(username, password)
register(username, password)
```

## 11. How To Run The Project

### Prerequisites

Install these tools first:

- Java 17 or newer
- Node.js and npm
- Maven wrapper is already included in the backend project

### Step 1: Install Frontend Dependencies

From the root project folder:

```powershell
cd D:\task_manager\task-frontend
npm install
```

### Step 2: Start Backend

Open a terminal:

```powershell
cd D:\task_manager\taskmanager
.\mvnw.cmd spring-boot:run
```

Backend will start at:

```text
http://localhost:8081
```

### Step 3: Start Frontend

Open another terminal:

```powershell
cd D:\task_manager\task-frontend
npm start
```

Frontend will start at:

```text
http://localhost:3000
```

### Alternative Root Commands

From:

```powershell
cd D:\task_manager
```

Start frontend:

```powershell
npm start
```

Start backend:

```powershell
npm run backend
```

Run frontend tests:

```powershell
npm test
```

## 12. AI Provider Setup

The AI assistant can use either Grok or Ollama. Grok is the default.

### Use Grok

```powershell
$env:AI_PROVIDER="grok"
$env:GROK_API_KEY="your_grok_api_key_here"
cd D:\task_manager\taskmanager
.\mvnw.cmd spring-boot:run
```

You can also set `XAI_API_KEY`, which is the environment variable name used in xAI's official examples. If the key is missing, the AI endpoint returns a service unavailable message. If the Grok request fails after the key is configured, the backend returns the Grok error instead of showing fake fallback suggestions.

### Use Ollama

Start Ollama and pull a local model:

```powershell
ollama serve
ollama pull phi3
```

Then start the backend with Ollama selected:

```powershell
$env:AI_PROVIDER="ollama"
$env:OLLAMA_MODEL="phi3:latest"
cd D:\task_manager\taskmanager
.\mvnw.cmd spring-boot:run
```

## 13. How To Use The Application

1. Start backend on port `8081`.
2. Start frontend on port `3000`.
3. Open `http://localhost:3000`.
4. Click register if you do not have an account.
5. Create a username and password.
6. After registration/login, the dashboard opens.
7. Add tasks using the quick add form.
8. Use search and filters to find tasks.
9. Mark tasks complete, edit tasks, or delete tasks.
10. Use the AI assistant by typing a goal, asking the configured provider for suggestions, and adding useful suggestions as tasks.

## 14. Testing

### Backend Test

Run:

```powershell
cd D:\task_manager\taskmanager
.\mvnw.cmd test
```

Current backend test coverage:

- Spring application context load test

### Frontend Test

Run:

```powershell
cd D:\task_manager\task-frontend
npm test -- --watchAll=false
```

Current frontend test coverage:

- Login screen renders when no token exists
- Dashboard renders when stored token exists
- Expired session returns user to login screen

## 15. Build Commands

### Frontend Production Build

```powershell
cd D:\task_manager\task-frontend
npm run build
```

Build output folder:

```text
task-frontend/build
```

### Backend Build

```powershell
cd D:\task_manager\taskmanager
.\mvnw.cmd clean package
```

Build output folder:

```text
taskmanager/target
```

## 16. Database Details

Current database:

```text
H2 in-memory database
```

Database URL:

```text
jdbc:h2:mem:taskdb
```

H2 console is enabled:

```properties
spring.h2.console.enabled=true
```

Because the database is in-memory, it is best for development and testing. For production or long-term storage, switch to a persistent database such as MySQL or PostgreSQL.

## 17. Security Notes

Current security implementation:

- JWT authentication is enabled.
- `/auth/**` routes are public.
- All other backend routes require authentication.
- Frontend sends JWT tokens through the `Authorization` header.
- CORS allows the React frontend at `http://localhost:3000`.

Important improvements needed before production:

- Passwords are currently stored as plain text.
- Use BCrypt password hashing before saving users.
- Move the JWT secret out of source code and into environment variables.
- Add user-based task ownership so each user only sees their own tasks.
- Add input validation for usernames, passwords, and tasks.
- Use a persistent database.

## 18. Current Limitations

- Tasks are not linked to a specific user.
- Any authenticated user can access the shared task list.
- Passwords are not encrypted.
- H2 database data is temporary.
- AI suggestions depend on either a configured Grok API key or a running local Ollama model.
- `walkthrough.md` mentions MySQL, but the current project uses H2.

## 19. Suggested Future Enhancements

- Add BCrypt password hashing.
- Add task ownership by user.
- Add due date support in backend model.
- Add priority field for tasks.
- Add categories or tags.
- Add pagination for large task lists.
- Add stronger backend validation.
- Add controller tests for authentication and tasks.
- Add deployment configuration.
- Add persistent database profile for MySQL or PostgreSQL.
- Add refresh token flow for longer sessions.

## 20. Final Summary

This project is a full-stack Task Manager application built with Spring Boot and React. It demonstrates user authentication, JWT-protected APIs, task CRUD operations, a modern responsive dashboard, and configurable AI integration for productivity suggestions.

It is a good learning and portfolio project because it covers important full-stack concepts:

- REST API development
- Frontend and backend integration
- Authentication and authorization
- Database persistence with JPA
- React state management
- API calls with Axios
- Tailwind CSS UI design
- AI API integration
- Testing basics

The project currently works well as a development prototype. Before using it in production, improve password security, user-specific data isolation, environment-based secrets, and persistent database storage.
