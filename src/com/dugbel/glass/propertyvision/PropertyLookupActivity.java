package com.dugbel.glass.propertyvision;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.dugbel.glass.exception.GeocodingException;
import com.dugbel.glass.exception.InitializationException;
import com.google.android.glass.app.Card;


/**
 * {@link Activity} for looking up and presenting property details
 * using various property API services
 * 
 * @author Doug Bell (douglas.bell@gmail.com)
 *
 */
public class PropertyLookupActivity extends Activity {

	/**
	 * {@link AsyncTask} that makes the call to the Zillow Search Results Service
	 * then reads and converts the returned document into a {@link PropertyDetail}
	 * object finally updating and displaying the static card with the property 
	 * information.
	 * 
	 * @author Doug Bell (douglas.bell@gmail.com)
	 *
	 */
	class PropertyDetailLookupTask extends AsyncTask<String, Integer, PropertyDetail> {

		/** The Zillow deep URL */
		private static final String DEEP_URL = "http://www.zillow.com/webservice/GetDeepSearchResults.htm";

		/** The Zillow ZWSID */
		private static final String ZWSID = "X1-ZWz1balbjavrwr_1d5w6";

		/** {@link DateTimeFormatter} instance */
		private final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("MM/dd/yyyy");

		/** {@link NumberFormat} instance */
		private final NumberFormat nf = NumberFormat.getCurrencyInstance();

		/** The {@link Log} tag */
		private final String TAG = PropertyDetailLookupTask.class.getSimpleName();

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected PropertyDetail doInBackground(String... params) {

			// Give the task a bit more horsepower for the DOM parsing
			Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND);

			if (params == null || params.length < 4) {
				throw new RuntimeException("Expected at four params. Received " + params.length);
			}

			final String address = params[0];
			final String city = params[1];
			final String region = params[2];
			final String postalCode = params[3];

			Log.v(TAG, "Looking up address: " + address + " city: " + city + " region:" + region + " postal code:" + postalCode) ;

			try {
				Log.v(TAG, "Retrieving and parsing document");

				//TODO prefer postal code when available 
				final String requestUrl = DEEP_URL + "?zws-id=" + ZWSID + "&address=" + URLEncoder.encode(address, "UTF-8") + "&citystatezip=" + URLEncoder.encode(postalCode, "UTF-8"); // + rentZestimate=true
				Log.v(TAG, "Request URL: " + requestUrl);
				final Document doc = builder.parse(requestUrl);

				Log.v(TAG, "Raw XML response\n" + toString(doc));

				// TODO Extract Currency and pass it in /SearchResults:searchresults/response/results/result/zestimate/amount[@currency="USD"]@currency
				try {
					XPathExpression bedroomsNode = xpath.compile("*/response/results/result/bedrooms/text()");
					Integer bedrooms = Integer.parseInt((String)bedroomsNode.evaluate(doc, XPathConstants.STRING));
					response.setBedrooms(bedrooms);
				} catch (NumberFormatException ignored) {}

				XPathExpression bathroomsNode = xpath.compile("*/response/results/result/bathrooms/text()");
				try {
					Float bathrooms = Float.parseFloat((String)bathroomsNode.evaluate(doc, XPathConstants.STRING));
					response.setBathrooms(bathrooms);
				} catch (NumberFormatException ignored) {}

				XPathExpression homedetailsNode = xpath.compile("*/response/results/result/links/homedetails/text()");
				String detailUrl = (String)homedetailsNode.evaluate(doc, XPathConstants.STRING);
				if (StringUtils.isNotBlank(detailUrl)) {
					response.setDetailUrl(detailUrl);
				}

				// TODO Extract Currency 

				try {
					XPathExpression valuationNode = xpath.compile("*/response/results/result/zestimate/amount[@currency=\"USD\"]/text()");
					BigDecimal valuation =  new BigDecimal(Double.parseDouble((String)valuationNode.evaluate(doc, XPathConstants.STRING)));
					response.setValuation(valuation);
				} catch (NumberFormatException ignored) {}

				try {
					XPathExpression highValuationNode = xpath.compile("*/response/results/result/zestimate/valuationRange/high[@currency=\"USD\"]/text()");
					BigDecimal highValuation =  new BigDecimal(Double.parseDouble((String)highValuationNode.evaluate(doc, XPathConstants.STRING)));
					response.setHighValuation(highValuation);
				} catch (NumberFormatException ignored) {}

				try {
					XPathExpression lowValuationNode = xpath.compile("*/response/results/result/zestimate/valuationRange/low[@currency=\"USD\"]/text()");
					BigDecimal lowValuation =  new BigDecimal(Double.parseDouble((String)lowValuationNode.evaluate(doc, XPathConstants.STRING)));
					response.setLowValuation(lowValuation);
				} catch (NumberFormatException ignored) {}

				try {
					XPathExpression finishedSqFtNode = xpath.compile("*/response/results/result/finishedSqFt/text()");
					Float totalSqFt = Float.parseFloat((String)finishedSqFtNode.evaluate(doc, XPathConstants.STRING));
					response.setTotalSqFt(totalSqFt);
				} catch (NumberFormatException ignored) {}


				XPathExpression lastSoldDateNode = xpath.compile("*/response/results/result/lastSoldDate/text()");
				String lastSoldDateStr = (String)lastSoldDateNode.evaluate(doc, XPathConstants.STRING);
				if (StringUtils.isNotBlank(lastSoldDateStr)) {
					DateTime lastSoldDate =  DateTime.parse(lastSoldDateStr, dateTimeFormatter);
					response.setLastSoldDate(lastSoldDate);
				}

				try {
					XPathExpression lastSoldPriceNode = xpath.compile("*/response/results/result/lastSoldPrice[@currency=\"USD\"]/text()");
					BigDecimal lastSoldPrice =  new BigDecimal(Double.parseDouble((String)lastSoldPriceNode.evaluate(doc, XPathConstants.STRING)));
					response.setLastSoldPrice(lastSoldPrice);
				} catch (NumberFormatException ignored) {}

				try {
					XPathExpression lotSizeSqFtNode = xpath.compile("*/response/results/result/lotSizeSqFt/text()");
					Float lotSizeSqFt = Float.parseFloat((String)lotSizeSqFtNode.evaluate(doc, XPathConstants.STRING));
					response.setLotSizeSqFt(lotSizeSqFt);
				} catch (NumberFormatException ignored) {}

				try {
					XPathExpression yearBuiltNode = xpath.compile("*/response/results/result/yearBuilt/text()");
					Integer yearBuilt = Integer.parseInt((String)yearBuiltNode.evaluate(doc, XPathConstants.STRING));
					response.setYearBuilt(yearBuilt);
				} catch (NumberFormatException ignored) {}

				Log.v(TAG, "Document parsed");

			} catch (IOException e) { 
				Log.e(TAG, e.getMessage()); 
			} catch (XPathExpressionException e) { 
				Log.e(TAG, e.getMessage()); 
			} catch (SAXException e) { 
				Log.e(TAG, e.getMessage()); 
			}

