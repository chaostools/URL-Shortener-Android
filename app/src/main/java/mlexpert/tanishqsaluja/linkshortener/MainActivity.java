package mlexpert.tanishqsaluja.linkshortener;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText longURL;
    private TextView smallURL;
    private Button shorten, copy;
    private ProgressDialog progressDialog;
    private String REQUEST_URL = "https://g-z.herokuapp.com/";
    private String POST_AT = "shorten";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        longURL = findViewById(R.id.txt);
        smallURL = findViewById(R.id.shorturl);
        shorten = findViewById(R.id.bttn);
        copy = findViewById(R.id.copyClipboard);
        Typeface tf = Typeface.createFromAsset(getAssets(), "qb.otf");

        smallURL.setTypeface(tf);
        shorten.setTypeface(tf); // will have to do this once again after the link is fetched

        shorten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (longURL.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter a valid URL.", Toast.LENGTH_SHORT).show();
                } else {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    RequestBody formBody = new FormBody.Builder()
                            .add("url", longURL.getText().toString())
                            .build();
                    Request request = new Request.Builder()
                            .url(REQUEST_URL + POST_AT)
                            .post(formBody)
                            .build();
                    //initialize the progress dialog and show it
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Shortening the link");
                    progressDialog.show();

                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try {
                                String JSONResponse = response.body().string();
                                progressDialog.dismiss();
                                JSONObject jsonObject = new JSONObject(JSONResponse);
                                final String hash = jsonObject.getString("hash");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        smallURL.setText(REQUEST_URL + hash);
                                    }
                                });
                                Log.e("TAG", JSONResponse);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
        copy.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                // Copy to clipboard
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Text", smallURL.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
        smallURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Copy to clipboard
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Text", smallURL.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
