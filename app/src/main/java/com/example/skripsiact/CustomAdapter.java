package com.example.skripsiact;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;


public class CustomAdapter extends ArrayAdapter<ValidationTest> {
    private Activity context;
    private List<ValidationTest> validationTestList;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private DatabaseReference reference5;
    private DatabaseReference reference2;
    private DatabaseReference reference3;
    private DatabaseReference reference6;
    private Task<Void> reference7;

    public CustomAdapter(Activity context, List<ValidationTest> validationTestList ) {
        super(context, R.layout.list_to_approval, validationTestList);
        this.context = context;
        this.validationTestList = validationTestList;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        final View view  = layoutInflater.inflate(R.layout.list_to_approval,null,true);
        final ValidationTest validationTest =  validationTestList.get(position);
        ImageView HealthCheck = view.findViewById(R.id.Healtcheck);
        TextView FullnameApp = view.findViewById(R.id.Fullnameapp);
        TextView TypeVaccine = view.findViewById(R.id.typevaccine);
        TextView StartDateApp = view.findViewById(R.id.startDateApp);
        TextView UntilDateApp = view.findViewById(R.id.endDateApp);
        TextView ViewPDF = view.findViewById(R.id.viewpdf);
        Button buttonApprove = view.findViewById(R.id.approve);
        Button buttonReject = view.findViewById(R.id.Reject);

        {
            if (HealthCheck.equals("default")) {
                Picasso.get().load(R.drawable.logopdf).into(HealthCheck);
            } else Picasso.get().load(validationTest.getHealth_Check_User()).into(HealthCheck);
        }

        HealthCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FullScreenDialog dialog = new FullScreenDialog(validationTest.getHealth_Check_User());
                FragmentTransaction ft = ((FragmentActivity)context).getFragmentManager().beginTransaction();
                dialog.show(ft, FullScreenDialog.TAG);
            }
        });

        ViewPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ViewPDF.getContext(),viewpdf.class);
                intent.putExtra("Health_Check_User",validationTest.getHealth_Check_User());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ViewPDF.getContext().startActivity(intent);
            }
        });
        Glide.with(context)
                .load(validationTest.getHealth_Check_User())
                .into(HealthCheck);

        FullnameApp.setText(validationTest.getEmail());
        TypeVaccine.setText(validationTest.getType_Vaccine());
        StartDateApp.setText(validationTest.getStart_Date());
        UntilDateApp.setText(validationTest.getEnd_Date());

        buttonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference().child("User").child(validationTest.getUserId());
                reference5 = database.getReference().child("Validation Data Test").child(validationTest.getUserId());
                reference5.removeValue();


                reference.child("Status").setValue("Inactive");
                reference.child("Valid_Date").setValue("Pending");
                Toast.makeText(context, "Data Deleted", Toast.LENGTH_SHORT).show();
            }
        });

        buttonApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                database = FirebaseDatabase.getInstance();
                reference2 = database.getReference().child("User").child(validationTest.getUserId());
                reference3 = database.getReference().child("Validation Data Test").child(validationTest.getUserId());

                reference2.child("Status").setValue("Active");
                reference2.child("Valid_Date").setValue(validationTest.getEnd_Date());

                Toast.makeText(context, "Data Approved", Toast.LENGTH_SHORT).show();
                reference3.removeValue();
            }
        });
        return view;
    }
}
