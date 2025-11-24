#include <list>
#include <vector>
#include <cstring>
#include <pthread.h>
#include <thread>
#include <cstring>
#include <jni.h>
#include <unistd.h>
#include <fstream>
#include <iostream>
#include <dlfcn.h>
#include "Includes/Logger.hpp"
#include "Includes/obfuscate.h"
#include "Includes/Utils.cpp"
#include "Includes/MonoInterop.hpp"
#include "KittyMemory/MemoryBackup.hpp"
#include "KittyMemory/KittyInclude.hpp"
#include "Dobby/dobby.h"
#include "Menu/Setup.cpp"

#pragma clang diagnostic push
#pragma ide diagnostic ignored "OCDFAInspection"

//Target lib here
#define targetLibName OBFUSCATE("libil2cpp.so")
ElfScanner g_il2cppELF;
uintptr_t il2cppBase;

bool RemoveAllShadows = false, MaximumLightRadius = false, LongKillDistance= false, CanMoveInVent = false, UnlimitedDurationForShapeshifter = false, NoCooldownForShapeshifter = false, UnlimitedVentDurationForEngineers = false, NoVentCooldownForEngineers = false, UnlimitedDurationForPhantom = false, NoCooldownForPhantom = false, UnlockAllItems = false, NoAds = false, FreeChat = false, AllowAllCharacters = false;
std::string PlayerName;
float PlayerSpeed = 1;
int PlayerLevel;

/*
bool feature1, feature2, featureHookToggle, Health;
int sliderValue = 1, level = 0;
void *instanceBtn;*/

// Hooking examples. Assuming you know how to write hook
float (*old_maximumlightradius)(void *instance);
float maximumlightradius(void *instance) {
    if (instance != nullptr && MaximumLightRadius) {
        return 2000;
    }
    return old_maximumlightradius(instance);
}

bool (*old_removeallshadows)(void *instance);
bool removeallshadows(void *instance) {
    if (instance != nullptr && RemoveAllShadows) {
        return true;
    }
    return old_removeallshadows(instance);
}

float (*old_longkilldistance)(void *instance);
float longkilldistance(void *instance) {
    if (instance != nullptr && LongKillDistance) {
        return 2000;
    }
    return old_longkilldistance(instance);
}

bool (*old_canmoveinvent)(void *instance);
bool canmoveinvent(void *instance) {
    if (instance != nullptr && CanMoveInVent) {
        return true;
    }
    return old_canmoveinvent(instance);
}

float (*old_unlimiteddurationforshapeshifter)(void *instance);
float unlimiteddurationforshapeshifter(void *instance) {
    if (instance != nullptr && UnlimitedDurationForShapeshifter) {
        return 2000;
    }
    return old_unlimiteddurationforshapeshifter(instance);
}

float (*old_nocooldownforshapeshifter)(void *instance);
float nocooldownforshapeshifter(void *instance) {
    if (instance != nullptr && NoCooldownForShapeshifter) {
        return 0;
    }
    return old_nocooldownforshapeshifter(instance);
}

float (*old_unlimitedventdurationforengineers)(void *instance);
float unlimitedventdurationforengineers(void *instance) {
    if (instance != nullptr && UnlimitedVentDurationForEngineers) {
        return 2000;
    }
    return old_unlimitedventdurationforengineers(instance);
}

float (*old_noventcooldownforengineers)(void *instance);
float noventcooldownforengineers(void *instance) {
    if (instance != nullptr && NoVentCooldownForEngineers) {
        return 0;
    }
    return old_noventcooldownforengineers(instance);
}

float (*old_unlimiteddurationforphantom)(void *instance);
float unlimiteddurationforphantom(void *instance) {
    if (instance != nullptr && UnlimitedDurationForPhantom) {
        return 2000;
    }
    return old_unlimiteddurationforphantom(instance);
}

float (*old_nocooldownforphantom)(void *instance);
float nocooldownforphantom(void *instance) {
    if (instance != nullptr && NoCooldownForPhantom) {
        return 0;
    }
    return old_nocooldownforphantom(instance);
}

bool (*old_unlockallitems)(void *instance);
bool unlockallitems(void *instance) {
    if (instance != nullptr && UnlockAllItems) {
        return true;
    }
    return old_unlockallitems(instance);
}

