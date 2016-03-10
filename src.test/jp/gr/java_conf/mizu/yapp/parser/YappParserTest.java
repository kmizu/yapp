package jp.gr.java_conf.mizu.yapp.parser;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import jp.gr.java_conf.mizu.yapp.AbstractYappTestCase;
import jp.gr.java_conf.mizu.yapp.parser.YappParser;
import junit.framework.TestCase;

public class YappParserTest extends AbstractYappTestCase {
  private YappParser[] parsers;

	protected void setUp() throws Exception {
    parsers = new YappParser[INPUT_FILES.length];
    for (int i = 0; i < INPUT_FILES.length; i++) {
      parsers[i] = new YappParser(file(INPUT_FILES[i]));      
    }
	}

	protected void tearDown() throws Exception {
	}

	public void testParse() throws Exception {
    for (int i = 0; i < parsers.length; i++) {
      parsers[i].parse();
    }
	}
  
  private Reader file(String fileName) throws IOException {
    return new FileReader(fileName);
  }
}
