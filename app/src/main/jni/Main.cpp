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
#include "Includes/Utils.cpp"
#include "Includes/MonoInterop.hpp"
#include "KittyMemory/MemoryBackup.hpp"
#include "KittyMemory/KittyInclude.hpp"
#include "Dobby/dobby.h"
#include "Menu/Setup.cpp"

#pragma clang diagnostic push
#pragma ide diagnostic ignored "OCDFAInspection"

//Target lib here
#define targetLibName ("libil2cpp.so")
ElfScanner g_il2cppElf;
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
    LOGI("pthread created");

    //Check if target lib is loaded
    do {
        sleep(1);
        g_il2cppElf = ElfScanner::findElf(targetLibName);
    } while (!g_il2cppElf.isValid());

    //If lib is valid, mark it as loaded
    if (g_il2cppElf.isValid()) {
        libLoaded = true;
    }

    LOGI(("%s has been loaded"), (const char *) targetLibName);

    //DobbyHook((void *)(address + 0x000000), (void *)example, (void **)&old_example);

    il2cppBase = g_il2cppElf.base();

#ifdef __aarch64__ // arm64-v8a
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)maximumlightradius, (void **)&old_maximumlightradius);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)removeallshadows, (void **)&old_removeallshadows);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)longkilldistance, (void **)&old_longkilldistance);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)canmoveinvent, (void **)&old_canmoveinvent);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)unlimiteddurationforshapeshifter, (void **)&old_unlimiteddurationforshapeshifter);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)nocooldownforshapeshifter, (void **)&old_nocooldownforshapeshifter);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)unlimitedventdurationforengineers, (void **)&old_unlimitedventdurationforengineers);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)noventcooldownforengineers, (void **)&old_noventcooldownforengineers);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)unlimiteddurationforphantom, (void **)&old_unlimiteddurationforphantom);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)nocooldownforphantom, (void **)&old_nocooldownforphantom);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)unlockallitems, (void **)&old_unlockallitems);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)noads, (void **)&old_noads);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)freechat, (void **)&old_freechat);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)allowallcharacters, (void **)&old_allowallcharacters);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)playerspeed, (void **)&old_playerspeed);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)playername, (void **)&old_playername);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)playerlevel, (void **)&old_playerlevel);

#elif defined(__arm__) // armeabi-v7a
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)maximumlightradius, (void **)&old_maximumlightradius);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)removeallshadows, (void **)&old_removeallshadows);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)longkilldistance, (void **)&old_longkilldistance);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)canmoveinvent, (void **)&old_canmoveinvent);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)unlimiteddurationforshapeshifter, (void **)&old_unlimiteddurationforshapeshifter);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)nocooldownforshapeshifter, (void **)&old_nocooldownforshapeshifter);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)unlimitedventdurationforengineers, (void **)&old_unlimitedventdurationforengineers);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)noventcooldownforengineers, (void **)&old_noventcooldownforengineers);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)unlimiteddurationforphantom, (void **)&old_unlimiteddurationforphantom);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)nocooldownforphantom, (void **)&old_nocooldownforphantom);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)unlockallitems, (void **)&old_unlockallitems);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)noads, (void **)&old_noads);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)freechat, (void **)&old_freechat);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)allowallcharacters, (void **)&old_allowallcharacters);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)playerspeed, (void **)&old_playerspeed);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)playername, (void **)&old_playername);
    DobbyHook((void *)(il2cppBase + 0x000000), (void *)playerlevel, (void **)&old_playerlevel);
