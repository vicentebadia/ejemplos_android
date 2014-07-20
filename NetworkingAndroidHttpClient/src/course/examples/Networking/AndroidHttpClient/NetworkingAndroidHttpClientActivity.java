package course.examples.Networking.AndroidHttpClient;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;

import android.app.Activity;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class NetworkingAndroidHttpClientActivity extends Activity {
	private TextView mTextView = null;

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

	private class HttpGetTask extends AsyncTask<Void, Void, String> { // <-- Extendemos de AsyncTask, porque queremos realizar esta tarea en background
																	  // Los 3 params.abstractos se usan en doInBackground, onProgressUpdate y onPostExecute respectivamente
		private static final String TAG = "HttpGetTask";

		// Get your own user name at http://www.geonames.org/login
		private static final String USER_NAME = "aporter";

		private static final String URL = "http://api.geonames.org/earthquakesJSON?north=44.1&south=-9.9&east=-22.4&west=55.2&username="
				+ USER_NAME;

		AndroidHttpClient mClient = AndroidHttpClient.newInstance(""); // <-- Objeto de Android.net que se encargará de la comunicación

		@Override
		protected String doInBackground(Void... params) {
			// El AndroidHttpClient que hemos definido arriba va a necesitar dos parámetros que a continución preparamos:
			HttpGet request = new HttpGet(URL); // <-- Objeto de org.apache para realizar la petición a la web
			ResponseHandler<String> responseHandler = new BasicResponseHandler(); // <-- Objeto para gestionar la respuesta

			try {

				return mClient.execute(request, responseHandler); // <-- El método execute devuelve un String directamente

			} catch (ClientProtocolException exception) {
				exception.printStackTrace();
			} catch (IOException exception) {
				exception.printStackTrace();
			}
			return null; // <-- Si fallara la conexión devolveríamos null
		}

		@Override
		protected void onPostExecute(String result) { // <-- Lo que ha devuelto doInBackground esta almacenado en result

			if (null != mClient)
				mClient.close(); // <-- Importante cerrar el AndroidHttpClient si no se había cerrado

			mTextView.setText(result); // <-- Podemos acceder al UIThread porque hemos usado una AsyncTask

		}
	}
}