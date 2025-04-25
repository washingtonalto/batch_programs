@ECHO OFF
REM ========================= Start of Batch Program Notes ======================================
REM Batch Program : runJDBCSQLSelect.bat
REM Description   : run JAVASQLSQLSelect java class and provides appropriate parameters 
REM Created by    : Washington Alto
REM Date Created  : July 2006
REM Date Modified : July 2006
REM Parameter
REM      %1 - inputSQLfile (w/out the *.txt extension)
REM      %2 - outputresultsfile (w/out the *.txt extension)
REM Environment Variable:
REM      javaexec          - Full path for java executable
REM	 servername        - Server Name
REM	 DBUser            - Database User Name
REM	 DBPassword        - Database User Password
REM	 inputSQLdir       - Directory where SQL query file is located 
REM	 inputSQLfile      - SQL query file containing SQL query to be executed
REM	 outputresultsdir  - Directory where Output Results file is located
REM	 outputresultsfile - Output Results file
REM	 isCSV             - 'True' if CSV and 'False' if fixed-width text 
REM      hasHeader         - 'True' if column name will be displayed and 'False' if column names will not
REM	 databasetype      - See detail below:
REM                             SQL2008Microsoft  - SQL Server 2008 JDBC Driver from Microsoft
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
REM      fielddelimiter    - field delimiter if isCSV is 'True' e.g. ",",":"
REM      textqualifier     - text qualifier if isCSV is 'True' e.g. "\"","'"
REM      rowscopiedincrement - counter used for display on the no. of rows copied to text file
REM      padspacedir       - Directory where Pad Space file is located. 
REM      padspacefile      - Name of Pad Space File containing specifications on how much spaces to pad for the fields.
REM                          This need not be an existing file if isCSV is true.
REM      isVerbose         - displays useful information if isverbose is 'True' otherwise, it suppresses its display
REM      Disphelp          - displays help or usage information
REM      OraclePort        - Oracle Port
REM      OracleSID         - Oracle SID
REM ========================= End of Batch Program Notes ======================================

REM ======== Start Definition of Local Environment Variables ====================
SETLOCAL
SET javaexec=C:\Program Files\Java\jre6\bin\java
SET servername=%dbservername%
SET DBUser=%username%
SET DBPassword=%userpasswd%
SET inputSQLdir=..\input
SET inputSQLfile=%1.txt
SET outputresultsdir=..\output
SET isCSV=True
SET hasHeader=True
SET databasetype=%database%
IF %OutputOption% EQU CSV ( SET fielddelimiter=,
SET textqualifier="
SET outputresultsfile=%2.csv ) else (  SET fielddelimiter=^|
SET textqualifier=
SET outputresultsfile=%2.txt )
SET rowscopiedincrement=5000
SET padspacedir=..\padspace
SET padspacefile=padspace.txt
SET isVerbose=True
SET Disphelp=False
SET OraclePort=1521
SET OracleSID=XE
REM ========== End Definition of Local Environment Variables ====================

REM ========== Start of Database-Specific Environment Variable Definition ==========
REM ----> SQL Server 2008 JDBC from Microsoft Settings <-------
SET SQL2008connectOption=%MSSQL2008dbname%
REM ----> SQL Server 2000 JDBC from Microsoft Settings <-------
SET SQL2000connectOption1=
REM ----> SQL Server 2000 JDBC (JTds) Settings <-------
SET SQL2000connectOption2=
REM ----> DB/2 for AS/400 HitSW JDBC Settings <-------
SET DB2400HitSWlibraries=
SET HitSWconnectOption=ccsid=37;fetch_block_size=64;LIBRARIES=%DB2400HitSWlibraries%
REM ----> DB/2 for AS/400 JTOpen JDBC Settings <-------
SET DB2400JTOpnlibraries=
SET JTOpnconnectOption=LIBRARIES=%DB2400JTOpnlibraries%
REM ----> MySQL JDBC Settings <-------
SET mysqldatabasename=%MySQLdbname%
REM ----> Oracle 10g Release 2 JDBC from Oracle Settings <-------
SET Oracle10gPort=%OraclePort%
SET Oracle10gServiceName=%OracleSID%
REM ----> Oracle 8.1.7 JDBC from Oracle Settings <-------
SET Oracle8Port=%OraclePort%
SET Oracle8ServiceName=%OracleSID%
REM ----> Oracle 9.2.0 JDBC from Oracle Settings <-------
SET Oracle9Port=%OraclePort%
SET Oracle9ServiceName=%OracleSID%
REM ----> DB2 JDBC from IBM Settings <-------
SET DB2DatabaseName=Sample
SET DB2Schema=WASHINGTON
REM ----> Postgres JDBC Settings <-------
SET postgresdatabasename=postgres
REM ----> Easysoft ODBC JDBC Settings <-------
SET DSNName=Northwind
SET EasysoftconnectOption=:logonuser=securitybank:logonpassword=securitybank
REM ----> StelsDBF JDBC Settings <------
SET DBFdir=G:\Backup Softwares\StelsDBF JDBC Driver\stels_dbf\test
REM ========== End of Database-Specific Environment Variable Definition ==========

