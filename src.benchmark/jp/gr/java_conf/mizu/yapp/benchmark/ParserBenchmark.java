package jp.gr.java_conf.mizu.yapp.benchmark;
import jp.gr.java_conf.mizu.yapp.benchmark.parser.*;

import java.io.*;
import java.lang.reflect.Constructor;

import jp.gr.java_conf.mizu.yapp.runtime.AbstractPackratParser;
import static java.lang.Runtime.*;

/**
 * @author Mizushima
 *
 */
public class ParserBenchmark {
  private static final String NL = System.getProperty("line.separator");
  public static final String USAGE =
    "Usage: java <this class' name> (-f <input file> | -d <input directory>) -e<extension> [options] <parser>" + NL +
    "options" + NL +
    "  -p: print result";
  private long bytes;
  private long time;
  
  private File input;
  private boolean isDirectory;
  private boolean isPrinted;
  private String extension;
  
  private static long n2m(long nanosecs) {
    return nanosecs / 1000000;
  }
  
  private void warmUp(int times) {
    for(int i = 0; i < times; i++){
      System.currentTimeMillis();
    }
  }
  
  public ParserBenchmark() {
  }
  
  @SuppressWarnings("unchecked")
  public int run(String... args) throws Exception {
    if(args.length < 3) return usage();    
    if(args[0].equals("-f")){
      input = new File(args[1]);
      isDirectory = false;
    }else if(args[0].equals("-d")){
      input = new File(args[1]);
      isDirectory = true;
    }
    int i = 2;
    for(;;){
      if(!args[i].startsWith("-")) break;
      if(args[i].equals("-p")){
        isPrinted = true;
        i++;
      }else if(args[i].startsWith("-e")){
        extension = args[i].substring(2);
        i++;
      }else{
        return usage();
      }
    }
    Class<?> parser = Class.forName(args[i]);
    if(isDirectory){
      benchmarkAll((Class<? extends GenericParser<?>>)parser);
    }else{
      benchmark((Class<? extends GenericParser<?>>)parser, input);
    }
    return 0;
  }
  
  private void benchmarkAll(Class<? extends GenericParser<?>> parser)
    throws Exception {
    time = 0;
    bytes = 0;
    for(File f : input.listFiles()){
      if(!f.getName().endsWith(extension)) continue;
      benchmark(parser, f);
    }
    if(isPrinted){
      System.out.printf("%4dKB/s%n", (long)((bytes / 1024) / (time / 1000.0)));
    }
  }
  
  private void benchmark(Class<? extends GenericParser<?>> parser, File file) throws Exception {
    warmUp(100000);
    long start, end, time;

    FileReader reader = new FileReader(file);
    GenericParser<?> aParser = parser.newInstance();
    aParser.setInput(reader);
    try {
      start = System.nanoTime();
      aParser.parse();
      end   = System.nanoTime();
      time = n2m(end - start);
      this.bytes += file.length();
      this.time += time;
    }finally{
      reader.close();
    }
  }
  
  private static int usage() {
    System.out.println(USAGE);
    return 1;
  }
  
  private static void printResult(String fileName, long bytes, long time) {
    System.out.printf("%30s: %7dB: time:%6d[ms]%n", fileName, bytes, time);
  }
  
  public static void main(String[] args) throws Exception {
    new ParserBenchmark().run(args);
  }
}
