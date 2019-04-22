package com.example.t2m.moneytracker.wallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.t2m.moneytracker.MainActivity;
import com.example.t2m.moneytracker.R;
import com.example.t2m.moneytracker.dataaccess.ITransactionsDAO;
import com.example.t2m.moneytracker.dataaccess.IWalletsDAO;
import com.example.t2m.moneytracker.dataaccess.MoneyTrackerDBHelper;
import com.example.t2m.moneytracker.dataaccess.TransactionsDAOImpl;
import com.example.t2m.moneytracker.dataaccess.WalletsDAOImpl;
import com.example.t2m.moneytracker.model.Category;
import com.example.t2m.moneytracker.model.MTDate;
import com.example.t2m.moneytracker.model.Transaction;
import com.example.t2m.moneytracker.model.TransactionTypes;
import com.example.t2m.moneytracker.model.Wallet;
import com.google.firebase.auth.FirebaseAuth;

public class AddWalletActivity extends AppCompatActivity {

    Button btnNhom;
    EditText txtTen,txtSotien;
    Switch swtTinhtong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wallet);
        // add back arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addControls();
        addEvents();
    }

    private void addEvents() {

    }

    private void addWallet() {
        String name = txtTen.getText().toString();
        String sotien = txtSotien.getText().toString();
        if(name.isEmpty() ) {
            txtTen.setError("Tên không được để trống");
            txtTen.requestFocus();
            return;
        }
        if (sotien.isEmpty()){
            txtSotien.setError("Không được để trống");
            txtSotien.requestFocus();
            return;
        }

        initWaller(name,sotien);
    }

    private void initWaller(String name, String sotien) {
        int soTien = Integer.parseInt(txtSotien.getText().toString());
        IWalletsDAO iWalletsDAO = new WalletsDAOImpl(this);
        ITransactionsDAO iTransactionsDAO = new TransactionsDAOImpl(this);
        Wallet wallet = new Wallet.WalletBuilder()
                .setWalletName(name)
                .setCurrentBalance(soTien)
                .setUserUID(FirebaseAuth.getInstance().getUid())
                .setCurrencyCode("VND")
                .setWalletType(1)
                .setImageSrc("")
                .build();
        boolean added_wallet = iWalletsDAO.insertWallet(wallet);

        Category category = new Category();
        category.setId(56);
        category.setType(TransactionTypes.INCOME.getValue());
        Transaction transaction = new Transaction.TransactionBuilder()
                .setTransactionDate(new MTDate().toDate())
                .setMoneyTrading(soTien)
                .setTransactionNote("Initial Wallet")
                .setCategory(category)
                .setWallet(wallet)
                .setCurrencyCode("VND")
                .build();
        boolean added_transactin =iTransactionsDAO.insertTransaction(transaction);
        if(added_wallet && added_transactin) {
            Intent intent = new Intent(AddWalletActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(this,"Khởi tạo ví thất bại",Toast.LENGTH_SHORT).show();
            if(added_wallet) {
                iWalletsDAO.deleteWallet(wallet.getWalletId());
            }
            else if(added_transactin) {
                iTransactionsDAO.deleteTransaction(transaction);
            }
        }
    }

    private void addControls() {
        //
        txtTen = (EditText)findViewById(R.id.txtTen);
        txtSotien = (EditText)findViewById(R.id.txtSotien);
        btnNhom = (Button)findViewById(R.id.btnNhom);
        swtTinhtong = (Switch)findViewById(R.id.swtTichtong);
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
                addWallet();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onClickedBack();
        return true;
    }

    private void onClickedBack() {
        finish();
    }
}
