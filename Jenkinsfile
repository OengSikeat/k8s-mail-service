pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "gotoh6951/my-app"
        DOCKER_TAG = "${BUILD_NUMBER}"
        MANIFEST_REPO = "https://github.com/OengSikeat/ArgoCD-Manifest.git"
        MANIFEST_DIR = "manifest/spring"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew clean build -x test --no-daemon'
            }
        }

        stage('Docker Build & Push') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                    sh """
                        docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .
                        docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest
                        docker push ${DOCKER_IMAGE}:${DOCKER_TAG}
                        docker push ${DOCKER_IMAGE}:latest
                    """
                }
            }
        }

        stage('Update Manifest') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'github-credentials',
                    usernameVariable: 'GIT_USER',
                    passwordVariable: 'GIT_PASS'
                )]) {
                    sh """
                        rm -rf ArgoCD-Manifest
                        git clone https://${GIT_USER}:${GIT_PASS}@github.com/OengSikeat/ArgoCD-Manifest.git
                        cd ArgoCD-Manifest/${MANIFEST_DIR}
                        sed -i 's|image: ${DOCKER_IMAGE}:.*|image: ${DOCKER_IMAGE}:${DOCKER_TAG}|g' deployment.yml
                        git config user.email "jenkins@ci.com"
                        git config user.name "Jenkins"
                        git add deployment.yml
                        git commit -m "ci: update image tag to ${DOCKER_TAG}"
                        git push
                    """
                }
            }
        }
    }

    post {
        success {
            echo "Build ${DOCKER_TAG} completed successfully"
        }
        failure {
            echo "Pipeline failed - check console output"
        }
    }
}