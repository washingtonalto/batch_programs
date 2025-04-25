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
REM ========================= End of Batch Program Notes ======================================

SET classpath=
"C:\Program Files\Java\jre6\bin\java" JDBCSQLSelect "--help"
PAUSE
