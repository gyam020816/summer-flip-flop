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
            when {
                expression {
                    return !(branch_name ==~ /^.*-illustration$/)
                }
            }
            agent {
                docker {
                    image 'duskforest.xyz:5000/buildenv-kotlin-maven-docker'
                    // https://www.testcontainers.org/usage/inside_docker.html
                    // also, 999 is the current group ID of the docker socket group owner
                    args '-v $HOME/dotm2:/tmp/maven_local_repo -v $PWD:$PWD -w $PWD -v /var/run/docker.sock:/var/run/docker.sock --group-add 999'
                    reuseNode true
                }
            }
            steps {
                checkout scm
                sh 'ls'
                sh 'docker --version'
                sh '''
                    mvn --global-toolchains=/maven-data/toolchains.xml -Dmaven.repo.local=/tmp/maven_local_repo -P fast-build clean package
                    mvn --global-toolchains=/maven-data/toolchains.xml -Dmaven.repo.local=/tmp/maven_local_repo -P fast-build jacoco:report-aggregate
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
