package com.nearblyapp.nearbly.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nearblyapp.nearbly.CategoryActivity;
import com.nearblyapp.nearbly.R;
import com.nearblyapp.nearbly.databases.NearblyDB;
import com.nearblyapp.nearbly.grades.Category;
import com.nearblyapp.nearbly.grades.Order;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends ArrayAdapter<Order> {

    private final Context context;
    private final ArrayList<Order> values;
    Typeface typeface;
    int categoryId;
    MenuInflater inf;
    NearblyDB orders;

    public OrderAdapter(Context context, ArrayList<Order> values, int categoryId){
        super(context, R.layout.order_activity,values);
        this.context=context;
        this.values=values;
        this.categoryId = categoryId;
        orders = new NearblyDB(context);
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent){

        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final Order order = values.get(position);

        final View rowView=inflater.inflate(R.layout.order_activity, parent, false);

        final LinearLayout ll_order = (LinearLayout) rowView.findViewById(R.id.ll_order);
        final ImageView delete_btn = (ImageView) rowView.findViewById(R.id.delete_order);
        final CheckBox chb_order =(CheckBox) rowView.findViewById(R.id.order_check);
        chb_order.setText(order.getOrder());

        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/glober_semibold.otf");
        chb_order.setTypeface(typeface);

        try {
            orders.open();
            if (orders.getOrderCount() > 0) {
                if (orders.isChecked(order.getId())) {
                    chb_order.setChecked(true);
                }
            }
            orders.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    orders.open();
                    orders.deleteOrder(order.getId());
                    orders.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                values.remove(order);
                CategoryActivity.orderAdapter.notifyDataSetChanged();
                if (values.size() == 0)
                {
                    CategoryActivity.menuBar.findItem(R.id.delete_list).setIcon(R.mipmap.ic_delete_list_disabled);
                    CategoryActivity.menuBar.findItem(R.id.delete_list).setEnabled(false);
                    CategoryActivity.menuBar.findItem(R.id.complete_list).setEnabled(false);
                    Toast.makeText(context, "Liste bos", Toast.LENGTH_LONG).show();
                }
            }
        });


        chb_order.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    orders.open();
                    if (buttonView.isChecked()) {
                        orders.updateOrder(order.getId(), 1);
                        if (orders.isAllUncheck(categoryId) != values.size()) {
                            CategoryActivity.menuBar.findItem(R.id.complete_list).setIcon(R.mipmap.ic_complete);
                            CategoryActivity.menuBar.findItem(R.id.complete_list).setEnabled(true);
                        }
                    } else {
                        orders.updateOrder(order.getId(), 0);
                        if (orders.isAllUncheck(categoryId) == values.size()) {
                            CategoryActivity.menuBar.findItem(R.id.complete_list).setIcon(R.mipmap.ic_complete_disabled);
                            CategoryActivity.menuBar.findItem(R.id.complete_list).setEnabled(false);
                        }
                        Toast.makeText(context, "Alım iptal", Toast.LENGTH_SHORT).show();
                    }


                    orders.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }



            }
        });


        return rowView;
    }


}