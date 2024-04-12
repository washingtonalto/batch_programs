// =================================================================================================
// Program    : JDBCSQLSelect.java
// Author     : Washington Alto
// Date       : July 2006
// Usage      : java JDBCSQLSelect <JDBC URL> <JDBC Driver> <dbuser> <dbpassword> <SQL query file> <Output Results File> <IsCSV>
// Parameters :    <JDBC URL>       - JDBC URL
//                 <JDBC Driver>    - JDBC Driver
//                 <dbuser>         - Database User Name
//                 <dbpassword>     - Database Password
//                 <SQL query file> - Full path for the SQL query file containing to SELECT SQL query to execute
//                 <Output Results File> - Full path for the results file containing the query results
//                 <IsCSV>          - "True" if results file is CSV and "False" if results file is fixed-width text
//                 <HasHeader>      - "True" if display of column names is desired and "False" if display of column names is suppressed
//                 <PadSpacefile>   - Full path for the pad spaces file
// =================================================================================================


import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.*;
import oracle.jdbc.pool.OracleDataSource;

public class JDBCSQLSelect {

    public JDBCSQLSelect () {}

    static public void main(String args[]) {

        try {
            String JDBCURL="",
                   JDBCdriver="",
                   dbuser="",
                   dbpassword="",
                   SQLqueryfile="",
                   Outputresultsfile="",
                   StrIsCSV="",
                   StrHasHeader="",
                   PadSpaceFile="";
            boolean IsCSV = false;
            boolean HasHeader = false;
            boolean IsOracle = false;
            System.out.println("JDBCSQLSelect");
            System.out.println("(C) Copyright 2006. Washington Alto. All rights reserved.");
            System.out.println(" ");
            if (args.length != 9) {
                   System.out.println("Invalid number of arguments!");
                   System.out.println("Usage: <JDBC URL> <JDBC Driver> <dbuser> <dbpassword> <SQL query file> <Output Results File> <IsCSV>");
                   System.out.println("Parameters :    <JDBC URL>       - JDBC URL");
                   System.out.println("                <JDBC Driver>    - JDBC Driver");
                   System.out.println("                <dbuser>         - Database User Name");
                   System.out.println("                <dbpassword>     - Database Password");
                   System.out.println("                <SQL query file> - Full path for the SQL query file containing to SELECT SQL query to execute");
                   System.out.println("                <Output Results File> - Full path for the results file containing the query results");
                   System.out.println("                <IsCSV>          - 'True' if results file is CSV and 'False' if results file is fixed-width text");
                   System.out.println("                <HasHeader>      - 'True' if display of column names is desired and 'False' if display of column names is suppressed");
                   System.out.println("                <PadSpacefile>   - Full path for the pad spaces file");
                   return;
            } else {
                JDBCURL           = args[0];
                JDBCdriver        = args[1];
                dbuser            = args[2];
                dbpassword        = args[3];
                SQLqueryfile      = args[4];
                Outputresultsfile = args[5];
                StrIsCSV          = args[6];
                StrHasHeader      = args[7];
                PadSpaceFile      = args[8];
                if (StrIsCSV.equalsIgnoreCase("True")) {
                     IsCSV = true;
                } else {
                     IsCSV = false;
                }
                if (StrHasHeader.equalsIgnoreCase("True")) {
                     HasHeader = true;
                } else {
                     HasHeader = false;
                }
                if (JDBCdriver.equalsIgnoreCase("Oracle")) {
                     IsOracle = true;
                } else {
                     IsOracle = false;
                }
            }

            BufferedReader inBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(SQLqueryfile)));
            String readline, SQLquery = "";
            while ((readline=inBuffer.readLine()) != null) {
                   SQLquery = SQLquery + " "+ readline;
            }
            inBuffer.close();

            try {
                Connection conn = null;
                Statement stmnt = null;
                if (IsOracle) {
                    OracleDataSource ods = new OracleDataSource();
                    ods.setURL(JDBCURL);
                    ods.setUser(dbuser);
                    ods.setPassword(dbpassword);
                    conn = ods.getConnection();
                } else {
                    Class.forName(JDBCdriver);
                    conn = DriverManager.getConnection(JDBCURL, dbuser, dbpassword);
                }
                stmnt = conn.createStatement();
                printResultSet(stmnt.executeQuery(SQLquery),Outputresultsfile, IsCSV, HasHeader);
                stmnt.close();
                conn.close();
            } catch (SQLException e1) {
                System.err.println("SQL Exception: " + e1.getMessage());
                System.err.println("SQL State:     " + e1.getSQLState());
                System.err.println("Vendor Code:   " + e1.getErrorCode());
            } catch (Exception e2) {
                e2.printStackTrace(System.err);
            }
            System.gc();
        } catch (Exception e3) {
            System.err.println("Exception: " + e3.getMessage());
        }
    }

    static void printResultSet(ResultSet rs, String outputfile, boolean IsCSV, boolean HasHeader) {

        try {
            BufferedWriter outBuffer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputfile)));
            ResultSetMetaData rsmd = rs.getMetaData();
            int i=0;

            if (HasHeader) {
                for (int j = 1; j <= rsmd.getColumnCount(); j++) {
                    String fieldName = null;
                    fieldName = rsmd.getColumnName(j);
                    if (IsCSV) {
                        fieldName = "\""+fieldName.trim()+"\"";
                        if (j < rsmd.getColumnCount()) {
                            fieldName = fieldName+", ";
                        }
                    }
                    outBuffer.write(fieldName);
                    outBuffer.flush();
                }
                outBuffer.newLine();
                outBuffer.flush();
           }

            for (i=0; rs.next();) {
                for (int j = 1; j <= rsmd.getColumnCount(); j++) {
                    String fieldstring = null;
                    try {
                        fieldstring = rs.getString(j);
                        /* If field value is NULL, then return a blank string */
                        if (fieldstring == null) {
                            fieldstring = "";
                        }
                    } catch (Exception e1) {
                        System.out.print("Resultset Getstring Exception: "+e1.getMessage());
                    }
                    if (IsCSV) {
                         fieldstring = "\""+fieldstring.trim()+"\"";
                         if (j < rsmd.getColumnCount()) {
                             fieldstring = fieldstring+", ";
                         }
                    }
                    i++;
                    if ((i % 1000) == 0) {
                        System.out.println(String.valueOf(i)+" rows copied");
                    }
                    outBuffer.write(fieldstring);
                    outBuffer.flush();
                }
                outBuffer.newLine();
                outBuffer.flush();
            }
            System.out.println("Total number of rows copied: "+String.valueOf(i)+".");
            rs.close();
            outBuffer.flush();
            outBuffer.close();
        } catch (Exception e2) {
            System.out.println("PrintResultset Exception: " + e2.getMessage());
        }
    }

}

