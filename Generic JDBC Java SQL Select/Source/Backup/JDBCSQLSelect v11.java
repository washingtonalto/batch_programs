/*
=================================================================================================
Program      : JDBCSQLSelect.java
Author       : Washington Alto
Date Started : July 2006
Date Modified: October 2006
Purpose      : Generic command-line JDBC tool for dumping SQL Select statements to fixed-width text and delimited text files
Usage        : java JDBCSQLSelect [JDBC URL] [JDBC Driver] [DB User] [DB Password] [SQL Query File] [Output Results File] [Is CSV]
                                  [Has Header] [Field Delim] [Text Qualifier] [Rows Copied Incre] [Pad Space File] [Is Verbose]
Parameters   :  [JDBC URL]            - JDBC URL
                [JDBC Driver]         - JDBC Driver
                [DB User]             - Database User Name
                [DB Password]         - Database Password
                [SQL Query File]      - Full path for the SQL query file containing to SELECT SQL query to execute
                [Output Results File] - Full path for the results file containing the query results
                [Is CSV]              - "True" if results file is CSV and "False" if results file is fixed-width text
                [Has Header]          - "True" if display of column names is desired and "False" if display of column names is suppressed
                [Field Delim]         - Sting used to delimit if isCSV is "True". This is by default ","
                [Text Qualifier]      - String used as qualifier for field names if isCSV is "True". This is by default the double quote
                [Rows Copied Incre]   - Number used as increment for display in console for informing the number of rows copied. By default, it's 1000.
                [Pad Space File]      - Full path for the Pad Spaces Specification File
                [Is Verbose]          - "True" if verbose display is desired e.g. database name, database version, driver name, etc.; otherwise display is
                                        suppressed.
 =================================================================================================
 */

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.*;
import oracle.jdbc.pool.OracleDataSource;

public class JDBCSQLSelect {

    /* This is the displayed Program version for this JDBCSQLSelect */ 
    static final String ProgramVersion = "1.0";
 
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
                TextQualifier="\"",
                StrIsVerbose="";
         String[] PadSpaceFileArray;
         int RowsCopiedIncrement=1000;
         boolean IsCSV = false,
                 HasHeader = false,
                 IsVerbose= false;
         System.out.println("JDBCSQLSelect - Generic JDBC tool for dumping SQL Select statements to ");
         System.out.println("                fixed-width text and delimited text files              ");
         System.out.println("Copyright (c) 2006, Washington Alto. All rights reserved.              ");
         System.out.println("Version: "+ProgramVersion);
         System.out.println("");
         
         /* Displays help if '/?' is used as first argument in the command-line */
         if (args[0].equalsIgnoreCase("--help")) {
              System.out.println("usage: java JDBCSQLSelect [JDBC URL] [JDBC Driver] [DB User] [DB Password]  ");
              System.out.println("                          [SQL Query File] [Output Results File] [Is CSV]   ");
              System.out.println("                          [Has Header] [Field Delim] [Text Qualifier]       ");
              System.out.println("                          [Rows Copied Incre] [Pad Space File] [Is Verbose] ");
              System.out.println("            [JDBC URL]            - JDBC URL                                ");
              System.out.println("            [JDBC Driver]         - JDBC Driver                             ");
              System.out.println("            [DB User]             - Database User Name                      ");
              System.out.println("            [DB Password]         - Database Password                       ");
              System.out.println("            [SQL Query File]      - Full path for the file containing SELECT");
              System.out.println("                                    SQL statements to execute               ");
              System.out.println("            [Output Results File] - Full path for the output file containing");
              System.out.println("                                    the results either in delimited text or ");
              System.out.println("                                    fixed-width text format                 ");
              System.out.println("            [Is CSV]              - 'True' if the output file is delimited  ");
              System.out.println("                                    and 'False' if output file is fixed-width");
              System.out.println("                                    text                                    ");
              System.out.println("            [Has Header]          - 'True' if column names will be displayed");
              System.out.println("                                    and 'False' if column names display is  ");
              System.out.println("                                    suppressed                              ");
              System.out.println("            [Field Delim]         - Delimiter used to separate fields if    ");
              System.out.println("                                    output file is delimited file; this is  ");
              System.out.println("                                    typically a comma (,)                   ");
              System.out.println("            [Text Qualifier]      - Text qualifier used in surrounding all  ");
              System.out.println("                                    fields if output file is delimited file;");
              System.out.println("                                    this is typically a double-quote (\")   ");
              System.out.println("            [Rows Copied Incre]   - Number used as increment for display in ");
              System.out.println("                                    console for the nos. of rows copied     ");
              System.out.println("            [Pad Space File]      - Full path for the Pad Spaces Specs File ");
              System.out.println("            [Is Verbose]          - 'True' if verbose information such as   ");
              System.out.println("                                    JDBC driver name and version will be    ");
              System.out.println("                                    displayed and 'False' if otherwise      ");
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
             /* Converts string inputs from command-line to integer for rowscopiedincrement variable */
             RowsCopiedIncrement = Integer.valueOf(args[10]);
         } catch (NumberFormatException e1) {
             System.err.println("Number Format Exception at main() method: "+e1.getMessage()); 
             System.err.println("");
             System.err.println("Stack Trace:");
             e1.printStackTrace();
         }
         PadSpaceFile      = args[11];
         StrIsVerbose      = args[12];
         
