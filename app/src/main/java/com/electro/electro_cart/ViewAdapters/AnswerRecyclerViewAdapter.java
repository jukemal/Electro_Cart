package com.electro.electro_cart.ViewAdapters;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.electro.electro_cart.R;
import com.electro.electro_cart.models.Answer;
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
import java.util.Locale;

/*
 *This is the recyclerview for answers section in questions which is included in the Product page.
 *
 * Contains two layouts.
 *
 * Layout for adding a answer and for displaying existing answers.
 */
public class AnswerRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Answer> answerList;
    private String id;
    private String questionId;

    /*
    * Layout types
    * */
    private static final int ADD_ANSWER_LAYOUT = 0;
    private static final int ANSWER_LAYOUT = 1;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final CollectionReference collectionReferenceProduct = db.collection("products");

    private final CollectionReference collectionReferenceUser = db.collection("users");

    private final DocumentReference documentReferenceUser = db.collection("users")
            .document(firebaseAuth.getCurrentUser().getUid());

    private final CollectionReference collectionReferenceAnswer;

   public AnswerRecyclerViewAdapter(Context context, List<Answer> answerList, String id, String questionId) {
        this.context = context;
        this.answerList = answerList;
        this.id = id;
        this.questionId = questionId;

        collectionReferenceAnswer = collectionReferenceProduct.document(id).collection("questions")
                .document(questionId).collection("answers");
    }

    /**
     * Assigning appropriate layout for given viewType.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;

        if (viewType == ADD_ANSWER_LAYOUT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_answer_add_answer, parent, false);

            viewHolder = new AddAnswerViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_answer, parent, false);

            viewHolder = new AnswerViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Answer answer = null;
        final User[] user = {null};

        if (position < answerList.size()) {
            answer = answerList.get(position);
        }

        /*
         * Add answer layout
         *
         * Answer added by the user is inserted to the database.
         */
        if (holder.getItemViewType() == ADD_ANSWER_LAYOUT) {
            AddAnswerViewHolder addAnswerViewHolder = (AddAnswerViewHolder) holder;

            addAnswerViewHolder.buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (addAnswerViewHolder.textInputEditTextAnswer.getText().toString().isEmpty()) {
                        addAnswerViewHolder.textInputLayoutAnswer.setError("Enter a Answer");
                    } else {
                        Answer answer1 = Answer.builder()
                                .answer(addAnswerViewHolder.textInputEditTextAnswer.getText().toString())
                                .ownerId(firebaseAuth.getCurrentUser().getUid()).build();

                        collectionReferenceAnswer.add(answer1)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.e("Answer Add", "DocumentSnapshot written with ID: " + documentReference.getId());

                                        documentReference.get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            Answer answer2=task.getResult().toObject(Answer.class);
                                                            answerList.add(answer2);
                                                            addAnswerViewHolder.textInputLayoutAnswer.setError(null);
                                                            addAnswerViewHolder.textInputEditTextAnswer.setText("");
                                                            notifyDataSetChanged();
                                                        } else {
                                                            Log.e("answer Add", "Error adding document");
                                                        }
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("answer Add", "Error adding document", e);
                                    }
                                });

                    }
                }
            });
        } else {
            /*
             *Layout for displaying existing answers.
             */
            AnswerViewHolder answerViewHolder = (AnswerViewHolder) holder;

            answerViewHolder.textViewAnswer.setText(answer.getAnswer());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm", Locale.ENGLISH);
            String date = simpleDateFormat.format(answer.getTimestamp());

            collectionReferenceUser.document(answer.getOwnerId()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                user[0] = task.getResult().toObject(User.class);
                                answerViewHolder.textViewUserDate.setText("by " + user[0].getName() + " on " + date);

                                if (user[0].getId().equals(firebaseAuth.getCurrentUser().getUid())){
                                    answerViewHolder.imageViewDelete.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Log.e("Questions", "Error getting questions");
                            }
                        }
                    });

            /*
            Deleting answers
             */
            Answer finalAnswer = answer;
            answerViewHolder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

                    builder.setMessage("Do you want to delete this Answer?")
                            .setTitle("Delete Answer")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    collectionReferenceAnswer.document(finalAnswer.getId())
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.e("Question Delete", "DocumentSnapshot successfully deleted!");
                                                    answerList.remove(position);
                                                    notifyItemRemoved(position);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("Question Delete", "Error deleting document", e);
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
        return answerList.size() + 1;
    }

    /*
    Return layout type according to the position.

    At the end of the list add answer layout.
    Otherwise displaying existing answers.
     */
    @Override
    public int getItemViewType(int position) {
        if (position == answerList.size())
            return ADD_ANSWER_LAYOUT;
        else return ANSWER_LAYOUT;
    }

    /*
    Innerclass for answer layout.
     */
    public class AnswerViewHolder extends RecyclerView.ViewHolder {

        TextView textViewAnswer;
        TextView textViewUserDate;
        ImageView imageViewDelete;

        public AnswerViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewAnswer = itemView.findViewById(R.id.text_answer_answer);
            textViewUserDate = itemView.findViewById(R.id.text_user_date_answer);
            imageViewDelete=itemView.findViewById(R.id.imgView_delete__answer);
        }
    }

    /*
    Innerclass for add answer layout.
     */
    public class AddAnswerViewHolder extends RecyclerView.ViewHolder {

        TextInputEditText textInputEditTextAnswer;
        TextInputLayout textInputLayoutAnswer;
        Button buttonAdd;

        public AddAnswerViewHolder(@NonNull View itemView) {
            super(itemView);

            textInputEditTextAnswer = itemView.findViewById(R.id.editText_answer_add_answer);
            textInputLayoutAnswer = itemView.findViewById(R.id.editText_answer_layout_add_answer);
            buttonAdd = itemView.findViewById(R.id.button_add_answer_add_answer);
        }
    }
}
