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
        - name: workspace-volume
          mountPath: /home/jenkins/agent
        - name: dockerhub
          mountPath: /kaniko/.docker
    - name: kubectl
      image: bitnami/kubectl:latest
      command: ['cat']
      tty: true
      volumeMounts:
        - name: workspace-volume
          mountPath: /home/jenkins/agent
  volumes:
    - name: workspace-volume
      emptyDir: {}
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

  options {
    timestamps()
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
        sh 'ls -la'
      }
    }

    stage('Build & Push image (Kaniko)') {
      steps {
        container('kaniko') {
          sh """
            /kaniko/executor \
              --dockerfile=Dockerfile \
              --context=/home/jenkins/agent/workspace/${JOB_NAME} \
              --destination=${IMAGE}:${GIT_COMMIT} \
              --destination=${IMAGE}:latest
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
            kubectl -n ${NS} get pods -o wide
          """
        }
      }
    }
  }

  post {
    always {
      container('kubectl') {
        sh """
          echo "==== K8S status (namespace ${NS}) ===="
          kubectl -n ${NS} get deploy,po,svc,ingress || true
        """
      }
    }
  }
}