#endif

    LOGI("Done");

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
            ("Category_Player Mods"), //Not counted
            ("1_Toggle_Maximum Light Radius"),
            ("2_Toggle_Remove All Shadows"),
    
    /*
            ("Category_The Category"), //Not counted
            ("Toggle_The toggle"),
            (
                    "100_Toggle_True_The toggle 2"), //This one have feature number assigned, and switched on by default
            ("110_Toggle_The toggle 3"), //This one too
            ("SeekBar_The slider_1_100"),
            ("Spinner_The spinner_Items 1,Items 2,Items 3"),
            ("Button_The button"),
            ("ButtonLink_The button with link_https://www.youtube.com/"), //Not counted
            ("ButtonOnOff_The On/Off button"),
            ("CheckBox_The Check Box"),
            ("InputValue_Input number"),
            ("InputValue_1000_Input number 2"), //Max value
            ("InputText_Input text"),
            ("RadioButton_Radio buttons_OFF,Mod 1,Mod 2,Mod 3"),

            //Create new collapse
            ("Collapse_Collapse 1"),
            ("CollapseAdd_Toggle_The toggle"),
            ("CollapseAdd_Toggle_The toggle"),
            ("123_CollapseAdd_Toggle_The toggle"),
            ("122_CollapseAdd_CheckBox_Check box"),
            ("CollapseAdd_Button_The button"),

            //Create new collapse again
            ("Collapse_Collapse 2_True"),
            ("CollapseAdd_SeekBar_The slider_1_100"),
            ("CollapseAdd_InputValue_Input number"),

            ("RichTextView_This is text view, not fully HTML."
                      "<b>Bold</b> <i>italic</i> <u>underline</u>"
                      "<br />New line <font color='red'>Support colors</font>"
                      "<br/><big>bigger Text</big>"),
            ("RichWebView_<html><head><style>body{color: white;}</style></head><body>"
                      "This is WebView, with REAL HTML support!"
                      "<div style=\"background-color: darkblue; text-align: center;\">Support CSS</div>"
                      "<marquee style=\"color: green; font-weight:bold;\" direction=\"left\" scrollamount=\"5\" behavior=\"scroll\">This is <u>scrollable</u> text</marquee>"
                      "</body></html>")*/
    };

    //Now you dont have to manually update the number everytime;
    int Total_Feature = (sizeof features / sizeof features[0]);
    ret = (jobjectArray)
            env->NewObjectArray(Total_Feature, env->FindClass(("java/lang/String")),
                                env->NewStringUTF(""));

    for (int i = 0; i < Total_Feature; i++)
        env->SetObjectArrayElement(ret, i, env->NewStringUTF(features[i]));

    return (ret);
}

jobjectArray GetFeatureList2(JNIEnv *env, jobject context) {
    jobjectArray ret;

    const char *features[] = {
            ("Category_Player Abilities"), //Not counted
            ("3_Toggle_Long Kill Distance"),
            ("4_Toggle_Can Move In Vent"),
            ("5_Toggle_Unlimited Duration For Shapeshifter"),
            ("6_Toggle_No Cooldown For Shapeshifter"),
            ("7_Toggle_Unlimited Vent Duration For Engineers"),
            ("8_Toggle_No Vent Cooldown For Engineers"),
            ("9_Toggle_Unlimited Duration For Phantom"),
            ("10_Toggle_No Phantom Cooldown"),
            ("11_SeekBar_Player Speed_1_100"),
    
    /*
            ("Category_The Category 2"), //Not counted
            ("Toggle_The toggle"),
            (
                    "100_Toggle_True_The toggle 2"), //This one have feature number assigned, and switched on by default
            ("110_Toggle_The toggle 3"), //This one too
            ("SeekBar_The slider_1_100"),
            ("Spinner_The spinner_Items 1,Items 2,Items 3"),
            ("Button_The button"),
            ("ButtonLink_The button with link_https://www.youtube.com/"), //Not counted
            ("ButtonOnOff_The On/Off button"),
            ("CheckBox_The Check Box"),
            ("InputValue_Input number"),
            ("InputValue_1000_Input number 2"), //Max value
            ("InputText_Input text"),
            ("RadioButton_Radio buttons_OFF,Mod 1,Mod 2,Mod 3"),

            //Create new collapse
            ("Collapse_Collapse 1"),
            ("CollapseAdd_Toggle_The toggle"),
            ("CollapseAdd_Toggle_The toggle"),
            ("123_CollapseAdd_Toggle_The toggle"),
            ("122_CollapseAdd_CheckBox_Check box"),
            ("CollapseAdd_Button_The button"),

            //Create new collapse again
            ("Collapse_Collapse 2_True"),
            ("CollapseAdd_SeekBar_The slider_1_100"),
            ("CollapseAdd_InputValue_Input number"),

            ("RichTextView_This is text view, not fully HTML."
                      "<b>Bold</b> <i>italic</i> <u>underline</u>"
                      "<br />New line <font color='red'>Support colors</font>"
                      "<br/><big>bigger Text</big>"),
            ("RichWebView_<html><head><style>body{color: white;}</style></head><body>"
                      "This is WebView, with REAL HTML support!"
                      "<div style=\"background-color: darkblue; text-align: center;\">Support CSS</div>"
                      "<marquee style=\"color: green; font-weight:bold;\" direction=\"left\" scrollamount=\"5\" behavior=\"scroll\">This is <u>scrollable</u> text</marquee>"
                      "</body></html>")*/
    };

    //Now you dont have to manually update the number everytime;
    int Total_Feature = (sizeof features / sizeof features[0]);
    ret = (jobjectArray)
            env->NewObjectArray(Total_Feature, env->FindClass(("java/lang/String")),
                                env->NewStringUTF(""));

    for (int i = 0; i < Total_Feature; i++)
        env->SetObjectArrayElement(ret, i, env->NewStringUTF(features[i]));

    return (ret);
}

