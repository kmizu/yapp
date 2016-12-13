package com.github.kmizu.yapp.tools;

import java.io.File;

import com.github.kmizu.yapp.AbstractYappTestCase;
import com.github.kmizu.yapp.tools.YappMain;
import junit.framework.TestCase;

public class YappMainTest extends AbstractYappTestCase {
  private String tempDirName = "temp";
  
  protected void setUp() throws Exception {
    f(tempDirName).mkdirs();
  }

  protected void tearDown() throws Exception {
    File tempDir = f(tempDirName);
    for(File file : tempDir.listFiles()){
      if(file.isFile()) file.delete();
    }
    tempDir.delete();
  }
  
  private File f(String fileName) {
    return new File(fileName);
  }

  /*
   * 'com.github.kmizu.yapp.YappMain.generate(File)' のためのテスト・メソッド
   */
  public void testGenerate() throws Exception {
    for(int i = 0; i < INPUT_FILES.length; i++){
      assertTrue(new YappMain(INPUT_FILES[i], tempDirName).generate());
      //assertTrue(new YappMain("--not_memoize", INPUT_FILES[i], tempDirName).generate());
    }
  }
}
