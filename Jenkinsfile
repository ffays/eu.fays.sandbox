// [Pipeline: Basic Steps](https://jenkins.io/doc/pipeline/steps/workflow-basic-steps/)
// [Pipeline Syntax](https://jenkins.io/doc/book/pipeline/syntax/)
// [Pipeline Steps Reference](https://jenkins.io/doc/pipeline/steps/)
// [Trigger hourly build from scripted Jenkinsfile](https://stackoverflow.com/questions/44113834/trigger-hourly-build-from-scripted-jenkinsfile)
// ["Build Periodically" with a Multi-branch Pipeline in Jenkins](https://stackoverflow.com/questions/39168861/build-periodically-with-a-multi-branch-pipeline-in-jenkins/39172513#39172513)

node {
	properties(
		[
			pipelineTriggers([
				[
					$class: 'TimerTrigger',
					spec: '@midnight'
				]
			 ])
		]
	)

	def linux = 'linux', macosx = 'macosx', win32 = 'win32' // supported OSes
	//def hostOs = System.getProperty('os.name').replace(' ','').toLowerCase().replaceAll('win\\p{Alnum}*',win32)
	def hostOs = isUnix()?sh(returnStdout: true, script: 'uname').trim().toLowerCase().replace("darwin", macosx):win32
	def mvnHome = tool 'M3'
	def jdkHome = tool 'JDK17'
	def fileSeparator = isUnix()?"/":"\\" // System.getProperty('file.separator')
	def scmUrl = scm.getUserRemoteConfigs()[0].getUrl()
	def projectName = scmUrl.substring(scmUrl.lastIndexOf('/')+1, scmUrl.lastIndexOf('.'))
	def jenkinsProjectName = (env.JOB_NAME.tokenize('/') as String[])[0]
	def projectBuildOs = jenkinsProjectName.substring(jenkinsProjectName.lastIndexOf('-')+1) // one of: linux,macosx,win32
	def mvnExe  = "${mvnHome}${fileSeparator}bin${fileSeparator}mvn"
	// --offline : Work offline - remove this option if a Maven plugin version has been modified
	def mvnOpts = "--offline -f ${projectName}${fileSeparator}pom.xml"
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

	echo "projectName=${projectName}"
	echo "jenkinsProjectName=${jenkinsProjectName}"
	echo "hostOs=${hostOs}"
	echo "projectBuildOs=${projectBuildOs}"
	echo "jdkHome=${jdkHome}"
	echo "scmUrl=${scmUrl}"

	env.JAVA_HOME = jdkHome
	env.PROJECT_NAME = projectName

/*
	if(linux.equals(projectBuildOs)) {
		triggers { pollSCM('00 03 * * 1-5') }
	} else if(macosx.equals(projectBuildOs)) {
		triggers { pollSCM('00 02 * * 1-5') }
	} else if(win32.equals(projectBuildOs)) {
		triggers { pollSCM('00 01 * * 1-5') }
	}
*/

	def scmVars // Map keys: GIT_BRANCH, GIT_COMMIT, GIT_PREVIOUS_COMMIT, GIT_PREVIOUS_SUCCESSFUL_COMMIT, GIT_URL	
	stage('Checkout') {
		deleteDir()
		dir(env.PROJECT_NAME) {
			scmVars  = checkout scm
			echo "scmVars"
			print scmVars
		}
	}

	stage('Build') {
		if(linux.equals(projectBuildOs)) {
			mvnOpts = '-Dproject.build.os=linux -Dproject.build.ws=gtk ' + mvnOpts
		} else if(macosx.equals(projectBuildOs)) {
			mvnOpts = '-Dproject.build.os=macosx -Dproject.build.ws=cocoa ' + mvnOpts
		} else if(win32.equals(projectBuildOs)) {
			mvnOpts = '-Dproject.build.os=win32 -Dproject.build.ws=win32 ' + mvnOpts
		}

		if(linux.equals(hostOs)) {
			if(linux.equals(projectBuildOs)) {
				wrap([$class: 'Xvfb', displayName: 9, screen: '1920x1080x24']) {
					withEnv(['DISPLAY=:9']) {
						try {
							echo "'${mvnExe}' ${mvnOpts} ${mvnGoals}"
							sh "'${mvnExe}' ${mvnOpts} ${mvnGoals}"
						} catch(e) {
							// [Send an email on Jenkins pipeline failure](https://stackoverflow.com/questions/39720225/send-an-email-on-jenkins-pipeline-failure)
							currentBuild.result = 'FAILURE'
							def currentResult = currentBuild.currentResult
							echo "catch: projectName=${projectName}"
							echo "catch: currentResult=${currentResult}"
							emailext subject: '$DEFAULT_SUBJECT',
								body: '$DEFAULT_CONTENT',
								recipientProviders: [
									[$class: 'CulpritsRecipientProvider'],
									[$class: 'DevelopersRecipientProvider'],
									[$class: 'RequesterRecipientProvider']
								],
								replyTo: '$DEFAULT_REPLYTO',
								to: '$DEFAULT_RECIPIENTS'
							throw e
						} finally  {
							echo "Maven build finished"
						}
					}
				}
			} else {
				mvnOpts = '-DskipTests ' + mvnOpts
				sh "'${mvnExe}' ${mvnOpts} ${mvnGoals}"
			}
		} else if(macosx.equals(hostOs)) {
			if(!macosx.equals(projectBuildOs)) {
				mvnOpts = '-DskipTests ' + mvnOpts
			}
			env.MAVEN_OPTS = '-XstartOnFirstThread'
			mvnOpts = '-Dproject.build.os=macosx -Dproject.build.ws=cocoa ' + mvnOpts
			sh "'${mvnExe}' ${mvnOpts} ${mvnGoals}"
		} else if(win32.equals(hostOs)) {
			if(!win32.equals(projectBuildOs)) {
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
			// mvnOpts = mvnOpts + / --global-settings C:\Users\devops\.m2\settings.xml/
			bat(/cd ${workspaceFolder} & mvn ${mvnOpts} ${mvnGoals}/)
		}
		def commitHash = null
		if(scmVars != null) {
			commitHash = scmVars.GIT_COMMIT
		} else {
			dir(projectName) {
			commitHash = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
			}
		}
		echo "commitHash=${commitHash}"

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
