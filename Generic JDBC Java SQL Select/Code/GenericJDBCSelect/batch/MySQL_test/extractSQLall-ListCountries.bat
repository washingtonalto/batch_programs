@ECHO OFF
SETLOCAL 
REM =============== Start of Batch Program Description  ==============
REM Program : Extracts all SQL select statements specified in input files 
REM           pipe delimited text files 
REM Author  : Washington Alto
REM Date Start: May 2008
REM Called Batch: extractSQL_to_txt.bat
REM
REM Note: All parameters passed to extractSQL_to_txt.bat MUST NOT HAVE SPACES in between
REM For example,
REM     Wrong:   CALL extractSQL_to_txt.bat GFIS - ddm 
REM     Correct: CALL extractSQL_to_txt.bat GFIS-ddm 
REM
REM Environment Variables
REM     dbservername      - Database Server Name
REM     username          - Database User Name
REM     userpasswd        - Database User Password
REM     database          - Database can have the ff. values below:
REM                             SQL2000Microsoft  - SQL Server 2000 JDBC Driver from Microsoft
REM                             SQL2000Jtds       - Open Source SQL Server 2000 JDBC Driver
REM                             AS400HitSW        - DB2/400 Driver from HitSW
REM                             AS400JTOpen       - DB2/400 Driver from IBM's JTOpen Open Source
REM                             MySQL             - MySQL Driver from MySQL
REM                             Oracle10gR2Oracle - Oracle 10gR2 Driver from Oracle
REM                             Oracle8_1_7Oracle - Oracle 8.1.7 Driver from Oracle
REM                             Oracle9_2_0Oracle - Oracle 9.2.0 Driver from Oracle
REM                             UDB2IBM           - IBM Universal DB/2 for Windows or Linux
REM                             Postgres          - Postgres Database
REM                             EasysoftODBC      - Use EasysoftJDBC-ODBC Bridge
REM     OutputOption      - CSV if output is CSV or PIPE if output is pipe-delimited
REM ================= End of Batch Program Description  ==============

REM ====================== Start of Set Global Variables =============
SET dbservername=127.0.0.1
SET username=test
SET userpasswd=test
SET database=MySQL
SET OutputOption=PIPE
SET MySQLdbname=test
REM ====================== End of Set Global Variables ===============

REM ====================== Start of Main Routine =====================

REM ---------------------- Start of Subfolder creation if they don't exist yet --------------
IF NOT EXIST "..\..\input"    ( MKDIR ..\..\input )
IF NOT EXIST "..\..\output"   ( MKDIR ..\..\output )
IF NOT EXIST "..\..\log"      ( MKDIR ..\..\log )
IF NOT EXIST "..\..\padspace" ( MKDIR ..\..\padspace )
REM ---------------------- Start of Subfolder creation if they don't exist yet --------------

REM ------------------ Start of Extraction Routine -------------------
ECHO Please wait while extraction for all SQL input files is ongoing ...
CD ..

CALL extractSQL_to_txt.bat countries  YES

ECHO SQL Extraction for all input files complete!
REM ------------------ End of Extraction Routine ---------------------

REM ====================== End of Main Routine   =====================
ENDLOCAL

