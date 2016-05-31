package com.xiaoguo.wasp.mobile;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class WASPApplication extends Application{
	 private List<Activity> mList = new LinkedList<Activity>(); 
	    private static WASPApplication instance; 
	  
	    private WASPApplication() {   
	    } 
	    public synchronized static WASPApplication getInstance() { 
	        if (null == instance) { 
	            instance = new WASPApplication(); 
	        } 
	        return instance; 
	    } 
	    /**
	     * 在需要退出的Activity的onCrete()方法 
	     * @param activity
	     */
	    public void addActivity(Activity activity) { 
	        mList.add(activity); 
	    } 
	  
	    public void exit() { 
	        try { 
	            for (Activity activity : mList) { 
	                if (activity != null) 
	                    activity.finish(); 
	            } 
	        } catch (Exception e) { 
	            e.printStackTrace(); 
	        } finally { 
	            System.exit(0); 
	        } 
	    } 
	    public void onLowMemory() { 
	        super.onLowMemory();     
	        System.gc(); 
	    }  

}
