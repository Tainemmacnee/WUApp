package com.example.wuapp.data;

import android.content.Context;

import com.example.wuapp.model.Event;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Config {

    private boolean cacheEvents;
    private int cacheEventsDuration;

    public Config(boolean cacheEvents, int cacheEventsDuration){
        this.cacheEvents = cacheEvents;
        this.cacheEventsDuration = cacheEventsDuration;
    }

    public static Config getDefaultConfig(){
        return new Config(true, 30);
    }

    public void saveConfig(Context context){
        File file = new File(context.getFilesDir(), "config.txt");
        if(file.exists()){
            file.delete();
        }

        try (FileOutputStream fout = context.openFileOutput("config.txt", Context.MODE_PRIVATE); ObjectOutputStream oos = new ObjectOutputStream(fout)) {
            oos.writeObject(cacheEvents);
            oos.writeObject(cacheEventsDuration);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Config readConfig(Context context){
        boolean cacheEvents;
        int cacheEventsDuration;
        try (FileInputStream fin = context.openFileInput("config.txt"); ObjectInputStream oin = new ObjectInputStream(fin)) {
            cacheEvents = (boolean) oin.readObject();
            cacheEventsDuration = (int) oin.readObject();

            return new Config(cacheEvents, cacheEventsDuration);
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (ClassNotFoundException classNotFoundException) {
            classNotFoundException.printStackTrace();
        }

        return Config.getDefaultConfig();
    }

    public boolean getCacheEvents() {
        return this.cacheEvents;
    }

    public void setCacheEvents(boolean cacheEvents, Context context) {
        this.cacheEvents = cacheEvents;
        saveConfig(context);
    }

    public int getCacheEventsDuration() {
        return cacheEventsDuration;
    }

    public void setCacheEventsDuration(int cacheEventsDuration, Context context) {
        this.cacheEventsDuration = cacheEventsDuration;
        saveConfig(context);
    }
}
