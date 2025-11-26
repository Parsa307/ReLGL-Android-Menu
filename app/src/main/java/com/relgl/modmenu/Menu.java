package com.relgl.modmenu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.Base64;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import android.view.View.OnClickListener;

import static android.widget.RelativeLayout.ALIGN_PARENT_LEFT;
import static android.widget.RelativeLayout.ALIGN_PARENT_RIGHT;
import android.widget.TableLayout.LayoutParams;

import java.util.Objects;

public class Menu {
    //********** Here you can easily change the menu appearance **********//

    //region Variable
    //public static final String TAG = "Mod_Menu"; //Tag for logcat

    int TEXT_COLOR = Color.YELLOW;
    int BTN_COLOR = Color.parseColor("#FFA500");
    int MENU_BG_COLOR = Color.parseColor("#FFA500");
    int MENU_WIDTH = 250;
    int MENU_HEIGHT = 160;
    int POS_X = 0;
    int POS_Y = 100;

    float MENU_CORNER = 4f;
    int ICON_SIZE = 45; //Change both width and height of image
    float ICON_ALPHA = 1f; //Transparent
    int CategoryBG = Color.parseColor("#FFA500");
    int SeekBarColor = Color.parseColor("#FFA500");
    int SeekBarProgressColor = Color.parseColor("#FFA500");
    int CheckBoxColor = Color.parseColor("#FFA500");
    int RadioColor = Color.parseColor("#FFA500");
    String NumberTxtColor = "#FFA500";

    int LST_MAB = Color.parseColor("#FFA500");
    int TAB_STROKE_COLOR = Color.parseColor("#FFA500");
    int TAB_TEXT_COLOR = Color.YELLOW;
    float TAB_TEXT_SIZE = 12.5f;
    HorizontalScrollView Scroll;
    ScrollView H;
    LinearLayout l, tl;
    LinearLayout h, h2, v1, v2, v3, v4, v5;
    TextView t1, t2, t3, t4;
    //********************************************************************//

    RelativeLayout mCollapsed, mRootContainer;
    LinearLayout mExpanded, mods, mods2, mods3, mods4, mSettings, mCollapse;
    LinearLayout.LayoutParams scrlLL;
    WindowManager mWindowManager;
    WindowManager.LayoutParams vmParams;
    ImageView startimage;
    FrameLayout rootFrame;
    ScrollView scrollView;
    boolean stopChecking, overlayRequired;
    Context getContext;
    LinearLayout subLayout;

    //initialize methods from the native library
    native void Init(Context context, TextView title, TextView subTitle, TextView t1, TextView t2, TextView t3, TextView t4);

    native String Icon();

    native String Background();

    native String IconWebViewData();

    native String[] GetFeatureList();

    native String[] GetFeatureList2();

    native String[] GetFeatureList3();

    native String[] GetFeatureList4();

    native String[] SettingsList();

    native boolean IsGameLibLoaded();

