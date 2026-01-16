
---

## Running the Application

  
- ## AI Integration

The application exposes a backend endpoint that receives user messages and stores them in the database.
Each message is then forwarded to an external AI workflow service, which generates a response.
The response is saved and returned to the client.

This approach keeps AI logic external to the core application while preserving conversation history and data consistency.


### Steps

1. Open the project folder in **IntelliJ IDEA**
2. Configure the database connection in `application.properties`:
   - database URL
   - username
   - password
3. Let IntelliJ resolve Maven dependencies
4. Run the main Spring Boot application class using the IDE

The application will start locally and connect to the configured database.
The application starts locally on http://localhost:8080 .

---

## Database Management

The application uses a **relational PostgreSQL database** with normalized tables and constraints.

**Main entities include:**

- users  
- student_profiles  
- conversation_threads  
- chat_messages  
- requests  
- message_log  

**SQL views** are defined to simplify data access and reporting, such as:

- student request summaries  
- conversation summaries  
- open requests filtering  

---

## Main Features

### Authentication and Authorization
- User registration and login
- Role-based access control (`STUDENT` / `ADMIN`)

### Conversation Management
- Students can create conversation threads
- Messages are stored and linked to threads
- AI responses are logged for later inspection

### Request Management
- Students can submit requests with a specific type and message
- Administrators can view, update status, and respond to requests

### Data Persistence
- All data is stored in a relational database
- Integrity enforced using primary keys, foreign keys, and constraints

---

## Development Notes

- Dependency management is handled by **Maven**
- The project follows a **layered architecture**:
  - controller
  - service
  - repository
- The application can be extended with additional features such as notifications or analytics
##  Running the Application

### Steps
1. Open the project folder in **IntelliJ IDEA**
2. Configure the database connection in `application.properties`:
   - database URL  
   - username  
   - password  
3. Allow IntelliJ to resolve Maven dependencies
4. Run the main Spring Boot application class using the IDE

The application starts locally on the default Spring Boot port (`http://localhost:8080`) and connects to the configured PostgreSQL database.
---

## Development Notes

- Dependency management is handled by **Maven**
- The project follows a **layered architecture**:
  - controller  
  - service  
  - repository  



