package com.revyuk.mystorekeeper;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class ReportActivity extends Activity {

    Button reportButton;
    RadioButton radioButton1, radioButton2, radioButton3, radioButton4;
    RadioGroup radioGroup;
    Spinner spinner;
    int reportType = 1;
    Context context;
    ArrayList<MyTest> arrayList = new ArrayList<MyTest>();

    class MyTest {
        String one;
        String two;
        String three;

        public MyTest() {
            this("", "", "");
        }
        public MyTest(String one, String two, String three) {
            this.one = one;
            this.two = two;
            this.three = three;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        context = this;
        ListView listView = (ListView) findViewById(R.id.listViewSummary);

        reportButton = (Button) findViewById(R.id.reportButton);
        reportButton.setOnClickListener(onClickListener);
        radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
        radioButton1.setOnClickListener(onRadioClickListener);
        radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
        radioButton2.setOnClickListener(onRadioClickListener);
        radioButton3 = (RadioButton) findViewById(R.id.radioButton3);
        radioButton3.setOnClickListener(onRadioClickListener);
        radioButton4 = (RadioButton) findViewById(R.id.radioButton4);
        radioButton4.setOnClickListener(onRadioClickListener);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        spinner = (Spinner) findViewById(R.id.spinner);
        getItemList();
    }

    View.OnClickListener onRadioClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.radioButton1: {
                    reportType = 1;
                    getItemList();
                    break;
                }
                case R.id.radioButton2: {
                    reportType = 2;
                    getItemList();
                    break;
                }
                case R.id.radioButton3: {
                    reportType = 3;
                    getItemList();
                    break;
                }
                case R.id.radioButton4: {
                    reportType = 4;
                    getItemList();
                    break;
                }
            }
        }
    };

    public class CustomAdapter extends ArrayAdapter {
        Context context;
        int resource;
        ArrayList<MyTest> objects;

        public CustomAdapter(Context context, int resource, ArrayList<MyTest> objects) {
            super(context, resource, objects);
            this.context = context;
            this.resource = resource;
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            String s = objects.get(position).two+" "+objects.get(position).three;
            view.setText(s);
            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
            String s = objects.get(position).two+" "+objects.get(position).three;
            view.setText(s);
            return view;
        }


    }

    public void getItemList() {
        String errorMsg = "";
        WebTaskPost web = new WebTaskPost();
        String urlParams = "act=get&uid="+MainActivity.uid+"&reporttype="+reportType;
        web.execute(MainActivity.serverName, MainActivity.reportScript, urlParams);
        JSONObject jsonObject;
        try {
            jsonObject = (JSONObject) new JSONTokener(web.get()).nextValue();
            if(jsonObject.getString("Error").equals("0")) {
                // Create items of spinner
                arrayList.clear();
                for(int i=0; i<jsonObject.getJSONArray("one").length(); i++) {
                    arrayList.add(new MyTest(jsonObject.getJSONArray("one").get(i).toString(), jsonObject.getJSONArray("two").get(i).toString(), jsonObject.getJSONArray("three").get(i).toString()));
                }
                CustomAdapter customAdapter = new CustomAdapter(context, android.R.layout.simple_spinner_dropdown_item, arrayList);
                spinner.setAdapter(customAdapter);
            } else {
                errorMsg = jsonObject.getString("ErrorMessage");
            }
        } catch(JSONException e) {
            e.printStackTrace(); errorMsg = e.getMessage();
        } catch (ExecutionException e) {
            e.printStackTrace(); errorMsg = e.getMessage();
        } catch (InterruptedException e) {
            e.printStackTrace(); errorMsg = e.getMessage();
        }
        if(errorMsg.length()>0) MainActivity.showMessage(context, errorMsg);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.reportButton) {
                if(MainActivity.uid.equals("0")) {
                    MainActivity.showMessage(context, getString(R.string.quest_string));
                    return;
                }
                if(arrayList.size() > 0) {
                    //MainActivity.showMessage(context, arrayList.get(spinner.getSelectedItemPosition()).one);
                    // Write here ...
                    String errorMsg = "";
                    WebTaskPost web = new WebTaskPost();
                    String urlParams = "act=report&uid="+MainActivity.uid+"&reporttype="+reportType+"&one="+arrayList.get(spinner.getSelectedItemPosition()).one;
                    web.execute(MainActivity.serverName, MainActivity.reportScript, urlParams);
                    JSONObject jsonObject;
                    try {
                        jsonObject = (JSONObject) new JSONTokener(web.get()).nextValue();
                        if(jsonObject.getString("Error").equals("0")) {
                            // Create items of spinner
                            errorMsg = jsonObject.getString("ErrorMessage");
                        } else {
                            errorMsg = jsonObject.getString("ErrorMessage");
                        }
                    } catch(JSONException e) {
                        e.printStackTrace(); errorMsg = e.getMessage();
                    } catch (ExecutionException e) {
                        e.printStackTrace(); errorMsg = e.getMessage();
                    } catch (InterruptedException e) {
                        e.printStackTrace(); errorMsg = e.getMessage();
                    }
                    if(errorMsg.length()>0) MainActivity.showMessage(context, errorMsg);
                } else {
                    MainActivity.showMessage(context, "Nothing !");
                }
            }
        }
    };

}
