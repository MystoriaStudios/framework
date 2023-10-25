pipeline {
    agent any

    stages {
        stage('Setup') {
            steps {
                def gradlePropertiesPath = "/path/to/your/gradle.properties"
                sh "gradle build -Dgradle.user.home=${gradlePropertiesPath}"
            }
        }

        stage('Build & Publish') {
            steps {
                script {
                    sh 'chmod +x gradlew'

                    if (env.BRANCH_NAME == 'master') {
                        echo "Building master branch"
                        sh './gradlew build publishMavenJavaPublicationToMystoriaProdRepository --stacktrace'
                    } else {
                        echo "Building feature or other branch: ${env.BRANCH_NAME}"
                        sh './gradlew build publishMavenJavaPublicationToMystoriaDevRepository --stacktrace'
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
