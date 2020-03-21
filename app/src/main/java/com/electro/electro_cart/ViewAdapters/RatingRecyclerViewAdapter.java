package com.electro.electro_cart.ViewAdapters;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.electro.electro_cart.R;
import com.electro.electro_cart.models.Rating;
import com.electro.electro_cart.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;

public class RatingRecyclerViewAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context context;
    List<Rating> ratingList;
    String id;

    private static final int ADD_RATING_LAYOUT = 0;
    private static final int RATING_LAYOUT = 1;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final CollectionReference collectionReferenceProduct = db.collection("products");

    private final CollectionReference collectionReferenceUser = db.collection("users");

    private final DocumentReference documentReferenceUser = db.collection("users")
            .document(firebaseAuth.getCurrentUser().getUid());

    private final CollectionReference collectionReferenceRatings;

    public RatingRecyclerViewAdapter(Context context, List<Rating> ratingList, String id) {
        this.context = context;
        this.ratingList = ratingList;
        this.id = id;

        collectionReferenceRatings=collectionReferenceProduct.document(id).collection("ratings");
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;

        if (viewType==ADD_RATING_LAYOUT){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rating_add_rating, parent, false);

            viewHolder = new AddRatingViewHolder(view);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rating_single_rating, parent, false);

            viewHolder = new RatingViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Rating rating=null;
        final User[] user = {null};

        if (position>0){
            rating=ratingList.get(position-1);
        }

        if (holder.getItemViewType()==ADD_RATING_LAYOUT){
            AddRatingViewHolder addRatingViewHolder=(AddRatingViewHolder)holder;

            addRatingViewHolder.buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int stars=(int) addRatingViewHolder.ratingBar.getRating();
                    String header=addRatingViewHolder.textInputEditTextHeader.getText().toString();
                    String description=addRatingViewHolder.textInputEditTextDescription.getText().toString();

                    if (stars==0|header.isEmpty()||description.isEmpty()){
                        if (header.isEmpty()){
                            addRatingViewHolder.textInputLayoutHeader.setError("Enter a Heading");
                        }else {
                            addRatingViewHolder.textInputLayoutHeader.setError(null);
                        }
                        if (description.isEmpty()){
                            addRatingViewHolder.textInputLayoutDescription.setError("Add a description");
                        }else {
                            addRatingViewHolder.textInputLayoutDescription.setError(null);
                        }
                    }else {
                        Rating rating1=Rating.builder()
                                .header(header)
                                .description(description)
                                .score(stars)
                                .votes(0)
                                .ownerId(firebaseAuth.getCurrentUser().getUid()).build();

                        collectionReferenceRatings.add(rating1)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.e("Rating Add", "DocumentSnapshot written with ID: " + documentReference.getId());
                                        documentReference.get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()){
                                                            Rating rating2=task.getResult().toObject(Rating.class);
                                                            ratingList.add(rating2);

                                                            addRatingViewHolder.ratingBar.setRating(0);

                                                            addRatingViewHolder.textInputLayoutHeader.setError(null);
                                                            addRatingViewHolder.textInputEditTextHeader.setText("");

                                                            addRatingViewHolder.textInputLayoutDescription.setError(null);
                                                            addRatingViewHolder.textInputEditTextDescription.setText("");

                                                            notifyDataSetChanged();
                                                        }else {
                                                            Toast.makeText(context,"Error adding Rating",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("Question Add", "Error adding document", e);
                                    }
                                });
                    }
                }
            });
        }else {
            RatingViewHolder ratingViewHolder=(RatingViewHolder)holder;

            ratingViewHolder.textViewVotes.setText(String.valueOf(rating.getVotes()));

            ratingViewHolder.ratingBar.setRating(rating.getScore());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm");
            String date = simpleDateFormat.format(rating.getTimestamp());

            collectionReferenceUser.document(rating.getOwnerId()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                user[0] = task.getResult().toObject(User.class);
                                ratingViewHolder.textViewNameDate.setText("by " + user[0].getName() + " on " + date);

                                if (user[0].getId().equals(firebaseAuth.getCurrentUser().getUid())) {
                                    ratingViewHolder.imageViewDelete.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Log.e("Raing", "Error getting questions");
                            }
                        }
                    });

            ratingViewHolder.textViewHeader.setText(rating.getHeader());

            ratingViewHolder.textViewDescription.setText(rating.getDescription());

            Rating finalRating=rating;

            ratingViewHolder.imageViewUpVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    collectionReferenceRatings
                            .document(finalRating.getId())
                            .update("votes", Integer.parseInt(ratingViewHolder.textViewVotes.getText().toString()) + 1)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.e("Rating Update", "DocumentSnapshot successfully updated!");
                                    ratingViewHolder.textViewVotes.setText(String.valueOf(Integer.parseInt(ratingViewHolder.textViewVotes.getText().toString()) + 1));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("Rating Update", "Error updating document", e);
                                }
                            });
                }
            });

            ratingViewHolder.imageViewDownVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Integer.parseInt(ratingViewHolder.textViewVotes.getText().toString()) > 0) {
                        collectionReferenceRatings.document(finalRating.getId())
                                .update("votes", Integer.parseInt(ratingViewHolder.textViewVotes.getText().toString()) - 1)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.e("Rating Update", "DocumentSnapshot successfully updated!");
                                        ratingViewHolder.textViewVotes.setText(String.valueOf(Integer.parseInt(ratingViewHolder.textViewVotes.getText().toString()) - 1));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("Question Update", "Error updating document", e);
                                    }
                                });
                    }
                }
            });

            ratingViewHolder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

                    builder.setMessage("Do you want to delete this Rating?")
                            .setTitle("Delete Rating")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    collectionReferenceRatings.document(finalRating.getId())
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.e("Rating Delete", "DocumentSnapshot successfully deleted!");
                                                    ratingList.remove(position-1);
                                                    notifyDataSetChanged();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("Rating Delete", "Error deleting document", e);
                                        }
                                    });
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

                    builder.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return ratingList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position==0)
            return ADD_RATING_LAYOUT;
        else return RATING_LAYOUT;
    }

    public class RatingViewHolder extends RecyclerView.ViewHolder{

        ImageView imageViewUpVote;
        ImageView imageViewDownVote;
        TextView textViewVotes;

        RatingBar ratingBar;

        TextView textViewHeader;
        TextView textViewDescription;
        TextView textViewNameDate;

        ImageView imageViewDelete;

        public RatingViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewUpVote = itemView.findViewById(R.id.imgView_upVote_rating_single_rating);
            imageViewDownVote = itemView.findViewById(R.id.imgView_downVote_rating_single_rating);
            textViewVotes = itemView.findViewById(R.id.text_vote_rating_single_rating);

            ratingBar=itemView.findViewById(R.id.ratingBar_rating_single_rating);

            textViewHeader = itemView.findViewById(R.id.text_header_rating_single_rating);
            textViewDescription = itemView.findViewById(R.id.text_description_rating_single_rating);
            textViewNameDate = itemView.findViewById(R.id.text_name_date_rating_single_rating);

            imageViewDelete = itemView.findViewById(R.id.imgView_delete_rating_single_rating);
        }
    }

    public class AddRatingViewHolder extends RecyclerView.ViewHolder{

        RatingBar ratingBar;

        TextInputLayout textInputLayoutHeader;
        TextInputEditText textInputEditTextHeader;

        TextInputLayout textInputLayoutDescription;
        TextInputEditText textInputEditTextDescription;

        Button buttonAdd;

        public AddRatingViewHolder(@NonNull View itemView) {
            super(itemView);

            ratingBar=itemView.findViewById(R.id.ratingBar_layout_add_rating);

            textInputLayoutHeader=itemView.findViewById(R.id.editText_header_layout_add_rating);
            textInputEditTextHeader=itemView.findViewById(R.id.editText_header_add_rating);

            textInputLayoutDescription=itemView.findViewById(R.id.editText_description_layout_add_rating);
            textInputEditTextDescription=itemView.findViewById(R.id.editText_description_add_rating);

            buttonAdd=itemView.findViewById(R.id.button_add_rating_add_rating);
        }
    }
}
