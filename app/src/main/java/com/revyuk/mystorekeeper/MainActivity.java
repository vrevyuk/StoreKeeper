package com.revyuk.mystorekeeper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;
import java.util.prefs.Preferences;

import static android.net.Uri.encode;


public class MainActivity extends Activity implements View.OnClickListener {

    final static int UNITS_ACTIVITY = 1;
    final static int LOGIN_ACTIVITY = 3;
    static String serverName, scanScript, writeScript, loginScript, reportScript;
    static String uid="0";
    SharedPreferences myPref;
    Button mainButton;
    EditText barcode;
    IntentIntegrator integrator;
    TextView singin, singup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myPref = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        integrator = new IntentIntegrator(this);

        singin = (Button) findViewById(R.id.singin);
        singin.setOnClickListener(this);
        singup = (TextView) findViewById(R.id.reports);
        singup.setOnClickListener(this);
        mainButton = (Button) findViewById(R.id.mainButton);
        mainButton.setOnClickListener(this);
        barcode = (EditText) findViewById(R.id.barcode);

    }

    @Override
    public void onResume() {
        super.onResume();
        serverName = myPref.getString("serverName","storekeeper.zz.vc");
        scanScript = myPref.getString("scanScript","scancode.php");
        writeScript = myPref.getString("writeScript","writeitem.php");
        loginScript = myPref.getString("loginScript","login.php");
        reportScript = myPref.getString("reportScript","report.php");
        //uid = "1";
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences.Editor edit = myPref.edit();
        edit.clear();
        edit.commit();
        edit.putString("serverName", serverName);
        edit.putString("scanScript", scanScript);
        edit.putString("writeScript", writeScript);
        edit.putString("loginScript", loginScript);
        edit.putString("reportScript", reportScript);
        edit.apply();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.mainButton) {
            if(barcode.getText().toString().length() >0) {
                doWork(barcode.getText().toString());
            } else {
                integrator.initiateScan();
            }
        }
        if(v.getId() == R.id.singin) {
            int i=0;
            try {
                i = Integer.valueOf(uid);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(i>0) {
                singin.setText("Sing in");
                uid = "0";
            } else {
                Intent login= new Intent(this, SingInActivity.class);
                login.putExtra("servername", serverName);
                login.putExtra("loginscript", loginScript);
                startActivityForResult(login, LOGIN_ACTIVITY);
            }
        }
        if(v.getId() == R.id.reports) {
            Intent intent = new Intent(this, ReportActivity.class);
            startActivity(intent);
        }
    }

    public void doWork(String str) {
        WebTaskPost webTask = new WebTaskPost();
        String errorMsg = "";
        String UrlParams = "barcode="+str;
        JSONObject jsonObject;
        try {
            webTask.execute(serverName, scanScript, UrlParams);
            jsonObject = (JSONObject) new JSONTokener(webTask.get()).nextValue();
            if(jsonObject.getString("Error").compareTo("0")==0) {
                Intent unitIntent = new Intent(this, UnitActivity.class);
                unitIntent.putExtra("returnCode", jsonObject.getString("Error"));
                unitIntent.putExtra("barcode", jsonObject.getJSONArray("barcode").get(0).toString());
                unitIntent.putExtra("itemname", jsonObject.getJSONArray("itemname").get(0).toString());
                unitIntent.putExtra("trademark", jsonObject.getJSONArray("trademark").get(0).toString());
                unitIntent.putExtra("count", jsonObject.getJSONArray("count").get(0).toString());
                unitIntent.putExtra("uid", uid);
                startActivityForResult(unitIntent, UNITS_ACTIVITY);
            } else if(jsonObject.getString("Error").compareTo("3")==0) {
                Intent unitIntent = new Intent(this, UnitActivity.class);
                unitIntent.putExtra("returnCode", jsonObject.getString("Error"));
                unitIntent.putExtra("barcode", str);
                unitIntent.putExtra("uid", uid);
                startActivityForResult(unitIntent, UNITS_ACTIVITY);
            } else {
                showMessage(this, jsonObject.getJSONArray("ErrorMessage").get(0).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace(); errorMsg = e.getMessage();
        } catch (ExecutionException e) {
            e.printStackTrace(); errorMsg = e.getMessage();
        } catch (InterruptedException e) {
            e.printStackTrace(); errorMsg = e.getMessage();
        }
        if(errorMsg.length() >0) {
            showMessage(this, errorMsg);
        }
        // 0000000000
    }

    public void doSave(int action, String barCode, String itemName, String tradeMark, int addCount, int delCount) {
        WebTaskPost webSave = new WebTaskPost();
        String errorMsg = "";
        String UrlParams = new StringBuilder().append("uid=").append(uid).append("&act=").append(action).append("&barcode=")
                .append(barCode).append("&itemname=").append(encode(itemName, "UTF-8")).append("&trademark=")
                .append(encode(tradeMark, "UTF-8")).append("&addcount=").append(addCount).append("&delcount=").append(delCount)
                .toString();
        JSONObject jsonObject;
        try {
            webSave.execute(serverName, writeScript, UrlParams);
            jsonObject = (JSONObject) new JSONTokener(webSave.get()).nextValue();
            if(jsonObject.getString("Error").compareTo("0")==0) {
                StringBuilder str = new StringBuilder((action==1?"You added:":"You deleted:"));
                str.append("\n"); str.append(barCode);
                str.append("\n"); str.append(itemName);
                str.append("\n"); str.append(tradeMark);
                str.append("\n"); str.append(String.valueOf(action==1?addCount:delCount)); str.append(" pcs");
                showMessage(this, str.toString());
                barcode.setText("");
            } else {
                showMessage(this, jsonObject.getJSONArray("ErrorMessage").get(0).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace(); errorMsg = e.getMessage();
        } catch (ExecutionException e) {
            e.printStackTrace(); errorMsg = e.getMessage();
        } catch (InterruptedException e) {
            e.printStackTrace(); errorMsg = e.getMessage();
        }
        if(errorMsg.length() >0) {
            showMessage(this, errorMsg);
        }

    }

    static void showMessage(Context context, String str) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Warning information.");
        progressDialog.setMessage(str);
        progressDialog.setButton(Dialog.BUTTON_POSITIVE, "Okay",  new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        progressDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == IntentIntegrator.REQUEST_CODE) {
                IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if (scanResult != null) {
                       doWork(scanResult.getContents());
                     }
                }
            if(requestCode == LOGIN_ACTIVITY) {
                uid = data.getStringExtra("uid");
                if(Integer.valueOf(uid) > 0) singin.setText("Logoff");
            }
            if(requestCode == UNITS_ACTIVITY) {
                if(uid.equals("0")) {
                    showMessage(this, getString(R.string.quest_string));
                    return;
                }
                doSave(data.getIntExtra("action", 0), data.getStringExtra("barcode"), data.getStringExtra("itemname"), data.getStringExtra("trademark")
                        ,data.getIntExtra("addcount",0), data.getIntExtra("delcount", 0));
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_exit) finish();
        if (id == R.id.action_report) {
        }
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
