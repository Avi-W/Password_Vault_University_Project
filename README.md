# Password_Vault_School_project
Chrome Extension School Project using Intel Trusted Execution Environments. Software and hardware from Intel is required to run however most of the code to do so is provided in the repository. Used languages: Javascript, HTML/CSS, Java, C#

This project was done in a team of 2: Avi Rosenbaum and Chaim Fishman.

## Files included:
### Chrome Extension Folder:
assets/pIcon.png - not included due to copywrite. Was a simple png image of a P for the icon of the extension.
manigest.json - json file for the extension.
popup.html - html file that describes the front-end of the extension
popup.js - Javascript file for the functionality of the extension end of the password vault.

### Local Intermediary Server:
Local Intermediary server that receives from the chrome extension using websockets and sends to the C# server using TCP sockets.
Required imported modules/packages: 'node:http', 'node:net', 'websocket'

### Password Vault Host:
C# server for communicating between the Intermediary Server and the Trusted Execution environment.
Has required imports not available to the public for communication with the Trusted Execution environmt, therefore not able to function properly though the source code is available.

### Password Vault (src folder):
Java source code that handles the Intel Trusted Execution environemnt. Also not able to be run without packages from intel, however code is available here. 

### Final Report:
PDF file that details architecture of the system as well as the process of creating the system.
