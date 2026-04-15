package org.example.backend.client;

import org.example.backend.dto.OpenAIChoice;
import org.example.backend.dto.OpenAIMessage;
import org.example.backend.dto.OpenAIRequest;
import org.example.backend.dto.OpenAIResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OpenAiClient Tests")
class OpenAiClientTest {

    // --- Mocks für die RestClient Fluent-API-Kette ---
    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private OpenAiClient openAiClient;

    @BeforeEach
    void setUp() {
        // Konstruktor braucht einen API-Key; der echte RestClient
        // wird danach via ReflectionTestUtils durch den Mock ersetzt.
        openAiClient = new OpenAiClient("test-api-key-dummy");
        ReflectionTestUtils.setField(openAiClient, "restClient", restClient);
    }

    // -------------------------------------------------------
    // Hilfsmethoden
    // -------------------------------------------------------

    /** Baut eine minimal gültige OpenAIResponse für Tests. */
    private OpenAIResponse buildResponse(String imdbId) {
        OpenAIMessage message = new OpenAIMessage("assistant", imdbId);
        OpenAIChoice choice = new OpenAIChoice(0, message);
        return new OpenAIResponse("gpt-4o-mini", java.util.List.of(choice));
    }

    /** Stubbt die gesamte RestClient-Kette für einen erfolgreichen Aufruf. */
    private void stubRestClientChain(OpenAIResponse response) {
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(OpenAIRequest.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(OpenAIResponse.class)).thenReturn(response);
    }

    // -------------------------------------------------------
    // Tests – Happy Path
    // -------------------------------------------------------

    @Test
    @DisplayName("Gibt OpenAIResponse zurück, wenn API erfolgreich antwortet")
    void findMovieImdbID_shouldReturnResponse_whenApiRespondsSuccessfully() {
        // Given
        OpenAIResponse expected = buildResponse("tt0110912");
        stubRestClientChain(expected);

        // When
        OpenAIResponse actual =
                openAiClient.findMovieImdbID_whenCalledWithPrompt("Pulp Fiction");

        // Then
        assertThat(actual).isNotNull();
        assertThat(actual.choices()).hasSize(1);
        assertThat(actual.choices().getFirst().message().content()).isEqualTo("tt0110912");
        assertThat(actual.text()).isEqualTo("tt0110912");
    }

    // -------------------------------------------------------
    // Tests – Prompt-Aufbau
    // -------------------------------------------------------

    @Test
    @DisplayName("Prompt ändert sich korrekt je nach Suchbegriff")
    void findMovieImdbID_shouldBuildDistinctPromptsForDifferentInputs() {
        // Given
        stubRestClientChain(buildResponse("tt0111161"));
        ArgumentCaptor<OpenAIRequest> captor =
                ArgumentCaptor.forClass(OpenAIRequest.class);

        // When
        openAiClient.findMovieImdbID_whenCalledWithPrompt("Shawshank Redemption");

        // Then
        verify(requestBodyUriSpec).body(captor.capture());
        String prompt = captor.getValue().messages().getFirst().content();
        assertThat(prompt).contains("Shawshank Redemption");
        assertThat(prompt).doesNotContain("Matrix");
    }

    // -------------------------------------------------------
    // Tests – RestClient-Kette wird vollständig durchlaufen
    // -------------------------------------------------------

    @Test
    @DisplayName("RestClient-Kette wird in der richtigen Reihenfolge aufgerufen")
    void findMovieImdbID_shouldCallRestClientChainInOrder() {
        // Given
        stubRestClientChain(buildResponse("tt0068646"));

        // When
        openAiClient.findMovieImdbID_whenCalledWithPrompt("Der Pate");

        // Then – jede Stufe der Fluent-API muss genau einmal aufgerufen werden
        var inOrder = inOrder(restClient, requestBodyUriSpec, requestBodySpec, responseSpec);
        inOrder.verify(restClient).post();
        inOrder.verify(requestBodyUriSpec).body(any(OpenAIRequest.class));
        inOrder.verify(requestBodySpec).retrieve();
        inOrder.verify(responseSpec).body(OpenAIResponse.class);
    }

    @Test
    @DisplayName("retrieve() wird genau einmal pro Anfrage aufgerufen")
    void findMovieImdbID_shouldCallRetrieveExactlyOnce() {
        // Given
        stubRestClientChain(buildResponse("tt0468569"));

        // When
        openAiClient.findMovieImdbID_whenCalledWithPrompt("The Dark Knight");

        // Then
        verify(requestBodySpec, times(1)).retrieve();
    }

    // -------------------------------------------------------
    // Tests – Fehlerbehandlung
    // -------------------------------------------------------

    @Test
    @DisplayName("Wirft RestClientException weiter, wenn API nicht erreichbar")
    void findMovieImdbID_shouldPropagateRestClientException() {
        // Given
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(OpenAIRequest.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(OpenAIResponse.class))
                .thenThrow(new RestClientException("Connection refused"));

        // When / Then
        assertThatThrownBy(() ->
                openAiClient.findMovieImdbID_whenCalledWithPrompt("Inception"))
                .isInstanceOf(RestClientException.class)
                .hasMessageContaining("Connection refused");
    }

    @Test
    @DisplayName("Gibt null zurück, wenn API null antwortet")
    void findMovieImdbID_shouldReturnNull_whenApiReturnsNull() {
        // Given
        stubRestClientChain(null);

        // When
        OpenAIResponse actual =
                openAiClient.findMovieImdbID_whenCalledWithPrompt("Unbekannter Film");

        // Then – kein NullPointerException, null wird durchgereicht
        assertThat(actual).isNull();
    }

    @Test
    @DisplayName("Leerer Prompt führt nicht zu Fehler im Client selbst")
    void findMovieImdbID_shouldNotThrow_whenPromptIsEmpty() {
        // Given
        stubRestClientChain(buildResponse("tt0000001"));

        // When / Then – kein Fehler, der Prompt-String wird einfach leer eingebaut
        assertThatNoException().isThrownBy(() ->
                openAiClient.findMovieImdbID_whenCalledWithPrompt(""));
    }
}