			Log.d(TAG, "Returning response");

			return response;
		}

		/**
		 * Return a String of text representing the property details
		 * 
		 * @return {@link String}
		 */
		private String getPropertyDetails() {
			
			StringBuilder buf = new StringBuilder();

			if (response.getYearBuilt() != null) {
				buf.append("Built in " + response.getYearBuilt());
			}

			buf.append(" this " + response.getCity() + " home has ");

			if (response.getBedrooms() != null) {
				buf.append(response.getBedrooms() + (response.getBedrooms() == 1 ? " bedroom, " : " bedrooms, "));	
			}

			if (response.getBathrooms() != null) {
				//float bathrooms = response.getBathrooms();
				//TODO Convert 2.1 -> 2 1/2 etc.
				buf.append(response.getBathrooms() + (response.getBathrooms() == 1 ? " bathroom, " : " bathrooms, "));
			}

			if (response.getTotalSqFt() != null) {
				buf.append("and " + Math.round(response.getTotalSqFt()) + " total square feet ");
			}

			if (response.getLotSizeSqFt() != null) {
				buf.append("on a " + Math.round(response.getLotSizeSqFt()) + " square foot lot ");
			}

			return buf.toString();
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(PropertyDetail response) {
			Log.d(TAG, "onPostExecute start");

			setContentView(R.layout.activity_property_detail);
			Log.v(TAG, response.toString());

			if (StringUtils.isNotBlank(response.getAddress())){
				TextView addressTextView = (TextView) findViewById(R.id.addressTextView);
				addressTextView.setText(response.getAddress() + " " + response.getCity());
			}

			if (response.getValuation() != null) { 
				TextView valuationTextView = (TextView) findViewById(R.id.valuationTextView);
				valuationTextView.setText(nf.format(response.getValuation().doubleValue()));
			}

			if (response.getHighValuation() != null) {
				TextView highValuationTextView = (TextView) findViewById(R.id.highValuationTextView);
				highValuationTextView.setText(nf.format(response.getHighValuation().doubleValue()));
			}

			if (response.getLowValuation() != null) {
				TextView lowValuationTextView = (TextView) findViewById(R.id.lowValuationTextView);
				lowValuationTextView.setText(nf.format(response.getLowValuation().doubleValue()));
			}

			String propertySpecifics = getPropertyDetails();

			if (propertySpecifics.length() > 0) {
				TextView propertySpecificsTextView = (TextView) findViewById(R.id.propertySpecificsTextView);
				propertySpecificsTextView.setText(propertySpecifics);
			}

			//			final ImageView iv = (ImageView) findViewById(R.id.productImageView); 
			//			try {
			//				StrictMode.setThreadPolicy(ThreadPolicy.LAX);
			//				final Bitmap productBitmap = BitmapFactory.decodeStream((InputStream)new URL(response.getImgSrc()).getContent());
			//				response.setProductBitmap(productBitmap);
			//				iv.setImageBitmap(productBitmap);
			//			} catch (MalformedURLException e) {
			//				Log.e(TAG, e.getMessage() != null ? e.getMessage() : "Malformed URL. (" + response.getImgSrc() + ")");
			//			} catch (IOException e) {
			//				Log.e(TAG, e.getMessage());
			//			}

			Log.d(TAG, "onPostExecute Complete");
		}
		
		/**
		 * For debugging only converts a {@link Document} to a {@link String}
		 * 
		 * @param doc	The Document to transform
		 * 
		 * @return {@link String}
		 */
		public  String toString(Document doc) {
			try {
				StringWriter sw = new StringWriter();
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer transformer = tf.newTransformer();
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
				transformer.setOutputProperty(OutputKeys.METHOD, "xml");
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

				transformer.transform(new DOMSource(doc), new StreamResult(sw));
				return sw.toString();
			} catch (Exception ex) {
				throw new RuntimeException("Error converting to String", ex);
			}
		}

	}