bool (*old_noads)(void *instance);
bool noads(void *instance) {
    if (instance != nullptr && NoAds) {
        return true;
    }
    return old_noads(instance);
}

bool (*old_freechat)(void *value);
bool freechat(void *instance) {
    if (instance != nullptr && FreeChat) {
        return true;
    }
    return old_freechat(instance);
}

bool (*old_allowallcharacters)(void *instance);
bool allowallcharacters(void *instance) {
    if (instance != nullptr && AllowAllCharacters) {
        return true;
    }
    return old_allowallcharacters(instance);
}

float (*old_playerspeed)(void *instance);
float playerspeed(void *instance) {
    if (instance != nullptr && PlayerSpeed > 1) {
        return (float) PlayerSpeed;
    }
    return old_playerspeed(instance);
}

monoString* (*old_playername)(void *instance);
monoString* playername(void *instance) {
    if (instance != nullptr && !PlayerName.empty()) {
        return CreateMonoString(PlayerName.c_str());
    }
    return old_playername(instance);
}

monoString* (*old_playerlevel)(void *instance);
monoString* playerlevel(void *instance) {
    if (instance != nullptr && PlayerLevel > 0) {
        return CreateMonoString(std::to_string(PlayerLevel).c_str());
    }
    return old_playerlevel(instance);
}

/*
void (*AddMoneyExample)(void *instance, int amount);

bool (*old_get_BoolExample)(void *instance);
bool get_BoolExample(void *instance) {
    if (instance != nullptr && featureHookToggle) {
        return true;
    }
    return old_get_BoolExample(instance);
}

float (*old_get_FloatExample)(void *instance);
float get_FloatExample(void *instance) {
    if (instance != nullptr && sliderValue > 1) {
        return (float) sliderValue;
    }
    return old_get_FloatExample(instance);
}

int (*old_Level)(void *instance);
int Level(void *instance) {
    if (instance != nullptr && level) {
        return (int) level;
    }
    return old_Level(instance);
}

void (*old_FunctionExample)(void *instance);
void FunctionExample(void *instance) {
    instanceBtn = instance;
    if (instance != nullptr) {
        if (Health) {
            *(int *) ((uint64_t) instance + 0x48) = 999;
        }
    }
    return old_FunctionExample(instance);
}*/

