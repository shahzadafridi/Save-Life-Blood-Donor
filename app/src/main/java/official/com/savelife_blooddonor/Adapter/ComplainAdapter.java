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

import official.com.savelife_blooddonor.Model.Complain;
import official.com.savelife_blooddonor.R;

public class ComplainAdapter extends RecyclerView.Adapter<ComplainAdapter.MyViewHolder> {

    Context context;
    List<Complain> complainList;

    public ComplainAdapter(Context context, List<Complain> complainList) {
        this.context = context;
        this.complainList = complainList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.complain_item_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Complain complain = complainList.get(position);
        holder.name.setText(complain.getName());
        holder.phone.setText(complain.getPhone());
        holder.message.setText(complain.getMessage());
    }

    @Override
    public int getItemCount() {
        return complainList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView delete, message, phone, name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            delete = (TextView) itemView.findViewById(R.id.complain_item_delete);
            name = (TextView) itemView.findViewById(R.id.complain_item_name);
            message = (TextView) itemView.findViewById(R.id.complain_item_message);
            phone = (TextView) itemView.findViewById(R.id.complain_item_phone);
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            FirebaseDatabase.getInstance().getReference().child("complains").child(complainList.get(position).getPhone()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        complainList.remove(position);
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }
}
