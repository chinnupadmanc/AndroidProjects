Audio - Video Recorder and Player with Video File Sharing
-----------------------------------------------------------

Learnings:
-------------
>	Building apps with Multimedia. Add Audio and Video capabilities to the app with Android's APIs for playing and recording media.
>	Connecting to other devices and transferring data over Wi-Fi Peer-to-Peer (P2P).
>	Client-Server socket communication.
>	Tab Layout with Swipe Views.
>	Usage of SharedPreferences to save and retrieve persistent data.
>	Listview of video files with thumbnail, file name and file size.

Prerequisites:
--------------
Two android devices with Android 4.0 (API level 14) (Required for Wi-Fi P2P) or above with camera and Wi-Fi enabled.

Features:
---------

1.	Audio Recorder and Player.
>	User goes to Audio tab.
>	Click Record button.  After recording click Stop button. File is saved.
>	User is given with the audio file name which was just saved with Play back and Delete options.
>	User clicks Play button. Audio is played in the application specific Audio Player.
>	To view previously saved audio files, user selects Audio Playlist from the options menu on the Actions bar. 
	(Click action overflow icon on the right side of the action bar to view the options menu.)
>	For each audio file displayed in the playlist, user gets context menu for Play and Delete actions.

2.	Video Recorder and Player
>	User goes to Video tab.
>	Click Record button.  Goes to Video record mode. Record and come back.
>	User is given with the video file name which was just saved with Play back, Delete and Share options.
>	User clicks Play button. Video is played in the application specific Video Player.
>	To view previously saved video files, user selects Video Playlist from the options menu on the Actions bar.
	(Click action overflow icon on the right side of the action bar to view the options menu.)
>	For each video file displayed in the playlist, user gets context menu for Play, Delete and Share actions.

3.	Video File Sharing With Another Android Device
>	On the device from where the Video has to be shared, user selects Share option either form the context menu 
	of a video file or from options given for the recently saved video file on the Video tab.
>	User is taken to the Wi-Fi P2P screen.
>	On the second device, user selects Wi-Fi P2P option from the options menu on the Actions bar.
	(Click action overflow icon on the right side of the action bar to view the options menu.)
>	Both users can see the other device under PEER section.
>	On the first device, user selects the other device from PEER sections and clicks on Connect. Devices are now connected. User is given with Send option.
>	User click Send button and the file is sent.
>	On the second device, user receives file. Video is played in the Video Player screen.
>	One of the users selects Disconnect option. 
>	To send further files repeat the above steps.

How  Wi-Fi P2P is implemented:
------------------------------
>	Used Wi-Fi P2P Demo sample for basic architecture.
>	Enabled the same for Video file sharing.
>	As per the sample code, the Group Owner could not send the file.  
	In this application, it makes sense for the device which initiates the connection to be able to send the file to peer. 
	Hence redesigned the code to enable the same.

Future Improvements:
--------------------
>	Sending multiple files before disconnecting.
	Lead: Do not close sockets once the file transfer is complete. Server should wait for further data.  
	Faced design issues and could not complete due to time constraint.
>	Audio file sharing.
	Lead: Send file type before sending data. Based upon the file type, server can save and play back data.



