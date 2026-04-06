# Script para iniciar todos os serviços do JavaFlix
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   INICIANDO JAVAFLIX" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Verificar se PocketBase está rodando
Write-Host "1. Verificando PocketBase..." -ForegroundColor Yellow
try {
    $health = Invoke-RestMethod -Uri "http://localhost:8090/api/health" -Method Get -ErrorAction Stop
    Write-Host "   OK - PocketBase ja esta rodando!" -ForegroundColor Green
} catch {
    Write-Host "   Iniciando PocketBase..." -ForegroundColor Yellow
    Start-Process -FilePath ".\pocketbase.exe" -ArgumentList "serve" -WindowStyle Minimized
    Start-Sleep -Seconds 3
    Write-Host "   OK - PocketBase iniciado!" -ForegroundColor Green
}
Write-Host ""

# Iniciar Backend (Quarkus)
Write-Host "2. Iniciando Backend (Quarkus)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PWD'; .\mvnw.cmd quarkus:dev"
Write-Host "   Backend iniciando em segundo plano..." -ForegroundColor Green
Write-Host ""

# Aguardar backend iniciar
Write-Host "3. Aguardando backend iniciar..." -ForegroundColor Yellow
Start-Sleep -Seconds 10
Write-Host ""

# Iniciar Frontend (Vite)
Write-Host "4. Iniciando Frontend (Vite)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PWD\frontend'; npm run dev"
Write-Host "   Frontend iniciando em segundo plano..." -ForegroundColor Green
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   Servicos Iniciados!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "URLs dos servicos:" -ForegroundColor Cyan
Write-Host "  - Frontend:   http://localhost:5173" -ForegroundColor White
Write-Host "  - Backend:    http://localhost:8081" -ForegroundColor White
Write-Host "  - PocketBase: http://localhost:8090" -ForegroundColor White
Write-Host ""
Write-Host "Pressione qualquer tecla para fechar..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

# Made with Bob
