using KontactPlus.AI.Api.Models;

namespace KontactPlus.AI.Api.Services;

public interface IAiProvider
{
    Task<AiResponseDto> GenerateAsync(AiRequestDto request, CancellationToken ct);
}