// we will run our hacks in a new thread so our while loop doesn't block process main thread
void *hack_thread(void *) {
    LOGI(OBFUSCATE("pthread created"));

    //Check if target lib is loaded
    do {
        sleep(1);
        g_il2cppELF = ElfScanner::createWithPath(std::string(targetLibName));
    } while (!g_il2cppELF.isValid());

    //If lib is valid, mark it as loaded
    if (g_il2cppELF.isValid()) {
        libLoaded = true;
    }

    LOGI(OBFUSCATE("%s has been loaded"), (const char *) targetLibName);


    // Hook example. Comment out if you don't use hook
    // Strings in macros are automatically obfuscated. No need to obfuscate!

    //DobbyHook((void *)(address + 0x1288ABC), (void *)example, (void **)&old_example);

    il2cppBase = g_il2cppELF.base();

#ifdef __aarch64__ // arm64-v8a
    DobbyHook((void *)(il2cppBase + 0x1288ABC), (void *)maximumlightradius, (void **)&old_maximumlightradius);
    DobbyHook((void *)(il2cppBase + 0x313AA84), (void *)removeallshadows, (void **)&old_removeallshadows);
    DobbyHook((void *)(il2cppBase + 0x1A26A00), (void *)longkilldistance, (void **)&old_longkilldistance);
    DobbyHook((void *)(il2cppBase + 0x123F570), (void *)canmoveinvent, (void **)&old_canmoveinvent);
    DobbyHook((void *)(il2cppBase + 0x1A25E38), (void *)unlimiteddurationforshapeshifter, (void **)&old_unlimiteddurationforshapeshifter);
    DobbyHook((void *)(il2cppBase + 0x1A25F20), (void *)nocooldownforshapeshifter, (void **)&old_nocooldownforshapeshifter);
    DobbyHook((void *)(il2cppBase + 0x1A25C68), (void *)unlimitedventdurationforengineers, (void **)&old_unlimitedventdurationforengineers);
    DobbyHook((void *)(il2cppBase + 0x1A25B80), (void *)noventcooldownforengineers, (void **)&old_noventcooldownforengineers);
    DobbyHook((void *)(il2cppBase + 0x1A263A8), (void *)unlimiteddurationforphantom, (void **)&old_unlimiteddurationforphantom);
    DobbyHook((void *)(il2cppBase + 0x1A262C0), (void *)nocooldownforphantom, (void **)&old_nocooldownforphantom);
    DobbyHook((void *)(il2cppBase + 0x16D2318), (void *)unlockallitems, (void **)&old_unlockallitems);
    DobbyHook((void *)(il2cppBase + 0x137ADEC), (void *)noads, (void **)&old_noads);
    DobbyHook((void *)(il2cppBase + 0x188A070), (void *)freechat, (void **)&old_freechat);
    DobbyHook((void *)(il2cppBase + 0x1A8F1AC), (void *)allowallcharacters, (void **)&old_allowallcharacters);
    DobbyHook((void *)(il2cppBase + 0x16CE98C), (void *)playerspeed, (void **)&old_playerspeed);
    DobbyHook((void *)(il2cppBase + 0x16C53A4), (void *)playername, (void **)&old_playername);
    DobbyHook((void *)(il2cppBase + 0x1935CA4), (void *)playerlevel, (void **)&old_playerlevel);

#elif defined(__arm__) // armeabi-v7a
    DobbyHook((void *)(il2cppBase + 0xB691BC), (void *)maximumlightradius, (void **)&old_maximumlightradius);
    DobbyHook((void *)(il2cppBase + 0x2FC6F30), (void *)removeallshadows, (void **)&old_removeallshadows);
    DobbyHook((void *)(il2cppBase + 0x147C2E0), (void *)longkilldistance, (void **)&old_longkilldistance);
    DobbyHook((void *)(il2cppBase + 0xB0EFF0), (void *)canmoveinvent, (void **)&old_canmoveinvent);
    DobbyHook((void *)(il2cppBase + 0x147B580), (void *)unlimiteddurationforshapeshifter, (void **)&old_unlimiteddurationforshapeshifter);
    DobbyHook((void *)(il2cppBase + 0x147B688), (void *)nocooldownforshapeshifter, (void **)&old_nocooldownforshapeshifter);
    DobbyHook((void *)(il2cppBase + 0x147B370), (void *)unlimitedventdurationforengineers, (void **)&old_unlimitedventdurationforengineers);
    DobbyHook((void *)(il2cppBase + 0x147B268), (void *)noventcooldownforengineers, (void **)&old_noventcooldownforengineers);
    DobbyHook((void *)(il2cppBase + 0x147BBAC), (void *)unlimiteddurationforphantom, (void **)&old_unlimiteddurationforphantom);
    DobbyHook((void *)(il2cppBase + 0x147BAA4), (void *)nocooldownforphantom, (void **)&old_nocooldownforphantom);
    DobbyHook((void *)(il2cppBase + 0x1084514), (void *)unlockallitems, (void **)&old_unlockallitems);
    DobbyHook((void *)(il2cppBase + 0xC8A728), (void *)noads, (void **)&old_noads);
    DobbyHook((void *)(il2cppBase + 0x1290988), (void *)freechat, (void **)&old_freechat);
    DobbyHook((void *)(il2cppBase + 0x1501858), (void *)allowallcharacters, (void **)&old_allowallcharacters);
    DobbyHook((void *)(il2cppBase + 0x107F65C), (void *)playerspeed, (void **)&old_playerspeed);
    DobbyHook((void *)(il2cppBase + 0x10735AC), (void *)playername, (void **)&old_playername);
    DobbyHook((void *)(il2cppBase + 0x1358AD4), (void *)playerlevel, (void **)&old_playerlevel);
#endif

    LOGI(OBFUSCATE("Done"));

    //Anti-leech
    /*if (!iconValid || !initValid || !settingsValid) {
        //Bad function to make it crash
        sleep(5);
        int *p = 0;
        *p = 0;
    }*/

    return nullptr;
}

