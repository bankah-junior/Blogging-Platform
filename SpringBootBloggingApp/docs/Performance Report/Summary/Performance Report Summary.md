# Performance Report Summary: Before vs. After Optimization

**Application:** Spring Boot Blogging Application
**Testing Tools:** Postman and VisualVM
**Test Type:** Sequential Load Test (50 iterations per endpoint)
**Endpoints Tested:** Create Post | Get Posts Sorted by Date

---

## Overview

This summary compares JVM performance and API response metrics collected **before** and **after** optimization of the Spring Boot Blogging Application.

---

## 1. Create Post Endpoint

| Metric             | Before       | After        | Change                    |
| ------------------ | ------------ | ------------ | ------------------------- |
| Avg. Response Time | 62ms         | 26ms         | ⬇️ 58% faster             |
| Total Duration     | 8.19s        | 6.15s        | ⬇️ 25% faster             |
| CPU Usage          | 44.8%        | 6.6%         | ⬇️ 85% reduction          |
| Heap Used          | ~70 MB       | ~64 MB       | ⬇️ Slightly lower         |
| Live Threads       | 41           | 45           | ➡️ Stable (minor increase) |
| Class Loading      | +783         | +1,017       | ⬆️ More class loading      |

**Key Takeaway:** Create Post saw dramatic improvements — response time dropped by 58% and CPU usage dropped by ~85%, indicating significantly more efficient write processing after optimization.

---

## 2. Get Posts Sorted by Date Endpoint

| Metric             | Before       | After        | Change                        |
| ------------------ | ------------ | ------------ | ----------------------------- |
| Avg. Response Time | 61ms         | 73ms         | ⬆️ 20% slower                 |
| Total Duration     | 7.93s        | 8.56s        | ⬆️ Slightly longer            |
| CPU Usage          | 11.9%        | 5.4%         | ⬇️ 55% reduction              |
| Heap Used          | ~59 MB       | ~49 MB       | ⬇️ Lower memory usage         |
| Live Threads       | 41           | 44           | ➡️ Stable                     |
| Class Loading      | +132         | +3           | ⬇️ Near-zero loading activity |

**Key Takeaway:** While CPU and memory usage improved, the read endpoint became slightly slower after optimization (61ms → 73ms), possibly due to increased data volume, sorting complexity, or query changes.

---

## 3. Side-by-Side Comparison

| Metric                      | Before — Create | After — Create | Before — Get | After — Get |
| --------------------------- | --------------- | -------------- | ------------ | ----------- |
| Avg. Response Time          | 62ms            | **26ms** ✅    | 61ms         | 73ms ⚠️     |
| Total Duration              | 8.19s           | **6.15s** ✅   | 7.93s        | 8.56s ⚠️    |
| CPU Usage                   | 44.8%           | **6.6%** ✅    | 11.9%        | **5.4%** ✅  |
| Heap Used                   | ~70 MB          | **~64 MB** ✅  | ~59 MB       | **~49 MB** ✅|
| Live Threads                | 41              | 45             | 41           | 44          |

---

## 4. JVM Health (Both Cycles)

| Indicator               | Before | After  |
| ----------------------- | ------ | ------ |
| Memory Leak Detected    | ❌ No  | ❌ No  |
| Thread Leak Detected    | ❌ No  | ❌ No  |
| Abnormal GC Activity    | ❌ No  | ❌ No  |
| Saw-tooth Heap Pattern  | ✅ Yes | ✅ Yes |
| Stable Thread Count     | ✅ Yes | ✅ Yes |

JVM health remained stable across both test cycles with healthy garbage collection and no signs of leaks or starvation.

---

## 5. Key Findings

1. **Write performance improved significantly** — Create Post response time fell from 62ms to 26ms, and CPU usage dropped from 44.8% to 6.6%.
2. **Read performance regressed slightly** — Get Posts response time increased from 61ms to 73ms; likely caused by sorting logic or increased data volume.
3. **Overall resource efficiency improved** — Both endpoints show lower CPU and heap usage after optimization.
4. **JVM stability maintained** — No memory leaks, thread issues, or GC anomalies were observed in either cycle.

---

## 6. Recommendations

1. Investigate the read endpoint regression — analyze database indexing and query execution plans for the sorted query.
2. Conduct concurrent load testing (e.g., 200–1000 users) to validate performance at scale.
3. Enable detailed GC logging to support deeper memory analysis.
4. Perform stress testing (e.g., JMeter) to determine the system's breaking point.
5. Continue monitoring CPU usage under concurrent write loads to confirm scalability of Create Post gains.

---

## 7. Conclusion

Optimization efforts yielded substantial improvements for the **Create Post** endpoint, with response time and CPU usage both reduced dramatically. The **Get Posts Sorted by Date** endpoint maintained healthy JVM behavior but exhibited a minor response time regression that warrants further investigation. Overall, the application is stable, efficient, and shows no critical JVM or performance issues in either test cycle.
