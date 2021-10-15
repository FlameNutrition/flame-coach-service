<img src="./flameCoach.png?raw=true" 
data-canonical-src="./logo.png?raw=true"/>
# flame-coach-service
Service API for Flame Coach Application

[![Flame Coach - Service](https://circleci.com/gh/FlameNutrition/flame-coach-service.svg?style=svg)](https://circleci.com/github/FlameNutrition/flame-coach-service)

## Description 
This is the backend source of Flame Coach. Here you will find the API code to create clients, appointments, etc...
basically, all the core logic to put the application works. Code uses MySQL database to store the information 
but feel free to apply the necessary changes to support other databases.

Flame Coach backend uses Spring as the core framework and also all the code is written in Kotlin.

## Compile 🏗️
It's easy to compile and use the Flame Coach Service. The project is using maven to manage dependencies, 
this way, you only need to run the following command (please visit https://maven.apache.org/run.html):
```
e.g: ./mvn compile
```

If you want to run a full clean installation, running the test suite, you must run:
```
e.g: ./mvn clean install
```

## Run ▶️
To run the backend you will need to run the spring profile `local`. Please use the following command, inside launch folder, to run the application:
```
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

## Release & Deploy 🚀
Please use the following project [flame-coach-tools] to create a new release. 
If you need access to the project, please open an issue to request access.
```
Release:
e.g: ./flame-coach-tools --release --releaseVersion <version> --snapshotVersion <version-snapshot> --web/api

Deploy:
e.g: ./flame-coach-tools --api/web --deploy --version <version>
```

## Database versioning
This project use [flywaydb] to version the database, if you need to migrate the database 
to a new version you can use the following command line:
```
mvn -Dflyway.url=jdbc:mysql://<hostname>:3306/flame-coach -Dflyway.user=<username> -Dflyway.password=<password> flyway:migrate
```

## Contributing ✍️
Pull requests are welcome. Please check the [CONTRIBUTING.md](https://github.com/FlameNutrition/flame-coach-service/blob/master/CONTRIBUTING.md) to find the best way to contribute.

## Authors and acknowledgment
I'm waiting for you 🤟

## License
This opensource project is under the following license: [MIT]


[flywaydb]: https://flywaydb.org/documentation/usage/maven/
[flame-coach-tools]: https://github.com/FlameNutrition/flame-coach-tools
[mit]: https://choosealicense.com/licenses/mit/
