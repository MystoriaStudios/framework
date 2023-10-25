pipeline {
    agent any

    stages {
        stage('Build & Publish') {
            steps {
                script {
                    if (env.BRANCH_NAME == 'master') {
                        echo "Building master branch"
                        sh './gradlew build publishMavenJavaPublicationToMystoriaProdRepository'
                    } else {
                        echo "Building feature or other branch: ${env.BRANCH_NAME}"
                        sh './gradlew build publishMavenJavaPublicationToMystoriaDevRepository'
                    }
                }
            }
        }
    }
}
