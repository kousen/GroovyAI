package com.kousenit

import groovy.json.JsonSlurper
import spock.lang.IgnoreIf
import spock.lang.Specification
import spock.lang.Subject

import java.time.Duration

@IgnoreIf({ !System.getenv('OPENAI_API_KEY') })
class OpenAIIntegrationSpec extends Specification {

    @Subject
    OpenAIClient client = new OpenAIClient()
    
    def jsonSlurper = new JsonSlurper()

    def "should make real API call with simple prompt"() {
        given:
        def request = new ApiCall(
            model: 'gpt-5-nano',
            input: 'Say "Hello integration test" and nothing else.',
            summary: SummaryType.DETAILED,
            effort: ReasoningEffort.LOW
        )

        when:
        def response = client.call(request)

        then:
        response.statusCode() == 200
        
        and: "response should contain expected structure"
        def body = jsonSlurper.parseText(response.body())
        body.output != null
        body.output instanceof List
        
        and: "should have message content"
        def messageOutput = body.output.find { it.type == 'message' }
        messageOutput != null
        messageOutput.content != null
        messageOutput.content instanceof List
        messageOutput.content[0].text != null
    }

    def "should handle different reasoning parameters"() {
        given:
        def request = new ApiCall(
            model: 'gpt-5-nano',
            input: 'Count to 3.',
            summary: SummaryType.DETAILED,
            effort: ReasoningEffort.HIGH,
            timeout: Duration.ofMinutes(2)
        )

        when:
        def response = client.call(request)

        then:
        response.statusCode() == 200
        
        and: "should include reasoning output"
        def body = jsonSlurper.parseText(response.body())
        def reasoningOutput = body.output?.find { it.type == 'reasoning' }
        reasoningOutput?.summary != null
    }

    def "should handle timeout appropriately"() {
        given:
        def request = new ApiCall(
            model: 'gpt-5-nano',
            input: 'Just say "quick response".',
            timeout: Duration.ofSeconds(30)  // reasonable timeout
        )

        when:
        def response = client.call(request)

        then:
        response.statusCode() == 200
        noExceptionThrown()
    }

    def "should validate API key requirement"() {
        given:
        def clientWithoutKey = new OpenAIClient()
        clientWithoutKey.apiKey = null
        def request = new ApiCall(model: 'gpt-5-nano', input: 'Test')

        when:
        def response = clientWithoutKey.call(request)

        then:
        response.statusCode() == 401 || response.statusCode() == 403
    }

    def "should work with default parameters like Main.groovy"() {
        given:
        def request = new ApiCall(
            model: 'gpt-5-nano',
            input: 'Say "Hello integration test" and nothing else.'
        )

        when:
        def response = client.call(request)

        then:
        response.statusCode() == 200
        
        and: "response should contain expected structure"
        def body = jsonSlurper.parseText(response.body())
        body.output != null
        body.output instanceof List
        
        and: "should have message content"
        def messageOutput = body.output.find { it.type == 'message' }
        messageOutput != null
        messageOutput.content != null
        messageOutput.content instanceof List
        messageOutput.content[0].text != null
    }
}