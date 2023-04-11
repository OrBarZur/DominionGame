/**
 * RequestAdapter is a class that handles all http requests
 * and adds them to a requestQueue until they are executed.
 */
package com.example.dominion_game.classes;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class RequestAdapter {
    private RequestQueue requestQueue;
    private static RequestAdapter instance;
    private static Context ctx;

    /**
     * The constructor
     * @param context A Context which is the application context
     */
    private RequestAdapter(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    /**
     * A function that returns the RequestAdapter that handles the RequestQueue
     * and is synchronized because it can be called from different threads at the same time.
     * @param context A Context which is the application context
     * @return A RequestAdapter which is instance
     */
    public static synchronized RequestAdapter getInstance(Context context) {
        if (instance == null) {
            instance = new RequestAdapter(context);
        }
        return instance;
    }

    /**
     * A function that creates the requestQueue if null and returns it.
     * @return A RequestQueue which is requestQueue
     */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    /**
     * A function that adds the specified request to the request queue.
     * @param req
     */
    public void addToRequestQueue(JsonObjectRequest req) {
        requestQueue.add(req);
    }
}
