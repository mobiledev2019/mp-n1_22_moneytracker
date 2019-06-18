package com.example.t2m.moneytracker.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.t2m.moneytracker.R;
import com.example.t2m.moneytracker.budget.DetailBudgetActivity;
import com.example.t2m.moneytracker.model.Budget;
import com.example.t2m.moneytracker.utilities.CurrencyUtils;
import com.example.t2m.moneytracker.utilities.NotificationsUtils;

public class BudgetNotifications {

    public static String CHANNEL_ID = "Budget_NotificationsChannel";
    private static final int ID_NOTIFICATION = 0x000A;

    public Context getContext() {
        return mContext;
    }

    private Context mContext;
    public BudgetNotifications(Context context) {
        super();
        mContext = context;
    }

    public void notifyBudgetOverSpending(Budget budget) {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // create pending intent to view detail budget detail
        Intent intent = new Intent(getContext(),DetailBudgetActivity.class);
        intent.putExtra(DetailBudgetActivity.EXTRA_BUDGET,budget);

        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(),0,intent,0);

        // create notification

        try {
            NotificationsUtils.createNotificationChannel(getContext(),CHANNEL_ID);

            Notification notification = new NotificationCompat.Builder(getContext(),CHANNEL_ID)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(getContext().getString(R.string.app_name))
                    .setContentText(getContext().getString(R.string.notification_budget_overspending) + " " + CurrencyUtils.formatVnCurrence(String.valueOf(budget.getSpent() - budget.getAmount())))
                    .setSubText(getContext().getString(R.string.in) + " " + budget.getWallet().getWalletName())
                    .setSmallIcon(R.drawable.logo)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.VISIBILITY_PUBLIC)
                    .build();

            notificationManager.cancel(ID_NOTIFICATION);
            notificationManager.notify(ID_NOTIFICATION,notification);
        }
        catch (Exception e) {

        }
    }

}
