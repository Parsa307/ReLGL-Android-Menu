package com.relgl.modmenu;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private static SharedPreferences sharedPreferences;
    private static Preferences prefsInstance;
    public static boolean loadPref;

    private static final String DEFAULT_STRING_VALUE = "";
    private static final int DEFAULT_INT_VALUE = 0; //-1

    public static native void Changes(Context con, int fNum, String fName, int i, boolean bool, String str);

    public static void changeFeatureInt(Context context, String featureName, int featureNum, int value) {
        Preferences.with(context).writeInt(featureNum, value);
        Changes(context, featureNum, featureName, value, false, null);
    }

    public static void changeFeatureString(Context context, String featureName, int featureNum, String str) {
        Preferences.with(context).writeString(featureNum, str);
        Changes(context, featureNum, featureName, 0, false, str);
    }

    public static void changeFeatureBool(Context context, String featureName, int featureNum, boolean bool) {
        Preferences.with(context).writeBoolean(featureNum, bool);
        Changes(context, featureNum, featureName, 0, bool, null);
    }

    public static int loadPrefInt(Context context, String featureName, int featureNum) {
        if (loadPref) {
            int i = Preferences.with(context).readInt(featureNum);
            Changes(context, featureNum, featureName, i, false, null);
            return i;
        }
        return 0;
    }

    public static boolean loadPrefBool(Context context, String featureName, int featureNum, boolean bDef) {
        boolean bool = Preferences.with(context).readBoolean(featureNum, bDef);
        if (featureNum == -1) {
            loadPref = bool;
        }
        if (loadPref || featureNum < 0) {
            bDef = bool;
        }

        Changes(context, featureNum, featureName, 0, bDef, null);
        return bDef;
    }

    public static String loadPrefString(Context context, String featureName, int featureNum) {
        if (loadPref || featureNum <= 0) {
            String str = Preferences.with(context).readString(featureNum);
            Changes(context, featureNum, featureName, 0, false, str);
            return str;
        }
        return "";
    }

    private Preferences(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(
                context.getPackageName() + "_preferences",
                Context.MODE_PRIVATE
        );
    }

    private Preferences(Context context, String preferencesName) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(
                preferencesName,
                Context.MODE_PRIVATE
        );
    }

    /**
     * @return Returns a 'Preferences' instance
     */
    public static Preferences with(Context context) {
        if (prefsInstance == null) {
            prefsInstance = new Preferences(context);
        }
        return prefsInstance;
    }

    /**
     * @return Returns a 'Preferences' instance
     */
    public static Preferences with(Context context, boolean forceInstantiation) {
        if (forceInstantiation) {
            prefsInstance = new Preferences(context);
        }
        return prefsInstance;
    }

    /**
     * @return Returns a 'Preferences' instance
     */
    public static Preferences with(Context context, String preferencesName) {
        if (prefsInstance == null) {
            prefsInstance = new Preferences(context, preferencesName);
        }
        return prefsInstance;
    }

    /**
     * @return Returns a 'Preferences' instance
     */
    public static Preferences with(Context context, String preferencesName,
                                   boolean forceInstantiation) {
        if (forceInstantiation) {
            prefsInstance = new Preferences(context, preferencesName);
        }
        return prefsInstance;
    }

    // String related methods

    /**
     * @return Returns the stored value of 'what'
     */
    public String readString(int what) {
        try {
            return sharedPreferences.getString(String.valueOf(what), DEFAULT_STRING_VALUE);
        } catch (java.lang.ClassCastException ex) {
            return "";
        }
    }

    public void writeString(int where, String what) {
        sharedPreferences.edit().putString(String.valueOf(where), what).apply();
    }

    // int related methods

    /**
     * @return Returns the stored value of 'what'
     */
    public int readInt(int what) {
        try {
            return sharedPreferences.getInt(String.valueOf(what), DEFAULT_INT_VALUE);
        } catch (java.lang.ClassCastException ex) {
            return 0;
        }
    }

    /**
     */
    public void writeInt(int where, int what) {
        sharedPreferences.edit().putInt(String.valueOf(where), what).apply();
    }

    // boolean related methods

    /**
     * @return Returns the stored value of 'what'
     */
    public boolean readBoolean(int what, boolean defaultBoolean) {
        try {
            return sharedPreferences.getBoolean(String.valueOf(what), defaultBoolean);
        } catch (java.lang.ClassCastException ex) {
            return defaultBoolean;
        }
    }

    /**
     */
    public void writeBoolean(int where, boolean what) {
        sharedPreferences.edit().putBoolean(String.valueOf(where), what).apply();
    }

    /**
     * Clear all the preferences
     */
    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}