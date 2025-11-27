package com.relgl.modmenu;

import android.app.Activity;
import android.content.Context;

public class Main {

    //Load lib
    static {
        // When you change the lib name, change also on Android.mk file
        // Both must have same name
        System.loadLibrary("ReLGL");
    }

    private static native void CheckOverlayPermission(Context context);

    public static void StartWithoutPermission(Context context) {
        if (context instanceof Activity) {
            //Check if context is an Activity.
            Menu menu = new Menu(context);
            menu.SetWindowManagerActivity();
            menu.ShowMenu();
        } else {
            //Anything else, ask for permission
            CheckOverlayPermission(context);
        }
    }

    public static void Start(Context context) {
        CheckOverlayPermission(context);
    }
}
