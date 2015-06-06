package com.bc.spotifystreamer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
        searchAdapter = new SearchAdapter(new ArrayList<Artist>());
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
        SpotifyApi api = new SpotifyApi();

        SpotifyService spotifyService = api.getService();
        spotifyService.searchArtists(artistName, new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                //List artistList = artistsPager.artists.items;
                searchAdapter.removeAll();
                for(int i = 0; i < artistsPager.artists.items.size(); i++){
                    searchAdapter.add(i, artistsPager.artists.items.get(i));
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
        private ArrayList<Artist> mDataset;

        public SearchAdapter(ArrayList<Artist> dataSet) {
            super();
            mDataset = dataSet;
        }

        public void add(final int position, Artist artist) {
            mDataset.add(position, artist);


            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyItemInserted(position);
                }
            });
        }

        public void remove(Artist artist){
            int position = mDataset.indexOf(artist);
            mDataset.remove(position);
            notifyItemRemoved(position);
        }

        public void removeAll(){
            mDataset.removeAll(mDataset);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_song, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Artist artist = mDataset.get(position);

            if(artist.images.size() > 0){
                int imageToUse = artist.images.size() - 1;
                String imageUrl = artist.images.get(imageToUse).url;
                Picasso.with(getActivity()).load(imageUrl).into(holder.artistPicture);
            }

            holder.artistName.setText(artist.name);

        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }
}