         /* Assigns boolean variables given inputs from command-line */
         IsCSV =StrIsCSV.equalsIgnoreCase("True") ? true : false;
         HasHeader = StrHasHeader.equalsIgnoreCase("True") ? true : false;
         IsVerbose = StrIsVerbose.equalsIgnoreCase("True") ? true : false; 
                 
         SQLQuery = getInputSQLFile(SQLqueryfile); 
         PadSpaceFileArray = getFormatFileInput(PadSpaceFile);
         connectDatabase(JDBCURL,JDBCdriver,dbuser,dbpassword,SQLQuery,OutputFile,IsCSV,HasHeader,FieldDelimiter,TextQualifier,RowsCopiedIncrement,PadSpaceFileArray,IsVerbose);
         System.gc();      // performs system-wide garbage collection to free-up memory 
    }
    
    static void connectDatabase(String JDBCURL,String JDBCDriver,String dbuser,String dbpassword,String SQLQuery,String OutputFile,boolean IsCSV,boolean HasHeader,String FieldDelimiter,String TextQualifier,int RowsCopiedIncrement,String[] PadSpaceFileArray,boolean IsVerbose) {
         Connection conn = null;
         Statement stmnt = null;
         ResultSet rs = null;
         DatabaseMetaData dbmetadata = null;
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
              if (IsVerbose) {
                    dbmetadata = conn.getMetaData();
                    System.out.println("Supplied Information: ");
                    System.out.println("JDBC Driver: "+JDBCDriver);
                    System.out.println("JDBC URL   : "+JDBCURL);
                    System.out.println("User Name  : "+dbuser);
                    System.out.println("");
                    System.out.println("Extracted Information: ");
                    System.out.println("Database Name      : "+dbmetadata.getDatabaseProductName());
                    System.out.println("Database Version   : "+dbmetadata.getDatabaseProductVersion());
                    System.out.println("JDBC Driver Name   : "+dbmetadata.getDriverName());
                    System.out.println("JDBC Driver Version: "+dbmetadata.getDriverVersion());
                    System.out.println("Available Database String Functions  : "+dbmetadata.getStringFunctions());
                    System.out.println("Available Database Numeric Functions : "+dbmetadata.getNumericFunctions());
                    System.out.println("Available Database DateTime Functions: "+dbmetadata.getTimeDateFunctions());
                    System.out.println("Available Database Systems Functions : "+dbmetadata.getSystemFunctions());
                    System.out.println("String used to quote SQL identifiers : "+dbmetadata.getIdentifierQuoteString());
                    System.out.println("");
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
         int i=0;
         try {
              System.out.println("Start of copying rows: ");
              BufferedWriter outBuffer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(OutputFile)));
              ResultSetMetaData rsmd = rs.getMetaData();
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
                        outBuffer.write(fieldstring);
                        outBuffer.flush();
                   }
                   i++;
                   if ((i % RowsCopiedIncrement) == 0) {
                        System.out.println(String.format("%,d",i)+" rows copied");
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
        } else {
             System.out.println("NOTE: Pad Spaces Specification File does not exist!       ");
             System.out.println("      Fields won't be padded in fixed-width text output...");
             System.out.println("");
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
                    if (arglist.length == 1) {  
                         outputString = salientdate(inputFieldName,true);
                    } else {
                         outputString = salientdate(inputFieldName,false);
                    }     
              } else {
                    System.err.println("Pad Space File has an unidentified 1st column parameter!");
                    System.err.println("Column values must be either CHAR,NUMERIC or DATE only.");
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
