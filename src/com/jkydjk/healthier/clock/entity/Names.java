package com.jkydjk.healthier.clock.entity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * 一个扩展的HashMap&lt;Integer, String&gt;类<br />
 * 用来存放 id => key 这样的数据
 * 
 * @author miclle
 * 
 */
public class Names extends HashMap<Integer, String> {

  /**
   * Serial Version UID
   */
  private static final long serialVersionUID = -1232255463714594684L;

  private ArrayList<Integer> ids = new ArrayList<Integer>();

  private ArrayList<String> names = new ArrayList<String>();

  public Names() {
    super();
  }

  /**
   * 
   * @param JSONArray
   * @return ThumbEntity
   * @throws JSONException
   */
  public static Names parseJSONArray(JSONArray array) throws JSONException {

    Names thumbEntity = new Names();

    for (int i = 0; i < array.length(); i++) {
      JSONArray hour = (JSONArray) array.get(i);
      thumbEntity.put(hour.getInt(0), hour.getString(1));
    }

    return thumbEntity;
  }

  @Override
  public String put(Integer id, String name) {
    ids.add(id);
    names.add(name);
    return super.put(id, name);
  }

  @Override
  public String remove(Object key) {
    ids.remove(key);
    return super.remove(key);
  }

  @Override
  public void clear() {
    ids.clear();
    super.clear();
  }

  public ArrayList<Integer> getIds() {
    return ids;
  }

  public ArrayList<String> getNames() {
    return names;
  }

}
