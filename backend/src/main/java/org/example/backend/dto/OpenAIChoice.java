package org.example.backend.dto;

/*
{
      "index": 0,
      "message": {
        "role": "assistant",
        "content": "Hello! How can I assist you today?",
      }
    }
 */
public record OpenAIChoice(
        int index,
        OpenAIMessage message
) {
}
