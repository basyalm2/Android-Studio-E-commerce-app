package com.example.sajhakarobar;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static com.example.sajhakarobar.Main2Activity.showCart;
import static com.example.sajhakarobar.RegisterActivity.setSignUpFragment;

public class ProductDetailsActivity extends AppCompatActivity {

    private ViewPager productImagesViewPager;
    private TextView productTitle;
    private TextView averageRatingMiniView;
    private TextView totalRatingMiniView;
    private TextView productPrice;
    private TextView cuttedPrice;
    private ImageView codIndicator;
    private TextView tvCodIndicator;
    private TabLayout viewpagerIndicator;

    private LinearLayout couponReedemptionLayout;
    private Button couponRedeemButton;
    private TextView rewardTitle;
    private TextView rewardBody;



    ///////// Product Description
    private ConstraintLayout productDetailsOnlyContainer;
    private ConstraintLayout productDetailsTabsContainer;
    private ViewPager productDetailsViewpager;
    private TabLayout productDetailsTablayout;
    private TextView productOnlyDescriptionBody;

    private List<ProductSpecificationModel> productSpecificationModelList = new ArrayList<>();
    private String productDescription;
    private String productOtherDetails;

    ///////// Product Description

    ////////Rating Layout
    private LinearLayout rateNowContainer;
    private TextView totalRatings;
    private LinearLayout ratingsNoContainer;
    private TextView totalRatingsFigure;
    private LinearLayout ratingsProgressBarContainer;
    private TextView averageRating;
    ////////Raing Layout

    private Button buyNowBtn;
    private LinearLayout addToCartBtn;

    public static boolean ALREADY_ADDED_TO_WISHLIST = false;
    public static FloatingActionButton addToWishListBtn;
    private FirebaseFirestore firebaseFirestore;
    ///////Coupon Dialog

    public static TextView couponTitle;
    public static TextView couponBody;
    public static TextView couponExpiryDate;
    private static RecyclerView couponsRecyclerView;
    private static LinearLayout selectedCoupon;

    ///////Coupon Dialog

    private Dialog signInDialog;
    private Dialog loadingDialog;
    private FirebaseUser currentUser;
    private String productID;

    private DocumentSnapshot documentSnapshot;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productImagesViewPager = findViewById(R.id.product_images_viewpager);
        viewpagerIndicator = findViewById(R.id.viewpager_indicator);
        addToWishListBtn = findViewById(R.id.add_to_wishlist_button);
        productDetailsViewpager = findViewById(R.id.product_details_viewpager);
        productDetailsTablayout = findViewById(R.id.product_details_tablayout);
        buyNowBtn= findViewById(R.id.buy_now_btn);
        couponRedeemButton = findViewById(R.id.coupon_redemption_button);
        productTitle = findViewById(R.id.product_title);
        averageRatingMiniView = findViewById(R.id.tv_product_rating_miniview);
        totalRatingMiniView = findViewById(R.id.total_ratings_miniview);
        productPrice = findViewById(R.id.product_price);
        cuttedPrice = findViewById(R.id.cutted_price);
        codIndicator = findViewById(R.id.cod_indicator_imageview);
        tvCodIndicator = findViewById(R.id.tv_cod_indicator);
        rewardTitle = findViewById(R.id.reward_title);
        rewardBody = findViewById(R.id.reward_body);
        productDetailsTabsContainer = findViewById(R.id.product_details_tabs_container);
        productDetailsOnlyContainer = findViewById(R.id.product_details_container);
        productOnlyDescriptionBody = findViewById(R.id.product_details_body);
        totalRatings = findViewById(R.id.total_ratings);
        ratingsNoContainer = findViewById(R.id.ratings_numbers_container);
        totalRatingsFigure = findViewById(R.id.total_ratings_figure);
        ratingsProgressBarContainer = findViewById(R.id.ratings_progressbar_container);
        averageRating = findViewById(R.id.average_rating);
        addToCartBtn = findViewById(R.id.add_to_cart_btn);
        couponReedemptionLayout = findViewById(R.id.coupon_redemption_layout);

        /////Loading Dialog

