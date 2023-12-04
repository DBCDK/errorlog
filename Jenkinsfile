#!groovy

def workerNode = "devel11"

pipeline {
    agent { label workerNode }

    tools {
        jdk 'jdk11'
        maven "Maven 3"
    }

    triggers {
        upstream(upstreamProjects: "Docker-payara6-bump-trigger",
            threshold: hudson.model.Result.SUCCESS)
        pollSCM("H/03 * * * *")
    }

    options {
        timestamps()
    }

    environment {
        MAVEN_OPTS="-Dmaven.repo.local=.repo -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn"
    }

    stages {
        stage("Clean Workspace") {
            steps {
                deleteDir()
                checkout scm
            }
        }

        stage("Verify") {
            steps {
                sh "mvn verify pmd:pmd"
            }
        }

        stage("Publish PMD Results") {
            steps {
                def pmd = scanForIssues tool: [$class: 'Pmd']
                publishIssues issues: [pmd], unstableTotalAll: 1
            }
        }

        stage("Docker build") {
            when {
                branch "main"
            }
            steps {
                sh "mvn install -Dskip.push=false -Dtag=DIT-${BUILD_NUMBER}"
            }
        }
    }

}
