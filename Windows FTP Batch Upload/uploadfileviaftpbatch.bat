@ECHO OFF
SETLOCAL 
REM ========================= Start of Batch Program Description ==============================
REM Program : FTP Upload Batch Program
REM Author  : Washington Alto
REM Date    : May 2007
REM ========================= End of Batch Program Description   ==============================

REM ========================= Start of Global Variable Definition =============================
SET localdir=E:\My Documents\common\infosuite\Infocheck Demo\Generic\Input
SET FTPServer=altoxp
SET FTPuser=ftpuser
SET FTPpasswd=ftpuser
REM ========================= End of Global Variable Definition =============================

REM ======== Start of timestamp environment variable generation which will be used in subsequent operations ======
REM --- Get system date and remove all '/' ---
SET datestr=%DATE:/=%
REM --- Get the last 10 characters of the result
SET datestr=%datestr:~-8%
REM --- Get the mmdd portion of datestr
SET datestr1=%datestr:~0,4%
REM --- Get the last two of yyyy portion of datestr
SET datestr2=%datestr:~-2%
REM --- Concatenate datestr1 with datestr2 to get mmddyy
SET datestr=%datestr1%%datestr2%
REM --- Get system time and remove all ':' ---
SET timestr=%TIME::=%
REM --- Get the first 6 characters of the result ---
SET timestr=%timestr:~0,6%
REM --- Get the hour string from timestr string ---
SET hourstr=%timestr:~0,2%
REM --- Get the numeric value of hour string to remove the leading space ---
SET /A hournum=hourstr
REM --- Get the minute and second string from timestr string ---
SET minsecstr=%timestr:~2,4%
REM -- Concatenate the hournum and minsecstr to get the reformatted timestr string ---
SET timestr=%hournum%%minsecstr%
REM -- Concatenate the datestr string and timestr string to form timestamp ---
SET timestamp=%datestr%%timestr%
REM The timestamp that will be used is %timestamp%
REM ======== End of timestamp environment variable generation which will be used in subsequent operations ======

REM ========================= Start of Batch Routine =============================
SET logdir=%CD%\log
SET ftpresponsedir=%logdir%
SET ftpresponsefile=ftpresponse.txt
SET logfile=ftplog_%timestamp%.txt

ECHO FTP Session Started...
IF NOT EXIST "%logdir%" ( MKDIR "%logdir%" )
IF EXIST "%ftpresponsedir%\%ftpresponsefile%" ( DEL "%ftpresponsedir%\%ftpresponsefile%" )
ECHO %FTPuser%>      "%ftpresponsedir%\%ftpresponsefile%"
ECHO %FTPpasswd%>>   "%ftpresponsedir%\%ftpresponsefile%"
ECHO ascii        >> "%ftpresponsedir%\%ftpresponsefile%"
ECHO mput *.txt   >> "%ftpresponsedir%\%ftpresponsefile%"
ECHO binary       >> "%ftpresponsedir%\%ftpresponsefile%"
ECHO mput *.tif   >> "%ftpresponsedir%\%ftpresponsefile%"
ECHO quit         >> "%ftpresponsedir%\%ftpresponsefile%"

CD "%localdir%"
%localdir:~0,2%

ECHO.             >   "%logdir%\%logfile%"
ECHO Time Begin:  >>  "%logdir%\%logfile%"
TIME < nul        >>  "%logdir%\%logfile%"
ECHO.             >>  "%logdir%\%logfile%"
ECHO ---- Start of FTP Session --------  >>  "%logdir%\%logfile%"
ECHO.             >>  "%logdir%\%logfile%"

ftp -i -s:"%ftpresponsedir%\%ftpresponsefile%" %FTPServer% >> "%logdir%\%logfile%" 

ECHO.             >>  "%logdir%\%logfile%"
ECHO ---- End of FTP Session --------  >>  "%logdir%\%logfile%"
ECHO.             >>  "%logdir%\%logfile%"
ECHO Time End:    >> "%logdir%\%logfile%"
TIME < nul        >> "%logdir%\%logfile%"
ECHO.             >>  "%logdir%\%logfile%"


IF EXIST "%ftpresponsedir%\%ftpresponsefile%" ( DEL "%ftpresponsedir%\%ftpresponsefile%" )
ECHO FTP Session Complete!
PAUSE
REM ========================= Start of Batch Routine =============================
ENDLOCAL