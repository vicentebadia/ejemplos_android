package course.examples.Networking.Sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class NetworkingSocketsActivity extends Activity {
	TextView mTextView;

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
		private static final String HOST = "api.geonames.org";

		// Get your own user name at http://www.geonames.org/login
		private static final String USER_NAME = "aporter";
		private static final String HTTP_GET_COMMAND = "GET /earthquakesJSON?north=44.1&south=-9.9&east=-22.4&west=55.2&username="
				+ USER_NAME
				+ " HTTP/1.1"
				+ "\n"
				+ "Host: "
				+ HOST
				+ "\n"
				+ "Connection: close" + "\n\n";

		private static final String TAG = "HttpGet";

		@Override
		protected String doInBackground(Void... params) {
			Socket socket = null; // <-- Inicializamos a null un objeto tipo Socket
			String data = "";

			try {
				socket = new Socket(HOST, 80); // <-- Definimos donde conecta el socket y por que puerto
				PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
				pw.println(HTTP_GET_COMMAND); // <-- Con esta linea y la anteriors le pasamos al socket el comando HTTP

				data = readStream(socket.getInputStream()); // <-- Guardo en data el valor que me devuelve la página a la que hemos hecho el request

			} catch (UnknownHostException exception) {
				exception.printStackTrace();
			} catch (IOException exception) {
				exception.printStackTrace();
			} finally {
				if (null != socket)
					try {
						socket.close(); // <-- Importante cerrar el socket al acabar
					} catch (IOException e) {
						Log.e(TAG, "IOException");
					}
			}
			return data; // <-- doInBackground devuelve el string con la respuesta
		}

		@Override
		protected void onPostExecute(String result) {
			mTextView.setText(result); // <-- Al acabar la tarea asíncrona, actualizamos el TextView correspondiente en pantalla.
		}

		private String readStream(InputStream in) { // <-- Método auxiliar para leer el stream que nos devuelve la web
			BufferedReader reader = null;
			StringBuffer data = new StringBuffer();
			try {
				reader = new BufferedReader(new InputStreamReader(in)); // <-- Se crea objeto tipo BufferReader a partir del InputStream que se nos pasa
				String line = "";
				while ((line = reader.readLine()) != null) { // <-- Mientras haya lineas, vamos concatenandolo al StringBurffer data
					data.append(line);
				}
			} catch (IOException e) {
				Log.e(TAG, "IOException");
			} finally {
				if (reader != null) {
					try {
						reader.close(); // <-- Importante cerrar el BufferReader al acabar
					} catch (IOException e) {
						Log.e(TAG, "IOException");
					}
				}
			}
			return data.toString(); // <-- Convertimos el StringBuffer data en un String
		}
	}
}