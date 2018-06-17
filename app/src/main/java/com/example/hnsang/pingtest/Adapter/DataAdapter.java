package com.example.hnsang.pingtest.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hnsang.pingtest.Activity.GraphPingActivity;
import com.example.hnsang.pingtest.Object.Constant;
import com.example.hnsang.pingtest.Object.DataObject;
import com.example.hnsang.pingtest.Object.MainDataObject;
import com.example.hnsang.pingtest.R;
import java.util.List;

/**
 * Created by Ho Ngoc Sang on 13/06/2018.
 */
public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder> {

    private Context context;
    private List<MainDataObject> listData;

    public DataAdapter(Context context, List<MainDataObject> listData) {
        this.context = context;
        this.listData = listData;
    }


    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_data, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        final MainDataObject dataObject = listData.get(position);
        SharedPreferences preferences = context.getSharedPreferences(Constant.PREFERENCE_NAME, context.MODE_PRIVATE);
        final String id = preferences.getString(Constant.PREFERENCE_KEY_ID, null);

        if (dataObject != null) {
            holder.tvIdDevice.setText("Thiết bị: "+dataObject.getIdDevice());
            holder.tvUserName.setText("Người dùng: "+dataObject.getUsername());
            holder.tvTimeStart.setText(dataObject.getTimeStart());

            holder.cvData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, GraphPingActivity.class);
                    intent.putExtra("idDevice",dataObject.getIdDevice());
                    intent.putExtra("idUserName",dataObject.getUsername());
                    intent.putExtra("idTimeStart",dataObject.getTimeStart());
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {
        private TextView tvIdDevice;
        private TextView tvUserName;
        private TextView tvTimeStart;
        private CardView cvData;

        public DataViewHolder(View itemView) {
            super(itemView);

            tvIdDevice = itemView.findViewById(R.id.tv_id_device);
            tvUserName = itemView.findViewById(R.id.tv_username);
            tvTimeStart = itemView.findViewById(R.id.tv_time);
            cvData = itemView.findViewById(R.id.cv_data);
        }
    }
}
