pipeline {

    agent any

    stages {

        stage('Check Java') {
            steps {
                bat 'java -version'
            }
        }

        stage('Check Maven') {
            steps {
                bat 'mvn -version'
            }
        }

        stage('Check Docker') {
            steps {
                bat 'docker --version'
            }
        }

        stage('Check Kubernetes') {
            steps {
                bat 'kubectl version --client'
            }
        }

        stage('Check Kubernetes Cluster') {
            steps {
                bat 'kubectl get nodes'
            }
        }
    }
}