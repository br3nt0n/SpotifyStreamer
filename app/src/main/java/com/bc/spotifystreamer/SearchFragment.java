package com.bc.spotifystreamer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class SearchFragment extends Fragment {
    SearchAdapter searchAdapter;

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container);

        RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.search_results_recyclerview);
        searchAdapter = new SearchAdapter(new ArrayList<SearchArtist>());
        recyclerView.setAdapter(searchAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        if(savedInstanceState != null){
            searchAdapter.mDataset = (ArrayList)savedInstanceState.get("mDataSet");
        }

        final EditText searchText = (EditText)rootView.findViewById(R.id.search_text);
        searchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    updateSpotifyArtist(searchText.getText().toString());
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
                    return true;
                } else {
                    return false;
                }
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("mDataSet", (ArrayList)searchAdapter.mDataset);
    }

    private void updateSpotifyArtist(final String artistName){
        searchAdapter.removeAll();

        SpotifyApi api = new SpotifyApi();

        SpotifyService spotifyService = api.getService();
        spotifyService.searchArtists(artistName, new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                //List artistList = artistsPager.artists.items;
                for (int i = 0; i < artistsPager.artists.items.size(); i++) {
                    Artist artist = artistsPager.artists.items.get(i);

                    //Get the index of the second largest image (always returned by the API as count - 1)
                    if (artist.images.size() > 0) {
                        SearchArtist searchArtist = new SearchArtist(artist.name,
                                artist.id,
                                artist.images.get(0).url
                        );
                        searchAdapter.add(i, searchArtist);
                    } else {
                        SearchArtist searchArtist = new SearchArtist(artist.name,
                                artist.id
                        );
                        searchAdapter.add(i, searchArtist);
                    }

                    Log.v("log", artist.name);
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private TextView artistName;
        private ImageView artistPicture;

        public ViewHolder(View itemView) {
            super(itemView);
            artistName = (TextView) itemView.findViewById(R.id.artist_name);
            artistPicture = (ImageView) itemView.findViewById(R.id.artist_image);
        }
    }

    private class SearchAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<SearchArtist> mDataset;

        public SearchAdapter(ArrayList<SearchArtist> dataSet) {
            super();

            //Set the dataset to the dataset passed in the constructor
            mDataset = dataSet;
        }

        public void add(final int position, SearchArtist artist) {
            mDataset.add(position, artist);

            //Notify the view that the dataset has changed
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("Log","item inserted");
                    notifyItemInserted(position);
                }
            });
        }

        public void removeAll(){
            final int mDatasetSize = mDataset.size();

            //Check if the mDataset contains data, if so remove otherwise do nothing (prevents crash)
            if (mDatasetSize > 0){
                mDataset.removeAll(mDataset);

                //Notify the view that the dataset has changed
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_song, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final SearchArtist artist = mDataset.get(position);
            String imageUrl = artist.getImageUrl();
            if(imageUrl != null){
                Picasso.with(getActivity()).load(imageUrl).into(holder.artistPicture);
            }
            holder.artistName.setText(artist.getName());
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

}
