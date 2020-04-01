package com.electro.electro_cart.ui;

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
import com.electro.electro_cart.models.Answer;
import com.electro.electro_cart.models.CartItem;
import com.electro.electro_cart.models.Product;
import com.electro.electro_cart.models.Promotion;
import com.electro.electro_cart.models.Question;
import com.electro.electro_cart.models.Rating;
import com.electro.electro_cart.models.Specification;
import com.electro.electro_cart.utils.EnumProductType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

import com.electro.electro_cart.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddDataFragment extends Fragment {

    private FirebaseAuth firebaseAuth;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragmant_add_data, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        final CollectionReference collectionReference = db.collection("products");

        final String ownerId=firebaseAuth.getCurrentUser().getUid();

        final List<Rating> ratings = new ArrayList<>();

        final Rating rating = Rating.builder()
                .header("Best of the best")
                .description("Brought this one last week. Totally happy with the result.")
                .score(5)
                .ownerId(ownerId)
                .votes(12).build();

        ratings.add(rating);

        final List<Answer> answerList=new ArrayList<>();

        Answer answer1=Answer.builder()
                .answer("Yes")
                .ownerId(ownerId).build();

        answerList.add(answer1);

        Answer answer2=Answer.builder()
                .answer("No")
                .ownerId(ownerId).build();

        answerList.add(answer2);

        final List<Question> questions = new ArrayList<>();

        final Question question = Question.builder()
                .question("Does this have a sd card reader?")
                .ownerId(ownerId)
                .votes(10).build();

        questions.add(question);

        final Question question1 = Question.builder()
                .question("Does this have a Camera?")
                .ownerId(ownerId)
                .votes(10).build();

        questions.add(question1);

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

        Calendar c = Calendar.getInstance();
        c.set(2020,4,20);

        Promotion promotion1=Promotion.builder()
                .description("Best Budget Laptop\n\n\nNow you have the chance to buy the best budget laptop with 40% discount. Hurry up. Offer only valid until April 25.")
                .discountPercentage(40)
                .endDate(c.getTime()).build();

        final Product product1 = Product.builder().name("ASUS VivoBook E12 E203")
                .price(45000)
                .productType(EnumProductType.LAPTOP)
                .storeId(ownerId)
                .description(null)
                .specification(specification1)
                .image_link("gs://electro-cart-5c643.appspot.com/ASUS VivoBook E12 E203-1.png")
                .image_link("gs://electro-cart-5c643.appspot.com/ASUS VivoBook E12 E203-2.png")
                .image_link("gs://electro-cart-5c643.appspot.com/ASUS VivoBook E12 E203-3.png")
                .ar_link("gs://electro-cart-5c643.appspot.com/BoxAnimated.gltf")
                .promotion(promotion1)
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
                                    collectionReference.document(documentReference.getId()).collection("questions").add(question1)
                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if (task.isSuccessful()){
                                                        for (Answer a:answerList){
                                                            collectionReference.document(documentReference.getId())
                                                                    .collection("questions")
                                                                    .document(task.getResult().getId())
                                                                    .collection("answers").add(a);
                                                        }
                                                    }else {

                                                    }
                                                }
                                            });
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
                .storeId(ownerId)
                .description(null)
                .image_link("gs://electro-cart-5c643.appspot.com/Dell XPS 13 9370-1.jpg")
                .image_link("gs://electro-cart-5c643.appspot.com/Dell XPS 13 9370-2.jpg")
                .image_link("gs://electro-cart-5c643.appspot.com/Dell XPS 13 9370-3.jpg")
                .ar_link("gs://electro-cart-5c643.appspot.com/BoxAnimated.gltf")
                .specification(specification2)
                .promotion(null)
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
                                    collectionReference.document(documentReference.getId()).collection("questions").add(question1)
                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if (task.isSuccessful()){
                                                        for (Answer a:answerList){
                                                            collectionReference.document(documentReference.getId())
                                                                    .collection("questions")
                                                                    .document(task.getResult().getId())
                                                                    .collection("answers").add(a);
                                                        }
                                                    }else {

                                                    }
                                                }
                                            });                                }
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
                .storeId(ownerId)
                .description(null)
                .specification(specification3)
                .image_link("gs://electro-cart-5c643.appspot.com/Samsung Galaxy S20 Ultra 5G-1.jpg")
                .image_link("gs://electro-cart-5c643.appspot.com/Samsung Galaxy S20 Ultra 5G-2.jpg")
                .ar_link("gs://electro-cart-5c643.appspot.com/BoxAnimated.gltf")
                .promotion(promotion1)
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
                                    collectionReference.document(documentReference.getId()).collection("questions").add(question1)
                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if (task.isSuccessful()){
                                                        for (Answer a:answerList){
                                                            collectionReference.document(documentReference.getId())
                                                                    .collection("questions")
                                                                    .document(task.getResult().getId())
                                                                    .collection("answers").add(a);
                                                        }
                                                    }else {

                                                    }
                                                }
                                            });                                }
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

        Specification specification4=Specification.builder()
                .cpu("Intel Core i5-7360U")
                .gpu("Intel Iris Plus Graphics 640")
                .display("13.3”, WQXGA (2560 x 1600), IPS")
                .memory("128GB SSD")
                .ram("8GB, LPDDR3, 2133MHz")
                .os("Mac OS X")
                .battery("54.5Wh, Li-Po")
                .material("Aluminum")
                .dimensions("304 x 212 x 14.9 mm (11.97\" x 8.35\" x 0.59\")")
                .weight(" 1.37 kg (3 lbs)")
                .port("4x USB Type-C 3.1 (3.1 Gen 2)")
                .port("Thunderbolt 3")
                .port("Wi-Fi 802.11ac")
                .port("Bluetooth 4.2")
                .port("Audio jack headphone/microphone")
                .feature("Web camera")
                .feature("Microphone")
                .feature("Speakers").build();

        final Product product4=Product.builder()
                .name("Apple MacBook Pro 13 (Mid-2017)")
                .price(350000)
                .productType(EnumProductType.LAPTOP)
                .storeId(ownerId)
                .description(null)
                .image_link("gs://electro-cart-5c643.appspot.com/Apple MacBook Pro 13 (Mid-2017)-1.jpg")
                .image_link("gs://electro-cart-5c643.appspot.com/Apple MacBook Pro 13 (Mid-2017)-2.jpg")
                .image_link("gs://electro-cart-5c643.appspot.com/Apple MacBook Pro 13 (Mid-2017)-3.jpg")
                .ar_link("gs://electro-cart-5c643.appspot.com/BoxAnimated.gltf")
                .specification(specification4)
                .rating(5)
                .promotion(null)
                .build();

        Button button4=root.findViewById(R.id.btnP4);

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collectionReference.add(product4)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(getContext(), "Successful", Toast.LENGTH_SHORT).show();
                                Log.e("pro", documentReference.getId());

                                for (Rating rating1 : ratings) {
                                    collectionReference.document(documentReference.getId()).collection("ratings").add(rating1);
                                }

                                for (Question question1 : questions) {
                                    collectionReference.document(documentReference.getId()).collection("questions").add(question1)
                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if (task.isSuccessful()){
                                                        for (Answer a:answerList){
                                                            collectionReference.document(documentReference.getId())
                                                                    .collection("questions")
                                                                    .document(task.getResult().getId())
                                                                    .collection("answers").add(a);
                                                        }
                                                    }else {

                                                    }
                                                }
                                            });                                }
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

        Specification specification5=Specification.builder()
                .cpu("Qualcomm SDM730 Snapdragon 730")
                .gpu("Adreno 618")
                .display("IPS LCD 5.81'' 1080 x 2340 pixels, 19.5:9 ratio")
                .memory("64GB")
                .ram("6GB")
                .os("Android 11.0")
                .battery("Non-removable Li-Po 4500 mAh")
                .material("Glass front (Asahi Dragontrail), plastic back, plastic frame")
                .dimensions("151.3 x 70.1 x 8.2 mm (5.96 x 2.76 x 0.32 in)")
                .weight("147 g (5.19 oz)")
                .port("GSM / CDMA / HSPA / EVDO / LTE")
                .port("eSIM")
                .port("Wi-Fi 802.11 a/b/g/n/ac")
                .port("Bluetooth 5.0")
                .port("GPS")
                .port("USB Type-C")
                .feature("12.2MP Main Camera")
                .feature("8MP Selfie Camera")
                .feature("Fast charging 45W")
                .feature("Fingerprint (under display, ultrasonic)").build();

        Product product5=Product.builder()
                .name("Google Pixel 4a")
                .price(135000)
                .productType(EnumProductType.PHONE)
                .storeId("36363653535353")
                .description(null)
                .image_link("gs://electro-cart-5c643.appspot.com/Google Pixel 4a-1jpg.jpg")
                .image_link("gs://electro-cart-5c643.appspot.com/Google Pixel 4a-2.jpg")
                .ar_link("gs://electro-cart-5c643.appspot.com/BoxAnimated.gltf")
                .specification(specification5)
                .promotion(null)
                .rating(5)
                .build();

        Button button5=root.findViewById(R.id.btnP5);

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collectionReference.add(product5)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(getContext(), "Successful", Toast.LENGTH_SHORT).show();
                                Log.e("pro", documentReference.getId());

                                for (Rating rating1 : ratings) {
                                    collectionReference.document(documentReference.getId()).collection("ratings").add(rating1);
                                }

                                for (Question question1 : questions) {
                                    collectionReference.document(documentReference.getId()).collection("questions").add(question1)
                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if (task.isSuccessful()){
                                                        for (Answer a:answerList){
                                                            collectionReference.document(documentReference.getId())
                                                                    .collection("questions")
                                                                    .document(task.getResult().getId())
                                                                    .collection("answers").add(a);
                                                        }
                                                    }else {

                                                    }
                                                }
                                            });                                }
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


        Specification specification6=Specification.builder()
                .cpu("Qualcomm SDM730 Snapdragon 730")
                .gpu("Adreno 618")
                .display("IPS LCD 5.81'' 1080 x 2340 pixels, 19.5:9 ratio")
                .memory("64GB")
                .ram("6GB")
                .os("Android 11.0")
                .battery("Non-removable Li-Po 4500 mAh")
                .material("Glass front (Asahi Dragontrail), plastic back, plastic frame")
                .dimensions("151.3 x 70.1 x 8.2 mm (5.96 x 2.76 x 0.32 in)")
                .weight("147 g (5.19 oz)")
                .port("GSM / CDMA / HSPA / EVDO / LTE")
                .port("eSIM")
                .port("Wi-Fi 802.11 a/b/g/n/ac")
                .port("Bluetooth 5.0")
                .port("GPS")
                .port("USB Type-C")
                .feature("12.2MP Main Camera")
                .feature("8MP Selfie Camera")
                .feature("Fast charging 45W")
                .feature("Fingerprint (under display, ultrasonic)").build();

        Product product6=Product.builder()
                .specification(specification6)
                .name("Xiaomi Redmi Note 9 Pro")
                .price(75000)
                .productType(EnumProductType.PHONE)
                .storeId("53367827833538")
                .description(null)
                .image_link("gs://electro-cart-5c643.appspot.com/Xiaomi Redmi Note 9 Pro-1.jpg")
                .ar_link("gs://electro-cart-5c643.appspot.com/BoxAnimated.gltf")
                .promotion(null)
                .rating(4)
                .build();

        Button button6=root.findViewById(R.id.btnP6);

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collectionReference.add(product6)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(getContext(), "Successful", Toast.LENGTH_SHORT).show();
                                Log.e("pro", documentReference.getId());

                                for (Rating rating1 : ratings) {
                                    collectionReference.document(documentReference.getId()).collection("ratings").add(rating1);
                                }

                                for (Question question1 : questions) {
                                    collectionReference.document(documentReference.getId()).collection("questions").add(question1)
                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if (task.isSuccessful()){
                                                        for (Answer a:answerList){
                                                            collectionReference.document(documentReference.getId())
                                                                    .collection("questions")
                                                                    .document(task.getResult().getId())
                                                                    .collection("answers").add(a);
                                                        }
                                                    }else {

                                                    }
                                                }
                                            });                                }
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

        return root;
    }
}
