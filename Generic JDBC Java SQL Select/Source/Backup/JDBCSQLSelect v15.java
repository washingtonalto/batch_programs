/*
=================================================================================================
Program      : JDBCSQLSelect.java
Author       : Washington Alto
Copyright    : Copyright (c) 2006, Washington Alto. All rights reserved.
Date Started : July 2006
Date Modified: October 6, 2006
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
 Functions Calls:
   public JDBCSQLSelect()
   static public void main(String args[])
   static void connectDatabase(String JDBCURL,String JDBCDriver,String dbuser,String dbpassword,String SQLQuery,String OutputFile,boolean IsCSV,boolean HasHeader,String FieldDelimiter,String TextQualifier,int RowsCopiedIncrement,String[] PadSpaceFileArray,boolean IsVerbose)
   static private void printResultSet(ResultSet rs, String OutputFile, boolean IsCSV, boolean HasHeader,String FieldDelimiter,String TextQualifier,int RowsCopiedIncrement,String[] PadSpaceFileArray)
   static String getInputSQLFile(String InputSQLFile)
   static String[] getFormatFileInput(String InputFormatFile)
   static String rpadspace(String InputString, int StringLength)
   static String nrpadspace(String InputString, int StringLength, int Multiplier)
   static String salientdate(String InputDate, boolean isforSalient)
   static String padFieldNames (String inputFieldName,String padStrField)
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
              System.out.println("");
              System.out.println("   NOTE: Pad Spaces Specs File contains the ff. parameters for each line    ");
              System.out.println("         where each line correspond to the pad spaces specification for a   ");
              System.out.println("         a field e.g. 1st line is for 1st field, 2nd line is for 2nd field  ");
              System.out.println("         and so forth                                                       ");
              System.out.println("             1st parameter - This is either CHAR, NUMERIC or DATE           ");
              System.out.println("             2nd parameter - This is the string length of the field to be   ");
              System.out.println("                             padded with spaces. If 1st parameter is date   ");
              System.out.println("                             then date will be displayed as 'mm/dd/yyyy'    ");
              System.out.println("             3rd parameter - If the 1st parameter is NUMERIC, then this is  ");
              System.out.println("                             the multiplier number w/c is multiplied to the ");
              System.out.println("                             the numeric field                              ");
              System.out.println("");
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
                 
         SQLQuery = getInputSQLFile(SQLqueryfile);              // retrieves SQL statement from SQL query file
         PadSpaceFileArray = getFormatFileInput(PadSpaceFile);  // retrieves Pad Spaces Specification in string array from Pad Spaces Specs file
         connectDatabase(JDBCURL,JDBCdriver,dbuser,dbpassword,SQLQuery,OutputFile,IsCSV,HasHeader,FieldDelimiter,TextQualifier,RowsCopiedIncrement,PadSpaceFileArray,IsVerbose);
         System.gc();      // performs system-wide garbage collection to free-up memory 
    }
    
    /* ========================================================================= 
    connectDatabase - main method for establishing database connection to target database 
       ========================================================================= */
    static void connectDatabase(String JDBCURL,String JDBCDriver,String dbuser,String dbpassword,String SQLQuery,String OutputFile,boolean IsCSV,boolean HasHeader,String FieldDelimiter,String TextQualifier,int RowsCopiedIncrement,String[] PadSpaceFileArray,boolean IsVerbose) {
         Connection conn = null;
         Statement stmnt = null;
         ResultSet rs = null;
         DatabaseMetaData dbmetadata = null;
         boolean IsOracle = false;
         
         /* If JDBCDriver string is 'Oracle' then that means connection should use Oracle's JDBC Driver */
         IsOracle = JDBCDriver.equalsIgnoreCase("Oracle") ? true : false;
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
                    System.out.println("String used to quote SQL identifiers : "+dbmetadata.getIdentifierQuoteString());
                    System.out.println("");
                    System.out.println("Available Database String Functions  : "+dbmetadata.getStringFunctions());
                    System.out.println("Available Database Numeric Functions : "+dbmetadata.getNumericFunctions());
                    System.out.println("Available Database DateTime Functions: "+dbmetadata.getTimeDateFunctions());
                    System.out.println("Available Database Systems Functions : "+dbmetadata.getSystemFunctions());
                    System.out.println("");
                    System.out.println("Database Vendor Preferred Term for Catalog: "+dbmetadata.getCatalogTerm());
                    System.out.println("Database Vendor Preferred Term for Schema : "+dbmetadata.getSchemaTerm());
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
    
   /* ========================================================================= 
      printResultSet - main method for displaying results of SQL query to output file "OutputFile" 
      ========================================================================= */
    static private void printResultSet(ResultSet rs, String OutputFile, boolean IsCSV, boolean HasHeader,String FieldDelimiter,String TextQualifier,int RowsCopiedIncrement,String[] PadSpaceFileArray) {
         boolean IsPadSpaceFileExist = false;                              // Indicator for determining if Pad Spaces Specs File exist or not
         IsPadSpaceFileExist = PadSpaceFileArray == null ? false : true;   // PadSpaceFileArray is null if Pad Space File does not exist
         int i=0;      // counter for the number of rows copied
         try {
              System.out.println("Start of copying rows: ");
              BufferedWriter outBuffer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(OutputFile)));
              ResultSetMetaData rsmd = rs.getMetaData();
              
              /* Routine for displaying field names */
              if (HasHeader) {
                   for (int j = 1; j <= rsmd.getColumnCount(); j++) {   // j is the counter for column index specified field position in the SELECT SQL statement
                         String fieldName = null;  // Field Name as specified in SQL SELECT statement
                         try {
                              fieldName = rsmd.getColumnName(j);
                         } catch (SQLException e1) {
                             System.err.println("SQL Exception in printResultSet method: "+e1.getMessage());
                             System.err.println("");
                             System.err.println("Stack Trace: ");
                             e1.printStackTrace();
                         }
                         /* Routine to execute if output is delimited */
                         if (IsCSV) {
                              fieldName = "["+fieldName.trim()+"]";
                              if (j < rsmd.getColumnCount()) {
                                   fieldName = fieldName+FieldDelimiter;
                              }
                         }
                         outBuffer.write(fieldName);
                         outBuffer.flush();  // Flush unused memory
                   }
                   outBuffer.newLine();
                   outBuffer.flush();  // Flush unused memory
              }

              /* Routine for displaying SQL output to output file */
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
                        
                        /* Routine for Padding Field Names will execute if Pad Spaces Specs file exist and
                           output file is not delimited */
                        if (IsPadSpaceFileExist && !IsCSV) {
                             try {
                                 fieldstring = padFieldNames (fieldstring,PadSpaceFileArray[j-1]); 
                             } catch (ArrayIndexOutOfBoundsException e1) {
                                 System.err.println("NOTE: The number of fields in Pad Space Specs file   "); 
                                 System.err.println("      do not correspond to the number of fields      "); 
                                 System.err.println("      to be dumped to output file. The last few      "); 
                                 System.err.println("      fields won't be padded with spaces or formatted"); 
                                 System.err.println("Column Index currently processed: "+String.valueOf(j));
                                 System.err.println(""); 
                             } catch (Exception e2) {
                                 System.err.println("Exception in printResultSet method: "+e2.getMessage()); 
                                 System.err.println("");
                                 System.err.println("Stack Trace: ");
                                 e2.printStackTrace();
                             }
                        }
                        /* Routine to execute if output file is delimited */
                        if (IsCSV) {
                             fieldstring = TextQualifier+fieldstring+TextQualifier;
                             if (j < rsmd.getColumnCount()) {
                                  fieldstring = fieldstring+FieldDelimiter;
                             }
                        }
                        outBuffer.write(fieldstring);
                        outBuffer.flush();   // Flush unused memory
                   }
                   i++;
                   if ((i % RowsCopiedIncrement) == 0) {  // Displays to console the number of rows copied per RowsCopiedIncrement
                        System.out.println(String.format("%,d",i)+" rows copied");
                   }
                   outBuffer.newLine();
                   outBuffer.flush();   // Flush unused memory
              }
              System.out.println("Total number of rows copied: "+String.format("%,d",i)+".");
              rs.close();
              outBuffer.flush();   // Flush unused memory
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
 
   /* ========================================================================= 
      getInputSQLFile - method for retrieving SQL statement from SQL Query File 
      ========================================================================= */
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

   /* ========================================================================= 
      getFormatFileInput - Retrieves Pad Spaces Specification from the Pad Spaces Specs file
                           and stores them to a string array where each array element correspond
                           to a line in the Pad Spaces Specs file 
      ========================================================================= */
    static String[] getFormatFileInput(String InputFormatFile) {
        Vector strVector = new Vector(1);  // Uses vector since the number of fields in the Pad Spaces Specs File may vary
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

   /* ========================================================================= 
      rpadspace - Gets a string as input and pads trailing spaces to fit the 
                  number specified in StringLength. If StringLength is less than
                  the length of InputString, then the InutString is truncated to
                  the appropriate length specified in StringLength
      ========================================================================= */
    static String rpadspace(String InputString, int StringLength) {
         String outputString=null;
         /* if InputString is null, then return an empty string */
         InputString = InputString == null ? "" : InputString.trim();
         if (InputString.length() <= StringLength) {
              /* Makes use of format function to pad trailing spaces */
              String formatString="%-"+String.valueOf(StringLength)+"s";
              outputString = String.format(formatString,InputString);
         } else { 
              outputString = InputString.substring(0,StringLength);
         }
         return outputString;
    }

   /* ========================================================================= 
      nrpadspace - Gets a numeric string as input and pads trailing spaces to fit the 
                   number specified in StringLength. If StringLength is less than
                   the length of InputString, then the InutString is truncated to
                   the appropriate length specified in StringLength. If the 
                   numeric string exceed the amount specified in MAXNUMERIC, then
                   the output string is a padded (or truncated) string whose value
                   is MAXINTEGER. Multiplier is a number that is multiplied to the
                   number before the function returns the numeric result
      ========================================================================= */
    static String nrpadspace(String InputString, int StringLength, int Multiplier) {
         int MAXNUMERIC = 2147483647;  // this is the maximum integer limit to be displayed
         String precision = "0";  // precision is the number of decimals digits to the right of decimal point to be displayed
         double Inputdbl = 0;
         String outputString = "";
        
         /* if numeric string InputString is null, then return an empty string */
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
              /* if the absolute of numeric string InputString exceeds MAXNUMERIC, then output "MAXINTEGER". Format prevents number from being displayed
                 in Scientific Exponential notation  */
              outputString = Math.abs(Inputdbl) > MAXNUMERIC ? rpadspace("MAXINTEGER",StringLength) : rpadspace(String.format("%."+precision+"f",Inputdbl),StringLength);
         } else {
              outputString = rpadspace("",StringLength);  // makes use of rpadspace to handle the padding of data
         }
         return outputString;
    }

   /* ========================================================================= 
      salientdate - method for returning formatted date. If isforSalient is true,
                    then the date string return is in 'mmddyy' format; otherwise,
                    it returns it in 'mm/dd/yyyy' format
      ========================================================================= */
    static String salientdate(String InputDate, boolean isforSalient) {
         String outputString = "";
         /* The routine below assumes that the date string is in JDBC datetime format; that is,
            it displays any datetime fields as 'yyyy-mm-dd hh:mm:ss.nn format' */
         if (InputDate != null) { 
             outputString = isforSalient ? InputDate.substring(5,7)+InputDate.substring(8,10)+InputDate.substring(2,4) : InputDate.substring(5,7)+"/"+InputDate.substring(8,10)+"/"+InputDate.substring(0,4);
         } else {
             outputString = isforSalient ? "      " : "          "; 
         }
         return outputString;
    }

   /* ========================================================================= 
      padFieldNames - method for choosing the appropriate pad functions e.g. rpadspace,
                      nrpadspace, and salientdate given the commands specified in the 
                      padStrField string
      ========================================================================= */
    static String padFieldNames (String inputFieldName,String padStrField) {
         String outputString = inputFieldName;
         String[] arglist;
         arglist = padStrField.split(",");  // parses the string padStrField based on the delimiter ",""
         try {
              if (arglist[0].trim().toUpperCase().equalsIgnoreCase("CHAR")) {
                    outputString = rpadspace(inputFieldName,Integer.valueOf(arglist[1]));
              } else if (arglist[0].trim().toUpperCase().equalsIgnoreCase("NUMERIC")) {
                    if (arglist.length == 3) {          
                         outputString = nrpadspace(inputFieldName,Integer.valueOf(arglist[1].trim()),Integer.valueOf(arglist[2].trim()));
                    } else {
                         outputString = nrpadspace(inputFieldName,Integer.valueOf(arglist[1].trim()),100);
                    }
              } else if (arglist[0].trim().toUpperCase().equalsIgnoreCase("DATE")) {
                    if (arglist.length == 1) {  
                         outputString = salientdate(inputFieldName,true);
                    } else {
                         outputString = salientdate(inputFieldName,false);
                    }     
              } else {
                    System.err.println("NOTE: Pad Space File has an unidentified 1st column parameter!");
                    System.err.println("      Column values must be either CHAR,NUMERIC or DATE only  ");
                    System.err.println("Field Value                         : "+inputFieldName);
                    System.err.println("Parameter passed from Pad Space File: "+padStrField);
                    System.err.println("");
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
