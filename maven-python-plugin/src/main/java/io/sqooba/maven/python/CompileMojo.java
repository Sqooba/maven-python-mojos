package io.sqooba.maven.python;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Run mypy (optional static typing) on source directory
 *
 * See http://mypy-lang.org/
 *
 * @goal mypy
 * @phase compile
 */
public class CompileMojo extends AbstractMojo {

  private static final String COMPILE_COMMAND = "mypy";
  private static final String PYTHON_FLAG = "-m";

  /**
   * @parameter default-value="${project.basedir}/src/main/python/"
   * @required
   */
  private String sourceDirectory;

  /**
   * @parameter default-value="python"
   * @required
   */
  private String pythonExecutable;

  /**
   * @parameter default-value="${project}"
   * @required
   * @readonly
   */
  private MavenProject project;

  /**
   * @parameter default-value="mypy"
   * @required
   * @readonly
   */
  private String pythonMypyExecutable;

  /**
   * @parameter default-value="--ignore-missing-imports"
   * @required
   */
  private String extraParams;

  /* (non-Javadoc)
   * @see org.apache.maven.plugin.AbstractMojo#execute()
   */
  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info("Executing " + pythonMypyExecutable);

    final String command = String.format("'%s %s %s %s %s'",
            pythonExecutable,
            PYTHON_FLAG,
            COMPILE_COMMAND,
            sourceDirectory,
            extraParams
    );

    final File sourceDirectoryFile = new File(sourceDirectory);

    //Verify input of ProcessBuilder to prevent command injection
    if (!Utils.verifyPython(pythonExecutable)) {
      throw new MojoExecutionException(String.format("%s is not a valid python executable",
              pythonExecutable));
    }
    if (!Utils.verifyPath(sourceDirectory)) {
      throw new MojoExecutionException(String.format("%s is not a valid directory",
              sourceDirectory));
    }

    try {

      getLog().info(String.format("Running %s", command));
      List<String> args = new ArrayList<String>();
      args.add(pythonExecutable);
      args.add(PYTHON_FLAG);
      args.add(COMPILE_COMMAND);
      args.add(sourceDirectory);
      for (String arg : extraParams.split(" ")) {
        args.add(arg);
      }
      ProcessBuilder processBuilder = new ProcessBuilder(args);
      processBuilder.directory(sourceDirectoryFile);
      processBuilder.redirectErrorStream(true);
      Process pr = processBuilder.start();
      int exitCode = pr.waitFor();
      BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
      String line;
      while ((line = buf.readLine()) != null) {
        getLog().info(line);
      }

      // See https://github.com/python/mypy/issues/2754
      switch (exitCode) {
        case 1:
          throw new MojoFailureException("Static code validation failed");

        case 2:
          throw new MojoExecutionException("Command line syntax errors, missing files, or blocking errors");

        default:
          getLog().info("All mypy checks passed successfully");
      }
    } catch (IOException | InterruptedException e) {
      throw new MojoExecutionException(String.format("Unable to execute %s",
              command), e);
    }
  }
}