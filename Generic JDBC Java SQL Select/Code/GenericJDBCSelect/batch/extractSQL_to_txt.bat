@ECHO OFF
REM ========================= Start of Batch Program Description ==============================
REM Program : Extracts an SQL Select statement to delimited text file 
REM Author  : Washington Alto
REM Caller Batch: extractSQLall.bat
REM Called Batch: runJDBCSQLSelect.bat
REM Parameters: %1 - Name of input file (w/out the *.txt extension)
REM             %2 - YES if output filename has timestamp appended and NO if output filename does not have timestamp appended
REM ========================= End of Batch Program Description ==============================

REM ====================== Start of Individual File Extraction Routine =====================
REM ------------------ Start of Time Stamp Routine -------------------
REM --- Get system date and remove all '/' ---
SET datestr=%DATE:/=%
REM --- Get the last 10 characters of the result
SET datestr=%datestr:~-8%
REM --- Get the mm portion of datestr
SET datestr1=%datestr:~0,2%
REM --- Get the dd portion of datestr
SET datestr2=%datestr:~2,2%
REM --- Get the last four of yyyy portion of datestr
SET datestr3=%datestr:~-4%
REM --- Concatenate datestr1,datestr2,datestr3 to get yyyymmdd
SET datestr=%datestr3%%datestr1%%datestr2%
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
SET timestamp=%datestr%_%timestr%
ECHO The timestamp that will be used is %timestamp%
REM -------------------- End of Time Stamp Routine -------------------

REM ---------------------- Start of Set Local Variables -------------
SET SQLinputdir=..\input
SET logdir=..\log
SET logfile=LOG_%1_%database%-%username%-%timestamp%.txt
REM ---------------------- End of Set Local Variables -------------

ECHO =======================^> Start Extraction for %1.txt > "%logdir%\%logfile%"
ECHO Input SQL: >> "%logdir%\%logfile%"
TYPE "%SQLinputdir%\%1.txt" >> "%logdir%\%logfile%"
ECHO. >> "%logdir%\%logfile%"
ECHO. >> "%logdir%\%logfile%"
ECHO. >> "%logdir%\%logfile%"
ECHO Query Results Statistics: >> "%logdir%\%logfile%"
ECHO. >> "%logdir%\%logfile%"

ECHO.
ECHO Currently running %1 on database type %database% and user name %username%
IF %2 EQU YES ( CALL runJDBCSQLSelect.bat %1 %1_%database%_%username%_%timestamp% >> "%logdir%\%logfile%" ) else ( CALL runJDBCSQLSelect.bat %1 %1_%database%_%username% >> "%logdir%\%logfile%" )

ECHO Run %1 on %OracleSID% is complete!

ECHO =======================^> End Extraction for %1.txt   >> "%logdir%\%logfile%"

REM ====================== End of Individual File Extraction Routine =====================
