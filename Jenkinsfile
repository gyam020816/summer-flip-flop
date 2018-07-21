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
                    image 'mzagar/jenkins-slave-jdk-maven-git'
                    reuseNode true
                }
            }
            steps {
                checkout scm
                sh 'ls'
                sh '''
                    mkdir -p maven_local_repo
                    mvn -Dmaven.repo.local=./maven_local_repo clean package
                    mvn -Dmaven.repo.local=./maven_local_repo jacoco:report-aggregate
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
                reportName: 'Coverage Report'
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
