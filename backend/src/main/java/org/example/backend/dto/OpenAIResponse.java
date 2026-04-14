package org.example.backend.dto;

import java.util.List;

/*
{
  "model": "gpt-5.4",
  "choices": [
    {
      "index": 0,
      "message": {
        "role": "assistant",
        "content": "Hello! How can I assist you today?",
      }
    }
  ],
}
 */
public record OpenAIResponse(String model,
                             List<OpenAIChoice> choices) {
    public String text(){
        return choices.getFirst().message().content();
    }
}
