require 'time'

RATS_DIR = 'lib\rats-1.14.3'

class Performance
  MIN_MEM = 10
  MAX_MEM = 500
  STEP = 10
  REPEAT = 20
  BENCHMARK_CLASS = 'jp.gr.java_conf.mizu.yapp.benchmark.ParserBenchmark'
  PARSER_PACKAGE='jp.gr.java_conf.mizu.yapp.benchmark'
  CLASSPATH="build;build.benchmark;#{RATS_DIR}\\rats-runtime.jar;#{RATS_DIR}\\xtc.jar"
  
  def initialize(dir, ext, result_file)
    @dir = dir
    @ext = ext
    @result_file = result_file
  end
  
  def median input
    size = input.size
    m = size / 2
    if size % 2 == 0
      (input[m] + input[m - 1]) / 2
	  else
      input[m]
    end
  end
  
  def measure parser, dir, mem
    command = "java -cp #{CLASSPATH} -Xms#{mem}m -Xmx#{mem}m #{BENCHMARK_CLASS} -d #{dir} -e#{@ext} -p #{PARSER_PACKAGE}.#{parser} 2>nul"
    perf = []
    REPEAT.times {
      if `#{command}` =~ /([0-9]+)KB\/s/
        perf << $1.to_i
      else
        return nil
      end
    }
    median(perf.sort)
  end

  def benchmark parsers
    mem = MIN_MEM
    open(@result_file, 'w') do|f|
      parser_descs = parsers.map{|parser| parser[1]}
		  puts "       ,#{parser_descs.join(',')}"
		  f.puts "       ,#{parser_descs.join(',')}"
	    MIN_MEM.step(MAX_MEM, STEP) do|mem|
			  perfs = parsers.map{|parser| measure parser[0], @dir, mem}
			  perfs = perfs.map{|perf| perf ? perf : 0}
			  result = "#{(sprintf "%3d", mem)},#{perfs.join(',')}"
			  puts result
			  f.puts result
	    end
	  end
  end
end

class HeapUsage
  MIN = 2    #-Xmxオプションが2Mより大きい値を要求するため
  MAX = 512
  BENCHMARK_CLASS = 'jp.gr.java_conf.mizu.yapp.benchmark.ParserBenchmark'
  PARSER_PACKAGE='jp.gr.java_conf.mizu.yapp.benchmark'
  CLASSPATH="build;build.benchmark;#{RATS_DIR}\\rats-runtime.jar;#{RATS_DIR}\\xtc.jar"
  
  def initialize dir, ext, result_file
    @dir = dir
    @ext = ext
    @result_file = result_file
  end
  
  def measure parser, file
    min = MIN
    max = MAX
    last = MAX
    while true
      v = (min + max) / 2
      break if v == last
      if system "java -cp #{CLASSPATH} -ms#{v}m -Xmx#{v}m #{BENCHMARK_CLASS} -f #{file} -e#{@ext} #{PARSER_PACKAGE}.#{parser} 2>nul"
        max = v
      else
        min = v
      end
      last = v
    end
    max
  end

  def benchmark parsers
    open(@result_file, 'w') do |f|
      header = "       ,#{parsers.map{|parser| parser[1]}.join(',')}"
      puts header
      f.puts header 
      Dir.foreach(@dir) do|e|
        if e =~ /#{@ext}$/
          file = "#{@dir}/#{e}"
          mems = parsers.map{|parser| measure(parser[0], file)}
          body = "#{sprintf "%7d", File.size(file) / 1024},#{mems.join(',')}"
          puts body
          f.puts body
        end
      end
    end
  end
end

puts "Java parser heap usage measurement..."

HeapUsage.new('benchmark/javacc', '.java', 'heap_java_parser.csv').benchmark([
  ['YappJavaRecognizer', 'NO-CUT'],
  ['YappACJavaRecognizer', 'AUTO'],
	['YappOptimizedJavaRecognizer', 'CUT'], 
	['RatsJavaRecognizer', 'RATS'], 
])

puts "XML parser heap usage measurement..."

HeapUsage.new('benchmark/ijs-elan2.0', '.tei', 'heap_xml_parser.csv').benchmark([
  ['YappXMLRecognizer', 'NO-CUT'],
  ['YappACXMLRecognizer', 'AUTO'],
	['YappOptimizedXMLRecognizer', 'CUT'], 
	['RatsXMLRecognizer', 'RATS'], 
])

puts "JSON parser heap usage measurement..."

HeapUsage.new('benchmark/json-ijs-elan2.0', '.json', 'heap_json_parser.csv').benchmark([
  ['YappJSONRecognizer', 'NO-CUT'],
  ['YappACJSONRecognizer', 'AUTO'],
  ['YappOptimizedJSONRecognizer', 'CUT'], 
  ['RatsJSONRecognizer', 'RATS'], 
])

puts "ESLL parser heap usage measurement..."

HeapUsage.new('benchmark/esll', '.esll', 'heap_esll_parser.csv').benchmark([
  ['YappESLLRecognizer', 'NO-CUT'],
  ['YappACESLLRecognizer', 'AUTO'],
  ['YappOptimizedESLLRecognizer', 'CUT'], 
  ['RatsESLLRecognizer', 'RATS'], 
])

puts "Java parser performance measurement..."

Performance.new('benchmark/javacc', '.java', 'perf_java_parser.csv').benchmark([
  ['YappJavaRecognizer', 'NO-CUT'],
  ['YappACJavaRecognizer', 'AUTO'],
	['YappOptimizedJavaRecognizer', 'CUT'],
	['RatsJavaRecognizer', 'RATS'], 
])

puts "XML parser performance measurement..."

Performance.new('benchmark/ijs-elan2.0', '.tei', 'perf_xml_parser.csv').benchmark([
  ['YappXMLRecognizer', 'NO-CUT'],
  ['YappACXMLRecognizer', 'AUTO'],
  ['YappOptimizedXMLRecognizer', 'CUT'],
  ['RatsXMLRecognizer', 'RATS'], 
])

puts "JSON parser performance measurement..."

Performance.new('benchmark/json-ijs-elan2.0', '.json', 'perf_json_parser.csv').benchmark([
  ['YappJSONRecognizer', 'NO-CUT'],
  ['YappACJSONRecognizer', 'AUTO'],
  ['YappOptimizedJSONRecognizer', 'CUT'],
  ['RatsJSONRecognizer', 'RATS'], 
])

puts "ESLL parser performance measurement..."

Performance.new('benchmark/esll', '.esll', 'perf_esll_parser.csv').benchmark([
  ['YappESLLRecognizer', 'NO-CUT'],
  ['YappACESLLRecognizer', 'AUTO'],
  ['YappOptimizedESLLRecognizer', 'CUT'],
  ['RatsESLLRecognizer', 'RATS'], 
])