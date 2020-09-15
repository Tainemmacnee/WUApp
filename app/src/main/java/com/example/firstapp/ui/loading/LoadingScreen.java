package com.example.firstapp.ui.loading;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.firstapp.R;

import org.w3c.dom.Text;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class LoadingScreen extends Fragment {

    private Queue<Load> queue = new ArrayDeque<>();
    private Load current;

    public interface loadableActivity{

        public void processResult(Object Result, boolean finished);

    }

    private class Load{
        private String loadingMessage;
        private Future load;
        private loadableActivity activity;

        public Load(String loadingMessage, Future load, loadableActivity activity){
            this.loadingMessage = loadingMessage;
            this.load = load;
            this.activity = activity;
        }

        public String getLoadingMessage() {
            return loadingMessage;
        }

        public Future getLoad() {
            return load;
        }

        public loadableActivity getActivity() {
            return activity;
        }
    }

    public void load(String loadingMessage, Future load, loadableActivity activity){
        if(current == null){
            current = new Load(loadingMessage, load, activity);
            start();
        } else {
            queue.add(new Load(loadingMessage, load, activity));
        }
    }

    private void start(){
        //updateUI(current);
        Handler handler = new Handler();
        int delay = 500; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                //do something
                if(current.getLoad().isDone()){
                    try {
                        if(queue.isEmpty()){ //finished loading
                            current.getActivity().processResult(current.getLoad().get(), true);
                        } else { //send result and get next load
                            current.getActivity().processResult(current.getLoad().get(), false);
                            startNextLoad();
                        }
                    } catch (Exception e) {
                        current.getActivity().processResult(null, true);
                    }
                } else {
                    handler.postDelayed(this, delay);
                }
            }
        }, delay);
    }

    private void startNextLoad(){
        updateUI(queue.peek());
        current = queue.poll();
        start();
    }

    private void updateUI(Load newLoad){
        View view = getView();
        TextView text = view.findViewById(R.id.loading_text);
        text.setText(newLoad.getLoadingMessage());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_loading_screen, container, false);
        TextView message = view.findViewById(R.id.loading_text);
        message.setText(current.getLoadingMessage());

        return view;
    }
}