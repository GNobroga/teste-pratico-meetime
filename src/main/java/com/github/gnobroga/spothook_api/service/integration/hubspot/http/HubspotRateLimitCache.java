package com.github.gnobroga.spothook_api.service.integration.hubspot.http;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

@Service
public class HubspotRateLimitCache {

    public static final String RATE_LIMIT_MAX = "X-HubSpot-RateLimit-Max";
    public static final String RATE_LIMIT_REMAINING = "X-HubSpot-RateLimit-Remaining";
    public static final String RATE_LIMIT_INTERVAL = "X-HubSpot-RateLimit-Interval-Milliseconds";

    private final AtomicInteger max = new AtomicInteger(0);
    private final AtomicInteger remaining = new AtomicInteger(0);
    private final AtomicLong intervalMs = new AtomicLong(0);
    private final AtomicLong lastUpdated = new AtomicLong(0);

    public void updateFromHeaders(Optional<String> maxOpt, Optional<String> remainingOpt, Optional<String> intervalOpt) {
        maxOpt.ifPresent(val -> this.max.set(Integer.parseInt(val)));
        remainingOpt.ifPresent(val -> this.remaining.set(Integer.parseInt(val)));
        intervalOpt.ifPresent(val -> this.intervalMs.set(Long.parseLong(val)));
        this.lastUpdated.set(System.currentTimeMillis());
    }

    public boolean shouldWait() {
        if (remaining.get() > 0) return false;
        long elapsed = System.currentTimeMillis() - lastUpdated.get();
        return elapsed < intervalMs.get();
    }

    public long getTimeInMsToWait() {
        return Math.max(0, intervalMs.get() - (System.currentTimeMillis() - lastUpdated.get()));
    }
    
}
