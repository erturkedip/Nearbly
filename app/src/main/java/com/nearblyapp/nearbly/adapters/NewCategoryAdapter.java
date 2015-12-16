package com.nearblyapp.nearbly.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nearblyapp.nearbly.MainActivity;
import com.nearblyapp.nearbly.R;
import com.nearblyapp.nearbly.databases.NearblyDB;
import com.nearblyapp.nearbly.grades.Category;

import java.sql.SQLException;
import java.util.ArrayList;

public class NewCategoryAdapter extends ArrayAdapter<Category> {

    private final Context context;
    private final ArrayList<Category> values;
    Typeface typeface;
    NearblyDB categories;

    public NewCategoryAdapter(Context context, ArrayList<Category> values) {
        super(context, R.layout.list_category, values);
        this.context = context;
        this.values = values;
        categories = new NearblyDB(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final Category category = values.get(position);


        View rowView = inflater.inflate(R.layout.list_all_category, parent, false);

        TextView txt_categoryName = (TextView)rowView.findViewById(R.id.newCategoryName);
        ImageView imageView = (ImageView)rowView.findViewById(R.id.new_category_logo);
        CheckBox chb_category = (CheckBox) rowView.findViewById(R.id.categoryCheck);

        txt_categoryName.setText(category.getCat_name());

        try {
            categories.open();
            if (categories.isCategoryValid(category.getCat_name()))
                chb_category.setChecked(true);
            categories.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        chb_category.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    try {
                        categories.open();
                        categories.insertData(category.getCat_name(), category.getImg_id(), category.getType());
                        MainActivity.categoryList.add(category.getCat_name());
                        categories.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }else {
                    try {
                        categories.open();
                        categories.deleteNewCategory(categories.getCategoryId(category.getCat_name()));
                        MainActivity.categoryList.remove(category.getCat_name());
                        categories.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }

            }
        });

        typeface = Typeface.createFromAsset(context.getAssets(),"fonts/Oswald-Regular.ttf");
        txt_categoryName.setTypeface(typeface);

        imageView.setImageResource(category.getImg_id());

        return rowView;
    }
}