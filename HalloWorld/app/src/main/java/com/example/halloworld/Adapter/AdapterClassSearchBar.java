package com.example.halloworld.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.halloworld.R;
import com.example.halloworld.Model.User;

import java.util.ArrayList;

public class AdapterClassSearchBar extends RecyclerView.Adapter<AdapterClassSearchBar.MyViewHolder> {

    ArrayList<User> list;
    private SearchbarClickInterface searchbarClickInterface;
    public AdapterClassSearchBar(ArrayList<User> list, SearchbarClickInterface searchbarClickInterface){
        this.list= list;
        this.searchbarClickInterface= searchbarClickInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_holder,parent,false);
        return new MyViewHolder(view,searchbarClickInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.id.setText(list.get(position).getName());
        holder.description.setText(list.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView id;
        TextView description;
        SearchbarClickInterface searchbarClickInterface;
        public MyViewHolder(@NonNull View itemView, SearchbarClickInterface searchbarClickInterface) {
            super(itemView);
            id= itemView.findViewById(R.id.userID);
            description= itemView.findViewById(R.id.description);
            this.searchbarClickInterface=searchbarClickInterface;
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            searchbarClickInterface.onSearchbarClickListener(getAdapterPosition());
        }
    }

   public interface SearchbarClickInterface{
         void onSearchbarClickListener(int position);
    }
}
