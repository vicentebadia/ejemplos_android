package course.examples.Networking.AndroidHttpClientJSON;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.ListActivity;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class NetworkingAndroidHttpClientJSONActivity extends ListActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new HttpGetTask().execute(); // <-- Hacer click genera un nuevo objeto tipo HttpGetTask sobre el que llamamos a execute
	}

	private class HttpGetTask extends AsyncTask<Void, Void, List<String>> { // <-- Extendemos de AsyncTask, porque queremos realizar esta tarea en background
														 					// Los 3 params.abstractos se usan en doInBackground, onProgressUpdate y onPostExecute respectivamente
		private static final String TAG = "HttpGetTask";

		// Get your own user name at http://www.geonames.org/login
		private static final String USER_NAME = "aporter";

		private static final String URL = "http://api.geonames.org/earthquakesJSON?north=44.1&south=-9.9&east=-22.4&west=55.2&username="
				+ USER_NAME; // <-- Estamos haciendo una consulta tipo JSON

		AndroidHttpClient mClient = AndroidHttpClient.newInstance(""); // <-- Objeto de Android.net que se encargará de la comunicación

		@Override
		protected List<String> doInBackground(Void... params) { // <-- doInBackgroung va a devolver una lista de Strings
			// El AndroidHttpClient que hemos definido arriba va a necesitar dos parámetros que a continución preparamos:
			HttpGet request = new HttpGet(URL); // <-- Objeto de org.apache para realizar la petición a la web
			JSONResponseHandler responseHandler = new JSONResponseHandler(); // <-- Objeto para gestionar la respuesta de tipo JSON (nos lo genermos nosotros)
			try {
				return mClient.execute(request, responseHandler); // <-- El metodo execute devuelve una lista de Strings
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null; // <-- Si fallara la conexión devolveríamos null
		}

		@Override
		protected void onPostExecute(List<String> result) { // <-- Ahora onPostExecute recibe la lista de Strings "result"
			if (null != mClient)
				mClient.close(); // <-- Importante cerrar el AndroidHttpClient
			setListAdapter(new ArrayAdapter<String>(NetworkingAndroidHttpClientJSONActivity.this, R.layout.list_item, result));
			// Creamos un listAdapter pasando como contexto esta actividad, el recurso de android de lista de items, y los datos en result
		}
	}

	private class JSONResponseHandler implements ResponseHandler<List<String>> { // <-- ResponseHandler para manegar objetos JSON  que nosotros mismos creamos

		private static final String LONGITUDE_TAG = "lng";
		private static final String LATITUDE_TAG = "lat";
		private static final String MAGNITUDE_TAG = "magnitude";
		private static final String EARTHQUAKE_TAG = "earthquakes";

		@Override
		public List<String> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
			
			List<String> result = new ArrayList<String>();
			String JSONResponse = new BasicResponseHandler().handleResponse(response); // <-- Pasamos el contenido de la respuesta a un String
			try {

				// Get top-level JSON Object - a Map
				JSONObject responseObject = (JSONObject) new JSONTokener(JSONResponse).nextValue();

				// Extract value of "earthquakes" key -- a List
				JSONArray earthquakes = responseObject.getJSONArray(EARTHQUAKE_TAG); // <-- Devuelve el contenido de earthquakes completo

				// Iterate over earthquakes list
				for (int idx = 0; idx < earthquakes.length(); idx++) {

					// Get single earthquake data - a Map
					JSONObject earthquake = (JSONObject) earthquakes.get(idx); // <-- Tomamos un elemento de la lista

					// Summarize earthquake data as a string and add it to
					// result
					result.add(MAGNITUDE_TAG + ":"
							+ earthquake.get(MAGNITUDE_TAG) + ","
							+ LATITUDE_TAG + ":"
							+ earthquake.getString(LATITUDE_TAG) + ","
							+ LONGITUDE_TAG + ":"
							+ earthquake.get(LONGITUDE_TAG)); // <-- Y a la lista en la posición idx el String con los datos correspondientes
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return result; // <-- Devolvemos la lista de Strings	
		}
	}
}