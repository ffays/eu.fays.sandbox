// Jenkins Multibranch Pipeline item name: sb-win32-win32-x86_64
// Max folder name length with branch name: 32
// [Pipeline: Basic Steps](https://jenkins.io/doc/pipeline/steps/workflow-basic-steps/)
// [Pipeline Syntax](https://jenkins.io/doc/book/pipeline/syntax/)
// [Pipeline Steps Reference](https://jenkins.io/doc/pipeline/steps/)
// [Trigger hourly build from scripted Jenkinsfile](https://stackoverflow.com/questions/44113834/trigger-hourly-build-from-scripted-jenkinsfile)
// ["Build Periodically" with a Multi-branch Pipeline in Jenkins](https://stackoverflow.com/questions/39168861/build-periodically-with-a-multi-branch-pipeline-in-jenkins/39172513#39172513)

node {
	// offline : false if either a Maven plugin version has been modified or the Eclipse RCP target platform has been modified
	def offline = false
	def performClone = true

	def linux = 'linux', macosx = 'macosx', win32 = 'win32' // supported OSes
	def gtk = 'gtk', cocoa = 'cocoa' /* ,win32 = 'win32' */ // supported Window Systems
	def x86_64 = 'x86_64', aarch64 = 'aarch64'                 // supported CPU architectures
	def mvnHome = tool 'M3'
	def jdkHome = tool 'JDK21'
	def fileSeparator = isUnix()?"/":"\\" // System.getProperty('file.separator')
	def scmUrl = scm.getUserRemoteConfigs()[0].getUrl()
	def projectName = 'eu.fays.sandbox'
	def jenkinsProjectName = (env.JOB_NAME.tokenize('/') as String[])[0]
	def uname = isUnix()?sh(returnStdout: true, script: 'uname').trim().toLowerCase():win32
	def hostOs = isUnix()?uname.replace("darwin", macosx):win32
	def hostWs = isUnix()?uname.replace("darwin", cocoa).replace("linux", gtk):win32
	def hostArch = isUnix()?sh(returnStdout: true, script: 'uname -m').trim().replace("arm64", aarch64):x86_64	
	def projectBuildOs   = jenkinsProjectName.split('-')[1] // one of: linux,macosx,win32
	def projectBuildWs   = jenkinsProjectName.split('-')[2] // one of: gtk,cocoa,win32
	def projectBuildArch = jenkinsProjectName.split('-')[3] // one of: x86_64,aarch64
	def loggingFormat = '%1$tF %1$tT	%4$s	%3$s	%5$s%6$s%n'
	def q = isUnix()?"'":'"'
	def mvnExe  = "${mvnHome}${fileSeparator}bin${fileSeparator}mvn"
	def mvnOpts = /-f ${projectName}${fileSeparator}pom.xml -Dproject.build.os=${projectBuildOs} -Dproject.build.ws=${projectBuildWs} -Djava.util.logging.SimpleFormatter.format=$q$loggingFormat$q/
	if(offline) mvnOpts += ' --offline'
	if(!projectBuildOs.equals(hostOs)) mvnOpts += ' -DskipTests'
	def mvnGoals = 'clean verify'

	def multiArchList = [
		win32 +'/'+win32+'/'+x86_64,
		macosx+'/'+cocoa+'/'+aarch64,
		macosx+'/'+cocoa+'/'+x86_64,
		linux +'/'+gtk  +'/'+aarch64,
		linux +'/'+gtk  +'/'+x86_64
	]
	def currentArch = projectBuildOs+'/'+projectBuildWs+'/'+projectBuildArch
	def displayName = 42 // Xvfb display number 
	
/*
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
*/

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
	echo "hostWs=${hostWs}"
	echo "hostArch=${hostArch}"
	echo "projectBuildOs=${projectBuildOs}"
	echo "projectBuildWs=${projectBuildWs}"
	echo "projectBuildArch=${projectBuildArch}"
	echo "mvnOpts=${mvnOpts}"
	echo "scmUrl=${scmUrl}"
	echo "mvnOpts=${mvnOpts}"
	echo "displayName=${displayName}"

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
	stage('Clone') {
		// mkdir ~/git-bare
		// cd ~/git-bare
		// git clone --mirror git@github.com:ffays/eu.fays.sandbox.git
		// git --git-dir=$(getent passwd $USER | cut -d: -f6)/git-bare/eu.fays.sandbox.git remote update	

		if(performClone) {
			if(scmUrl.charAt(0) == '/') {
				sh "git --git-dir=$HOME/git-bare/${projectName}.git remote update"
			}
			deleteDir()
			dir(env.PROJECT_NAME) {
				scmVars = checkout scm
				echo "scmVars"
				print scmVars
			}
		} else {
			echo 'Skipped git checkout'
		}
	}

	stage('Build') {
		try {
			if(linux.equals(hostOs)) {
				if(linux.equals(projectBuildOs)) {
					wrap([$class: 'Xvfb', displayName: displayName, screen: '1920x1080x24']) {
						withEnv(['DISPLAY=:'+displayName]) {
							echo "'${mvnExe}' ${mvnOpts} ${mvnGoals}"
							sh "'${mvnExe}' ${mvnOpts} ${mvnGoals}"
						}
					}
				} else {
					sh "'${mvnExe}' ${mvnOpts} ${mvnGoals}"
				}
			} else if(macosx.equals(hostOs)) {
				env.MAVEN_OPTS = '-XstartOnFirstThread'
				mvnOpts = '-Dproject.build.os=macosx -Dproject.build.ws=cocoa ' + mvnOpts
				sh "'${mvnExe}' ${mvnOpts} ${mvnGoals}"
			} else if(win32.equals(hostOs)) {
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
			echo "Maven build finished"
		} catch(e) {
			echo "Maven build failed"
			sh "'${mvnExe}' -f ${projectName}${fileSeparator}pom.xml surefire-report:report"
			// [Send an email on Jenkins pipeline failure](https://stackoverflow.com/questions/39720225/send-an-email-on-jenkins-pipeline-failure)
			// [Google account : Less secure app access : Allow less secure apps](https://myaccount.google.com/lesssecureapps)
			// swaks --to support@example.com --server smtp.gmail.com:587 -tls -a LOGIN					
			currentBuild.result = 'FAILURE'

//			emailext subject: '$DEFAULT_SUBJECT',
//				body: '$DEFAULT_CONTENT',
//				recipientProviders: [
//					[$class: 'CulpritsRecipientProvider'],
//					[$class: 'DevelopersRecipientProvider'],
//					[$class: 'RequesterRecipientProvider']
//				],
//				replyTo: '$DEFAULT_REPLYTO',
//				to: '$DEFAULT_RECIPIENTS',
//				attachmentsPattern: '**/surefire-report.html'

			throw e
		} finally  {
			echo "Build finalizer"
		}
		echo "Build end"
	}
	
	stage('Archiver') {
		echo "Archiver begin"
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
		echo "Archiver end"
	}

	stage('Email') {
		echo "Not sending an e-mail notification."
//		emailext subject: '$DEFAULT_SUBJECT',
//			body: '$DEFAULT_CONTENT',
//			recipientProviders: [
//				[$class: 'RequesterRecipientProvider']
//			],
//			replyTo: '$DEFAULT_REPLYTO',
//			to: '$DEFAULT_RECIPIENTS'
	}
}

