package thinhlp.easycall;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.util.Base64;
import io.flutter.embedding.android.FlutterActivity;
import java.security.MessageDigest;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;
import android.content.pm.Signature;
import com.zing.zalo.zalosdk.oauth.ZaloSDK;
import com.zing.zalo.zalosdk.oauth.ZaloSDKApplication;
import com.zing.zalo.zalosdk.oauth.ZaloOpenAPICallback;
import com.zing.zalo.zalosdk.oauth.OauthResponse;
import com.zing.zalo.zalosdk.oauth.LoginVia;
import com.zing.zalo.zalosdk.oauth.OAuthCompleteListener;

import org.json.JSONObject;


public class MainActivity extends FlutterActivity {

    private static final String CHANNEL = "com.example.zalogin/auth";
    private String codeVerifier;
    private String zaloHashKey = "Chưa khởi tạo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zaloHashKey = getHashKey();  // Gọi khi app khởi động
    }

    private String getHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
            }
        } catch (Exception e) {
            return "Lỗi: " + e.getMessage();
        }
        return "Không có";
    }

     @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
            .setMethodCallHandler((call, result) -> {
                if (call.method.equals("loginZalo")) {
                    codeVerifier = PKCEUtil.generateCodeVerifier();
                    String codeChallenge = PKCEUtil.generateCodeChallenge(codeVerifier);

                    ZaloSDK.Instance.authenticateZaloWithAuthenType(
                        this,
                        LoginVia.APP,
                        codeChallenge,
                        new OAuthCompleteListener() {
                     

                            @Override
                            public void onGetOAuthComplete(OauthResponse response) {
                              // Nếu nhận được oauthCode từ Zalo
                                if (response != null && response.getOauthCode() != null) {
                                    String oauthCode = response.getOauthCode();
                                    result.success(oauthCode);

                                    // Lấy access token từ oauthCode
                                    getAccessTokenByOAuthCode(oauthCode, codeVerifier);
                                } else {
                                    result.error("NO_CODE", "No OAuthCode received", null);
                                }
                            }
                        }
                    );
                } else if (call.method.equals("getHashKey")) {
                    result.success(zaloHashKey);
                } else {
                    result.notImplemented();
                }
            });
    }

    private void getAccessTokenByOAuthCode(String oauthCode, String codeVerifier) {
        Context context = this; // Hoặc context mà bạn đang sử dụng, thường là Activity hoặc ApplicationContext

        ZaloSDK.Instance.getAccessTokenByOAuthCode(context, oauthCode, codeVerifier, new ZaloOpenAPICallback() {
            @Override
            public void onResult(JSONObject data) {
                int err = data.optInt("error");
                if (err == 0) {
                    // Lấy access_token, refresh_token, và expires_in
                    String accessToken = data.optString("access_token");
                    String refreshToken = data.optString("refresh_token");
                    long expiresIn = Long.parseLong(data.optString("expires_in"));

                    // Lưu lại các token vào cache hoặc database
                    saveTokensToCache(accessToken, refreshToken, expiresIn);
                } else {
                    // Xử lý lỗi nếu có
                    Log.e("Zalo", "Error: " + data.optString("error_description"));
                }
            }
        });
    }

    private void saveTokensToCache(String accessToken, String refreshToken, long expiresIn) {
        // Lưu lại access_token và refresh_token vào nơi lưu trữ thích hợp (SharedPreferences, Database, v.v.)
        SharedPreferences prefs = getSharedPreferences("ZaloToken", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("access_token", accessToken);
        editor.putString("refresh_token", refreshToken);
        editor.putLong("expires_in", expiresIn);
        editor.apply();
    }

    private void getAccessTokenByRefreshToken(String refreshToken) {
        Context context = this; // Hoặc context mà bạn đang sử dụng

        ZaloSDK.Instance.getAccessTokenByRefreshToken(context, refreshToken, new ZaloOpenAPICallback() {
            @Override
            public void onResult(JSONObject data) {
                int err = data.optInt("error");
                if (err == 0) {
                    // Lấy lại access_token và refresh_token mới
                    String accessToken = data.optString("access_token");
                    String newRefreshToken = data.optString("refresh_token");
                    long expiresIn = Long.parseLong(data.optString("expires_in"));

                    // Lưu lại token mới vào cache hoặc database
                    saveTokensToCache(accessToken, newRefreshToken, expiresIn);
                } else {
                    // Xử lý lỗi nếu có
                    Log.e("Zalo", "Error: " + data.optString("error_description"));
                }
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ZaloSDK.Instance.onActivityResult(this, requestCode, resultCode, data);
    }

}
