pipeline {
  agent any
  
  environment {
    DOCKER_REGISTRY = 'pfa-recrutement'
  }

  stages {
    stage('Build Backend (Maven)') {
      steps {
        dir('backend') {
          // Construit tous les modules maven
          sh 'mvn clean package -DskipTests'
        }
      }
    }
    
    stage('Tests Unitaires') {
      steps {
        dir('backend') {
          // Lance les tests maven
          sh 'mvn test'
        }
      }
    }

    stage('Build Frontend (NPM)') {
      steps {
        dir('frontend') {
          sh 'npm install'
          sh 'npm run build --prod'
        }
      }
    }
    
    stage('Build Docker Images') {
      steps {
        // Build de toutes les images via docker-compose
        sh 'docker compose build'
      }
    }
    
    stage('Deploy to DEV') {
      steps {
        sh 'docker compose up -d'
      }
    }
  }
  
  post {
    always {
      // Archive les rapports de tests JUnit
      junit 'backend/**/target/surefire-reports/*.xml'
      // Nettoyer l'environnement si nécessaire
      sh 'docker system prune -f'
    }
  }
}
