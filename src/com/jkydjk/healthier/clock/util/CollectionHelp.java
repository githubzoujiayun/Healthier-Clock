package com.jkydjk.healthier.clock.util;

import java.util.Collection;

/**
 * 
 * @author miclle
 *
 */
public class CollectionHelp {

  /**
   * 拼接
   * 
   * @param collections
   * @return
   */
  public static <T> String join(Collection<T>... collections) {
    return join(",", collections);
  }

  /**
   * 拼接
   * 
   * @param conjunction
   * @param collections
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <T> String join(String conjunction, Collection<T>... collections) {

    int length = collections.length;

    if (length == 0)
      return null;

    Collection<Object> collection = (Collection<Object>) collections[0];

    if (length > 1) {
      for (int i = 1; i < length; i++) {
        collection.addAll(collections[i]);
      }
    }

    StringBuilder sb = new StringBuilder();

    boolean first = true;

    for (Object obj : collection) {
      if (obj == null)
        break;

      if (first)
        first = false;
      else
        sb.append(conjunction);

      sb.append(obj.toString());
    }

    return sb.toString();
  }

}
