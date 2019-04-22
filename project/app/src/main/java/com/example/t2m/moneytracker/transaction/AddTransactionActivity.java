package com.example.t2m.moneytracker.transaction;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.t2m.moneytracker.R;
import com.example.t2m.moneytracker.adapter.WalletListAdapter;
import com.example.t2m.moneytracker.dataaccess.ITransactionsDAO;
import com.example.t2m.moneytracker.dataaccess.IWalletsDAO;
import com.example.t2m.moneytracker.dataaccess.TransactionsDAOImpl;
import com.example.t2m.moneytracker.dataaccess.WalletsDAOImpl;
import com.example.t2m.moneytracker.model.MTDate;
import com.example.t2m.moneytracker.model.Transaction;
import com.example.t2m.moneytracker.model.Category;
import com.example.t2m.moneytracker.model.Wallet;
import com.example.t2m.moneytracker.utilities.BitmapUtils;
import com.example.t2m.moneytracker.utilities.TransactionsManager;
import com.example.t2m.moneytracker.wallet.SelectCategoryActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddTransactionActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_CATEGORY = 1;
    private static final int REQUEST_CODE_GALLERY = 2;
    private static final int REQUEST_CODE_CAMERA = 3 ;
    public static final String EXTRA_TRANSACTION = "com.example.t2m.moneytracker.extra.transaction";
    private static final Object IMAGE_DIRECTORY = "images";

    private EditText mTextMoney;
    private EditText mTextCategory;
    private EditText mTextNote;
    private EditText mTextDate;
    private EditText mTextWallet;
    private ImageView mImgCamera;
    private ImageView mImgPicture;
    private ImageView mImgPreView;
    private Calendar mCalendar;

    private FirebaseUser mCurrentUser;
    private IWalletsDAO iWalletsDAO;
    private ITransactionsDAO iTransactionsDAO;
    private List<Wallet> mListWallet;
    private Category mCurrentCategory =null;
    private Wallet mCurrentWallet = null;
    private Bitmap mCurrentImage = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addControls();
        updateLabelDate();
        addEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save : {
                onClickedAdd();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
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
        mImgCamera = findViewById(R.id.image_capture_picture);
        mImgPicture = findViewById(R.id.image_choose_picture);
        mImgPreView = findViewById(R.id.image_preview);
        mCalendar = Calendar.getInstance();
        iWalletsDAO = new WalletsDAOImpl(this);
        iTransactionsDAO = new TransactionsDAOImpl(this);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mListWallet = iWalletsDAO.getAllWalletByUser(mCurrentUser.getUid());
    }

    private void addEvents() {
        mTextCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickedCategory(v);
            }
        });
        mTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickedDate(v);
            }
        });
        mTextWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickedWallet(v);
            }
        });
        mImgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhotoFromCamera();
            }
        });
        mImgPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhotoFromGallary();
            }
        });
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    private void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY);
    }

    private void onClickedAdd() {
        if(mCurrentWallet == null) {
            mTextWallet.setError("Chọn ví của bạn");
            mTextWallet.requestFocus();
            return;
        }
        if(mCurrentCategory == null) {
            mTextCategory.setError("Chọn kiểu giao dịch");
            mTextCategory.requestFocus();
            return;
        }
        String mMediaUri = null;
        if(mCurrentImage != null) {
            mMediaUri = BitmapUtils.saveImage(this,mCurrentImage);
        }
        float money = Float.parseFloat(mTextMoney.getText().toString());
        String note = mTextNote.getText().toString();
        Date date = mCalendar.getTime();
        Transaction transaction = new Transaction.TransactionBuilder()
                .setTransactionDate(date)
                .setCategory(mCurrentCategory)
                .setWallet(mCurrentWallet)
                .setCurrencyCode(mCurrentWallet.getCurrencyCode())
                .setMoneyTrading(money)
                .setTransactionNote(note)
                .setMediaUri(mMediaUri)
                .build();

        TransactionsManager.getInstance(this).addTransaction(transaction);

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TRANSACTION,transaction);
        setResult(RESULT_OK,intent);
        finish();
    }
    private void onClickedCancle() {

        setResult(RESULT_CANCELED);
        finish();
    }

    private void onClickedCategory(View v) {
        Intent intent = new Intent(this,SelectCategoryActivity.class);
        startActivityForResult(intent,REQUEST_CODE_CATEGORY);
    }
    private void onClickedDate(View v) {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mCalendar.set(year,month,dayOfMonth);
                updateLabelDate();
            }
        };
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int date = mCalendar.get(Calendar.DATE);
        new DatePickerDialog(this,dateSetListener,year,month,date).show();
    }

    private void onClickedWallet(View v) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.drawable.ic_account_balance_wallet_black_24dp);
        builderSingle.setTitle("Chọn ví của bạn");

        final ArrayAdapter arrayAdapter = new WalletListAdapter(this, R.layout.custom_item_wallet,mListWallet);


        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCurrentWallet = mListWallet.get(which);
                updateUI();
                dialog.dismiss();
            }
        });
        builderSingle.show();
    }

    private void updateLabelDate() {
        Date date = mCalendar.getTime();
        if(DateUtils.isToday(date.getTime())) {
            mTextDate.setText(getResources().getString(R.string.today));
        }
        else {
            String strDate = new MTDate(mCalendar).toIsoDateShortTimeString();
            mTextDate.setText(strDate);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK) {
            if(requestCode == REQUEST_CODE_CATEGORY) {
                mCurrentCategory =(Category) data.getSerializableExtra(SelectCategoryActivity.EXTRA_CATEGORY);
                updateUI();
            }
            else if(requestCode == REQUEST_CODE_GALLERY) {
                if (data != null) {
                    Uri contentURI = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                        mCurrentImage = bitmap;
                        updateImagePreView(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(AddTransactionActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else if(requestCode == REQUEST_CODE_CAMERA) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                mCurrentImage = thumbnail;
                updateImagePreView(thumbnail);
            }
        }

    }

    private void updateImagePreView(Bitmap bitmap) {
        if(bitmap != null) {
            int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
            mImgPreView.setImageBitmap(scaled);
        }
    }

    private void updateUI() {
        if(mCurrentCategory != null) {
            mTextCategory.setText(mCurrentCategory.getCategory());
            ImageView imageView = findViewById(R.id.image_transaction_category);
            // lấy ảnh từ asset
            String base_path = "category/";
            try {
                Drawable img = Drawable.createFromStream(this.getAssets().open(base_path + mCurrentCategory.getIcon()),null);
                imageView.setImageDrawable(img);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(mCurrentWallet != null) {
            mTextWallet.setText(mCurrentWallet.getWalletName());
        }
    }
}