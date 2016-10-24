package com.nagaraju.rakshith.moviestage;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rakshith on 10/12/16.
 */
public class Movie implements Parcelable{


    private String posterUrl;
    private String movieName;
    private int image;
    private String ratings;
    private String plot;
    private String backdropUrl;
    private String releaseDate;

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }



    public String getBackdropUrl() {
        return backdropUrl;
    }

    public void setBackdropUrl(String backdropUrl) {
        this.backdropUrl = backdropUrl;
    }


    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getRatings() {
        return ratings;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }


    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }



    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }



    Movie(String posterUrl, String movieName,String ratings,String plot,String backdropUrl,String releaseDate){
       this.posterUrl = posterUrl;
        this.movieName = movieName;
        this.ratings = ratings;
        this.plot = plot;
        this.backdropUrl=backdropUrl;
        this.releaseDate=releaseDate;
    }

    private Movie(Parcel in){
        posterUrl = in.readString();
        movieName = in.readString();
        ratings = in.readString();
        plot = in.readString();
        backdropUrl=in.readString();
        releaseDate=in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(posterUrl);
        parcel.writeString(movieName);
        parcel.writeString(ratings);
        parcel.writeString(plot);
        parcel.writeString(backdropUrl);
        parcel.writeString(releaseDate);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }

    };
}
