package official.com.savelife_blooddonor.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import official.com.savelife_blooddonor.Model.Request;
import official.com.savelife_blooddonor.R;
import official.com.savelife_blooddonor.Screens.BloodRequest.PostRequestActivity;
import official.com.savelife_blooddonor.Screens.Registration.RegistrationMenu;
import official.com.savelife_blooddonor.Util.AppConstants;

public class RequestAdapterAdmin extends RecyclerView.Adapter<RequestAdapterAdmin.MyViewHolder> {

    Context context;
    List<Request> requests;
    Request request;
    String TAG = "RequestAdapter";

    public RequestAdapterAdmin(Context context, List<Request> request) {
        this.context = context;
        this.requests = request;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.request_item_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        request = requests.get(position);
        holder.name.setText(request.getName());
        holder.bgroup.setText(request.getBgroup());
        holder.message.setText(request.getMessage());
        holder.address.setText(request.getAddress());
        holder.contact.setText(request.getPhone());
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name, address, message, bgroup, delete, contact;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.request_item_name);
            message = (TextView) itemView.findViewById(R.id.reqeust_item_message);
            address = (TextView) itemView.findViewById(R.id.reqeust_item_Address);
            bgroup = (TextView) itemView.findViewById(R.id.reqeust_item_Bgroup);
            delete = (TextView) itemView.findViewById(R.id.reqeust_item_delete);
            contact = (TextView) itemView.findViewById(R.id.request_item_phone);
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            FirebaseDatabase.getInstance().getReference().child("Request").child(requests.get(position).getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        requests.remove(position);
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }

}
