package jp.gr.java_conf.mizu.yapp.util;

import java.io.File;

public class SystemProperties {
  public static final String LINE_SEPARATOR = System.getProperty("line.separator");
  public static final String PATH_SEPARATOR = File.pathSeparator;
  public static final String FILE_SEPARATOR = File.separator;
}
