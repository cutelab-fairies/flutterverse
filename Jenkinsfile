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
        sh 'mvn jar:jar install:install help:evaluate -Dexpression=project.name'
      }
    }
  }
}