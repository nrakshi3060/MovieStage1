package com.nagaraju.rakshith.moviestage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Rakshith on 10/3/16.
 */
public class MovieFragment extends Fragment {
    private MovieAdapter movieAdapter;
    private Movie[] mArray;
    private ArrayList<Movie> movieArrayList = new ArrayList<Movie>();


    public MovieFragment() {
    }

    public void updateMovie(){

        FetchMovieTask movieTask = new FetchMovieTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String choice = prefs.getString(getString(R.string.pref_choice_list_key),getString(R.string.pref_location_default));
        System.out.println(choice);
        movieTask.execute(choice);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public void onSavedInstanceState(Bundle savedInstanceState){
        //savedInstanceState.
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList("Movies", movieArrayList);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Context c = getContext();

        if(savedInstanceState == null || !savedInstanceState.containsKey("Movies")) {
            if(isNetworkAvailable(c)){
                updateMovie();
            } else {
                Toast.makeText(c,"Network not available! \n Please Connect to a Network to continue",Toast.LENGTH_LONG).show();
            }
            super.onCreate(savedInstanceState);
            //movieArrayList = new ArrayList<Movie>(Arrays.asList(mArray));
        } else {

            movieArrayList = savedInstanceState.getParcelableArrayList("Movies");
        }

        setHasOptionsMenu(true);
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.moviefragment,menu);
    }

    public void onStart(){
        super.onStart();
        Context c = getContext();
        if(isNetworkAvailable(c)){
            updateMovie();
        } else {
            Toast.makeText(c,"Network not available! \n Please Connect to a Network to continue",Toast.LENGTH_LONG).show();
        }

    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getContext(), SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.gridview_movie, container, false);

        GridView gridview = (GridView) rootView.findViewById(R.id.gridview_movie);
        movieAdapter = new MovieAdapter(getActivity(), movieArrayList);
        gridview.setAdapter(movieAdapter);





        gridview.setOnItemClickListener( new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
                Movie m = movieAdapter.getItem(position);


                Intent intent = new Intent(getActivity(),DetailActivity.class).putExtra("Movie",m);
                startActivity(intent);
            }

        });



        return rootView;
    }


    public class FetchMovieTask extends AsyncTask<String, Void, Movie[] > {


        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        @Override
        protected Movie[] doInBackground(String... params) {

            if(params.length==0){
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;


            String movieJsonStr = null;


            try {


                final String FORECAST_BASE_URL ="https://api.themoviedb.org/3/movie/";
                final String APP_KEY = "123";


                final String APPID_PARAM = "api_key";
                final String LANGUAGE_PARAM = "language";
                String uri = FORECAST_BASE_URL;
                if(params[0]!=null&&!params[0].equals("")){
                    uri += params[0];
                } else {
                    uri += "popular";
                }
                uri +="?"+APPID_PARAM+"="+APP_KEY;
                //Log.v("hakhgf",uri);
                URL url = new URL(uri);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    movieJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    movieJsonStr = null;
                }
                movieJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                movieJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }

            }
            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {

                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] result) {
           // mArray=result;
//            if(result!=null){
//                movieAdapter.clear();
//                for(Movie movieStr : result){
//                    movieAdapter.add(movieStr);
//                }
//            }

            if(result!=null){
                movieAdapter.clear();
                for(Movie movieStr : result){
                    movieArrayList.add(movieStr);
                }
            }
        }

        private String formatPosterPath(String posterStr) {
            String posterUrl = "http://image.tmdb.org/t/p/w500/";
            posterUrl += posterStr;
            return posterUrl;
        }

        private String formatRating(String ratingStr) {
            return ratingStr+"/10";
        }

        private String formatBackDropPath(String posterStr) {
            String posterUrl = "http://image.tmdb.org/t/p/w780/";
            posterUrl += posterStr;
            return posterUrl;
        }

        private Movie[] getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            final String OWM_RESULTS = "results";
            final String OWM_POSTER = "poster_path";
            final String OWM_BACKDROP ="backdrop_path";
            final String OWM_MOVIE_NAME = "original_title";
            final String OWM_PLOT="overview";
            final String OWM_RATING = "vote_average";
            final String OWM_RELEASE_DATE = "release_date";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(OWM_RESULTS);


            Movie[] resultStrs = new Movie[movieArray.length()];
            for(int i = 0; i < movieArray.length(); i++) {
                String poster;
                JSONObject movie = movieArray.getJSONObject(i);
                poster = movie.getString(OWM_POSTER);
                String movieName = movie.getString(OWM_MOVIE_NAME);
                String plot = movie.getString(OWM_PLOT);
                String rating = movie.getString(OWM_RATING);
                String backdrop = movie.getString(OWM_BACKDROP);
                String releaseDate = movie.getString(OWM_RELEASE_DATE);

                resultStrs[i]= new Movie(formatPosterPath(poster),movieName,formatRating(rating),plot,formatBackDropPath(backdrop),releaseDate);
            }


            return resultStrs;

        }

    }

}
