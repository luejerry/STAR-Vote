Legacy Documentation
====================

Disabling IPv6
--------------
Auditorium currently does not support IPv6. Machines using IPv6 will not be
able to connect to other machines. To disable IPv6, run the application with
the JVM argument:
`-Djava.net.preferIPv4Stack=true`

Command-line usage
------------------

Using VoteBox: (make sure IPv6 is disabled)
`java -jar VoteBox.jar [serial number]

Using Supervisor:
`java -jar Supervisor.jar [serial number]

Using Tap:
`java -jar Tap.jar [serial number] [report address] [port]

`[port]` should be the same between Tap and ChallengeWebServer
`[serial number]`s should all be distinct (and must have corresponding keys)

Configuration Options
---------------------
Must be placed in vb.conf, supervisor.conf, tap.conf, and bs.conf.

~~~
DISCOVER_TIMEOUT 
Integer, milliseconds 
Default: 4000 

DISCOVER_PORT 
Integer, port number 
9782 

DISCOVER_REPLY_TIMEOUT
Integer, milliseconds 
Default: 1000 

DISCOVER_REPLY_PORT
Integer, port number 
Default: 9783 

LISTEN_PORT 
Integer, port number 
Default: 9700 

JOIN_TIMEOUT 
Integer, millisecond 
Default: 1000 

BROADCAST_ADDRESS 
String, network address 
Default: 255.255.255.255 

LOG_LOCATION 
String, file path 
Default: log.out 

KEYS_DIRECTORY 
String, directory/classloader path 
Default: "/keys/" 

VIEW_IMPLEMENTATION 
String, one of SDL, AWT 
Default: AWT

RULE_FILE 
String, directoy path 
Default: rules 

CAST_BALLOT_ENCRYPTION_ENABLED 
Boolean 
Default: true

USE_COMMIT_CHALLENGE_MODEL 
Boolean 
Default: true

USE_ELO_TOUCH_SCREEN 
Boolean 
Default: false 

ELO_TOUCH_SCREEN_DEVICE 
String, path to device 
Default: null 

VIEW_RESTART_TIMEOUT 
Integer, milliseconds 
Default: 5000 

DEFAULT_SERIAL_NUMBER 
Integer 
Default: -1 (ignored) 

DEFAULT_REPORT_ADDRESS 
String, IP, Computer Name, Domain Name, etc. 
Default: "" (ignored, used exclusively by Tap) 

DEFAULT_CHALLENGE_PORT 
Integer, port number 
Default: -1 (ignored, used by Tap and ChallengeWebServer) 

DEFAULT_HTTP_PORT 
Integer, port number 
Default: 80 (used exclusively by ChallengeWebServer) 

DEFAULT_BALLOT_FILE 
String, path to file 
Default: "" (ignored, used by ChallengeWebServer)
~~~