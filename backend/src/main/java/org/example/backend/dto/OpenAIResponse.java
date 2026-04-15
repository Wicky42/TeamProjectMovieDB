package org.example.backend.dto;

import java.util.List;

public record OpenAIResponse(String model,
                             List<OpenAIChoice> choices) {
    public String text(){
        return choices.getFirst().message().content();
    }
}