jobjectArray GetFeatureList3(JNIEnv *env, jobject context) {
    jobjectArray ret;

    const char *features[] = {
            ("Category_Account Mods"), //Not counted
            ("12_Toggle_Unlock All Items"),
            ("13_Toggle_No Ads"),
            ("14_Toggle_Free Chat"),
            ("15_Toggle_Allow All Characters"),
            ("16_InputText_Player Name"),
            ("17_InputValue_Player Level"),

    /*
            ("Category_The Category 3"), //Not counted
            ("Toggle_The toggle"),
            (
                    "100_Toggle_True_The toggle 2"), //This one have feature number assigned, and switched on by default
            ("110_Toggle_The toggle 3"), //This one too
            ("SeekBar_The slider_1_100"),
            ("Spinner_The spinner_Items 1,Items 2,Items 3"),
            ("Button_The button"),
            ("ButtonLink_The button with link_https://www.youtube.com/"), //Not counted
            ("ButtonOnOff_The On/Off button"),
            ("CheckBox_The Check Box"),
            ("InputValue_Input number"),
            ("InputValue_1000_Input number 2"), //Max value
            ("InputText_Input text"),
            ("RadioButton_Radio buttons_OFF,Mod 1,Mod 2,Mod 3"),

            //Create new collapse
            ("Collapse_Collapse 1"),
            ("CollapseAdd_Toggle_The toggle"),
            ("CollapseAdd_Toggle_The toggle"),
            ("123_CollapseAdd_Toggle_The toggle"),
            ("122_CollapseAdd_CheckBox_Check box"),
            ("CollapseAdd_Button_The button"),

            //Create new collapse again
            ("Collapse_Collapse 2_True"),
            ("CollapseAdd_SeekBar_The slider_1_100"),
            ("CollapseAdd_InputValue_Input number"),

            ("RichTextView_This is text view, not fully HTML."
                      "<b>Bold</b> <i>italic</i> <u>underline</u>"
                      "<br />New line <font color='red'>Support colors</font>"
                      "<br/><big>bigger Text</big>"),
            ("RichWebView_<html><head><style>body{color: white;}</style></head><body>"
                      "This is WebView, with REAL HTML support!"
                      "<div style=\"background-color: darkblue; text-align: center;\">Support CSS</div>"
                      "<marquee style=\"color: green; font-weight:bold;\" direction=\"left\" scrollamount=\"5\" behavior=\"scroll\">This is <u>scrollable</u> text</marquee>"
                      "</body></html>")*/
    };

    //Now you dont have to manually update the number everytime;
    int Total_Feature = (sizeof features / sizeof features[0]);
    ret = (jobjectArray)
            env->NewObjectArray(Total_Feature, env->FindClass(("java/lang/String")),
                                env->NewStringUTF(""));

    for (int i = 0; i < Total_Feature; i++)
        env->SetObjectArrayElement(ret, i, env->NewStringUTF(features[i]));

    return (ret);
}

