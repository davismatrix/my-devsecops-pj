
pipeline {
  agent { label 'build' }
   environment { 
        registry = "davismatrix/devsecops-pj" 
        registryCredential = 'dockerhub' 
   }

  stages {
    stage('Checkout') {
      steps {
        git branch: 'main', credentialsId: 'github', url: 'https://github.com/davismatrix/my-devsecops-pj.git'
      }
    }
  
   stage('Stage I: Build') {
      steps {
        echo "Building Jar Component ..."
        sh "export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64; mvn clean package "
      }
    }

   stage('Stage II: Code Coverage ') {
      steps {
	    echo "Running Code Coverage ..."
        sh "export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64; mvn jacoco:report"
      }
    }

   stage('Stage III: SCA') {
      steps { 
        echo "Running Software Composition Analysis using OWASP Dependency-Check ..."
        sh "export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64; mvn org.owasp:dependency-check-maven:check"
      }
    }

   stage('Stage IV: SAST') {
      steps { 
        echo "Running Static application security testing using SonarQube Scanner ..."
        withSonarQubeEnv('sonarqube-server') {
            sh 'mvn sonar:sonar -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml -Dsonar.dependencyCheck.jsonReportPath=target/dependency-check-report.json -Dsonar.dependencyCheck.htmlReportPath=target/dependency-check-report.html -Dsonar.projectName=devsecops-pj'
       }
      }
    }

   stage('Stage V: QualityGates') {
      steps { 
        echo "Running Quality Gates to verify the code quality"
        script {
          timeout(time: 10, unit: 'MINUTES') {
            def qg = waitForQualityGate()
            if (qg.status != 'OK') {
              error "Pipeline aborted due to quality gate failure: ${qg.status}"
            }
           }
        }
      }
    }
   
   stage('Stage VI: Build Image') {
      steps { 
        echo "Build Docker Image"
        script {
               docker.withRegistry( '', registryCredential ) { 
                 myImage = docker.build registry + ":$BUILD_NUMBER"
                 myImage.push()
                }
        }
      }
    }
        
   stage('Stage VII: Scan Image ') {
      steps { 
        echo "Scanning Image for Vulnerabilities"
        sh "trivy image --scanners vuln --offline-scan davismatrix/devsecops-pj:$BUILD_NUMBER > trivyresults.txt"
        }
    }
          
   stage('Stage VIII: Smoke Test ') {
      steps { 
        echo "Smoke Test the Image"
        sh "docker run -d --name smokerun -p 8081:8080 davismatrix/devsecops-pj:$BUILD_NUMBER"
        sh "sleep 90; ./check.sh"
        sh "docker rm --force smokerun"
        }
    }

    stage('Stage IX: Deploy to K8s') {
        steps { 
          script {
            TAG = "$BUILD_NUMBER"
            echo "Deploying to K8s"
              build wait: false, job: 'devsecops-pj-cd-pipeline', parameters: [string(name: 'IMAGETAG', value: "${TAG}")]
          }
        }
      }
  }
}