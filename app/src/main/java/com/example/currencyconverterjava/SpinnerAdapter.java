package com.example.currencyconverterjava;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

//кастомный адаптер, расширяющий ArrayAdapter
public class SpinnerAdapter extends ArrayAdapter<Currency> {

    LayoutInflater layoutInflater;

    public SpinnerAdapter(Context context, int resource, ArrayList<Currency> currencyList) {
        super(context, resource, currencyList);
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View rowView = layoutInflater.inflate(R.layout.custom_spinnner_adapter, null, true);
        Currency currency = getItem(position);

        TextView textViewName = rowView.findViewById(R.id.name);
        TextView textCharCode = rowView.findViewById(R.id.value);
        textViewName.setText(currency.getName());
        textCharCode.setText(currency.getCharCode());

        return rowView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.custom_spinnner_adapter, parent, false);

        Currency currency = getItem(position);
        TextView textViewName = convertView.findViewById(R.id.name);
        TextView textCharCode = convertView.findViewById(R.id.value);
        textViewName.setText(currency.getName());
        textCharCode.setText(currency.getCharCode());
        return convertView;
    }
}