	/** Instance of {@link DocumentBuilder} */
	private static final DocumentBuilder builder;;

	/** The {@link Log} tag */
	private static final String TAG = "ItemLookupActivity";

	/** Instnace of {@link XPath} */
	private static final XPath xpath = XPathFactory.newInstance().newXPath();

	/** Initialize the {@link DocumentBuilder} */
	static{
		try {
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			// No need to validate we know the XML is valid
			factory.setNamespaceAware(false);
			factory.setValidating(false);

			builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new EntityResolver() {
				@Override
				public InputSource resolveEntity(String publicId, String systemId)
						throws SAXException, IOException {
					return new InputSource(new StringReader(""));

				}
			});

		} catch (ParserConfigurationException e) {
			throw new InitializationException(e.getMessage());
		}
	}

	/** The {@link ItemLookupResponse} holder */
	private PropertyDetail response = new PropertyDetail();

	/**
	 * Geocode the latitude / longitude to the best matched address
	 * 
	 * @param latitude		The latitude
	 * @param longitude		The longitude
	 * 
	 * @return {@link Address}
	 * @throws GeocodingException
	 */
	private Address geocode(final double latitude, final double longitude) 
			throws GeocodingException {
		try {
			Log.v(TAG, "Geocoding -> latitude: " + latitude + " longitude: " + longitude);
			Geocoder geocoder = new Geocoder(this, Locale.getDefault());
			return geocoder.getFromLocation(latitude, longitude, 1).get(0);	
		} catch (IOException e) {
			throw new GeocodingException("Unable to geocode location " + e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate starting");

		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		Location location = (Location)getIntent().getExtras().get("location");
		response.setLocation(location);

		Log.d(TAG, "Property detail lookup starting");

		try {
			Address address = geocode(location.getLatitude(), location.getLongitude());

			String addr = address.getAddressLine(0);
			response.setAddress(addr);
			Log.v(TAG, "addr: " + addr);

			String city = address.getLocality();
			response.setCity(city);
			Log.v(TAG, "city: " + city);

			String region = address.getAdminArea();
			response.setRegion(region);
			Log.v(TAG, "region: " + region);

			String postalCode = address.getPostalCode();
			response.setPostalCode(postalCode);
			Log.v(TAG, "postalCode: " + postalCode);
			PropertyDetailLookupTask lookup = new PropertyDetailLookupTask();
			lookup.execute(addr, city, region, postalCode);

			setContentView(R.layout.activity_searching_interstitial);
			TextView searchingFootnoteTextView = (TextView) findViewById(R.id.searchingFootnoteTextView);
			searchingFootnoteTextView.setText(addr + " in " + city);
		} catch (GeocodingException e) {
			Log.e(TAG, e.getMessage());
			Card card = new Card(this);
			card.setText("Could not determine the current address.");
			card.setFootnote("Please move to another location and try again");
			setContentView(card.getView());
		}

	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override 
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			openOptionsMenu();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.more_information:
			if (StringUtils.isNotBlank(response.getDetailUrl())) {
				Intent intent = new Intent(Intent.ACTION_VIEW); //TODO disable JavaScript
				intent.setData(Uri.parse(response.getDetailUrl()));

				startActivity(intent);
			}
			return true;
		case R.id.save_for_later:
			Log.d(TAG, "Selected 'save to timeline'");

			//TimelineManager manager = TimelineManager.from(this);

			Card card = new Card(this);

			card.setImageLayout(Card.ImageLayout.LEFT);
			card.addImage(R.drawable.ic_question_150);

			StringBuilder buf = new StringBuilder();

			if (response.getBedrooms() != null) {
				buf.append(response.getBedrooms() + (response.getBedrooms() == 1 ? " bedroom, " : " bedrooms, "));	
			}

			if (response.getBathrooms() != null) {
				buf.append(response.getBathrooms() + (response.getBathrooms() == 1 ? " bathroom, " : " bathrooms, "));
			}

			if (response.getTotalSqFt() != null) {
				buf.append(Math.round(response.getTotalSqFt()) + " square feet ");
			}

			card.setText(response.getAddress() + " " + response.getCity() + "\n\n" + buf.toString());

			//manager.insert(card);

			return true;
		default:
			return super.onOptionsItemSelected(item); 
		}

	}
}