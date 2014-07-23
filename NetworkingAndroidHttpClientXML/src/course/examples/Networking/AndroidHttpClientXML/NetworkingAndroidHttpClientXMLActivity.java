package course.examples.Networking.AndroidHttpClientXML;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;

import android.app.ListActivity;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class NetworkingAndroidHttpClientXMLActivity extends ListActivity { // <-- Extendemos de ListActivity para poder usar luega setListAdapter que es muy comodo

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		new HttpGetTask().execute(); // <-- Cuando se hace click en el botón, se lanza este intent que lo primero que hace es 
								 	// generar un nuevo objeto tipo HttpGetTask sobre el que llamamos a execute
	}

	private class HttpGetTask extends AsyncTask<Void, Void, List<String>> { // <-- Extendemos de AsyncTask, porque queremos realizar esta tarea en background
																			// Los 3 params.abstractos se usan en doInBackground, onProgressUpdate y onPostExecute respectivamente
		private static final String TAG = "HttpGetTask";

		// Get your own user name at http://www.geonames.org/login
		private static final String USER_NAME = "aporter";

		private static final String URL = "http://api.geonames.org/earthquakes?north=44.1&south=-9.9&east=-22.4&west=55.2&username="
				+ USER_NAME;

		AndroidHttpClient mClient = AndroidHttpClient.newInstance(""); // <-- Objeto de Android.net que se encargará de la comunicación

		@Override
		protected List<String> doInBackground(Void... params) {
			// El AndroidHttpClient que hemos definido arriba va a necesitar dos parámetros que a continución preparamos:
			HttpGet request = new HttpGet(URL); // <-- Objeto de org.apache para realizar la petición a la web
			XMLResponseHandler responseHandler = new XMLResponseHandler(); // <-- Objeto para gestionar la respuesta
			try {
				
				return mClient.execute(request, responseHandler); // <-- El método execute devuelve un String directamente
				
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null; // <-- Si fallara la conexión devolveríamos null
		}

		@Override
		protected void onPostExecute(List<String> result) {
			if (null != mClient)
				mClient.close(); // <-- Importante cerrar el AndroidHttpClient
			setListAdapter(new ArrayAdapter<String>(NetworkingAndroidHttpClientXMLActivity.this, R.layout.list_item, result));
			// Al haber extendido de ListActivity, podemos usar setListAdapter que es más facil 
		}
	}
}