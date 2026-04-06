# Script para adicionar O Poderoso Chefao ao PocketBase
$POCKETBASE_URL = "http://127.0.0.1:8090"

Write-Host "========================================"
Write-Host "  Adicionando O Poderoso Chefao        "
Write-Host "========================================" 
Write-Host ""

$godfatherData = @{
    titulo = "O Poderoso Chefao"
    tipo = "filme"
    genero = "Drama, Crime"
    classificacao_etaria = 16
    duracao_minutos = 175
    diretor = "Francis Ford Coppola"
    temporadas = 0
    episodios_por_temporada = 0
    duracao_media_episodio = 0
    trailer_url = "https://www.youtube.com/watch?v=sY1S34973zA"
    video_url = "https://www.youtube.com/watch?v=sY1S34973zA"
    image_url = "https://m.media-amazon.com/images/M/MV5BM2MyNjYxNmUtYTAwNi00MTYxLWJmNWYtYzZlODY3ZTk3OTFlXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_FMjpg_UX1000_.jpg"
    destaque = $true
    media_avaliacoes = 0
}

$jsonData = $godfatherData | ConvertTo-Json

try {
    Write-Host "Criando registro de O Poderoso Chefao..."
    
    $response = Invoke-RestMethod `
        -Uri "$POCKETBASE_URL/api/collections/conteudos/records" `
        -Method Post `
        -ContentType "application/json" `
        -Body $jsonData
    
    Write-Host "OK O Poderoso Chefao adicionado com sucesso!" -ForegroundColor Green
    Write-Host "ID: $($response.id)"
    Write-Host "Titulo: $($response.titulo)"
    Write-Host "Trailer: $($response.trailer_url)"
    Write-Host ""
    Write-Host "Concluido!"
}
catch {
    Write-Host "ERRO ao adicionar: $_" -ForegroundColor Red
    Write-Host "Certifique-se de que o PocketBase esta rodando em $POCKETBASE_URL"
}

Write-Host ""

# Made with Bob