// Do not change or translate the first text unless you know what you are doing
// Assigning feature numbers is optional. Without it, it will automatically count for you, starting from 0
// Assigned feature numbers can be like any numbers 1,3,200,10... instead in order 0,1,2,3,4,5...
// ButtonLink, Category, RichTextView and RichWebView is not counted. They can't have feature number assigned
// Toggle, ButtonOnOff and Checkbox can be switched on by default, if you add True_. Example: CheckBox_True_The Check Box
// To learn HTML, go to this page: https://www.w3schools.com/

jobjectArray GetFeatureList(JNIEnv *env, jobject context) {
    jobjectArray ret;

    const char *features[] = {
            OBFUSCATE("Category_Player Mods"), //Not counted
            OBFUSCATE("1_Toggle_Maximum Light Radius"),
            OBFUSCATE("2_Toggle_Remove All Shadows"),
    
    /*
            OBFUSCATE("Category_The Category"), //Not counted
            OBFUSCATE("Toggle_The toggle"),
            OBFUSCATE(
                    "100_Toggle_True_The toggle 2"), //This one have feature number assigned, and switched on by default
            OBFUSCATE("110_Toggle_The toggle 3"), //This one too
            OBFUSCATE("SeekBar_The slider_1_100"),
            OBFUSCATE("Spinner_The spinner_Items 1,Items 2,Items 3"),
            OBFUSCATE("Button_The button"),
            OBFUSCATE("ButtonLink_The button with link_https://www.youtube.com/"), //Not counted
            OBFUSCATE("ButtonOnOff_The On/Off button"),
            OBFUSCATE("CheckBox_The Check Box"),
            OBFUSCATE("InputValue_Input number"),
            OBFUSCATE("InputValue_1000_Input number 2"), //Max value
            OBFUSCATE("InputText_Input text"),
            OBFUSCATE("RadioButton_Radio buttons_OFF,Mod 1,Mod 2,Mod 3"),

            //Create new collapse
            OBFUSCATE("Collapse_Collapse 1"),
            OBFUSCATE("CollapseAdd_Toggle_The toggle"),
            OBFUSCATE("CollapseAdd_Toggle_The toggle"),
            OBFUSCATE("123_CollapseAdd_Toggle_The toggle"),
            OBFUSCATE("122_CollapseAdd_CheckBox_Check box"),
            OBFUSCATE("CollapseAdd_Button_The button"),

            //Create new collapse again
            OBFUSCATE("Collapse_Collapse 2_True"),
            OBFUSCATE("CollapseAdd_SeekBar_The slider_1_100"),
            OBFUSCATE("CollapseAdd_InputValue_Input number"),

            OBFUSCATE("RichTextView_This is text view, not fully HTML."
                      "<b>Bold</b> <i>italic</i> <u>underline</u>"
                      "<br />New line <font color='red'>Support colors</font>"
                      "<br/><big>bigger Text</big>"),
            OBFUSCATE("RichWebView_<html><head><style>body{color: white;}</style></head><body>"
                      "This is WebView, with REAL HTML support!"
                      "<div style=\"background-color: darkblue; text-align: center;\">Support CSS</div>"
                      "<marquee style=\"color: green; font-weight:bold;\" direction=\"left\" scrollamount=\"5\" behavior=\"scroll\">This is <u>scrollable</u> text</marquee>"
                      "</body></html>")*/
    };

    //Now you dont have to manually update the number everytime;
    int Total_Feature = (sizeof features / sizeof features[0]);
    ret = (jobjectArray)
            env->NewObjectArray(Total_Feature, env->FindClass(OBFUSCATE("java/lang/String")),
                                env->NewStringUTF(""));

    for (int i = 0; i < Total_Feature; i++)
        env->SetObjectArrayElement(ret, i, env->NewStringUTF(features[i]));

    return (ret);
}

