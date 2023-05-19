# Seek and Log
This Android app detects installed apps on the smartphone and allows you to monitor the date and time of opening.
The app has 2 modules, **app** and **seekappfeature** 
<br>

- **seekappfeature** module contains only the apps detection logic and is an on-demand module, it means that it's loaded and installed when required. In this case it happens when a button is clicked.
- **app** module allow user to select the apps he want to monitor and creates logs composed by the app name, date and time of opening. These logs are stored in a file and printed on a dedicated UI.
<br>

UI is very simple and consists of 3 screens. The first require the module installation. 
Once this is done, the user can select the apps he wants to monitor in the second screen (the search function can help if the list is very long).
The third prints the logs relating to the apps that user has chosen to monitor (you can also pause or restart monitoring). 
<br><br>

<img src=https://github.com/LucaPalenga/Seek-and-Log/assets/49731077/66b021d1-e1fb-4434-bba8-9bf878839f88 width = 300/>
<br><br>

### Local test
To test locally you can run *create-install-apk.bat* (for windows users) that will automatically install apk on your connected device using bundletool otherwise you can manually install it on your device by the apk file you will find in *apks* folder. 
<br><br>

> Remember to enable **usage access** for this app or it will not be able to detect other apps 
> <br>
> *Settings > Apps > Special app access > Usage access*
> <br><br>
> 
> <img src=https://github.com/LucaPalenga/Seek-and-Log/assets/49731077/8e8c31ab-9cb4-47d0-ba5a-c8817ca79423 width = 200/> &emsp; <img src=https://github.com/LucaPalenga/Seek-and-Log/assets/49731077/f4ddae7c-e8c4-4486-a45c-9e43fdb150cc width = 200/>
   
