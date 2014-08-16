git pull
mvn install
ps aux | grep -ie com.bitpieces.dev.web_service.WebService | awk '{print $2}' | xargs kill -9 
java -cp target/bitpieces_practice-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.bitpieces.dev.web_service.WebService