    private void AddColor(View view, int color, int strokeWidth, int strokeColor) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius((float) 0);
        gd.setStroke(strokeWidth, strokeColor);
        view.setBackground(gd);
    }

    //Here we write the code for our Menu
    // Reference: https://www.androidhive.info/2016/11/android-floating-widget-like-facebook-chat-head/
    @SuppressLint({"SetTextI18n", "SuspiciousIndentation", "ClickableViewAccessibility"})
    public Menu(Context context) {

        getContext = context;
        rootFrame = new FrameLayout(context); // Global markup
        rootFrame.setOnTouchListener(onTouchListener());
        mRootContainer = new RelativeLayout(context); // Markup on which two markups of the icon and the menu itself will be placed
        mCollapsed = new RelativeLayout(context); // Markup of the icon (when the menu is minimized)
        mCollapsed.setVisibility(View.VISIBLE);
        mCollapsed.setAlpha(ICON_ALPHA);

        //********** The box of the mod menu **********
        mExpanded = new LinearLayout(context); // Menu markup (when the menu is expanded)
        mExpanded.setVisibility(View.GONE);
        mExpanded.setBackgroundColor(MENU_BG_COLOR);
        mExpanded.setOrientation(LinearLayout.VERTICAL);
        // mExpanded.setPadding(1, 1, 1, 1); //So borders would be visible
        mExpanded.setLayoutParams(new LinearLayout.LayoutParams(dp(MENU_WIDTH), WRAP_CONTENT));
        GradientDrawable gdMenuBody = new GradientDrawable();
        gdMenuBody.setCornerRadius(MENU_CORNER); //Set corner
        gdMenuBody.setColor(MENU_BG_COLOR); //Set background color
        gdMenuBody.setStroke(1, Color.parseColor("#32cb00")); //Set border
        //mExpanded.setBackground(gdMenuBody); //Apply GradientDrawable to it
        byte[] bgdecode = Base64.decode(Background(), 0);
        Bitmap backgroundbitmap = BitmapFactory.decodeByteArray(bgdecode, 0, bgdecode.length);
        BitmapDrawable bgimage = new BitmapDrawable(context.getResources(), backgroundbitmap);
        mExpanded.setBackground(bgimage);

        //********** The icon to open mod menu **********
        startimage = new ImageView(context);
        startimage.setLayoutParams(new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        int applyDimension = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ICON_SIZE, context.getResources().getDisplayMetrics()); //Icon size
        startimage.getLayoutParams().height = applyDimension;
        startimage.getLayoutParams().width = applyDimension;
        //startimage.requestLayout();
        startimage.setScaleType(ImageView.ScaleType.FIT_XY);
        byte[] decode = Base64.decode(Icon(), 0);
        startimage.setImageBitmap(BitmapFactory.decodeByteArray(decode, 0, decode.length));
        ((ViewGroup.MarginLayoutParams) startimage.getLayoutParams()).topMargin = convertDipToPixels(10);
        //Initialize event handlers for buttons, etc.
        startimage.setOnTouchListener(onTouchListener());
        startimage.setOnClickListener(view -> {
            mCollapsed.setVisibility(View.GONE);
            mExpanded.setVisibility(View.VISIBLE);
        });

        //********** The icon in Webview to open mod menu **********
        WebView wView = new WebView(context); //Icon size width=\"50\" height=\"50\"
        wView.setLayoutParams(new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        int applyDimension2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ICON_SIZE, context.getResources().getDisplayMetrics()); //Icon size
        wView.getLayoutParams().height = applyDimension2;
        wView.getLayoutParams().width = applyDimension2;
        wView.loadData("<html>" +
                "<head></head>" +
                "<body style=\"margin: 0; padding: 0\">" +
                "<img src=\"" + IconWebViewData() + "\" width=\"" + ICON_SIZE + "\" height=\"" + ICON_SIZE + "\" >" +
                "</body>" +
                "</html>", "text/html", "utf-8");
        wView.setBackgroundColor(0x00000000); //Transparent
        wView.setAlpha(ICON_ALPHA);
        wView.getSettings().setDatabaseEnabled(true);
        wView.setOnTouchListener(onTouchListener());

        //********** Sub Layout **********
        subLayout = new LinearLayout(context);
        subLayout.setOrientation(LinearLayout.VERTICAL);
        subLayout.setPadding(5, 5, 5, 5);

        //********** Settings hide **********
        H = new ScrollView(context);
        H.setVisibility(View.GONE);
        H.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, dp(220)));

        //********** Settings **********
        mSettings = new LinearLayout(context);
        mSettings.setOrientation(LinearLayout.VERTICAL);
        featureList(SettingsList(), mSettings);

        //********** Title **********
        RelativeLayout titleText = new RelativeLayout(context);
        titleText.setPadding(10, 5, 10, 5);
        titleText.setVerticalGravity(16);

        TextView title = new TextView(context);
        title.setTextColor(TEXT_COLOR);
        title.setTextSize(18.0f);
        title.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        rl.addRule(RelativeLayout.CENTER_HORIZONTAL);
        title.setLayoutParams(rl);

        //********** Sub title **********
        TextView subTitle = new TextView(context);
        subTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        subTitle.setMarqueeRepeatLimit(-1);
        subTitle.setSingleLine(true);
        subTitle.setSelected(true);
        subTitle.setTextColor(TEXT_COLOR);
        subTitle.setTextSize(10.0f);
        subTitle.setGravity(Gravity.CENTER);
        subTitle.setPadding(0, 0, 0, 5);

        LayoutParams layoutParams2 = new LayoutParams(WRAP_CONTENT, MATCH_PARENT);
        layoutParams2.setMargins(5, 2, 5, 2);

        //********** HORIZONTAL SPLITS **********
        h = new LinearLayout(context);
        h.setBackgroundColor(TAB_STROKE_COLOR);
        h.setPadding(-1, 2, -1, 1);

        h2 = new LinearLayout(context);
        h2.setBackgroundColor(TAB_STROKE_COLOR);
        h2.setPadding(-1, 2, -1, 1);

        //********** VERTICAL SPLITS **********
        v1 = new LinearLayout(context);
        v1.setBackgroundColor(TAB_STROKE_COLOR);
        v1.setPadding(2, 5, 1, 5);
        v1.setLayoutParams(layoutParams2);

        v2 = new LinearLayout(context);
        v2.setBackgroundColor(TAB_STROKE_COLOR);
        v2.setPadding(2, 5, 1, 5);
        v2.setLayoutParams(layoutParams2);

        v3 = new LinearLayout(context);
        v3.setBackgroundColor(TAB_STROKE_COLOR);
        v3.setPadding(2, 5, 1, 5);
        v3.setLayoutParams(layoutParams2);

        v4 = new LinearLayout(context);
        v4.setBackgroundColor(TAB_STROKE_COLOR);
        v4.setPadding(2, 5, 1, 5);
        v4.setLayoutParams(layoutParams2);

        v5 = new LinearLayout(context);
        v5.setBackgroundColor(-1);
        v5.setPadding(2, 5, 1, 5);
        v5.setLayoutParams(layoutParams2);

        //********** MAIN LAYOUT OF TABS **********
        l = new LinearLayout(context);
        l.setLayoutParams(new LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(3, 0, 3, 0);

        //********** HorizontalScrollView for Menu Buttons **********
        Scroll = new HorizontalScrollView(context);
        Scroll.setEnabled(true);
        Scroll.setHorizontalScrollBarEnabled(false);

        //********** Layout **********
        tl = new LinearLayout(context);
        tl.setOrientation(LinearLayout.HORIZONTAL);

        LayoutParams layoutParams = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        layoutParams.setMargins(-2, 5, -2, 5);

        //****************************** TAB 1 *****************************\\
        t1 = new TextView(context);
        t1.setLayoutParams(layoutParams);
        t1.setGravity(Gravity.CENTER);
        t1.setTextColor(TAB_TEXT_COLOR);
        t1.setTextSize(TAB_TEXT_SIZE);
        t1.setPadding(45, 15, 45, 15);
        t1.setShadowLayer(7.0f, 0.0f, 0.0f, Color.BLACK);
        AddColor(t1, LST_MAB, 3, TAB_STROKE_COLOR);
        t1.setTypeface(null, Typeface.BOLD);
        t1.setOnClickListener(p1 -> {
            scrollView.removeAllViews();
            scrollView.addView(mods);
            AddColor(t1, LST_MAB, 3, TAB_STROKE_COLOR);
            AddColor(t2, Color.TRANSPARENT, 0, TAB_STROKE_COLOR);
            AddColor(t3, Color.TRANSPARENT, 0, TAB_STROKE_COLOR);
            AddColor(t4, Color.TRANSPARENT, 0, TAB_STROKE_COLOR);

        });

        //****************************** TAB 2 *****************************\\
        t2 = new TextView(context);
        t2.setLayoutParams(layoutParams);
        t2.setGravity(Gravity.CENTER);
        t2.setTextColor(TAB_TEXT_COLOR);
        t2.setTextSize(TAB_TEXT_SIZE);
        t2.setPadding(45, 15, 45, 15);
        t2.setShadowLayer(7.0f, 0.0f, 0.0f, Color.BLACK);
        AddColor(t2, Color.TRANSPARENT, 0, TAB_STROKE_COLOR);
        t2.setTypeface(null, Typeface.BOLD);
        t2.setOnClickListener(p1 -> {
            scrollView.removeAllViews();
            scrollView.addView(mods2);
            AddColor(t1, Color.TRANSPARENT, 0, TAB_STROKE_COLOR);
            AddColor(t2, LST_MAB, 3, TAB_STROKE_COLOR);
            AddColor(t3, Color.TRANSPARENT, 0, TAB_STROKE_COLOR);
            AddColor(t4, Color.TRANSPARENT, 0, TAB_STROKE_COLOR);

        });

        //****************************** TAB 3 *****************************\\
        t3 = new TextView(context);
        t3.setLayoutParams(layoutParams);
        t3.setGravity(Gravity.CENTER);
        t3.setTextColor(TAB_TEXT_COLOR);
        t3.setTextSize(TAB_TEXT_SIZE);
        t3.setPadding(45, 15, 45, 15);
        t3.setShadowLayer(7.0f, 0.0f, 0.0f, Color.BLACK);
        AddColor(t3, Color.TRANSPARENT, 0, TAB_STROKE_COLOR);
        t3.setTypeface(null, Typeface.BOLD);
        t3.setOnClickListener(p1 -> {
            scrollView.removeAllViews();
            scrollView.addView(mods3);
            AddColor(t1, Color.TRANSPARENT, 0, TAB_STROKE_COLOR);
            AddColor(t2, Color.TRANSPARENT, 0, TAB_STROKE_COLOR);
            AddColor(t3, LST_MAB, 3, TAB_STROKE_COLOR);
            AddColor(t4, Color.TRANSPARENT, 0, TAB_STROKE_COLOR);

        });

        //****************************** TAB 4 *****************************\\
        t4 = new TextView(context);
        t4.setLayoutParams(layoutParams);
        t4.setGravity(Gravity.CENTER);
        t4.setTextColor(TAB_TEXT_COLOR);
        t4.setTextSize(TAB_TEXT_SIZE);
        t4.setPadding(45, 15, 45, 15);
        t4.setShadowLayer(7.0f, 0.0f, 0.0f, Color.BLACK);
        AddColor(t4, Color.TRANSPARENT, 0, TAB_STROKE_COLOR);
        t4.setTypeface(null, Typeface.BOLD);
        t4.setOnClickListener(p1 -> {
            scrollView.removeAllViews();
            scrollView.addView(mods4);
            AddColor(t1, Color.TRANSPARENT, 0, TAB_STROKE_COLOR);
            AddColor(t2, Color.TRANSPARENT, 0, TAB_STROKE_COLOR);
            AddColor(t3, Color.TRANSPARENT, 0, TAB_STROKE_COLOR);
            AddColor(t4, LST_MAB, 3, TAB_STROKE_COLOR);

        });

        //********** Mod menu feature list **********
        scrollView = new ScrollView(context);
        scrlLL = new LinearLayout.LayoutParams(MATCH_PARENT, dp(MENU_HEIGHT));
        scrollView.setLayoutParams(scrlLL);
        mods = new LinearLayout(context);
        mods.setOrientation(LinearLayout.VERTICAL);

        mods2 = new LinearLayout(context);
        mods2.setOrientation(LinearLayout.VERTICAL);

        mods3 = new LinearLayout(context);
        mods3.setOrientation(LinearLayout.VERTICAL);

        mods4 = new LinearLayout(context);
        mods4.setOrientation(LinearLayout.VERTICAL);

        //********** RelativeLayout for buttons **********
        RelativeLayout relativeLayout = new RelativeLayout(context);
        relativeLayout.setPadding(10, 3, 10, 3);
        relativeLayout.setVerticalGravity(Gravity.CENTER);

        //**********  Hide/Kill button **********
        RelativeLayout.LayoutParams lParamsHideBtn = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lParamsHideBtn.addRule(ALIGN_PARENT_LEFT);

        final Button hideBtn = new Button(context);
        hideBtn.setLayoutParams(lParamsHideBtn);
        GradientDrawable hidegd = new GradientDrawable();
        hidegd.setColor(BTN_COLOR);
        hidegd.setStroke(3, Color.WHITE);
        hidegd.setCornerRadii(new float[]{0, 0, 30, 30, 0, 0, 0, 0});
        hideBtn.setBackground(hidegd);
        hideBtn.setText("SETTINGS");
        hideBtn.setTextColor(TEXT_COLOR);
        hideBtn.setOnClickListener(new OnClickListener() {
            boolean settingsOpen;

            @SuppressLint("SetTextI18n")
            public void onClick(View view) {
                settingsOpen = !settingsOpen;
                if (settingsOpen) {
                    hideBtn.setText("CLOSE");
                    H.setVisibility(View.VISIBLE);
                    l.setVisibility(View.GONE);
                    scrollView.setVisibility(View.GONE);
                } else {
                    hideBtn.setText("SETTINGS");
                    H.setVisibility(View.GONE);
                    l.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.VISIBLE);
                }
            }
        });

        //********** Close button **********
        RelativeLayout.LayoutParams lParamsCloseBtn = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lParamsCloseBtn.addRule(ALIGN_PARENT_RIGHT);

        Button closeBtn = new Button(context);
        closeBtn.setLayoutParams(lParamsCloseBtn);
        GradientDrawable closegd = new GradientDrawable();
        closegd.setColor(BTN_COLOR);
        closegd.setStroke(3, Color.WHITE);
        closegd.setCornerRadii(new float[]{30, 30, 0, 0, 0, 0, 0, 0});
        closeBtn.setBackground(closegd);
        closeBtn.setText("MINIMIZE");
        closeBtn.setTextColor(TEXT_COLOR);
        closeBtn.setOnClickListener(view -> {
            mCollapsed.setVisibility(View.VISIBLE);
            mCollapsed.setAlpha(ICON_ALPHA);
            mExpanded.setVisibility(View.GONE);
        });

        //********** Adding view components **********
        mRootContainer.addView(mCollapsed);
        mRootContainer.addView(mExpanded);
        if (IconWebViewData() != null) {
            mCollapsed.addView(wView);
        } else {
            mCollapsed.addView(startimage);
        }
        titleText.addView(title);
        mExpanded.addView(subLayout);
        subLayout.addView(titleText);
        subLayout.addView(subTitle);
        subLayout.addView(l);
        l.addView(h);
        l.addView(Scroll);
        Scroll.addView(tl);
        tl.addView(v1);
        tl.addView(t1);
        tl.addView(v2);
        tl.addView(t2);
        tl.addView(v3);
        tl.addView(t3);
        tl.addView(v4);
        tl.addView(t4);
        tl.addView(v5);
        l.addView(h2);
        H.addView(mSettings);
        mExpanded.addView(H);
        scrollView.addView(mods);
        mExpanded.addView(scrollView);
        relativeLayout.addView(hideBtn);
        relativeLayout.addView(closeBtn);
        mExpanded.addView(relativeLayout);

        Init(context, title, subTitle, t1, t2, t3, t4);
    }

    public void ShowMenu() {
        rootFrame.addView(mRootContainer);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            boolean viewLoaded = false;

            @Override
            public void run() {
                //If the save preferences is enabled, it will check if game lib is loaded before starting menu
                //Comment the if-else code out except startService if you want to run the app and test preferences
                if (Preferences.loadPref && !IsGameLibLoaded() && !stopChecking) {
                    if (!viewLoaded) {
                        Category(mods, "Save preferences is enabled. Waiting for game lib to be loaded...\n\nForce load menu may not apply mods instantly. You would need to reactivate them again");
                        Button(mods, -100, "Force load menu");
                        Category(mods2, "Save preferences is enabled. Waiting for game lib to be loaded...\n\nForce load menu may not apply mods instantly. You would need to reactivate them again");
                        Button(mods2, -100, "Force load menu");
                        Category(mods3, "Save preferences is enabled. Waiting for game lib to be loaded...\n\nForce load menu may not apply mods instantly. You would need to reactivate them again");
                        Button(mods3, -100, "Force load menu");
                        Category(mods4, "Save preferences is enabled. Waiting for game lib to be loaded...\n\nForce load menu may not apply mods instantly. You would need to reactivate them again");
                        Button(mods4, -100, "Force load menu");
                        viewLoaded = true;
                    }
                    handler.postDelayed(this, 600);
                } else {
                    mods.removeAllViews();
                    mods2.removeAllViews();
                    mods3.removeAllViews();
                    mods4.removeAllViews();
                    featureList(GetFeatureList(), mods);
                    featureList(GetFeatureList2(), mods2);
                    featureList(GetFeatureList3(), mods3);
                    featureList(GetFeatureList4(), mods4);
                }
            }
        }, 500);
    }

    @SuppressLint("WrongConstant")
    public void SetWindowManagerWindowService() {
        //Variable to check later if the phone supports Draw over other apps permission
        int iparams = Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ? 2038 : 2002;
        vmParams = new WindowManager.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, iparams, 8, -3);
        //params = new WindowManager.LayoutParams(WindowManager.LayoutParams.LAST_APPLICATION_WINDOW, 8, -3);
        vmParams.gravity = 51;
        vmParams.x = POS_X;
        vmParams.y = POS_Y;

        mWindowManager = (WindowManager) getContext.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(rootFrame, vmParams);

        overlayRequired = true;
    }

    @SuppressLint("WrongConstant")
    public void SetWindowManagerActivity() {
        vmParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                POS_X,//initialX
                POS_Y,//initialy
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_SPLIT_TOUCH,
                PixelFormat.TRANSPARENT
        );
        vmParams.gravity = 51;
        vmParams.x = POS_X;
        vmParams.y = POS_Y;

        mWindowManager = ((Activity) getContext).getWindowManager();
        mWindowManager.addView(rootFrame, vmParams);
    }

    private View.OnTouchListener onTouchListener() {
        return new View.OnTouchListener() {
            final View collapsedView = mCollapsed;
            final View expandedView = mExpanded;
            private float initialTouchX, initialTouchY;
            private int initialX, initialY;

            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = vmParams.x;
                        initialY = vmParams.y;
                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int rawX = (int) (motionEvent.getRawX() - initialTouchX);
                        int rawY = (int) (motionEvent.getRawY() - initialTouchY);
                        mExpanded.setAlpha(1f);
                        mCollapsed.setAlpha(1f);
                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (rawX < 10 && rawY < 10 && isViewCollapsed()) {
                            //When user clicks on the image view of the collapsed layout,
                            //visibility of the collapsed layout will be changed to "View.GONE"
                            //and expanded view will become visible.
                        if (collapsedView != null) {
                            collapsedView.setVisibility(View.GONE);
                        }
                        if (expandedView != null) {
                            expandedView.setVisibility(View.VISIBLE);
                        }
                            view.performClick();
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        mExpanded.setAlpha(0.5f);
                        mCollapsed.setAlpha(0.5f);
                        //Calculate the X and Y coordinates of the view.
                        vmParams.x = initialX + ((int) (motionEvent.getRawX() - initialTouchX));
                        vmParams.y = initialY + ((int) (motionEvent.getRawY() - initialTouchY));
                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(rootFrame, vmParams);
                        return true;
                    default:
                        return false;
                }
            }
        };
    }

    private void featureList(String[] listFT, LinearLayout linearLayout) {
        //Currently looks messy right now. Let me know if you have improvements
        int featNum, subFeat = 0;
        LinearLayout llBak = linearLayout;

        for (int i = 0; i < listFT.length; i++) {
            boolean switchedOn = false;
            //Log.i("featureList", listFT[i]);
            String feature = listFT[i];
            if (feature.contains("_True")) {
                switchedOn = true;
                feature = feature.replaceFirst("_True", "");
            }

            linearLayout = llBak;
            if (feature.contains("CollapseAdd_")) {
                //if (collapse != null)
                linearLayout = mCollapse;
                feature = feature.replaceFirst("CollapseAdd_", "");
            }
            String[] str = feature.split("_");

            //Assign feature number
            if (TextUtils.isDigitsOnly(str[0]) || str[0].matches("-[0-9]*")) {
                featNum = Integer.parseInt(str[0]);
                feature = feature.replaceFirst(str[0] + "_", "");
                subFeat++;
            } else {
                //Subtract feature number. We don't want to count ButtonLink, Category, RichTextView and RichWebView
                featNum = i - subFeat;
            }
            String[] strSplit = feature.split("_");
            switch (strSplit[0]) {
                case "Toggle":
                    Switch(linearLayout, featNum, strSplit[1], switchedOn);
                    break;
                case "SeekBar":
                    SeekBar(getContext, linearLayout, featNum, strSplit[1], Integer.parseInt(strSplit[2]), Integer.parseInt(strSplit[3]));
                    break;
                case "Button":
                    Button(linearLayout, featNum, strSplit[1]);
                    break;
                case "ButtonOnOff":
                    ButtonOnOff(linearLayout, featNum, strSplit[1], switchedOn);
                    break;
                case "Spinner":
                    TextView(linearLayout, strSplit[1]);
                    Spinner(linearLayout, featNum, strSplit[1], strSplit[2]);
                    break;
                case "InputText":
                    InputText(linearLayout, featNum, strSplit[1]);
                    break;
                case "InputValue":
                    if (strSplit.length == 3)
                        InputNum(linearLayout, featNum, strSplit[2], Integer.parseInt(strSplit[1]));
                    if (strSplit.length == 2)
                        InputNum(linearLayout, featNum, strSplit[1], 0);
                    break;
                case "CheckBox":
                    CheckBox(linearLayout, featNum, strSplit[1], switchedOn);
                    break;
                case "RadioButton":
                    RadioButton(linearLayout, featNum, strSplit[1], strSplit[2]);
                    break;
                case "Collapse":
                    Collapse(linearLayout, strSplit[1], switchedOn);
                    subFeat++;
                    break;
                case "ButtonLink":
                    subFeat++;
                    ButtonLink(linearLayout, strSplit[1], strSplit[2]);
                    break;
                case "Category":
                    subFeat++;
                    Category(linearLayout, strSplit[1]);
                    break;
                case "RichTextView":
                    subFeat++;
                    TextView(linearLayout, strSplit[1]);
                    break;
                case "RichWebView":
                    subFeat++;
                    WebTextView(linearLayout, strSplit[1]);
                    break;
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private void Switch(LinearLayout linLayout, final int featNum, final String featName, boolean swiOn) {
        final Switch switchR = new Switch(getContext);

        final GradientDrawable GD_TRACK = new GradientDrawable();
        GD_TRACK.setSize(20, 20);
        GD_TRACK.setCornerRadius(100);

        final GradientDrawable GD_THUMB = new GradientDrawable();
        GD_THUMB.setSize(50, 50);
        GD_THUMB.setShape(GradientDrawable.OVAL);
        boolean isOn = Preferences.loadPrefBool(getContext, featName, featNum, swiOn);

        if (isOn) {
            GD_TRACK.setStroke(2, LST_MAB);
            GD_TRACK.setColor(LST_MAB);

            GD_THUMB.setStroke(2, LST_MAB);
            GD_TRACK.setColor(LST_MAB);
        } else {
            GD_TRACK.setStroke(2, LST_MAB);
            GD_TRACK.setColor(LST_MAB);

            GD_TRACK.setStroke(2, LST_MAB);
            GD_THUMB.setColor(LST_MAB);
        }
        switchR.setText(featName);
        switchR.setTextColor(TEXT_COLOR);
        switchR.setShadowLayer(7.0f, 0.0f, 0.0f, Color.BLACK);
        switchR.setPadding(10, 5, 10, 5);
        switchR.setThumbDrawable(GD_THUMB);
        switchR.setTrackDrawable(GD_TRACK);
        switchR.setChecked(Preferences.loadPrefBool(getContext, featName, featNum, swiOn));
        switchR.setOnCheckedChangeListener((compoundButton, bool) -> {
            Preferences.changeFeatureBool(getContext, featName, featNum, bool);
            if (bool) {
                GD_TRACK.setStroke(2, Color.WHITE);
                GD_TRACK.setColor(LST_MAB);

                GD_THUMB.setStroke(2, Color.WHITE);
                GD_THUMB.setColor(LST_MAB);
            } else {
                GD_TRACK.setStroke(2, LST_MAB);
                GD_TRACK.setColor(LST_MAB);

                GD_THUMB.setStroke(2, LST_MAB);
                GD_THUMB.setColor(Color.WHITE);
            }
            switch (featNum) {
                case -1: //Save perferences
                    Preferences.with(switchR.getContext()).writeBoolean(-1, bool);
                    if (!bool)
                        Preferences.with(switchR.getContext()).clear(); //Clear perferences if switched off
                    break;
            }
        });

        linLayout.addView(switchR);
    }

    private void SeekBar(Context context, LinearLayout linLayout, final int featNum, final String featName, final int min, int max) {
        int loadedProg = Preferences.loadPrefInt(context, featName, featNum);
        LinearLayout linearLayout = new LinearLayout(getContext);
        linearLayout.setPadding(10, 5, 0, 5);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);

        final TextView textView = new TextView(getContext);
        textView.setText(Html.fromHtml(featName + ": <font color='" + NumberTxtColor + "'>" + ((loadedProg == 0) ? min : loadedProg)));
        textView.setTextColor(TEXT_COLOR);

        SeekBar seekBar = new SeekBar(getContext);
        seekBar.setPadding(25, 10, 35, 10);
        seekBar.setMax(max);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            seekBar.setMin(min); //setMin for Oreo and above
        seekBar.setProgress((loadedProg == 0) ? min : loadedProg);
        seekBar.getThumb().setColorFilter(SeekBarColor, PorterDuff.Mode.SRC_ATOP);
        seekBar.getProgressDrawable().setColorFilter(SeekBarProgressColor, PorterDuff.Mode.SRC_ATOP);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                //if progress is greater than minimum, don't go below. Else, set progress
                seekBar.setProgress(Math.max(i, min));
                Preferences.changeFeatureInt(getContext, featName, featNum, Math.max(i, min));
                textView.setText(Html.fromHtml(featName + ": <font color='" + NumberTxtColor + "'>" + (Math.max(i, min))));
            }
        });
        linearLayout.addView(textView);
        linearLayout.addView(seekBar);

        linLayout.addView(linearLayout);
    }

    private void Button(LinearLayout linLayout, final int featNum, final String featName) {
        final Button button = new Button(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParams.setMargins(7, 5, 7, 5);
        button.setLayoutParams(layoutParams);
        button.setTextColor(TEXT_COLOR);
        button.setAllCaps(false); //Disable caps to support html
        button.setText(Html.fromHtml(featName));
        GradientDrawable buttongd = new GradientDrawable();
        buttongd.setColor(BTN_COLOR);
        buttongd.setStroke(3, Color.WHITE);
        buttongd.setCornerRadii(new float[]{30, 30, 0, 0, 30, 30, 0, 0});
        button.setBackground(buttongd);
        button.setOnClickListener(v -> {
            switch (featNum) {

                case -6:
                    scrollView.removeView(mSettings);
                    scrollView.addView(mods);
                    break;
                case -100:
                    stopChecking = true;
                    break;
            }
            Preferences.changeFeatureInt(getContext, featName, featNum, 0);
        });

        linLayout.addView(button);
    }

    private void ButtonLink(LinearLayout linLayout, final String featName, final String url) {
        final Button button = new Button(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParams.setMargins(7, 5, 7, 5);
        button.setLayoutParams(layoutParams);
        button.setAllCaps(false); //Disable caps to support html
        button.setTextColor(TEXT_COLOR);
        button.setText(Html.fromHtml(featName));
        GradientDrawable buttonlinkgd = new GradientDrawable();
        buttonlinkgd.setColor(BTN_COLOR);
        buttonlinkgd.setStroke(3, Color.WHITE);
        buttonlinkgd.setCornerRadii(new float[]{30, 30, 0, 0, 30, 30, 0, 0});
        button.setBackground(buttonlinkgd);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(url));
            getContext.startActivity(intent);
        });
        linLayout.addView(button);
    }

    private void ButtonOnOff(LinearLayout linLayout, final int featNum, String featName, boolean switchedOn) {
        final Button button = new Button(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParams.setMargins(7, 5, 7, 5);
        button.setLayoutParams(layoutParams);
        button.setTextColor(TEXT_COLOR);
        button.setAllCaps(false); //Disable caps to support html

        final String finalfeatName = featName.replace("OnOff_", "");
        boolean isOn = Preferences.loadPrefBool(getContext, featName, featNum, switchedOn);
        if (isOn) {
            button.setText(Html.fromHtml(finalfeatName + ": ON"));
            GradientDrawable buttonongd = new GradientDrawable();
            buttonongd.setColor(LST_MAB);
            buttonongd.setStroke(3, Color.WHITE);
            buttonongd.setCornerRadii(new float[]{30, 30, 0, 0, 30, 30, 0, 0});
            button.setBackground(buttonongd);
            isOn = false;
        } else {
            button.setText(Html.fromHtml(finalfeatName + ": OFF"));
            GradientDrawable buttonoffgd = new GradientDrawable();
            buttonoffgd.setColor(BTN_COLOR);
            buttonoffgd.setStroke(3, Color.WHITE, 4, 4);
            buttonoffgd.setCornerRadii(new float[]{30, 30, 0, 0, 30, 30, 0, 0});
            button.setBackground(buttonoffgd);
            isOn = true;
        }
        final boolean finalIsOn = isOn;
        button.setOnClickListener(new View.OnClickListener() {
            boolean isOn = finalIsOn;

            public void onClick(View v) {
                Preferences.changeFeatureBool(getContext, finalfeatName, featNum, isOn);
                if (isOn) {
                    button.setText(Html.fromHtml(finalfeatName + ": ON"));
                    GradientDrawable buttonongd = new GradientDrawable();
                    buttonongd.setColor(LST_MAB);
                    buttonongd.setStroke(3, Color.WHITE);
                    buttonongd.setCornerRadii(new float[]{30, 30, 0, 0, 30, 30, 0, 0});
                    button.setBackground(buttonongd);
                    isOn = false;
                } else {
                    button.setText(Html.fromHtml(finalfeatName + ": OFF"));
                    GradientDrawable buttonoffgd = new GradientDrawable();
                    buttonoffgd.setColor(BTN_COLOR);
                    buttonoffgd.setStroke(3, Color.WHITE, 4, 4);
                    buttonoffgd.setCornerRadii(new float[]{30, 30, 0, 0, 30, 30, 0, 0});
                    button.setBackground(buttonoffgd);
                    isOn = true;
                }
            }
        });
        linLayout.addView(button);
    }

    private void Spinner(LinearLayout linLayout, final int featNum, final String featName, final String list) {
        final List<String> lists = new LinkedList<>(Arrays.asList(list.split(",")));

        // Create another LinearLayout as a workaround to use it as a background
        // to keep the down arrow symbol. No arrow symbol if setBackgroundColor set
        LinearLayout linearLayout2 = new LinearLayout(getContext);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        layoutParams2.setMargins(7, 2, 7, 2);
        linearLayout2.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable spinnergd = new GradientDrawable();
        spinnergd.setColor(LST_MAB);
        spinnergd.setStroke(3, Color.WHITE);
        spinnergd.setCornerRadii(new float[]{30, 30, 0, 0, 30, 30, 0, 0});
        linearLayout2.setBackground(spinnergd);
        linearLayout2.setLayoutParams(layoutParams2);

        final Spinner spinner = new Spinner(getContext, Spinner.MODE_DROPDOWN);
        spinner.setLayoutParams(layoutParams2);
        spinner.getBackground().setColorFilter(1, PorterDuff.Mode.SRC_ATOP); //trick to show white down arrow color
        //Creating the ArrayAdapter instance having the list
        ArrayAdapter<String> aa = new ArrayAdapter<>(getContext, android.R.layout.simple_spinner_dropdown_item, lists);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner'
        spinner.setAdapter(aa);
        spinner.setSelection(Preferences.loadPrefInt(getContext, featName, featNum));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Preferences.changeFeatureInt(getContext, spinner.getSelectedItem().toString(), featNum, position);
                ((TextView) parentView.getChildAt(0)).setTextColor(TEXT_COLOR);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        linearLayout2.addView(spinner);
        linLayout.addView(linearLayout2);
    }

    @SuppressLint({"SetTextI18n", "SuspiciousIndentation"})
    private void InputNum(LinearLayout linLayout, final int featNum, final String featName, final int maxValue) {
        final EditTextNum edittextnum = new EditTextNum();

        LinearLayout linearLayout = new LinearLayout(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParams.setMargins(15, 10, 15, 10);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(16);
        linearLayout.setPadding(10, 8, 8, 8);

        GradientDrawable InputNumgd = new GradientDrawable();
        InputNumgd.setStroke(3, Color.WHITE);
        InputNumgd.setCornerRadii(new float[]{30, 30, 0, 0, 30, 30, 0, 0});
        linearLayout.setBackground(InputNumgd);
        linearLayout.setLayoutParams(layoutParams);

        LayoutParams lp = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lp.weight = 1.0f;

        LinearLayout linearLayout2 = new LinearLayout(getContext);
        linearLayout2.setLayoutParams(lp);
        linearLayout2.setOrientation(LinearLayout.VERTICAL);
        linearLayout2.setGravity(Gravity.CENTER);

        TextView textView = new TextView(getContext);
        textView.setTextColor(TEXT_COLOR);
        textView.setText(featName);
        textView.setTextSize(14.0F);
        textView.setGravity(3);
        textView.setPadding(5, 0, 0, 0);

        final TextView textView2 = new TextView(getContext);
        int num = Preferences.loadPrefInt(getContext, featName, featNum);
        edittextnum.setNum((num == 0) ? 1 : num);
        textView2.setText(Html.fromHtml("-> " + "<font color='" + NumberTxtColor + "'>" + ((num == 0) ? 1 : num) + "</font>"));
        textView2.setTextColor(Color.WHITE);
        textView2.setTextSize(14.0F);
        textView2.setGravity(3);
        textView2.setPadding(5, 0, 0, 0);

        final Button button = new Button(getContext);
        button.setText("ENTER");
        GradientDrawable enter1 = new GradientDrawable();
        enter1.setColor(BTN_COLOR);
        enter1.setStroke(3, Color.WHITE);
        enter1.setCornerRadii(new float[]{30, 30, 0, 0, 30, 30, 0, 0});

        button.setBackground(enter1);
        button.setTextColor(TEXT_COLOR);

        button.setOnClickListener(view -> {
            final AlertDialog alert = new AlertDialog.Builder(getContext, 2).create();
            if (overlayRequired) {
                Objects.requireNonNull(alert.getWindow()).setType(Build.VERSION.SDK_INT >= 26 ? 2038 : 2002);
            }

            //LinearLayout
            LinearLayout linearLayout1 = new LinearLayout(getContext);
            linearLayout1.setPadding(5, 5, 5, 5);
            linearLayout1.setOrientation(LinearLayout.VERTICAL);
            GradientDrawable box2 = new GradientDrawable();
            box2.setColor(LST_MAB);
            box2.setStroke(4, Color.WHITE, 4, 4);
            linearLayout1.setBackground(box2);
            linearLayout1.setElevation(5.0F);

            //TextView
            final TextView titleText = new TextView(getContext);
            titleText.setText(Html.fromHtml("<u>" + featName + "</u>"));
            titleText.setGravity(Gravity.CENTER);
            titleText.setTypeface(Typeface.DEFAULT_BOLD);
            titleText.setTextColor(TEXT_COLOR);
            titleText.setTextSize(22f);

            //TextView
            final TextView TextViewNote = new TextView(getContext);
            TextViewNote.setGravity(Gravity.CENTER);
            TextViewNote.setTextSize(14.0F);
            TextViewNote.setText("Click \"Set Value\" button to apply changes || outside to cancel");
            TextViewNote.setPadding(10, 5, 10, 5);
            TextViewNote.setTextColor(TEXT_COLOR);

            LayoutParams lpl = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            lpl.weight = 1;

            //Edit text
            final EditText edittext = new EditText(getContext);
            edittext.setLayoutParams(lpl);
            edittext.setMaxLines(1);
            edittext.setHint("Write Value");
            edittext.setWidth(convertDipToPixels(300));
            edittext.setTextColor(TEXT_COLOR);
            edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
            edittext.setKeyListener(DigitsKeyListener.getInstance("0123456789-"));
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(10);
            edittext.setFilters(FilterArray);

            //Button
            LayoutParams layoutParams1 = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            layoutParams1.setMargins(20, 10, 20, 15);

            Button btndialog = new Button(getContext);
            btndialog.setLayoutParams(layoutParams1);
            GradientDrawable setvalue2 = new GradientDrawable();
            setvalue2.setColor(BTN_COLOR);

            setvalue2.setCornerRadii(new float[]{30, 30, 0, 0, 30, 30, 0, 0});
            setvalue2.setStroke(3, Color.WHITE);
            btndialog.setBackground(setvalue2);
            btndialog.setTextColor(TEXT_COLOR);
            btndialog.setPadding(15, 10, 15, 10);
            btndialog.setText("SET VALUE");
            btndialog.setOnClickListener(view1 -> {
                int num1;
                try {
                    num1 = Integer.parseInt(TextUtils.isEmpty(edittext.getText().toString()) ? "0" : edittext.getText().toString());
                    if (maxValue != 0 && num1 >= maxValue) {
                        num1 = maxValue;
                    }
                } catch (NumberFormatException ex) {
                    num1 = 2147483640;
                }

                edittextnum.setNum(num1);
                textView2.setText(Html.fromHtml("-> " + "<font color='" + NumberTxtColor + "'>" + num1 + "</font>"));
                alert.dismiss();
                Preferences.changeFeatureInt(getContext, featName, featNum, num1);
                edittext.setFocusable(false);
            });
            linearLayout1.addView(titleText);
            linearLayout1.addView(TextViewNote);
            linearLayout1.addView(edittext);
            linearLayout1.addView(btndialog);
            alert.setView(linearLayout1);
            alert.show();
        });

        linearLayout.addView(linearLayout2);
        linearLayout2.addView(textView);
        linearLayout2.addView(textView2);
        linearLayout.addView(button);

        linLayout.addView(linearLayout);
    }

    @SuppressLint({"SetTextI18n", "SuspiciousIndentation"})
    private void InputText(LinearLayout linLayout, final int featNum, final String featName) {
        final EditTextString edittextstring = new EditTextString();
        LinearLayout linearLayout = new LinearLayout(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParams.setMargins(15, 10, 15, 10);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(16);
        linearLayout.setPadding(10, 8, 8, 8);

        GradientDrawable Inputtextgd = new GradientDrawable();
        Inputtextgd.setStroke(3, Color.WHITE);
        Inputtextgd.setCornerRadii(new float[]{30, 30, 0, 0, 30, 30, 0, 0});
        linearLayout.setBackground(Inputtextgd);
        linearLayout.setLayoutParams(layoutParams);

        LayoutParams lp = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lp.weight = 1.0f;

        LinearLayout linearLayout2 = new LinearLayout(getContext);
        linearLayout2.setLayoutParams(lp);
        linearLayout2.setOrientation(LinearLayout.VERTICAL);
        linearLayout2.setGravity(Gravity.CENTER);

        TextView textView = new TextView(getContext);
        textView.setTextColor(TEXT_COLOR);
        textView.setText(featName);
        textView.setTextSize(14.0F);
        textView.setGravity(3);
        textView.setSingleLine(true);
        textView.setPadding(5, 0, 0, 0);

        final TextView textView2 = new TextView(getContext);
        String string = Preferences.loadPrefString(getContext, featName, featNum);
        edittextstring.setString(string);
        textView2.setText(Html.fromHtml("-> " + "<font color='" + NumberTxtColor + "'>" + string + "</font>"));

        textView2.setTextColor(Color.WHITE);
        textView2.setTextSize(14.0F);
        textView2.setGravity(3);
        textView2.setPadding(5, 0, 0, 0);

        final Button button = new Button(getContext);
        button.setText("ENTER");
        GradientDrawable enter = new GradientDrawable();
        enter.setColor(BTN_COLOR);
        enter.setStroke(3, Color.WHITE);
        enter.setCornerRadii(new float[]{30, 30, 0, 0, 30, 30, 0, 0});

        button.setBackground(enter);
        button.setTextColor(TEXT_COLOR);

        button.setOnClickListener(view -> {
            final AlertDialog alert = new AlertDialog.Builder(getContext, 2).create();
            if (overlayRequired) {
                Objects.requireNonNull(alert.getWindow()).setType(Build.VERSION.SDK_INT >= 26 ? 2038 : 2002);
            }

            //LinearLayout
            LinearLayout linearLayout1 = new LinearLayout(getContext);
            linearLayout1.setPadding(5, 5, 5, 5);
            linearLayout1.setOrientation(LinearLayout.VERTICAL);

            GradientDrawable box = new GradientDrawable();
            box.setColor(LST_MAB);
            box.setStroke(4, Color.WHITE, 4, 4);
            linearLayout1.setBackground(box);
            linearLayout1.setElevation(5.0F);

            //TextView
            final TextView titleText = new TextView(getContext);
            titleText.setText(Html.fromHtml("<u>" + featName + "</u>"));
            titleText.setGravity(Gravity.CENTER);
            titleText.setTypeface(Typeface.DEFAULT_BOLD);
            titleText.setTextColor(TEXT_COLOR);
            titleText.setTextSize(22f);

            //TextView
            final TextView TextViewNote = new TextView(getContext);
            TextViewNote.setGravity(Gravity.CENTER);
            TextViewNote.setTextSize(14.0F);
            TextViewNote.setText("Click \"Set Value\" button to apply changes || outside to cancel");
            TextViewNote.setPadding(10, 15, 10, 10);
            TextViewNote.setTextColor(TEXT_COLOR);

            LayoutParams lpl = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            lpl.weight = 1;

            //Edit text
            final EditText edittext = new EditText(getContext);
            edittext.setLayoutParams(lpl);
            edittext.setMaxLines(1);
            edittext.setHint("Write Text");
            edittext.setWidth(convertDipToPixels(300));
            edittext.setTextColor(TEXT_COLOR);
            edittext.setText(edittextstring.getString());

            //Button
            LayoutParams layoutParams1 = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            layoutParams1.setMargins(20, 10, 20, 15);

            Button btndialog = new Button(getContext);
            btndialog.setLayoutParams(layoutParams1);
            GradientDrawable setvalue = new GradientDrawable();
            setvalue.setColor(BTN_COLOR);

            setvalue.setCornerRadii(new float[]{30, 30, 0, 0, 30, 30, 0, 0});
            setvalue.setStroke(3, Color.WHITE);
            btndialog.setBackground(setvalue);
            btndialog.setTextColor(TEXT_COLOR);
            btndialog.setPadding(15, 10, 15, 10);
            btndialog.setText("SET VALUE");
            btndialog.setOnClickListener(view1 -> {
                String str = edittext.getText().toString();
                edittextstring.setString(edittext.getText().toString());
                textView2.setText(Html.fromHtml("-> " + "<font color='" + NumberTxtColor + "'>" + str + "</font>"));
                alert.dismiss();
                Preferences.changeFeatureString(getContext, featName, featNum, str);
                edittext.setFocusable(false);
            });
            linearLayout1.addView(titleText);
            linearLayout1.addView(TextViewNote);
            linearLayout1.addView(edittext);
            linearLayout1.addView(btndialog);
            alert.setView(linearLayout1);
            alert.show();
        });

        linearLayout.addView(linearLayout2);
        linearLayout2.addView(textView);
        linearLayout2.addView(textView2);
        linearLayout.addView(button);

        linLayout.addView(linearLayout);
    }

    @SuppressLint("ObsoleteSdkInt")
    private void CheckBox(LinearLayout linLayout, final int featNum, final String featName, boolean switchedOn) {
        final CheckBox checkBox = new CheckBox(getContext);
        checkBox.setText(featName);
        checkBox.setTextColor(TEXT_COLOR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            checkBox.setButtonTintList(ColorStateList.valueOf(CheckBoxColor));
        checkBox.setChecked(Preferences.loadPrefBool(getContext, featName, featNum, switchedOn));
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> Preferences.changeFeatureBool(getContext, featName, featNum, isChecked));
        linLayout.addView(checkBox);
    }

    @SuppressLint({"SetTextI18n", "ObsoleteSdkInt"})
    private void RadioButton(LinearLayout linLayout, final int featNum, String featName, final String list) {
        //Credit: LoraZalora
        final List<String> lists = new LinkedList<>(Arrays.asList(list.split(",")));

        final TextView textView = new TextView(getContext);
        textView.setText(featName + ":");
        textView.setTextColor(TEXT_COLOR);

        final RadioGroup radioGroup = new RadioGroup(getContext);
        radioGroup.setPadding(10, 5, 10, 5);
        radioGroup.setOrientation(LinearLayout.VERTICAL);
        radioGroup.addView(textView);

        for (int i = 0; i < lists.size(); i++) {
            final RadioButton Radioo = new RadioButton(getContext);
            final String finalfeatName = featName, radioName = lists.get(i);
            View.OnClickListener first_radio_listener = v -> {
                textView.setText(Html.fromHtml(finalfeatName + ": <font color='" + NumberTxtColor + "'>" + radioName));
                Preferences.changeFeatureInt(getContext, finalfeatName, featNum, radioGroup.indexOfChild(Radioo));
            };
            System.out.println(lists.get(i));
            Radioo.setText(lists.get(i));
            Radioo.setTextColor(Color.LTGRAY);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                Radioo.setButtonTintList(ColorStateList.valueOf(RadioColor));
            Radioo.setOnClickListener(first_radio_listener);
            radioGroup.addView(Radioo);
        }

        int index = Preferences.loadPrefInt(getContext, featName, featNum);
        if (index > 0) { //Preventing it to get an index less than 1. below 1 = null = crash
            textView.setText(Html.fromHtml(featName + ": <font color='" + NumberTxtColor + "'>" + lists.get(index - 1)));
            ((RadioButton) radioGroup.getChildAt(index)).setChecked(true);
        }
        linLayout.addView(radioGroup);
    }

    @SuppressLint("SetTextI18n")
    private void Collapse(LinearLayout linLayout, final String text, final boolean expanded) {
        LinearLayout.LayoutParams layoutParamsLL = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParamsLL.setMargins(0, 5, 0, 0);

        LinearLayout collapse = new LinearLayout(getContext);
        collapse.setLayoutParams(layoutParamsLL);
        collapse.setVerticalGravity(16);
        collapse.setOrientation(LinearLayout.VERTICAL);

        final LinearLayout collapseSub = new LinearLayout(getContext);
        collapseSub.setVerticalGravity(16);
        collapseSub.setPadding(0, 5, 0, 5);
        collapseSub.setOrientation(LinearLayout.VERTICAL);
        collapseSub.setBackgroundColor(Color.parseColor("#ABFF0000"));
        collapseSub.setVisibility(View.GONE);
        mCollapse = collapseSub;

        final TextView textView = new TextView(getContext);
        textView.setBackgroundColor(CategoryBG);
        textView.setText("▽ " + text + " ▽");
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(TEXT_COLOR);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setPadding(0, 20, 0, 20);

        if (expanded) {
            collapseSub.setVisibility(View.VISIBLE);
            textView.setText("△ " + text + " △");
        }

        textView.setOnClickListener(new View.OnClickListener() {
            boolean isChecked = expanded;

            @Override
            public void onClick(View v) {

                boolean z = !isChecked;
                isChecked = z;
                if (z) {
                    collapseSub.setVisibility(View.VISIBLE);
                    textView.setText("△ " + text + " △");
                    return;
                }
                collapseSub.setVisibility(View.GONE);
                textView.setText("▽ " + text + " ▽");
            }
        });
        collapse.addView(textView);
        collapse.addView(collapseSub);
        linLayout.addView(collapse);
    }

    private void Category(LinearLayout linLayout, String text) {
        TextView textView = new TextView(getContext);
        textView.setBackgroundColor(CategoryBG);
        textView.setText(Html.fromHtml(text));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(TEXT_COLOR);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setPadding(0, 5, 0, 5);
        linLayout.addView(textView);
    }

    private void TextView(LinearLayout linLayout, String text) {
        TextView textView = new TextView(getContext);
        textView.setText(Html.fromHtml(text));
        textView.setTextColor(TEXT_COLOR);
        textView.setPadding(10, 5, 10, 5);
        linLayout.addView(textView);
    }

    private void WebTextView(LinearLayout linLayout, String text) {
        WebView wView = new WebView(getContext);
        wView.loadData(text, "text/html", "utf-8");
        wView.setBackgroundColor(0x00000000); //Transparent
        wView.setPadding(0, 5, 0, 5);
        wView.getSettings().setDatabaseEnabled(false);
        linLayout.addView(wView);
    }

    private static class EditTextString {
        private String text;

        public void setString(String s) {
            text = s;
        }

        public String getString() {
            return text;
        }
    }

    private static class EditTextNum {
        int val;

        public void setNum(int i) {
            val = i;
        }
    }

    private boolean isViewCollapsed() {
        return rootFrame == null || mCollapsed.getVisibility() == View.VISIBLE;
    }

    //For our image a little converter
    private int convertDipToPixels(int i) {
        return (int) ((((float) i) * getContext.getResources().getDisplayMetrics().density) + 0.5f);
    }

    private int dp(int i) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) i, getContext.getResources().getDisplayMetrics());
    }

    public void setVisibility(int view) {
        if (rootFrame != null) {
            rootFrame.setVisibility(view);
            }
        }

    public void onDestroy() {
        if (rootFrame != null) {
            mWindowManager.removeView(rootFrame);
        }
    }
}
