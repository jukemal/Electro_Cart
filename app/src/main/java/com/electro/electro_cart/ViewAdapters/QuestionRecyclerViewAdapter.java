package com.electro.electro_cart.ViewAdapters;

import android.content.Context;
import android.content.DialogInterface;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.electro.electro_cart.R;
import com.electro.electro_cart.models.Answer;
import com.electro.electro_cart.models.Product;
import com.electro.electro_cart.models.Question;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.SneakyThrows;

/*
Recyclerview for displaying questions in product page
 */
public class QuestionRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Question> questionList;
    private String id;

    /*
    Contains two layouts.

    One for adding questions and one for displaying existing questions.
     */
    private static final int ADD_QUESTION_LAYOUT = 0;
    private static final int QUESTION_LAYOUT = 1;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final CollectionReference collectionReferenceProduct = db.collection("products");

    private final CollectionReference collectionReferenceUser = db.collection("users");

    private final DocumentReference documentReferenceUser = db.collection("users")
            .document(firebaseAuth.getCurrentUser().getUid());

    private final CollectionReference collectionReferenceQuestions;

    public QuestionRecyclerViewAdapter(Context context, List<Question> questionList, String id) {
        this.context = context;
        this.questionList = questionList;
        this.id = id;

        collectionReferenceQuestions = collectionReferenceProduct.document(id).collection("questions");
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;

        if (viewType == ADD_QUESTION_LAYOUT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_question_add_question, parent, false);

            viewHolder = new AddQuestionLayoutViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_question_single_question, parent, false);

            viewHolder = new QuestionLayoutViewHolder(view);
        }

        return viewHolder;
    }

    @SneakyThrows
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Question question = null;
        final User[] user = {null};

        if (position < questionList.size()) {
            question = questionList.get(position);
        }

        /*
        Layout for adding questions.
         */
        if (holder.getItemViewType() == ADD_QUESTION_LAYOUT) {
            AddQuestionLayoutViewHolder addQuestionLayoutViewHolder=(AddQuestionLayoutViewHolder)holder;

            addQuestionLayoutViewHolder.buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (addQuestionLayoutViewHolder.textInputEditTextQuestion.getText().toString().isEmpty()){
                        addQuestionLayoutViewHolder.textInputLayoutQuestion.setError("Enter a Question.");
                    }else {
                        Question question1=Question.builder()
                                .question(addQuestionLayoutViewHolder.textInputEditTextQuestion.getText().toString())
                                .ownerId(firebaseAuth.getCurrentUser().getUid())
                                .votes(0)
                                .build();

                        collectionReferenceQuestions.add(question1)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.e("Question Add", "DocumentSnapshot written with ID: " + documentReference.getId());
                                        documentReference.get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()){
                                                            Question question2=task.getResult().toObject(Question.class);
                                                            questionList.add(question2);
                                                            addQuestionLayoutViewHolder.textInputLayoutQuestion.setError(null);
                                                            addQuestionLayoutViewHolder.textInputEditTextQuestion.setText("");
                                                            notifyDataSetChanged();
                                                        }else {
                                                            Toast.makeText(context,"Error adding Question",Toast.LENGTH_SHORT).show();
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
        } else {

            /*
            Layout for displaying questions.
             */
            QuestionLayoutViewHolder questionLayoutViewHolder = (QuestionLayoutViewHolder) holder;

            questionLayoutViewHolder.textViewVotes.setText(String.valueOf(question.getVotes()));

            questionLayoutViewHolder.textViewQuestion.setText(question.getQuestion());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm");
            String date = simpleDateFormat.format(question.getTimestamp());

            /*
            Setting delete button visible for questions added by currently logged in user.
             */
            collectionReferenceUser.document(question.getOwnerId()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                user[0] = task.getResult().toObject(User.class);
                                questionLayoutViewHolder.textViewNameDate.setText("by " + user[0].getName() + " on " + date);

                                if (user[0].getId().equals(firebaseAuth.getCurrentUser().getUid())) {
                                    questionLayoutViewHolder.imageViewDelete.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Log.e("Questions", "Error getting questions");
                            }
                        }
                    });

            questionLayoutViewHolder.recyclerView.setHasFixedSize(true);
            questionLayoutViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

            /*
            Setting up AnswerRecyclerview for displaying answers for each question.
             */
            Question finalQuestion1 = question;
            collectionReferenceQuestions.document(question.getId()).collection("answers")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            List<Answer> answerList = new ArrayList<>();

                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.e("Answers", document.getId() + " => " + document.getData());
                                    Answer answer = document.toObject(Answer.class);
                                    answerList.add(answer);
                                }

                                AnswerRecyclerViewAdapter answerRecyclerViewAdapter = new AnswerRecyclerViewAdapter(context, answerList,id, finalQuestion1.getId());
                                questionLayoutViewHolder.recyclerView.setAdapter(answerRecyclerViewAdapter);
                            } else {
                                Log.e("Answers", "Error getting documents: ", task.getException());
                            }
                        }
                    });

            Question finalQuestion = question;

            /*
            Up vote button for question.
             */
            questionLayoutViewHolder.imageViewUpVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    collectionReferenceQuestions
                            .document(finalQuestion.getId())
                            .update("votes", Integer.parseInt(questionLayoutViewHolder.textViewVotes.getText().toString()) + 1)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.e("Question Update", "DocumentSnapshot successfully updated!");
                                    questionLayoutViewHolder.textViewVotes.setText(String.valueOf(Integer.parseInt(questionLayoutViewHolder.textViewVotes.getText().toString()) + 1));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("Question Update", "Error updating document", e);
                                }
                            });
                }
            });

            /*
            Down vote button for question.
             */
            questionLayoutViewHolder.imageViewDownVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Integer.parseInt(questionLayoutViewHolder.textViewVotes.getText().toString()) > 0) {
                        collectionReferenceQuestions.document(finalQuestion.getId())
                                .update("votes", Integer.parseInt(questionLayoutViewHolder.textViewVotes.getText().toString()) - 1)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.e("Question Update", "DocumentSnapshot successfully updated!");
                                        questionLayoutViewHolder.textViewVotes.setText(String.valueOf(Integer.parseInt(questionLayoutViewHolder.textViewVotes.getText().toString()) - 1));
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

            /*
            Delete button or question
             */
            questionLayoutViewHolder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

                    builder.setMessage("Do you want to delete this question?")
                            .setTitle("Delete Question")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    collectionReferenceQuestions.document(finalQuestion.getId())
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.e("Question Delete", "DocumentSnapshot successfully deleted!");
                                                    questionList.remove(position);
                                                    notifyDataSetChanged();
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
        return questionList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == questionList.size())
            return ADD_QUESTION_LAYOUT;
        else return QUESTION_LAYOUT;

    }

    /*
    Innerclass for add question layout.
     */
    public class AddQuestionLayoutViewHolder extends RecyclerView.ViewHolder {

        TextInputEditText textInputEditTextQuestion;
        TextInputLayout textInputLayoutQuestion;
        Button buttonAdd;

        public AddQuestionLayoutViewHolder(@NonNull View itemView) {
            super(itemView);

            textInputEditTextQuestion=itemView.findViewById(R.id.editText_question_add_question);
            textInputLayoutQuestion=itemView.findViewById(R.id.editText_question_layout_add_question);
            buttonAdd=itemView.findViewById(R.id.button_add_question_add_question);
        }
    }

    /*
    Innerclass for question layout.
     */
    public class QuestionLayoutViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewUpVote;
        ImageView imageViewDownVote;
        TextView textViewVotes;

        TextView textViewQuestion;
        TextView textViewNameDate;

        RecyclerView recyclerView;

        ImageView imageViewDelete;

        public QuestionLayoutViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewUpVote = itemView.findViewById(R.id.imgView_upVote_question_single_question);
            imageViewDownVote = itemView.findViewById(R.id.imgView_downVote_question_single_question);
            textViewVotes = itemView.findViewById(R.id.text_vote_question_single_question);

            textViewQuestion = itemView.findViewById(R.id.text_question_question_single_question);
            textViewNameDate = itemView.findViewById(R.id.text_name_date_question_single_question);

            recyclerView = itemView.findViewById(R.id.recyclerview_question_single_question);

            imageViewDelete = itemView.findViewById(R.id.imgView_delete_question_single_question);
        }
    }
}
