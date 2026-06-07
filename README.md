# URL Shortener Service

A production-style REST API built with Spring Boot that shortens URLs, tracks click analytics, and uses Redis caching for fast redirects.

---

## Tech Stack

- **Java 26** — core language
- **Spring Boot 4.x** — web, JPA, validation, async
- **PostgreSQL** — primary data store
- **Redis / Valkey** — caching layer for fast redirects
- **Flyway** — database schema migrations
- **HikariCP** — connection pooling

---

## Features

- Shorten any URL with an auto-generated Base62 encoded short code
- Optional custom alias (e.g. `/api/shorten` with `customAlias: "mylink"`)
- Optional URL expiry — expired URLs return a 410 Gone response
- 302 redirect with Redis cache-aside pattern — cache hits skip the database entirely
- Async click event recording — redirect response is never blocked by analytics writes
- Click analytics per short URL — total clicks and daily breakdown
- Cache eviction on URL deletion
- Input validation with meaningful error responses
- Global exception handler returning structured JSON errors

---

## Architecture

```
Client
  │
  ▼
UrlController
  ├── POST /api/shorten       → UrlService.shorten()
  ├── GET  /{code}            → UrlService.redirect() + AnalyticsService.recordClick() [async]
  ├── GET  /api/urls/{code}   → UrlService.getMetadata()
  └── DELETE /api/urls/{code} → UrlService.deleteUrl()

AnalyticsController
  └── GET /api/stats/{code}   → AnalyticsService.getStats()

UrlService
  ├── Checks Redis cache first on redirect
  ├── Falls back to PostgreSQL on cache miss
  └── Populates Redis with TTL on miss

AnalyticsService (@Async)
  ├── Fetches ShortUrl from DB
  ├── Saves ClickEvent (ip, user-agent, referrer)
  └── Increments totalClicks counter
```

---

## Database Schema

### short_urls
| Column | Type | Notes |
|---|---|---|
| id | BIGSERIAL | Primary key, sequence starts at 100000 |
| short_code | VARCHAR(10) | Unique, Base62 encoded from id |
| original_url | TEXT | Not null |
| custom_alias | VARCHAR(30) | Optional |
| expires_at | TIMESTAMP | Optional, null means never expires |
| created_at | TIMESTAMP | Auto-set on insert |
| updated_at | TIMESTAMP | Auto-updated on change |
| total_clicks | BIGINT | Incremented async on every redirect |

### click_events
| Column | Type | Notes |
|---|---|---|
| id | BIGSERIAL | Primary key |
| short_url_id | BIGINT | Foreign key → short_urls(id) ON DELETE CASCADE |
| clicked_at | TIMESTAMP | Auto-set on insert |
| ip_address | VARCHAR(45) | IPv4 and IPv6 support |
| user_agent | TEXT | Browser/client info |
| referrer | TEXT | Referring page |
| country_code | VARCHAR(2) | Reserved for future GeoIP integration |

---

## API Endpoints

### Shorten a URL
```
POST /api/shorten
Content-Type: application/json

{
  "originalUrl": "https://www.example.com/very/long/url",
  "customAlias": "mylink",
  "expiresAt": "2026-12-31T00:00:00"
}
```

**Response — 201 Created**
```json
{
  "shortCode": "Xam",
  "shortUrl": "http://localhost:8080/Xam",
  "originalUrl": "https://www.example.com/very/long/url",
  "customAlias": "mylink",
  "createdAt": "2026-06-08T00:00:00",
  "expiresAt": "2026-12-31T00:00:00"
}
```

---

### Redirect
```
GET /{code}
```
Returns **302 Found** with `Location` header pointing to the original URL.

---

### Get URL Metadata
```
GET /api/urls/{code}
```

**Response — 200 OK**
```json
{
  "shortCode": "Xam",
  "originalUrl": "https://www.example.com/very/long/url",
  "customAlias": "mylink",
  "createdAt": "2026-06-08T00:00:00",
  "expiresAt": "2026-12-31T00:00:00"
}
```

