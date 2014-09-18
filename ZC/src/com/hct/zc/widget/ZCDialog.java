package com.hct.zc.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.hct.zc.R;


/**
 * @author lzlong@zwmob.com
 * 自定义dialog  可以控制dialog大小与动画
 */
public class ZCDialog extends Dialog {
    private int windowDialogWidth;
    private int windowDialogHeight;
    private boolean isAnimation = true;
    private int animationStyle = R.style.dialogAnimation;
    
    public ZCDialog(Context context,int windowDialogWidth,int windowDialogHeight) {
        this(context,R.style.myDialogTheme,windowDialogWidth,windowDialogHeight);
    }
    
    public ZCDialog(Context context,int theme,int windowDialogWidth,int windowDialogHeight) {
        super(context,theme);
        this.windowDialogWidth = windowDialogWidth;
        this.windowDialogHeight = windowDialogHeight;
    }

    public ZCDialog setAnimation(boolean isAnimation){
    	this.isAnimation = isAnimation;
    	return this;
    }
    
    public void setAnimationStyle(int animationStyle){
    	this.animationStyle = animationStyle;
    }
    
    public void createDialog(View view){
        setContentView(view);
        Window window = getWindow(); //得到对话框
        if(isAnimation) window.setWindowAnimations(animationStyle); 
        if(windowDialogWidth>0||windowDialogHeight>0){
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.width = windowDialogWidth;
//            params.height = windowDialogHeight;
            params.dimAmount=0f;
            getWindow().setAttributes(params);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);  
        }
    }
    
    public void createDialog(int layoutResId){
        setContentView(layoutResId);
        Window window = getWindow(); //得到对话框
        if(isAnimation) window.setWindowAnimations(animationStyle); 
        if(windowDialogWidth>0||windowDialogHeight>0){
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.width = windowDialogWidth;
//            params.height = windowDialogHeight;
            params.dimAmount=0f;
            getWindow().setAttributes(params);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);  
        }
    }
    
    public void showDialog(View view){
        setContentView(view);
        Window window = getWindow(); //得到对话框
        if(isAnimation) window.setWindowAnimations(animationStyle); 
        if(windowDialogWidth>0||windowDialogHeight>0){
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.width = windowDialogWidth; 
//            params.height = windowDialogHeight;
            params.dimAmount=0.5f;
            getWindow().setAttributes(params);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);  
        }
        show();
    }
}
