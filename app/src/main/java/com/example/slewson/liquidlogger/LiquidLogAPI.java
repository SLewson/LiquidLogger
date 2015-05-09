package com.example.slewson.liquidlogger;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by slewson on 5/9/15.
 */
public class LiquidLogAPI {

    private LiquidLogApiCallback callback = null;

    public LiquidLogAPI(LiquidLogApiCallback callback) {
        this.callback = callback;
    }

    public void getCoffeeStatus() {
        String[] urls = {"http://23.253.213.123:1880/status"};
        APILoader loader = new APILoader();
        loader.execute(urls);
    }

    private String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
            }
            else {
                result = "Did not work!";
            }

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    private class APILoader extends AsyncTask<String, Void, CoffeeStatus> {

        @Override
        protected CoffeeStatus doInBackground(String... params) {
            try {
                //http://23.253.213.123:1880/status
                String url = params[0];
                String response = GET(url);
                JSONObject statusObject = new JSONObject(response);

                CoffeeStatus status = new CoffeeStatus(statusObject.getString("temp"), statusObject.getString("pH"));
                return status;
            }
            catch (JSONException e) {
                return null;
            }
        }

        protected void onPostExecute(CoffeeStatus result) {
            if (result != null) {
                callback.onLiquidLogApiStatusResponse(result);
            }
            else {
                callback.onLiquidLogApiError();
            }
        }
    }

    public class CoffeeStatus {
        private double temp = 0.0;
        private double pH = 0.0;

        public CoffeeStatus(double temp, double pH) {
            this.temp = temp;
            this.pH = pH;
        }

        public CoffeeStatus(String sTemp, String sph) {
            this.temp = Double.valueOf(sTemp);
            this.pH = Double.valueOf(sph);
        }

        public Double getTemp() {
            return temp;
        }

        public Double getpH() {
            return pH;
        }
    }

    public interface LiquidLogApiCallback {
        public void onLiquidLogApiError();
        public void onLiquidLogApiStatusResponse(CoffeeStatus status);
    }
}
