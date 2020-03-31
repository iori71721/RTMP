package com.example.test.rmtp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.test.rmtp.R;

import java.util.ArrayList;
import java.util.List;

public class FriendAdapter extends BaseAdapter {
    private List<Item> friends=new ArrayList<>(100);
    private final Context context;

    public FriendAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public Item getItem(int position) {
        return friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView == null){
            convertView=View.inflate(context,R.layout.item_friend,null);
            holder=new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }
        Item viewItem=getFriends().get(position);
        holder.txv_name.setText(viewItem.getName());
        return convertView;
    }

    public void reloadFriends(List<Item> friends) {
        synchronized (this.friends){
            this.friends.clear();
            this.friends.addAll(friends);
        }

    }

    private List<Item> getFriends() {
        return friends;
    }

    public static class Item{
        private String name="";

        public Item(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private class ViewHolder{
        private TextView txv_name;
        public ViewHolder(View view) {
            txv_name=view.findViewById(R.id.txv_name);
        }
    }
}
