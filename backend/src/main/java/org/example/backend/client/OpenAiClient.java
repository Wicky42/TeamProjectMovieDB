package org.example.backend.client;

import org.example.backend.dto.OpenAIRequest;
import org.example.backend.dto.OpenAIResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OpenAiClient {
    private static final String BASE_URL = "https://api.openai.com/v1/chat/completions";

    private final RestClient restClient;
    private final String apiKey;

    public OpenAiClient(@Value("${openai.api}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public OpenAIResponse findMovieImdbID_whenCalledWithPrompt(String prompt){
        return restClient.post()
                .body(new OpenAIRequest("Finde einen Film der zu dieser Suche passt: " + prompt + ". Gib nur eine gültige imdbID zurück (Regex: tt\\\\d+). Keine Erklärung."))
                .retrieve()
                .body(OpenAIResponse.class);
    }
}
