# Teste de carga simples usando PowerShell
# Envia 100 requisições concorrentes e sequenciais para medir se o Concurrent HTTP ThreadPool funciona
Write-Host "Iniciando Teste de Carga no Endpoint /api/catalogo (100 chamadas simultaneas)"

$url = "http://localhost:8080/api/catalogo"
$jobs = @()

$sw = [Diagnostics.Stopwatch]::StartNew()

for ($i = 0; $i -lt 100; $i++) {
    $job = Start-Job -ScriptBlock {
        param($url)
        Invoke-RestMethod -Uri $url -Method Get | Out-Null
        return "OK"
    } -ArgumentList $url
    $jobs += $job
}

Write-Host "Aguardando as 100 requisições finalizarem..."
Wait-Job -Job $jobs | Out-Null
Receive-Job -Job $jobs | Out-Null
Remove-Job -Job $jobs

$sw.Stop()

Write-Host "Teste Finalizado em: " $sw.Elapsed.TotalSeconds " segundos."
Write-Host "Se o servidor não travou e o tempo foi baixo, o Pool Concorrente está funcionando corretamente."
