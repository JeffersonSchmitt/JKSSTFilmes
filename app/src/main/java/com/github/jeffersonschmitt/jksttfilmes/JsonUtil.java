package com.github.jeffersonschmitt.jksttfilmes;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {

  public static List<ItemFilme> jsonlist(String json) {

    List<ItemFilme> listFilme= new ArrayList<>();


    try {
      JSONObject jsonBase = new JSONObject(json);
      JSONArray results = jsonBase.getJSONArray("results");

      for (int i = 0; i < results.length(); i++) {
        JSONObject filmeObject = results.getJSONObject(i);
        ItemFilme itemfilme = new ItemFilme(filmeObject);
        listFilme.add(itemfilme);
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return listFilme;
  }
}
