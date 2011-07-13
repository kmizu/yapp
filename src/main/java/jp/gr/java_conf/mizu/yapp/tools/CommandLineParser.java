package jp.gr.java_conf.mizu.yapp.tools;

import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.gr.java_conf.mizu.yapp.util.CollectionUtil;

public class CommandLineParser {
  public final Map<String, Option> options = CollectionUtil.map();
  public final Map<String, Object> optionValues = CollectionUtil.map();  
  public final List<String> values = CollectionUtil.list();
  public final List<String> descriptions = CollectionUtil.list();
  
  public static enum OptionType {INT, STR, NONE}
  public static class Option {
    public final String key;
    public final String name;
    public final OptionType type;
    public final String description;
    
    public Option(String key, String name, OptionType type, String description) {
      this.key = key;
      this.name = name;
      this.type = type;
      this.description = description;
    }    
  }
  public CommandLineParser opt(String key, String name, OptionType type, String description) {
    options.put(name, new Option(key, name, type, description));
    descriptions.add(description);
    return this;
  }
  public void parse(String[] commandLine) {
    for(int i = 0; i < commandLine.length; i++) {
      Option option = options.get(commandLine[i]);
      if(option == null) {
        values.add(commandLine[i]);
        continue;
      }
      if(option.type == OptionType.NONE) {
        //put dummy object
        optionValues.put(option.key, new Object());
        continue;
      }
      if(i >= commandLine.length - 1) {
        throw new CommandLineException("no argument is supplied for " + option.name);
      }
      i++;
      switch(option.type) {
      case INT:
        try {
          optionValues.put(option.key, Integer.parseInt(commandLine[i]));
        }catch(NumberFormatException e) {
          throw new CommandLineException("argument for " + option.name + " must be integer");
        }
        break;
      case STR:
        optionValues.put(option.key, commandLine[i]);
        break;
      default:
        throw new AssertionError("should not reach here");
      }
    }
  }
  
  public boolean hasOption(String key) {
    return optionValues.containsKey(key);
  }
  
  public String getString(String key) {
    return (String)optionValues.get(key);
  }
  
  public String getString(String key, String defaultValue) {
    return optionValues.containsKey(key) ?
      getString(key) : defaultValue;
  }
  
  public int getInt(String key) {
    return (Integer)optionValues.get(key);
  }
  
  public int getInt(String key, int defaultValue) {
    return optionValues.containsKey(key) ?
      getInt(key) : defaultValue;
  }
}