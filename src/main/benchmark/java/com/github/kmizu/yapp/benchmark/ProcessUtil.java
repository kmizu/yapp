/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package com.github.kmizu.yapp.benchmark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ProcessUtil {
  private static void printAll(InputStream in) throws IOException {
    BufferedReader reader = new BufferedReader(
      new InputStreamReader(in)
    );
    String line;
    while((line = reader.readLine()) != null){
      System.out.println(line);
    }
  }
  
  public static int exec(String... command) {
    ProcessBuilder builder = new ProcessBuilder(command);
    builder.redirectErrorStream(true);
    try {
      final Process process = builder.start();
      new Thread(){
        public void run() {
          try {
            printAll(process.getInputStream());
          }catch(IOException e){
            throw new RuntimeException(e);
          }
        }
      }.start();
      return process.waitFor();
    }catch(IOException e){
      throw new RuntimeException(e);
    }catch(InterruptedException e){
      throw new RuntimeException(e);
    }
  }
}