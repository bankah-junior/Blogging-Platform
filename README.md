# Smart Blogging Platform – Database Fundamentals Project

## Overview
The Smart Blogging Platform is a data-driven blogging system designed to demonstrate strong understanding of **database design, normalization, performance optimization, and JavaFX–database integration**.

The project focuses on building a robust **data layer and persistence logic** that supports core blogging operations such as post creation, comment management, tagging, searching, sorting, and performance analytics.

This project emphasizes **engineering correctness, scalability, and performance**, rather than UI complexity.

---

## Homepage Screenshot
![Homepage Screenshot](homepage.png)

---

## Features

### Core Functionality
- ✅ User authentication (login/register)
- ✅ Create, read, update, and delete blog posts
- ✅ Comment management on posts
- ✅ Tag assignment and management
- ✅ Search posts by title, tag, or author
- ✅ Sort posts by date or title (ascending/descending)
- ✅ Performance analytics dashboard
- ✅ Caching for improved performance
- ✅ Database indexing for optimized queries

### Technical Highlights
- **Database**: MongoDB (NoSQL)
- **UI Framework**: JavaFX
- **Architecture**: Layered design (Controller → Service → DAO)
- **Caching**: LRU Cache implementation
- **Performance**: Indexed queries, in-memory sorting, optimized search

---

## Prerequisites

Before running this project, ensure you have the following installed:

1. **Java Development Kit (JDK) 21 or higher**
   - Download from: https://www.oracle.com/java/technologies/downloads/
   - Verify installation: `java -version`

2. **Maven 3.6+**
   - Download from: https://maven.apache.org/download.cgi
   - Verify installation: `mvn -version`

3. **MongoDB 5.0+**
   - Download from: https://www.mongodb.com/try/download/community
   - MongoDB should be running on `localhost:27017`

4. **IDE (Optional but recommended)**
   - IntelliJ IDEA, Eclipse, or VS Code with Java extensions

---

## Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd NoSQL-Blog-App
```

### 2. Start MongoDB

**Windows:**
```bash
# Navigate to MongoDB bin directory
cd C:\Program Files\MongoDB\Server\<version>\bin
mongod.exe
```

**macOS/Linux:**
```bash
# Using Homebrew (macOS)
brew services start mongodb-community

# Or manually
mongod --dbpath /path/to/data/directory
```

Verify MongoDB is running:
```bash
mongosh
# or
mongo
```

### 3. Configure Database Connection

The application connects to MongoDB at `mongodb://127.0.0.1:27017` by default.

If your MongoDB is running on a different host/port, update the connection string in:
```
src/main/java/com/amalitech/nosqlblog/utils/MongoDBConnection.java
```

### 4. Build the Project

```bash
# Clean and compile
mvn clean compile

# Or build with tests
mvn clean install
```

### 5. Run the Application

**Option 1: Using Maven**
```bash
mvn clean javafx:run
```

**Option 2: Using Java directly**
```bash
# Compile first
mvn clean compile

# Run the launcher
java --module-path <path-to-javafx> --add-modules javafx.controls,javafx.fxml -cp target/classes com.amalitech.nosqlblog.Launcher
```

**Option 3: Using IDE**
- Open the project in your IDE
- Run the `Launcher.java` class (located in `src/main/java/com/amalitech/nosqlblog/`)

---

## Database Setup

The application will automatically create the following collections on first run:
- `users` - User accounts
- `posts` - Blog posts
- `comments` - Post comments
- `tags` - Post tags
- `post_tags` - Post-tag relationships
- `reviews` - Post reviews

Indexes are automatically created when the DAO classes are instantiated.

### Manual Database Setup (Optional)

If you want to set up the database manually, you can use the MongoDB shell:

```javascript
use java-demo

// Create indexes
db.users.createIndex({ username: 1 }, { unique: true })
db.users.createIndex({ email: 1 }, { unique: true })
db.posts.createIndex({ authorId: 1 })
db.posts.createIndex({ title: "text" })
db.posts.createIndex({ published: 1 })
db.comments.createIndex({ postId: 1 })
db.comments.createIndex({ userId: 1 })
db.tags.createIndex({ name: 1 }, { unique: true })
db.post_tags.createIndex({ postId: 1 })
db.post_tags.createIndex({ tagId: 1 })
db.post_tags.createIndex({ postId: 1, tagId: 1 }, { unique: true })
db.reviews.createIndex({ postId: 1 })
db.reviews.createIndex({ userId: 1 })
db.reviews.createIndex({ postId: 1, userId: 1 }, { unique: true })
```

---

## Project Structure

