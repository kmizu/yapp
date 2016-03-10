def gen_class class_name, dir, size
  method = <<METHOD_HEADER
  public int doSomething() {
    return 
METHOD_HEADER
  500.times {
    method << <<METHOD_BODY
      + 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9
METHOD_BODY
  }
  method << <<METHOD_FOOTER
    ;
  }
METHOD_FOOTER
  open("#{dir}/#{class_name}.java", 'w') do|f|
    f.puts "public class #{class_name} {"
    while size > 0
      f.puts method
      size -= method.length
    end
    f.puts "}"  
  end
end

count = 0
102.step(1024, 102) do|sizeKB|
  gen_class "M#{count}", 'benchmark/auto', sizeKB * 1024
  count += 1
end