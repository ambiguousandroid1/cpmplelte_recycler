package com.example.api_implement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class adapter_rectcler extends RecyclerView.Adapter<adapter_rectcler.myViewholder> {
    List<data_model> data;
    Context context;

    public adapter_rectcler(List<data_model> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public myViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyler, parent, false);
        return new myViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewholder holder, int position) {
        data_model model = data.get(position);
        Picasso.get()
                .load(model.getUrl()).into(holder.imageView);
        holder.title.setText(model.getTitle());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context,model.getTitle(),Toast.LENGTH_LONG).show();
                String title =model.getTitle();
                Toast.makeText(context,title,Toast.LENGTH_LONG).show();

            }
        });

    }
    public void removeItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(data_model item, int position) {
        data.add(position,item);
        notifyItemInserted(position);
    }

    public List<data_model> getData() {
        return data;
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    public void filterList(ArrayList<data_model> filteredlist) {
        data = filteredlist;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    public class myViewholder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, detail;

        public myViewholder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView_row);
            title = itemView.findViewById(R.id.title_row);
        }
    }
}
