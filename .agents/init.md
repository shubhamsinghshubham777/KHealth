---
description: Entrypoint initialization document for AI agents operating in the KHealth repository.
always_on: true
---

# KHealth Agent Initialization

Welcome to the **KHealth** repository!

This repository contains the KHealth Kotlin Multiplatform library for Android Health Connect and Apple HealthKit, alongside sample applications.

## Quick Index for AI Agents

- **Architecture & Codebase Overview**: Read [.agents/README.md](file:///Users/shubham/Projects/Personal/KHealth/.agents/README.md) for full context, module structure, design principles, and guidelines.
- **Architectural Rules**: See [.agents/rules/architecture.md](file:///Users/shubham/Projects/Personal/KHealth/.agents/rules/architecture.md) for automated rules applied during code generation.
- **Public API Documentation**: See [README.md](file:///Users/shubham/Projects/Personal/KHealth/README.md) for consumer-facing API docs and setup examples.

## Key Tenets

1. **Zero-Arg Factory in `commonMain`**: `val kHealth = KHealth()` works in shared code.
2. **Android Auto-Init**: Handled by `KHealthInitProvider`. Do not mandate manual `initialise()` calls.
3. **Type-Safe Generic Reads**: `KHReadRequest<T : KHRecord>` returns `List<T>` directly.
4. **Single-Responsibility Mappers**: Do not place business logic or mappings directly in `KHealth.apple.kt` or `KHealth.android.kt`. Delegate to specialized reader/writer/permission mappers.
5. **Normalized BMR**: `KHUnit.Energy` (kcal/day). `KHEither` is deprecated and removed.

Refer to [.agents/README.md](file:///Users/shubham/Projects/Personal/KHealth/.agents/README.md) for complete workflows and verification commands.
