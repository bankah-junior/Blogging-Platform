# MongoDB Database Performance Report

| | |
|---|---|
| **Database**     | `blogging_platform_test` (`mongodb://localhost:27017`) |
| **Collections**  | `users`, `posts`, `comments`, `reviews`, `tags`, `post_tags` |
| **Generated**    | 2026-02-19 10:44:28 |
| **Total Ops**    | 48 |
| **Total Time**   | 912 ms |
| **Avg per Op**   | 19 ms |
| **Passed / Failed** | ✅ 48  /  ❌ 0 |

---

## USER OPERATIONS — collection: `users`

| Operation | Duration | Status |
|-----------|----------|--------|
| `[User]    save()               INSERT new document` | 329 ms | ✅ PASS |
| `[User]    findById()            SELECT by _id (ObjectId)` | 31 ms | ✅ PASS |
| `[User]    findByUsername()       SELECT by unique indexed field` | 9 ms | ✅ PASS |
| `[User]    findByEmail()          SELECT by unique indexed field` | 2 ms | ✅ PASS |
| `[User]    findByEmailForLogin()  SELECT + bcrypt password verify` | 267 ms | ✅ PASS |
| `[User]    findAll()              SELECT all documents` | 27 ms | ✅ PASS |
| `[User]    save() update          UPDATE full document` | 6 ms | ✅ PASS |
| `[User]    existsByUsername()     EXISTS check on unique field` | 9 ms | ✅ PASS |
| `[User]    existsByEmail()        EXISTS check on unique field` | 3 ms | ✅ PASS |
| `[User]    deleteById()           DELETE document by _id` | 1 ms | ✅ PASS |

> **Subtotal:** 684 ms over 10 operation(s) — 10 passed

---

## POST OPERATIONS — collection: `posts`

| Operation | Duration | Status |
|-----------|----------|--------|
| `[Post]    save()               INSERT new document` | 7 ms | ✅ PASS |
| `[Post]    findById()            SELECT by _id (ObjectId)` | 7 ms | ✅ PASS |
| `[Post]    findByAuthor()         SELECT by indexed DBRef field` | 16 ms | ✅ PASS |
| `[Post]    findByAuthorId()       SELECT by author string ID` | 7 ms | ✅ PASS |
| `[Post]    searchByTitle()        Regex search (case-insensitive)` | 4 ms | ✅ PASS |
| `[Post]    findByPublishedTrue()  SELECT by indexed boolean field` | 12 ms | ✅ PASS |
| `[Post]    countByPublished()     COUNT by indexed boolean field` | 5 ms | ✅ PASS |
| `[Post]    findAll()              SELECT all documents` | 13 ms | ✅ PASS |
| `[Post]    save() update          UPDATE document fields` | 2 ms | ✅ PASS |
| `[Post]    deleteById()           DELETE document by _id` | 1 ms | ✅ PASS |

> **Subtotal:** 74 ms over 10 operation(s) — 10 passed

---

## COMMENT OPERATIONS — collection: `comments`

| Operation | Duration | Status |
|-----------|----------|--------|
| `[Comment] save()               INSERT new document` | 7 ms | ✅ PASS |
| `[Comment] findById()            SELECT by _id (ObjectId)` | 7 ms | ✅ PASS |
| `[Comment] findByPost()           SELECT by indexed DBRef field` | 7 ms | ✅ PASS |
| `[Comment] findByPostId()         SELECT by post ID string` | 6 ms | ✅ PASS |
| `[Comment] countByPostId()        COUNT documents by post ID` | 3 ms | ✅ PASS |
| `[Comment] findAll()              SELECT all documents` | 5 ms | ✅ PASS |
| `[Comment] save() update          UPDATE document fields` | 10 ms | ✅ PASS |
| `[Comment] deleteById()           DELETE document by _id` | 1 ms | ✅ PASS |

> **Subtotal:** 46 ms over 8 operation(s) — 8 passed

---

## REVIEW OPERATIONS — collection: `reviews`

| Operation | Duration | Status |
|-----------|----------|--------|
| `[Review]  save()               INSERT new document` | 8 ms | ✅ PASS |
| `[Review]  findById()            SELECT by _id (ObjectId)` | 7 ms | ✅ PASS |
| `[Review]  findByPost()           SELECT by indexed DBRef field` | 5 ms | ✅ PASS |
| `[Review]  findByPostId()         SELECT by post ID string` | 6 ms | ✅ PASS |
| `[Review]  calculateAverageRating() MongoDB aggregation pipeline` | 26 ms | ✅ PASS |
| `[Review]  existsByPostIdAndUserId() EXISTS check (compound index)` | 4 ms | ✅ PASS |
| `[Review]  findAll()              SELECT all documents` | 8 ms | ✅ PASS |
| `[Review]  save() update          UPDATE document fields` | 6 ms | ✅ PASS |
| `[Review]  deleteById()           DELETE document by _id` | 1 ms | ✅ PASS |

> **Subtotal:** 71 ms over 9 operation(s) — 9 passed

---

## TAG OPERATIONS — collections: `tags`, `post_tags`

| Operation | Duration | Status |
|-----------|----------|--------|
| `[Tag]     save()               INSERT new document` | 5 ms | ✅ PASS |
| `[Tag]     findById()            SELECT by _id (ObjectId)` | 2 ms | ✅ PASS |
| `[Tag]     findByName()           SELECT by unique indexed field` | 3 ms | ✅ PASS |
| `[Tag]     searchByName()         Regex search (case-insensitive)` | 3 ms | ✅ PASS |
| `[Tag]     findAll()              SELECT all documents` | 3 ms | ✅ PASS |
| `[Tag]     assignTagToPost()      INSERT into post_tags collection` | 5 ms | ✅ PASS |
| `[Tag]     findByPostId()         SELECT post_tags by post ID` | 3 ms | ✅ PASS |
| `[Tag]     existsByPostIdAndTagId() EXISTS check (compound unique index)` | 3 ms | ✅ PASS |
| `[Tag]     save() update          UPDATE document fields` | 2 ms | ✅ PASS |
| `[Tag]     deleteByPostId()       DELETE many from post_tags` | 7 ms | ✅ PASS |
| `[Tag]     deleteById()           DELETE tag document by _id` | 1 ms | ✅ PASS |

> **Subtotal:** 37 ms over 11 operation(s) — 11 passed

---

## Summary

| Metric | Value |
|--------|-------|
| Total Operations | 48 |
| Total Duration   | **912 ms** |
| Average Duration | **19 ms** |
| Passed           | ✅ 48 |
| Failed           | ❌ 0 |

> *Report auto-generated by `DatabasePerformanceReportTest` — do not edit manually.*
