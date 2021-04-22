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
	log.Printf("1 Command: %v", cmdReleaseVersion.String())

	gitCommitRelease := exec.Command("git", "commit", "--all", "-m", fmt.Sprintf("AUTO-RELEASE: %s", *releaseVersion))
	log.Printf("2 Command: %v", gitCommitRelease.String())

	gitTagRelease := exec.Command("git", "tag", "-a", fmt.Sprintf("v%s", *releaseVersion), "-m", fmt.Sprintf("Flame Coach Service version: %s", *releaseVersion))
	log.Printf("3 Command: %v", gitTagRelease.String())

	cmdSnapshotVersion := exec.Command("mvn", "versions:set", fmt.Sprintf("-DnewVersion=%s-SNAPSHOT", *snapshotVersion), "-f", "../../pom.xml")
	log.Printf("4 Command: %v", cmdSnapshotVersion.String())

	gitCommitSnapshot := exec.Command("git", "commit", "--all", "-m", fmt.Sprintf("AUTO-SNAPSHOT: %s", *snapshotVersion))
	log.Printf("5 Command: %v", gitCommitSnapshot.String())

	executeCmd(cmdReleaseVersion, "cmdReleaseVersion")
	executeCmd(gitCommitRelease, "gitCommitRelease")
	executeCmd(gitTagRelease, "gitTagRelease")
	executeCmd(cmdSnapshotVersion, "cmdSnapshotVersion")
	executeCmd(gitCommitSnapshot, "gitCommitSnapshot")

	log.Printf("IMPORTANT: Please push the code manually! This is temporary")
	//gitPush := exec.Command("git", "push", "origin", "--tags")
	//executeCmd(gitPush, "gitPush")

}

func executeCmd(cmd *exec.Cmd, commandTag string) {

	// 1 Command
	out, err := cmd.Output()
	if err != nil {
		log.Fatalf("Command %s version finished with error: %v", commandTag, err)
	}
	log.Printf("Output: %s", out)

}