jobjectArray GetFeatureList2(JNIEnv *env, jobject context) {
    jobjectArray ret;

    const char *features[] = {
            OBFUSCATE("Category_Player Abilities"), //Not counted
            OBFUSCATE("3_Toggle_Long Kill Distance"),
            OBFUSCATE("4_Toggle_Can Move In Vent"),
            OBFUSCATE("5_Toggle_Unlimited Duration For Shapeshifter"),
            OBFUSCATE("6_Toggle_No Cooldown For Shapeshifter"),
            OBFUSCATE("7_Toggle_Unlimited Vent Duration For Engineers"),
            OBFUSCATE("8_Toggle_No Vent Cooldown For Engineers"),
            OBFUSCATE("9_Toggle_Unlimited Duration For Phantom"),
            OBFUSCATE("10_Toggle_No Phantom Cooldown"),
            OBFUSCATE("11_SeekBar_Player Speed_1_100"),
    
    /*
            OBFUSCATE("Category_The Category 2"), //Not counted
            OBFUSCATE("Toggle_The toggle"),
            OBFUSCATE(
                    "100_Toggle_True_The toggle 2"), //This one have feature number assigned, and switched on by default
            OBFUSCATE("110_Toggle_The toggle 3"), //This one too
            OBFUSCATE("SeekBar_The slider_1_100"),
            OBFUSCATE("Spinner_The spinner_Items 1,Items 2,Items 3"),
            OBFUSCATE("Button_The button"),
            OBFUSCATE("ButtonLink_The button with link_https://www.youtube.com/"), //Not counted
            OBFUSCATE("ButtonOnOff_The On/Off button"),
            OBFUSCATE("CheckBox_The Check Box"),
            OBFUSCATE("InputValue_Input number"),
            OBFUSCATE("InputValue_1000_Input number 2"), //Max value
            OBFUSCATE("InputText_Input text"),
            OBFUSCATE("RadioButton_Radio buttons_OFF,Mod 1,Mod 2,Mod 3"),

            //Create new collapse
            OBFUSCATE("Collapse_Collapse 1"),
            OBFUSCATE("CollapseAdd_Toggle_The toggle"),
            OBFUSCATE("CollapseAdd_Toggle_The toggle"),
            OBFUSCATE("123_CollapseAdd_Toggle_The toggle"),
            OBFUSCATE("122_CollapseAdd_CheckBox_Check box"),
            OBFUSCATE("CollapseAdd_Button_The button"),

            //Create new collapse again
            OBFUSCATE("Collapse_Collapse 2_True"),
            OBFUSCATE("CollapseAdd_SeekBar_The slider_1_100"),
            OBFUSCATE("CollapseAdd_InputValue_Input number"),

            OBFUSCATE("RichTextView_This is text view, not fully HTML."
                      "<b>Bold</b> <i>italic</i> <u>underline</u>"
                      "<br />New line <font color='red'>Support colors</font>"
                      "<br/><big>bigger Text</big>"),
            OBFUSCATE("RichWebView_<html><head><style>body{color: white;}</style></head><body>"
                      "This is WebView, with REAL HTML support!"
                      "<div style=\"background-color: darkblue; text-align: center;\">Support CSS</div>"
                      "<marquee style=\"color: green; font-weight:bold;\" direction=\"left\" scrollamount=\"5\" behavior=\"scroll\">This is <u>scrollable</u> text</marquee>"
                      "</body></html>")*/
    };

    //Now you dont have to manually update the number everytime;
    int Total_Feature = (sizeof features / sizeof features[0]);
    ret = (jobjectArray)
            env->NewObjectArray(Total_Feature, env->FindClass(OBFUSCATE("java/lang/String")),
                                env->NewStringUTF(""));

    for (int i = 0; i < Total_Feature; i++)
        env->SetObjectArrayElement(ret, i, env->NewStringUTF(features[i]));

    return (ret);
}

