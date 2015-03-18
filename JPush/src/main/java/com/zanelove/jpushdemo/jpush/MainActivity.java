package com.zanelove.jpushdemo.jpush;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import java.util.Set;


public class MainActivity extends Activity {
    private static final int  GET_MSG_SUC = 0;
    private String TAG = MainActivity.class.getName();
    private static final int MSG_SET_ALIAS = 1001;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_MSG_SUC:
                /**
                 *这里设置了别名，是因为了我开发的app中，是在这里获取的用户登录的信息
                 *并且此时已经获取了用户的userId,然后就可以用用户的userId来设置别名了
                 */
                    Log.e(TAG, "触发广播，接收到广播信息，说明必备数据获取成功");
                    setAlias(19940316);//设置极光推送的别名
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        //判断当前手机网络状态的工具类，没什么特殊的，网上很多，就不详细写了

        if(!(0==0)){
            //友好提示
        }else {
            // JPush初始化
            JPushInterface.setDebugMode(false); // 设置true 开启日志，发布时需关闭日志false即可
            JPushInterface.init(this); // 初始化 Jpush 固定的方法，JPushInterface类中的静态方法
            Message message = Message.obtain();
            message.what = 0;
            handler.sendMessage(message);
        }
    }

    @Override
    protected void onPause() {
        JPushInterface.onPause(MainActivity.this);
        super.onPause();
    }

    @Override
    protected void onResume() {
//      极光推送服务会恢复正常工作
        JPushInterface.onResume(MainActivity.this);
        super.onResume();
    }

    /**
     * Jpush设置
     */
    private void setAlias(int userId) {
        String alias = String.valueOf(userId);
        // 调用JPush API设置Alias
        mHandler2.sendMessage(mHandler2.obtainMessage(MSG_SET_ALIAS, alias));
        Log.d(TAG, "设置Jpush推送的别名alias=" + alias);
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler2 = new Handler() {//专门用了一个Handler对象处理别名的注册问题
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "设置激光推送的别名-mHandler2");
            JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, mAliasCallback);
            Toast.makeText(MainActivity.this, "成功", Toast.LENGTH_SHORT).show();
        }
    };

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success极光推送别名设置成功";
                    Log.e(TAG, logs);
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.极光推送别名设置失败，60秒后重试";
                    Log.e(TAG, logs);
                    mHandler2.sendMessageDelayed(mHandler2.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    break;
                default:
                    logs = "极光推送设置失败，Failed with errorCode = " + code;
                    Log.e(TAG, logs);
                    break;
            }
        }
    };
}
