/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package jp.gr.java_conf.mizu.yapp.runtime;

import java.io.IOException;

public class RuntimeIOException extends RuntimeException {
  private static final long serialVersionUID = 5846485643752619982L;
  protected final IOException ex;
  public RuntimeIOException(IOException ex) {
    this.ex = ex;
  }
  public IOException getIOException() {
    return ex;
  }
}