jobjectArray GetFeatureList3(JNIEnv *env, jobject context) {
    jobjectArray ret;

    const char *features[] = {
            OBFUSCATE("Category_Account Mods"), //Not counted
            OBFUSCATE("12_Toggle_Unlock All Items"),
            OBFUSCATE("13_Toggle_No Ads"),
            OBFUSCATE("14_Toggle_Free Chat"),
            OBFUSCATE("15_Toggle_Allow All Characters"),
            OBFUSCATE("16_InputText_Player Name"),
            OBFUSCATE("17_InputValue_Player Level"),

    /*
            OBFUSCATE("Category_The Category 3"), //Not counted
            OBFUSCATE("Toggle_The toggle"),
            OBFUSCATE(
                    "100_Toggle_True_The toggle 2"), //This one have feature number assigned, and switched on by default
            OBFUSCATE("110_Toggle_The toggle 3"), //This one too
            OBFUSCATE("SeekBar_The slider_1_100"),
            OBFUSCATE("Spinner_The spinner_Items 1,Items 2,Items 3"),
            OBFUSCATE("Button_The button"),
            OBFUSCATE("ButtonLink_The button with link_https://www.youtube.com/"), //Not counted
            OBFUSCATE("ButtonOnOff_The On/Off button"),
            OBFUSCATE("CheckBox_The Check Box"),
            OBFUSCATE("InputValue_Input number"),
            OBFUSCATE("InputValue_1000_Input number 2"), //Max value
            OBFUSCATE("InputText_Input text"),
            OBFUSCATE("RadioButton_Radio buttons_OFF,Mod 1,Mod 2,Mod 3"),

            //Create new collapse
            OBFUSCATE("Collapse_Collapse 1"),
            OBFUSCATE("CollapseAdd_Toggle_The toggle"),
            OBFUSCATE("CollapseAdd_Toggle_The toggle"),
            OBFUSCATE("123_CollapseAdd_Toggle_The toggle"),
            OBFUSCATE("122_CollapseAdd_CheckBox_Check box"),
            OBFUSCATE("CollapseAdd_Button_The button"),

            //Create new collapse again
            OBFUSCATE("Collapse_Collapse 2_True"),
            OBFUSCATE("CollapseAdd_SeekBar_The slider_1_100"),
            OBFUSCATE("CollapseAdd_InputValue_Input number"),

            OBFUSCATE("RichTextView_This is text view, not fully HTML."
                      "<b>Bold</b> <i>italic</i> <u>underline</u>"
                      "<br />New line <font color='red'>Support colors</font>"
                      "<br/><big>bigger Text</big>"),
            OBFUSCATE("RichWebView_<html><head><style>body{color: white;}</style></head><body>"
                      "This is WebView, with REAL HTML support!"
                      "<div style=\"background-color: darkblue; text-align: center;\">Support CSS</div>"
                      "<marquee style=\"color: green; font-weight:bold;\" direction=\"left\" scrollamount=\"5\" behavior=\"scroll\">This is <u>scrollable</u> text</marquee>"
                      "</body></html>")*/
    };

    //Now you dont have to manually update the number everytime;
    int Total_Feature = (sizeof features / sizeof features[0]);
    ret = (jobjectArray)
            env->NewObjectArray(Total_Feature, env->FindClass(OBFUSCATE("java/lang/String")),
                                env->NewStringUTF(""));

    for (int i = 0; i < Total_Feature; i++)
        env->SetObjectArrayElement(ret, i, env->NewStringUTF(features[i]));

    return (ret);
}

