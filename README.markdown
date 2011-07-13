# Yapp - Yet Another Packrat Parser generator.

Yapp is a packrat parser generator.  Packrat parsing is a powerful parsing 
technique developed by Bryan Ford in 2002.  

Packrat parsers can handle complicated grammars and recursive structures 
in lexical elements more easily than the traditional LL(k) or LR(1) parsing 
algorithms.  And Packrat parsers is able to recognize wide-ranged languages 
PEG can express in linear time.

However, packrat parsers require O(n) extra space for memoization, where n is the 
length of the input.  In some applications (e.g. large XML file), this is
problematic.

Then, I developed a new packrat parser named Yapp.  Yapp has feature called
`cut`, which suppress backtracking.  If you insert cut operator into your
grammar appropriately, generated packrat parsers don't require O(n) extra 
space but only `almost` constant extra space for memoization.

Also, Yapp has feature for automatic insertion of cut operators into your
grammars.  Altough This feature is yet experimental, it is effective for
some grammars.

## Build from sources
### requirement
+ Ant 1.6 or newer
+ J2SE 5 or newer

### building jar

Under ths project's root directory, run the following command:

   $ ant jar
   
After that, "yapp.jar" file is generated under the directory.  The generated file is an executable 
jar file which doesn't depend on any other jar files.

## Usage of tools
### yapp
  
#### Usage:

   java -jar yapp [options] <yapp grammar file>
       
###  Available options:
+ --pre: specify parser class' prefix"
+ --time: measure time elapsed
+ --pkg: specify parser class' package")
+ --pm: print the number of rules that should be memoized")
+ --edr: eliminate dead rules")
+ --em: eliminate needless memoizations
+ --reg: insert cut automatically by regex like method
+ --Onj: turn off 'join column optimization'
+ --ac: turn on 'auto cut insertion' optimization
+ --inl: inline nonterminal expression
+ --ACfirst: auto cut insertion by first set method.  This option must be specified with --ac.
+ --ACfollow: auto cut insertion by follow set method.  This option must be specified with --ac.
+ --space: calculate space complexity of specified rule and print.
   
## Limitations

+ There are some features that is supported partially.
+ Some options are experimental.
+ Poor documentations.
  + I will improve it in a few months.

This software is distributed under modified BSD license (see LICENSE.txt).