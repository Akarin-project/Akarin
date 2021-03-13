//@Library('forge-shared-library') _
pipeline {
  agent any
  environment {
        DISCORD_WEBHOOK_URL = credentials('3e8105ad-8e03-4550-bc66-a27438ec6fb3')
        CHANGES = getChanges(currentBuild)
        ARTIFACT = "http://ci.josephworks.net/job/Akarin/job/1.15.2/${currentBuild.id}/artifact/target/akarin-1.15.2-launcher.jar"
  }
  stages {
    stage('Initialize') {
      steps {
        sh '''git submodule update --init --recursive'''
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
        discordSend(
          description: "**Build:** [${currentBuild.id}](${env.BUILD_URL})\n**Status:** [${currentBuild.currentResult}](${env.BUILD_URL})\n\n**Changes:**\n```$CHANGES```\n**Artifacts:**\n - $ARTIFACT",
          footer: "JosephWorks Jenkins Server", 
          link: env.BUILD_URL, 
          result: currentBuild.currentResult, 
          title: "${env.JOB_NAME} #${currentBuild.id}",
          webhookURL: "$DISCORD_WEBHOOK_URL"
          )
      }
    }

  }
  post {
    always {
      cleanWs()
    }
  }
}
