pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh 'bash ./scripts/build.sh'
      }
    }

    stage('Archive') {
      steps {
        archiveArtifacts(artifacts: '*.jar', fingerprint: true)
      }
    }

  }
}
