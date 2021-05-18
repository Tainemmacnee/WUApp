package com.macneet.wuapp.datamanagers;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ConfigManager {

    private static ConfigManager configManager;

    private boolean cacheEvents;
    private boolean cacheLogin;
    private Context context;

    public ConfigManager(boolean cacheEvents, boolean cacheLogin, Context context){
        this.cacheEvents = cacheEvents;
        this.cacheLogin = cacheLogin;
        this.context = context;
    }

    public static void initialise(Context context) {
        if(configManager == null) {
            configManager = readConfig(context);
        }
    }

    public static ConfigManager getInstance(){ return configManager; }

    private static ConfigManager getDefaultConfig(Context context){
        return new ConfigManager(true, true, context);
    }

    private static ConfigManager readConfig(Context context){
        boolean cacheEvents;
        boolean cacheLogin;
        try (FileInputStream fin = context.openFileInput("config.txt"); ObjectInputStream oin = new ObjectInputStream(fin)) {
            cacheEvents = (boolean) oin.readObject();
            cacheLogin = (boolean) oin.readObject();

            return new ConfigManager(cacheEvents, cacheLogin, context);
        } catch (FileNotFoundException fileNotFoundException) {
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (ClassNotFoundException classNotFoundException) {
            classNotFoundException.printStackTrace();
        }

        return ConfigManager.getDefaultConfig(context);
    }

    private void saveConfig(Context context){
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
