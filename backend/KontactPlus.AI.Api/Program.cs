using DotNetEnv;
using KontactPlus.AI.Api.Models;
using KontactPlus.AI.Api.Services;
using Microsoft.AspNetCore.Mvc;

Env.Load();

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();
builder.Services.AddHttpClient<IAiProvider, OpenAiCompatibleAiProvider>();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.MapGet("/health", () => Results.Ok(new { status = "Healthy" }));

app.MapPost("/api/v1/ai/generate", async (
    [FromBody] AiRequestDto request,
    IAiProvider aiProvider,
    CancellationToken ct) =>
{
    try
    {
        // Simple validation
        if (string.IsNullOrWhiteSpace(request.Instruction)) return Results.BadRequest("Instruction is required.");

        var result = await aiProvider.GenerateAsync(request, ct);
        return Results.Ok(result);
    }
    catch (Exception ex)
    {
        // Don't leak details in production
        return Results.Problem("AI generation failed. Please try again later.");
    }
})
.WithName("GenerateAiText")
.WithOpenApi();

app.Run();
