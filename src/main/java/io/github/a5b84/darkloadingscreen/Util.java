package io.github.a5b84.darkloadingscreen;

public class Util {
  public static String padStart(String s, int minLength, char padding) {
    if (s.length() < minLength) {
      StringBuilder result = new StringBuilder(minLength);
      int paddingLength = minLength - s.length();

      while (result.length() < paddingLength) {
        result.append(padding);
      }

      result.append(s);
      return result.toString();
    } else {
      return s;
    }
  }
}
