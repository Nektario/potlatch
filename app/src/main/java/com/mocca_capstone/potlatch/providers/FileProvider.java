package com.mocca_capstone.potlatch.providers;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGD;

/**
 * Created by nektario on 10/16/2014.
 */
public class FileProvider<T> implements StorageProvider<T> {
    private static final String TAG = "FileProvider";
    private File mFileFullPath;
    private final Gson mGson = new Gson();
    private final Type mDataType;// = new TypeToken<List<T>>() {}.getType();


    public FileProvider(Context context, String fileName, Type typeToken) {
        mFileFullPath = new File(context.getFilesDir() + File.separator + fileName);
        mDataType = typeToken;
    }

    public List<T> load() {
        List<T> items = new ArrayList<T>();
        if (mFileFullPath.canRead()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(mFileFullPath)));
                items = mGson.fromJson(reader, mDataType);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        for (Object u: items) {
            T data = (T) u;
            LOGD(TAG, "Loaded: " + u.toString());
        }

        return items;
    }

    @Override
    public void save(List<T> items) {
        String json = mGson.toJson(items, mDataType);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mFileFullPath), "UTF-8"));
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
