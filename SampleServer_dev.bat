@echo off
:Retry
javac SampleServer.java || (pause & goto :Retry)
call SampleServer.bat
