package com.github.jeffersonschmitt.jksttfilmes;

import android.net.Uri;
import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;

public class ItemFilme implements Serializable {

  private long id;

  private String titulo;

  private String descricao;

  private String dataLancamento;

  // http://image.tmdb.org/t/p/w185/IfB9hy4JH1eH6HEfIgIGORXi5h.jpg
  private String posterPath;

  // http://image.tmdb.org/t/p/w185/4ynQYtSEuU5hyipcGkfD6ncwtwz.jpg
  private String capaPath;

  private float avaliacao;

  public ItemFilme(long id, String titulo, String descricao, String dataLancamento, String posterPath, String capaPath, float avaliacao) {
    this.id = id;
    this.titulo = titulo;
    this.descricao = descricao;
    this.dataLancamento = dataLancamento;
    this.posterPath = posterPath;
    this.capaPath = capaPath;
    this.avaliacao = avaliacao;
  }

  public ItemFilme(JSONObject jsonObject) throws JSONException {
    this.id = jsonObject.getLong("id");
    this.titulo = jsonObject.getString("title");
    this.descricao = jsonObject.getString("overview");
    this.dataLancamento = jsonObject.getString("release_date");
    this.posterPath = jsonObject.getString("poster_path");
    this.capaPath = jsonObject.getString("backdrop_path");
    this.avaliacao = (float) jsonObject.getDouble("vote_average");
  }

  private String buildPath(String width, String path) {
    StringBuilder builder = new StringBuilder();
    builder.append("http://image.tmdb.org/t/p/")
        .append(width)
        .append(path);
    return  builder.toString();
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }

  public String getDataLancamento() {
    return dataLancamento;
  }

  public void setDataLancamento(String dataLancamento) {
    this.dataLancamento = dataLancamento;
  }

  public String getPosterPath() {
    return buildPath("w500", posterPath);
  }

  public void setPosterPath(String posterPath) {
    this.posterPath = posterPath;
  }

  public String getCapaPath() {
    return buildPath("w780", capaPath);
  }

  public void setCapaPath(String capaPath) {
    this.capaPath = capaPath;
  }

  public float getAvaliacao() {
    return avaliacao;
  }

  public void setAvaliacao(float avaliacao) {
    this.avaliacao = avaliacao;
  }
}