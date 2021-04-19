package com.example.wuapp.activities.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.wuapp.databinding.DisplayFragmentBinding;
import com.example.wuapp.databinding.FragmentExceptionDisplayBinding;
import com.example.wuapp.datamanagers.DataReceiver;
import com.example.wuapp.exceptions.InvalidLinkException;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.List;

public abstract class DisplayFragment<T> extends Fragment implements DataReceiver {

    protected DisplayFragmentBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = DisplayFragmentBinding.inflate(inflater);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return binding.getRoot();
    }

    public <T> void loadData(List data){}

    public void loadNoDataMessage(){
        binding.loadingView.getRoot().setVisibility(View.GONE);//Hide loading animation
        binding.infoMessage.setVisibility(View.VISIBLE);
    }

    public void loadErrorMessage(String message){
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
