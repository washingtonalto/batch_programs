SET javacompiler=C:\Program Files\Java\jdk1.6.0_01\bin\javac
SET javasourcefiles=JDBCSQLSelect.java
SET classpath=.;Drivers\Oracle10gR2Oracle\ojdbc14_g.jar;Drivers\Oracle10gR2Oracle\orai18n.jar

"%javacompiler%" "%javasourcefiles%" 

PAUSE