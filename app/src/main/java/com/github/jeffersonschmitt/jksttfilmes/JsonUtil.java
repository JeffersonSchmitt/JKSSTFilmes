package com.github.jeffersonschmitt.jksttfilmes;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {

  public static List<ItemFilme> fromJsonToList(String json) {

    List<ItemFilme> list= new ArrayList<>();


    try {
      JSONObject jsonBase = new JSONObject(json);
      JSONArray results = jsonBase.getJSONArray("results");

      for (int i = 0; i < results.length(); i++) {
        JSONObject filmeObject = results.getJSONObject(i);
        ItemFilme itemfilme = new ItemFilme(filmeObject);
        list.add(itemfilme);
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return list;
  }
}
