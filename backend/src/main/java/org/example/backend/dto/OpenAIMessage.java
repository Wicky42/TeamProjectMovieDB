package org.example.backend.dto;

/*
{
        "role": "developer",
        "content": "You are a helpful assistant."
      },
 */
public record OpenAIMessage(String role,
                            String content) {
}
