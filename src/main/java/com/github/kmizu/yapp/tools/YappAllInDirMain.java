package com.github.kmizu.yapp.tools;

import java.io.File;
import java.io.IOException;

public class YappAllInDirMain {
  public static void main(String[] args) throws IOException, CommandLineException {
    File srcDir = new File(args[0]);
    File dstDir = new File(args[1]);
    for(File file : srcDir.listFiles()){
      if(file.getName().endsWith(".ypp")){
        String[] newArgs = new String[args.length];
        System.arraycopy(args, 2, newArgs, 0, args.length - 2);
        newArgs[newArgs.length - 2] = file.getCanonicalPath();
        newArgs[newArgs.length - 1] = dstDir.getCanonicalPath();
        new YappMain(newArgs).generate();
      }
    }
  }
}
