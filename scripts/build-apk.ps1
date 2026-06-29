# Build Orca debug APK
# Usage: .\scripts\build-apk.ps1

$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot

function Resolve-JavaHome {
    if ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME\bin\javac.exe")) {
        return $env:JAVA_HOME
    }
    $candidates = @(
        "$env:USERPROFILE\.jdks\openjdk-22.0.1",
        "$env:USERPROFILE\.jdks\openjdk-21.0.2",
        "$env:USERPROFILE\.jdks\openjdk-20.0.2",
        "C:\Program Files\Java\jdk-20"
    )
    foreach ($dir in $candidates) {
        if (Test-Path "$dir\bin\javac.exe") { return $dir }
    }
    throw "No JDK found. Install JDK 17+ or set JAVA_HOME."
}

Write-Host "Building Orca APK..." -ForegroundColor Cyan
$env:JAVA_HOME = Resolve-JavaHome
Write-Host "JAVA_HOME: $env:JAVA_HOME"

$sdkRoot = "D:\Infusion-Studio\.android-sdk"
if (Test-Path "$sdkRoot\platform-tools") {
    $env:ANDROID_HOME = $sdkRoot
    $env:ANDROID_SDK_ROOT = $sdkRoot
    Write-Host "ANDROID_HOME: $sdkRoot"
}

Push-Location $Root
.\gradlew.bat assembleDebug

$apkSrc = Join-Path $Root "app\build\outputs\apk\debug\app-debug.apk"
$releases = Join-Path $Root "releases"
New-Item -ItemType Directory -Force -Path $releases | Out-Null
$apkDest = Join-Path $releases "Orca-debug.apk"
Copy-Item $apkSrc $apkDest -Force

Write-Host ""
Write-Host "APK ready!" -ForegroundColor Green
Write-Host "  File: $apkDest"
Pop-Location