IF %Disphelp% == True (goto DisplayHelp )
IF %databasetype% == SQL2008Microsoft ( goto SQL2008Microsoft ) 
IF %databasetype% == SQL2000Microsoft ( goto SQL2000Microsoft ) 
IF %databasetype% == SQL2000Jtds ( goto SQL2000Jtds ) 
IF %databasetype% == AS400HitSW ( goto AS400HitSW ) 
IF %databasetype% == AS400JTOpen ( goto AS400JTOpen ) 
IF %databasetype% == MySQL ( goto MySQL ) 
IF %databasetype% == Oracle10gR2Oracle ( goto Oracle10gR2Oracle )
IF %databasetype% == UDB2IBM ( goto UDB2IBM )
IF %databasetype% == Postgres ( goto Postgres )
IF %databasetype% == Oracle8_1_7Oracle ( goto Oracle8_1_7Oracle )
IF %databasetype% == Oracle9_2_0Oracle ( goto Oracle9_2_0Oracle )
IF %databasetype% == EasysoftODBC ( goto EasysoftODBC )
IF %databasetype% == StelsDBF ( goto StelsDBF )
goto end 

REM ---------- Start of classpath, JDBCURL and JDBCDriver Environment Variable Definition ------------

REM ----> SQL Server 2008 JDBC from Microsoft Settings <-------
:SQL2008Microsoft
SET classpath=.;Drivers\SQL2008Microsoft\sqljdbc4.jar
SET JDBCURL=jdbc:sqlserver://%servername%:1433;databaseName=%SQL2008connectOption%
SET JDBCDriver=com.microsoft.sqlserver.jdbc.SQLServerDriver
goto main

REM ----> SQL Server 2000 JDBC from Microsoft Settings <-------
:SQL2000Microsoft
SET classpath=.;Drivers\SQL2000Microsoft\msbase.jar;Drivers\SQL2000Microsoft\mssqlserver.jar;Drivers\SQL2000Microsoft\msutil.jar
SET JDBCURL=jdbc:microsoft:sqlserver://%servername%:1433;%SQL2000connectOption1%
SET JDBCDriver=com.microsoft.jdbc.sqlserver.SQLServerDriver
goto main

REM ----> SQL Server 2000 JDBC (JTds) Settings <-------
:SQL2000Jtds
SET classpath=.;Drivers\SQL2000Jtds\jtds-1.2.jar
SET JDBCURL=jdbc:jtds:sqlserver://%servername%:1433;%SQL2000connectOption2%
SET JDBCDriver=net.sourceforge.jtds.jdbc.Driver
goto main

REM ----> DB/2 for AS/400 HitSW JDBC Settings <-------
:AS400HitSW
SET classpath=.;Drivers\AS400HitSW\hitjdbc400.jar;Drivers\AS400HitSW\hitlicense.jar
SET JDBCURL=jdbc:as400://%servername%;%HitSWconnectOption%
SET JDBCDriver=hit.as400.As400Driver
goto main

REM ----> DB/2 for AS/400 JTOpen JDBC Settings <-------
:AS400JTOpen
SET classpath=.;Drivers\AS400JTOpen\jt400.jar
SET JDBCURL=jdbc:as400://%servername%;%JTOpnconnectOption%
SET JDBCDriver=com.ibm.as400.access.AS400JDBCDriver
goto main

REM ----> MySQL JDBC Settings <-------
:MySQL
SET classpath=.;Drivers\MySQL\mysql-connector-java-5.1.12-bin.jar
SET JDBCURL=jdbc:mysql://%servername%:3306/%mysqldatabasename%
SET JDBCDriver=com.mysql.jdbc.Driver
goto main

