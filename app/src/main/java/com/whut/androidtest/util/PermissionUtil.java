package com.whut.androidtest.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.tbruyelle.rxpermissions.RxPermissions;
import com.whut.androidtest.activities.MainActivity;

import cn.pedant.SweetAlert.SweetAlertDialog;
import rx.functions.Action1;

public class PermissionUtil {
    public static void getPermission(Activity activity){
        RxPermissions.getInstance(activity)
                .request(Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.READ_CONTACTS)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if(aBoolean){
                            Log.d("PERMISSION","OK");
                        }
                        else{
                            Log.d("DENY","NMO");
                            new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("提示")
                                    .setContentText("无权限，应用将退出")
                                    .setConfirmText("好的")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismissWithAnimation();
                                            activity.finish();
                                        }
                                    })
                                    .show();

                        }
                    }
                });
    }


}
