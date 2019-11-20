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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import official.com.savelife_blooddonor.Model.Request;
import official.com.savelife_blooddonor.R;
import official.com.savelife_blooddonor.Screens.BloodRequest.PostRequestActivity;
import official.com.savelife_blooddonor.Screens.BloodRequest.RequestActivity;
import official.com.savelife_blooddonor.Screens.Registration.RegistrationMenu;
import official.com.savelife_blooddonor.Util.AppConstants;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.MyViewHolder> {

    Context context;
    List<Request> requests;
    Request request;
    String TAG = "RequestAdapter";
    String str_role;

    public RequestAdapter(Context context, List<Request> requests, String str_role) {
        this.context = context;
        this.requests = requests;
        this.str_role = str_role;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.request_item, parent, false);
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
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name, address, message, bgroup, edit, delete, contact;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.request_name);
            message = (TextView) itemView.findViewById(R.id.request_message);
            address = (TextView) itemView.findViewById(R.id.request_address);
            bgroup = (TextView) itemView.findViewById(R.id.request_bgroup);
            edit = (TextView) itemView.findViewById(R.id.request_edit);
            delete = (TextView) itemView.findViewById(R.id.request_delete);
            contact = (TextView) itemView.findViewById(R.id.request_contact);
            contact.setOnClickListener(this);
            edit.setOnClickListener(this);
            delete.setOnClickListener(this);

            if (str_role.contentEquals("donee")) {
                edit.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
            } else {
                edit.setVisibility(View.INVISIBLE);
                delete.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Request request = requests.get(position);

            if (v.getId() == R.id.request_contact) {

                if (AppConstants.isUserLogin(context)) {
                    if (AppConstants.checkPhonePermission(context)) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + request.getPhone()));
                        context.startActivity(intent);
                    }
                } else {
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Want to contact?")
                            .setContentText("Please login first then contact")
                            .setConfirmText("Login")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    Intent intent = new Intent(context, RegistrationMenu.class);
                                    intent.putExtra("contact","contact");
                                    context.startActivity(intent);
                                }
                            });
                    sweetAlertDialog.show();
                }

            } else if (v.getId() == R.id.request_edit) {
                Intent intent = new Intent(context, PostRequestActivity.class);
                intent.putExtra("type", "update");
                intent.putExtra("id", request.getId());
                intent.putExtra("name", request.getName());
                intent.putExtra("message", request.getMessage());
                intent.putExtra("address", request.getAddress());
                intent.putExtra("bgroup", request.getBgroup());
                context.startActivity(intent);
            } else if (v.getId() == R.id.request_delete) {
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Won't be able to recover this post!")
                        .setConfirmText("Yes,delete it!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                FirebaseDatabase.getInstance().getReference("Request").child(request.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isComplete()) {
                                            Log.e(TAG, "Post deleted successfully.");
                                            sDialog.dismissWithAnimation();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, e.getMessage());
                                    }
                                });

                            }
                        });
                sweetAlertDialog.show();
            }
        }
    }

    public void setRequestList(List<Request> requests) {
        this.requests = requests;
        notifyDataSetChanged();
    }

}
