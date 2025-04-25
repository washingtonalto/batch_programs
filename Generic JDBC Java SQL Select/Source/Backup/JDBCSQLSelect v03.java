/*
=================================================================================================
Program    : JDBCSQLSelect.java
Author     : Washington Alto
Date       : July 2006
Usage      : java JDBCSQLSelect <JDBC URL> <JDBC Driver> <dbuser> <dbpassword> <SQL query file> <Output Results File> <IsCSV>
Parameters :    <JDBC URL>       - JDBC URL
                <JDBC Driver>    - JDBC Driver
                <dbuser>         - Database User Name
                <dbpassword>     - Database Password
                <SQL query file> - Full path for the SQL query file containing to SELECT SQL query to execute
                <Output Results File> - Full path for the results file containing the query results
                <IsCSV>          - "True" if results file is CSV and "False" if results file is fixed-width text
                <HasHeader>      - "True" if display of column names is desired and "False" if display of column names is suppressed
                <PadSpacefile>   - Full path for the pad spaces file
=================================================================================================
 */

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.*;
import oracle.jdbc.pool.OracleDataSource;

public class JDBCSQLSelect {
    
    public JDBCSQLSelect() {}
    
    static public void main(String args[]) {
         String JDBCURL="",
                JDBCdriver="",
                dbuser="",
                dbpassword="",
                SQLqueryfile="",
                OutputFile="",
                StrIsCSV="",
                StrHasHeader="",
                PadSpaceFile="",
                SQLQuery="",
                FieldDelimiter=",",
                TextQualifier="\"";
         String[] PadSpaceFileArray;
         int RowsCopiedIncrement=1000;
         boolean IsCSV = false;
         boolean HasHeader = false;
         System.out.println("JDBCSQLSelect");
         System.out.println("(C) Copyright 2006. Washington Alto. All rights reserved.");
         System.out.println(" ");
         if (args[0].equalsIgnoreCase("/H")) {
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
         } 
         JDBCURL           = args[0];
         JDBCdriver        = args[1];
         dbuser            = args[2];
         dbpassword        = args[3];
         SQLqueryfile      = args[4];
         OutputFile        = args[5];
         StrIsCSV          = args[6];
         StrHasHeader      = args[7];
         if (args.length <= 9) {
             FieldDelimiter    = args[8];
         }    
         if (args.length <= 10) {
             TextQualifier    = args[9];
         }    
         if (args.length <= 11) {
             PadSpaceFile      = args[10];
             PadSpaceFileArray = getFormatFileInput(PadSpaceFile);
         }    
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
         SQLQuery = getInputSQLFile(SQLqueryfile); 
         connectDatabase(JDBCURL,JDBCdriver,dbuser,dbpassword,SQLQuery,OutputFile,IsCSV,HasHeader,FieldDelimiter,TextQualifier,RowsCopiedIncrement);
         System.gc();
    }
    
    static void connectDatabase(String JDBCURL,String JDBCDriver,String dbuser,String dbpassword,String SQLQuery,String OutputFile,boolean IsCSV,boolean HasHeader,String FieldDelimiter,String TextQualifier,int RowsCopiedIncrement) {
         Connection conn = null;
         Statement stmnt = null;
         ResultSet rs = null;
         boolean IsOracle = false;
         
         if (JDBCDriver.equalsIgnoreCase("Oracle")) {
              IsOracle = true;
         } else {
              IsOracle = false;
         }
         try {
              if (IsOracle) {
                   OracleDataSource ods = new OracleDataSource();
                   ods.setURL(JDBCURL);
                   ods.setUser(dbuser);
                   ods.setPassword(dbpassword);
                   conn = ods.getConnection();
              } else {
                   Class.forName(JDBCDriver);
                   conn = DriverManager.getConnection(JDBCURL, dbuser, dbpassword);
              }
              stmnt = conn.createStatement();
              rs    = stmnt.executeQuery(SQLQuery);
              printResultSet(rs,OutputFile, IsCSV, HasHeader,FieldDelimiter,TextQualifier,RowsCopiedIncrement);
              stmnt.close();
              conn.close();
         } catch (SQLException e1) {
                System.err.println("SQL Exception in connectDatabase method: " + e1.getMessage());
                System.err.println("    SQL State                          : " + e1.getSQLState());
                System.err.println("    Vendor Code                        : " + e1.getErrorCode());
         } catch (Exception e2) {
                System.err.println(e2.getMessage());
         }     
    }
    static private void printResultSet(ResultSet rs, String OutputFile, boolean IsCSV, boolean HasHeader,String FieldDelimiter,String TextQualifier,int RowsCopiedIncrement) {
         try {
              BufferedWriter outBuffer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(OutputFile)));
              ResultSetMetaData rsmd = rs.getMetaData();
              int i=0;

