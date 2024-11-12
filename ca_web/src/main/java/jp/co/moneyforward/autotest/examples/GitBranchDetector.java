package jp.co.moneyforward.autotest.examples;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;

public class GitBranchDetector {
 
  public static void main(String[] args) {
    try {
      FileRepositoryBuilder builder = new FileRepositoryBuilder();
      Repository repository = builder
          .setGitDir(new File(".git"))
          .readEnvironment()
          .findGitDir()
          .build();
      
      try (Git git = new Git(repository)) {
        String branch = repository.getBranch();
        System.out.println("Current branch: " + branch);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}