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

    public OpenAIResponse findMovieImdbID_whenCalledWithPrompt(String prompt) {
        String systemPrompt = """
        Du bist ein Filmempfehlungssystem.
        
        Aufgabe:
        Analysiere die Nutzeranfrage und bestimme anhand von Stimmung, Ton, Themen, Genre-Wünschen oder ähnlichen Hinweisen den passendsten Film.

        Regeln:
        - Wähle genau EINEN Film aus.
        - Interpretiere die Stimmung des Nutzers und leite daraus das passende Genre oder die passende Filmart ab.
        - Wenn mehrere Filme passen, wähle den bekanntesten Film.
        - Gib ausschließlich eine gültige IMDb-ID zurück.
        - Ausgabeformat: tt1234567
        - Keine Erklärung.
        - Kein zusätzlicher Text.
        - Keine Anführungszeichen.
        - Keine Satzzeichen.
        - Nur die IMDb-ID.

        Nutzeranfrage:
        """ + prompt;

        return restClient.post()
                .body(new OpenAIRequest(systemPrompt))
                .retrieve()
                .body(OpenAIResponse.class);
    }
}
