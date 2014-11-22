package com.revyuk.mystorekeeper;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.concurrent.ExecutionException;


public class SingInActivity extends Activity implements View.OnClickListener {
    protected String email, password;
    EditText emailF, passwordF, retypepasswordF;
    Button singin;
    FrameLayout fl;
    CheckBox checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_in);

        fl = (FrameLayout) findViewById(R.id.singin_frame);
        fl.setVisibility(View.GONE);
        checkbox = (CheckBox) findViewById(R.id.singincheckbox);
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fl.getVisibility() == View.GONE) {
                    fl.setVisibility(View.VISIBLE);
                    singin.setText(R.string.singup_button_text);
                } else {
                    fl.setVisibility(View.GONE);
                    singin.setText(R.string.singin_button_text);
                }
            }
        });

        emailF = (EditText) findViewById(R.id.email);
        passwordF = (EditText) findViewById(R.id.password);
        retypepasswordF = (EditText) findViewById(R.id.retype_password);
        singin = (Button) findViewById(R.id.singinbtn);
        singin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.singinbtn) {
            boolean result = false;
            String errorMsg = "";
            email = emailF.getText().toString();
            password = passwordF.getText().toString();
            if(!email.contains("@")) {
                MainActivity.showMessage(this, getString(R.string.error_email));
                return;
            }
            if(checkbox.isChecked()) {
                if(!passwordF.getText().toString().equals(retypepasswordF.getText().toString())) {
                    MainActivity.showMessage(this, getString(R.string.singin_error_retypepassword));
                    retypepasswordF.requestFocus();
                    return;
                }
            }
            WebTaskPost www = new WebTaskPost();
            String server = getIntent().getStringExtra("servername");
            String script = getIntent().getStringExtra("loginscript");
            String urldata;
            if(checkbox.isChecked()) {
                urldata = "umail="+email+"&upwd="+password+"&act=new";
            } else {
                urldata = "umail="+email+"&upwd="+password;
            }
            JSONObject jsonObject;

            try {
                www.execute(server, script, urldata);
                jsonObject = (JSONObject) new JSONTokener(www.get()).nextValue();
                if(jsonObject.getString("Error").compareTo("0")==0) {
                    Intent intent = new Intent();
                    intent.putExtra("uid", jsonObject.getJSONArray("uid").get(0).toString());
                    intent.putExtra("umail", jsonObject.getJSONArray("umail").get(0).toString());
                    setResult(RESULT_OK, intent);
                    result = true;
                } else {
                    errorMsg = jsonObject.getString("ErrorMessage");
                    passwordF.setText("");
                    passwordF.requestFocus();
                }
            } catch (JSONException e) {
                e.printStackTrace(); errorMsg = e.getMessage();
            } catch (InterruptedException e) {
                e.printStackTrace(); errorMsg = e.getMessage();
            } catch (ExecutionException e) {
                e.printStackTrace(); errorMsg = e.getMessage();
            }
            if(result) { finish(); } else {
                MainActivity.showMessage(this, errorMsg);
            }
        }
    }

}
