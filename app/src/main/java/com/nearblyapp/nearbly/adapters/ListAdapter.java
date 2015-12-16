package com.nearblyapp.nearbly.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nearblyapp.nearbly.MainActivity;
import com.nearblyapp.nearbly.R;
import com.nearblyapp.nearbly.databases.NearblyDB;
import com.nearblyapp.nearbly.grades.Category;
import com.nearblyapp.nearbly.grades.Order;
import com.nearblyapp.nearbly.grades.OrderList;

import java.sql.SQLException;
import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<OrderList> {

    private final Context context;
    private final ArrayList<OrderList> values;
    Typeface typeface;
    int orderCount;

    NearblyDB orders;

    public ListAdapter(Context context, ArrayList<OrderList> values){
        super(context, R.layout.order_activity,values);
        this.context=context;
        this.values=values;
        orders = new NearblyDB(context);
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent){

        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final OrderList oList = values.get(position);


        final View rowView=inflater.inflate(R.layout.list_main, parent, false);

        TextView listName = (TextView) rowView.findViewById(R.id.c_listName);
        TextView listOrderCount = (TextView) rowView.findViewById(R.id.c_orderCount);
        TextView listDate = (TextView) rowView.findViewById(R.id.c_listDate);
        ImageView deleteList = (ImageView) rowView.findViewById(R.id.delete_list);

        deleteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert(oList);
            }
        });

        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/glober_semibold.otf");
        listName.setTypeface(typeface);

        try {
            orders.open();
            orderCount = orders.getListedOrderCount(oList.getListId());
            if (orderCount > 0) {
                listOrderCount.setVisibility(View.VISIBLE);
                listOrderCount.setText(String.valueOf(orderCount));
            }
            orders.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        listName.setText(oList.getListName());
        listDate.setText(oList.getListSaveDate());

        return rowView;
    }

    public void showAlert(final OrderList oList){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle(oList.getListName());

            alertDialog.setMessage("Listeyi silmek istiyor musunuz?");


        alertDialog.setPositiveButton("SİL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    orders.open();
                    orders.deleteList(oList.getListId());
                    orders.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                values.remove(oList);
                MainActivity.listAdapter.notifyDataSetChanged();

            }
        });

        alertDialog.setNegativeButton("İPTAL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }
}