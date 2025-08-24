# Claude Code Configuration

This file contains configuration and instructions for Claude Code when working with this project.

## Project Overview

This is a Groovy client library for OpenAI's Responses API, built with:
- Groovy 4.0.22 with records and enums for type safety
- Comprehensive testing using Spock Framework 2.3
- WireMock for HTTP mocking in unit tests
- Real API integration tests

## Build Commands

### Testing
```bash
./gradlew test
```

### Running the Demo
```bash
./gradlew run
```

### Clean Build
```bash
./gradlew clean build
```

## Project Structure

- `src/main/groovy/com/kousenit/` - Main source code
  - `OpenAIClient.groovy` - HTTP client for OpenAI API
  - `ApiCall.groovy` - Request data record with JSON serialization
  - `SummaryType.groovy` - Enum for summary types (AUTO, CONCISE, DETAILED)
  - `ReasoningEffort.groovy` - Enum for effort levels (LOW, MEDIUM, HIGH)
  - `Main.groovy` - Demo script showing usage

- `src/test/groovy/com/kousenit/` - Test code
  - `OpenAIClientSpec.groovy` - Unit tests using WireMock
  - `OpenAIIntegrationSpec.groovy` - Integration tests with real API

## Important Notes

### API Key Requirements
- Integration tests require `OPENAI_API_KEY` environment variable
- Tests are automatically skipped if API key is not present (`@IgnoreIf` annotation)

### Model Compatibility
- Different OpenAI models support different parameter combinations
- Example: `gpt-5-nano` only supports `'detailed'` and `'auto'` summary types, not `'concise'`
- Always verify parameter support for specific models when adding new tests

### Security
- Build file includes forced dependency versions to address CVEs
- Uses `resolutionStrategy.force()` for vulnerable transitive dependencies from WireMock

### Groovy Version
- Currently uses Groovy 4.0.22 due to Spock compatibility
- Core code is compatible with Groovy 5.0.0
- Will upgrade when Spock releases Groovy 5 support

## Common Tasks

When working on this project:

1. **Adding new tests**: Follow Spock BDD style, use WireMock for HTTP mocking
2. **API changes**: Update both unit tests (mocked) and integration tests (real API)
3. **Model support**: Check OpenAI documentation for parameter compatibility
4. **Dependencies**: Be mindful of security vulnerabilities in transitive dependencies

## Test Execution Notes

- Unit tests (OpenAIClientSpec): Fast, no external dependencies
- Integration tests (OpenAIIntegrationSpec): Slower, require internet and API key
- All tests should pass before committing changes
- Integration tests validate real API behavior and parameter support