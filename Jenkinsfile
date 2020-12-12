pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
          sh 'bash ./scripts/inst.sh --fast'
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