---

### Get Click Analytics
```
GET /api/stats/{code}
```

**Response — 200 OK**
```json
{
  "shortCode": "Xam",
  "originalUrl": "https://www.example.com/very/long/url",
  "totalClicks": 42,
  "clicksPerDay": {
    "2026-06-06": 15,
    "2026-06-07": 20,
    "2026-06-08": 7
  }
}
```

---

### Delete a URL
```
DELETE /api/urls/{code}
```
Returns **204 No Content**. Also evicts the entry from Redis cache.

---

## Error Responses

All errors return structured JSON:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "No URL found for short code: abc",
  "timestamp": "2026-06-08T00:00:00"
}
```

| Scenario | Status |
|---|---|
| Short code not found | 404 Not Found |
| URL has expired | 410 Gone |
| Invalid request body | 400 Bad Request |
| Unexpected error | 500 Internal Server Error |

---

## Redis Caching

The redirect endpoint uses a **cache-aside pattern**:

1. Check Redis for `url:{code}`
2. **Cache hit** → return `originalUrl` immediately, no DB call
3. **Cache miss** → query PostgreSQL, populate Redis with TTL, return `originalUrl`
4. On URL deletion → evict `url:{code}` from Redis

Default TTL is **3600 seconds (1 hour)**, configurable via `app.redis.ttl` in `application.yml`.

---

## How to Run Locally

### Prerequisites
- Java 26+
- PostgreSQL running on port 5432
- Redis or Valkey running on port 6379
- Maven

### 1. Create the database
```sql
CREATE DATABASE url_shortener_db;
```

### 2. Configure `application.yml`
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/url_shortener_db
    username: your_username
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379

app:
  base-url: http://localhost:8080
  redis:
    ttl: 3600
```

### 3. Run the application
```bash
mvn spring-boot:run
```

Flyway will automatically create all tables on first startup.

### 4. Test with Postman
Import the endpoints above and start with `POST /api/shorten`.

---

## Project Structure

```
src/main/java/com/yourname/urlshortener/
├── controller/
│   ├── UrlController.java
│   └── AnalyticsController.java
├── service/
│   ├── UrlService.java
│   └── AnalyticsService.java
├── repository/
│   ├── UrlRepository.java
│   └── ClickEventRepository.java
├── entity/
│   ├── ShortUrl.java
│   └── ClickEvent.java
├── dto/
│   ├── ShortenRequest.java
│   ├── ShortenResponse.java
│   └── AnalyticsResponse.java
├── exception/
│   ├── UrlNotFoundException.java
│   ├── UrlExpiredException.java
│   └── GlobalExceptionHandler.java
├── config/
│   └── RedisConfig.java
└── util/
    └── Base62Encoder.java

src/main/resources/
├── application.yml
└── db/migration/
    ├── V1__create_short_urls.sql
    ├── V2__reset_sequence.sql
    └── V3__create_click_events.sql
```

---

## Key Design Decisions

**Base62 encoding** — short codes are derived from the auto-incremented database ID encoded in Base62. This guarantees uniqueness without collision checks and produces short, alphanumeric codes.

**Async analytics** — click recording runs on a separate thread via `@Async` so the redirect response is returned to the user immediately without waiting for the DB write.

**Cache-aside over Spring `@Cacheable`** — `RedisTemplate` is used directly for full control over key naming, TTL per entry, and explicit cache eviction on delete.

**Flyway over `ddl-auto`** — schema is managed entirely through versioned SQL migration files, matching production best practices.

**Separation of concerns** — redirect URL lookup and analytics recording are in separate services. Neither service depends on the other — the controller orchestrates both.

---

## Future Improvements

- Rate limiting per IP using `HandlerInterceptor`
- GeoIP country detection via MaxMind GeoLite2
- JWT authentication for user-owned links
- Docker Compose setup for local dev parity
- Swagger / OpenAPI documentation
