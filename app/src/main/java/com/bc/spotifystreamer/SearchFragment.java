package com.bc.spotifystreamer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * A placeholder fragment containing a simple view.
 */
public class SearchFragment extends Fragment {

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container);

        RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.search_results_recyclerview);
        SearchAdapter searchAdapter = new SearchAdapter();
        recyclerView.setAdapter(searchAdapter);

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

    private void updateSpotifyArtist(String artistName){
        Log.v("Spotify", artistName);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class SearchAdapter extends RecyclerView.Adapter<ViewHolder> {

        public SearchAdapter() {
            super();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
}
