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
      image: bitnami/kubectl:latest
      command: ['cat']
      tty: true
"""
    }
  }

  environment {
    NS = "sorivma"
    APP = "dubrovsky-arseny"
    IMAGE = "sorivma/dubrovsky-arseny"
    // важно: repo публичный, kaniko сможет взять контекст из git
    GIT_URL = "github.com/sorivma/dubrovsky-arseny.git"
  }

  options { timestamps() }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
        sh 'echo "GIT_COMMIT=$GIT_COMMIT"'
      }
    }

    stage('Build & Push (Kaniko pod)') {
      steps {
        container('kubectl') {
          sh """
            set -euo pipefail

            cat > /tmp/kaniko-pod.yaml <<EOF
apiVersion: v1
kind: Pod
metadata:
  name: kaniko-build
  namespace: ${NS}
spec:
  restartPolicy: Never
  containers:
  - name: kaniko
    image: gcr.io/kaniko-project/executor:latest
    args:
      - --dockerfile=Dockerfile
      - --context=git://${GIT_URL}#${GIT_COMMIT}
      - --destination=${IMAGE}:${GIT_COMMIT}
      - --destination=${IMAGE}:latest
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
EOF

            kubectl -n ${NS} delete pod kaniko-build --ignore-not-found=true
            kubectl apply -f /tmp/kaniko-pod.yaml

            kubectl -n ${NS} logs -f pod/kaniko-build -c kaniko
            kubectl -n ${NS} wait --for=condition=Succeeded pod/kaniko-build --timeout=30m
          """
        }
      }
    }

    stage('Deploy to Kubernetes') {
      steps {
        container('kubectl') {
          sh """
            set -euo pipefail
            kubectl apply -f k8s/
            kubectl -n ${NS} set image deploy/${APP} ${APP}=${IMAGE}:${GIT_COMMIT}
            kubectl -n ${NS} rollout status deploy/${APP} --timeout=180s
          """
        }
      }
    }
  }
}
