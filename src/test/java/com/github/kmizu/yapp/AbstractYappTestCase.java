package com.github.kmizu.yapp;

import java.io.File;

import junit.framework.TestCase;

public class AbstractYappTestCase extends TestCase {
  protected static final String[] INPUT_FILES = {
    "data" + File.separator + "JavaRecognizer.ypp",
    "data" + File.separator + "OptimizedJavaRecognizer.ypp",
  };
}
