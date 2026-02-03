# 📘 DATABASE_DESIGN.md

## 1. 📌 Overview

This document describes the **logical and physical database design** for the NoSQL Blog Platform.
The system is designed using **MongoDB (document-based NoSQL)** with a focus on:

* Scalability
* Read performance
* Clear separation of concerns
* Minimal duplication with controlled denormalization
* Industry best practices

### Core Collections

1. Users
2. Posts
3. Comments
4. Tags
5. Reviews

Each collection is designed to be **independently scalable** while supporting efficient querying.

---

## 2. 👤 Users Collection

### Purpose

Stores registered users of the platform.

### Collection Name

```
users
```

### Document Structure

```json
{
  "_id": ObjectId("..."),
  "username": "john_doe",
  "email": "john@example.com",
  "passwordHash": "$2a$10$...",
  "createdAt": 1700000000000,
  "updatedAt": 1700001000000
}
```

### Field Description

| Field          | Type     | Description            |
| -------------- | -------- | ---------------------- |
| `_id`          | ObjectId | Primary key            |
| `username`     | String   | Unique username        |
| `email`        | String   | Unique email           |
| `passwordHash` | String   | BCrypt-hashed password |
| `createdAt`    | Long     | Epoch timestamp        |
| `updatedAt`    | Long     | Epoch timestamp        |

### Indexes

```js
db.users.createIndex({ username: 1 }, { unique: true })
db.users.createIndex({ email: 1 }, { unique: true })
```

### Design Notes

* Passwords are **never stored in plain text**
* Unique constraints enforce identity integrity
* Minimal fields to reduce sensitive surface area

---

## 3. 📝 Posts Collection

### Purpose

Stores blog posts created by users.

### Collection Name

```
posts
```

### Document Structure

```json
{
  "_id": ObjectId("..."),
  "authorId": ObjectId("..."),
  "title": "Understanding MongoDB Indexes",
  "content": "Post content here...",
  "published": true,
  "createdAt": 1700000000000,
  "updatedAt": 1700001000000
}
```

### Field Description

| Field       | Type     | Description           |
| ----------- | -------- | --------------------- |
| `_id`       | ObjectId | Primary key           |
| `authorId`  | ObjectId | Reference to Users    |
| `title`     | String   | Post title            |
| `content`   | String   | Post body             |
| `published` | Boolean  | Visibility flag       |
| `createdAt` | Long     | Creation timestamp    |
| `updatedAt` | Long     | Last update timestamp |

### Indexes

```js
db.posts.createIndex({ authorId: 1 })
db.posts.createIndex({ title: "text" })
db.posts.createIndex({ published: 1 })
```

### Design Notes

* Posts reference users by ID (no embedding)
* Text index supports search
* Published flag enables drafts

---

## 4. 💬 Comments Collection

### Purpose

Stores comments on posts.

### Collection Name

```
comments
```

### Document Structure

```json
{
  "_id": ObjectId("..."),
  "postId": ObjectId("..."),
  "userId": ObjectId("..."),
  "content": "Great article!",
  "createdAt": 1700000000000,
  "updatedAt": 1700001000000
}
```

### Field Description

| Field       | Type     | Description        |
| ----------- | -------- | ------------------ |
| `_id`       | ObjectId | Primary key        |
| `postId`    | ObjectId | Reference to Posts |
| `userId`    | ObjectId | Reference to Users |
| `content`   | String   | Comment text       |
| `createdAt` | Long     | Creation timestamp |
| `updatedAt` | Long     | Update timestamp   |

### Indexes

```js
db.comments.createIndex({ postId: 1 })
db.comments.createIndex({ userId: 1 })
```

### Design Notes

* Comments are separated for scalability
* Avoids unbounded array growth in posts
* Enables pagination

---

## 5. 🏷 Tags Collection

### Purpose

Stores reusable tags and post-tag relationships.

### Collection Name

```
tags
```

### Document Structure

```json
{
  "_id": ObjectId("..."),
  "name": "mongodb"
}
```

### Field Description

| Field  | Type     | Description |
| ------ | -------- | ----------- |
| `_id`  | ObjectId | Primary key |
| `name` | String   | Tag name    |

### Indexes

```js
db.tags.createIndex({ name: 1 }, { unique: true })
```

---

### 🔗 Post–Tag Relationship (Join Collection)

#### Collection Name

```
post_tags
```

#### Document Structure

```json
{
  "_id": ObjectId("..."),
  "postId": ObjectId("..."),
  "tagId": ObjectId("...")
}
```

### Design Notes

* Many-to-many relationship
* Prevents tag duplication
* Scales better than embedded arrays

---

## 6. ⭐ Reviews Collection

### Purpose

Stores user reviews and ratings for posts.

### Collection Name

```
reviews
```

### Document Structure

```json
{
  "_id": ObjectId("..."),
  "postId": ObjectId("..."),
  "userId": ObjectId("..."),
  "rating": 5,
  "feedback": "Excellent write-up!",
  "createdAt": 1700000000000,
  "updatedAt": 1700001000000
}
```

### Field Description

| Field       | Type     | Description        |
| ----------- | -------- | ------------------ |
| `_id`       | ObjectId | Primary key        |
| `postId`    | ObjectId | Reference to Posts |
| `userId`    | ObjectId | Reference to Users |
| `rating`    | Integer  | 1–5 stars          |
| `feedback`  | String   | Optional review    |
| `createdAt` | Long     | Creation timestamp |
| `updatedAt` | Long     | Update timestamp   |

### Indexes

```js
db.reviews.createIndex({ postId: 1 })
db.reviews.createIndex({ userId: 1 })
db.reviews.createIndex({ postId: 1, userId: 1 }, { unique: true })
```

### Design Notes

* One review per user per post
* Average rating calculated dynamically or cached

---

## 7. 🔐 Data Integrity & Validation Strategy

* Application-level validation (Service layer)
* ObjectId validation before queries
* Unique indexes enforce constraints
* No cascading deletes (handled in service layer)

---

## 8. 📈 Scalability Considerations

* Horizontal scaling via sharding (postId-based)
* Read-heavy optimized with indexes
* Separation of collections prevents document bloat
* Safe for millions of users and posts

