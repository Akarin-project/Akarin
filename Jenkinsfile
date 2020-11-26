pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh 'chmod +x scripts/inst.sh'
        sh 'sh ./scripts/inst.sh --setup --remote'
      }
    }

    stage('Archive') {
      steps {
        archiveArtifacts(artifacts: '*.jar', fingerprint: true)
      }
    }

  }
}