jobjectArray GetFeatureList4(JNIEnv *env, jobject context) {
    jobjectArray ret;

    const char *features[] = {
            ("Category_ReLGL"), //Not counted
            ("ButtonLink_ReLGL Project_https://github.com/Parsa307/ReLGL-Android-Menu"), //Not counted

    /*
            ("Category_The Category 4"), //Not counted
            ("Toggle_The toggle"),
            (
                    "100_Toggle_True_The toggle 2"), //This one have feature number assigned, and switched on by default
            ("110_Toggle_The toggle 3"), //This one too
            ("SeekBar_The slider_1_100"),
            ("Spinner_The spinner_Items 1,Items 2,Items 3"),
            ("Button_The button"),
            ("ButtonLink_The button with link_https://www.youtube.com/"), //Not counted
            ("ButtonOnOff_The On/Off button"),
            ("CheckBox_The Check Box"),
            ("InputValue_Input number"),
            ("InputValue_1000_Input number 2"), //Max value
            ("InputText_Input text"),
            ("RadioButton_Radio buttons_OFF,Mod 1,Mod 2,Mod 3"),

            //Create new collapse
            ("Collapse_Collapse 1"),
            ("CollapseAdd_Toggle_The toggle"),
            ("CollapseAdd_Toggle_The toggle"),
            ("123_CollapseAdd_Toggle_The toggle"),
            ("122_CollapseAdd_CheckBox_Check box"),
            ("CollapseAdd_Button_The button"),

            //Create new collapse again
            ("Collapse_Collapse 2_True"),
            ("CollapseAdd_SeekBar_The slider_1_100"),
            ("CollapseAdd_InputValue_Input number"),

            ("RichTextView_This is text view, not fully HTML."
                      "<b>Bold</b> <i>italic</i> <u>underline</u>"
                      "<br />New line <font color='red'>Support colors</font>"
                      "<br/><big>bigger Text</big>"),
            ("RichWebView_<html><head><style>body{color: white;}</style></head><body>"
                      "This is WebView, with REAL HTML support!"
                      "<div style=\"background-color: darkblue; text-align: center;\">Support CSS</div>"
                      "<marquee style=\"color: green; font-weight:bold;\" direction=\"left\" scrollamount=\"5\" behavior=\"scroll\">This is <u>scrollable</u> text</marquee>"
                      "</body></html>")*/
    };

    //Now you dont have to manually update the number everytime;
    int Total_Feature = (sizeof features / sizeof features[0]);
    ret = (jobjectArray)
            env->NewObjectArray(Total_Feature, env->FindClass(("java/lang/String")),
                                env->NewStringUTF(""));

    for (int i = 0; i < Total_Feature; i++)
        env->SetObjectArrayElement(ret, i, env->NewStringUTF(features[i]));

    return (ret);
}

void Changes(JNIEnv *env, jclass clazz, jobject obj,
                                        jint featNum, jstring featName, jint value,
                                        jboolean boolean, jstring str) {

/*
    LOGD(("Feature name: %d - %s | Value: = %d | Bool: = %d | Text: = %s"), featNum,
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
                    LOGD(("Selected item 1"));
                    break;
                case 1:
                    LOGD(("Selected item 2"));
                    break;
                case 2:
                    LOGD(("Selected item 3"));
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
            {("Icon"), ("()Ljava/lang/String;"), reinterpret_cast<void *>(Icon)},
            {("Background"), ("()Ljava/lang/String;"), reinterpret_cast<void *>(Background)},
            {("IconWebViewData"),  ("()Ljava/lang/String;"), reinterpret_cast<void *>(IconWebViewData)},
            {("IsGameLibLoaded"),  ("()Z"), reinterpret_cast<void *>(isGameLibLoaded)},
            {("Init"),("(Landroid/content/Context;Landroid/widget/TextView;Landroid/widget/TextView;Landroid/widget/TextView;Landroid/widget/TextView;Landroid/widget/TextView;Landroid/widget/TextView;)V"),reinterpret_cast<void *>(Init)},
            {("SettingsList"),  ("()[Ljava/lang/String;"), reinterpret_cast<void *>(SettingsList)},
            {("GetFeatureList"),  ("()[Ljava/lang/String;"), reinterpret_cast<void *>(GetFeatureList)},
            {("GetFeatureList2"),  ("()[Ljava/lang/String;"), reinterpret_cast<void *>(GetFeatureList2)},
            {("GetFeatureList3"),  ("()[Ljava/lang/String;"), reinterpret_cast<void *>(GetFeatureList3)},
            {("GetFeatureList4"),  ("()[Ljava/lang/String;"), reinterpret_cast<void *>(GetFeatureList4)},
    };

    jclass clazz = env->FindClass(("com/relgl/modmenu/Menu"));
    if (!clazz)
        return JNI_ERR;
    if (env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof(methods[0])) != 0)
        return JNI_ERR;
    return JNI_OK;
}

int RegisterPreferences(JNIEnv *env) {
    JNINativeMethod methods[] = {
            {("Changes"), ("(Landroid/content/Context;ILjava/lang/String;IZLjava/lang/String;)V"), reinterpret_cast<void *>(Changes)},
    };
    jclass clazz = env->FindClass(("com/relgl/modmenu/Preferences"));
    if (!clazz)
        return JNI_ERR;
    if (env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof(methods[0])) != 0)
        return JNI_ERR;
    return JNI_OK;
}

int RegisterMain(JNIEnv *env) {
    JNINativeMethod methods[] = {
            {("CheckOverlayPermission"), ("(Landroid/content/Context;)V"), reinterpret_cast<void *>(CheckOverlayPermission)},
    };
    jclass clazz = env->FindClass(("com/relgl/modmenu/Main"));
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
