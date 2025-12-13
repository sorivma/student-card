pipeline {
  agent any

  environment {
    NS    = "sorivma"
    APP   = "dubrovsky-arseny"
    IMAGE = "sorivma/dubrovsky-arseny"
  }

  options {
    timestamps()
    disableConcurrentBuilds()
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
        sh 'echo "GIT_COMMIT=$GIT_COMMIT"'
      }
    }

    stage('Build & Push (Docker on runner)') {
      options { timeout(time: 30, unit: 'MINUTES') }
      steps {
        withCredentials([usernamePassword(
          credentialsId: 'sorivma-dockerhub-creds',
          usernameVariable: 'DOCKERHUB_USER',
          passwordVariable: 'DOCKERHUB_PASS'
        )]) {
          sh '''
            set -eu

            echo "$DOCKERHUB_PASS" | docker login -u "$DOCKERHUB_USER" --password-stdin

            docker build \
              -t "${IMAGE}:${GIT_COMMIT}" \
              -t "${IMAGE}:latest" \
              .

            docker push "${IMAGE}:${GIT_COMMIT}"
            docker push "${IMAGE}:latest"

            docker logout || true
          '''
        }
      }
    }

    stage('Deploy to Kubernetes') {
      options { timeout(time: 10, unit: 'MINUTES') }
      steps {
        // kubeconfig в Jenkins Credentials как "Secret file"
        withCredentials([file(credentialsId: 'kubeconfig-sorivma', variable: 'KUBECONFIG')]) {
          sh '''
            set -eu

            kubectl -n "${NS}" apply -f k8s/
            kubectl -n "${NS}" set image deploy/"${APP}" "${APP}"="${IMAGE}:${GIT_COMMIT}"
            kubectl -n "${NS}" rollout status deploy/"${APP}" --timeout=180s
          '''
        }
      }
    }
  }

  post {
    always {
      // чуть-чуть чистим, чтобы не забить диск на раннере
      sh 'docker image prune -f || true'
    }
  }
}
