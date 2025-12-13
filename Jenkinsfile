pipeline {
  agent {
    kubernetes {
      defaultContainer 'kubectl'
      yaml """
apiVersion: v1
kind: Pod
spec:
  serviceAccountName: jenkins
  containers:
    - name: kubectl
      image: bitnami/kubectl:1.30
      command: ['sh', '-c', 'cat']
      tty: true

    - name: kaniko
      image: gcr.io/kaniko-project/executor:v1.23.2
      command: ['sh', '-c', 'cat']
      tty: true
      env:
        - name: DOCKER_CONFIG
          value: /kaniko/.docker
      volumeMounts:
        - name: dockerhub
          mountPath: /kaniko/.docker

  volumes:
    - name: dockerhub
      secret:
        secretName: dockerhub
        items:
          - key: .dockerconfigjson
            path: config.json
"""
    }
  }

  environment {
    NS    = "sorivma"
    APP   = "dubrovsky-arseny"
    IMAGE = "sorivma/dubrovsky-arseny"
  }

  options { timestamps() }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
        sh 'echo "GIT_COMMIT=$GIT_COMMIT"'
      }
    }

    stage('Build & Push (Kaniko)') {
      steps {
        container('kaniko') {
          sh '''
            set -eu

            /kaniko/executor \
              --dockerfile=Dockerfile \
              --context="$WORKSPACE" \
              --destination="${IMAGE}:${GIT_COMMIT}" \
              --destination="${IMAGE}:latest" \
              --verbosity=info
          '''
        }
      }
    }

    stage('Deploy to Kubernetes') {
      steps {
        container('kubectl') {
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
}
