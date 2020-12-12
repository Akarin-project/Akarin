pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
          sh 'bash ./scripts/inst.sh --setup --fast --remote'
       // sh 'chmod +x scripts/build.sh'
     //   sh 'sh ./scripts/build.sh'
      }
    }

    stage('Archive') {
      steps {
        archiveArtifacts(artifacts: '*.jar', fingerprint: true)
      }
    }

  }
}
