# Full-Stack Authentication Walkthrough 🚀

We successfully built a complete JSON Web Token (JWT) authentication system using Spring Boot (Backend), React (Frontend), and MySQL! 

We tackled standard issues surrounding CORS policies, 401/403 access rejections, and correct HTTP header usage on the frontend. The complete system stores your JWT via `localStorage` tightly integrated with `axios` request interceptors, to automatically attach tokens to API calls.

---

## What We Modified

### 1. Spring Security Configuration (`SecurityConfig.java`)
Configured to turn off standard session creation (stateless), allowed CORS specifically for standard frontend domains (`http://localhost:3000`), and guarded API routes so `/auth/login` and `register` are open while all other endpoints require authorization.

### 2. JWT Request Filter (`JwtFilter.java`)
Added the critical logic missing in standard setups: extracting the JWT text, using `JwtUtil` to grab the username string out of it, and actively putting that `UsernamePasswordAuthenticationToken` item into the overarching `SecurityContextHolder`. This is what solves the `403 Forbidden` wall!

### 3. Controller Actions and Passwords (`AuthController.java` & `User.java`)
Adapted authentication routes to gracefully handle database validations like duplicate users constraint (`@Column(unique = true)`), returning appropriate status codes like `401 Unauthorized` or `400 Bad Request` packed securely through `ResponseEntity`.

### 4. Frontend Resilience (`Login.js` & `api.js`)
We fortified the React login elements to correctly grab `axios` errors gracefully and interpret HTTP `401 Unauthorized` gracefully into user-friendly validation hints, storing the acquired token directly into your dashboard components and pushing it dynamically via `interceptors`.

---

> [!TIP]
> The backend application currently uses plain-text passwords for the sake of simplicity and prototyping. For a production environment, you should add a `BCryptPasswordEncoder` bean in the `SecurityConfig` and encode inputs in `AuthController` accordingly.

---

## Step-by-Step Instructions to Run 🏃‍♂️

### 1. Ready your Database
Make sure you have an active MySQL server instance up and running locally since JPA is configured for port `3306`.
- Based on `application.properties`, you are targeting a database called **`taskdb`** (i.e., `jdbc:mysql://localhost:3306/taskdb`).
- **Required Action:** Run `CREATE DATABASE taskdb;` if you haven't yet, keeping `root` | `990230@charan` credentials accessible.

### 2. Start the Spring Boot Backend
Open a terminal located in your workspace `taskmanager` folder:
```bash
cd taskmanager
.\mvnw.cmd spring-boot:run
```
The server will initialize on port **`8081`**. The MySQL tables (specifically `user` and `task` schemas) will be auto-generated for you by Hibernate JPA (`ddl-auto=update`).

### 3. Start the React Frontend
Open a separate terminal and navigate to your `task-frontend` folder to install deps and run:
```bash
cd task-frontend
npm start
```
The application will spring into life at `http://localhost:3000`.

### 4. End-to-End Test
- Launch the browser to `localhost:3000`.
- Use the **Register** form to create a brand new account first.
- The interface will automatically transition to **Sign In** and load the dashboard.
- Create a few tasks! You can confirm looking through the Chrome DevTools `Application -> Local Storage` tab that your JWT token is actively attached under the key `token` and being pushed as a `Bearer` authorization header.

Enjoy your secure Full-Stack Task Manager architecture! Feel free to ask if you'd like me to start exploring additional features!
