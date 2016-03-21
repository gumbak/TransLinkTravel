# TransLink Travel

###Overview
The TransLink Travel is an android phone application that allows users to search for nearby TransLink bus stop information based on the phone's GPS and/or Network Location Provider coordinates. 
Using the TransLink Travel api, REST calls are created to display bus stop distances, schedules, and countdown until the next bus.

Please note that the application works best if used within the TransLink zones. For more information about TransLink, visit http://www.translink.ca/

###Requirements
- Register and provide the application with an TransLink API key: https://developer.translink.ca/Account/Register
- Location access is required to gather GPS and/or Network Location Provider.

###Testing on Emulator
1. Go to the 'Bus Stop' page on the emulator
2. On a command line, connect to the emulator via telnet
	ex. telnet localhost 5554
3. On the command line, provide the emulator with a gps location
	ex. geo fix -122.850060 49.2877