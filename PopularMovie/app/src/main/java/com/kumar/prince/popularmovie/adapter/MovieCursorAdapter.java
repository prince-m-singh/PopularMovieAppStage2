package com.kumar.prince.popularmovie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.kumar.prince.popularmovie.R;
import com.kumar.prince.popularmovie.utilities.MovieGeneralFabDataModal;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;

import java.io.Serializable;
import java.util.List;

public class MovieCursorAdapter extends RecyclerView.Adapter<MovieCursorAdapter.MovieAdapterViewHolder> implements Serializable {

    private String[] posterURL;
    private JSONArray movieData;
    private Context context;
    List<MovieGeneralFabDataModal> movieGeneralModals;
    final private MovieCursorAdapter.MovieAdapterOnClickHandler mClickHandler;

    public MovieCursorAdapter(MovieCursorAdapter.MovieAdapterOnClickHandler movieAdapterOnClickHandler) {
        mClickHandler = movieAdapterOnClickHandler;
    }


    public interface MovieAdapterOnClickHandler {
        void onClick(MovieGeneralFabDataModal movieData);
    }

    @Override
    public MovieCursorAdapter.MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movieposter, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieCursorAdapter.MovieAdapterViewHolder holder, int position) {
        Picasso.with(context).load(posterURL[position]).into(holder.imageMoviePoster);
    }

    @Override
    public int getItemCount() {
        if (posterURL == null) {
            return 0;
        }
        return posterURL.length;
    }

    public void setMovierURLData(String[] posterURL) {
        this.posterURL = posterURL;
        notifyDataSetChanged();
    }

    public void setMovieGeneralModals(List<MovieGeneralFabDataModal> movieData) {
        this.movieGeneralModals = movieData;
    }


    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView imageMoviePoster;

        public MovieAdapterViewHolder(View itemView) {

            super(itemView);
            imageMoviePoster = (ImageView) itemView.findViewById(R.id.imgmovieposter);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            mClickHandler.onClick(movieGeneralModals.get(getAdapterPosition()));

        }
    }

}
