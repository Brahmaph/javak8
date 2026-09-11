pipeline {

    agent any

    environment {
        DOCKER_IMAGE = "brahma999/javak8"
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Checking out source code from GitHub...'
                checkout scm
            }
        }

        stage('Build Java Application') {
            steps {
                echo 'Building Java application...'
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Docker Build') {
            steps {
                echo 'Building Docker image...'
                bat 'docker build -t %DOCKER_IMAGE%:%IMAGE_TAG% .'
            }
        }

        stage('Docker Login and Push') {
            steps {

                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-creds',
                        usernameVariable: 'DOCKER_USERNAME',
                        passwordVariable: 'DOCKER_PASSWORD'
                    )
                ]) {

                    echo 'Logging in to Docker Hub...'

                    bat 'echo %DOCKER_PASSWORD% | docker login -u %DOCKER_USERNAME% --password-stdin'

                    echo 'Pushing Docker image to Docker Hub...'

                    bat 'docker push %DOCKER_IMAGE%:%IMAGE_TAG%'

                    echo 'Logging out from Docker Hub...'

                    bat 'docker logout'
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {

                echo 'Deploying application to Kubernetes...'

                bat 'kubectl set image deployment/javak8 javak8=%DOCKER_IMAGE%:%IMAGE_TAG%'

                bat 'kubectl rollout status deployment/javak8'
            }
        }

        stage('Verify Kubernetes') {
            steps {

                echo 'Checking Kubernetes deployment...'

                bat 'kubectl get pods'

                bat 'kubectl get deployment javak8'

                bat 'kubectl get service java-k8s-service'
            }
        }
    }

    post {

        success {
            echo '========================================='
            echo 'CI/CD PIPELINE SUCCESSFUL!'
            echo "Docker Image: ${DOCKER_IMAGE}:${IMAGE_TAG}"
            echo "Kubernetes Deployment: javak8"
            echo '========================================='
        }

        failure {
            echo '========================================='
            echo 'CI/CD PIPELINE FAILED!'
            echo 'Check the Jenkins Console Output.'
            echo '========================================='
        }
    }
}
