<img src="./logo.png?raw=true" 
data-canonical-src="./logo.png?raw=true" width="150" height="150" />
# flame-coach-service 
Service API for Flame Nutrition Coach

## Release
The project use the git tags to mark the code to be released. When you need to do a release/deploy, first you should create a git tag at master using the script `update_project_version.go`. This will also update the maven version tag.

```
cd launch/deploy && go run update_project_version.go -releaseVersion <version> -snapshotVersion <version-snapshot>
```

## Deploy
The following commands allow you to deploy the application. You can deploy or redeploy the service using these options: `--deploy` or `--redeploy`.
Also, you can run the `--dryRun` option to verify what commands the script is going to use.

* Deploy service: `cd launch/deploy && go run deploy.go --version <version> --deploy`
* Redeploy service: `cd launch/deploy && go run deploy.go --version <version> --redeploy`
* Dryrun script: `cd launch/deploy && go run deploy.go --version <version> --redeploy --dryRun`