jobjectArray GetFeatureList4(JNIEnv *env, jobject context) {
    jobjectArray ret;

    const char *features[] = {
            OBFUSCATE("Category_ReLGL"), //Not counted
            OBFUSCATE("ButtonLink_ReLGL Project_https://github.com/Parsa307/ReLGL-Android-Menu"), //Not counted

    /*
            OBFUSCATE("Category_The Category 4"), //Not counted
            OBFUSCATE("Toggle_The toggle"),
            OBFUSCATE(
                    "100_Toggle_True_The toggle 2"), //This one have feature number assigned, and switched on by default
            OBFUSCATE("110_Toggle_The toggle 3"), //This one too
            OBFUSCATE("SeekBar_The slider_1_100"),
            OBFUSCATE("Spinner_The spinner_Items 1,Items 2,Items 3"),
            OBFUSCATE("Button_The button"),
            OBFUSCATE("ButtonLink_The button with link_https://www.youtube.com/"), //Not counted
            OBFUSCATE("ButtonOnOff_The On/Off button"),
            OBFUSCATE("CheckBox_The Check Box"),
            OBFUSCATE("InputValue_Input number"),
            OBFUSCATE("InputValue_1000_Input number 2"), //Max value
            OBFUSCATE("InputText_Input text"),
            OBFUSCATE("RadioButton_Radio buttons_OFF,Mod 1,Mod 2,Mod 3"),

            //Create new collapse
            OBFUSCATE("Collapse_Collapse 1"),
            OBFUSCATE("CollapseAdd_Toggle_The toggle"),
            OBFUSCATE("CollapseAdd_Toggle_The toggle"),
            OBFUSCATE("123_CollapseAdd_Toggle_The toggle"),
            OBFUSCATE("122_CollapseAdd_CheckBox_Check box"),
            OBFUSCATE("CollapseAdd_Button_The button"),

            //Create new collapse again
            OBFUSCATE("Collapse_Collapse 2_True"),
            OBFUSCATE("CollapseAdd_SeekBar_The slider_1_100"),
            OBFUSCATE("CollapseAdd_InputValue_Input number"),

            OBFUSCATE("RichTextView_This is text view, not fully HTML."
                      "<b>Bold</b> <i>italic</i> <u>underline</u>"
                      "<br />New line <font color='red'>Support colors</font>"
                      "<br/><big>bigger Text</big>"),
            OBFUSCATE("RichWebView_<html><head><style>body{color: white;}</style></head><body>"
                      "This is WebView, with REAL HTML support!"
                      "<div style=\"background-color: darkblue; text-align: center;\">Support CSS</div>"
                      "<marquee style=\"color: green; font-weight:bold;\" direction=\"left\" scrollamount=\"5\" behavior=\"scroll\">This is <u>scrollable</u> text</marquee>"
                      "</body></html>")*/
    };

    //Now you dont have to manually update the number everytime;
    int Total_Feature = (sizeof features / sizeof features[0]);
    ret = (jobjectArray)
            env->NewObjectArray(Total_Feature, env->FindClass(OBFUSCATE("java/lang/String")),
                                env->NewStringUTF(""));

    for (int i = 0; i < Total_Feature; i++)
        env->SetObjectArrayElement(ret, i, env->NewStringUTF(features[i]));

    return (ret);
}

void Changes(JNIEnv *env, jclass clazz, jobject obj,
                                        jint featNum, jstring featName, jint value,
                                        jboolean boolean, jstring str) {

/*
    LOGD(OBFUSCATE("Feature name: %d - %s | Value: = %d | Bool: = %d | Text: = %s"), featNum,
         env->GetStringUTFChars(featName, 0), value,
         boolean, str != nullptr ? env->GetStringUTFChars(str, 0) : "");*/

    //BE CAREFUL NOT TO ACCIDENTALLY REMOVE break;

    switch (featNum) {
        case 1:
            MaximumLightRadius = boolean;
            break;
        case 2:
            RemoveAllShadows = boolean;
            break;
        case 3:
            LongKillDistance = boolean;
            break;
        case 4:
            CanMoveInVent = boolean;
            break;
        case 5:
            UnlimitedDurationForShapeshifter = boolean;
            break;
        case 6:
            NoCooldownForShapeshifter = boolean;
            break;
        case 7:
            UnlimitedVentDurationForEngineers = boolean;
            break;
        case 8:
            NoVentCooldownForEngineers = boolean;
            break;
        case 9:
            UnlimitedDurationForPhantom = boolean;
            break;
        case 10:
            NoCooldownForPhantom = boolean;
            break;
        case 11:
            PlayerSpeed = static_cast<float>(value);
            break;
        case 12:
            UnlockAllItems = boolean;
            break;
        case 13:
            NoAds = boolean;
            break;
        case 14:
            FreeChat = boolean;
            break;
        case 15:
            AllowAllCharacters = boolean;
            break;
        case 16:
            PlayerName = env->GetStringUTFChars(str, nullptr);
            break;
        case 17:
            PlayerLevel = value;
            break;
        default:
            break;
  
    /*
        case 100:
            break;
        case 1:
            if (value >= 1) {
                sliderValue = value;
            }
            break;
        case 2:
            switch (value) {
                case 0:
                    LOGD(OBFUSCATE("Selected item 1"));
                    break;
                case 1:
                    LOGD(OBFUSCATE("Selected item 2"));
                    break;
                case 2:
                    LOGD(OBFUSCATE("Selected item 3"));
                    break;
            }
            break;
        case 3:
            // Since we have instanceBtn as a field, we can call it out of Update hook function
            if (instanceBtn != nullptr)
                AddMoneyExample(instanceBtn, 999999);
            break;
        case 4:
            break;
        case 5:
            featureHookToggle = boolean;
            break;
        case 6:
            level = value;
            break;
        case 7:
            break;
        case 8:
            break;*/
    }
}

