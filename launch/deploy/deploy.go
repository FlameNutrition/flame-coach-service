package main

import (
	"encoding/json"
	"flag"
	"fmt"
	"log"
	"os/exec"
)

func main() {

	application := "FlameCoachAPI"
	awsS3Bucket := "flamecoach-api"

	version := flag.String("version", "", "Application version")
	deploy := flag.Bool("deploy", false, "Deploy application")
	redeploy := flag.Bool("redeploy", false, "Redeploy application")
	dryRun := flag.Bool("dryRun", false, "Simulating zip and deploy application")

	flag.Parse()

	if *version != "" {

		applicationPackage := application + "v" + *version + ".zip"

		if *deploy {

			copyCmd(*dryRun)
			//pushS3Cmd(application, awsS3Bucket, applicationPackage, *dryRun)
			//deployCmd(application, awsS3Bucket, applicationPackage, *dryRun)

		}

		if *redeploy {

			deployCmd(application, awsS3Bucket, applicationPackage, *dryRun)

		}
	} else {
		log.Fatal("Please define a software version to deploy")
	}
}

func pushS3Cmd(applicationName string, s3BucketName string, applicationPackage string, dryRun bool) {
	s3Cmd := exec.Command("aws", "deploy", "push",
		"--application-name", applicationName,
		"--s3-location", fmt.Sprintf("s3://%s/%s", s3BucketName, applicationPackage),
		"--source", "../target/deploy")
	if dryRun {
		log.Printf("s3 Cmd: %v", s3Cmd.String())
	} else {
		out, err := s3Cmd.Output()
		if err != nil {
			log.Fatalf("Failed when try to push to S3, %v", err)
		}
		log.Printf("%s", out)
	}
}

func deployCmd(applicationName string, s3BucketName string, applicationPackage string, dryRun bool) {
	deployCmd := exec.Command("aws", "deploy", "create-deployment",
		"--application-name", applicationName,
		"--deployment-group-name", "production",
		"--s3-location", fmt.Sprintf("bucket=%s,key=%s,bundleType=zip", s3BucketName, applicationPackage))
	if dryRun {
		log.Printf("deploy create-deployment Cmd: %v", deployCmd.String())
	} else {
		out, err := deployCmd.Output()
		if err != nil {
			log.Fatalf("Failed when try to push to create-deployment, %v", err)
		}

		var jsonResult map[string]interface{}
		err = json.Unmarshal(out, &jsonResult)
		if err != nil {
			log.Fatalf("deploymentId json parsing failed, %v", err)
		}

		deploymentId := jsonResult["deploymentId"].(string)

		log.Printf("deploymentId: %s", deploymentId)

		_ = exec.Command("aws", "deploy", "wait", "deployment-successful",
			"--deployment-id", deploymentId).Run()
	}
}

func copyCmd(dryRun bool) {

	rm := exec.Command("rm", "-R", "../target/deploy")
	mkdir := exec.Command("mkdir", "../target/deploy")
	mkdirScripts := exec.Command("mkdir", "../target/deploy/scripts")
	cp1 := exec.Command("cp", "../target/flame-coach.jar", "../target/deploy/flame-coach.jar")
	cp2 := exec.Command("cp", "../../appspec.yml", "../target/deploy/appspec.yml")
	cp3 := exec.Command("cp", "../scripts/start-application.sh", "../target/deploy/scripts/start-application.sh")
	cp4 := exec.Command("cp", "../scripts/stop-application.sh", "../target/deploy/scripts/stop-application.sh")
	cp5 := exec.Command("cp", "../../application/src/main/resources/log4j2-production.xml", "../target/deploy/log4j2.xml")

	if dryRun {
		log.Printf("rm Cmd: %v", rm.String())
		log.Printf("mkdir Cmd: %v", mkdir.String())
		log.Printf("mkdirScripts Cmd: %v", mkdirScripts.String())
		log.Printf("cp1 Cmd: %v", cp1.String())
		log.Printf("cp2 Cmd: %v", cp2.String())
		log.Printf("cp3 Cmd: %v", cp3.String())
		log.Printf("cp4 Cmd: %v", cp4.String())
		log.Printf("cp5 Cmd: %v", cp5.String())
	} else {
		_ = rm.Run()
		_ = mkdir.Run()
		_ = mkdirScripts.Run()
		_ = cp1.Run()
		_ = cp2.Run()
		_ = cp3.Run()
		_ = cp4.Run()
		_ = cp5.Run()
	}
}
