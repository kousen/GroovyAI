package com.kousenit

import com.github.tomakehurst.wiremock.WireMockServer
import groovy.json.JsonSlurper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import java.time.Duration

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

class OpenAIClientSpec extends Specification {

    @Shared
    WireMockServer wireMockServer

    @Subject
    OpenAIClient client

    def jsonSlurper = new JsonSlurper()

    def setupSpec() {
        wireMockServer = new WireMockServer(wireMockConfig().port(8089))
        wireMockServer.start()
    }

    def cleanupSpec() {
        wireMockServer.stop()
    }

    def setup() {
        client = new OpenAIClient('http://localhost:8089')
        client.apiKey = 'test-api-key'
        wireMockServer.resetAll()
    }

    def "should make successful API call with correct request format"() {
        given:
        wireMockServer.stubFor(post(urlEqualTo('/v1/responses'))
            .withHeader('Content-Type', equalTo('application/json'))
            .withHeader('Authorization', equalTo('Bearer test-api-key'))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader('Content-Type', 'application/json')
                .withBody('{"output":[{"type":"message","content":[{"text":"Test response"}]}]}')))

        and:
        def apiCall = new ApiCall(
            model: 'gpt-4',
            input: 'Test prompt',
            timeout: Duration.ofMinutes(2),
            summary: SummaryType.DETAILED,
            effort: ReasoningEffort.HIGH
        )

        when:
        def response = client.call(apiCall)

        then:
        response.statusCode() == 200
        def body = jsonSlurper.parseText(response.body())
        body.output[0].content[0].text == 'Test response'

        and: "verify the request body was sent correctly"
        wireMockServer.verify(postRequestedFor(urlEqualTo('/v1/responses'))
            .withRequestBody(matchingJsonPath('$.model', equalTo('gpt-4')))
            .withRequestBody(matchingJsonPath('$.input[0].role', equalTo('user')))
            .withRequestBody(matchingJsonPath('$.input[0].content', equalTo('Test prompt')))
            .withRequestBody(matchingJsonPath('$.reasoning.summary', equalTo('detailed')))
            .withRequestBody(matchingJsonPath('$.reasoning.effort', equalTo('high'))))
    }

    def "should handle API error responses"() {
        given:
        wireMockServer.stubFor(post(urlEqualTo('/v1/responses'))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader('Content-Type', 'application/json')
                .withBody('{"error": "Bad Request"}')))

        and:
        def apiCall = new ApiCall(model: 'gpt-4', input: 'Test prompt')

        when:
        def response = client.call(apiCall)

        then:
        response.statusCode() == 400
        response.body() == '{"error": "Bad Request"}'
    }

    def "should use environment API key when available"() {
        given:
        def originalClient = new OpenAIClient()
        
        expect:
        originalClient.apiKey == System.getenv('OPENAI_API_KEY')
    }

    def "should send correct JSON structure with default values"() {
        given:
        wireMockServer.stubFor(post(urlEqualTo('/v1/responses'))
            .willReturn(aResponse().withStatus(200).withBody('{}')))

        and:
        def apiCall = new ApiCall(model: 'gpt-4', input: 'Test prompt')

        when:
        client.call(apiCall)

        then:
        wireMockServer.verify(postRequestedFor(urlEqualTo('/v1/responses'))
            .withRequestBody(matchingJsonPath('$.reasoning.summary', equalTo('auto')))
            .withRequestBody(matchingJsonPath('$.reasoning.effort', equalTo('medium'))))
    }

    def "should respect timeout configuration"() {
        given:
        wireMockServer.stubFor(post(urlEqualTo('/v1/responses'))
            .willReturn(aResponse()
                .withStatus(200)
                .withFixedDelay(100) // Small delay to test timeout handling
                .withBody('{}')))

        and:
        def apiCall = new ApiCall(
            model: 'gpt-4', 
            input: 'Test', 
            timeout: Duration.ofMillis(50)
        )

        when:
        client.call(apiCall)

        then:
        thrown(Exception) // Should timeout
    }
}