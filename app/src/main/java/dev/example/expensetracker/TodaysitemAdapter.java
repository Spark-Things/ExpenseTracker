package dev.example.expensetracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class TodaysitemAdapter extends RecyclerView.Adapter<TodaysitemAdapter.ViewHolder>{

    private Context mContext;
    private List<Data> myDataList;

    private String postid;
    private String note;
    private int amount;
    private String item;

    public TodaysitemAdapter(Context mContext, List<Data> myDataList) {
        this.mContext = mContext;
        this.myDataList = myDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.retrive_layout,parent,false);
        return new TodaysitemAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
             Data data = myDataList.get(position);
//            Log.w("POS", String.valueOf(position));
            holder.item.setText("Item: " +data.getItem());
             holder.amount.setText("Spent: ₹" + data.getAmount());
            holder.date.setText("Today: " + data.getDate());
            holder.notes.setText("Note: " + data.getNotes());

//            Log.d("DATA",data.getItem());
//
                switch (data.getItem()) {
                    case "Transport":
                        holder.imageView.setImageResource(R.drawable.ic_transport);
                        break;

                    case "Food":
                        holder.imageView.setImageResource(R.drawable.ic_food);
                        break;

                    case "Entertainment":
                        holder.imageView.setImageResource(R.drawable.ic_mv);
                        break;

                    case "Other":
                        holder.imageView.setImageResource(R.drawable.ic_transport);
                        break;
                    default:
                        holder.imageView.setImageResource(R.drawable.ic_transport);
                }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    postid = data.getId();
                    note = data.getNotes();
                    amount = data.getAmount();
                    item = data.getItem();

                    updateData();
                }
            });

    }

    private void updateData() {
        AlertDialog.Builder myDailog = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View myView = inflater.inflate(R.layout.update_layout,null);

        myDailog.setView(myView);

        final AlertDialog dialog = myDailog.create();

        final TextView mItem = myView.findViewById(R.id.item);
        final EditText mAmount = myView.findViewById(R.id.amount);
        final EditText mNote = myView.findViewById(R.id.notes);
          mItem.setText(item);
          mAmount.setText(String.valueOf(amount));
          mAmount.setSelection(String.valueOf(amount).length());
          mNote.setText(note);
          mNote.setSelection(String.valueOf(note).length());
            Button updatebtn = myView.findViewById(R.id.Update);
            Button cancel = myView.findViewById(R.id.cancel);

         updatebtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 amount = Integer.parseInt(mAmount.getText().toString());
                 note = mNote.getText().toString();

                 DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                 Calendar cal = Calendar.getInstance();
                 String date = dateFormat.format(cal.getTime());

                 Data data = new Data(item,date,postid,note,amount);
                 DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("expenses").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                 reference.child(postid).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                              if (task.isSuccessful()){
                                  Toast.makeText(mContext, "Updated", Toast.LENGTH_SHORT).show();
                              }else {
                                  Toast.makeText(mContext, "Fail to update", Toast.LENGTH_SHORT).show();
                              }
                     }
                 });
                 dialog.dismiss();
             }
         });
          dialog.show();
    }

    @Override
    public int getItemCount() {
        Log.w("SIZE", String.valueOf(myDataList.size()));
        return myDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        public TextView item,amount,notes,date;
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.item);
            amount = itemView.findViewById(R.id.amount);
            notes = itemView.findViewById(R.id.note);
            date = itemView.findViewById(R.id.date);

            imageView = itemView.findViewById(R.id.imageView);

        }
    }
}
