pipeline {
  agent any
  stages {
    stage('Init Submodules') {
      steps {
        sh 'git submodule update --init --recursive'
      }
    }
    
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
