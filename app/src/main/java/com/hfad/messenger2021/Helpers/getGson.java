package com.hfad.messenger2021.Helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class getGson {
    private static Gson gson;

    public static Gson get(){
        if (gson == null){
            GsonBuilder builder = new GsonBuilder();
            gson = builder.create();
        }
        return gson;
    }
}
