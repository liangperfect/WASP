package com.xiaoguo.wasp.mobile.widget;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;

public class MultipleChart {

	private Context context;

	private float[] data;

	private String[] time;

	private String xTitle;

	private String yTitle;

	private String title;

	public MultipleChart(Context context1, float[] data1, String[] time1,
			String xTitle1, String yTitle1, String title1) {

		this.context = context1;

		this.data = data1;

		this.time = time1;

		this.xTitle = xTitle1;

		this.yTitle = yTitle1;

		this.title = title1;
	}

	@SuppressWarnings("deprecation")
	public Intent execute() {
		List<float[]> x = new ArrayList<float[]>();
		x.add(new float[] { 1, 2, 3, 4, 5, 6, 7 });

		List<float[]> values = new ArrayList<float[]>();
		values.add(data);

		int[] colors = new int[] { Color.GREEN };
		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE };
		XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
		for (int i = 0; i < 1; i++) {
			((XYSeriesRenderer) renderer.getSeriesRendererAt(i))
					.setFillPoints(true);
		}
		// renderer.setChartTitle(title);
		// renderer.setXTitle(xTitle);
		// renderer.setYTitle(yTitle);
		// renderer.setXAxisMin(xMin);
		// renderer.setXAxisMax(xMax);
		// renderer.setYAxisMin(yMin);
		// renderer.setYAxisMax(yMax);
		// renderer.setAxesColor(axesColor);
		// renderer.setLabelsColor(labelsColor);
		//
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
//		renderer.setXAxisMin(0);
//		renderer.setXAxisMax(10);
//		renderer.setYAxisMin(-100);
//		renderer.setYAxisMax(60);
//		renderer.setYAxisMax(1000);
		//不封顶
		renderer.setPanEnabled(true);
		renderer.setAxesColor(Color.LTGRAY);
		renderer.setLabelsColor(Color.LTGRAY);
		renderer.setXLabels(0);
		renderer.setMargins(new int[] { 20, 20, 20, 20 });
		for (int i = 0; i < time.length; i++) {
			renderer.addTextLabel(i + 1, time[i]);
		}

		renderer.setShowGrid(true);
		renderer.setBackgroundColor(Color.BLACK);
		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setLabelsTextSize(10);
		renderer.setZoomButtonsVisible(true);
		renderer.setLabelsTextSize(15);
		renderer.setPanLimits(new double[] { -10, 20, -10, 40 });
		renderer.setZoomLimits(new double[] { -10, 20, -10, 40 });
		// 数据了
		// XYMultipleSeriesDataset dataset = buildDataset(titles, x, values);
		// XYSeries series = dataset.getSeriesAt(0);
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		XYSeries series = new XYSeries(title, 0);
		for (int i = 0; i < data.length; i++) {
			series.add(i + 1, data[i]);
		}
		dataset.addSeries(series);

		Intent intent = ChartFactory.getLineChartIntent(context, dataset,
				renderer);
		return intent;
	}

	protected XYMultipleSeriesRenderer buildRenderer(int[] colors,
			PointStyle[] styles) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		setRenderer(renderer, colors, styles);
		return renderer;
	}

	protected void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors,
			PointStyle[] styles) {
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setPointSize(5f);
		renderer.setMargins(new int[] { 20, 30, 15, 20 });
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors[i]);
			r.setPointStyle(styles[i]);
			renderer.addSeriesRenderer(r);
		}
	}

}
