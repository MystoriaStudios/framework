pipeline {
    agent any

    stages {
        stage('Build & Publish') {
            steps {
                script {
                    sh 'chmod +x gradlew'

                    def gradlePropertiesPath = "/var/lib/jenkins/gradle"

                    if (env.BRANCH_NAME == 'master') {
                        echo "Building master branch"
                        sh './gradlew build publishMavenJavaPublicationToMystoriaProdRepository --stacktrace -Dgradle.user.home=${gradlePropertiesPath}'
                    } else {
                        echo "Building feature or other branch: ${env.BRANCH_NAME}"
                        sh './gradlew build publishMavenJavaPublicationToMystoriaDevRepository --stacktrace -Dgradle.user.home=${gradlePropertiesPath}'
                    }
                }
            }
        }

        stage('Cleanup') {
            steps {
                sh 'rm gradle.properties'
            }
        }
    }
}
