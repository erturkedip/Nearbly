package com.nearblyapp.nearbly.adapters;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nearblyapp.nearbly.MainActivity;
import com.nearblyapp.nearbly.R;
import com.nearblyapp.nearbly.databases.NearblyDB;
import com.nearblyapp.nearbly.grades.Category;

import java.sql.SQLException;
import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<Category>{

    //vdfvdvd

    private final Context context;
    private final ArrayList<Category> values;
    Typeface typeface;
    NearblyDB categories;

    public CategoryAdapter(Context context, ArrayList<Category> values){
        super(context,R.layout.list_category,values);
        this.context=context;
        this.values=values;
        categories = new NearblyDB(context);
        }

    @Override
    public View getView(int position,View convertView,ViewGroup parent){

        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final Category category = values.get(position);



        View rowView=inflater.inflate(R.layout.list_category, parent, false);

        TextView txt_categoryName=(TextView)rowView.findViewById(R.id.categoryName);
        ImageView imageView=(ImageView)rowView.findViewById(R.id.logo);
        TextView txt_ordercount = (TextView) rowView.findViewById(R.id.orderCount);
        ImageView delete_btn = (ImageView) rowView.findViewById(R.id.delete_category);

        txt_categoryName.setText(category.getCat_name());
        if (!isEmpty(category)) {
            txt_ordercount.setVisibility(View.VISIBLE);
            txt_ordercount.setText(String.valueOf(category.getOrderCount()));
        }


        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAlert(category, isEmpty(category));

            }
        });



        typeface = Typeface.createFromAsset(context.getAssets(),"fonts/Oswald-Regular.ttf");
        txt_categoryName.setTypeface(typeface);

        imageView.setImageResource(category.getImg_id());

        return rowView;
        }

    public void showAlert(final Category category, boolean isEmpty){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle(category.getCat_name());

        if (!isEmpty)
            alertDialog.setMessage("Alışverişi tamamlamadınız kategoriyi silmek istiyor musunuz?");
        else
            alertDialog.setMessage("Kategoriyi silmek istiyor musunuz?");

        alertDialog.setPositiveButton("SİL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    categories.open();
                    categories.updateCategory(category.getCategoryId(), 0);
                    MainActivity.categoryList.add(category.getCat_name());
                    categories.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                values.remove(category);
                MainActivity.arrayAdapter.notifyDataSetChanged();

            }
        });

        alertDialog.setNegativeButton("İPTAL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public boolean isEmpty(final Category category){
        return category.getOrderCount() == 0;
    }
}
