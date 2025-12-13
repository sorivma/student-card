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
    - name: kaniko
      image: gcr.io/kaniko-project/executor:latest
      tty: true
      volumeMounts:
        - name: dockerhub
          mountPath: /kaniko/.docker
    - name: kubectl
      image: bitnami/kubectl:latest
      command: ['cat']
      tty: true
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
    NS = "sorivma"
    APP = "dubrovsky-arseny"
    IMAGE = "sorivma/dubrovsky-arseny"
  }

  options { timestamps() }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
        sh 'pwd; ls -la; ls -la k8s || true'
      }
    }

    stage('Build & Push image (Kaniko)') {
      steps {
        container('kaniko') {
          sh '''
            set -euo pipefail
            echo "WORKSPACE=$WORKSPACE"
            ls -la "$WORKSPACE"
            test -f "$WORKSPACE/Dockerfile"

            /kaniko/executor \
              --dockerfile="$WORKSPACE/Dockerfile" \
              --context="$WORKSPACE" \
              --destination=''' + "${IMAGE}:${GIT_COMMIT}" + ''' \
              --destination=''' + "${IMAGE}:latest" + '''
          '''
        }
      }
    }

    stage('Deploy to Kubernetes') {
      steps {
        container('kubectl') {
          sh '''
            set -euo pipefail
            kubectl apply -f "$WORKSPACE/k8s/"
            kubectl -n ''' + "${NS}" + ''' set image deploy/''' + "${APP}" + ''' ''' + "${APP}" + '''=''' + "${IMAGE}:${GIT_COMMIT}" + '''
            kubectl -n ''' + "${NS}" + ''' rollout status deploy/''' + "${APP}" + ''' --timeout=180s
          '''
        }
      }
    }
  }
}
