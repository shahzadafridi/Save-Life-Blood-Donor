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

import official.com.savelife_blooddonor.Model.Donee;
import official.com.savelife_blooddonor.R;

public class DoneeAdapter extends RecyclerView.Adapter<DoneeAdapter.MyViewHolder> {

    Context context;
    List<Donee> doneeList;

    public DoneeAdapter(Context context, List<Donee> doneeList) {
        this.context = context;
        this.doneeList = doneeList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.donee_item_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Donee donee = doneeList.get(position);
        holder.name.setText(donee.getName());
        holder.fname.setText(donee.getFname());
        holder.phone.setText(donee.getPhone());
    }

    @Override
    public int getItemCount() {
        return doneeList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView delete, name, fname, phone;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            delete = (TextView) itemView.findViewById(R.id.donee_item_delete);
            name = (TextView) itemView.findViewById(R.id.donee_item_name);
            fname = (TextView) itemView.findViewById(R.id.donee_item_fname);
            phone = (TextView) itemView.findViewById(R.id.donee_item_phone);
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            FirebaseDatabase.getInstance().getReference().child("Donee").child(doneeList.get(position).getPhone()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        doneeList.remove(position);
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }
}
