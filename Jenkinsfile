pipeline {
  agent any
  environment {
        DISCORD_WEBHOOK_URL = credentials('3e8105ad-8e03-4550-bc66-a27438ec6fb3')
  }
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
        discordSend \'$DISCORD_WEBHOOK_URL\'
      }
    }

  }
}
