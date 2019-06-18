package com.example.t2m.moneytracker.budget;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.t2m.moneytracker.R;
import com.example.t2m.moneytracker.adapter.WalletListAdapter;
import com.example.t2m.moneytracker.dataaccess.BudgetDAOImpl;
import com.example.t2m.moneytracker.dataaccess.IBudgetDAO;
import com.example.t2m.moneytracker.dataaccess.WalletsDAOImpl;
import com.example.t2m.moneytracker.model.Budget;
import com.example.t2m.moneytracker.model.Category;
import com.example.t2m.moneytracker.model.DateRange;
import com.example.t2m.moneytracker.model.MTDate;
import com.example.t2m.moneytracker.model.Transaction;
import com.example.t2m.moneytracker.model.Wallet;
import com.example.t2m.moneytracker.transaction.AddTransactionActivity;
import com.example.t2m.moneytracker.utilities.BitmapUtils;
import com.example.t2m.moneytracker.utilities.TransactionsManager;
import com.example.t2m.moneytracker.view.CurrencyEditText;
import com.example.t2m.moneytracker.wallet.SelectCategoryActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddBudgetActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_CATEGORY = 1;
    public static final int REQUEST_CODE_TIME_RANGE = 2;
    public static final String EXTRA_BUDGET = "com.example.t2m.money_tracker.extra.budget";

    //
    RelativeLayout mPageCategory;
    TableRow mPageAmount;
    RelativeLayout mPageTimeRange;
    RelativeLayout mPageWallet;
    CheckBox mCheckRepeatBudget;
    TextView mTextCategory;
    TextView mTextWallet;
    TextView mTextTimeRange;

    Wallet mCurrentWallet;
    private List<Wallet> mListWallet;
    FirebaseUser mCurrentUser;
    Category mCurrentCategory;
    CurrencyEditText mTextAmount;
    DateRange mDateRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addControls();
        addEvents();
    }

    private void addControls() {
        mPageCategory = findViewById(R.id.pageCategory);
        mPageAmount = findViewById(R.id.pageAmount);
        mPageTimeRange = findViewById(R.id.pageTimeRange);
        mPageWallet = findViewById(R.id.pageWallet);
        mCheckRepeatBudget = findViewById(R.id.cbx_repeat_budget);
        mTextCategory = findViewById(R.id.category);
        mTextWallet = findViewById(R.id.wallet_name);
        mTextAmount = findViewById(R.id.amount_budget);
        mTextTimeRange = findViewById(R.id.time_created);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mListWallet = new WalletsDAOImpl(this).getAllWalletByUser(mCurrentUser.getUid());
    }

    private void addEvents() {
        mPageCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickedCategory(v);
            }
        });
        mPageAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickedAmount(v);
            }
        });
        mPageTimeRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickedTimeRange(v);
            }
        });
        mPageWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickedWallet(v);
            }
        });

    }

    private void onClickedTimeRange(View v) {
        Intent intent = new Intent(this, SelectBudgetTimeRangeActivity.class);
        startActivityForResult(intent, REQUEST_CODE_TIME_RANGE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onClickedCancel();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                onClickedAdd();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    private void onClickedAmount(View v) {
        mTextAmount.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void onClickedAdd() {
        if(mCurrentCategory == null) {
            mTextCategory.requestFocus();
            mTextCategory.setError("Chọn nhóm");
            return;
        }
        if(mTextAmount.getCleanDoubleValue() <= 0) {
            mTextAmount.requestFocus();
            mTextAmount.setError("Số tiền phải lớn hơn 0");
            return;
        }
        if(mCurrentWallet == null) {
            mTextWallet.requestFocus();
            mTextWallet.setError("Chọn ví");
            return;
        }
        if(mDateRange == null) {
            mTextTimeRange.requestFocus();
            mTextTimeRange.setError("Chọn khoảng thời gian");
            return;
        }
        Budget budget = new Budget();
        budget.setWallet(mCurrentWallet);
        budget.setCategory(mCurrentCategory);
        budget.setAmount((float) mTextAmount.getCleanDoubleValue());
        budget.setSpent(0);
        budget.setTimeStart(mDateRange.getDateFrom());
        budget.setTimeEnd(mDateRange.getDateTo());
        budget.setLoop(mCheckRepeatBudget.isChecked());
        budget.setStatus("STARTED");
        IBudgetDAO budgetDAO = new BudgetDAOImpl(this);
        budgetDAO.insertBudget(budget);
        budgetDAO.updateBudgetSpent(budget);

        Intent intent = new Intent();
        intent.putExtra(EXTRA_BUDGET,budget);
        setResult(RESULT_OK,intent);
        finish();
    }

    private void onClickedCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void onClickedCategory(View v) {
        Intent intent = new Intent(this, SelectCategoryActivity.class);
        startActivityForResult(intent, REQUEST_CODE_CATEGORY);
    }

    private void onClickedWallet(View v) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.drawable.ic_account_balance_wallet_black_24dp);
        builderSingle.setTitle("Chọn ví của bạn");

        final ArrayAdapter arrayAdapter = new WalletListAdapter(this, R.layout.custom_item_wallet, mListWallet);


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

    private void updateUI() {
        if (mCurrentCategory != null) {
            mTextCategory.clearFocus();
            mTextCategory.setError(null);
            mTextCategory.setText(mCurrentCategory.getCategory());
            ImageView imageView = findViewById(R.id.cate_icon);
            // lấy ảnh từ asset
            String base_path = "category/";
            try {
                Drawable img = Drawable.createFromStream(this.getAssets().open(base_path + mCurrentCategory.getIcon()), null);
                imageView.setImageDrawable(img);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (mCurrentWallet != null) {
            mTextWallet.clearFocus();
            mTextWallet.setError(null);
            mTextWallet.setText(mCurrentWallet.getWalletName());
        }

        if(mDateRange != null) {
            mTextTimeRange.clearFocus();
            mTextTimeRange.setError(null);
            mTextTimeRange.setText(mDateRange.toString());
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CATEGORY) {
                mCurrentCategory = (Category) data.getSerializableExtra(SelectCategoryActivity.EXTRA_CATEGORY);
                updateUI();
            }
            if(requestCode == REQUEST_CODE_TIME_RANGE) {
                mDateRange =  (DateRange) data.getSerializableExtra(SelectBudgetTimeRangeActivity.EXTRA_BUDGET_TIME_RANGE);
                updateUI();
            }
        }

    }
}
