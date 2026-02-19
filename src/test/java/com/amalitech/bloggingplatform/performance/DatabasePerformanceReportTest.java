package com.amalitech.bloggingplatform.performance;

import com.amalitech.bloggingplatform.dao.impl.*;
import com.amalitech.bloggingplatform.model.*;
import com.amalitech.bloggingplatform.model.Tag;
import com.amalitech.bloggingplatform.utils.MongoDBConnection;
import com.mongodb.client.MongoDatabase;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Integration performance report for all MongoDB DAO operations.
 *
 * Prerequisites: A running MongoDB instance at mongodb://127.0.0.1:27017
 *
 * Each test method benchmarks a single DAO operation, records the elapsed time
 * and pass/fail status, then @AfterAll prints a formatted ASCII table report.
 * All inserted test documents are cleaned up within the test run.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("MongoDB Database Performance Report")
class DatabasePerformanceReportTest {

    // ── DAO instances ─────────────────────────────────────────────────────────
    private static UserDAOImpl    userDAO;
    private static PostDAOImpl    postDAO;
    private static CommentDAOImpl commentDAO;
    private static ReviewDAOImpl  reviewDAO;
    private static TagDAOImpl     tagDAO;

    // ── Shared test-data identifiers (populated as tests run) ─────────────────
    private static String testUserId;
    private static String testPostId;
    private static String testCommentId;
    private static String testReviewId;
    private static String testTagId;
    private static String testUsername;
    private static String testEmail;
    private static String testTagName;
    private static final String RAW_PASSWORD = "PerfTest@123";

    // ── Result accumulator ────────────────────────────────────────────────────
    private static final List<String[]> results = new ArrayList<>();

    // ─────────────────────────────────────────────────────────────────────────
    @BeforeAll
    static void setUp() {
        MongoDatabase db = MongoDBConnection.getDatabase();
        userDAO    = new UserDAOImpl(db);
        postDAO    = new PostDAOImpl(db);
        commentDAO = new CommentDAOImpl(db);
        reviewDAO  = new ReviewDAOImpl(db);
        tagDAO     = new TagDAOImpl(db);
    }

