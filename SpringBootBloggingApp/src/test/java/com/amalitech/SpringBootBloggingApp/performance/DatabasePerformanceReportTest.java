package com.amalitech.SpringBootBloggingApp.performance;

import com.amalitech.SpringBootBloggingApp.model.entity.*;
import com.amalitech.SpringBootBloggingApp.model.entity.Tag;
import com.amalitech.SpringBootBloggingApp.repository.*;
import com.amalitech.SpringBootBloggingApp.util.PasswordUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

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
 * Integration performance report for all MongoDB Repository operations.
 *
 * Prerequisites: A running MongoDB instance at mongodb://localhost:27017
 *
 * Each test method benchmarks a single repository operation, records elapsed time
 * and pass/fail status, then @AfterAll prints a formatted ASCII table report
 * and saves a Markdown file to docs/SPRING_PERFORMANCE_REPORT.md.
 * All inserted test documents are cleaned up within the test run.
 */
@DataMongoTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Spring Boot Blogging App — Database Performance Report")
class DatabasePerformanceReportTest {

    @Autowired private UserRepository    userRepository;
    @Autowired private PostRepository    postRepository;
    @Autowired private CommentRepository commentRepository;
    @Autowired private ReviewRepository  reviewRepository;
    @Autowired private TagRepository     tagRepository;
    @Autowired private PostTagRepository postTagRepository;

    // ── Shared test-data identifiers ──────────────────────────────────────────
    private static String testUserId;
    private static String testPostId;
    private static String testCommentId;
    private static String testReviewId;
    private static String testTagId;
    private static String testUsername;
    private static String testEmail;
    private static String testTagName;
    private static User   savedUser;
    private static Post   savedPost;
    private static final String RAW_PASSWORD = "PerfTest@123";

