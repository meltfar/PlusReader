package com.zhouplus.plusreader.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhouplus.plusreader.R;
import com.zhouplus.plusreader.domains.forJson.UpdateInfo;
import com.zhouplus.plusreader.fragments.ShelfFragment;
import com.zhouplus.plusreader.utils.PlusConstants;
import com.zhouplus.plusreader.utils.PreferenceManager;
import com.zhouplus.plusreader.utils.ViewTools;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class SplashActivity extends AppCompatActivity {

    private Handler handler;
    private final int MSG_NET_ERROR = 2;
    private final int MSG_ENTER_MAIN = 1;
    private final int MSG_ENTER_GUIDE = 0;
    // 网络活动是否已经完成
    private boolean bNetComplete = false;

    //TODO 应该有个GuideActivity

    private final String NEW_VCODE = "updateVersionCode";
    private final String NEW_VNAME = "updateVersionName";

    private int currentVersionCode;
    private String currentVersionName;
    //当前更新是否以后还提示
    private final String IGNORE_UPDATE = "ignoreCurrentUpdate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 隐藏ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        //工作线程更新UI
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    //网络错误
                    case MSG_NET_ERROR:
                        Log.e("SplashActivity", "网络错误\n" + msg.obj);
                    case MSG_ENTER_MAIN:
                        bNetComplete = true;
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        //检查更新
        checkUpdate();
        //准备进入主界面
        enterMain();
    }


    /**
     * 检查更新
     */
    private void checkUpdate() {
        RequestParams rp = new RequestParams(PlusConstants.UPDATE_URL);
        x.http().get(rp, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                // 解析数据
                parseJson(s);
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                Message msg = handler.obtainMessage();
                msg.obj = throwable.toString();
                msg.what = MSG_NET_ERROR;
                handler.sendMessage(msg);
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 解析数据，进行升级
     */
    private void parseJson(String s) {
        Gson gson = new Gson();
        UpdateInfo updateInfo = gson.fromJson(s, UpdateInfo.class);

        refreshPreference(updateInfo);

        if (comparedUpdate(updateInfo)) {
            askForUpdate(updateInfo);
        } else {
            bNetComplete = true;
        }
    }

    /**
     * 对比是否应该更新
     *
     * @param updateInfo 解析信息
     * @return 是否应该更新
     */
    private boolean comparedUpdate(UpdateInfo updateInfo) {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            this.currentVersionCode = info.versionCode;
            this.currentVersionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return currentVersionCode < updateInfo.versionCode;
    }

    /**
     * 更新配置文件中的信息
     *
     * @param updateInfo 解析的信息
     */
    private void refreshPreference(UpdateInfo updateInfo) {
        PreferenceManager.setPreferenceString(this, NEW_VCODE, "" + updateInfo.versionCode);
        PreferenceManager.setPreferenceString(this, NEW_VNAME, "" + updateInfo.versionName);
    }

    /**
     * 询问是否升级
     */
    private void askForUpdate(final UpdateInfo updateInfo) {
        //如果当前版本已经确定不再提示
        if (updateInfo.versionCode == Integer.parseInt(PreferenceManager.getPreferenceString(
                SplashActivity.this, NEW_VCODE, "" + currentVersionCode))) {
            if (PreferenceManager.getPreferenceBoolean(SplashActivity.this, IGNORE_UPDATE, false)) {
                bNetComplete = true;
                return;
            }
        }
        //如果需要提示，或者是更新的版本
        String d = String.copyValueOf(updateInfo.description.toCharArray());
        d = "\n版本号更新为：" +
                currentVersionCode + "->" + updateInfo.versionCode +
                "\n版本名更新为："
                + currentVersionName + "->" + updateInfo.versionName
                + "\n\n" + d;
        ViewTools.MessageBox(SplashActivity.this, d, "有更新~", null,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击确认后触发
                        Update(updateInfo.downloadUrl);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //点击取消后触发
                        ignoreUpdate();
                    }
                });
    }

    //取消并忽略更新
    private void ignoreUpdate() {
        PreferenceManager.setPreferenceBoolean(this, IGNORE_UPDATE, true);
        bNetComplete = true;
    }

    //更新
    private void Update(String url) {
        System.out.println("更新啦！！\n下载： " + url);
    }

    /**
     * 进入主界面
     */
    private void enterMain() {
        new Thread() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                // 如果网络活动没有完成，我们就等待
                while (!bNetComplete) {
                    SystemClock.sleep(200);
                }
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }.start();
    }
}
