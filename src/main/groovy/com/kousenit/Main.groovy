package com.kousenit

import groovy.json.JsonSlurper

def ai = new OpenAIClient()
def request = new ApiCall(model: 'gpt-5-nano',
        input: 'Write a haiku about Groovy programming.')
def response = ai.call(request)

println "Status: ${response.statusCode()}"

response.statusCode() == 200 ?
        new JsonSlurper().parseText(response.body()).with { result ->
            // Get the reasoning summary
            result.output?.find { it.type == 'reasoning' }?.summary?.with { summary ->
                println "Reasoning: ${summary.collect { it.text }.join('\n\n')}"
            }

            // Get and print the result
            println "\nResult:"
            println result.output?.find { it.type == 'message' }?.content?[0]?.text ?: 'No story found'
        } :
        println("Error: ${response.body()}")