        loadingDialog = new Dialog(ProductDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        /////Loading Dialog


        firebaseFirestore = FirebaseFirestore.getInstance();

        final List<String> productImages = new ArrayList<>();
        productID = getIntent().getStringExtra("PRODUCT_ID");
        firebaseFirestore.collection("PRODUCTS").document(productID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    documentSnapshot = task.getResult();

                    for (long x =1 ; x < (long)documentSnapshot.get("no_of_product_images")+1; x++){
                        productImages.add(documentSnapshot.get("product_image_"+x).toString());
                    }
                    ProductimagesAdapter productimagesAdapter = new ProductimagesAdapter(productImages);
                    productImagesViewPager.setAdapter(productimagesAdapter);

                    productTitle.setText(documentSnapshot.get("product_title").toString());
                    averageRatingMiniView.setText(documentSnapshot.get("average_rating").toString());
                    totalRatingMiniView.setText("("+(long)documentSnapshot.get("total_ratings")+")ratings");
                    productPrice.setText("Rs. "+ documentSnapshot.get("product_price").toString()+"/-");
                    cuttedPrice.setText("Rs. "+ documentSnapshot.get("cutted_price").toString()+"/-");

                    if ((boolean)documentSnapshot.get("COD")){
                        codIndicator.setVisibility(View.VISIBLE);
                        tvCodIndicator.setVisibility(View.VISIBLE);
                    }else{
                        codIndicator.setVisibility(View.INVISIBLE);
                        tvCodIndicator.setVisibility(View.INVISIBLE);
                    }
                    rewardTitle.setText((long)documentSnapshot.get("free_coupens")+ documentSnapshot.get("free_coupen_title").toString());
                    rewardBody.setText(documentSnapshot.get("free_coupen_body").toString());

                    if ((boolean)documentSnapshot.get("use_tab_layout")){
                        productDetailsTabsContainer.setVisibility(View.VISIBLE);
                        productDetailsOnlyContainer.setVisibility(View.GONE);
                        productDescription = documentSnapshot.get("product_description").toString();
                        productOtherDetails = documentSnapshot.get("product_other_details").toString();


                        for (long x = 1; x < (long)documentSnapshot.get("total_spec_titles")+1; x++){
                            productSpecificationModelList.add(new ProductSpecificationModel(0, documentSnapshot.get("spec_title_"+ x).toString()));
                            for (long y =1; y < (long)documentSnapshot.get("spec_title_"+x+"_total_fields")+1; y++){
                                productSpecificationModelList.add(new ProductSpecificationModel(1, documentSnapshot.get("spec_title_"+x+"_field_"+y+"_name").toString(), documentSnapshot.get("spec_title_"+x+"_field_"+y+"_value").toString()));
                            }
                        }

                    }else{
                        productDetailsTabsContainer.setVisibility(View.GONE);
                        productDetailsOnlyContainer.setVisibility(View.VISIBLE);
                        productOnlyDescriptionBody.setText(documentSnapshot.get("product_description").toString());
                    }

                    totalRatings.setText((long)documentSnapshot.get("total_ratings")+" ratings");

                    for (int x =0; x < 5; x++){
                        TextView rating = (TextView) ratingsNoContainer.getChildAt(x);
                        rating.setText(String.valueOf((long)documentSnapshot.get((5-x) +"_star")));

                        ProgressBar progressBar = (ProgressBar) ratingsProgressBarContainer.getChildAt(x);
                        int maxProgress = Integer.parseInt (String.valueOf((long)documentSnapshot.get("total_ratings")));
                        progressBar.setMax(maxProgress);
                        progressBar.setProgress(Integer.parseInt(String.valueOf((long)documentSnapshot.get((5-x) +"_star"))));
                    }
                    totalRatingsFigure.setText(String.valueOf((long)documentSnapshot.get("total_ratings")));
                    averageRating.setText(documentSnapshot.get("average_rating").toString());
                    productDetailsViewpager.setAdapter(new ProductDetailsAdapter(getSupportFragmentManager(), productDetailsTablayout.getTabCount(), productDescription, productOtherDetails, productSpecificationModelList));

                    if(currentUser != null) {
                        if (DBqueries.wishList.size() == 0) {
                            DBqueries.loadWishList(ProductDetailsActivity.this, loadingDialog, false);
                        } else {
                            loadingDialog.dismiss();
                        }
                    }else {
                        loadingDialog.dismiss();
                    }

                    if (DBqueries.wishList.contains(productID)){
                        ALREADY_ADDED_TO_WISHLIST = true;
                        addToWishListBtn.setSupportImageTintList((getResources().getColorStateList(R.color.colorPrimaryDark)));

                    }else{
                        ALREADY_ADDED_TO_WISHLIST = false;
                    }

                }else{
                    loadingDialog.dismiss();
                    String error = task.getException().getMessage();
                    Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewpagerIndicator.setupWithViewPager(productImagesViewPager, true);

        addToWishListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null){
                    signInDialog.show();
                }else{

                    addToWishListBtn.setEnabled(false);

                if(ALREADY_ADDED_TO_WISHLIST){
                    int index = DBqueries.wishList.indexOf(productID);
                    DBqueries.removeFromWishlist(index, ProductDetailsActivity.this);
                    addToWishListBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#727272")));
                }else{
                    addToWishListBtn.setSupportImageTintList(getResources().getColorStateList(R.color.colorPrimaryDark));
                    Map<String, Object> addProduct = new HashMap<>();
                    addProduct.put("product_ID_"+ String.valueOf(DBqueries.wishList.size()), productID);

                    firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_WISHLIST")
                            .set(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                Map<String, Object> updateListSize = new HashMap<>();
                                updateListSize.put("list_size", (long)(DBqueries.wishList.size()+1));
                                firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_WISHLIST")
                                        .update(updateListSize).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){

                                            if (DBqueries.wishlistModelList.size()!=0){
                                                DBqueries.wishlistModelList.add(new WishlistModel(documentSnapshot.get("product_image_1").toString()
                                                    ,documentSnapshot.get("product_title").toString()
                                                    ,(long)documentSnapshot.get("free_coupens")
                                                    ,documentSnapshot.get("average_rating").toString()
                                                    ,(long)documentSnapshot.get("total_ratings")
                                                    ,documentSnapshot.get("product_price").toString()
                                                    ,documentSnapshot.get("cutted_price").toString()
                                                    ,(boolean)documentSnapshot.get("COD")));

                                            }

                                            ALREADY_ADDED_TO_WISHLIST = true;
                                            addToWishListBtn.setSupportImageTintList((getResources().getColorStateList(R.color.colorPrimaryDark)));
                                            DBqueries.wishList.add(productID);
                                            Toast.makeText(ProductDetailsActivity.this, "Added to Wishlist Successfully", Toast.LENGTH_SHORT).show();
                                        }else{
                                            addToWishListBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#727272")));
                                            String error = task.getException().getMessage();
                                            Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                        }
                                        addToWishListBtn.setEnabled(true);
                                    }
                                });
                            }else{
                                addToWishListBtn.setEnabled(true);
                                String error = task.getException().getMessage();
                                Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();

                            }
                        }
                    });



                }
            }}
        });



        productDetailsViewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(productDetailsTablayout));
        productDetailsTablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                productDetailsViewpager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //////rating layout
        rateNowContainer = findViewById(R.id.rate_now_container);
        for(int x = 0; x< rateNowContainer.getChildCount(); x++) {
            final int starPosition = x;
            rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentUser == null){
                        signInDialog.show();
                    }else{
                        setRating(starPosition);
                    }

                }
            });
        }
        //////rating layout

        buyNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null){
                    signInDialog.show();
                }else{
                Intent deliveryIntent = new Intent(ProductDetailsActivity.this,DeliveryActivity.class);
                startActivity(deliveryIntent);
            }}
        });


        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null){
                    signInDialog.show();
                }else{
                    //....tofo add to cart
                }
            }
        });

        ///////coupon dialog

        final Dialog checkCouponPriceDialog = new Dialog(ProductDetailsActivity.this);
        checkCouponPriceDialog.setContentView(R.layout.coupen_redeem_dialog);
        checkCouponPriceDialog.setCancelable(true);
        checkCouponPriceDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ImageView toggleRecyclerView = checkCouponPriceDialog.findViewById(R.id.toggle_recyclerview);
        couponsRecyclerView = checkCouponPriceDialog.findViewById(R.id.coupons_recyclerview);
        selectedCoupon = checkCouponPriceDialog.findViewById(R.id.selected_coupon);
        couponTitle = checkCouponPriceDialog.findViewById(R.id.coupon_title);
        couponExpiryDate = checkCouponPriceDialog.findViewById(R.id.coupon_validity);
        couponBody = checkCouponPriceDialog.findViewById(R.id.coupon_body);


        TextView originalPrice = checkCouponPriceDialog.findViewById(R.id.original_price);
        TextView discountedPrice = checkCouponPriceDialog.findViewById(R.id.discounted_price);

        LinearLayoutManager layoutManager = new LinearLayoutManager(ProductDetailsActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        couponsRecyclerView.setLayoutManager(layoutManager);

        List<RewardModel> rewardModelList = new ArrayList<>();
        rewardModelList.add(new RewardModel("Cashback", "Till 2nd Baisakh, 2076", "Get 25% CASHBACK on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new RewardModel("Discount", "Till 2nd Baisakh, 2076", "Get 25% CASHBACK on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new RewardModel("Free Food", "Till 2nd Baisakh, 2076", "Get 25% CASHBACK on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new RewardModel("Cashback", "Till 2nd Baisakh, 2076", "Get 25% CASHBACK on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new RewardModel("Free Shipping", "Till 2nd Baisakh, 2076", "Get 25% CASHBACK on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new RewardModel("Free Shipping", "Till 2nd Baisakh, 2076", "Get 25% CASHBACK on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new RewardModel("Cashback", "Till 2nd Baisakh, 2076", "Get 25% CASHBACK on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new RewardModel("Discount", "Till 2nd Baisakh, 2076", "Get 25% CASHBACK on any product above Rs. 200/- and below Rs. 3000/-"));

        MyRewardsAdapter myRewardsAdapter = new MyRewardsAdapter(rewardModelList, true);
        couponsRecyclerView.setAdapter(myRewardsAdapter);
        myRewardsAdapter.notifyDataSetChanged();

        toggleRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogRecylerView();
            }
        });

        //////coupon Dialog

        couponRedeemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkCouponPriceDialog.show();

            }
        });

        /////// sign in dialog
        signInDialog = new Dialog(ProductDetailsActivity.this);
        signInDialog.setContentView(R.layout.sign_in_dialog);
        signInDialog.setCancelable(true);
        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button dialogSignInBtn = signInDialog.findViewById(R.id.sign_in_btn);
        Button dialogSignUpBtn = signInDialog.findViewById(R.id.sign_up_btn);

        final Intent registerIntent = new Intent(ProductDetailsActivity.this, RegisterActivity.class);

        dialogSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignINFragment.disableCloseBtn = true;
                SingUpFragment.disableCloseBtn = true;
                signInDialog.dismiss();
                setSignUpFragment = false;
                startActivity(registerIntent);
            }
        });
        dialogSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignINFragment.disableCloseBtn = true;
                SingUpFragment.disableCloseBtn = true;
                signInDialog.dismiss();
                setSignUpFragment = true;
                startActivity(registerIntent);
            }
        });
        /////// sign in dialog


    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null){
            couponReedemptionLayout.setVisibility(View.GONE);
        }else{
            couponReedemptionLayout.setVisibility(View.VISIBLE);
        }
        if(currentUser != null) {
            if (DBqueries.wishList.size() == 0) {
                DBqueries.loadWishList(ProductDetailsActivity.this, loadingDialog, false);
            } else {
                loadingDialog.dismiss();
            }
        }else {
            loadingDialog.dismiss();
        }

        if (DBqueries.wishList.contains(productID)){
            ALREADY_ADDED_TO_WISHLIST = true;
            addToWishListBtn.setSupportImageTintList((getResources().getColorStateList(R.color.colorPrimaryDark)));

        }else{
            ALREADY_ADDED_TO_WISHLIST = false;
        }

    }

    public static void showDialogRecylerView(){
        if(couponsRecyclerView.getVisibility()== View.GONE){
            couponsRecyclerView.setVisibility(View.VISIBLE);
            selectedCoupon.setVisibility(View.GONE);
        }else{
            couponsRecyclerView.setVisibility(View.GONE);
            selectedCoupon.setVisibility(View.VISIBLE);
        }

    }

    private void setRating(int starPosition){
        for(int x = 0; x < rateNowContainer.getChildCount(); x++ ){
            ImageView starBtn = (ImageView)rateNowContainer.getChildAt(x);
            starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bebebe")));
            if(x<=starPosition){
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffbb00")));
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_and_cart_icon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }else if (id ==R.id.main_search_icon){
            return true;

        } else if (id == R.id.main_cart_icon){
            if(currentUser == null){
                signInDialog.show();
            }else{
            Intent cartIntent = new Intent(ProductDetailsActivity.this, Main2Activity.class);
            showCart = true;
            startActivity(cartIntent);
            return true;
        }}

        return super.onOptionsItemSelected(item);
    }
}
