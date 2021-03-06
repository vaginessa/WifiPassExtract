package com.zigapk.wifipassextract;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CardHolder {
    CardView cardView;
    Network network;
    private TextView hiddenTv;
    public String hiddenText;
    private Context context;
    private boolean focused = false;
    private float defaultElevation = 5;
    public boolean hidePass = false; //true when hidden wifi networks occur


    public CardHolder(Network network, Context context) {
        this.network = network;
        this.context = context;

        CardView result = new CardView(context);
        TableRow.LayoutParams cardParams = new TableRow.LayoutParams();
        cardParams.height = TableRow.LayoutParams.WRAP_CONTENT;
        cardParams.width = TableRow.LayoutParams.MATCH_PARENT;
        cardParams.setMargins(32, 8, 32, 16);
        cardParams.gravity = Gravity.CENTER;
        result.setLayoutParams(cardParams);

        LinearLayout contentLayout = new LinearLayout(context);
        TableRow.LayoutParams linParams = new TableRow.LayoutParams();
        linParams.height = TableRow.LayoutParams.WRAP_CONTENT;
        linParams.width = TableRow.LayoutParams.MATCH_PARENT;
        linParams.gravity = Gravity.CENTER_HORIZONTAL;
        contentLayout.setLayoutParams(linParams);
        contentLayout.setOrientation(LinearLayout.VERTICAL);


        if (network.ssid != null) contentLayout.addView(getTitle(network.ssid));
        if (network.key_mgmt != null)
            contentLayout.addView(getLayoutForParam("Security", network.key_mgmt, false));
        if (network.eap != null)
            contentLayout.addView(getLayoutForParam("Eap", network.eap, false));
        if (network.psk != null) {
            contentLayout.addView(getLayoutForParam("Password", "********", true));
            hiddenText = network.psk;
        }
        if (network.identity != null)
            contentLayout.addView(getLayoutForParam("Identity", network.identity, false));
        if (network.anonymous_identity != null)
            contentLayout.addView(getLayoutForParam("Anonymous identity", network.anonymous_identity, false));
        if (network.password != null) {
            contentLayout.addView(getLayoutForParam("Password", "********", true));
            hiddenText = network.password;
        }

        result.addView(contentLayout);

        result.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    setFocused(!isFocused(), true);
                    if (isFocused()) unfocusAllButMe(MainActivity.cardHolders);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            result.setElevation(defaultElevation);
        }

        hidePass = HiddenNetworks.shouldBeHidden(this);

        this.cardView = result;
    }

    private void unfocusAllButMe(ArrayList<CardHolder> list) {
        for (CardHolder current : list) {
            if (current.cardView != cardView) current.setFocused(false, false);
        }
    }

    public boolean isFocused() {
        return focused;
    }

    public void setFocused(boolean focused, boolean onCLickUnfocus) {
        this.focused = focused;
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            if (focused) cardView.animate().z(25);
            else cardView.animate().z(defaultElevation);
        } else {
            if (focused) cardView.setCardBackgroundColor(Color.LTGRAY);
            else cardView.setCardBackgroundColor(Color.WHITE);
        }

        if (hiddenText != null && !hidePass) {
            if (focused) hiddenTv.setText(hiddenText);
            else hiddenTv.setText("********");
        }else if(hidePass  && focused){
            Toast.makeText(context, "Password for this network won't be shown.", Toast.LENGTH_SHORT).show();
        }

        if(onCLickUnfocus && !focused) MainActivity.fab.setEnabled(false);
        else if(focused) {
            if(network.password != null || network.psk != null) MainActivity.fab.setEnabled(true);
            else MainActivity.fab.setEnabled(false);
        }

        if(focused) {
            MainActivity.currentCardHolder = this;
        }
    }

    private TextView getTitle(String title) {
        TextView result = new TextView(context);
        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.width = TableRow.LayoutParams.MATCH_PARENT;
        params.height = TableRow.LayoutParams.WRAP_CONTENT;
        result.setText(title);
        result.setTextSize(18);
        result.setTextColor(Color.BLACK);
        result.setGravity(Gravity.CENTER_HORIZONTAL);
        result.setTextAlignment(TextView.TEXT_ALIGNMENT_GRAVITY);
        result.setLayoutParams(params);
        return result;
    }

    private LinearLayout getLayoutForParam(String tag, String content, boolean setAsTv) {
        LinearLayout result = new LinearLayout(context);
        TableRow.LayoutParams resultParams = new TableRow.LayoutParams();
        resultParams.width = TableRow.LayoutParams.MATCH_PARENT;
        resultParams.height = TableRow.LayoutParams.WRAP_CONTENT;
        resultParams.setMargins(0, 0, 0, 2);
        resultParams.gravity = Gravity.CENTER_HORIZONTAL;
        result.setLayoutParams(resultParams);
        result.setOrientation(LinearLayout.HORIZONTAL);

        TextView left = new TextView(context);
        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.width = TableRow.LayoutParams.WRAP_CONTENT;
        params.height = TableRow.LayoutParams.WRAP_CONTENT;
        params.weight = 1;
        left.setText(tag + ":");
        left.setTextColor(Color.GRAY);
        left.setGravity(Gravity.RIGHT);
        left.setTextAlignment(TextView.TEXT_ALIGNMENT_GRAVITY);
        left.setLayoutParams(params);

        TextView right = new TextView(context);
        TableRow.LayoutParams params2 = new TableRow.LayoutParams();
        params2.width = TableRow.LayoutParams.WRAP_CONTENT;
        params2.height = TableRow.LayoutParams.WRAP_CONTENT;
        params2.weight = 1;
        right.setText(" " + content);
        right.setTextColor(Color.DKGRAY);
        right.setGravity(Gravity.LEFT);
        right.setTextAlignment(TextView.TEXT_ALIGNMENT_GRAVITY);
        right.setLayoutParams(params2);

        result.addView(left);
        result.addView(right);

        if (setAsTv) hiddenTv = right;

        return result;
    }
}
