# 🎟️ Sweepstakes API

> Backend system for organizing lottery sweepstakes ("bolões") among coworkers and friends — built with security, transparency, and proportional prize/quota distribution as core requirements.

This is a personal project I built to deepen my hands-on experience with **Java, Spring Boot, and clean backend architecture**, while solving a real problem: organizing group lottery pools fairly, where each participant's prize share matches exactly what they contributed.

## 🧠 The Problem

Group lottery pools are common between coworkers and friends, but they're usually managed informally (spreadsheets, WhatsApp group chats) — which leads to disputes over who paid what and how prizes should be split. This API models the full lifecycle of a pool:

1. An **organizer** creates a Pool (lottery type, dates, min/max quota value), verified via WhatsApp OTP.
2. **Players** join with a unique nickname and the max amount they're willing to bet.
3. When the pool closes, the system calculates the total amount raised and simulates the best possible combination of games for that budget.
4. Quotas are split into **R$0.01 units** and distributed proportionally to each player's contribution.
5. After the draw, prizes are distributed using the exact same proportional logic — down to the cent.

A second module, **Generic Pools**, generalizes the same proportional-distribution engine to non-lottery decisions/bets (custom options instead of lottery numbers), reusing the same domain model.

## 🏗️ Architecture & Design Decisions

- **Layered architecture** (`api` → `application/services` → `domain`) separating controllers/DTOs/mappers from business logic and persistence, to keep the domain testable and framework-agnostic.
- **Strategy Pattern** for prize/game calculations: `GameDistributionStrategy` implementations (`MegaSenaStrategy`, `LotofacilStrategy`, `QuinaStrategy`) are resolved at runtime by `GameDistributionStrategyFactory`, making it trivial to add new lottery types without touching existing logic.
- **Shared abstractions** (`BasePool`, `BaseParticipant`) between the lottery-pool and generic-pool domains, avoiding duplicated logic while letting each module evolve independently.
- **Proportional share calculator** (`ParticipantShareCalculator`) isolates the cent-accurate quota/prize math so it can be unit-tested independently of HTTP or persistence concerns.
- **Database versioning with Flyway**, so schema evolution is tracked and reproducible across environments.
- **OTP-based authentication via WhatsApp** instead of passwords, fitting the actual usage context (closed groups of coworkers/friends).
- **End-to-end test suite** (`PoolE2ETest`, `GenericPoolE2ETest`, `ConsistencyE2ETest`) validating full pool lifecycles — creation, participation, closing, and prize consistency — not just isolated units.

## 🚀 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5 (Web, Data JPA, Validation) |
| Database | MySQL + Flyway migrations |
| Testing | JUnit, Spring Boot Test, H2 (in-memory E2E) |
| Docs | Swagger / OpenAPI |
| Build | Maven |
| Deploy | Railway / Nixpacks |

## 📌 Core Modules

- **Auth** — OTP verification via WhatsApp, token issuance
- **Pools** — lottery sweepstakes lifecycle: creation, participation, closing report, financial distribution
- **Generic Pools** — same proportional-distribution engine applied to custom option-based pools
- **Game Distribution** — per-lottery-type strategies for simulating optimal games from a given budget
- **Financial Service** — proportional quota/prize calculations down to the cent

## 📍 Roadmap

- PIX payment integration (QR code generation, payment confirmation)
- Caixa Econômica Federal API integration for automatic draw results
- Geographic dashboard of active pools
- Historical reports and audit trail per pool

## 💬 About This Project

This is a self-driven project, not a production system for a company — built to practice designing a non-trivial domain (proportional financial distribution, strategy-based calculators, multi-module architecture) the way I'd want to find it in a real codebase: layered, tested, and documented.

If you're hiring for a backend role or just want to talk about the architecture decisions above, feel free to reach out — I'd love to discuss them.
