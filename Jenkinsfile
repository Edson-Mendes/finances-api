pipeline {
    agent any
    stages {
        stage('unit test') {
            steps {
                sh 'mvn clean test -P unit-tests'
            }
        }
        stage('integration test') {
            steps {
                sh 'mvn clean test -P integration-tests'
            }
        }
        stage('package') {
            steps {
                sh 'mvn -B -DskipTests clean package'
            }
        }
        stage('build image') {
            environment {
                IMAGE_VERSION = sh (returnStdout: true, script: "grep '^version=' ./target/maven-archiver/pom.properties | cut -d '=' -f 2")
            }
            steps {
                sh 'docker build -t edsonmendes/finances-api:${IMAGE_VERSION} -t edsonmendes/finances-api .'
            }
        }
        stage('login dockerhub') {
            environment {
                DOCKERHUB_CREDENTIALS = credentials('edsonmendes-dockerhub')
            }
            steps {
                sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
            }
        }
        stage('push image') {
            environment {
                IMAGE_VERSION = sh (returnStdout: true, script: "grep '^version=' ./target/maven-archiver/pom.properties | cut -d '=' -f 2")
            }
            steps {
                sh 'docker push edsonmendes/finances-api:${IMAGE_VERSION}'
                sh 'docker push edsonmendes/finances-api:latest'
            }
            post {
                always {
                    sh 'docker logout'
                }
            }
        }
    }
}