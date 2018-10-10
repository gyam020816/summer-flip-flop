properties([[$class: 'GitLabConnectionProperty', gitLabConnection: 'gitlab']])
pipeline {
    agent {
        label 'docker'
    }
    options {
        skipDefaultCheckout true
    }
    stages {
        stage('Clean workspace') {
            steps {
                deleteDir()
            }
        }
        stage('Build') {
            agent {
                docker {
                    image 'maven:3.5.4-jdk-8-alpine'
                    args '-v $HOME/dotm2:/tmp/maven_local_repo'
                    reuseNode true
                }
            }
            steps {
                checkout scm
                sh 'ls'
                sh '''
                    mvn -Dmaven.repo.local=/tmp/maven_local_repo clean package
                    mvn -Dmaven.repo.local=/tmp/maven_local_repo jacoco:report-aggregate
                '''
            }
        }
    }
    post {
        always {
            junit '**/target/surefire-reports/*.xml'
            publishHTML target: [
                allowMissing: true,
                alwaysLinkToLastBuild: false,
                keepAll: true,
                reportDir: 'test-jacoco-reporter/target/site/jacoco-aggregate',
                reportFiles: 'index.html',
                reportName: 'Coverage Report',
                includes: '*'
              ]
            step([$class: 'Mailer', notifyEveryUnstableBuild: true, recipients: 'automation@ha3.eu'])
        }
        success {
            updateGitlabCommitStatus name: 'jenkins', state: 'success'
        }
        failure {
            updateGitlabCommitStatus name: 'jenkins', state: 'failed'
        }
        unstable {
            updateGitlabCommitStatus name: 'jenkins', state: 'failed'
        }
    }
}
