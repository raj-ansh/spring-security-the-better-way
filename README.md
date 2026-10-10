# Spring Security: The Better Way — Companion Code

Source code for the book **Spring Security: The Better Way — A Beginner's Step-by-Step Guide to Authentication, Authorization and the Security Filter Chain** by Rajkumar Panda.

Every project was built and tested with:

| Tool | Version |
|---|---|
| Java | 21 |
| Spring Boot | 4.1.1 |
| Spring Security | 7.1.1 |
| Maven | 3.9.x |

## Which folder belongs to which chapter?

| Folder | Chapters | What it contains |
|---|---|---|
| [`password-storage-demo`](password-storage-demo) | 2, 5 | A plain Java program: password encoding, encryption, concurrency, Jackson and localization helpers |
| [`01-hello-spring-security`](01-hello-spring-security) | 7, 8 | The first secured application (no configuration), plus the Chapter 8 `WhoAmIController` and `AuthenticationEvents` |
| [`02-rules-and-users`](02-rules-and-users) | 9 | Your first `SecurityFilterChain`: public and protected URLs, in-memory users, `PasswordEncoder`, roles |
| [`spring-security-demo`](spring-security-demo) | 11–19 | The final state after Chapter 17: two filter chains, `RequestTimingFilter`, `TenantFilter`, JSON error handling |
| [`spring-security-jwt`](spring-security-jwt) | 20 | A stateless REST API that issues and verifies JWTs |

The three `spring-security-demo` stages (`01-…`, `02-…` and `spring-security-demo`) are the same project, `com.example.security`, captured at different points in the book.

## How to run a project

Open a terminal in the project folder, for example:

```bash
cd 02-rules-and-users
mvn spring-boot:run
```

The application starts on `http://localhost:8080`. Then follow the curl commands in the matching chapter.

`password-storage-demo` is not a web application. Run it with:

```bash
cd password-storage-demo
mvn package
java -jar target/password-storage-demo-1.0.0.jar
```

## Users in the examples

| Username | Password | Roles | Projects |
|---|---|---|---|
| `user` | printed in the console at startup | — | `01-hello-spring-security` |
| `alice` | `alice123` | USER | `02-…`, `spring-security-demo`, `spring-security-jwt` |
| `mike` | `mike123` | USER, MANAGER | `02-…`, `spring-security-demo` |
| `admin` | `admin123` | USER, ADMIN | `02-…`, `spring-security-demo`, `spring-security-jwt` |

> These users and passwords are for learning only. Never use in-memory demo users or the demo JWT secret in production.

## Quick test (spring-security-demo)

```bash
curl http://localhost:8080/api/public/hello                     # 200
curl http://localhost:8080/api/products                         # 401
curl -u alice:alice123 http://localhost:8080/api/products       # 200
curl -u alice:alice123 http://localhost:8080/api/admin/users    # 403
curl -u admin:admin123 http://localhost:8080/api/admin/users    # 200
```

## License

The code is released under the [MIT License](LICENSE): you may use it freely in your own projects. The book text is not part of this license.
