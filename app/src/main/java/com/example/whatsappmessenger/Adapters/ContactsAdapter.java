package com.example.whatsappmessenger.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappmessenger.Activities.ChatRoomActivity;
import com.example.whatsappmessenger.Activities.ChatsActivity;
import com.example.whatsappmessenger.Activities.FriendsActivity;
import com.example.whatsappmessenger.R;
import com.example.whatsappmessenger.Classes.Contact;

import java.io.Serializable;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {

    Context context;
    List<Contact> contactList;

    public ContactsAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_contacts_list,parent,false);
        return new ContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsViewHolder holder, final int position) {
        holder.nameTextView.setText(contactList.get(position).getContactNames());
        holder.aboutTextView.setText(contactList.get(position).getContactAbout());
        Glide.with(context).load(contactList.get(position).getContactImage())
//                            .apply(RequestOptions.circleCropTransform())
                            .centerInside()
                            .dontAnimate()
                            .into(holder.contactImageView);

        holder.contactsConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatRoomActivity.class);

                intent.putExtra("name",contactList.get(position).getContactNames());
                intent.putExtra("image",contactList.get(position).getContactImage());
                intent.putExtra("number",contactList.get(position).getContactPhoneNumber());
                intent.putExtra("contacts_map", (Serializable) ((FriendsActivity)context).contactsMap);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ContactsViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout contactsConstraintLayout;
        TextView nameTextView,aboutTextView;
        ImageView contactImageView;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            contactsConstraintLayout = itemView.findViewById(R.id.contactConstraintLayout);
            nameTextView = itemView.findViewById(R.id.numberTextView);
            aboutTextView = itemView.findViewById(R.id.aboutTextView);
            contactImageView = itemView.findViewById(R.id.contactImageView);
        }
    }
}
