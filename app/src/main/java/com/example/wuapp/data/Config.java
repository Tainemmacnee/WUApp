package com.example.wuapp.data;

import android.content.Context;

import com.example.wuapp.model.Event;
import com.example.wuapp.model.UserLoginToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Config {

    private boolean cacheEvents;
    private boolean cacheLogin;

    public Config(boolean cacheEvents, boolean cacheLogin){
        this.cacheEvents = cacheEvents;
        this.cacheLogin = cacheLogin;
    }

    public static Config getDefaultConfig(){
        return new Config(true, true);
    }

    public void saveConfig(Context context){
        File file = new File(context.getFilesDir(), "config.txt");
        if(file.exists()){
            file.delete();
        }

        try (FileOutputStream fout = context.openFileOutput("config.txt", Context.MODE_PRIVATE); ObjectOutputStream oos = new ObjectOutputStream(fout)) {
            oos.writeObject(cacheEvents);
            oos.writeObject(cacheLogin);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Config readConfig(Context context){
        boolean cacheEvents;
        boolean cacheLogin;
        try (FileInputStream fin = context.openFileInput("config.txt"); ObjectInputStream oin = new ObjectInputStream(fin)) {
            cacheEvents = (boolean) oin.readObject();
            cacheLogin = (boolean) oin.readObject();

            return new Config(cacheEvents, cacheLogin);
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

    public boolean getCacheLogin() {
        return this.cacheLogin;
    }

    public void setCacheLogin(boolean isChecked, Context context) {
        this.cacheLogin = isChecked;
        saveConfig(context);
    }
}
