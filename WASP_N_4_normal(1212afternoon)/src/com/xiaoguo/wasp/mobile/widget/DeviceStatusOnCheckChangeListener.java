package com.xiaoguo.wasp.mobile.widget;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.TaskListener;

import android.content.Context;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class DeviceStatusOnCheckChangeListener implements
		OnCheckedChangeListener {

	private Context context;

	private static DeviceStatusOnCheckChangeListener instan;

	private int realDataAreaID;

	private int sluice;

	private int roller;

	private boolean sluiceStatus;

	private CommandBase commandBase = CommandBase.instance();

	public static DeviceStatusOnCheckChangeListener instance() {

		if (instan == null) {

			instan = new DeviceStatusOnCheckChangeListener();
		}

		return instan;
	}

	/**
	 * @return the sluiceStatus
	 */
	public boolean isSluiceStatus() {
		return sluiceStatus;
	}

	/**
	 * @param sluiceStatus
	 *            the sluiceStatus to set
	 */
	public void setSluiceStatus(boolean sluiceStatus) {
		this.sluiceStatus = sluiceStatus;
	}

	public DeviceStatusOnCheckChangeListener() {

	}

	/**
	 * @return the context
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * @return the realDataAreaID
	 */
	public int getRealDataAreaID() {
		return realDataAreaID;
	}

	/**
	 * @param realDataAreaID
	 *            the realDataAreaID to set
	 */
	public void setRealDataAreaID(int realDataAreaID) {
		this.realDataAreaID = realDataAreaID;
	}

	/**
	 * @return the sluice
	 */
	public int getSluice() {
		return sluice;
	}

	/**
	 * @param sluice
	 *            the sluice to set
	 */
	public void setSluice(int sluice) {
		this.sluice = sluice;
	}

	/**
	 * @return the roller
	 */
	public int getRoller() {
		return roller;
	}

	/**
	 * @param roller
	 *            the roller to set
	 */
	public void setRoller(int roller) {
		this.roller = roller;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {

			commandBase.request(new TaskListener() {

				@Override
				public void updateCacheDate(
						List<HashMap<String, Object>> cacheData) {

				}

				@Override
				public void start() {
				}

				@Override
				public String requestUrl() {
					return "updateStatus";
				}

				@Override
				public JSONObject requestData() {

					JSONObject object1 = new JSONObject();
					try {
						object1.put("id", realDataAreaID);
						object1.put("sluice", 1);
						object1.put("roller", roller);
					} catch (JSONException e) {
						e.printStackTrace();
					}

					setSluiceStatus(true);

					return object1;
				}

				@Override
				public String readCache() {
					return null;
				}

				@Override
				public boolean needCacheTask() {
					return false;
				}

				@Override
				public void messageUpdated(JSONObject msg) {
					System.out.println("返回的数据11111111111111111--->>"
							+ msg.toString());
				}

				@Override
				public void finish() {
				}

				@Override
				public String filepath() {
					return null;
				}

				@Override
				public void failure(String str) {

				}

				@Override
				public String contentype() {
					return null;
				}
			});

		} else {
			commandBase.request(new TaskListener() {

				@Override
				public void updateCacheDate(
						List<HashMap<String, Object>> cacheData) {

				}

				@Override
				public void start() {

				}

				@Override
				public String requestUrl() {
					return "updateStatus";
				}

				@Override
				public JSONObject requestData() {

					JSONObject object1 = new JSONObject();
					try {
						object1.put("id", realDataAreaID);
						object1.put("sluice", 0);
						object1.put("roller", roller);
					} catch (JSONException e) {
						e.printStackTrace();
					}

					setSluiceStatus(false);
					return object1;
				}

				@Override
				public String readCache() {
					return null;
				}

				@Override
				public boolean needCacheTask() {
					return false;
				}

				@Override
				public void messageUpdated(JSONObject msg) {
					System.out.println("返回的数据222222222222222222--->>"
							+ msg.toString());
				}

				@Override
				public void finish() {

				}

				@Override
				public String filepath() {
					return null;
				}

				@Override
				public void failure(String str) {

				}

				@Override
				public String contentype() {
					return null;
				}
			});

		}

	}

}
