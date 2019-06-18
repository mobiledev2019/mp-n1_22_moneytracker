package com.example.t2m.moneytracker.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.t2m.moneytracker.R;
import com.example.t2m.moneytracker.budget.OnBudgetItemClickListener;
import com.example.t2m.moneytracker.model.Budget;
import com.example.t2m.moneytracker.model.DateRange;
import com.example.t2m.moneytracker.model.MTDate;

import java.io.IOException;
import java.util.List;

public class BudgetsAdapter extends RecyclerView.Adapter<BudgetsAdapter.ViewHolder> {


    List<Budget> mBudgets;

    public void setListener(OnBudgetItemClickListener listener) {
        this.listener = listener;
    }

    OnBudgetItemClickListener listener;

    public BudgetsAdapter(List<Budget> budgets) {
        mBudgets = budgets;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_budget_overview, viewGroup, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Budget budget = mBudgets.get(i);
        Context context = viewHolder.itemView.getContext();
        // lấy ảnh từ asset
        String base_path = "category/";
        try {
            Drawable img = Drawable.createFromStream(context.getAssets().open(base_path + budget.getCategory().getIcon()), null);
            viewHolder.iconGoal.setImageDrawable(img);
        } catch (IOException e) {
            e.printStackTrace();
        }

        viewHolder.textBudgetTitle.setText(budget.getCategory().getCategory());
        viewHolder.textTimeRange.setText(new DateRange(budget.getTimeStart(), budget.getTimeEnd()).toString());

        String textLeft = context.getString(R.string.remain_prefix);
        MTDate now = new MTDate();

        long remainDays =(long) Math.ceil( (budget.getTimeEnd().getMillis() - now.getMillis()) / 24 / 60 / 60 / 1000.0f);
        if (remainDays > 0) {
            textLeft += " " + remainDays + " " + context.getString(R.string.day);
        }
        viewHolder.textTimeLeft.setText(textLeft);

        float remain = budget.getAmount() - budget.getSpent();
        if(remain < 0) {
            remain = Math.abs(remain);
            viewHolder.textCurrent.setText(context.getString(R.string.transaction_detail_cashback_over));
        }
        else {
            viewHolder.textCurrent.setText(context.getString(R.string.transaction_detail_cashback_left));
        }
        viewHolder.textAmountLeft.setText(String.valueOf(remain));
        int progress = (int) (budget.getSpent() / budget.getAmount() * 100);
        viewHolder.progressBudget.setMax(100);
        viewHolder.progressBudget.setProgress(progress);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (progress <= 100) {
                viewHolder.progressBudget.setProgressTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorMoneyTradingPositive)));
            } else {
                viewHolder.progressBudget.setProgressTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorMoneyTradingNegative)));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mBudgets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnLongClickListener{
        ImageView iconGoal;
        ImageView iconWallet;
        TextView textBudgetTitle;
        TextView textTimeRange;
        TextView textAmountLeft;
        TextView textTimeLeft;
        TextView textCurrent;
        ProgressBar progressBudget;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconGoal = itemView.findViewById(R.id.icon_goal);
            iconWallet = itemView.findViewById(R.id.iconWallet);
            textBudgetTitle = itemView.findViewById(R.id.txtBudgetTitle);
            textTimeRange = itemView.findViewById(R.id.txtTimeRanger);
            textTimeLeft = itemView.findViewById(R.id.txtTimeLeft);
            textCurrent = itemView.findViewById(R.id.current);
            textAmountLeft = itemView.findViewById(R.id.txtAmountLeft);
            progressBudget = itemView.findViewById(R.id.prgBudget);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(v,getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            listener.onItemLongClick(v,getAdapterPosition());
            return false;
        }


    }

}
