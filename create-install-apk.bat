@echo off
echo -- removing old apk
del ".\apks\app-debug.apks"
echo -- creating new apk from bundle
START /b /wait java -jar bundletool-all-1.14.1.jar build-apks --local-testing --bundle=.\app\build\outputs\bundle\debug\app-debug.aab --output=.\apks\app-debug.apks
echo -- installing apk on connected device
START /b /wait java -jar bundletool-all-1.14.1.jar install-apks --apks=.\apks\app-debug.apks 
