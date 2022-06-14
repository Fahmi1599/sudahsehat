package com.example.skripsiact;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HistoryAdapter extends ArrayAdapter<History> {
    private Activity context;
    private List<History> historyList;
    private FirebaseDatabase database;
    private DatabaseReference reference,reference5,reference2,reference3;

    public HistoryAdapter(Activity context, List<History> historyList ) {
        super(context, R.layout.activity_history_user, historyList);
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        final View view  = layoutInflater.inflate(R.layout.activity_history_adapter,null,true);
        final History history =  historyList.get(position);
        ImageView HealthCheck = view.findViewById(R.id.Healtcheck2);
        TextView StatusUpload = view.findViewById(R.id.DocStatus);
        TextView UploadAt = view.findViewById(R.id.DocUpload);
        TextView ViewPDF = view.findViewById(R.id.viewpdf2);

        {
            if (HealthCheck.equals("default")) {
                Picasso.get().load(R.drawable.logopdf).into(HealthCheck);
            } else Picasso.get().load(history.getHealth_Check_User()).into(HealthCheck);
        }

        HealthCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FullScreenDialog dialog = new FullScreenDialog(history.getHealth_Check_User());
                FragmentTransaction ft = ((FragmentActivity)context).getFragmentManager().beginTransaction();
                dialog.show(ft, FullScreenDialog.TAG);
            }
        });

        ViewPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ViewPDF.getContext(),viewpdf.class);
                intent.putExtra("Health_Check_User",history.getHealth_Check_User());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ViewPDF.getContext().startActivity(intent);
            }
        });
        Glide.with(context)
                .load(history.getHealth_Check_User())
                .into(HealthCheck);

        StatusUpload.setText(history.getStatus());
        UploadAt.setText("Di Upload Pada" + " "+ history.getStart_Date());

        return view;
    }
}