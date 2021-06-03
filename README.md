<img src="./logo.png?raw=true" 
data-canonical-src="./logo.png?raw=true" width="150" height="150" />
# flame-coach-service 
Service API for Flame Nutrition Coach

## Release
Please using the following project [flame-coach-tools]
```
e.g: ./flame-coach-tools --release --releaseVersion <version> --snapshotVersion <version-snapshot> --web/api
```

## Deploy
Please using the following project [flame-coach-tools]
```
e.g: ./flame-coach-tools --api/web --deploy --version <version>
```

## Database
This project use [flywaydb] to version the database, if you need to migrate the
production database to a new version you can use the following command line:
```
mvn -Dflyway.url=jdbc:mysql://18.168.135.251:3306/flame-coach -Dflyway.user= -Dflyway.password= flyway:migrate
```

[flywaydb]: https://flywaydb.org/documentation/usage/maven/
[flame-coach-tools]: https://github.com/FlameNutrition/flame-coach-tools
