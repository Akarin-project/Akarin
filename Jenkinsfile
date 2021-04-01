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
	    sh 'chmod +x scripts/inst.sh'
        sh './scripts/inst.sh --setup'
      }
    }

    stage('Archive') {
      steps {
        archiveArtifacts(artifacts: '*.jar', fingerprint: true)
      }
    }

  }
}
