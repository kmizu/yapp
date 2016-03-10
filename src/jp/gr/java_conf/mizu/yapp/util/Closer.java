package jp.gr.java_conf.mizu.yapp.util;

import java.io.Closeable;
import java.io.IOException;

public class Closer {
  public static void close(Closeable closable) {
    if(closable != null)
      try {
        closable.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
  }
}
