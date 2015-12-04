package be.ti.groupe2.projetintegration;

import android.app.Application;
import android.content.Context;

public class VariableGlobale extends Application{

    private int iDUser;
    private String listEvent;
    private String listEventParticip;
    private static Context context;
    private Event e;

    @Override
    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
        iDUser = 0;
        listEvent = null;
        e = null;
    }

    public static Context getContext(){
        return context;
    }

    public int getiDUser() {
        return iDUser;
    }

    public void setiDUser(int iDUser) {
        this.iDUser = iDUser;
    }

    public String getlistEvent() {
        return listEvent;
    }

    public void setListEvent(String listEvent) {
        this.listEvent = listEvent;
    }

    public String getlistEventParticip() {
        return listEventParticip;
    }

    public void setListEventParticip(String listEvent) {
        this.listEventParticip = listEvent;
    }

    public Event getEvent() {
        return e;
    }

    public void setEvent(Event e) {
        this.e = e;
    }
}

