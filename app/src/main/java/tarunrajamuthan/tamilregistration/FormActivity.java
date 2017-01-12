package tarunrajamuthan.tamilregistration;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FormActivity extends AppCompatActivity implements View.OnClickListener {
    private static final MediaType FORM_DATA_TYPE
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    //URL derived from form URL
    private static final String URL
            = "https://docs.google.com/forms/d/e/1FAIpQLScakXpQXP4HlGfvNYcIkRyQ7LWgYvkzgNe5vpCLx6mWhi7D_g/formResponse";
    //input element ids found from the live form page
    private static final String FIRST_KEY = "entry_40873075";
    private static final String LAST_KEY = "entry_726471811";
    private static final String PHONE_KEY = "entry_107279496";
    private static final String EMAIL_KEY = "entry_5181983";
    private static final String ADDRESS_KEY = "entry_1797725138";
    private static final String CITY_KEY = "entry_12825885";
    private static final String MEMBER_KEY = "entry_177154304";

    private Context context;
    private EditText firstEditText,
            lastEditText,
            phoneEditText,
            emailEditText,
            addressEditText,
            cityEditText;
    private ToggleButton memberButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        //save activity to context for later
        context = this;
        Button submit = (Button) findViewById(R.id.submitButton);
        firstEditText = (EditText) findViewById(R.id.first);
        lastEditText = (EditText) findViewById(R.id.last);
        phoneEditText = (EditText) findViewById(R.id.phone);
        emailEditText = (EditText) findViewById(R.id.email);
        addressEditText = (EditText) findViewById(R.id.address);
        cityEditText = (EditText) findViewById(R.id.city);
        memberButton = (ToggleButton) findViewById(R.id.membership);
        submit.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        /* opening a website in browser
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
            startActivity(browserIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request."
                    + " Please install a web browser",  Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }*/


        //Make sure all the fields are filled with values
        if (TextUtils.isEmpty(firstEditText.getText().toString()) ||
                TextUtils.isEmpty(lastEditText.getText().toString()) ||
                TextUtils.isEmpty(phoneEditText.getText().toString()) ||
                TextUtils.isEmpty(emailEditText.getText().toString()) ||
                TextUtils.isEmpty(addressEditText.getText().toString()) ||
                TextUtils.isEmpty(cityEditText.getText().toString())) {
            Toast.makeText(context, "All fields are mandatory.", Toast.LENGTH_LONG).show();
            return;
        }
        //Check if a valid email is entered
        if (!Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText().toString()).matches()) {
            Toast.makeText(context, "Please enter a valid email.", Toast.LENGTH_LONG).show();
            return;
        }
        if (!Patterns.PHONE.matcher(phoneEditText.getText().toString()).matches()) {
            Toast.makeText(context, "Please enter a valid phone number.", Toast.LENGTH_LONG).show();
            return;
        }

        //Create Membership String
        String membership;
        if (memberButton.isChecked()) membership = "Life";
        else membership = "Annual";

        //Create an object for PostDataTask AsyncTask
        PostDataTask postDataTask = new PostDataTask();

        //execute asynctask
        postDataTask.execute(
                URL,
                firstEditText.getText().toString(),
                lastEditText.getText().toString(),
                phoneEditText.getText().toString(),
                emailEditText.getText().toString(),
                addressEditText.getText().toString(),
                cityEditText.getText().toString(),
                membership);
    }

    //AsyncTask to send data as a http POST request
    private class PostDataTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... contactData) {
            Boolean result = true;
            String url = contactData[0];
            String first = contactData[1];
            String last = contactData[2];
            String phone = contactData[3];
            String email = contactData[4];
            String address = contactData[5];
            String city = contactData[6];
            String member = contactData[7];
            String postBody = "";

            try {
                //all values must be URL encoded to make sure that special characters like & | ",etc.
                //do not cause problems
                postBody = FIRST_KEY + "=" + URLEncoder.encode(first, "UTF-8") +
                        "&" + LAST_KEY + "=" + URLEncoder.encode(last, "UTF-8") +
                        "&" + PHONE_KEY + "=" + URLEncoder.encode(phone, "UTF-8") +
                        "&" + EMAIL_KEY + "=" + URLEncoder.encode(email, "UTF-8") +
                        "&" + ADDRESS_KEY + "=" + URLEncoder.encode(address, "UTF-8") +
                        "&" + CITY_KEY + "=" + URLEncoder.encode(city, "UTF-8") +
                        "&" + MEMBER_KEY + "=" + URLEncoder.encode(member, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                result = false;
            }

            try {
                //Create OkHttpClient for sending request
                OkHttpClient client = new OkHttpClient();
                //Create the request body with the help of Media Type
                RequestBody body = RequestBody.create(FORM_DATA_TYPE, postBody);
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                //Send the request
                Response response = client.newCall(request).execute();
            } catch (IOException exception) {
                result = false;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            //Print Success or failure message accordingly
            Toast.makeText(context, result ? "Registration sent!" : "There was some error in sending message. Please try again after some time.", Toast.LENGTH_LONG).show();
            if (result){
                firstEditText.setText("");
                lastEditText.setText("");
                phoneEditText.setText("");
                emailEditText.setText("");
                addressEditText.setText("");
                cityEditText.setText("");
            }
        }
    }
}