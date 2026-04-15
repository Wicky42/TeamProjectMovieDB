package org.example.backend.dto;

import java.util.Collections;
import java.util.List;

public record OpenAIRequest(String model,
                            List<OpenAIMessage> messages) {
    public OpenAIRequest(String message) {
        this("gpt-4o-mini", Collections.singletonList(new OpenAIMessage("user", message)));
    }
}
