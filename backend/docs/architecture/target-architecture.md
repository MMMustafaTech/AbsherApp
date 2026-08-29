# Target Clean Architecture

## Dependency direction

```text
REST controllers / request-response DTOs
                 |
                 v
Application use cases and ports
                 |
                 v
Domain models and business rules
                 ^
                 |
JPA adapters, token providers, mail/OTP providers, audit sink
```

The domain has no dependency on Spring MVC, Spring Security, JPA, HTTP, or MySQL. Infrastructure implements ports declared by the application layer.

## Package layout

```text
com.absher.absherapp
├── shared
│   ├── domain
│   ├── application
│   ├── infrastructure
│   └── presentation
├── account
├── citizen
├── document
├── request
└── shared
```

Each feature has the same internal structure:

```text
feature/
├── domain/          # aggregates, value objects, domain rules
├── application/     # commands, queries, use cases, ports
└── infrastructure/  # JPA adapters, external providers, and web controllers/DTOs
```

## Controller and use-case rules

- Controllers accept validated request DTOs and do not expose JPA entities.
- Controllers do not access repositories, JPA entities, or security-provider internals.
- A use case owns transaction boundaries and authorization checks for its business operation.
- JPA entities remain inside `infrastructure`; mappers translate them to domain objects.
- Queries return purpose-built response models, never entities.
- Cross-context access occurs through a port/use-case contract, never another context's repository.

## API direction

- Public endpoints will move to `/api/v1`.
- Citizen endpoints will use `/me` rather than a client-supplied national ID.
- Administrative access will be in explicitly role-protected routes.
- Errors will move to a consistent RFC 9457 `ProblemDetail` representation with a stable application error code and correlation ID in a later API-hardening pass.
- API documentation is development-only until each endpoint has an authorization policy and examples without real personal data.

## Authentication direction

The legacy public login/register boundary has been replaced by:

1. verified account enrollment through an OTP or approved identity-provider port;
2. an application authentication use case;
3. short-lived access tokens plus hashed, rotating refresh sessions;
4. role and citizen-ownership checks at every protected use case;
5. audit events for login, token refresh, document access, and workflow actions.

The actual OTP/identity provider is an infrastructure decision and will be configurable; no provider credentials or external integration will be embedded in the domain. Local and test profiles currently use a development sender only.
