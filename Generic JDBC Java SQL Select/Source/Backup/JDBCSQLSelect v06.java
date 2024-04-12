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
         boolean IsCSV = false,
                 HasHeader = false;
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
         FieldDelimiter    = args[8];
         TextQualifier     = args[9];
         try {
             RowsCopiedIncrement = Integer.valueOf(args[10]);
         } catch (NumberFormatException e1) {
             System.err.println("Number Format Exception at main() method: "+e1.getMessage()); 
             System.err.println("");
             System.err.println("Stack Trace:");
             e1.printStackTrace();
         }
         PadSpaceFile      = args[11];
         
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
         PadSpaceFileArray = getFormatFileInput(PadSpaceFile);
         SQLQuery = getInputSQLFile(SQLqueryfile); 
         connectDatabase(JDBCURL,JDBCdriver,dbuser,dbpassword,SQLQuery,OutputFile,IsCSV,HasHeader,FieldDelimiter,TextQualifier,RowsCopiedIncrement,PadSpaceFileArray);
         System.gc();
    }
    
    static void connectDatabase(String JDBCURL,String JDBCDriver,String dbuser,String dbpassword,String SQLQuery,String OutputFile,boolean IsCSV,boolean HasHeader,String FieldDelimiter,String TextQualifier,int RowsCopiedIncrement,String[] PadSpaceFileArray) {
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
              printResultSet(rs,OutputFile, IsCSV, HasHeader,FieldDelimiter,TextQualifier,RowsCopiedIncrement,PadSpaceFileArray);
              stmnt.close();
              conn.close();
         } catch (SQLException e1) {
                System.err.println("SQL Exception in connectDatabase method: " + e1.getMessage());
                System.err.println("    SQL State                          : " + e1.getSQLState());
                System.err.println("    Vendor Code                        : " + e1.getErrorCode());
                System.err.println("");
                System.err.println("Stack Trace: ");
                e1.printStackTrace();
         } catch (Exception e2) {
                System.err.println("Exception in connectDatabase method: "+e2.getMessage());
                System.err.println("");
                System.err.println("Stack Trace: ");
                e2.printStackTrace();
         }     
    }
    static private void printResultSet(ResultSet rs, String OutputFile, boolean IsCSV, boolean HasHeader,String FieldDelimiter,String TextQualifier,int RowsCopiedIncrement,String[] PadSpaceFileArray) {
         boolean IsPadSpaceFileExist = false;
         IsPadSpaceFileExist = PadSpaceFileArray == null ? false : true;
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
                             System.err.println("SQL Exception in printResultSet method: "+e1.getMessage());
                             System.err.println("");
                             System.err.println("Stack Trace: ");
                             e1.printStackTrace();
                         }
                         if (IsCSV) {
                              fieldName = "["+fieldName.trim()+"]";
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
                             System.err.println("SQL Exception in printResultSet method: "+e1.getMessage());
                             System.err.println("");
                             System.err.println("Stack Trace: ");
                             e1.printStackTrace();
                        }
                        if (IsPadSpaceFileExist && !IsCSV) {
                             try {
                                 fieldstring = padFieldNames (fieldstring,PadSpaceFileArray[j-1]); 
                             } catch (Exception e1) {
                                 System.err.println("Exception in printResultSet method: "+e1.getMessage()); 
                                 System.err.println("");
                                 System.err.println("Stack Trace: ");
                                 e1.printStackTrace();
                             }
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
            System.err.println("I/O Exception in printResultSet method: " + e1.getMessage());
            System.err.println("");
            System.err.println("Stack Trace: ");
            e1.printStackTrace();
        } catch (Exception e2) {
            System.err.println("Exception in printResultSet method: " + e2.getMessage()); 
            System.err.println("");
            System.err.println("Stack Trace: ");
            e2.printStackTrace();
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
                System.err.println("");
                System.err.println("Stack Trace: ");
                e1.printStackTrace();
            } catch (Exception e2) {
                System.err.println("Exception in getInputSQLFile method: "+e2.getMessage());  
                System.err.println("");
                System.err.println("Stack Trace: ");
                e2.printStackTrace();
            }   
         }
         return SQLquery;
    }
    
    static String[] getFormatFileInput(String InputFormatFile) {
        Vector strVector = new Vector(1);
        String[] strArray = null;
        File InputFile = new File(InputFormatFile);
        String readline = "";
        if (InputFile.exists()) {
             try {  
                  BufferedReader inBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(InputFile)));
                  while ((readline=inBuffer.readLine()) != null) {
                       strVector.addElement(readline);
                  }
                  inBuffer.close();
             } catch (IOException e1) {
                  System.err.println("I/0 Exception in getInputSQLFile method: "+e1.getMessage());  
                  System.err.println("");
                  System.err.println("Stack Trace: ");
                  e1.printStackTrace();
             } catch (Exception e2) {
                  System.err.println("Exception in getInputSQLFile method: "+e2.getMessage());  
                  System.err.println("");
                  System.err.println("Stack Trace: ");
                  e2.printStackTrace();
             } 
             strArray = new String[strVector.size()];
             for (int i = 0; i < strVector.size();i++) {
                  strArray[i] = (String) strVector.get(i);
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
    
    static String nrpadspace(String InputString, int StringLength, int Multiplier) {
         int MAXNUMERIC = 2147483647;
         String precision = "0";
         double Inputdbl = 0;
         String outputString = "";
        
         InputString = InputString == null ? "" : InputString;
         if (!InputString.trim().equalsIgnoreCase("")) {
              try {
                   Inputdbl = Double.valueOf(InputString) * Multiplier;
              } catch (NumberFormatException e1) {
                   System.err.println("Number Format Exception in nrpadspace method: "+e1.getMessage());
                   System.err.println("");
                   System.err.println("Stack Trace: ");
                   e1.printStackTrace();
                   return rpadspace("",StringLength);
              }
              outputString = Math.abs(Inputdbl) > MAXNUMERIC ? rpadspace("MAXINTEGER",StringLength) : rpadspace(String.format("%."+precision+"f",Inputdbl),StringLength);
         } else {
              outputString = rpadspace("",StringLength);
         }
         return outputString;
    }
    
    static String salientdate(String InputDate, boolean isforSalient) {
         String outputString = "";
         if (InputDate != null) { 
             outputString = isforSalient ? InputDate.substring(5,7)+InputDate.substring(8,10)+InputDate.substring(2,4) : InputDate.substring(5,7)+"/"+InputDate.substring(8,10)+"/"+InputDate.substring(0,4);
         } else {
             outputString = isforSalient ? "      " : "          "; 
         }
         return outputString;
    }
    
    static String padFieldNames (String inputFieldName,String padStrField) {
         String outputString = inputFieldName;
         String[] arglist;
         arglist = padStrField.split(",");
         try {
              if (arglist[0].trim().toUpperCase().equalsIgnoreCase("CHAR")) {
                    outputString = rpadspace(inputFieldName,Integer.valueOf(arglist[1]));
              } else if (arglist[0].trim().toUpperCase().equalsIgnoreCase("NUMERIC")) {
                    if (arglist.length == 3) {          
                         outputString = nrpadspace(inputFieldName,Integer.valueOf(arglist[1]),Integer.valueOf(arglist[2]));
                    } else {
                         outputString = nrpadspace(inputFieldName,Integer.valueOf(arglist[1]),100);
                    }
              } else if (arglist[0].trim().toUpperCase().equalsIgnoreCase("DATE")) {
                    if (arglist.length == 3) {  
                         outputString = salientdate(inputFieldName,true);
                    } else {
                         outputString = salientdate(inputFieldName,false);
                    }     
              } else {
                    System.err.println("Error in Pad Space File detected!");
              }
         } catch (Exception e1) {
              System.err.println("Exception in padFieldNames method: "+e1.getMessage());
              System.err.println("");
              System.err.println("Stack Trace: ");
              e1.printStackTrace();
         }     
         return outputString;
    }     
}