__attribute__((constructor))
void lib_main() {
    // Create a new thread so it does not block the main thread, means the game would not freeze
    pthread_t ptid;
    pthread_create(&ptid, nullptr, hack_thread, nullptr);
}

int RegisterMenu(JNIEnv *env) {
    JNINativeMethod methods[] = {
            {OBFUSCATE("Icon"), OBFUSCATE("()Ljava/lang/String;"), reinterpret_cast<void *>(Icon)},
            {OBFUSCATE("Font"), OBFUSCATE("()Ljava/lang/String;"), reinterpret_cast<void *>(Font)},
            {OBFUSCATE("Background"), OBFUSCATE("()Ljava/lang/String;"), reinterpret_cast<void *>(Background)},
            {OBFUSCATE("IconWebViewData"),  OBFUSCATE("()Ljava/lang/String;"), reinterpret_cast<void *>(IconWebViewData)},
            {OBFUSCATE("IsGameLibLoaded"),  OBFUSCATE("()Z"), reinterpret_cast<void *>(isGameLibLoaded)},
            {OBFUSCATE("Init"),OBFUSCATE("(Landroid/content/Context;Landroid/widget/TextView;Landroid/widget/TextView;Landroid/widget/TextView;Landroid/widget/TextView;Landroid/widget/TextView;Landroid/widget/TextView;Landroid/widget/TextView;)V"),reinterpret_cast<void *>(Init)},
            {OBFUSCATE("SettingsList"),  OBFUSCATE("()[Ljava/lang/String;"), reinterpret_cast<void *>(SettingsList)},
            {OBFUSCATE("GetFeatureList"),  OBFUSCATE("()[Ljava/lang/String;"), reinterpret_cast<void *>(GetFeatureList)},
            {OBFUSCATE("GetFeatureList2"),  OBFUSCATE("()[Ljava/lang/String;"), reinterpret_cast<void *>(GetFeatureList2)},
            {OBFUSCATE("GetFeatureList3"),  OBFUSCATE("()[Ljava/lang/String;"), reinterpret_cast<void *>(GetFeatureList3)},
            {OBFUSCATE("GetFeatureList4"),  OBFUSCATE("()[Ljava/lang/String;"), reinterpret_cast<void *>(GetFeatureList4)},
    };

    jclass clazz = env->FindClass(OBFUSCATE("com/relgl/modmenu/Menu"));
    if (!clazz)
        return JNI_ERR;
    if (env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof(methods[0])) != 0)
        return JNI_ERR;
    return JNI_OK;
}

int RegisterPreferences(JNIEnv *env) {
    JNINativeMethod methods[] = {
            {OBFUSCATE("Changes"), OBFUSCATE("(Landroid/content/Context;ILjava/lang/String;IZLjava/lang/String;)V"), reinterpret_cast<void *>(Changes)},
    };
    jclass clazz = env->FindClass(OBFUSCATE("com/relgl/modmenu/Preferences"));
    if (!clazz)
        return JNI_ERR;
    if (env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof(methods[0])) != 0)
        return JNI_ERR;
    return JNI_OK;
}

int RegisterMain(JNIEnv *env) {
    JNINativeMethod methods[] = {
            {OBFUSCATE("CheckOverlayPermission"), OBFUSCATE("(Landroid/content/Context;)V"), reinterpret_cast<void *>(CheckOverlayPermission)},
    };
    jclass clazz = env->FindClass(OBFUSCATE("com/relgl/modmenu/Main"));
    if (!clazz)
        return JNI_ERR;
    if (env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof(methods[0])) != 0)
        return JNI_ERR;

    return JNI_OK;
}

extern "C"
JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    vm->GetEnv((void **) &env, JNI_VERSION_1_6);
    if (RegisterMenu(env) != 0)
        return JNI_ERR;
    if (RegisterPreferences(env) != 0)
        return JNI_ERR;
    if (RegisterMain(env) != 0)
        return JNI_ERR;
    return JNI_VERSION_1_6;
}

#pragma clang diagnostic pop