package com.revyuk.mystorekeeper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class UnitDetailActivity extends Activity {
    SharedPreferences myPref;
    String serverName, unitDetailScript, uid, urlparams, barcode;
    ListView list;

    public class UserDetail {
        String id;
        String count;
        String addtime;

        public UserDetail(String id, String count, String addtime) {
            this.id = id;
            this.count = count;
            this.addtime = addtime;
        }
        public UserDetail() {
            this("", "", "");
        }

    }

    public class UserDetailAdapter extends ArrayAdapter<UserDetail> {
        public UserDetailAdapter(Context context, ArrayList<UserDetail> userDetailArrayList) {
            super(context, R.layout.activity_unit_detail, userDetailArrayList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            UserDetail userDetail = getItem(position);
            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.unitdetailrow, parent, false);
            }
            TextView layout_unitID = (TextView) convertView.findViewById(R.id.detailId);
            TextView layout_count = (TextView) convertView.findViewById(R.id.detailCount);
            TextView layout_addtime = (TextView) convertView.findViewById(R.id.detailAddtime);
            layout_unitID.setText(userDetail.id);
            layout_count.setText(userDetail.count);
            layout_addtime.setText(userDetail.addtime);
            return convertView;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_detail);
        ListView list1 = (ListView) findViewById(R.id.list1);
        myPref = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        serverName = myPref.getString("serverName", "storekeeper.zz.vc");
        unitDetailScript = myPref.getString("unitDetailScript", "unitdetail.php");
        uid = getIntent().getStringExtra("uid");
        barcode = getIntent().getStringExtra("barcode");
        WebTaskPost webTaskPost = new WebTaskPost();
        JSONObject jsonObject;
        String errorMsg = "";
        urlparams = "uid="+uid+"&barcode="+barcode;
        int x = 0;
        try {
            webTaskPost.execute(serverName, unitDetailScript, urlparams);
            jsonObject = (JSONObject) new JSONTokener(webTaskPost.get()).nextValue();
            if(jsonObject.getString("Error").compareTo("0")==0) {
                ArrayList<UserDetail> userDetail = new ArrayList<UserDetail>();
                userDetail.add(new UserDetail("ID", "Count", "Transaction date & time"));
                while(x < jsonObject.getJSONArray("id").length()) {
                    UserDetail ud = new UserDetail();
                    ud.id = jsonObject.getJSONArray("id").getString(x);
                    ud.count = jsonObject.getJSONArray("count").getString(x);
                    ud.addtime = jsonObject.getJSONArray("addtime").getString(x);
                    userDetail.add(ud);
                    x++;
                }
                UserDetailAdapter adapter = new UserDetailAdapter(this, userDetail);
                list1.setAdapter(adapter);
            } else {
                MainActivity.showMessage(this, jsonObject.getString("ErrorMessage").toString());
            }
        } catch (ExecutionException e) {
            e.printStackTrace(); errorMsg = e.getMessage();
        } catch (JSONException e) {
            e.printStackTrace(); errorMsg = e.getMessage();
        } catch (InterruptedException e) {
            e.printStackTrace(); errorMsg = e.getMessage();
        }
        if(errorMsg.length() > 0) { MainActivity.showMessage(this, errorMsg); }
    }


}
