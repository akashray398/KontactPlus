namespace KontactPlus.AI.Api.Models;

public record AiResponseDto(
    string RequestId,
    string Text,
    string? ModelLabel = null,
    string? FinishReason = null
);
