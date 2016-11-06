1. use shared password to establish a shared session key
2. use session key to secure communication


Compilation:
---------------------------------------
1. compile either "Alice" or "Bob" file 
   directory by the following:
		a. javac Alice\*.java
		b. javac Bob\*.java



Before Execution:
---------------------------------------
make sure that the Host file is running
first before execucting on the Client file.



Shared password:
---------------------------------------
shared passwords are stored in a folder
called "vault" under each directory.

pw.dat file contents contains the
password data which is hardcorded.

shared passwords will contain 6 numeric
characters.

both alice and bob will contain the same
secrety key which are symmetric keys.



Directory:
---------------------------------------
A1
|_______Alice
|		|___Host.java
|		|___vault
|			|___pw.dat
|
|_______Bob
|		|___Client.java
|		|___vault
|			|___pw.dat


Execute Environment
---------------------------------------
Ubuntu, using terminal
