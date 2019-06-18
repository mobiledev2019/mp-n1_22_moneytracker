package com.example.t2m.moneytracker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.t2m.moneytracker.budget.BudgetFragment;
import com.example.t2m.moneytracker.budget.DetailBudgetActivity;
import com.example.t2m.moneytracker.dataaccess.IWalletsDAO;
import com.example.t2m.moneytracker.dataaccess.TransactionsDAOImpl;
import com.example.t2m.moneytracker.model.Transaction;
import com.example.t2m.moneytracker.model.Budget;
import com.example.t2m.moneytracker.model.Wallet;
import com.example.t2m.moneytracker.setting.Setting;

import com.example.t2m.moneytracker.dataaccess.WalletsDAOImpl;

import com.example.t2m.moneytracker.statistical.Fragment_ThongKe_Tab0;
import com.example.t2m.moneytracker.statistical.StatisticalTabFragment;
import com.example.t2m.moneytracker.sync.SyncCloudFirestore;
import com.example.t2m.moneytracker.transaction.TransactionListSearch;
import com.example.t2m.moneytracker.transaction.TransactionTabFragment;
import com.example.t2m.moneytracker.utilities.WalletsManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView txtEmail, txtUsername;
    FirebaseAuth mAuth;
    public static final int REQUEST_SETTING_CODE = 1;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_transaction));

        IWalletsDAO iWalletsDAO = new WalletsDAOImpl(this);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        iWalletsDAO.getAllWalletByUser(user.getUid());

        txtEmail = navigationView.getHeaderView(0).findViewById(R.id.txtEmail);
        txtUsername = navigationView.getHeaderView(0).findViewById(R.id.txtUsername);

        if (user != null) {
            if (txtUsername != null && user.getDisplayName() != null) {
                txtUsername.setText(user.getDisplayName());
            }
            if (txtEmail != null) txtEmail.setText(user.getEmail());
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_save; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_sync) {
            onSyncAction();
        }
        else if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, Setting.class);
            startActivityForResult(intent, REQUEST_SETTING_CODE);
            return true;
        } else if (id == R.id.search_transaction_by_date) {
            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_search_transactions, null);
            final EditText txtdateSelect = (EditText) mView.findViewById(R.id.txtdateSelect);
            Button btnok = (Button) mView.findViewById(R.id.btnOK);
            mBuilder.setView(mView);

            final AlertDialog mDialog = mBuilder.create();
            mDialog.show();

            txtdateSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, month);
                            calendar.set(Calendar.DATE, dayOfMonth);

                            txtdateSelect.setText(sdf.format(calendar.getTime()));
                        }
                    };

                    DatePickerDialog mDatePickerDialog = new DatePickerDialog(
                            MainActivity.this,
                            callback,
                            calendar.get(calendar.YEAR),
                            calendar.get(calendar.MONTH),
                            calendar.get(calendar.DATE)
                    );
                    mDatePickerDialog.show();
                }
            });
            btnok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    String txtdateStart = txtdateSelect.getText().toString() + " 00:00";
                    String txtdateEnd = txtdateSelect.getText().toString() + " 23:59";
                    if (txtdateStart.isEmpty()) {
                        txtdateSelect.setError("Không được để trống");
                        txtdateSelect.requestFocus();
                        return;
                    } else {
                        try {

                            Date mDateStart = sdf1.parse(String.valueOf(txtdateStart));
                            Date mDateEnd = sdf1.parse(txtdateEnd);
                            long milisStart = mDateStart.getTime();
                            long milisEnd = mDateEnd.getTime();
//                                Toast.makeText(MainActivity.this, "" + milis, Toast.LENGTH_LONG).show();
                            List<Transaction> listtrans = new ArrayList<>();

                            TransactionsDAOImpl trans = new TransactionsDAOImpl(MainActivity.this);
                            List list = trans.getAllTransactionDataByDate(milisStart, milisEnd);
                            listtrans.addAll(list);
                            TransactionListSearch transactionListFragment = new TransactionListSearch().newInstance(listtrans);
                            getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, transactionListFragment).addToBackStack(null).commit();

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else if (id == R.id.search_transaction_by_time) {
            try {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.fragment_search_transaction, null);
                final Button btndateStart = (Button) mView.findViewById(R.id.btndateStart);
                final Button btndateEnd = (Button) mView.findViewById(R.id.btndateEnd);
                Button btnok = (Button) mView.findViewById(R.id.btnOki);
                mBuilder.setView(mView);
                final AlertDialog mDialog = mBuilder.create();
                mDialog.show();
                btndateStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DATE, dayOfMonth);

                                btndateStart.setText(sdf.format(calendar.getTime()));
                            }
                        };

                        DatePickerDialog mDatePickerDialog = new DatePickerDialog(
                                MainActivity.this,
                                callback,
                                calendar.get(calendar.YEAR),
                                calendar.get(calendar.MONTH),
                                calendar.get(calendar.DATE)
                        );
                        mDatePickerDialog.show();
                    }
                });
                btndateEnd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DATE, dayOfMonth);

                                btndateEnd.setText(sdf.format(calendar.getTime()));
                            }
                        };

                        DatePickerDialog mDatePickerDialog = new DatePickerDialog(
                                MainActivity.this,
                                callback,
                                calendar.get(calendar.YEAR),
                                calendar.get(calendar.MONTH),
                                calendar.get(calendar.DATE)
                        );
                        mDatePickerDialog.show();
                    }
                });
                btnok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        String dateStart = btndateStart.getText().toString() + " 00:00";
                        String dateEnd = btndateEnd.getText().toString() + " 23:59";
                        if (dateStart.isEmpty()) {
                            btndateStart.setError("Không được để trống");
                            btndateStart.requestFocus();
                            return;
                        }
                        if (dateEnd.isEmpty()) {
                            btndateEnd.setError("Không được để trống");
                            btndateEnd.requestFocus();
                            return;
                        } else {
                            try {
                                Date mDateStart = sdf1.parse(dateStart);
                                Date mDateEnd = sdf1.parse(dateEnd);
                                long milisStart = mDateStart.getTime();
                                long milisEnd = mDateEnd.getTime();
//                                Toast.makeText(MainActivity.this, "" + milis, Toast.LENGTH_LONG).show();
                                List<Transaction> listtrans = new ArrayList<>();

                                TransactionsDAOImpl trans = new TransactionsDAOImpl(MainActivity.this);
                                List list = trans.getAllTransactionDataByDate(milisStart, milisEnd);
                                listtrans.addAll(list);
                                TransactionListSearch transactionListFragment = new TransactionListSearch().newInstance(listtrans);

                                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, transactionListFragment).addToBackStack(null).commit();

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (id == R.id.search_transaction_by_items) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_search_items, null);
            Button btnIncome = (Button) mView.findViewById(R.id.btnIncome);
            Button btnExpense = (Button) mView.findViewById(R.id.btnExpense);
            Button btnLoan = (Button) mView.findViewById(R.id.btnLoan);
            Button btnBorrowing = (Button) mView.findViewById(R.id.btnBorrowing);

            mBuilder.setView(mView);
            final AlertDialog mDialog = mBuilder.create();
            mDialog.show();

            btnIncome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    List<Transaction> listtrans = new ArrayList<>();

                    TransactionsDAOImpl trans = new TransactionsDAOImpl(MainActivity.this);
                    List list = trans.getAllTransactionDataByType(2);
                    listtrans.addAll(list);
                    TransactionListSearch transactionListFragment = new TransactionListSearch().newInstance(listtrans);

                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, transactionListFragment).addToBackStack(null).commit();
                }
            });
            btnBorrowing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    List<Transaction> listtrans = new ArrayList<>();

                    TransactionsDAOImpl trans = new TransactionsDAOImpl(MainActivity.this);
                    List list = trans.getAllTransactionDataByType(4);
                    listtrans.addAll(list);
                    TransactionListSearch transactionListFragment = new TransactionListSearch().newInstance(listtrans);

                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, transactionListFragment).addToBackStack(null).commit();
                }
            });
            btnExpense.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    List<Transaction> listtrans = new ArrayList<>();

                    TransactionsDAOImpl trans = new TransactionsDAOImpl(MainActivity.this);
                    List list = trans.getAllTransactionDataByType(1);
                    listtrans.addAll(list);
                    TransactionListSearch transactionListFragment = new TransactionListSearch().newInstance(listtrans);

                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, transactionListFragment).addToBackStack(null).commit();
                }
            });
            btnLoan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    List<Transaction> listtrans = new ArrayList<>();

                    TransactionsDAOImpl trans = new TransactionsDAOImpl(MainActivity.this);
                    List list = trans.getAllTransactionDataByType(3);
                    listtrans.addAll(list);
                    TransactionListSearch transactionListFragment = new TransactionListSearch().newInstance(listtrans);

                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, transactionListFragment).addToBackStack(null).commit();
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    private void onSyncAction() {
        new syncTransactions(this).execute();
    }




    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //int checkedId = navigationView.getCheckedItem().getItemId();
        //if(checkedId == id) ;
        Fragment fragment;
        Class fragmentClass = null;
        if (id == R.id.nav_transaction) {
            Log.d(MainActivity.class.getSimpleName(), "Start Transaction Activity");
            fragmentClass = TransactionTabFragment.class;


        } else if (id == R.id.nav_chart) {
            Log.d(MainActivity.class.getSimpleName(), "Start Transaction Activity");
            fragmentClass = Fragment_ThongKe_Tab0.class;
        }
