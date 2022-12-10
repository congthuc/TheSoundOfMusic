call mvn clean package -Dmaven.test.skip

call echo D |  xcopy /E target\dependency-jars  .\dependency-jars
call xcopy target\TheSoundOfMusic-1.0-SNAPSHOT.jar  .