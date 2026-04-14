package org.example.backend.dto;

import java.util.Collections;
import java.util.List;

/*
    "model": "VAR_chat_model_id",
    "messages": [
      {
        "role": "developer",
        "content": "You are a helpful assistant."
      },
      {
        "role": "user",
        "content": "Hello!"
      }
    ]
  }'
 */
public record OpenAIRequest(String model,
                            List<OpenAIMessage> messages) {
    public OpenAIRequest(String message) {
        this("gpt-4o-mini", Collections.singletonList(new OpenAIMessage("user", message)));
    }
}
