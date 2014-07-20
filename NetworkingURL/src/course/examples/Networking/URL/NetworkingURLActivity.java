package course.examples.Networking.URL;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class NetworkingURLActivity extends Activity {
	private TextView mTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		mTextView = (TextView) findViewById(R.id.textView1);

		final Button loadButton = (Button) findViewById(R.id.button1);
		loadButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new HttpGetTask().execute(); // <-- Hacer click genera un nuevo objeto tipo HttpGetTask sobre el que llamamos a execute
			}
		});
	}

	private class HttpGetTask extends AsyncTask<Void, Void, String> { // <-- Extendemos de AsyncTask para hacer el trabajo en background
																	  // Los 3 params.abstractos se usan en doInBackground, onProgressUpdate y onPostExecute respectivamente
		private static final String TAG = "HttpGetTask";

		// Get your own user name at http://www.geonames.org/login
		private static final String USER_NAME = "aporter";
		private static final String URL = "http://api.geonames.org/earthquakesJSON?north=44.1&south=-9.9&east=-22.4&west=55.2&username="
				+ USER_NAME;

		@Override
		protected String doInBackground(Void... params) {
			String data = "";
			HttpURLConnection httpUrlConnection = null; // <-- Inicializamos a null un objeto tipo HttpURLConnection de la libreria Java.net

			try {
				httpUrlConnection = (HttpURLConnection) new URL(URL).openConnection(); // <-- Llamamos a openConnection sobre un nuevo objeto tipo URL

				InputStream in = new BufferedInputStream(httpUrlConnection.getInputStream()); // <-- getInputStream sobre el objeto anterior nos devuelve un BufferedInputStream 

				data = readStream(in); // <-- Con el método que nos hemos hecho mas abajo, convertimos el BufferedInputStream en un String

			} catch (MalformedURLException exception) {
				Log.e(TAG, "MalformedURLException");
			} catch (IOException exception) {
				Log.e(TAG, "IOException");
			} finally {
				if (null != httpUrlConnection)
					httpUrlConnection.disconnect(); // <-- Importante desconectar el URL al acabar
			}
			return data; // <-- Devolvemos el String con el resultado que nos ha devuelto la web
		}

		@Override
		protected void onPostExecute(String result) { // <-- onPostExecute permite actuar sobre el UI Thread sin problemas
			mTextView.setText(result);
		}

		private String readStream(InputStream in) { // <-- Metodo que hemos creado para convertir de InputStream a String
			BufferedReader reader = null;
			StringBuffer data = new StringBuffer("");
			try {
				reader = new BufferedReader(new InputStreamReader(in));
				String line = "";
				while ((line = reader.readLine()) != null) {
					data.append(line);
				}
			} catch (IOException e) {
				Log.e(TAG, "IOException");
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return data.toString();
		}
	}
}