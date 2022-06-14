package com.example.skripsiact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;

    ArrayList<User> list;


    public MyAdapter(Context context, ArrayList<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return  new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        User user = list.get(position);
        holder.Email.setText(user.getEmail());
        holder.Fullname.setText(user.getFullname());
        holder.StatusUser.setText(user.getStatus());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Email, Fullname, Age , StatusUser;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Email = itemView.findViewById(R.id.tvEmailall);
            Fullname = itemView.findViewById(R.id.tvFullnameall);
            StatusUser = itemView.findViewById(R.id.tvstatus);

        }
    }

}
