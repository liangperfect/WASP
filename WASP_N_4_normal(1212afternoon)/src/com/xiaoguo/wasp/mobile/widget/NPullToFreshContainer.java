package com.xiaoguo.wasp.mobile.widget;


import com.xiaoguo.wasp.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;
/*
 * 下拉刷新布局设置
 * */

public class NPullToFreshContainer extends FrameLayout {
	private int HEAD_VIEW_HEIGHT = 0;
	private final int STATE_RESET = 0;
	private final int STATE_PULL_TO_REFRESH = 1;
	private final int STATE_RELEASE_TO_REFRESH = 2;
	private final int STATE_REFRESHING = 3;
	private final int ACTION_MASK = 0xff;// 2.1以后才支持
	private int mState = STATE_RESET;
	private ImageView mRefreshViewImage;
	private TextView mText;
	private TextView mDateTv;
	private RotateAnimation mAnimationDown;
	private RotateAnimation mAnimationUp;
	private View mRefreshView;
	private float mLastMotionY;
	private int mTatolScroll;
	private ProgressBar mProgressBar;
	private OnContainerRefreshListener mOnRefreshListener;
	private Flinger mFling;
	private String mDate;
	private boolean mFirstLayout = true;
	long mlastUpdate = 0;
	boolean mbDeal = false;

	private int mTouchSlop = 0;
	
	public NPullToFreshContainer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public NPullToFreshContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public NPullToFreshContainer(Context context) {
		super(context);
		init(context);
	}
	
	private void init(Context context) {
		mRefreshView = LayoutInflater.from(getContext()).inflate(R.layout.pull_to_refresh_header, null);

		mRefreshView.setBackgroundColor(0xf7f7f8);
		mRefreshViewImage = (ImageView)mRefreshView.findViewById(R.id.pull_to_refresh_image);
		mRefreshViewImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
		float density = context.getResources().getDisplayMetrics().density;
		mRefreshViewImage.setMinimumHeight((int)(50 * density));
		
		mText = (TextView)mRefreshView.findViewById(R.id.pull_to_refresh_text);
		mDateTv = (TextView)mRefreshView.findViewById(R.id.pull_to_refresh_updated_at);
		mDateTv.setVisibility(View.INVISIBLE);
		mProgressBar = (ProgressBar)mRefreshView.findViewById(R.id.pull_to_refresh_progress);
				
		mAnimationUp = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f,RotateAnimation.RELATIVE_TO_SELF, 0.5f);        
		mAnimationUp.setInterpolator(new LinearInterpolator());        
		mAnimationUp.setDuration(100);        
		mAnimationUp.setFillAfter(true);
		
		mAnimationDown = new RotateAnimation(-180, 0,                
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,                
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);        
		mAnimationDown.setInterpolator(new LinearInterpolator());        
		mAnimationDown.setDuration(100);        
		mAnimationDown.setFillAfter(true);
	   
	    //add pullToRefreshView
        if (mFirstLayout) {    
        	measureView(mRefreshView);
        	HEAD_VIEW_HEIGHT = mRefreshView.getMeasuredHeight();
        	mFirstLayout = false;
		}
        
		addView(mRefreshView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		mFling = new Flinger();
		//获取当前控件的最小滑动误判距离
		final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
	}


	
	@Override
    public void addView(View child) {
        if (getChildCount() > 2) {
            throw new IllegalStateException("NPullToFreshContainer can host only two direct child");
        }

        super.addView(child);
    }
	
    @Override
    public void addView(View child, int index) {
        if (getChildCount() > 2) {
            throw new IllegalStateException("NPullToFreshContainer can host only two direct child");
        }

        super.addView(child, index);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (getChildCount() > 2) {
            throw new IllegalStateException("NPullToFreshContainer can host only two direct child");
        }

        super.addView(child, params);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() > 2) {
            throw new IllegalStateException("NPullToFreshContainer can host only two direct child");
        }

