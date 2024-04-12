# JDBCSQLSelect 
This is the Generic JDBC SQL tool for dumping SQL Select statements to fixed-width text and delimited text files. I created this many years ago and I'm giving this back to the open-source community as I no longer am capable of actively maintaining it. It might still be useful for some batch-oriented SQL tasks running in Windows but one just need to download the latest JDBC driver for the database like PostGres or MySQL.

The program streams the output of the SQL statement immediately instead of storing it in memory before dumping it to output and hence, it is able to handle large data output. 

## Code
The Code folder contains the batch programs and the Java program as well as all associated Java classes and JDBC drivers needed to run the program
## Source
The Source folder contains all the source codes that I used to create the simple Java program