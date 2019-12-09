package com.amatkovskiy.ci

import groovy.transform.Field
import groovy.json.JsonOutput
import groovy.json.JsonSlurperClassic

def git_pull_singl_branch (git_repository, git_branch, credentialsId, directory=''){
  withCredentials([sshUserPrivateKey(credentialsId: credentialsId, keyFileVariable: 'SSH_KEY', passphraseVariable: 'SSH_PASS', usernameVariable: 'SSH_USER')]) {
    try {
      echo "git_branch = " + git_branch
      sh """
      set +x
        eval `ssh-agent -a ~/.ssh-agent.sock`
        ( openssl rsa -passin env:SSH_PASS -in ${SSH_KEY} | ssh-add -  ) || ( ssh-agent -k && exit 1)
        git clone --single-branch --branch ${git_branch} ${git_repository} ${directory}
        ssh-agent -k
      """
    }
    catch (Exception e) {
      def command = 'ps ax | grep "/var/lib/jenkins/.ssh-agent.soc[k]" | awk \'{print $1}\'  | xargs -n1 -I{} bash -c "SSH_AGENT_PID={} ssh-agent -a  ~/.ssh-agent.sock -k"'
      echo command
      sh(returnStdout: true, script: command).trim()
      error 'Failed to pull repository'
    }
  }
}


def git_pull (git_repository, credentialsId, directory=''){
  withCredentials([sshUserPrivateKey(credentialsId: credentialsId, keyFileVariable: 'SSH_KEY', passphraseVariable: 'SSH_PASS', usernameVariable: 'SSH_USER')]) {
    try {
      echo "git_branch = " + git_branch
      sh """
      set +x
        eval `ssh-agent -a ~/.ssh-agent.sock`
        ( openssl rsa -passin env:SSH_PASS -in ${SSH_KEY} | ssh-add -  ) || ( ssh-agent -k && exit 1)
        git clone ${git_repository} ${directory}
        ssh-agent -k
      """
    }
    catch (Exception e) {
      def command = 'ps ax | grep "/var/lib/jenkins/.ssh-agent.soc[k]" | awk \'{print $1}\'  | xargs -n1 -I{} bash -c "SSH_AGENT_PID={} ssh-agent -a  ~/.ssh-agent.sock -k"'
      echo command
      sh(returnStdout: true, script: command).trim()
      error 'Failed to pull repository'
    }
  }
}


def git_merge (git_repository, src_branch, dst_branch, commit_message, credentialsId, directory=''){
  withCredentials([sshUserPrivateKey(credentialsId: credentialsId, keyFileVariable: 'SSH_KEY', passphraseVariable: 'SSH_PASS', usernameVariable: 'SSH_USER')]) {
    try {
      echo "src_branch = " + src_branch
      echo "dst_branch = " + dst_branch
      
      sh """
      set =x
        eval `ssh-agent -a ~/.ssh-agent.sock`
        ( openssl rsa -passin env:SSH_PASS -in ${SSH_KEY} | ssh-add -  ) || ( ssh-agent -k && exit 1)
        git clone ${git_repository} ${directory}
        cd ${directory}
        git checkout ${src_branch}
        git checkout ${dst_branch}
        git merge ${src_branch} -m "${commit_message}"
        git push --set-upstream origin ${dst_branch}
        ssh-agent -k
      """
    }
    catch (Exception e) {
      def command = 'ps ax | grep "/var/lib/jenkins/.ssh-agent.soc[k]" | awk \'{print $1}\'  | xargs -n1 -I{} bash -c "SSH_AGENT_PID={} ssh-agent -a  ~/.ssh-agent.sock -k"'
      echo command
      sh(returnStdout: true, script: command).trim()
      error 'Failed to pull repository'
    }
  }
}