REM ----> Oracle 10g Release 2 JDBC from Oracle Settings <-------
:Oracle10gR2Oracle
SET classpath=.;Drivers\Oracle10gR2Oracle\ojdbc14_g.jar;Drivers\Oracle10gR2Oracle\orai18n.jar
SET JDBCURL=jdbc:oracle:thin:@//%servername%:%Oracle10gPort%/%Oracle10gServiceName%
SET JDBCDriver=Oracle
goto main

REM ----> Oracle 8.1.7 JDBC from Oracle Settings <-------
:Oracle8_1_7Oracle
SET classpath=.;Drivers\Oracle8_1_7Oracle\classes12.zip;Drivers\Oracle8_1_7Oracle\nls_charset12.zip
SET JDBCURL=jdbc:oracle:thin:@%servername%:%Oracle8Port%:%Oracle8ServiceName%
SET JDBCDriver=oracle.jdbc.driver.OracleDriver
goto main

REM ----> Oracle 9.2.0 JDBC from Oracle Settings <-------
:Oracle9_2_0Oracle
SET classpath=.;Drivers\Oracle9_2_0Oracle\ojdbc14.jar;Drivers\Oracle9_2_0Oracle\ojdbc14_g.jar;Drivers\Oracle9_2_0Oracle\ocrs12.zip
SET JDBCURL=jdbc:oracle:thin:@%servername%:%Oracle9Port%:%Oracle9ServiceName%
SET JDBCDriver=oracle.jdbc.driver.OracleDriver
goto main

REM ----> DB2 JDBC from IBM Settings <-------
:UDB2IBM
SET classpath=.;Drivers\UDB2IBM\db2jcc_license_cu.jar;Drivers\UDB2IBM\db2jcc.jar;Drivers\UDB2IBM\db2jcct2.dll

REM This is for DB/2 version 8.2 and above only
REM SET DB2connectOption=currentSchema=%DB2Schema%;
SET DB2connectOption=

REM JDBC Type 2 Calls
SET JDBCURL=jdbc:db2:%DB2DatabaseName%

REM JDBC Type 4 Calls
REM SET JDBCURL=jdbc:db2://%servername%:50000/%DB2DatabaseName%:%DB2connectOption%

SET JDBCDriver=com.ibm.db2.jcc.DB2Driver
goto main

REM ----> Postgres JDBC Settings <-------
:Postgres
SET classpath=.;Drivers\Postgres\postgresql-8.1-407.jdbc3.jar
SET JDBCURL=jdbc:postgresql://%servername%:5432/%postgresdatabasename%
SET JDBCDriver=org.postgresql.Driver
goto main

REM ----> Easysoft ODBC JDBC Settings <-------
:EasysoftODBC
SET classpath=.;Drivers\EasysoftODBC\EJOB.jar
SET JDBCURL=jdbc:easysoft://%servername%:8831/%DSNName%%EasysoftconnectOption%
SET JDBCDriver=easysoft.sql.jobDriver
goto main

REM ----> StelsDBF JDBC Settings <-------
:StelsDBF
SET classpath=.;Drivers\STELSDBF\dbfdriver.jar
SET JDBCURL=jdbc:jstels:dbf:"%DBFdir%"
SET JDBCDriver=jstels.jdbc.dbf.DBFDriver
goto main

REM ---------- End of classpath, JDBCURL and JDBCDriver Environment Variable Definition --------------


:DisplayHelp
SET classpath=
"%javaexec%" JDBCSQLSelect "--help"
goto end

:main

ECHO -- JDBCSQLSelect Start -- 
ECHO.
ECHO Start Date and Time: 
DATE /T
TIME < NUL
ECHO.
ECHO.

"%javaexec%" JDBCSQLSelect %JDBCURL% %JDBCDriver% %DBUser% %DBPassword% "%inputSQLdir%\%inputSQLfile%"  "%outputresultsdir%\%outputresultsfile%" %isCSV% %hasHeader% "%fielddelimiter%" "%textqualifier%" "%rowscopiedincrement%" "%padspacedir%\%padspacefile%" "%isVerbose%" 

ECHO.
ECHO End Date and Time: 
DATE /T
TIME < NUL
ECHO.
ECHO.
ECHO -- JDBCSQLSelect End -- 

:end
REM ========== End of Main JAVA Routine =========================================
ENDLOCAL


