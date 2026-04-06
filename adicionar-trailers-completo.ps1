# Script para adicionar trailers a todos os conteudos no PocketBase
$POCKETBASE_URL = "http://127.0.0.1:8090"

Write-Host "========================================"
Write-Host "  Adicionando Trailers aos Conteudos   "
Write-Host "========================================"
Write-Host ""

# Mapeamento de trailers PT-BR
$trailers = @{
    "O Poderoso Chefao" = "https://www.youtube.com/watch?v=sY1S34973zA"
    "Shrek" = "https://www.youtube.com/watch?v=CwXOrWvPBPk"
    "Matrix" = "https://www.youtube.com/watch?v=m8e-FF8MsqU"
    "Inception" = "https://www.youtube.com/watch?v=8hP9D6kZseM"
    "Breaking Bad" = "https://www.youtube.com/watch?v=XZ8daibM3AE"
    "Stranger Things" = "https://www.youtube.com/watch?v=b9EkMc79ZSU"
    "La Casa de Papel" = "https://www.youtube.com/watch?v=_InqQJRqGW4"
    "Dark" = "https://www.youtube.com/watch?v=rrwycJ08PSA"
}

# Mapeamento de imagens
$imagens = @{
    "O Poderoso Chefao" = "https://m.media-amazon.com/images/M/MV5BM2MyNjYxNmUtYTAwNi00MTYxLWJmNWYtYzZlODY3ZTk3OTFlXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_FMjpg_UX1000_.jpg"
    "Shrek" = "https://m.media-amazon.com/images/M/MV5BOGZhM2FhNTItODAzNi00YjA0LWEyN2UtNjJlYWQzYzU1MDg5L2ltYWdlL2ltYWdlXkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_FMjpg_UX1000_.jpg"
    "Matrix" = "https://m.media-amazon.com/images/M/MV5BNzQzOTk3OTAtNDQ0Zi00ZTVkLWI0MTEtMDllZjNkYzNjNTc4L2ltYWdlXkEyXkFqcGdeQXVyNjU0OTQ0OTY@._V1_FMjpg_UX1000_.jpg"
    "Inception" = "https://m.media-amazon.com/images/M/MV5BMjAxMzY3NjcxNF5BMl5BanBnXkFtZTcwNTI5OTM0Mw@@._V1_FMjpg_UX1000_.jpg"
    "Breaking Bad" = "https://m.media-amazon.com/images/M/MV5BYmQ4YWMxYjUtNjZmYi00MDQ1LWFjMjMtNjA5ZDdiYjdiODU5XkEyXkFqcGdeQXVyMTMzNDExODE5._V1_FMjpg_UX1000_.jpg"
    "Stranger Things" = "https://m.media-amazon.com/images/M/MV5BN2ZmYjg1YmItNWQ4OC00YWM0LWE0ZDktYThjOTZiZjhhN2Q2XkEyXkFqcGdeQXVyNjgxNTQ3Mjk@._V1_FMjpg_UX1000_.jpg"
    "La Casa de Papel" = "https://m.media-amazon.com/images/M/MV5BODI0ZTljYTMtODQ1NC00NmI0LTk1YWUtN2FlNDM1MDExMDlhXkEyXkFqcGdeQXVyMTM0NTUzNDIy._V1_FMjpg_UX1000_.jpg"
    "Dark" = "https://m.media-amazon.com/images/M/MV5BOTk2NzY4MjItZGFkZS00OWUyLWJiM2UtY2M2NjY0NzMxZjQxXkEyXkFqcGdeQXVyMjg1NDcxNDE@._V1_FMjpg_UX1000_.jpg"
}

try {
    Write-Host "Buscando conteudos..."
    $response = Invoke-RestMethod -Uri "$POCKETBASE_URL/api/collections/conteudos/records" -Method Get
    
    $totalConteudos = $response.items.Count
    Write-Host "Encontrados $totalConteudos conteudos"
    Write-Host ""
    
    $atualizados = 0
    $erros = 0
    
    foreach ($conteudo in $response.items) {
        $titulo = $conteudo.titulo
        $id = $conteudo.id
        
        Write-Host "Processando: $titulo"
        
        if ($trailers.ContainsKey($titulo)) {
            $trailerUrl = $trailers[$titulo]
            $imagemUrl = $imagens[$titulo]
            
            $updateData = @{
                trailer_url = $trailerUrl
                image_url = $imagemUrl
                destaque = $true
            }
            
            $jsonData = $updateData | ConvertTo-Json
            
            try {
                $updateResponse = Invoke-RestMethod `
                    -Uri "$POCKETBASE_URL/api/collections/conteudos/records/$id" `
                    -Method Patch `
                    -ContentType "application/json" `
                    -Body $jsonData
                
                Write-Host "  OK Trailer adicionado: $trailerUrl"
                Write-Host "  OK Imagem adicionada"
                $atualizados++
            }
            catch {
                Write-Host "  ERRO ao atualizar: $_"
                $erros++
            }
        }
        else {
            Write-Host "  AVISO Trailer nao encontrado no mapeamento"
        }
        
        Write-Host ""
    }
    
    Write-Host "========================================"
    Write-Host "  Resumo da Atualizacao                "
    Write-Host "========================================"
    Write-Host "Total de conteudos: $totalConteudos"
    Write-Host "Atualizados: $atualizados"
    Write-Host "Erros: $erros"
    Write-Host ""
    
    if ($atualizados -gt 0) {
        Write-Host "Trailers adicionados com sucesso!"
    }
}
catch {
    Write-Host "Erro ao conectar ao PocketBase: $_"
    Write-Host "Certifique-se de que o PocketBase esta rodando em $POCKETBASE_URL"
}

Write-Host ""
Write-Host "Concluido!"

# Made with Bob
