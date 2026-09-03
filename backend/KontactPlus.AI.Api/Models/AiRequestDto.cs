namespace KontactPlus.AI.Api.Models;

public record AiRequestDto(
    string Action,
    string Tone,
    string Instruction,
    string SelectedText = "",
    string ContactAlias = "Contact",
    string? Context = null,
    string Locale = "en-IN"
);
