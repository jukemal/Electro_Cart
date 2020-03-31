package com.electro.electro_cart.ui.store;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.electro.electro_cart.R;
import com.electro.electro_cart.models.CartItem;
import com.electro.electro_cart.models.OrderHistory;
import com.electro.electro_cart.models.Product;
import com.electro.electro_cart.models.User;
import com.electro.electro_cart.utils.EnumUserType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import lombok.SneakyThrows;

public class RevenueStoreFragment extends Fragment {

    private LineChartView lineChartView;

    ProgressBar progressBar;
    ConstraintLayout constraintLayout;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final CollectionReference collectionReferenceProduct = db.collection("products");

    private final CollectionReference collectionReferenceUser = db.collection("users");

    @SneakyThrows
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_store_revenue, container, false);

        Random random = new Random();

        lineChartView = root.findViewById(R.id.chart_store_revenue);

        TextView textViewTotalSold=root.findViewById(R.id.text_total_sold_store_revenue);
        textViewTotalSold.setText(String.valueOf(random.nextInt(4000)+1000));

        TextView textViewTotalRevenue=root.findViewById(R.id.text_total_store_revenue);

        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();

        Line line = new Line(yAxisValues).setColor(Color.parseColor("#9C27B0"));

        String[] months = new DateFormatSymbols().getShortMonths();

        String currentMonth = new SimpleDateFormat("M", Locale.getDefault()).format(new Date());
        Log.e("Today", currentMonth);

        List<String> axisData = new ArrayList<>();

        for (int i = 0; i <months.length; i++) {
            axisData.add(months[i]);
            Log.e("Date", months[i]);
        }

        for (int i = 0; i < axisData.size(); i++) {
            axisValues.add(i, new AxisValue(i).setLabel(axisData.get(i)));
        }

        List<Integer> yValues=new ArrayList<>();

        for (int i = 0; i < axisData.size(); i++) {
            if (i<Integer.parseInt(currentMonth)){
                int num=random.nextInt(400000)+100000;
                yValues.add(num);
                yAxisValues.add(new PointValue(i,num ));
            }else {
                yValues.add(0);
                yAxisValues.add(new PointValue(i, 0));
            }
        }

        int sum=0;

        for (int i:yValues){
            sum+=i;
        }

        textViewTotalRevenue.setText(String.valueOf(sum)+" LKR");

        List lines = new ArrayList();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axis = new Axis();
        axis.setValues(axisValues);
        axis.setTextSize(16);
        axis.setTextColor(Color.parseColor("#03A9F4"));
        data.setAxisXBottom(axis);

        Axis yAxis = new Axis();
//        yAxis.setName("Revenue (LKR)");
        yAxis.setTextColor(Color.parseColor("#03A9F4"));
        yAxis.setTextSize(16);
        yAxis.setMaxLabelChars(6);
        data.setAxisYLeft(yAxis);

        lineChartView.setLineChartData(data);

        Viewport viewport = new Viewport(lineChartView.getCurrentViewport());
        viewport.top = (float)Collections.max(yValues);
        viewport.bottom=0;
        viewport.left=0;
        viewport.right=months.length-1;
        lineChartView.setMaximumViewport(viewport);
        lineChartView.setCurrentViewport(viewport);

        lineChartView.setViewportCalculationEnabled(false);


        return root;
    }
}