    /**
     * Runs {@code task}, measures wall-clock time, and appends a result row.
     */
    private static void benchmark(String operation, Runnable task) {
        long start = System.nanoTime();
        try {
            task.run();
            long ms = (System.nanoTime() - start) / 1_000_000L;
            results.add(new String[]{operation, ms + " ms", "PASS"});
        } catch (Exception e) {
            long ms = (System.nanoTime() - start) / 1_000_000L;
            results.add(new String[]{operation, ms + " ms", "FAIL: " + e.getMessage()});
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  USER OPERATIONS  (collection: users)
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(10)
    @DisplayName("[User] save() — INSERT new user")
    void user_save() {
        long ts = System.currentTimeMillis();
        testUsername = "perf_user_" + ts;
        testEmail    = "perf_" + ts + "@test.com";
        benchmark("[User]    save()               INSERT new document", () -> {
            User u = new User();
            u.setUsername(testUsername);
            u.setEmail(testEmail);
            u.setPasswordHash(RAW_PASSWORD);
            testUserId = userDAO.save(u).getId();
        });
    }

    @Test @Order(11)
    @DisplayName("[User] findById() — SELECT by _id")
    void user_findById() {
        benchmark("[User]    findById()            SELECT by _id (ObjectId)", () ->
                userDAO.findById(testUserId));
    }

    @Test @Order(12)
    @DisplayName("[User] findByUsername() — SELECT by unique index")
    void user_findByUsername() {
        benchmark("[User]    findByUsername()       SELECT by unique indexed field", () ->
                userDAO.findByUsername(testUsername));
    }

    @Test @Order(13)
    @DisplayName("[User] findByEmail() — SELECT by unique index")
    void user_findByEmail() {
        benchmark("[User]    findByEmail()          SELECT by unique indexed field", () ->
                userDAO.findByEmail(testEmail));
    }

    @Test @Order(14)
    @DisplayName("[User] login() — SELECT + bcrypt verify")
    void user_login() {
        benchmark("[User]    login()                SELECT + bcrypt password verify", () ->
                userDAO.login(testEmail, RAW_PASSWORD));
    }

    @Test @Order(15)
    @DisplayName("[User] findAll() — SELECT all documents")
    void user_findAll() {
        benchmark("[User]    findAll()              SELECT all documents", () ->
                userDAO.findAll());
    }

    @Test @Order(16)
    @DisplayName("[User] update() — UPDATE full document")
    void user_update() {
        benchmark("[User]    update()               UPDATE full document", () -> {
            User u = new User();
            u.setId(testUserId);
            u.setUsername(testUsername + "_upd");
            u.setEmail("upd_" + testEmail);
            u.setPasswordHash(RAW_PASSWORD);
            userDAO.update(u);
        });
    }

    @Test @Order(17)
    @DisplayName("[User] updateUserDetails() — UPDATE partial (no password)")
    void user_updateUserDetails() {
        benchmark("[User]    updateUserDetails()    UPDATE partial fields", () -> {
            User u = new User();
            u.setId(testUserId);
            u.setUsername(testUsername + "_v2");
            u.setEmail("v2_" + testEmail);
            userDAO.updateUserDetails(u);
        });
    }

    @Test @Order(18)
    @DisplayName("[User] updatePassword() — UPDATE password hash")
    void user_updatePassword() {
        benchmark("[User]    updatePassword()       UPDATE password hash only", () ->
                userDAO.updatePassword(testUserId, RAW_PASSWORD + "_new"));
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  POST OPERATIONS  (collection: posts)
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(20)
    @DisplayName("[Post] save() — INSERT new post")
    void post_save() {
        benchmark("[Post]    save()               INSERT new document", () -> {
            Post p = new Post();
            p.setAuthorId(testUserId != null ? testUserId : "perf-author");
            p.setTitle("Performance Test Post " + System.currentTimeMillis());
            p.setContent("Content for MongoDB performance benchmark testing.");
            p.setPublished(true);
            testPostId = postDAO.save(p).getId();
        });
    }

    @Test @Order(21)
    @DisplayName("[Post] findById() — SELECT by _id")
    void post_findById() {
        benchmark("[Post]    findById()            SELECT by _id (ObjectId)", () ->
                postDAO.findById(testPostId));
    }

    @Test @Order(22)
    @DisplayName("[Post] findByAuthorId() — SELECT by indexed field")
    void post_findByAuthorId() {
        benchmark("[Post]    findByAuthorId()       SELECT by indexed field", () ->
                postDAO.findByAuthorId(testUserId != null ? testUserId : "perf-author"));
    }

    @Test @Order(23)
    @DisplayName("[Post] searchByTitle() — Regex search on title")
    void post_searchByTitle() {
        benchmark("[Post]    searchByTitle()        Regex search (case-insensitive)", () ->
                postDAO.searchByTitle("Performance Test"));
    }

    @Test @Order(24)
    @DisplayName("[Post] findAll() — SELECT all documents")
    void post_findAll() {
        benchmark("[Post]    findAll()              SELECT all documents", () ->
                postDAO.findAll());
    }

    @Test @Order(25)
    @DisplayName("[Post] update() — UPDATE post fields")
    void post_update() {
        benchmark("[Post]    update()               UPDATE document fields", () -> {
            Post p = new Post();
            p.setId(testPostId);
            p.setTitle("Updated Performance Test Post");
            p.setContent("Updated benchmark content.");
            p.setPublished(false);
            postDAO.update(p);
        });
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  COMMENT OPERATIONS  (collection: comments)
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(30)
    @DisplayName("[Comment] save() — INSERT new comment")
    void comment_save() {
        benchmark("[Comment] save()               INSERT new document", () -> {
            Comment c = new Comment();
            c.setPostId(testPostId != null ? testPostId : "perf-post");
            c.setUserId(testUserId != null ? testUserId : "perf-user");
            c.setContent("This is a performance test comment.");
            testCommentId = commentDAO.save(c).getId();
        });
    }

    @Test @Order(31)
    @DisplayName("[Comment] findById() — SELECT by _id")
    void comment_findById() {
        benchmark("[Comment] findById()            SELECT by _id (ObjectId)", () ->
                commentDAO.findById(testCommentId));
    }

    @Test @Order(32)
    @DisplayName("[Comment] findByPostId() — SELECT by indexed field")
    void comment_findByPostId() {
        benchmark("[Comment] findByPostId()         SELECT by indexed field", () ->
                commentDAO.findByPostId(testPostId != null ? testPostId : "perf-post"));
    }

    @Test @Order(33)
    @DisplayName("[Comment] findAll() — SELECT all documents")
    void comment_findAll() {
        benchmark("[Comment] findAll()              SELECT all documents", () ->
                commentDAO.findAll());
    }

    @Test @Order(34)
    @DisplayName("[Comment] update() — UPDATE comment content")
    void comment_update() {
        benchmark("[Comment] update()               UPDATE document fields", () -> {
            Comment c = new Comment();
            c.setId(testCommentId);
            c.setContent("Updated performance test comment content.");
            commentDAO.update(c);
        });
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  REVIEW OPERATIONS  (collection: reviews)
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(40)
    @DisplayName("[Review] save() — INSERT new review")
    void review_save() {
        benchmark("[Review]  save()               INSERT new document", () -> {
            Review r = new Review();
            r.setPostId(testPostId != null ? testPostId : "perf-post");
            r.setUserId(testUserId != null ? testUserId : "perf-user");
            r.setRating(5);
            r.setFeedback("Excellent performance test post!");
            testReviewId = reviewDAO.save(r).getId();
        });
    }

    @Test @Order(41)
    @DisplayName("[Review] findById() — SELECT by _id")
    void review_findById() {
        benchmark("[Review]  findById()            SELECT by _id (ObjectId)", () ->
                reviewDAO.findById(testReviewId));
    }

    @Test @Order(42)
    @DisplayName("[Review] findByPostId() — SELECT by indexed field")
    void review_findByPostId() {
        benchmark("[Review]  findByPostId()         SELECT by indexed field", () ->
                reviewDAO.findByPostId(testPostId != null ? testPostId : "perf-post"));
    }

    @Test @Order(43)
    @DisplayName("[Review] calculateAverageRating() — Aggregation-style query")
    void review_calculateAverageRating() {
        benchmark("[Review]  calculateAverageRating() Fetch + in-memory aggregation", () ->
                reviewDAO.calculateAverageRating(testPostId != null ? testPostId : "perf-post"));
    }

    @Test @Order(44)
    @DisplayName("[Review] findAll() — SELECT all documents")
    void review_findAll() {
        benchmark("[Review]  findAll()              SELECT all documents", () ->
                reviewDAO.findAll());
    }

    @Test @Order(45)
    @DisplayName("[Review] update() — UPDATE review fields")
    void review_update() {
        benchmark("[Review]  update()               UPDATE document fields", () -> {
            Review r = new Review();
            r.setId(testReviewId);
            r.setRating(4);
            r.setFeedback("Updated benchmark review feedback.");
            reviewDAO.update(r);
        });
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  TAG OPERATIONS  (collections: tags, post_tags)
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(50)
    @DisplayName("[Tag] save() — INSERT new tag")
    void tag_save() {
        benchmark("[Tag]     save()               INSERT new document", () -> {
            testTagName = "perf-tag-" + System.currentTimeMillis();
            Tag t = new Tag();
            t.setName(testTagName);
            testTagId = tagDAO.save(t).getId();
        });
    }

    @Test @Order(51)
    @DisplayName("[Tag] findById() — SELECT by _id")
    void tag_findById() {
        benchmark("[Tag]     findById()            SELECT by _id (ObjectId)", () ->
                tagDAO.findById(testTagId));
    }

    @Test @Order(52)
    @DisplayName("[Tag] findByName() — SELECT by indexed field")
    void tag_findByName() {
        benchmark("[Tag]     findByName()           SELECT by indexed field", () ->
                tagDAO.findByName(testTagName));
    }

    @Test @Order(53)
    @DisplayName("[Tag] findAll() — SELECT all documents")
    void tag_findAll() {
        benchmark("[Tag]     findAll()              SELECT all documents", () ->
                tagDAO.findAll());
    }

    @Test @Order(54)
    @DisplayName("[Tag] assignTagToPost() — INSERT into post_tags")
    void tag_assignTagToPost() {
        benchmark("[Tag]     assignTagToPost()      INSERT into post_tags", () ->
                tagDAO.assignTagToPost(
                        testPostId != null ? testPostId : "perf-post",
                        testTagId  != null ? testTagId  : "perf-tag"));
    }

    @Test @Order(55)
    @DisplayName("[Tag] findTagsByPostId() — JOIN-like: post_tags → tags")
    void tag_findTagsByPostId() {
        benchmark("[Tag]     findTagsByPostId()     JOIN-like lookup (post_tags -> tags)", () ->
                tagDAO.findTagsByPostId(testPostId != null ? testPostId : "perf-post"));
    }

    @Test @Order(56)
    @DisplayName("[Tag] update() — UPDATE tag name")
    void tag_update() {
        benchmark("[Tag]     update()               UPDATE document fields", () -> {
            Tag t = new Tag();
            t.setId(testTagId);
            t.setName("updated-perf-tag");
            tagDAO.update(t);
        });
    }

    @Test @Order(57)
    @DisplayName("[Tag] unassignAllTagsFromPost() — DELETE from post_tags")
    void tag_unassignAllTagsFromPost() {
        benchmark("[Tag]     unassignAllTagsFromPost() DELETE many from post_tags", () ->
                tagDAO.unassignAllTagsFromPost(testPostId != null ? testPostId : "perf-post"));
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  DELETE OPERATIONS  (clean up in dependency order)
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(60)
    @DisplayName("[Review] deleteById() — DELETE review document")
    void review_delete() {
        benchmark("[Review]  deleteById()           DELETE document by _id", () ->
                reviewDAO.deleteById(testReviewId));
    }

    @Test @Order(61)
    @DisplayName("[Comment] deleteById() — DELETE comment document")
    void comment_delete() {
        benchmark("[Comment] deleteById()           DELETE document by _id", () ->
                commentDAO.deleteById(testCommentId));
    }

    @Test @Order(62)
    @DisplayName("[Tag] deleteById() — DELETE tag + post_tag mappings")
    void tag_delete() {
        benchmark("[Tag]     deleteById()           DELETE document + cascade post_tags", () ->
                tagDAO.deleteById(testTagId));
    }

    @Test @Order(63)
    @DisplayName("[Post] deleteById() — DELETE post document")
    void post_delete() {
        benchmark("[Post]    deleteById()           DELETE document by _id", () ->
                postDAO.deleteById(testPostId));
    }

    @Test @Order(64)
    @DisplayName("[User] deleteById() — DELETE user document")
    void user_delete() {
        benchmark("[User]    deleteById()           DELETE document by _id", () ->
                userDAO.deleteById(testUserId));
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PERFORMANCE REPORT
    // ══════════════════════════════════════════════════════════════════════════

    @AfterAll
    static void printPerformanceReport() {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        long passed  = results.stream().filter(r -> r[2].startsWith("PASS")).count();
        long failed  = results.stream().filter(r -> r[2].startsWith("FAIL")).count();
        long totalMs = results.stream()
                .mapToLong(r -> Long.parseLong(r[1].replace(" ms", "").trim()))
                .sum();
        long avgMs = results.isEmpty() ? 0 : totalMs / results.size();

        printConsoleReport(timestamp, passed, failed, totalMs, avgMs);
        writeMarkdownReport(timestamp, passed, failed, totalMs, avgMs);
    }

    // ── Console (ASCII table) ─────────────────────────────────────────────────

    private static void printConsoleReport(String timestamp,
                                           long passed, long failed,
                                           long totalMs, long avgMs) {
        String border = "+----------------------------------------------------------+---------------+----------------------------------+";
        String fmt    = "| %-56s | %13s | %-32s |%n";

        System.out.println();
        System.out.println("+==========================================================================================+");
        System.out.printf( "|  %-88s|%n", " MONGODB DATABASE PERFORMANCE REPORT");
        System.out.printf( "|  %-88s|%n", " Database : java-demo   (mongodb://127.0.0.1:27017)");
        System.out.printf( "|  %-88s|%n", " Generated: " + timestamp);
        System.out.printf( "|  %-88s|%n", " Collections: users | posts | comments | reviews | tags | post_tags");
        System.out.println("+==========================================================================================+");
        System.out.println();
        System.out.println(border);
        System.out.printf(fmt, "Operation", "Duration", "Status");
        System.out.println(border);

        String currentSection = "";
        for (String[] row : results) {
            String section = resolveSectionLabel(row[0]);
            if (!section.equals(currentSection)) {
                currentSection = section;
                System.out.printf(fmt,
                        "── " + currentSection + " ─────────────────────────────────────────",
                        "", "");
                System.out.println(border);
            }
            String status = row[2].startsWith("PASS") ? "✔  " + row[2] : "✘  " + row[2];
            System.out.printf(fmt, row[0], row[1], status);
        }

        System.out.println(border);
        System.out.printf(fmt, "Total Operations : " + results.size(), totalMs + " ms",
                "PASS=" + passed + "  FAIL=" + failed);
        System.out.printf(fmt, "Average per Operation", avgMs + " ms", "");
        System.out.println(border);
        System.out.println();
    }

    // ── Markdown file ─────────────────────────────────────────────────────────

    private static void writeMarkdownReport(String timestamp,
                                            long passed, long failed,
                                            long totalMs, long avgMs) {
        Path reportPath = Paths.get("docs", "PERFORMANCE_REPORT.md");
        try {
            Files.createDirectories(reportPath.getParent());
        } catch (IOException e) {
            System.err.println("[PerfReport] Could not create docs/ directory: " + e.getMessage());
            return;
        }

        try (PrintWriter md = new PrintWriter(Files.newBufferedWriter(reportPath))) {

            md.println("# MongoDB Database Performance Report");
            md.println();
            md.println("| | |");
            md.println("|---|---|");
            md.println("| **Database**     | `java-demo` (`mongodb://127.0.0.1:27017`) |");
            md.println("| **Collections**  | `users`, `posts`, `comments`, `reviews`, `tags`, `post_tags` |");
            md.println("| **Generated**    | " + timestamp + " |");
            md.println("| **Total Ops**    | " + results.size() + " |");
            md.println("| **Total Time**   | " + totalMs + " ms |");
            md.println("| **Avg per Op**   | " + avgMs + " ms |");
            md.println("| **Passed / Failed** | ✅ " + passed + "  /  ❌ " + failed + " |");
            md.println();

            // Group results by section
            Map<String, List<String[]>> sections = new LinkedHashMap<>();
            for (String[] row : results) {
                String section = resolveSectionLabel(row[0]);
                sections.computeIfAbsent(section, k -> new ArrayList<>()).add(row);
            }

            for (Map.Entry<String, List<String[]>> entry : sections.entrySet()) {
                md.println("---");
                md.println();
                md.println("## " + entry.getKey());
                md.println();
                md.println("| Operation | Duration | Status |");
                md.println("|-----------|----------|--------|");
                for (String[] row : entry.getValue()) {
                    String status = row[2].startsWith("PASS")
                            ? "✅ PASS"
                            : "❌ " + row[2];
                    md.printf("| `%s` | %s | %s |%n", row[0].trim(), row[1], status);
                }
                md.println();

                // Section subtotal
                long sectionMs = entry.getValue().stream()
                        .mapToLong(r -> Long.parseLong(r[1].replace(" ms", "").trim()))
                        .sum();
                long sectionPassed = entry.getValue().stream()
                        .filter(r -> r[2].startsWith("PASS")).count();
                md.printf("> **Subtotal:** %d ms over %d operation(s) — %d passed%n%n",
                        sectionMs, entry.getValue().size(), sectionPassed);
            }

            md.println("---");
            md.println();
            md.println("## Summary");
            md.println();
            md.println("| Metric | Value |");
            md.println("|--------|-------|");
            md.println("| Total Operations | " + results.size() + " |");
            md.println("| Total Duration   | **" + totalMs + " ms** |");
            md.println("| Average Duration | **" + avgMs + " ms** |");
            md.println("| Passed           | ✅ " + passed + " |");
            md.println("| Failed           | ❌ " + failed + " |");
            md.println();
            md.println("> *Report auto-generated by `DatabasePerformanceReportTest` — do not edit manually.*");

            System.out.println("[PerfReport] Markdown report saved → " + reportPath.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("[PerfReport] Failed to write markdown report: " + e.getMessage());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static String resolveSectionLabel(String operation) {
        if (operation.startsWith("[User]"))    return "USER OPERATIONS — collection: `users`";
        if (operation.startsWith("[Post]"))    return "POST OPERATIONS — collection: `posts`";
        if (operation.startsWith("[Comment]")) return "COMMENT OPERATIONS — collection: `comments`";
        if (operation.startsWith("[Review]"))  return "REVIEW OPERATIONS — collection: `reviews`";
        if (operation.startsWith("[Tag]"))     return "TAG OPERATIONS — collections: `tags`, `post_tags`";
        return "OTHER";
    }
}
