package main

import (
	"flag"
	"fmt"
	"log"
	"os/exec"
)

func main() {

	releaseVersion := flag.String("releaseVersion", "", "Release version")
	snapshotVersion := flag.String("snapshotVersion", "", "New snapshot version")

	flag.Parse()

	if *releaseVersion == "" {
		log.Fatal("Missing -releaseVersion flag")
	}

	if *snapshotVersion == "" {
		log.Fatal("Missing -snapshotVersion flag")
	}

	cmdReleaseVersion := exec.Command("mvn", "versions:set", fmt.Sprintf("-DnewVersion=%s", *releaseVersion), "-f", "../../pom.xml")
	log.Printf("Command: %v", cmdReleaseVersion.String())

	/*out,err := cmdReleaseVersion.Output()
	if err != nil {
		log.Fatalf("Command release version finished with error: %v", err)
	}
	log.Printf("Output: %s", out)
	*/

	gitCommitRelease := exec.Command("git", "commit", "--all", "-m", fmt.Sprintf("\"AUTO-RELEASE: %s\"", *releaseVersion))
	log.Printf("Command: %v", gitCommitRelease.String())

	gitTagRelease := exec.Command("git", "tag", "-a", fmt.Sprintf("v%s", *releaseVersion), "-m", fmt.Sprintf("\"Flame Coach Service version: %s\"", *releaseVersion))
	log.Printf("Command: %v", gitTagRelease.String())

	cmdSnapshotVersion := exec.Command("mvn", "versions:set", fmt.Sprintf("-DnewVersion=%s-SNAPSHOT", *snapshotVersion), "-f", "../../pom.xml")
	log.Printf("Command: %v", cmdSnapshotVersion.String())

	gitCommitSnapshot := exec.Command("git", "commit", "--all", "-m", fmt.Sprintf("\"AUTO-SNAPSHOT: %s\"", *snapshotVersion))
	log.Printf("Command: %v", gitCommitSnapshot.String())

}
