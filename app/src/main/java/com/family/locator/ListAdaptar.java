package com.family.locator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class ListAdaptar extends RecyclerView.Adapter<ListAdaptar.ViewHolder> {
    List<String> listData = null;
    private DBHelper dbHelper;
    private RecyclerView recyclerView;
    private MainActivity activity;
    public ListAdaptar(List<String> listdata,DBHelper dbHelper,RecyclerView recyclerView,MainActivity activity) {

        this.listData = listdata;
        this.dbHelper = dbHelper;

        this.recyclerView = recyclerView;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String myListData = listData.get(position);
        holder.textView.setText(myListData);

        holder.itemView.findViewById(R.id.deleteItemButton).setTag(position);

        holder.itemView.findViewById(R.id.deleteItemButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int idx = (int) view.getTag();

                String phoneText = listData.get(idx);
                dbHelper.deleteNumber(phoneText);
                notifyItemRemoved(holder.getAdapterPosition());
                listData.remove(idx);

            }
        });
    }


    @Override
    public int getItemCount() {
        return listData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.textView = (TextView) itemView.findViewById(R.id.textView);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }
}
