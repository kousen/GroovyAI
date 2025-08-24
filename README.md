# GroovyAI

A clean, type-safe Groovy client for OpenAI's Responses API, featuring comprehensive testing with Spock Framework.

## Features

- **Type-safe API calls** using Groovy 4 records and enums
- **Configurable reasoning parameters** (summary and effort levels)
- **Comprehensive test coverage** with Spock Framework and WireMock
- **Security-focused dependencies** with vulnerability mitigations
- **Clean separation of concerns** with organized package structure

## Project Structure

```
src/main/groovy/com/kousenit/
├── ApiCall.groovy          # Record for API request data
├── OpenAIClient.groovy     # HTTP client for OpenAI API
├── ReasoningEffort.groovy  # Enum for effort levels (LOW, MEDIUM, HIGH)
├── SummaryType.groovy      # Enum for summary types (AUTO, DETAILED) - CONCISE not supported by all models
└── Main.groovy            # Usage example
```

## Usage

```groovy
// Create client and request
def client = new OpenAIClient()
def request = new ApiCall(
    model: 'gpt-5-nano',
    input: 'Write a haiku about Groovy programming.',
    summary: SummaryType.DETAILED,
    effort: ReasoningEffort.HIGH
)

// Make API call
def response = client.call(request)

// Process response
if (response.statusCode() == 200) {
    def result = new JsonSlurper().parseText(response.body())
    // Extract reasoning and content...
}
```

### Demo Script

For a complete working example, see `Main.groovy` which demonstrates:
- API client usage with default parameters
- Response processing and error handling
- Extracting both reasoning summaries and message content

Run the demo with:
```bash
./gradlew run
```

## Configuration

Set your OpenAI API key as an environment variable:
```bash
export OPENAI_API_KEY=your_api_key_here
```

## Testing

The project includes comprehensive tests using Spock Framework with WireMock for HTTP mocking:

```bash
./gradlew test
```

### Test Coverage

- **OpenAIClientSpec**: Unit tests using WireMock for HTTP client functionality, error handling, and timeout behavior
- **OpenAIIntegrationSpec**: Integration tests with real OpenAI API calls (requires API key)

**Note**: Integration tests are automatically skipped if the `OPENAI_API_KEY` environment variable is not set.

## Dependencies

- **Groovy 4.0.22**: Modern Groovy features and performance
- **Spock 2.3**: BDD-style testing framework
- **WireMock 3.3.1**: HTTP service mocking for tests
- **Security**: Forced dependency versions to address CVEs

**Note on Groovy 5**: The core application code is fully compatible with Groovy 5.0.0. However, the project currently uses Groovy 4.0.22 due to Spock Framework's compatibility constraints. Once Spock releases a version supporting Groovy 5, this project will be updated accordingly.

**Model Compatibility**: Different OpenAI models support different parameter combinations. For example, `gpt-5-nano` only supports `'detailed'` and `'auto'` summary types, not `'concise'`. Always check the API documentation for model-specific limitations.

## Security

This project uses Gradle's dependency resolution strategy to force secure versions of transitive dependencies, addressing multiple CVEs in libraries like Jetty, Commons Lang, and others.

## Requirements

- Java 17+
- Gradle 8.14+

## License

MIT License - see [LICENSE](LICENSE) file for details.

## Development

The project demonstrates modern Groovy development practices:
- Records for immutable data structures
- Enums for type safety
- Package organization
- Comprehensive testing
- Security-conscious dependency management