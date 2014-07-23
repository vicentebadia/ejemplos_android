package course.examples.Networking.AndroidHttpClientXML;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

class XMLResponseHandler implements ResponseHandler<List<String>> {

	private static final String MAGNITUDE_TAG = "magnitude";
	private static final String LONGITUDE_TAG = "lng";
	private static final String LATITUDE_TAG = "lat";
	private String mLat, mLng, mMag;
	private boolean mIsParsingLat, mIsParsingLng, mIsParsingMag;
	private final List<String> mResults = new ArrayList<String>();

	@Override
	public List<String> handleResponse(HttpResponse response)
			throws ClientProtocolException, IOException {
		try {

			// Create the Pull Parser
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xpp = factory.newPullParser();

			// Set the Parser's input to be the XML document in the HTTP Response
			xpp.setInput(new InputStreamReader(response.getEntity().getContent()));
			
			// Get the first Parser event and start iterating over the XML document 
			int eventType = xpp.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) { // <-- Segun el tag que vamos encontrando realizamos una acción un otra

				if (eventType == XmlPullParser.START_TAG) {
					startTag(xpp.getName());
				} else if (eventType == XmlPullParser.END_TAG) {
					endTag(xpp.getName());
				} else if (eventType == XmlPullParser.TEXT) {
					text(xpp.getText());
				}
				eventType = xpp.next();
			}
			return mResults; // <-- Si todo va bien, devolvemos una lista de String con los datos ya parseados
		} catch (XmlPullParserException e) {
		}
		return null; // <-- Si algo falla, devolvemos null
	}

	public void startTag(String localName) { // <-- Si hemos encontrado un start tag, ponemos a true el flag correspondiente
		if (localName.equals(LATITUDE_TAG)) {
			mIsParsingLat = true;
		} else if (localName.equals(LONGITUDE_TAG)) {
			mIsParsingLng = true;
		} else if (localName.equals(MAGNITUDE_TAG)) {
			mIsParsingMag = true;
		}
	}

	public void text(String text) { // <-- Si hemos encontrado texto, lo guardamos donde corresponde
		if (mIsParsingLat) {
			mLat = text.trim();
		} else if (mIsParsingLng) {
			mLng = text.trim();
		} else if (mIsParsingMag) {
			mMag = text.trim();
		}
	}

	public void endTag(String localName) { // <-- Al ver un end tag, ponemos a false el flag correspondiente
		if (localName.equals(LATITUDE_TAG)) {
			mIsParsingLat = false;
		} else if (localName.equals(LONGITUDE_TAG)) {
			mIsParsingLng = false;
		} else if (localName.equals(MAGNITUDE_TAG)) {
			mIsParsingMag = false;
		} else if (localName.equals("earthquake")) { // <-- Añadimos el dato a la posicion de la lista de Strings
			mResults.add(MAGNITUDE_TAG + ":" + mMag + "," + LATITUDE_TAG + ":"
					+ mLat + "," + LONGITUDE_TAG + ":" + mLng);
			mLat = null;	// <-- Y ponemos a null los datos a null para poder rellenarlos en la siguiente iteración (si fuera necesario)
			mLng = null;
			mMag = null;
		}
	}
}