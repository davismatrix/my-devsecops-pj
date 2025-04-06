pipeline{
    agent {
        label 'build'
    }
    environment {
        GITHUB_TOKEN = credentials('github-token')
    }
    parameters {
        //password (name: 'PASSWD', defaultValue: '', description: 'Please enter your password')
        string (name: 'IMAGETAG', defaultValue: '1', description: 'Please enter the Image Tag to Deploy?')
    }
    stages {
        stage("Deploy") {
            steps{
                git branch: 'main', credentialsId: 'github', url: 'https://github.com/davismatrix/my-devsecops-pj.git'
                dir('./cd/k8s') {
                    sh "sed -i 's/image: davismatrix.*/image: davismatrix\\/devsecops-pj:${IMAGETAG}/g' deployment.yml"
                }
                sh 'git config user.name "Jenkins CI"'
                sh 'git config user.email "davismatrix@gmail.com"'
                sh 'git commit -a -m "New deployment for Build ${IMAGETAG}"'
                sh 'git push https://davismatrix:${GITHUB_TOKEN}@github.com/davismatrix/my-devsecops-pj.git'
            }
            
        }
    }
}