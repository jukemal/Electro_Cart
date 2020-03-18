package com.electro.electro_cart.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


import com.electro.electro_cart.SplashActivity;
import com.electro.electro_cart.models.CartItem;
import com.electro.electro_cart.models.Product;
import com.electro.electro_cart.models.Question;
import com.electro.electro_cart.models.Rating;
import com.electro.electro_cart.models.Specification;
import com.electro.electro_cart.utils.EnumProductType;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import com.electro.electro_cart.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private FirebaseAuth firebaseAuth;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        final TextView textView = root.findViewById(R.id.text_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        Button button = root.findViewById(R.id.btnLogout);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Intent intent = new Intent(getActivity(), SplashActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        ProfileViewModel profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        profileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        final CollectionReference collectionReference = db.collection("products");

        final DocumentReference documentReferenceRating=db.collection("users").document("ibmpnjlrznRYPDCtfmGOBNoJy9H3");

        final List<Rating> ratings = new ArrayList<>();

        final Rating rating = Rating.builder().header("Best of the best")
                .description("Brought this one last week. Totally happy with the result.")
                .score(5)
                .ownerName(documentReferenceRating)
                .votes(12).build();

        ratings.add(rating);

        final DocumentReference documentReferenceRating1=db.collection("users").document("tiHErBBLdWZXFSd4n4fdsgaPN113");

        final List<Question> questions = new ArrayList<>();

        final Question question = Question.builder().question("Does this have a sd card reader?")
                .questionOwner(documentReferenceRating)
                .answer("yes")
                .answerOwner(documentReferenceRating1)
                .votes(10).build();

        questions.add(question);

        Specification specification1 = Specification.builder().cpu("Intel Celeron N4000")
                .gpu("Intel UHD Graphics 600")
                .display("11.6”, HD (1366 x 768), TN")
                .memory("32GB eMMC")
                .ram("2GB")
                .os("Windows 10")
                .battery("38Wh, 2-cell")
                .material("Plastic")
                .dimensions("286 x 193 x 17 mm (11.26\" x 7.60\" x 0.67\")")
                .weight("0.98 kg (2.2 lbs)")
                .port("1x USB Type-C 3.0 (3.1 Gen 1)")
                .port("2x USB Type-A 3.0 (3.1 Gen 1)")
                .port("HDMI 1")
                .port("Card reader microSD")
                .port("Wi-Fi 802.11ac")
                .port("Bluetooth 4.1")
                .port("Audio jack combo audio/microphone jack")
                .feature("Web camera VGA")
                .feature("Microphone Digital array microphone")
                .feature("Speakers Stereo 2W")
                .feature("Optical drive ")
                .feature("Security Lock slot").build();

        final Product product1 = Product.builder().name("ASUS VivoBook E12 E203 new")
                .price(45000)
                .productType(EnumProductType.LAPTOP)
                .available_store("singer")
                .available_store("abans")
                .description(null)
                .specification(specification1)
                .image_link("gs://electro-cart-5c643.appspot.com/ASUS VivoBook E12 E203-1.png")
                .image_link("gs://electro-cart-5c643.appspot.com/ASUS VivoBook E12 E203-2.png")
                .image_link("gs://electro-cart-5c643.appspot.com/ASUS VivoBook E12 E203-3.png")
                .ar_link("gs://electro-cart-5c643.appspot.com/BoxAnimated.gltf")
                .promotion(0)
                .favourite(true)
                .rating(5).build();

        Button button1 = root.findViewById(R.id.btnP1);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collectionReference.add(product1)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(getContext(), "Successful", Toast.LENGTH_SHORT).show();
                                Log.e("pro", documentReference.getId());

                                for (Rating rating1 : ratings) {
                                    collectionReference.document(documentReference.getId()).collection("ratings").add(rating1);
                                }

                                for (Question question1 : questions) {
                                    collectionReference.document(documentReference.getId()).collection("questions").add(question1);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                Log.e("pro", e.toString());
                            }
                });
            }
        });

        Specification specification2=Specification.builder().cpu(" Intel Core i7-8550U")
                .gpu(" Intel UHD Graphics 620")
                .display("13.3”, 4K UHD (3840 x 2160), IPS")
                .memory("512GB SSD")
                .ram("16GB LPDDR3, 2133 MHz")
                .os("Windows 10")
                .battery("52Wh")
                .material("plastic")
                .dimensions("302 x 199 x 16.2 mm (11.89\" x 7.83\" x 0.64\")")
                .weight("1.27 kg (2.8 lbs)")
                .port("2x USB Type-C 3.1 (3.1 Gen 2)")
                .port("1x USB Type-C 3.0 (3.1 Gen 1)")
                .port("Card reader")
                .port("Wi-Fi 802.11ac")
                .port("Bluetooth 4.2")
                .port("Audio jack combo audio / microphone jack")
                .feature("Fingerprint reader")
                .feature("Web camera HD 720p + infrared camera (VGA)")
                .feature("Backlit keyboard")
                .feature("Microphone Dual-Array Microphone")
                .feature("Speakers 2x 2W").build();

        final Product product2=Product.builder().name("Dell XPS 13 9370")
                .price(180000)
                .productType(EnumProductType.LAPTOP)
                .available_store("metropolitan")
                .available_store("nanotek")
                .description(null)
                .image_link("gs://electro-cart-5c643.appspot.com/Dell XPS 13 9370-1.jpg")
                .image_link("gs://electro-cart-5c643.appspot.com/Dell XPS 13 9370-2.jpg")
                .image_link("gs://electro-cart-5c643.appspot.com/Dell XPS 13 9370-3.jpg")
                .ar_link("gs://electro-cart-5c643.appspot.com/BoxAnimated.gltf")
                .specification(specification2)
                .promotion(160000)
                .favourite(false)
                .rating(4).build();

        Button button2=root.findViewById(R.id.btnP2);

        final List<Product> productList=new ArrayList<>();

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collectionReference.add(product2)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(getContext(), "Successful", Toast.LENGTH_SHORT).show();
                                Log.e("pro", documentReference.getId());

                                for (Rating rating1 : ratings) {
                                    collectionReference.document(documentReference.getId()).collection("ratings").add(rating1);
                                }

                                for (Question question1 : questions) {
                                    collectionReference.document(documentReference.getId()).collection("questions").add(question1);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                        Log.e("pro", e.toString());
                    }
                });
            }
        });


        Specification specification3=Specification.builder().cpu("Qualcomm SM8250 Snapdragon 865")
                .gpu("Adreno 650")
                .display("AMOLED 6.7'' 1440 x 3200 pixels, 20:9 ratio (~511 ppi density)")
                .memory("128GB")
                .ram("12GB")
                .os("Android 10.0")
                .battery("Non-removable Li-Po 5000 mAh")
                .material("Glass front (Gorilla Glass 6), glass back (Gorilla Glass 6), aluminum frame")
                .dimensions("166.9 x 76 x 8.8 mm (6.57 x 2.99 x 0.35 in)")
                .weight("222 g (7.83 oz)")
                .port("GSM / CDMA / HSPA / EVDO / LTE / 5G")
                .port("Single SIM (Nano-SIM and/or eSIM) or Hybrid Dual SIM (Nano-SIM, dual stand-by)")
                .port("microSDXC")
                .port("Wi-Fi 802.11 a/b/g/n/ac/ax")
                .port("Bluetooth 5.0")
                .port("GPS")
                .port("USB Type-C 3.2")
                .feature("108MP Main Camera")
                .feature("40MP Selfie Camera")
                .feature("Fingerprint (under display, ultrasonic)")
                .feature("Fast charging 45W")
                .feature("Bixby natural language commands and dictation").build();

        final Product product3=Product.builder().name("Samsung Galaxy S20 Ultra 5G")
                .price(160000)
                .productType(EnumProductType.PHONE)
                .available_store("singer")
                .available_store("abans")
                .description(null)
                .specification(specification3)
                .image_link("gs://electro-cart-5c643.appspot.com/Samsung Galaxy S20 Ultra 5G-1.jpg")
                .image_link("gs://electro-cart-5c643.appspot.com/Samsung Galaxy S20 Ultra 5G-2.jpg")
                .ar_link("gs://electro-cart-5c643.appspot.com/BoxAnimated.gltf")
                .promotion(145000)
                .favourite(true)
                .rating(3).build();

        Button button3=root.findViewById(R.id.btnP3);

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collectionReference.add(product3)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(getContext(), "Successful", Toast.LENGTH_SHORT).show();
                                Log.e("pro", documentReference.getId());

                                for (Rating rating1 : ratings) {
                                    collectionReference.document(documentReference.getId()).collection("ratings").add(rating1);
                                }

                                for (Question question1 : questions) {
                                    collectionReference.document(documentReference.getId()).collection("questions").add(question1);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                        Log.e("pro", e.toString());
                    }
                });
            }
        });

        CollectionReference collectionReferenceUser=db.collection("users");

        Button button4=root.findViewById(R.id.btnP4);

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference documentReferenceP1=db.collection("products").document("Bz2oLC5lhZLQFlc2oS17");

                CartItem cartItem=CartItem.builder()
                        .ProductID("Bz2oLC5lhZLQFlc2oS17")
                        .productReference(documentReferenceP1)
                        .itemCount(2).build();

                collectionReferenceUser
                        .document(firebaseAuth.getCurrentUser().getUid())
                        .collection("cart")
                        .document("Bz2oLC5lhZLQFlc2oS17")
                        .set(cartItem)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.e("pro", "jttrjt");
                            }
                        });
            }
        });

        return root;
    }
}