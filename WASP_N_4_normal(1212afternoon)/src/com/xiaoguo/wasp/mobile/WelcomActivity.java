package com.xiaoguo.wasp.mobile;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;

/*
 * 欢迎界面
 * */
public class WelcomActivity extends Activity implements AnimationListener {

	private Animation alphaAnimation = null;
	FrameLayout txtTextView;
	UserSettingInfo userSettingInfo = null;
	private Animation alphaAnimation2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcom);

		userSettingInfo = new UserSettingInfo(this);

		setupView();
	}

	public void setupView() {
		txtTextView = (FrameLayout) this.findViewById(R.id.layout_welcom);

		alphaAnimation = AnimationUtils.loadAnimation(this,
				R.anim.welcome_alpha);

		alphaAnimation2 = AnimationUtils.loadAnimation(WelcomActivity.this,
				R.anim.welcome_alpha2);

		alphaAnimation2.setFillEnabled(true);
		alphaAnimation2.setFillAfter(true);
		// txtTextView.setAnimation(alphaAnimation);
		// txtTextView.startAnimation(alphaAnimation2);
		txtTextView.setAnimation(alphaAnimation2);
		alphaAnimation2.setAnimationListener(this);
	}

	@Override
	public void onAnimationEnd(Animation arg0) {
		// Intent intent = new Intent(this, BeforeLoginActivity.class);
		Intent intent = new Intent();
		/*
		 * if(userSettingInfo.getAutoLogin() && userSettingInfo.getSavePswd()){
		 * intent.setClass(this, WeatherInfoActivity.class); }else{
		 */
		intent.setClass(this, LoginActivity.class);
		// }
		startActivity(intent);
		this.finish();
	}

	@Override
	public void onAnimationRepeat(Animation arg0) {

	}

	@Override
	public void onAnimationStart(Animation arg0) {

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 在欢迎界面屏蔽BACK键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return false;
	}

}
