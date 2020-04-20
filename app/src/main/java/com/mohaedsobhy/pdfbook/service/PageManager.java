package com.mohaedsobhy.pdfbook.service;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class PageManager {

    private Context ctx;
    private SharedPreferences settingPrefs;

    public PageManager(Context ctx) {
        this.ctx = ctx;
    }

    public void storePage(String page) {
        settingPrefs = ctx.getSharedPreferences("ConfigData", MODE_PRIVATE);
        SharedPreferences.Editor ed = settingPrefs.edit();
        ed.putString("AppPage", page);
        ed.apply();
    }

    public String getPage() {
        String APP_PAGE;
        settingPrefs = ctx.getSharedPreferences("ConfigData", MODE_PRIVATE);
        String page = settingPrefs.getString("AppPage", "1");
        if (!settingPrefs.getAll().isEmpty()) {
            APP_PAGE = page;
        } else {
            APP_PAGE = "1";
        }
        return APP_PAGE;
    }
}