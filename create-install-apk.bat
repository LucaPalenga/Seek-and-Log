@echo off
del ".\apks\app-debug.apks"
START /B /WAIT java -jar bundletool-all-1.14.1.jar build-apks --local-testing --bundle=.\app\build\outputs\bundle\debug\app-debug.aab --output=.\apks\app-debug.apks
START /B java -jar bundletool-all-1.14.1.jar install-apks --apks=.\apks\app-debug.apks 
