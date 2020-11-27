package com.example.halloworld.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.halloworld.R;

import java.util.ArrayList;

public class AdapterClassFriends extends RecyclerView.Adapter<AdapterClassFriends.MyViewHolder> {

    ArrayList<String> list;
    private FriendsClickInterface searchbarClickInterface;
    public AdapterClassFriends(ArrayList<String> list, FriendsClickInterface searchbarClickInterface){
        this.list= list;
        this.searchbarClickInterface= searchbarClickInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_holder_friends,parent,false);
        return new MyViewHolder(view,searchbarClickInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.friend.setText(list.get(position));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView friend;

        FriendsClickInterface searchbarClickInterface;
        public MyViewHolder(@NonNull View itemView, FriendsClickInterface searchbarClickInterface) {
            super(itemView);
            friend= itemView.findViewById(R.id.friendID);
            this.searchbarClickInterface=searchbarClickInterface;
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            searchbarClickInterface.onFriendsClickListener(getAdapterPosition());
        }
    }

   public interface FriendsClickInterface{
         void onFriendsClickListener(int position);
    }
}
