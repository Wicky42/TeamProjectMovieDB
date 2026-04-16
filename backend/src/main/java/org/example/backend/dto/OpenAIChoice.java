package org.example.backend.dto;

public record OpenAIChoice(
        int index,
        OpenAIMessage message
) {
}
