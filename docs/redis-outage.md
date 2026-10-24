# Postmortem: Redis outage caused 500 errors on doctor lookup

**Date:** 2026-10-24 | **Severity:** Medium (simulated) | **Status:** Fix attempted, not yet verified

## Summary
Redis was stopped to simulate an outage. `GET /api/doctors/1` returned
500 "Redis command timed out" although the data was safe in MySQL.

## Impact
- Every cached endpoint failed while Redis was down
- Requests hung for about 60 seconds (default Redis timeout) before failing
- Logs filled with repeated stack traces from reconnect attempts

## Timeline
- ~06:47 Redis container stopped
- 06:47:23 First failing request: 500 "Redis command timed out"
- 06:48 Logs show DnsErrorCauseException: Query failed with SERVFAIL (Redis hostname no longer resolving)
- Redis restarted: service recovered by itself, no app restart needed (200 OK)

## Root cause
Redis is a cache, but the app treated it as a hard dependency. Spring's
@Cacheable calls Redis first, and with no cache error handling, the
failure reached the user.

## Fix attempt
Added a CacheErrorHandler to fall back to MySQL, and set
spring.data.redis.timeout=1s. A retest at 07:12 still returned a 500
after 1m0.84s, and no handler log line appeared, so it is unverified.
Likely cause: the running image was not rebuilt. Next step: clean
rebuild (`docker compose build --no-cache app`) and retest.

## Prevention / action items
- [ ] Verify the cache fallback after a clean rebuild
- [ ] Shorter Redis timeout
- [ ] Replace println with a proper logger
- [ ] Health check endpoint and an alert on Redis failures
- [ ] Evict `allDoctors` cache on create/update (found stale-data risk)