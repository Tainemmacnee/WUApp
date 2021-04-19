package com.example.wuapp.datamanagers;

import java.util.List;

/**
 * This interface defines how a class can interact with a data manager instance
 */
public interface DataReceiver {
    public <T> void receiveResponse(Response<T> response);

    /**
     * A response to a request made. If an exception did occurs while processing the request then
     * results will be null
     * @param <T> the data type of the results returned
     */
    class Response<T>{
        public final Request initialRequest;
        public final List<T> results;
        public final Throwable exception;

        public Response(List<T> results, Throwable exception, Request initialRequest) {
            this.results = results;
            this.exception = exception;
            this.initialRequest = initialRequest;
        }
    }

    class Request{

        public final String request;
        public final DataReceiver callback;
        public final String link;

        public Request(DataReceiver callback, String request){
            this.request = request;
            this.callback = callback;
            this.link = null;
        }

        public Request(DataReceiver callback, String request, String link){
            this.request = request;
            this.callback = callback;
            this.link = link;
        }

    }
}