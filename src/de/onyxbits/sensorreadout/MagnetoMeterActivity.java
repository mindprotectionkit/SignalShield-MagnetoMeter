/*
   Copyright 2012 Patrick Ahlbrecht

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package de.onyxbits.sensorreadout;

import org.achartengine.ChartFactory;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.mimdprotectionkit.magnetometer.R;

/**
 * <code>Activity</code> that displays the readout of one <code>Sensor</code>.
 * This <code>Activity</code> must be started with an <code>Intent</code> that
 * passes in the number of the <code>Sensor</code> to display. If none is
 * passed, the first available <code>Sensor</code> is used.
 */
public class MagnetoMeterActivity extends ReadoutActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_PROGRESS);
		
		super.onCreate(savedInstanceState);
		
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		this.getSupportActionBar().setHomeButtonEnabled(false);
		this.getSupportActionBar().setTitle(R.string.app_name);
		

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		int idx = getIntent().getIntExtra(SENSORINDEX, 0);
		sensor = sensorManager.getSensorList(Sensor.TYPE_ALL).get(idx);
		setTitle(sensor.getName());

		sensorData = new XYMultipleSeriesDataset();
		renderer = new XYMultipleSeriesRenderer();
		renderer.setGridColor(Color.DKGRAY);
		renderer.setShowGrid(true);
		renderer.setXAxisMin(0.0);
		//renderer.setXTitle(getString(R.string.samplerate, 1000 / SAMPLERATE));
		renderer.setXAxisMax(10000 / (1000 / SAMPLERATE)); // 10 seconds wide
		renderer.setXLabels(10); // 1 second per DIV
		renderer.setChartTitle(" ");
		renderer.setYLabelsAlign(Paint.Align.RIGHT);
		chartView = ChartFactory.getLineChartView(this, sensorData, renderer);
		chartView.setOnTouchListener(this);
		float textSize = new TextView(this).getTextSize();
		float upscale = textSize / renderer.getLegendTextSize();
		renderer.setLabelsTextSize(textSize);
		renderer.setLegendTextSize(textSize);
		renderer.setChartTitleTextSize(textSize);
		renderer.setAxisTitleTextSize(textSize);
		renderer.setFitLegend(true);
		int[] margins = renderer.getMargins();
		margins[0] *= upscale;
		margins[1] *= upscale;
		margins[2] = (int) (2*renderer.getLegendTextSize());
		renderer.setMargins(margins);
		setContentView(R.layout.readout_pending);

	}



	/**
	 * Final configuration step. Must be called between receiving the first
	 * <code>SensorEvent</code> and updating the graph for the first time.
	 * 
	 * @param event
	 *          the event
	 */
	@SuppressWarnings("deprecation")
	private void configure(SensorEvent event) {
		String[] channelNames = new String[event.values.length];
		channel = new XYSeries[event.values.length];
		for (int i = 0; i < channelNames.length; i++) {
			channelNames[i] = getString(R.string.channel_default) + i;
		}

		switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER: {
				channelNames[0]=getString(R.string.channel_x_axis);
				channelNames[1]=getString(R.string.channel_y_axis);
				channelNames[2]=getString(R.string.channel_z_axis);
				renderer.setYTitle(getString(R.string.unit_acceleration));
				break;
			}
			case Sensor.TYPE_GRAVITY: {
				channelNames[0]=getString(R.string.channel_x_axis);
				channelNames[1]=getString(R.string.channel_y_axis);
				channelNames[2]=getString(R.string.channel_z_axis);
				renderer.setYTitle(getString(R.string.unit_acceleration));
				break;
			}
			case Sensor.TYPE_GYROSCOPE: {
				channelNames[0]=getString(R.string.channel_x_axis);
				channelNames[1]=getString(R.string.channel_y_axis);
				channelNames[2]=getString(R.string.channel_z_axis);
				renderer.setYTitle(getString(R.string.unit_gyro));
				break;
			}
			case Sensor.TYPE_LIGHT: {
				channel = new XYSeries[1];
				channelNames[0]=getString(R.string.channel_light);
				renderer.setYTitle(getString(R.string.unit_light));
				break;
			}
			case Sensor.TYPE_LINEAR_ACCELERATION: {
				channelNames[0]=getString(R.string.channel_x_axis);
				channelNames[1]=getString(R.string.channel_y_axis);
				channelNames[2]=getString(R.string.channel_z_axis);
				renderer.setYTitle(getString(R.string.unit_acceleration));
				break;
			}
			case Sensor.TYPE_MAGNETIC_FIELD: {
				channelNames[0]=getString(R.string.channel_x_axis);
				channelNames[1]=getString(R.string.channel_y_axis);
				channelNames[2]=getString(R.string.channel_z_axis);
				renderer.setYTitle(getString(R.string.unit_magnetic));
				break;
			}
			case Sensor.TYPE_PRESSURE: {
				channel = new XYSeries[1];
				channelNames[0]=getString(R.string.channel_pressure);
				renderer.setYTitle(getString(R.string.unit_pressure));
				break;
			}
			case Sensor.TYPE_PROXIMITY: {
				channel = new XYSeries[1];
				channelNames[0]=getString(R.string.channel_distance);
				renderer.setYTitle(getString(R.string.unit_distance));
				break;
			}
			case Sensor.TYPE_ROTATION_VECTOR: {
				channelNames[0]=getString(R.string.channel_x_axis);
				channelNames[1]=getString(R.string.channel_y_axis);
				channelNames[2]=getString(R.string.channel_z_axis);
				break;
			}
			case Sensor.TYPE_ORIENTATION: {
				channelNames[0]=getString(R.string.channel_azimuth);
				channelNames[1]=getString(R.string.channel_pitch);
				channelNames[2]=getString(R.string.channel_roll);
				break;
			}
			case 7:
			case 13: {
				// Dirty hack: TYPE_TEMPERATURE became deprecated in favour of
				// TYPE_AMBIENT_TEMPERATURE. By
				// using the numeric instead of the symbolic constants, we can easily
				// compile for pre- and
				// post API level 14.
				renderer.setYTitle(getString(R.string.unit_temperature));
				break;
			}
		}

		int[] colors = { Color.RED, Color.YELLOW, Color.BLUE, Color.GREEN,
				Color.MAGENTA, Color.CYAN };
		for (int i = 0; i < channel.length; i++) {
			channel[i] = new XYSeries(channelNames[i]);
			sensorData.addSeries(channel[i]);
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors[i % colors.length]);
			renderer.addSeriesRenderer(r);
		}
	}
	
	protected void onTick(SensorEvent currentEvent) {

		if (xTick == 0) {
			// Dirty, but we only learn a few things after getting the first event.
			configure(currentEvent);
			setContentView(chartView);
		}

		if (xTick > renderer.getXAxisMax()) {
			renderer.setXAxisMax(xTick);
			renderer.setXAxisMin(++lastMinX);
		}

		this.fitYAxis(currentEvent);

		for (int i = 0; i < channel.length; i++) {
			if (channel[i] != null) {
				channel[i].add(xTick, currentEvent.values[i]);
			}
		}

		xTick++;

		//renderer.setChartTitle(getString(R.string.sensor_accuracy_high));
		

		chartView.repaint();
	}

}