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
        archiveArtifacts(artifacts: 'target/*.jar', fingerprint: true)
      }
    }

    stage('Report') {
      steps {
        discordSend 'https://discordapp.com/api/webhooks/695027453122445433/rijjbFyNPhgvTmgXHCCdMExGwR3vHBveH4PjYi0ScsP9d7rSdGbKMhId36WAypZjUj5h'
      }
    }

  }
}