    // ── Result accumulator ────────────────────────────────────────────────────
    private static final List<String[]> results = new ArrayList<>();

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Measures wall-clock time for {@code task} and appends a result row.
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
            User u = new User(testUsername, testEmail, PasswordUtil.hashPassword(RAW_PASSWORD));
            savedUser  = userRepository.save(u);
            testUserId = savedUser.getId();
        });
    }

    @Test @Order(11)
    @DisplayName("[User] findById() — SELECT by _id")
    void user_findById() {
        benchmark("[User]    findById()            SELECT by _id (ObjectId)", () ->
                userRepository.findById(testUserId));
    }

    @Test @Order(12)
    @DisplayName("[User] findByUsername() — SELECT by unique index")
    void user_findByUsername() {
        benchmark("[User]    findByUsername()       SELECT by unique indexed field", () ->
                userRepository.findByUsername(testUsername));
    }

    @Test @Order(13)
    @DisplayName("[User] findByEmail() — SELECT by unique index")
    void user_findByEmail() {
        benchmark("[User]    findByEmail()          SELECT by unique indexed field", () ->
                userRepository.findByEmail(testEmail));
    }

    @Test @Order(14)
    @DisplayName("[User] findByEmailForLogin() + BCrypt — SELECT + password verify")
    void user_login() {
        benchmark("[User]    findByEmailForLogin()  SELECT + bcrypt password verify", () -> {
            User u = userRepository.findByEmailForLogin(testEmail).orElseThrow();
            PasswordUtil.verifyPassword(RAW_PASSWORD, u.getPasswordHash());
        });
    }

    @Test @Order(15)
    @DisplayName("[User] findAll() — SELECT all documents")
    void user_findAll() {
        benchmark("[User]    findAll()              SELECT all documents", () ->
                userRepository.findAll());
    }

    @Test @Order(16)
    @DisplayName("[User] save() (update) — UPDATE full document")
    void user_update() {
        benchmark("[User]    save() update          UPDATE full document", () -> {
            savedUser.setUsername(testUsername + "_upd");
            savedUser.setEmail("upd_" + testEmail);
            savedUser.setUpdatedAt(System.currentTimeMillis());
            savedUser = userRepository.save(savedUser);
        });
    }

    @Test @Order(17)
    @DisplayName("[User] existsByUsername() — CHECK unique constraint")
    void user_existsByUsername() {
        benchmark("[User]    existsByUsername()     EXISTS check on unique field", () ->
                userRepository.existsByUsername(testUsername + "_upd"));
    }

    @Test @Order(18)
    @DisplayName("[User] existsByEmail() — CHECK unique constraint")
    void user_existsByEmail() {
        benchmark("[User]    existsByEmail()        EXISTS check on unique field", () ->
                userRepository.existsByEmail("upd_" + testEmail));
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  POST OPERATIONS  (collection: posts)
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(20)
    @DisplayName("[Post] save() — INSERT new post")
    void post_save() {
        benchmark("[Post]    save()               INSERT new document", () -> {
            Post p = new Post();
            p.setAuthor(savedUser);
            p.setTitle("Performance Test Post " + System.currentTimeMillis());
            p.setContent("Content for Spring Boot MongoDB performance benchmark testing.");
            p.setPublished(true);
            p.setCreatedAt(System.currentTimeMillis());
            p.setUpdatedAt(System.currentTimeMillis());
            savedPost  = postRepository.save(p);
            testPostId = savedPost.getId();
        });
    }

    @Test @Order(21)
    @DisplayName("[Post] findById() — SELECT by _id")
    void post_findById() {
        benchmark("[Post]    findById()            SELECT by _id (ObjectId)", () ->
                postRepository.findById(testPostId));
    }

    @Test @Order(22)
    @DisplayName("[Post] findByAuthor() — SELECT by indexed DBRef field")
    void post_findByAuthor() {
        benchmark("[Post]    findByAuthor()         SELECT by indexed DBRef field", () ->
                postRepository.findByAuthor(savedUser));
    }

    @Test @Order(23)
    @DisplayName("[Post] findByAuthorId() — SELECT by author ID string")
    void post_findByAuthorId() {
        benchmark("[Post]    findByAuthorId()       SELECT by author string ID", () ->
                postRepository.findByAuthorId(testUserId));
    }

    @Test @Order(24)
    @DisplayName("[Post] searchByTitle() — Regex search on title")
    void post_searchByTitle() {
        benchmark("[Post]    searchByTitle()        Regex search (case-insensitive)", () ->
                postRepository.searchByTitle("Performance Test"));
    }

    @Test @Order(25)
    @DisplayName("[Post] findByPublishedTrue() — SELECT published posts")
    void post_findByPublished() {
        benchmark("[Post]    findByPublishedTrue()  SELECT by indexed boolean field", () ->
                postRepository.findByPublishedTrue());
    }

    @Test @Order(26)
    @DisplayName("[Post] countByPublished() — COUNT published posts")
    void post_countByPublished() {
        benchmark("[Post]    countByPublished()     COUNT by indexed boolean field", () ->
                postRepository.countByPublished(true));
    }

    @Test @Order(27)
    @DisplayName("[Post] findAll() — SELECT all documents")
    void post_findAll() {
        benchmark("[Post]    findAll()              SELECT all documents", () ->
                postRepository.findAll());
    }

    @Test @Order(28)
    @DisplayName("[Post] save() (update) — UPDATE post fields")
    void post_update() {
        benchmark("[Post]    save() update          UPDATE document fields", () -> {
            savedPost.setTitle("Updated Performance Test Post");
            savedPost.setContent("Updated benchmark content.");
            savedPost.setPublished(false);
            savedPost.setUpdatedAt(System.currentTimeMillis());
            savedPost = postRepository.save(savedPost);
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
            c.setPost(savedPost);
            c.setUser(savedUser);
            c.setContent("This is a performance test comment.");
            c.setCreatedAt(System.currentTimeMillis());
            c.setUpdatedAt(System.currentTimeMillis());
            testCommentId = commentRepository.save(c).getId();
        });
    }

    @Test @Order(31)
    @DisplayName("[Comment] findById() — SELECT by _id")
    void comment_findById() {
        benchmark("[Comment] findById()            SELECT by _id (ObjectId)", () ->
                commentRepository.findById(testCommentId));
    }

    @Test @Order(32)
    @DisplayName("[Comment] findByPost() — SELECT by indexed DBRef field")
    void comment_findByPost() {
        benchmark("[Comment] findByPost()           SELECT by indexed DBRef field", () ->
                commentRepository.findByPost(savedPost));
    }

    @Test @Order(33)
    @DisplayName("[Comment] findByPostId() — SELECT by post ID string")
    void comment_findByPostId() {
        benchmark("[Comment] findByPostId()         SELECT by post ID string", () ->
                commentRepository.findByPostId(testPostId));
    }

    @Test @Order(34)
    @DisplayName("[Comment] countByPostId() — COUNT by post ID")
    void comment_countByPostId() {
        benchmark("[Comment] countByPostId()        COUNT documents by post ID", () ->
                commentRepository.countByPostId(testPostId));
    }

    @Test @Order(35)
    @DisplayName("[Comment] findAll() — SELECT all documents")
    void comment_findAll() {
        benchmark("[Comment] findAll()              SELECT all documents", () ->
                commentRepository.findAll());
    }

    @Test @Order(36)
    @DisplayName("[Comment] save() (update) — UPDATE comment content")
    void comment_update() {
        benchmark("[Comment] save() update          UPDATE document fields", () -> {
            Comment c = commentRepository.findById(testCommentId).orElseThrow();
            c.setContent("Updated performance test comment content.");
            c.setUpdatedAt(System.currentTimeMillis());
            commentRepository.save(c);
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
            r.setPost(savedPost);
            r.setUser(savedUser);
            r.setRating(5);
            r.setFeedback("Excellent performance test post!");
            r.setCreatedAt(System.currentTimeMillis());
            r.setUpdatedAt(System.currentTimeMillis());
            testReviewId = reviewRepository.save(r).getId();
        });
    }

    @Test @Order(41)
    @DisplayName("[Review] findById() — SELECT by _id")
    void review_findById() {
        benchmark("[Review]  findById()            SELECT by _id (ObjectId)", () ->
                reviewRepository.findById(testReviewId));
    }

    @Test @Order(42)
    @DisplayName("[Review] findByPost() — SELECT by indexed DBRef field")
    void review_findByPost() {
        benchmark("[Review]  findByPost()           SELECT by indexed DBRef field", () ->
                reviewRepository.findByPost(savedPost));
    }

    @Test @Order(43)
    @DisplayName("[Review] findByPostId() — SELECT reviews by post ID")
    void review_findByPostId() {
        benchmark("[Review]  findByPostId()         SELECT by post ID string", () ->
                reviewRepository.findByPostId(testPostId));
    }

    @Test @Order(44)
    @DisplayName("[Review] calculateAverageRatingByPostId() — MongoDB aggregation pipeline")
    void review_calculateAverageRating() {
        benchmark("[Review]  calculateAverageRating() MongoDB aggregation pipeline", () ->
                reviewRepository.calculateAverageRatingByPostId(testPostId));
    }

    @Test @Order(45)
    @DisplayName("[Review] existsByPostIdAndUserId() — CHECK duplicate review")
    void review_existsByPostIdAndUserId() {
        benchmark("[Review]  existsByPostIdAndUserId() EXISTS check (compound index)", () ->
                reviewRepository.existsByPostIdAndUserId(testPostId, testUserId));
    }

    @Test @Order(46)
    @DisplayName("[Review] findAll() — SELECT all documents")
    void review_findAll() {
        benchmark("[Review]  findAll()              SELECT all documents", () ->
                reviewRepository.findAll());
    }

    @Test @Order(47)
    @DisplayName("[Review] save() (update) — UPDATE review fields")
    void review_update() {
        benchmark("[Review]  save() update          UPDATE document fields", () -> {
            Review r = reviewRepository.findById(testReviewId).orElseThrow();
            r.setRating(4);
            r.setFeedback("Updated benchmark review feedback.");
            r.setUpdatedAt(System.currentTimeMillis());
            reviewRepository.save(r);
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
            testTagId = tagRepository.save(t).getId();
        });
    }

    @Test @Order(51)
    @DisplayName("[Tag] findById() — SELECT by _id")
    void tag_findById() {
        benchmark("[Tag]     findById()            SELECT by _id (ObjectId)", () ->
                tagRepository.findById(testTagId));
    }

    @Test @Order(52)
    @DisplayName("[Tag] findByName() — SELECT by unique indexed field")
    void tag_findByName() {
        benchmark("[Tag]     findByName()           SELECT by unique indexed field", () ->
                tagRepository.findByName(testTagName));
    }

    @Test @Order(53)
    @DisplayName("[Tag] searchByName() — Regex search on tag name")
    void tag_searchByName() {
        benchmark("[Tag]     searchByName()         Regex search (case-insensitive)", () ->
                tagRepository.searchByName("perf-tag"));
    }

    @Test @Order(54)
    @DisplayName("[Tag] findAll() — SELECT all documents")
    void tag_findAll() {
        benchmark("[Tag]     findAll()              SELECT all documents", () ->
                tagRepository.findAll());
    }

    @Test @Order(55)
    @DisplayName("[PostTag] save() — INSERT (assign tag to post)")
    void tag_assignTagToPost() {
        benchmark("[Tag]     assignTagToPost()      INSERT into post_tags collection", () ->
                postTagRepository.save(new PostTag(testPostId, testTagId)));
    }

    @Test @Order(56)
    @DisplayName("[PostTag] findByPostId() — SELECT post_tags by post ID")
    void tag_findTagsByPostId() {
        benchmark("[Tag]     findByPostId()         SELECT post_tags by post ID", () ->
                postTagRepository.findByPostId(testPostId));
    }

    @Test @Order(57)
    @DisplayName("[PostTag] existsByPostIdAndTagId() — CHECK post_tag mapping")
    void tag_existsByPostIdAndTagId() {
        benchmark("[Tag]     existsByPostIdAndTagId() EXISTS check (compound unique index)", () ->
                postTagRepository.existsByPostIdAndTagId(testPostId, testTagId));
    }

    @Test @Order(58)
    @DisplayName("[Tag] save() (update) — UPDATE tag name")
    void tag_update() {
        benchmark("[Tag]     save() update          UPDATE document fields", () -> {
            Tag t = tagRepository.findById(testTagId).orElseThrow();
            t.setName("updated-perf-tag-" + System.currentTimeMillis());
            tagRepository.save(t);
        });
    }

    @Test @Order(59)
    @DisplayName("[PostTag] deleteByPostId() — DELETE from post_tags")
    void tag_unassignAllTagsFromPost() {
        benchmark("[Tag]     deleteByPostId()       DELETE many from post_tags", () ->
                postTagRepository.deleteByPostId(testPostId));
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  DELETE OPERATIONS  (clean up in dependency order)
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(60)
    @DisplayName("[Review] deleteById() — DELETE review document")
    void review_delete() {
        benchmark("[Review]  deleteById()           DELETE document by _id", () ->
                reviewRepository.deleteById(testReviewId));
    }

    @Test @Order(61)
    @DisplayName("[Comment] deleteById() — DELETE comment document")
    void comment_delete() {
        benchmark("[Comment] deleteById()           DELETE document by _id", () ->
                commentRepository.deleteById(testCommentId));
    }

    @Test @Order(62)
    @DisplayName("[Tag] deleteById() — DELETE tag document")
    void tag_delete() {
        benchmark("[Tag]     deleteById()           DELETE tag document by _id", () ->
                tagRepository.deleteById(testTagId));
    }

    @Test @Order(63)
    @DisplayName("[Post] deleteById() — DELETE post document")
    void post_delete() {
        benchmark("[Post]    deleteById()           DELETE document by _id", () ->
                postRepository.deleteById(testPostId));
    }

    @Test @Order(64)
    @DisplayName("[User] deleteById() — DELETE user document")
    void user_delete() {
        benchmark("[User]    deleteById()           DELETE document by _id", () ->
                userRepository.deleteById(testUserId));
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
        System.out.printf( "|  %-88s|%n", " Database : blogging_platform_test   (mongodb://localhost:27017)");
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
        Path reportPath = Paths.get("docs", "SPRING_PERFORMANCE_REPORT.md");
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
            md.println("| **Database**     | `blogging_platform_test` (`mongodb://localhost:27017`) |");
            md.println("| **Collections**  | `users`, `posts`, `comments`, `reviews`, `tags`, `post_tags` |");
            md.println("| **Generated**    | " + timestamp + " |");
            md.println("| **Total Ops**    | " + results.size() + " |");
            md.println("| **Total Time**   | " + totalMs + " ms |");
            md.println("| **Avg per Op**   | " + avgMs + " ms |");
            md.println("| **Passed / Failed** | \u2705 " + passed + "  /  \u274c " + failed + " |");
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
                            ? "\u2705 PASS"
                            : "\u274c " + row[2];
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
            md.println("| Passed           | \u2705 " + passed + " |");
            md.println("| Failed           | \u274c " + failed + " |");
            md.println();
            md.println("> *Report auto-generated by `DatabasePerformanceReportTest` — do not edit manually.*");

            System.out.println("[PerfReport] Markdown report saved \u2192 " + reportPath.toAbsolutePath());

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
