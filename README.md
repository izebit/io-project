### talks service
the service has rest api. It is written in java, data is stored in mongodb. Also, liquibase is used to data migration.

### how to run it
run:  
```shell
docker-compose up
```
and check [http://localhost:8080/api/v1/talks?author=Mona](http://localhost:8080/api/v1/talks?author=Mona)

### how to build it
run:
```shell
./gradlew build
```

### api documentation

[https://localhost:8080/swagger-ui.html](https://localhost:8080/swagger-ui.html)