pipeline {
    agent any
    stages {
        stage('Hello'){
            steps {
                echo 'Hello'
            }
        }
        stage('Cloning'){
            steps {
               git url: 'https://github.com/vaibhavdikha2206/swapiapp-spring'
            }
        }
        stage('Install'){
            steps {
               sh 'mvn install'
               sh 'docker build -t interconnected .'
            }
        }
        stage('Deploy'){
            steps {
                catchError {
                    sh 'docker stop interconnected'
                }
                echo currentBuild.result

                catchError {
                    sh 'docker rm interconnected'
                }
                sh 'docker run --name interconnected -p 8080:8080 interconnected'
            }
        }

    }
}