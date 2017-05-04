package com.github.jeffersonschmitt.jksttfilmes;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.github.jeffersonschmitt.jksttfilmes.data.FilmesContract;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

  private int posicaoItem = ListView.INVALID_POSITION;

  private static final String KEY_POSICAO = "SELECIONADO";

  private boolean useFilmeDestaque = false;

  private FilmeAdapter adapter;

  private static final int FILMES_LOADER=0;

  private ProgressDialog progressDialog;

  ListView lvfilmes;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_main, container, false);

    lvfilmes = (ListView) view.findViewById(R.id.lvfilmes);

    adapter = new FilmeAdapter(getContext(), null);
    adapter.setUseFilmeDestaque(useFilmeDestaque);

    lvfilmes.setAdapter(adapter);

    lvfilmes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      Uri uri= FilmesContract.FilmeEntry.buildUriForFilmes(id);
        Callback callback = (Callback) getActivity();
        callback.onItemSelected(uri);
        posicaoItem = position;
      }
    });

    if (savedInstanceState != null && savedInstanceState.containsKey(KEY_POSICAO)) {
      posicaoItem = savedInstanceState.getInt(KEY_POSICAO);
    }

    progressDialog = new ProgressDialog(getContext());
    progressDialog.setTitle(getString(R.string.pd_carregando_titulo));
    progressDialog.setMessage(getString(R.string.pd_aguarde_mensagem));
    progressDialog.setCancelable(false);

    getLoaderManager().initLoader(FILMES_LOADER,null,this);
    new FilmesAsyncTask().execute();

    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    if (savedInstanceState != null) {
      lvfilmes.smoothScrollToPosition(savedInstanceState.getInt(KEY_POSICAO));
    }

  }

  @Override public void onSaveInstanceState(Bundle outState) {
    if (posicaoItem != ListView.INVALID_POSITION) {
      outState.putInt(KEY_POSICAO, posicaoItem);
    }
    super.onSaveInstanceState(outState);
  }


  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    inflater.inflate(R.menu.menu, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case R.id.menu_atualizar:
        new FilmesAsyncTask().execute();
        Toast.makeText(getContext(), "Atualizado com sucesso", Toast.LENGTH_LONG).show();
        return true;

      case R.id.menu_config:
        startActivity(new Intent(getContext(), SettingsActivity.class));
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public void setUseFilmeDestaque(boolean useFilmeDestaque) {
    this.useFilmeDestaque = useFilmeDestaque;
    if (adapter != null) {
      adapter.setUseFilmeDestaque(useFilmeDestaque);
    }
  }

  @Override
  public void onResume() {
    super.onResume();

    getLoaderManager().restartLoader(FILMES_LOADER, null, this);
  }


  @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {

    progressDialog.show();

    String[] projection = {
        FilmesContract.FilmeEntry._ID,
        FilmesContract.FilmeEntry.COLUMN_TITULO,
        FilmesContract.FilmeEntry.COLUNM_DESCRICAO,
        FilmesContract.FilmeEntry.COLUMN_POSTER_PATH,
        FilmesContract.FilmeEntry.COLUMN_CAPA_PATH,
        FilmesContract.FilmeEntry.COLUMN_AVALIACAO,
        FilmesContract.FilmeEntry.COLUMN_DATA_LANCAMENTO,
        FilmesContract.FilmeEntry.COLUMN_POPULARIDADE

    };

    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

    String ordem = preferences.getString(getString(R.string.prefs_ordem_key), "popular");

    String orderby=null;

    String popularValue=getResources().getStringArray(R.array.prefs_ordem_values)[0];


    if(ordem.equals(popularValue)){
      orderby= FilmesContract.FilmeEntry.COLUMN_POPULARIDADE+" DESC";
    }else{
      orderby= FilmesContract.FilmeEntry.COLUMN_AVALIACAO+" DESC";
    }

    return new CursorLoader(getContext(), FilmesContract.FilmeEntry.CONTENT_URI, projection, null,
        null, orderby);


  }

  @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    adapter.swapCursor(data);
    progressDialog.dismiss();
  }

  @Override public void onLoaderReset(Loader<Cursor> loader) {
    adapter.swapCursor(null);
  }

  public class FilmesAsyncTask extends AsyncTask<Void, Void, List<ItemFilme>> {

    @Override protected List<ItemFilme> doInBackground(Void... params) {
      //https://api.themoviedb.org/3/movie/popular?api_key=3a66b12e3f4b81c2db0889528dd44c09&language=pt-BR

      
      HttpURLConnection urlConnection = null;
      BufferedReader reader = null;

      SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

      String ordem = preferences.getString(getString(R.string.prefs_ordem_key), "popular");
      String idioma = preferences.getString(getString(R.string.prefs_idioma_key), "pt-BR");

      try {
        String urlBase = "https://api.themoviedb.org/3/movie/" + ordem + "?";
        String apiKey = "api_key";
        String language = "language";

        Uri uriApi = Uri.parse(urlBase)
            .buildUpon()
            .appendQueryParameter(apiKey, BuildConfig.TMDB_API_KEY)
            .appendQueryParameter(language, idioma)
            .build();

        URL url = new URL(uriApi.toString());
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        InputStream inputStream = urlConnection.getInputStream();

        if (inputStream == null) {
          return null;
        }

        reader = new BufferedReader(new InputStreamReader(inputStream));

        String linha;
        StringBuffer buffer = new StringBuffer();
        while ((linha = reader.readLine()) != null) {
          buffer.append(linha);
          buffer.append("\n");
        }

        return JsonUtil.jsonlist(buffer.toString());
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        if (urlConnection != null) {
          urlConnection.disconnect();
        }
        if (reader != null) {
          try {
            reader.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
      return null;
    }

    @Override protected void onPostExecute(List<ItemFilme> itemfilmes) {

     if(itemfilmes==null){
        return;
      }

      for (ItemFilme itemfilme : itemfilmes) {
        ContentValues values = new ContentValues();

        values.put(FilmesContract.FilmeEntry._ID, itemfilme.getId());
        values.put(FilmesContract.FilmeEntry.COLUMN_TITULO, itemfilme.getTitulo());
        values.put(FilmesContract.FilmeEntry.COLUNM_DESCRICAO, itemfilme.getDescricao());
        values.put(FilmesContract.FilmeEntry.COLUMN_POSTER_PATH, itemfilme.getPosterPath());
        values.put(FilmesContract.FilmeEntry.COLUMN_CAPA_PATH, itemfilme.getCapaPath());
        values.put(FilmesContract.FilmeEntry.COLUMN_AVALIACAO, itemfilme.getAvaliacao());
        values.put(FilmesContract.FilmeEntry.COLUMN_DATA_LANCAMENTO, itemfilme.getDataLancamento());
        values.put(FilmesContract.FilmeEntry.COLUMN_POPULARIDADE, itemfilme.getPopularidade());




        int update = getContext().getContentResolver()
            .update(FilmesContract.FilmeEntry.buildUriForFilmes(itemfilme.getId()),values,null,null);



        if (update == 0) {
          getContext().getContentResolver().insert(FilmesContract.FilmeEntry.CONTENT_URI, values);
        }
      }
    }
  }
}