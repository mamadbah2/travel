// ================================================================
//  Pipeline CI — Travel Management System
//  Build backend (7 services) + frontend + analyse SonarQube
// ================================================================

pipeline {
    agent any

    environment {
        WORKSPACE_TRAVEL = '/workspace/travel'
        SONAR_HOST_URL   = 'http://sonarqube:9000'
    }

    stages {

        // ── Stage 1 : Checkout ──────────────────────────────────
        stage('Checkout') {
            steps {
                sh "rm -rf ${WORKSPACE}/* ${WORKSPACE}/.* 2>/dev/null || true && cp -r ${WORKSPACE_TRAVEL}/. ${WORKSPACE}"
            }
        }

        // ── Stage 2 : Charger Token SonarQube ─────────────────────
        stage('Charger Token SonarQube') {
            steps {
                script {
                    env.SONAR_TOKEN = sh(script: 'cat /opt/sonarqube/data/jenkins-token.txt', returnStdout: true).trim()
                }
            }
        }

        // ── Stage 3 : Build Backend ─────────────────────────────
        stage('Build Backend') {
            steps {
                script {
                    def services = [
                        'api-gateway',
                        'auth-service',
                        'travel-service',
                        'payment-service',
                        'notification-service',
                        'search-service',
                        'rec-service'
                    ]
                    for (svc in services) {
                        dir("backend/${svc}") {
                            sh "chmod +x mvnw"
                            sh "./mvnw clean compile -DskipTests -q"
                        }
                    }
                }
            }
        }

        // ── Stage 4 : Build Frontend ────────────────────────────
        stage('Build Frontend') {
            steps {
                dir('frontend') {
                    sh 'npm ci --prefer-offline'
                    sh 'npx ng build --configuration=production'
                }
            }
        }

        // ── Stage 5 : SonarQube Backend ─────────────────────────
        stage('SonarQube Backend') {
            steps {
                script {
                    def services = [
                        'api-gateway',
                        'auth-service',
                        'travel-service',
                        'payment-service',
                        'notification-service',
                        'search-service',
                        'rec-service'
                    ]
                    for (svc in services) {
                        dir("backend/${svc}") {
                            sh """
                                ./mvnw org.sonarsource.scanner.maven:sonar-maven-plugin:4.0.0.4121:sonar \
                                    -Dsonar.host.url=${SONAR_HOST_URL} \
                                    -Dsonar.token=${env.SONAR_TOKEN} \
                                    -Dsonar.projectKey=travel-${svc} \
                                    -Dsonar.projectName="Travel - ${svc}" \
                                    -Dsonar.qualitygate.wait=false \
                                    -q
                            """
                        }
                    }
                }
            }
        }

        // ── Stage 6 : SonarQube Frontend ────────────────────────
        stage('SonarQube Frontend') {
            steps {
                dir('frontend') {
                    sh """
                        sonar-scanner \
                            -Dsonar.host.url=${SONAR_HOST_URL} \
                            -Dsonar.token=${env.SONAR_TOKEN} \
                            -Dsonar.projectKey=travel-frontend \
                            -Dsonar.projectName="Travel - Frontend" \
                            -Dsonar.sources=src \
                            -Dsonar.exclusions=**/node_modules/**,**/dist/** \
                            -Dsonar.qualitygate.wait=false
                    """
                }
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline CI terminé avec succès !'
        }
        failure {
            echo '❌ Pipeline CI échoué — vérifier les logs.'
        }
    }
}
