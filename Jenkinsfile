pipeline {
    agent any
    
    environment {
        NCLOUD_ACCESS_KEY_ID = credentials('NCLOUD_ACCESS_KEY_ID')
        NCLOUD_SECRET_KEY = credentials('NCLOUD_SECRET_KEY')
    }

    stages {
        stage('Test') {
            steps {
                script {
                    dir('be') {                
                       echo 'Test'
                        sh './gradlew clean test'
                    }
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    dir('be') {
                        echo 'build'
                        sh 'docker build -t jshop.kr.ncr.ntruss.com/jshop:v1.0.0 .'
                    }                    
                }
            }
        }

        stage('NCR_Push') {
            steps {
                script {
                    echo 'push'
                    sh 'echo $NCLOUD_SECRET_KEY | docker login jshop.kr.ncr.ntruss.com -u ${NCLOUD_ACCESS_KEY_ID} --password-stdin'
                    sh 'docker push jshop.kr.ncr.ntruss.com/jshop:v1.0.0'
                }
            }
        }
    }
}
