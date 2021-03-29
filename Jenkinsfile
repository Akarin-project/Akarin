pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh 'bash ./scripts/inst.sh --setup'
      }
    }

    stage('Archive') {
      steps {
        archiveArtifacts(artifacts: '*.jar', fingerprint: true)
      }
    }

  }
}