```
NoSQL-Blog-App/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/amalitech/nosqlblog/
│   │   │       ├── cache/          # Caching implementation
│   │   │       ├── controller/     # JavaFX controllers
│   │   │       ├── dao/            # Data Access Objects
│   │   │       ├── model/          # Domain models
│   │   │       ├── service/         # Business logic
│   │   │       └── utils/          # Utility classes
│   │   └── resources/
│   │       └── com/amalitech/nosqlblog/
│   │           └── *.fxml         # JavaFX UI files
│   └── test/                       # Test files
├── docs/
│   ├── DATABASE_DESIGN.md          # Database schema documentation
│   ├── PERFORMANCE_REPORT.md       # Performance analysis
│   └── THOUGHTPROCESS.md           # Design decisions
├── pom.xml                         # Maven configuration
└── README.md                       # This file
```

---

## Usage Guide

### 1. Register a New User
1. Launch the application
2. Click "Register" on the login screen
3. Enter username, email, and password
4. Click "Register" to create account

### 2. Login
1. Enter your email and password
2. Click "Login"

### 3. Create a Post
1. Click "New Post" button in the home screen
2. Enter post title and content
3. Optionally add tags (comma-separated)
4. Check "Published" to make post visible
5. Click "Save Post"

### 4. Search Posts
1. Enter search term in the search field
2. Click "Search" or press Enter
3. Results are filtered by title

### 5. Sort Posts
1. Select sort criteria from "Sort by" dropdown (Date or Title)
2. Select sort order (Ascending or Descending)
3. Posts are automatically sorted

### 6. View Analytics
1. Click "Analytics" button in the home screen
2. View performance metrics and optimization details

### 7. Edit/Delete Post
1. Click "Edit" or "Delete" button on a post (only visible for your own posts)
2. Edit: Modify post in the editor dialog
3. Delete: Confirm deletion in the confirmation dialog

### 8. Add Comments
1. Enter comment text in the comment field below a post
2. Click "Post" to submit comment

---

## Dependencies

The project uses the following key dependencies (see `pom.xml` for complete list):

- **JavaFX 21.0.6** - UI framework
- **MongoDB Java Driver 5.6.2** - MongoDB connectivity
- **SLF4J 2.0.17** - Logging framework
- **Logback 1.5.23** - Logging implementation
- **JBCrypt 0.4** - Password hashing

---

## Performance Optimizations

### Database Indexing
- Indexes on frequently queried fields (authorId, title, postId, etc.)
- Text index for full-text search
- Compound indexes for multi-field queries

### Caching
- LRU Cache with capacity of 50 posts
- Automatic cache invalidation on updates
- 40-60% faster retrieval for cached data

### Algorithmic Optimizations
- In-memory sorting using TimSort
- Efficient search using indexed queries
- Optimized data structures for caching

See `docs/PERFORMANCE_REPORT.md` for detailed performance analysis.

---

## Testing

### Running Tests
```bash
mvn test
```

### Manual Testing Checklist
- [ ] User registration and login
- [ ] Create, edit, and delete posts
- [ ] Search functionality
- [ ] Sorting functionality
- [ ] Comment creation
- [ ] Tag assignment
- [ ] Analytics dashboard

---

## Troubleshooting

### MongoDB Connection Issues
- **Error**: "Failed to connect to MongoDB"
  - **Solution**: Ensure MongoDB is running on `localhost:27017`
  - Check MongoDB service status
  - Verify connection string in `MongoDBConnection.java`

### JavaFX Runtime Issues
- **Error**: "JavaFX runtime components are missing"
  - **Solution**: Ensure JavaFX dependencies are properly downloaded
  - Run `mvn clean install` to download dependencies
  - For Java 11+, JavaFX may need to be added separately

### Port Already in Use
- **Error**: "Address already in use"
  - **Solution**: Stop other MongoDB instances or change MongoDB port

---

## Documentation

- **Database Design**: See `docs/DATABASE_DESIGN.md`
- **Performance Report**: See `docs/PERFORMANCE_REPORT.md`
- **Design Decisions**: See `docs/THOUGHTPROCESS.md`

---

## Future Enhancements

Potential improvements for future versions:
- Pagination for large post lists
- User profiles and avatars
- Post categories
- Rich text editor for post content
- Image upload support
- Email notifications
- Advanced search filters
- Export functionality

---

## Author

**Anthony Bekoe Bankah**  
Smart Blogging Platform – Database Fundamentals

---

## License

This project is part of an educational assignment.

---

## Acknowledgments

- MongoDB for the excellent NoSQL database
- JavaFX team for the UI framework
- Open source community for various libraries
