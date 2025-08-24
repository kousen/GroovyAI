package com.kousenit

import java.net.http.*
import java.time.Duration

class OpenAIClient {
    private client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(60))
            .build()
    private apiKey = System.getenv('OPENAI_API_KEY')
    private baseUrl = 'https://api.openai.com'

    OpenAIClient() {}
    
    OpenAIClient(String baseUrl) {
        this.baseUrl = baseUrl
    }

    def call(ApiCall request) {
        client.send(
                HttpRequest.newBuilder()
                        .uri("${baseUrl}/v1/responses".toURI())
                        .timeout(request.timeout)
                        .headers('Content-Type', 'application/json',
                                'Authorization', "Bearer $apiKey")
                        .POST(HttpRequest.BodyPublishers.ofString(request.toJson()))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        )
    }
}
