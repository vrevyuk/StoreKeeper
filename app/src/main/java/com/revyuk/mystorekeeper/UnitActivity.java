package com.revyuk.mystorekeeper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class UnitActivity extends Activity implements View.OnClickListener {

    EditText barcode, itemname, trademark, count, expenseCount;
    TextView countLabel;
    Button saveButton, plus, plus10, minus, minus10, expensePlus, expensePlus10,
            expenseMinus, expenseMinus10, expenseDeleteButton, logButton;
    String uid = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit);

        barcode = (EditText) findViewById(R.id.barcode);
        itemname = (EditText) findViewById(R.id.itemname);
        trademark = (EditText) findViewById(R.id.trademark);
        count = (EditText) findViewById(R.id.count);
        countLabel = (TextView) findViewById(R.id.countlabel);
        logButton = (Button) findViewById(R.id.logButton);
        logButton.setOnClickListener(this);
        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);
        plus = (Button) findViewById(R.id.plus);
        plus.setOnClickListener(this);
        plus10 = (Button) findViewById(R.id.plus10);
        plus10.setOnClickListener(this);
        minus = (Button) findViewById(R.id.minus);
        minus.setOnClickListener(this);
        minus10 = (Button) findViewById(R.id.minus10);
        minus10.setOnClickListener(this);

        expensePlus = (Button) findViewById(R.id.expensePlus);
        expensePlus10 = (Button) findViewById(R.id.expensePlus10);
        expenseMinus = (Button) findViewById(R.id.expenseMinus);
        expenseMinus10 = (Button) findViewById(R.id.expenseMinus10);
        expensePlus.setOnClickListener(this);
        expensePlus10.setOnClickListener(this);
        expenseMinus.setOnClickListener(this);
        expenseMinus10.setOnClickListener(this);
        expenseCount = (EditText) findViewById(R.id.expenseCount);
        expenseDeleteButton = (Button) findViewById(R.id.expenseDeleteButton);
        expenseDeleteButton.setOnClickListener(this);

        barcode.setText(getIntent().getStringExtra("barcode"));
        itemname.setText(getIntent().getStringExtra("itemname"));
        trademark.setText(getIntent().getStringExtra("trademark"));
        countLabel.setText("Preset "+getIntent().getStringExtra("count")+" units.");

        uid = getIntent().getStringExtra("uid");
        if(getIntent().getStringExtra("returnCode").equals("3")) {
            barcode.setEnabled(false);
            itemname.setEnabled(true);
            itemname.setHint("Enter product name");
            itemname.requestFocus();
            trademark.setHint("Enter trademark of manufacturer product");
            trademark.setEnabled(true);
        } else {
            barcode.setEnabled(false);
            itemname.setEnabled(false);
            trademark.setEnabled(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getIntent().getStringExtra("returnCode").compareTo("3")==0) {
            MainActivity.showMessage(this, getString(R.string.item_not_found));
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.plus) count.setText(String.valueOf(Integer.valueOf(count.getText().toString()) + 1));
        if(v.getId() == R.id.plus10) count.setText(String.valueOf(Integer.valueOf(count.getText().toString()) + 10));
        if(v.getId() == R.id.minus) {
            int i=Integer.valueOf(count.getText().toString()) - 1;
            count.setText(String.valueOf(i<0?0:i));
        }
        if(v.getId() == R.id.minus10) {
            int i=Integer.valueOf(count.getText().toString()) - 10;
            count.setText(String.valueOf(i<0?0:i));
        }
        if(v.getId() == R.id.expensePlus) expenseCount.setText(String.valueOf(Integer.valueOf(expenseCount.getText().toString()) + 1));
        if(v.getId() == R.id.expensePlus10) expenseCount.setText(String.valueOf(Integer.valueOf(expenseCount.getText().toString()) + 10));
        if(v.getId() == R.id.expenseMinus) {
            int i=Integer.valueOf(expenseCount.getText().toString()) - 1;
            expenseCount.setText(String.valueOf(i<0?0:i));
        }
        if(v.getId() == R.id.expenseMinus10) {
            int i=Integer.valueOf(expenseCount.getText().toString()) - 10;
            expenseCount.setText(String.valueOf(i<0?0:i));
        }
        if(v.getId() == R.id.saveButton || v.getId() == R.id.expenseDeleteButton) {
            Intent intent = new Intent();
            intent.putExtra("barcode", barcode.getText().toString());
            intent.putExtra("itemname", itemname.getText().toString());
            intent.putExtra("trademark", trademark.getText().toString());
            intent.putExtra("addcount", Integer.valueOf(count.getText().toString()));
            intent.putExtra("delcount", Integer.valueOf(expenseCount.getText().toString()));
            if(v.getId() == R.id.saveButton) {
                intent.putExtra("action", 1); } else if(v.getId() == R.id.expenseDeleteButton) { intent.putExtra("action", 2); };
            setResult(RESULT_OK, intent);
            finish();
        }
        if(v.getId()==R.id.logButton) {
            if(uid.equals("0")) {
                MainActivity.showMessage(this, getString(R.string.quest_string));
                return;
            }
            Intent intent = new Intent(this, UnitDetailActivity.class);
            intent.putExtra("barcode", barcode.getText().toString());
            intent.putExtra("uid", uid);
            startActivity(intent);
        }
    }


}
