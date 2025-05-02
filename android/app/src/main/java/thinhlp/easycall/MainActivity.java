package thinhlp.easycall; // Đổi theo package app của bạn

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;

import io.flutter.embedding.android.FlutterActivity;

import androidx.annotation.NonNull;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;
import android.content.pm.Signature;


public class MainActivity extends FlutterActivity {
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
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), "zalo_hash_key")
            .setMethodCallHandler((call, result) -> {
                if (call.method.equals("getHashKey")) {
                    result.success(zaloHashKey);
                } else {
                    result.notImplemented();
                }
            });
    }
}
