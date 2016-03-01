package com.example.julie.applestoapples;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import java.util.List;

/**
 * Created by Julie on 2/29/16.
 */

public class ButtonAdapter extends BaseAdapter {
    List<Card> mCards;
    Player mplayer;
    Context mContext;

    public ButtonAdapter(List<Card> c, Context context){
        mCards = c;
        mContext = context;
    }

    public int getCount(){
        return mCards.size();
    }
    public Object getItem(int position){
        return null;
    }

    public long getItemId(int position){
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Button button;
        if (convertView == null) {
            button = new Button(mContext);
            button.setLayoutParams(new GridView.LayoutParams(600,100));
            button.setPadding(5, 2, 5, 2);
        }else{
            button = (Button) convertView;
        }
        button.setText(mCards.get(position).mName);
        return button;
    }


}