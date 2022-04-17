pipeline {
        agent any
        environment {
            def shortCommit = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()
            def author = sh(returnStdout: true, script: 'git show -s --pretty=%an').trim()
            def url = sh(returnStdout: true, script: 'git config remote.origin.url').trim()
            def iacRepo = "https://github.com/meissabcmbaye/devsecops-iac"
            def microserviceURL = "http://192.168.49.2:30434"
        }
        stages {
            stage('Checkout SCM') {
                steps {
                    checkout scm
                }
            }

            stage('Check Java') {
                steps {
                    sh 'java -version'
                }
            }

            // Sensitive Information Scanning with Trivy
            stage('Iac Sensitive Information Scanning') {
                steps {
                    sh """ docker run --rm aquasec/trivy repo --no-progress --exit-code 1 --severity HIGH,CRITICAL ${iacRepo} """
                }
            }

            stage('Clean') {
                steps {
                    sh 'chmod +x mvnw'
                    sh './mvnw -ntp clean -P-webpack'
                }
            }

            stage('Checkstyle') {
                steps {
                    sh './mvnw -ntp checkstyle:check'
                }
            }

            // Software Composition/Component Analysis with OWASP Dependency-Check 
            stage('OWASP Dependency-Check Vulnerabilities') {
                steps {
                    sh """ ./mvnw dependency-check:check """
                    dependencyCheckPublisher pattern: 'target/dependency-check-report.xml'
                }
            }

            stage('SonarQube Analysis') {
                steps {
                    withSonarQubeEnv('devsecops-sonarqube') {
                        sh './mvnw clean install'
                        sh './mvnw initialize sonar:sonar -Dsonar.dependencyCheck.jsonReportPath=target/dependency-check-report.json -Dsonar.dependencyCheck.xmlReportPath=target/dependency-check-report.xml -Dsonar.dependencyCheck.htmlReportPath=target/dependency-check-report.html -Dsonar.coverage.jacoco.xmlReportPaths=target/jacoco/test/jacoco.xml,target/jacoco/integrationTest/jacoco.xml,target/site/jacoco-aggregate-all/jacoco.xml\n'
                    }
                }
            }

            stage('Quality Gate') {
                steps {
                    timeout(time: 10, unit: 'MINUTES') {
                        waitForQualityGate abortPipeline: true
                    }
                }
            }

            stage('Build Docker Image') {
                steps {
                    sh """ ./mvnw package verify jib:dockerBuild """
                }
            }

            // Image Vulnerability Scan with Trivy
            stage('Image Vulnerability Scan') {
                steps {
                    sh """ docker run --rm -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy image --no-progress product:latest """
                }
            }

            stage('Tag and Publish Docker image') {
                steps {
                    sh """
                            docker tag product:latest product:dev-${shortCommit}
                            docker tag product:latest product:staging-${shortCommit}
                            echo "Tag and push dev and staging docker images."
                           """
                }
            }

            // DAST with OWASP ZAP
            stage('Dynamic Application Analysis') {
                steps {
                    sh """ docker run -t owasp/zap2docker-stable zap-baseline.py -x -J -t ${microserviceURL} || true """
                }
            }

            stage('Clean Env') {
                steps {
                    sh """
                            docker rmi product:latest
                            docker rmi product:dev-${shortCommit}
                            docker rmi product:staging-${shortCommit}
                            echo "Environment cleaned."

                           """
                }
            }

            stage('GitOps Dev') {
                when {
                    branch 'dev'
                }
                steps {
                    sh """ echo "Deploy to dev environment" """
                }
            }

            stage('GitOps staging ') {
                when {
                    branch 'staging'
                }
                steps {
                    sh """ echo "Deploy to staging environment" """
                }
            }

            stage('GitOps master ') {
                when {
                    branch 'master'
                }
                steps {
                    sh """ echo "Deploy to dev and staging environment" """
                }
            }

            stage('Notifications') {
                steps {
                    sh """ echo "Send notifications for steps status." """
                }
            }
        }
}
