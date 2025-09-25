<#
PowerShell script to create MySQL database `geniedb`, create user `genie`, and run Flyway migrations.
Run from project root with:
  powershell -ExecutionPolicy Bypass -File .\scripts\create_mysql_db.ps1
If mysql client is not installed, install MySQL client or use MySQL Workbench / IntelliJ Database tool instead.
#>

try {
    $mysqlCmd = Get-Command mysql.exe -ErrorAction SilentlyContinue
    if (-not $mysqlCmd) {
        Write-Error "mysql client (mysql.exe) not found in PATH. Install MySQL client or use MySQL Workbench / IntelliJ Database tool."
        exit 1
    }

    Write-Host "Found mysql client: $($mysqlCmd.Path)" -ForegroundColor Green

    $rootUser = Read-Host -Prompt "MySQL admin user (default: root)"
    if ([string]::IsNullOrWhiteSpace($rootUser)) { $rootUser = 'root' }
    $secure = Read-Host -Prompt "Password for $rootUser (input hidden)" -AsSecureString
    $bstr = [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure)
    $rootPass = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto($bstr)
    [System.Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bstr)

    $sql = @"
CREATE DATABASE IF NOT EXISTS geniedb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'genie'@'localhost' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON geniedb.* TO 'genie'@'localhost';
FLUSH PRIVILEGES;
"@

    Write-Host "Creating database and user..." -ForegroundColor Cyan
    # Execute SQL via mysql client
    $args = @("-u", $rootUser, "-p$rootPass", "-e", $sql)
    $proc = Start-Process -FilePath $mysqlCmd.Path -ArgumentList $args -NoNewWindow -Wait -PassThru
    if ($proc.ExitCode -ne 0) {
        Write-Error "mysql exited with code $($proc.ExitCode). Check credentials and MySQL server status."
        exit $proc.ExitCode
    }
    Write-Host "Database geniedb and user genie created/ensured." -ForegroundColor Green

    # Run Flyway migrations via Maven wrapper (project root assumed script is in scripts/)
    $projectRoot = Join-Path $PSScriptRoot '..'
    Set-Location $projectRoot

    $mvnw = Join-Path $projectRoot 'mvnw.cmd'
    if (-not (Test-Path $mvnw)) { Write-Warning "mvnw.cmd not found in project root. Skipping Flyway migrate. You can run: mvnw -Dspring.profiles.active=mysql flyway:migrate"; exit 0 }

    Write-Host "Running Flyway migrations using Maven wrapper (profile=mysql)..." -ForegroundColor Cyan
    $mvnArgs = @('-DskipTests','-Dspring.profiles.active=mysql','flyway:migrate')
    $mvnProc = Start-Process -FilePath $mvnw -ArgumentList $mvnArgs -NoNewWindow -Wait -PassThru
    if ($mvnProc.ExitCode -ne 0) {
        Write-Error "Maven (flyway:migrate) failed with exit code $($mvnProc.ExitCode). Check Maven output above."; exit $mvnProc.ExitCode
    }
    Write-Host "Flyway migrations completed successfully." -ForegroundColor Green
    Write-Host "You can now run the app with: .\mvnw.cmd -Dspring.profiles.active=mysql spring-boot:run" -ForegroundColor Yellow

} catch {
    Write-Error "Error: $($_.Exception.Message)"
    exit 1
}
