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
        sh './scripts/inst.sh --setup --fast --remote
'
      }
    }

    stage('Archive') {
      steps {
        archiveArtifacts(artifacts: '*.jar', fingerprint: true)
      }
    }

  }
}
