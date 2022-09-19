package dev.example.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView AmountText;
    private RecyclerView recycle;
    private FloatingActionButton fab;

    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private String onlineUserId = "";
    private ProgressDialog loader;


    private TodaysitemAdapter todaysitemAdapter;
    private List<Data> myDataList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Today's Amount");

        AmountText = findViewById(R.id.Amount);


        fab = findViewById(R.id.fab);

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference().child("expenses").child(onlineUserId);
        loader = new ProgressDialog(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    addIntemOnSpent();
            }
        });

        recycle = findViewById(R.id.recycleview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recycle.setHasFixedSize(true);
        recycle.setLayoutManager(linearLayoutManager);

        myDataList = new ArrayList<>();
        todaysitemAdapter = new TodaysitemAdapter(HomeActivity.this,myDataList);
        recycle.setAdapter(todaysitemAdapter);
        
        readItems();

    }

    private void readItems() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("expenses").child(onlineUserId);
        Query query = reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Data data = dataSnapshot.getValue(Data.class);
                    myDataList.add(data);
                }

                todaysitemAdapter.notifyDataSetChanged();
                int totalAmount= 0;
                for(DataSnapshot ds : snapshot.getChildren()){
                    Map<String,Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    int Ptotal  = Integer.parseInt(String.valueOf(total));
                    totalAmount += Ptotal;

                    AmountText.setText("Total day's Spending : ₹ " + totalAmount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addIntemOnSpent() {
        AlertDialog.Builder  myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater =  LayoutInflater.from(this);

        View myView = inflater.inflate(R.layout.input_layout,null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final Spinner itemsSpinner = myView.findViewById(R.id.Spinner);
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this,
               android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.items));

        itemsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemsSpinner.setAdapter(itemsAdapter);


        final EditText amount = myView.findViewById(R.id.amount);
        final EditText notes = myView.findViewById(R.id.notes);

        final Button save = myView.findViewById(R.id.save);
        final Button cancel = myView.findViewById(R.id.cancel);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mAmount = amount.getText().toString();
                String mNotes = notes.getText().toString();
                String item = itemsSpinner.getSelectedItem().toString();

                if (mAmount.isEmpty()){
                    amount.setError("Amount Required");
                    return;
                }

                if (mNotes.isEmpty()){
                    notes.setError("Notes Required");
                    return;
                }

                if (item.equals("select item")){
                    Toast.makeText(HomeActivity.this, "Select A Valid item", Toast.LENGTH_SHORT).show();
                }
                else{
                    loader.setMessage("Adding Item to database");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String id = ref.push().getKey();

                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal = Calendar.getInstance();
                    String date = dateFormat.format(cal.getTime());


                    Data data = new Data(item,date,id,mNotes,Integer.parseInt(mAmount));

                    ref.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(HomeActivity.this, "item Added Successfully !!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }else{
                                Toast.makeText(HomeActivity.this, "Faild to Add items", Toast.LENGTH_SHORT).show();
                                 dialog.dismiss();
                            }
                            loader.dismiss();
                        }
                    });
                }
                loader.dismiss();
            }
        });


         cancel.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 dialog.dismiss();
             }
         });

          dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.my_account){
            Intent i = new Intent(HomeActivity.this,AccountActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}