//        else if (id == R.id.nav_){
//          fragmentClass = SearchTransaction.class;
//        }
        else if (id == R.id.nav_manage) {
            Intent intent = new Intent(MainActivity.this, Setting.class);
            startActivity(intent);
            return true;

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_budget) {
            fragmentClass = BudgetFragment.class;
        } else if (id == R.id.nav_periodic_transaction) {
        }

        try {
            if (fragmentClass != null) {
                fragment = (Fragment) fragmentClass.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, fragment).addToBackStack(null).commit();
                // Highlight the selected item has been done by NavigationView
                // item.setChecked(true);
                // Set action bar title
                setTitle(item.getTitle());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void recreateAppMain(AppCompatActivity appCompatActivity) {
        appCompatActivity.recreate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SETTING_CODE && resultCode == RESULT_OK) {
            MainActivity.recreateAppMain(this);
        }
    }

    // =====================================================

    protected class syncTransactions extends AsyncTask<Void, Void, Void> {
        private Dialog mDialog;
        private Activity activity;

        private syncTransactions(Activity activity) {
            this.activity = activity;
        }
        @Override
        protected void onPreExecute() {
            mDialog = new Dialog(activity);
            // chu y phai dat truoc setcontentview
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setContentView(R.layout.loading_view);
            mDialog.setTitle(getResources().getString(R.string.sync_in_progress));
            mDialog.setCancelable(false);
            mDialog.show();
        }

        protected Void doInBackground(Void... unused) {

            // su dung phuong thuc de update lai adapter
            activity.runOnUiThread(runnableSyncTransaction);

            return (null);
        }

        protected void onPostExecute(Void unused) {

            mDialog.dismiss();
        }
    }


    private Runnable runnableSyncTransaction = new Runnable() {

        @Override
        public void run() {
            SyncCloudFirestore sync = new SyncCloudFirestore(getApplicationContext());
            Wallet wallet = WalletsManager.getInstance(getApplicationContext()).getCurrentWallet();
            sync.onSync(wallet);
            //sync.addWallet(user.getUid(), wallet);
        }
    };
}