              if (HasHeader) {
                   for (int j = 1; j <= rsmd.getColumnCount(); j++) {
                         String fieldName = null;
                         try {
                              fieldName = rsmd.getColumnName(j);
                         } catch (SQLException e1) {
                             System.out.print("SQL Exception in printResultSet method: "+e1.getMessage());
                         }
                         if (IsCSV) {
                              fieldName = TextQualifier+fieldName.trim()+TextQualifier;
                              if (j < rsmd.getColumnCount()) {
                                   fieldName = fieldName+FieldDelimiter;
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
                             /* If field value is NULL, then return a blank string */
                             fieldstring = rs.getString(j) == null ? "" : rs.getString(j);
                        } catch (SQLException e1) {
                             System.out.print("SQL Exception in printResultSet method: "+e1.getMessage());
                        }
                        if (IsCSV) {
                             fieldstring = TextQualifier+fieldstring+TextQualifier;
                             if (j < rsmd.getColumnCount()) {
                                  fieldstring = fieldstring+FieldDelimiter;
                             }
                        }
                        i++;
                        if ((i % RowsCopiedIncrement) == 0) {
                             System.out.println(String.format("%,d",i)+" rows copied");
                        }
                        outBuffer.write(fieldstring);
                        outBuffer.flush();
                   }
                   outBuffer.newLine();
                   outBuffer.flush();
              }
              System.out.println("Total number of rows copied: "+String.format("%,d",i)+".");
              rs.close();
              outBuffer.flush();
              outBuffer.close();
        } catch (IOException e1) {
            System.out.println("I/O Exception in printResultSet method: " + e1.getMessage());
        } catch (Exception e2) {
            System.out.println("Exception in printResultSet method: " + e2.getMessage()); 
        }
    }
   
    static String getInputSQLFile(String InputSQLFile) {
         File InputFile = new File(InputSQLFile);
         String readline = "", 
                SQLquery = "";
         if (InputFile.exists()) {
            try {  
               BufferedReader inBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(InputFile)));
               while ((readline=inBuffer.readLine()) != null) {
                      SQLquery = SQLquery + " "+ readline;
               }
               inBuffer.close();
            } catch (IOException e1) {
               System.err.println("I/0 Exception in getInputSQLFile method: "+e1.getMessage());  
            } catch (Exception e2) {
               System.err.println("Exception in getInputSQLFile method: "+e2.getMessage());  
            }   
         }
         return SQLquery;
    }
    
    static String[] getFormatFileInput(String InputFormatFile) {
        String[] strArray = new String[50]; 
        File InputFile = new File(InputFormatFile);
        String readline = "";
        int ctr = 0;
        if (InputFile.exists()) {
             try {  
                  BufferedReader inBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(InputFile)));
                  while ((readline=inBuffer.readLine()) != null) {
                       strArray[ctr] = readline;
                       ctr++;   
                  }
                  inBuffer.close();
             } catch (IOException e1) {
                  System.err.println("I/0 Exception in getInputSQLFile method: "+e1.getMessage());  
             } catch (Exception e2) {
                  System.err.println("Exception in getInputSQLFile method: "+e2.getMessage());  
             }   
        }
        return strArray; 
    }
    
    
    static String rpadspace(String InputString, int StringLength) {
         String outputString=null;
         InputString = InputString == null ? "" : InputString.trim();
         if (InputString.length() <= StringLength) {
              String formatString="%-"+String.valueOf(StringLength)+"s";
              outputString = String.format(formatString,InputString);
         } else { 
              outputString = InputString.substring(0,StringLength);
         }
         return outputString;
    }

    
    /* 
    String nrpadspace(String InputString, int StringLenth, int Multiplier) {
        
    }
    String salientdate(String InputDate, int OutputType) {
        String.
    }
     
     */

}
