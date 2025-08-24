package com.kousenit

import groovy.json.JsonSlurper
import spock.lang.Specification
import spock.lang.Subject

import java.time.Duration

class ApiCallSpec extends Specification {

    def jsonSlurper = new JsonSlurper()

    def "should generate correct JSON with default values"() {
        given:
        @Subject
        def apiCall = new ApiCall(
            model: 'gpt-4',
            input: 'Write a haiku about testing'
        )

        when:
        def json = apiCall.toJson()
        def parsed = jsonSlurper.parseText(json)

        then:
        parsed.model == 'gpt-4'
        parsed.input == [[role: 'user', content: 'Write a haiku about testing']]
        parsed.reasoning.summary == 'auto'
        parsed.reasoning.effort == 'medium'
    }

    def "should generate correct JSON with custom values"() {
        given:
        @Subject
        def apiCall = new ApiCall(
            model: 'gpt-5-nano',
            input: 'Explain quantum computing',
            timeout: Duration.ofMinutes(5),
            summary: SummaryType.DETAILED,
            effort: ReasoningEffort.HIGH
        )

        when:
        def json = apiCall.toJson()
        def parsed = jsonSlurper.parseText(json)

        then:
        parsed.model == 'gpt-5-nano'
        parsed.input == [[role: 'user', content: 'Explain quantum computing']]
        parsed.reasoning.summary == 'detailed'
        parsed.reasoning.effort == 'high'
    }

    def "should generate valid JSON structure for all enum combinations"() {
        expect:
        def apiCall = new ApiCall(
            model: 'gpt-4',
            input: 'Test input',
            summary: summaryType,
            effort: effortType
        )
        
        def json = apiCall.toJson()
        def parsed = jsonSlurper.parseText(json)
        
        parsed.reasoning.summary == expectedSummary
        parsed.reasoning.effort == expectedEffort

        where:
        summaryType         | effortType             | expectedSummary | expectedEffort
        SummaryType.AUTO    | ReasoningEffort.LOW    | 'auto'          | 'low'
        SummaryType.CONCISE | ReasoningEffort.MEDIUM | 'concise'       | 'medium'
        SummaryType.DETAILED| ReasoningEffort.HIGH   | 'detailed'      | 'high'
    }

    def "should handle special characters in input"() {
        given:
        @Subject
        def apiCall = new ApiCall(
            model: 'gpt-4',
            input: 'Text with "quotes" and \n newlines & special chars'
        )

        when:
        def json = apiCall.toJson()
        def parsed = jsonSlurper.parseText(json)

        then:
        parsed.input[0].content == 'Text with "quotes" and \n newlines & special chars'
        notThrown(Exception)
    }

    def "should maintain input as user message format"() {
        given:
        @Subject
        def apiCall = new ApiCall(model: 'gpt-4', input: 'Any input text')

        when:
        def json = apiCall.toJson()
        def parsed = jsonSlurper.parseText(json)

        then:
        parsed.input instanceof List
        parsed.input.size() == 1
        parsed.input[0].role == 'user'
        parsed.input[0].content == 'Any input text'
    }
}