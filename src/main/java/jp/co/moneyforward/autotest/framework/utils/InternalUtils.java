package jp.co.moneyforward.autotest.framework.utils;

import java.util.Arrays;
import java.util.stream.Stream;

import static java.util.stream.Stream.concat;

public enum InternalUtils {
  ;
  
  @SafeVarargs
  public static <T> Stream<T> concat(Stream<T>... streams) {
    if (streams.length == 0)
      return Stream.empty();
    if (streams.length == 1)
      return streams[0];
    if (streams.length == 2)
      return Stream.concat(streams[0], streams[1]);
    else
      return Stream.concat(streams[0], concat(Arrays.copyOfRange(streams, 1, streams.length)));
  }
}
