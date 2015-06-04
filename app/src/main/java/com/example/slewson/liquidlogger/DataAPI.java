package com.example.slewson.liquidlogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Marie on 6/3/2015.
 */
public class DataAPI {
    private DataApiCallback callback = null;
    private Double fakeph = 7.0;
    private Double faketemp = 32.0;

    public DataAPI(DataApiCallback callback) {
        this.callback = callback;
    }

    public void getCoffeeStatus() {
        if (fakeph < 0.0 || faketemp > 220.0) {
            callback.onDataApiError("Invalid fake values, lol.");
        } else {
            CoffeeStatus status = new CoffeeStatus(faketemp, fakeph);
            callback.onDataApiStatusResponse(status);
        }

        fakeph -= 0.5;
        faketemp += 2.0;
    }

    public interface DataApiCallback {
        public void onDataApiError(String error);
        public void onDataApiStatusResponse(CoffeeStatus status);
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
}
