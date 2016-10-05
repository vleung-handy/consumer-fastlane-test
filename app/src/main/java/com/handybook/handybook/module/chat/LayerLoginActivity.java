package com.handybook.handybook.module.chat;


import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.BaseApplication;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Based on this sample app: https://github.com/layerhq/Atlas-Android-Messenger
 * <p>
 * and this tutorial to get sample users
 * https://docs.layer.com/sdk/web-3.0.beta/tutorials#sample-users
 *
 * @Deprecated
 */
public class LayerLoginActivity extends LayerBaseActivity {

    private static final String TAG = LayerLoginActivity.class.getName();

    @Bind(R.id.layer_main_user_name)
    TextInputEditText mUserName;

    @Inject
    @Named("layerAppId")
    String mLayerAppId;

    @Inject
    LayerHelper mLayerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layer_login);
        ButterKnife.bind(this);

        ((BaseApplication) this.getApplication()).inject(this);

        mUserName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(final TextView textView, final int i, final KeyEvent keyEvent) {
                if (i == EditorInfo.IME_NULL) {
                    //the enter key
                    login();
                    return true;
                }
                return false;
            }
        });


        ButterKnife.bind(this);
    }

    @OnClick(R.id.layer_main_submit)
    public void login() {
//        if (TextUtils.isEmpty(mUserName.getText()) || mUserName.getText().toString().trim().length() == 0) {
//            mUserName.setError("Please enter a name to continue");
//        } else {
//
//            Log.d(TAG, "login: starting login");
//            final String name = mUserName.getText().toString().trim();
//            final ProgressDialog progressDialog = new ProgressDialog(this);
//            progressDialog.setMessage(getResources().getString(R.string.login_dialog_message));
//            progressDialog.show();
//            mLayerHelper.authenticate(new LayerAuthenticationProvider.Credentials(mLayerAppId, name),
//                                      new AuthenticationProvider.Callback() {
//                                          @Override
//                                          public void onSuccess(AuthenticationProvider provider, String userId) {
//                                              progressDialog.dismiss();
//                                              Log.d(TAG, "Successfully authenticated as `" + name + "` with userId `" + userId + "`");
//                                              Intent intent = new Intent(
//                                                      LayerLoginActivity.this,
//                                                      MessagesListActivity.class
//                                              );
//                                              intent.putExtra(
//                                                      PushNotificationReceiver.LAYER_CONVERSATION_KEY,
//                                                      Uri.parse(
//                                                              "layer:///conversations/636a2014-dbd2-4e4e-a430-4131b18d56a9")
//                                              );
//                                              intent.putExtra("user", name);
//                                              startActivity(intent);
//                                              finish();
//                                          }
//
//                                          @Override
//                                          public void onError(AuthenticationProvider provider, final String error) {
//                                              progressDialog.dismiss();
//                                              Log.e(TAG, "Failed to authenticate as `" + name + "`: " + error);
//                                              runOnUiThread(new Runnable() {
//                                                  @Override
//                                                  public void run() {
//                                                      Toast.makeText(LayerLoginActivity.this, error, Toast.LENGTH_LONG).show();
//                                                  }
//                                              });
//                                          }
//                                      });
//        }
    }
}
