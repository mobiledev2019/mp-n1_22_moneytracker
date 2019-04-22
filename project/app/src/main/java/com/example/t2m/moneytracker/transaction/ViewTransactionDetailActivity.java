package com.example.t2m.moneytracker.transaction;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.t2m.moneytracker.R;
import com.example.t2m.moneytracker.model.MTDate;
import com.example.t2m.moneytracker.model.Transaction;
import com.example.t2m.moneytracker.utilities.BitmapUtils;

import java.io.IOException;
import java.util.Calendar;

public class ViewTransactionDetailActivity extends AppCompatActivity {


    public static final String EXTRA_TRANSACTION = "Extra.TransactionDetail.Transaction";
    private TextView mTextMoney;
    private TextView mTextCategory;
    private TextView mTextNote;
    private TextView mTextDate;
    private TextView mTextWallet;
    private ImageView mImgPreview;

    private Transaction mTransaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_transaction_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(savedInstanceState != null) {
            mTransaction = (Transaction) savedInstanceState.getSerializable(EXTRA_TRANSACTION);
        }
        else {
            mTransaction = (Transaction) getIntent().getSerializableExtra(EXTRA_TRANSACTION);
        }
        addControls();
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meunu_transaction_detail,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete : {
                onClickedDelete();
                return true;
            }
            case R.id.action_edit : {
                onClickedEdit();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onClickedEdit() {
    }

    private void onClickedDelete() {
    }

    @Override
    public boolean onSupportNavigateUp() {
        onClickedCancle();
        return true;
    }

    private void addControls() {
        mTextMoney = findViewById(R.id.text_transaction_money);
        mTextCategory = findViewById(R.id.text_transaction_category);
        mTextNote = findViewById(R.id.text_transaction_note);
        mTextDate = findViewById(R.id.text_transaction_date);
        mTextWallet = findViewById(R.id.text_transaction_wallet);
        mImgPreview = findViewById(R.id.image_preview);
    }

    private void addEvents() {

    }

    private void onClickedCancle() {

        setResult(RESULT_CANCELED);
        finish();
    }

    private void updateUI() {
        if(mTransaction != null) {
            mTextCategory.setText(mTransaction.getCategory().getCategory());
            ImageView imageView = findViewById(R.id.image_transaction_category);
            // lấy ảnh từ asset
            String base_path = "category/";
            try {
                Drawable img = Drawable.createFromStream(this.getAssets().open(base_path + mTransaction.getCategory().getIcon()),null);
                imageView.setImageDrawable(img);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mTextDate.setText(new MTDate(mTransaction.getTransactionDate()).toIsoDateShortTimeString());
            mTextMoney.setText(String.valueOf(mTransaction.getMoneyTrading()));
            if(mTransaction.getMoneyTrading() >= 0) {
                mTextMoney.setTextColor(getResources().getColor(R.color.colorMoneyTradingPositive));
            }
            else {
                mTextMoney.setTextColor(getResources().getColor(R.color.colorMoneyTradingNegative));
            }
            mTextNote.setText(mTransaction.getTransactionNote());
            mTextWallet.setText(mTransaction.getWallet().getWalletName());
            if(mTransaction.getMediaUri() != null && !mTransaction.getMediaUri().isEmpty() ) {
                updateImagePreView(mTransaction.getMediaUri());
            }
        }
    }
    private void updateImagePreView(String uri) {
        Bitmap bitmap = BitmapUtils.loadImageFromStorage(uri);
        if(bitmap != null) {
            int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
            mImgPreview.setImageBitmap(scaled);
            mImgPreview.setVisibility(View.VISIBLE);
        }
        else {
            mImgPreview.setVisibility(View.INVISIBLE);
        }
    }

}
