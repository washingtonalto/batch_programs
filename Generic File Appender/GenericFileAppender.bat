@ECHO ON
SETLOCAL 
REM ========================= Start of Batch Program Description ==============================
REM Program : Batch Program for appending to a single file all ascii or text file belonging of the same type based on directory file patterns
REM Author  : Washington Alto
REM Date    : May 2009
REM Variable
REM    dirpattern    - directory pattern used in filtering the files that needed to append
REM    outputfile    - output file name to be used
REM ========================= End of Batch Program Description   ==============================

REM ========================= Start of Global Variable Definition =============================
SET dirpattern=*cpy*.csv
SET outputfile=cpy_dwh.csv
REM ========================= End of Global Variable Definition =============================

REM ========================= Start of Main Batch Program Routine ===========================

REM -------- Start of timestamp environment variable generation which will be used in subsequent operations ------
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
REM -------- End of timestamp environment variable generation which will be used in subsequent operations ------

REM ------------------------ Start of Local Variable Definition ----------------------------
SET homedir=%CD%
SET inputdir=%homedir%\input
SET outputdir=%homedir%\output
SET logdir=%homedir%\log
REM ------------------------ End of Local Variable Definition ----------------------------

REM --------------- Start of create Target Directory if they don't exist yet ----------------------------
IF NOT EXIST "%inputdir%" ( MKDIR "%inputdir%" )
IF NOT EXIST "%outputdir%" ( MKDIR "%outputdir%" )
IF NOT EXIST "%logdir%" ( MKDIR "%logdir%" )
REM --------------- End of create Target Directory if they don't exist yet ----------------------------

REM ------------------------ Start of delete routine for all files in the output directory --------------
DEL /Q "%outputdir%\*.*"
REM ------------------------ End of delete routine for all files in the output directory --------------

ECHO Dirpattern: %dirpattern%            > "%logdir%\genericfileappend_%timestamp%.txt"
ECHO Outputfile: %outputfile%            >> "%logdir%\genericfileappend_%timestamp%.txt"
ECHO Input files include the following:  >> "%logdir%\genericfileappend_%timestamp%.txt"
ECHO.                                    >> "%logdir%\genericfileappend_%timestamp%.txt"
dir  "%inputdir%\%dirpattern%"           >> "%logdir%\genericfileappend_%timestamp%.txt"

copy /A "%inputdir%\%dirpattern%" "%outputdir%\%outputfile%" 

ENDLOCAL
REM ========================= End of Main Batch Program Routine ===========================
