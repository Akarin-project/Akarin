@Library('forge-shared-library') _
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
        discordSend(
                            title: "Finished ${currentBuild.currentResult}",
                            description: '```\n' + getChanges(currentBuild) + '\n```',
                            successful: currentBuild.resultIsBetterOrEqualTo("SUCCESS"),
                            result: currentBuild.currentResult,
                            thumbnail: "https://img.hexeption.co.uk/Magma_Block.png",
                            webhookURL: "$DISCORD_WEBHOOK_URL"
                    )
        discordSend(
          description: "Jenkins Pipeline Build", 
          footer: "Footer Text", 
          link: env.BUILD_URL, 
          result: currentBuild.currentResult, 
          title: JOB_NAME, 
          webhookURL: "$DISCORD_WEBHOOK_URL"
          )
      }
    }

  }
  //post {
  //      always {
  //              cleanWs()
  //          }
  //      }
  //  }
}
