pipeline {
  agent {
    docker {
      image '3.6.0-jdk-8-alpine'
    }

  }
  stages {
    stage('') {
      steps {
        sh 'mvn -B -DskipTests clean package'
      }
    }
  }
}