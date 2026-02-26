package com.amalitech.SpringBootBloggingApp.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

/**
 * Lock-free, thread-safe in-memory metrics store.
 *
 * <p>Tracks per-method performance data fed by {@link
 * com.amalitech.SpringBootBloggingApp.aspects.PerformanceAspect}:
 * <ul>
 *   <li><b>invocationCount</b> – how many times the method was called</li>
 *   <li><b>totalLatencyMs</b>  – cumulative execution time in ms</li>
 *   <li><b>maxLatencyMs</b>    – worst-case single execution time in ms</li>
 *   <li><b>errorCount</b>      – number of invocations that threw an exception</li>
 * </ul>
 *
 * <p>All updates use {@link LongAdder} (high-contention increment) and
 * {@link AtomicLong} (CAS-based max update), so no locks are needed even
 * under heavy concurrent load.
 */
@Service
public class MetricsService {

    /**
     * Per-method statistics bucket.
     * Uses {@link LongAdder} for invocation/latency (many writers, infrequent read)
     * and {@link AtomicLong} for max (compare-and-swap update).
     */
    public static class MethodMetrics {
        private final LongAdder invocationCount = new LongAdder();
        private final LongAdder totalLatencyMs  = new LongAdder();
        private final AtomicLong maxLatencyMs   = new AtomicLong(0);
        private final LongAdder errorCount      = new LongAdder();

        void record(long latencyMs) {
            invocationCount.increment();
            totalLatencyMs.add(latencyMs);
            // CAS loop to update max without a lock
            long prev;
            do {
                prev = maxLatencyMs.get();
            } while (latencyMs > prev && !maxLatencyMs.compareAndSet(prev, latencyMs));
        }

        void recordError() {
            errorCount.increment();
        }

        public long getInvocationCount() { return invocationCount.sum(); }
        public long getTotalLatencyMs()  { return totalLatencyMs.sum(); }
        public long getMaxLatencyMs()    { return maxLatencyMs.get(); }
        public long getErrorCount()      { return errorCount.sum(); }

        public double getAvgLatencyMs() {
            long count = invocationCount.sum();
            return count == 0 ? 0.0 : (double) totalLatencyMs.sum() / count;
        }
    }

    /** methodName → MethodMetrics */
    private final ConcurrentHashMap<String, MethodMetrics> registry = new ConcurrentHashMap<>();

    // ------------------------------------------------------------------ //
    //  Write API (called by PerformanceAspect)                            //
    // ------------------------------------------------------------------ //

    /**
     * Record a successful invocation for the given method.
     *
     * @param methodName  fully-qualified or simple method name
     * @param latencyMs   wall-clock execution time in milliseconds
     */
    public void record(String methodName, long latencyMs) {
        registry.computeIfAbsent(methodName, k -> new MethodMetrics())
                .record(latencyMs);
    }

    /**
     * Record a failed invocation (exception thrown).
     * Latency is still counted so averages stay accurate.
     *
     * @param methodName  fully-qualified or simple method name
     * @param latencyMs   wall-clock time until the exception was thrown
     */
    public void recordError(String methodName, long latencyMs) {
        MethodMetrics m = registry.computeIfAbsent(methodName, k -> new MethodMetrics());
        m.record(latencyMs);
        m.recordError();
    }

    // ------------------------------------------------------------------ //
    //  Read API (called by MetricsController)                             //
    // ------------------------------------------------------------------ //

    /**
     * Returns an immutable snapshot of all tracked method metrics as a
     * plain {@link Map} safe for JSON serialisation.
     */
    public Map<String, Map<String, Object>> getSnapshot() {
        return registry.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        e -> toMap(e.getValue())));
    }

    /** Returns metrics for a single method, or {@code null} if not tracked yet. */
    public Map<String, Object> getMethodSnapshot(String methodName) {
        MethodMetrics m = registry.get(methodName);
        return m == null ? null : toMap(m);
    }

    /** Total number of distinct methods currently tracked. */
    public int getTrackedMethodCount() {
        return registry.size();
    }

    /** Resets all collected metrics (useful for benchmarking warm-up phases). */
    public void reset() {
        registry.clear();
    }

    // ------------------------------------------------------------------ //
    //  Helpers                                                             //
    // ------------------------------------------------------------------ //

    private Map<String, Object> toMap(MethodMetrics m) {
        return Map.of(
                "invocations",    m.getInvocationCount(),
                "totalLatencyMs", m.getTotalLatencyMs(),
                "avgLatencyMs",   Math.round(m.getAvgLatencyMs() * 100.0) / 100.0,
                "maxLatencyMs",   m.getMaxLatencyMs(),
                "errors",         m.getErrorCount()
        );
    }
}
