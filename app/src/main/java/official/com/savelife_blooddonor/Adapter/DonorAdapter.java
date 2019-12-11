package official.com.savelife_blooddonor.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;
import official.com.savelife_blooddonor.Model.Donor;
import official.com.savelife_blooddonor.R;

public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.MyViewHolder> {

    Context context;
    List<Donor> donorList;

    public DonorAdapter(Context context, List<Donor> donorList) {
        this.context = context;
        this.donorList = donorList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.donor_item_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Donor donor = donorList.get(position);
        holder.name.setText(donor.getName());
        holder.email.setText(donor.getEmail());
        holder.age.setText(donor.getAge());
        holder.bgroup.setText(donor.getBgroup());
        holder.gender.setText(donor.getGender());
        holder.status.setText(donor.getStatus());
        holder.phone.setText(donor.getPhone());
    }

    @Override
    public int getItemCount() {
        return donorList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView delete, name, age,bgroup,gender,status, phone,email;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            delete = (TextView) itemView.findViewById(R.id.donor_item_delete);
            name = (TextView) itemView.findViewById(R.id.donor_item_name);
            age = (TextView) itemView.findViewById(R.id.donor_item_age);
            bgroup = (TextView) itemView.findViewById(R.id.donor_item_bgroup);
            gender = (TextView) itemView.findViewById(R.id.donor_item_gender);
            status = (TextView) itemView.findViewById(R.id.donor_item_status);
            email = (TextView) itemView.findViewById(R.id.donor_item_email);
            phone = (TextView) itemView.findViewById(R.id.donor_item_phone);
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            FirebaseDatabase.getInstance().getReference().child("Donor").child(donorList.get(position).getPhone()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        donorList.remove(position);
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }
}