        super.addView(child, index, params);
    }
    
    
    private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}
    
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {		
        if (getChildCount() > 2) {
            throw new IllegalStateException("NPullToFreshContainer can host only two direct child");
        }
        
		View headView = getChildAt(0);
		View contentView = getChildAt(1);
		if(headView != null){
			headView.layout(0, -HEAD_VIEW_HEIGHT + mTatolScroll, getMeasuredWidth(), mTatolScroll);
		}
		
		if(contentView != null){
			//原本高度采用getMeasuredHeight，结果发现会有闪跳效果，为此将其扩展为getMeasuredHeight()+ mTatolScroll
			contentView.layout(0, mTatolScroll, getMeasuredWidth(), getMeasuredHeight()+ mTatolScroll);
		}
        
	}
	
	public void onInvalidate()
	{		
		View headView = getChildAt(0);
		View contentView = getChildAt(1);
		if(headView == null)
		{
			throw new IllegalStateException("NPullToFreshContainer can host only two direct child");
		}
		
		
		switch(mState)
		{
		case STATE_PULL_TO_REFRESH:
		case STATE_RELEASE_TO_REFRESH:
			headView.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.GONE);

			if (mState == STATE_PULL_TO_REFRESH) {
				mText.setText(R.string.pull_to_fresh_text);
			} else {
				mText.setText(R.string.release_to_fresh_text);
			}
			if (mDate != null) {
				mDateTv.setText(mDate);
				mDateTv.setVisibility(View.VISIBLE);
			}else{
				mDateTv.setVisibility(View.GONE);
			}
			
			break;
		case STATE_REFRESHING:
			// 可以在action_up 的时候判断处于那种状态
			headView.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.VISIBLE);
			mRefreshViewImage.clearAnimation();
			mRefreshViewImage.setVisibility(View.GONE);
			mText.setText(R.string.list_refreshing_text);
			if (mDate != null) {
				mDateTv.setText(mDate);
				mDateTv.setVisibility(View.VISIBLE);
			}else{
				mDateTv.setVisibility(View.GONE);
			}
			break;
		case STATE_RESET:
			if(mTatolScroll==0 && headView.getVisibility() == View.VISIBLE){
				headView.setVisibility(View.INVISIBLE);
			}
			break;
		}

		if(headView.getVisibility() == View.VISIBLE){
			//refresh视图可见时移动
			headView.offsetTopAndBottom(mTatolScroll-headView.getTop()-HEAD_VIEW_HEIGHT);
			if(contentView != null){
				contentView.offsetTopAndBottom(mTatolScroll -contentView.getTop());
			}
		} else {
			//不可见时重新布局，此处不调用会导致留空白的情况出现
			if(contentView != null)	{
				contentView.requestLayout();
			}
		}
		
		invalidate();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		final int action = ev.getAction() & ACTION_MASK;
		final View contentView = (View) getChildAt(1);
		final float y = ev.getY();
		float mScroll = 0;
		float mMoveCurrent = 0;
		if(contentView ==null){
			return false;
		}
		switch(action){
		case MotionEvent.ACTION_DOWN:			
			mbDeal = true;
			return true;
//			break;
		case MotionEvent.ACTION_MOVE:
			mScroll = contentView.getTop();
			mMoveCurrent = y - mLastMotionY;
			mScroll += (mMoveCurrent / 1.7f);
			//当前是处理状态下，所以可以直接赋值 区别于onInterceptTouchEvent中的后期赋值
			mTatolScroll = (int)mScroll;
			if(mState != STATE_REFRESHING){
				if(mTatolScroll > 0 && mTatolScroll < HEAD_VIEW_HEIGHT){					
					mProgressBar.setVisibility(View.GONE);
					mRefreshViewImage.setVisibility(View.VISIBLE);
					if(mState != STATE_PULL_TO_REFRESH){
						mRefreshViewImage.clearAnimation();
						mRefreshViewImage.startAnimation(mAnimationDown);
					}
					mState = STATE_PULL_TO_REFRESH;					
				}else if(mTatolScroll > HEAD_VIEW_HEIGHT){					
					mProgressBar.setVisibility(View.GONE);
					mRefreshViewImage.setVisibility(View.VISIBLE);
					if(mState != STATE_RELEASE_TO_REFRESH){
						mRefreshViewImage.clearAnimation();
						mRefreshViewImage.startAnimation(mAnimationUp);
					}
					mState = STATE_RELEASE_TO_REFRESH;
				}else{
					//为了防止下拉过程中上拖导致浮动tab不显示原有tab消失而增加赋值
					mTatolScroll = 0;
					mState = STATE_RESET;
				}				
			}else{
			}
			onInvalidate();	
			break;
		case MotionEvent.ACTION_UP:
			if(contentView.getTop() > 0 && mState == STATE_RELEASE_TO_REFRESH){
				mState = STATE_REFRESHING;
				scrollToUpdate();
				onRefresh();
			}else if(mState == STATE_REFRESHING){
				if(mTatolScroll > HEAD_VIEW_HEIGHT){
					scrollToUpdate();
				}else{
					scrollToClose();
				}				
			}else{
				scrollToClose();
				mState = STATE_RESET;
			}			
			mbDeal = false;
			break;
		case MotionEvent.ACTION_CANCEL:
			if(mState == STATE_REFRESHING){
				scrollToUpdate();
			}else{
				scrollToClose();
				mState = STATE_RESET;					
			}
			mbDeal = false;	
			break;
		}
		
		mLastMotionY = y;
		return true;
	}
	
	private boolean isTouchView(View view){
		boolean FirstLayout = true;
		if(view instanceof ViewGroup){
			int count = ((ViewGroup) view).getChildCount();
			for(int i = 0; i < count; i++){
				View aView = ((ViewGroup) view).getChildAt(i);
				int viewScrollY =  aView.getScrollY();			
				if(viewScrollY != 0){
					return false;
				}

				if(aView instanceof AdapterView)
				{
					@SuppressWarnings("rawtypes")
					AdapterView adapterView = (AdapterView)aView;
					final int position = adapterView.getFirstVisiblePosition();
					if(position != 0){
						return false;
					}

					if (adapterView.getChildCount() > 0) {
						int vTop = adapterView.getChildAt(0).getTop();
						if(vTop != 0){
							return false;
						}
					}
				}
				
				if(isTouchView(aView)){
					continue;
				}else{
					return false;
				}
			}
		}else{
			int viewScrollY =  view.getScrollY();			
			if(viewScrollY != 0){
				return false;
			}else{
				return true;
			}
		}
		return FirstLayout;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		//当前没做拦截处理的情况下
		if(!mbDeal){
			final View contentView = (View) getChildAt(1);

			if(contentView ==null){				
				return false;
			}
			
			final float y = ev.getY();
			final int action = ev.getAction() & ACTION_MASK;// & MotionEvent.ACTION_MASK;
			float mScroll = 0;
			float mMoveCurrent = 0;
			//ACTION_MOVE操作下针对当前界面状态进行是否第一次拦截的判断			
			if(action == MotionEvent.ACTION_MOVE){
				//一移动就截获事件, 会导致无法响应点击, 因此去除将轻微的抖动判为点击的误操作
				int diff =  (int) Math.abs(y - mLastMotionY);
				if(diff <  mTouchSlop)
					return false;
				
				mScroll = contentView.getTop();
				mMoveCurrent = y - mLastMotionY;
				if(mState == STATE_REFRESHING){
//					return false;					
					mScroll += (mMoveCurrent / 1.7f);
					mTatolScroll = (int)mScroll;					
					onInvalidate();
					return true;
				}

				if(mState == STATE_RESET){
					int viewScrollY =  contentView.getScrollY();			
					if(viewScrollY != 0){
						return false;
					}

					if(contentView instanceof AdapterView){
						@SuppressWarnings("rawtypes")
						AdapterView adapterView = (AdapterView)contentView;
						final int position = adapterView.getFirstVisiblePosition();
						if(position != 0){
							return false;
						}

						if (adapterView.getChildCount() > 0) {
							int vTop = adapterView.getChildAt(0).getTop();
							if(vTop != 0){
								return false;
							}
						}
					}
					//判断子控件内是否存在滚动或adapterView
					if(!isTouchView(contentView))
						return false;
				}

				mScroll += (mMoveCurrent / 1.7f);
				//由于当前并非一定是处理状态，所以此处直接赋值为引发内容视图mtop的位置错乱
				if(mScroll > 0 && mScroll < HEAD_VIEW_HEIGHT)
				{					
					mProgressBar.setVisibility(View.GONE);
					mRefreshViewImage.setVisibility(View.VISIBLE);
					if(mState != STATE_PULL_TO_REFRESH)	{
						mRefreshViewImage.clearAnimation();
						mRefreshViewImage.startAnimation(mAnimationDown);
					}
					mState = STATE_PULL_TO_REFRESH;
					mbDeal = true;
					mTatolScroll = (int)mScroll;
					onInvalidate();
				}
				else if(mScroll > HEAD_VIEW_HEIGHT)
				{					
					mProgressBar.setVisibility(View.GONE);
					mRefreshViewImage.setVisibility(View.VISIBLE);
					if(mState != STATE_RELEASE_TO_REFRESH) {
						mRefreshViewImage.clearAnimation();
						mRefreshViewImage.startAnimation(mAnimationUp);
					}
					mState = STATE_RELEASE_TO_REFRESH;
					mbDeal = true;
					mTatolScroll = (int)mScroll;
					onInvalidate();
				}
			}
			mLastMotionY = y;
		}
		return mbDeal;
	}
		
	private class Flinger implements Runnable {
		private int mLastFlingX;
		private Scroller mScroller;

		public Flinger() {
			mScroller = new Scroller(getContext());
		}

		private void startCommon() {
			removeCallbacks(this);
		}

		public void run() {
			boolean more = mScroller.computeScrollOffset();
			if (more) {
				mTatolScroll -= mLastFlingX - mScroller.getCurrX();
				mLastFlingX = mScroller.getCurrX();
//				removeCallbacks(this);
				post(this);
			} else {				
			}
			onInvalidate();
		}

		public void startUsingDistance(int distance, int duration) {
			startCommon();
			mLastFlingX = 0;
			
			mScroller.startScroll(0, 0, -distance, 0, duration);
			post(this);
		}
	}

	private void endUpdate(String date) {
		setUpdateDate(date);
		
		if (mTatolScroll != 0) {
			scrollToClose();
		}
		mState = STATE_RESET;
	}
	
	private void setUpdateDate(String date){
		if (date == null) {
			return;
		}
		mDate = date;
	}
	
	private void scrollToUpdate() {
		mFling.startUsingDistance(mTatolScroll -HEAD_VIEW_HEIGHT, 250);
	}
	
	private void scrollToClose() {
		if (mTatolScroll == 0) {
			return;
		}
		mFling.startUsingDistance(mTatolScroll, 250);
	}
	
	public void onComplete(final String date) {
		endUpdate(date);
	}
	
	public void doRefresh() {
		mState = STATE_REFRESHING;
		scrollToUpdate();
		onRefresh();
	}
		
	public interface OnContainerRefreshListener {
		/**
		 * * Called when the list should be refreshed. *
		 * <p>
		 * * A call to {@link PullToRefreshListView #onRefreshComplete()} is *
		 * expected to indicate that the refresh has completed.
		 */
		public void onContainerRefresh();
	}
	/**
     * Register a callback to be invoked when this list should be refreshed.
     * 
     * @param onRefreshListener The callback to run.
     */
    public void setOnRefreshListener(OnContainerRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

	public void onRefresh() {
		if (mOnRefreshListener != null) {
			mOnRefreshListener.onContainerRefresh();
		}
	}


}