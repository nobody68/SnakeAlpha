package com.moonstub.training.app.snakealpha.framework;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GameIO {
    Context mContext;
    AssetManager mAssetManager;
    String mPath;

    public GameIO(Context context){
        mContext = context;
        mAssetManager = context.getAssets();
        mPath = Environment.getExternalStorageDirectory().
                getAbsolutePath() + File.separator;
    }

    public InputStream readAsset(String file) throws IOException {
        return mAssetManager.open(file);
    }

    public InputStream readFile(String file) throws IOException {
        return new FileInputStream(mPath + file);
    }

    public InputStream readLocalFile(String file) throws IOException {
        return new FileInputStream(file);
    }

    public OutputStream writeFile(String file) throws IOException{
        //TODO this is were you write a file for like a high score
        return new FileOutputStream(mPath + file);
    }

    public SharedPreferences getSharedPref(){
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }



}
