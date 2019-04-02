pipeline {
  agent {
    docker {
      image 'maven:3.6.0-jdk-8-alpine'
    }

  }
  stages {
    stage('Build') {
      steps {
        sh 'mvn -B -DskipTests clean package'
      }
    }
    stage('Deliver') {
      steps {
        archiveArtifacts 'target/flutterverse-1.0-SNAPSHOT.jar'
      }
    }
  }
}