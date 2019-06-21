package cn.bingoogolapple.qrcode.zxingdemo;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        BGAQRCodeUtil.setDebug(true);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.test_scan_qrcode:
                startActivity(new Intent(this, TestScanActivity.class));
                break;
            case R.id.test_generate_qrcode:
                startActivity(new Intent(this, TestGeneratectivity.class));
                break;
            case R.id.test_load_pic:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<Map<String, Object>> pics = loadSDpictures();
                        if (null == pics) {
                            return;
                        }
                        for (Map<String, Object> pic : pics) {
                            String path = (String) pic.get("path");
                            Bitmap bitmap = (Bitmap) pic.get("icon");
                            long startTime = System.currentTimeMillis();
                            //String result = QRCodeDecoder.syncDecodeQRCode(path);
                            String bit = QRCodeDecoder.syncDecodeQRCode(bitmap);
                            long endTime = System.currentTimeMillis();
                            long runTime = endTime - startTime;
                            Log.i("pic_time", path + "图片识别结果为：" + bit + ",方法使用时间 %d ms :" + runTime);
                        }
                    }
                }).start();
                break;
        }
    }

    private static final String PIC_FOLDER_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "mypic" + File.separator;

    public static List<Map<String, Object>> loadSDpictures() {
        List<Map<String, Object>> list = new ArrayList<>();
        File file = new File(PIC_FOLDER_DIR);
        if (!file.exists()) {
            return null;
        } else {
            //获取以png为后缀的图片
            File[] files = file.listFiles(new FilenameFilter() { // 找到匹配的图片
                public boolean accept(File dir, String filename) {
                    return filename.endsWith(".jpg");
                }
            });
            //将图片装载到List中，方便ListView进行显示
            for (int i = 0; i < files.length; i++) {
                Map<String, Object> map = new HashMap<>();
                String imagePath = PIC_FOLDER_DIR + files[i].getName();
                //Log.i(TAG, "读取到的图片：" + imagePath);
                map.put("icon", BitmapFactory.decodeFile(imagePath));
                map.put("path", imagePath);
                list.add(map);
            }
            return list;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestCodeQRCodePermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }

    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开相机和散光灯的权限", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        }
    }
}
