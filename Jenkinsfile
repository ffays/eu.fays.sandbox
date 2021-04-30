// [Pipeline: Basic Steps](https://jenkins.io/doc/pipeline/steps/workflow-basic-steps/)
// [Pipeline Syntax](https://jenkins.io/doc/book/pipeline/syntax/)
// [Pipeline Steps Reference](https://jenkins.io/doc/pipeline/steps/)


node {
	def mvnHome = tool 'M3'
	def jdkHome = tool 'JDK11'
	def fileSeparator = isUnix()?"/":"\\" // System.getProperty('file.separator')
	def scmUrl = scm.getUserRemoteConfigs()[0].getUrl()
	def projectName = scmUrl.substring(scmUrl.lastIndexOf('/')+1, scmUrl.lastIndexOf('.'))
	def jenkinsProjectName = (env.JOB_NAME.tokenize('/') as String[])[0]
	def projectBuildOs = jenkinsProjectName.substring(jenkinsProjectName.lastIndexOf('-')+1) // one of: linux,macosx,win32
	def mvnExe  = "${mvnHome}${fileSeparator}bin${fileSeparator}mvn"
	def mvnOpts = "-f ${projectName}${fileSeparator}pom.xml"
	def mvnGoals = 'clean verify'
	
	// credentialsId: it is the MD5 fingerprint of the ssh key, e.g. ssh-keygen -E md5 -l -f ~/.ssh/id_rsa.pub
	def credentialsId
	if(isUnix()) {
		credentialsId = sh(returnStdout: true, script: 'ssh-keygen -l -E md5 -f $HOME/.ssh/id_rsa.pub | sed "s/^.*MD5://;s/ .*$//;s/://g"').trim()
	} else {
		credentialsId = powershell(returnStdout: true, script: '''
$pub = "$env:USERPROFILE/.ssh/id_rsa.pub"
$b64 = (Get-Content -Path $pub -Raw) -replace "^[^ ]* ","" -replace " .*$",""
$md5 = new-object -TypeName System.Security.Cryptography.MD5CryptoServiceProvider
$bd  = [System.Convert]::FromBase64String($b64);
[System.BitConverter]::ToString($md5.ComputeHash($bd)).Replace("-","").ToLower()
		''').trim()
	}
	
	def osName
	if(isUnix()) {
		osName = sh(returnStdout: true, script: 'uname').trim() // "Darwin" => MacOS, "Linux" => "Linux"
	} else {
		osName = 'Windows'
		def username = powershell(returnStdout: true, script: 'whoami').trim()
		echo "username=$username"
	}

	echo "jdkHome=${jdkHome}"
	echo "osName=${osName}"
	echo "scmUrl=${scmUrl}"
	echo "projectName=${projectName}"
	echo "jenkinsProjectName=${jenkinsProjectName}"
	echo "projectBuildOs=${projectBuildOs}"
	
	echo sh(script: 'env|sort', returnStdout: true)

	env.JAVA_HOME = jdkHome
	env.PROJECT_NAME = projectName

	stage('Checkout') {
		def scmVars
		deleteDir()
		dir(env.PROJECT_NAME) {
			scmVars  = checkout scm
		}
	}

	stage('Build') {
		if("linux".equals(projectBuildOs)) {
		    mvnOpts = '-Dproject.build.os=linux -Dproject.build.ws=gtk ' + mvnOpts
		} else if("macosx".equals(projectBuildOs)) {
			mvnOpts = '-Dproject.build.os=macosx -Dproject.build.ws=cocoa ' + mvnOpts
		} else if("win32".equals(projectBuildOs)) {
			mvnOpts = '-Dproject.build.os=win32 -Dproject.build.ws=win32 ' + mvnOpts
		}

		if("Linux".equals(osName)) {
			if(!"linux".equals(projectBuildOs)) {
				mvnOpts = '-DskipTests ' + mvnOpts  
			}
			wrap([$class: 'Xvfb', displayName: 1, screen: '1920x1080x24']) {
				withEnv(['DISPLAY=:1']) {
					sh "'${mvnExe}' ${mvnOpts} ${mvnGoals}"
				}
			}
		} else if("Darwin".equals(osName)) {
			if(!"macosx".equals(projectBuildOs)) {
				mvnOpts = '-DskipTests ' + mvnOpts  
			}
			env.MAVEN_OPTS = '-XstartOnFirstThread'
			mvnOpts = '-Dproject.build.os=macosx -Dproject.build.ws=cocoa ' + mvnOpts
			sh "'${mvnExe}' ${mvnOpts} ${mvnGoals}"
		} else if("Windows".equals(osName)) {
			if(!"win32".equals(projectBuildOs)) {
				mvnOpts = '-DskipTests ' + mvnOpts  
			}

			def userHome = bat(returnStdout: true, script: '@echo %USERPROFILE%').trim()
			echo 'USERPROFILE=' + '"' + userHome + '"'

			// To avoid whitespace issues in the workspace folder name, a junction (i.e. a symbolic link) has been created on the root folder level:
			//   mklink /j C:\workspace "C:\Program Files (x86)\Jenkins\workspace"
			def workspaceFolder = /\workspace/ + pwd().substring(pwd().lastIndexOf(fileSeparator))

			// Maven's global settings may be tweaked in order to avoid to have yet another local repository in the system profile
			// c.f. "<localRepository>C:\Users\devops\.m2\repository</localRepository>" in "settings.xml"
			// otherwise the local maven's repository would have been there:
			//   C:\Windows\System32\config\systemprofile\.m2\repository

			// Maven build
			mvnOpts = mvnOpts + / --global-settings C:\Users\devops\.m2\settings.xml/
			bat(/cd ${workspaceFolder} & mvn ${mvnOpts} ${mvnGoals}/)
		}
		step([
			$class: 'ArtifactArchiver',
			artifacts: "${projectName}/target/*.jar",
			fingerprint: false
		])
		// step([
		// 	$class: 'JUnitResultArchiver',
		// 	testResults: '**/target/surefire-reports/TEST-*.xml'
		// ])
	}
}
