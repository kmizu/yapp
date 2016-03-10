STATEMENTS = []

STATEMENTS << <<PRINT
  print("Hello")
PRINT
STATEMENTS << <<IF
  if(i < 3){
    print("i < 3")
  }else {
    print("i >= 3")
  }
IF
STATEMENTS << <<BLOCK
  foo(1)
  bar(2)
  bar(3)
BLOCK
STATEMENTS << <<MATH_EXP
  x = x + 2
  y = y - 2
  z = z * 2
  a = a / 2
MATH_EXP
STATEMENTS << <<COMPARATIVE
  i < 5
  i > 5
  i <= 3
  i >= 3
COMPARATIVE
STATEMENTS << <<FUN_CALL
  f1(100)
  f2(200)
  f3(300) + f4(300)
FUN_CALL
STATEMENTS << <<STRING_LITERAL
  "2 + 3 = #{2 + 3}"
  "2 - 3 = #{2 - 3}"
  "2 * 3 = #{2 * 3}"
  "2 / 3 = #{2 / 3}"  
STRING_LITERAL


def generate_esll_program(size)
  buffer = ""
  prefix = "hoge"
  buffer << "fun main() {\n"
  while(buffer.size < size)
    STATEMENTS.each do|s|
      buffer << STATEMENTS[(rand * STATEMENTS.size).to_i]
      buffer << "\n"
    end
  end
  buffer << "}\n"
  buffer
end

count = 0
(1024 * 100).step(1024 * 1024, 1024 * 100) do|size|
	suffix = sprintf("%02d", count)
	open("benchmark/esll/generated#{suffix}.esll", "w") do|f|
		f.puts generate_esll_program(size)
	end
	count += 1
end
