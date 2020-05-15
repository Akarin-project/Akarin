pipeline {
  agent any
  stages {
    stage('Initialize') {
      steps {
        sh '''git fetch origin
git reset --hard origin/1.15.2
git submodule update --init --recursive'''
      }
    }

    stage('Build') {
      steps {
        sh './akarin jar'
      }
    }

    stage('Archive') {
      steps {
        archiveArtifacts(artifacts: '*.jar', fingerprint: true)
      }
    }

  }
}