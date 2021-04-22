package com.macneet.wuapp.activities.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.macneet.wuapp.databinding.DisplayFragmentBinding;
import com.macneet.wuapp.databinding.FragmentExceptionDisplayBinding;
import com.macneet.wuapp.datamanagers.DataReceiver;
import com.macneet.wuapp.exceptions.InvalidLinkException;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.List;

public abstract class DisplayFragment<T> extends Fragment implements DataReceiver {

    protected DisplayFragmentBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DisplayFragmentBinding.inflate(inflater);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                binding.swipeRefresh.setRefreshing(true);
            }
        });
        return binding.getRoot();
    }

    /**
     * This function is used when it successfully receives data to load
     * that data into a display fragments recycler view and do any other necessary setup
     * @param data the data to be displayed
     */
    public void loadData(List data){ binding.swipeRefresh.setRefreshing(false);}

    /**
     * This function is used to refresh the data. It should make the relevant data manager
     * re-download its data.
     */
    protected abstract void refresh();

    /**
     * This function is used to display a message when no the app fails to find any data to display
     */
    public void loadNoDataMessage(){
        binding.swipeRefresh.setRefreshing(false);
        binding.loadingView.getRoot().setVisibility(View.GONE);//Hide loading animation
        binding.infoMessage.setVisibility(View.VISIBLE);
    }

    /**
     * This function is used to display an error message when something unexpected happens while
     * downloading/parsing the relevant data
     * @param message A message associated to the error that occurred
     */
    public void loadErrorMessage(String message){
        binding.swipeRefresh.setRefreshing(false);
        FragmentExceptionDisplayBinding errorView = binding.errorView;
        Button reloadButton = errorView.reloadButton;
        errorView.errorMessage.setText(message);
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).forceReloadAll(null);
            }
        });
        binding.loadingView.getRoot().setVisibility(View.GONE);//Hide loading animation
        errorView.getRoot().setVisibility(View.VISIBLE);
    }

    @Override
    public <T> void receiveResponse(Response<T> response) {
        if(response.exception == null ){ //check we received some results and not an error
            if(response.results.isEmpty()) { loadNoDataMessage(); }
            else {
                loadData(response.results);
            }
        } else {
            try{
                throw response.exception;
            } catch (UnknownHostException e){
                loadErrorMessage("Couldn't connect to: wds.usetopscore.com \n please check you are connected to the internet");
            } catch (InvalidLinkException e) {
                loadErrorMessage("Couldn't find that information \n has the wds website changed?");
            } catch (ParseException e) {
                loadErrorMessage("We encountered a problem while processing this information \n has the wds website changed?");
            } catch (Throwable throwable) {
                loadErrorMessage(response.exception.getMessage());
            }
        }
    }
}
