using KontactPlus.AI.Api.Models;
using System.Net.Http.Headers;
using System.Text;
using System.Text.Json;

namespace KontactPlus.AI.Api.Services;

public class OpenAiCompatibleAiProvider : IAiProvider
{
    private readonly HttpClient _httpClient;
    private readonly IConfiguration _config;
    private readonly ILogger<OpenAiCompatibleAiProvider> _logger;

    public OpenAiCompatibleAiProvider(HttpClient httpClient, IConfiguration config, ILogger<OpenAiCompatibleAiProvider> logger)
    {
        _httpClient = httpClient;
        _config = config;
        _logger = logger;
    }

    public async Task<AiResponseDto> GenerateAsync(AiRequestDto request, CancellationToken ct)
    {
        var apiKey = _config["AI_PROVIDER_API_KEY"];
        var model = _config["AI_PROVIDER_MODEL"] ?? "gpt-3.5-turbo";
        var baseUrl = _config["AI_PROVIDER_BASE_URL"];

        if (string.IsNullOrEmpty(baseUrl)) throw new InvalidOperationException("AI_PROVIDER_BASE_URL not configured.");
        if (string.IsNullOrEmpty(apiKey)) throw new InvalidOperationException("AI_PROVIDER_API_KEY not configured.");

        var systemPrompt = ConstructSystemPrompt(request);

        var payload = new
        {
            model = model,
            messages = new[]
            {
                new { role = "system", content = systemPrompt },
                new { role = "user", content = request.Instruction }
            },
            max_tokens = 1000
        };

        var json = JsonSerializer.Serialize(payload);
        var content = new StringContent(json, Encoding.UTF8, "application/json");

        _httpClient.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", apiKey);

        var response = await _httpClient.PostAsync($"{baseUrl.TrimEnd('/')}/chat/completions", content, ct);
        response.EnsureSuccessStatusCode();

        var responseJson = await response.Content.ReadAsStringAsync(ct);
        using var document = JsonDocument.Parse(responseJson);
        var text = document.RootElement.GetProperty("choices")[0].GetProperty("message").GetProperty("content").GetString() ?? "";

        return new AiResponseDto(
            RequestId: Guid.NewGuid().ToString(),
            Text: text,
            ModelLabel: model,
            FinishReason: "completed"
        );
    }

    private string ConstructSystemPrompt(AiRequestDto request)
    {
        var sb = new StringBuilder();
        sb.AppendLine("You are a helpful and privacy-conscious relationship assistant called Kontact++.");
        sb.AppendLine($"Action: {request.Action}");
        sb.AppendLine($"Tone: {request.Tone}");
        sb.AppendLine($"Locale: {request.Locale}");
        sb.AppendLine($"Contact Alias: {request.ContactAlias}");

        if (!string.IsNullOrEmpty(request.Context))
        {
            sb.AppendLine($"Relationship Context: {request.Context}");
        }

        if (!string.IsNullOrEmpty(request.SelectedText))
        {
            sb.AppendLine($"Reference Text: {request.SelectedText}");
        }

        sb.AppendLine("Draft a short communication based on the following instructions. Never invent personal facts. Be concise.");
        return sb.ToString();
    }
}
