package com.kousenit

import groovy.json.JsonBuilder

import java.time.Duration

record ApiCall(String model, String input,
               Duration timeout = Duration.ofMinutes(3),
               SummaryType summary = SummaryType.AUTO,
               ReasoningEffort effort = ReasoningEffort.MEDIUM) {
    def toJson() {
        new JsonBuilder(model: model,
                input: [[role: 'user', content: input]],
                reasoning: [summary: summary.value, effort: effort.value]).toString()
    }
}
