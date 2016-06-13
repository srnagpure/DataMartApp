package com.example.datamartapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SubActivity extends Activity {
	private String sheetId = null;
	private ProgressBar spinner;

	public class TextViewAdapter extends BaseAdapter {

		private Context context;
		
		private String[] dataArr;

		public TextViewAdapter(Context c, String[] dataArr) {
			context = c;
			this.dataArr = dataArr;
		}
		
		public TextViewAdapter(Context c) {
			context = c;
		}

		public int getCount() {
			return dataArr.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			
			TextView textView = new TextView(context);

			try {
				
				
				CharSequence data = dataArr[position].subSequence(0,
						dataArr[position].length());

				String dataStr = data.toString();
				if(dataStr.indexOf("*") > 0){
					textView.setTextColor(Color.RED);
					textView.setText("**");
				}
				else if(dataStr.indexOf("##") > 0){
					textView.setTextColor(Color.RED);
					textView.setText(dataStr.substring(dataStr.lastIndexOf("#")+1, dataStr.length()));
				}
				else
					textView.setText(data);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			return textView;
			
			
			/*ImageView imageView;
			if (convertView == null) {
				imageView = new ImageView(context);
				imageView.setLayoutParams(new GridView.LayoutParams(80, 80));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(4, 4, 4, 4);
			} else {
				imageView = (ImageView) convertView;
			}
			imageView.setImageResource(imageIDs[position]);*/

		}

	}

	private GridView etResponse;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sub);
		String newString;
		
		  spinner=(ProgressBar)findViewById(R.id.progressBar2);
	      spinner.setVisibility(View.VISIBLE);

		
		if (savedInstanceState == null) {
		    Bundle extras = getIntent().getExtras();
		    if(extras == null) {
		        newString= "Test";
		    } else {
		        newString= extras.getString("id");
		        sheetId = extras.getString(MainActivity.SHEET_ID);
		    }
		} else {
		    newString= (String) savedInstanceState.getSerializable("id");
		    sheetId = (String) savedInstanceState.getSerializable(MainActivity.SHEET_ID);
		}
		
		
		setTitle(newString);
		loadData();

		/*etResponse = (GridView) findViewById(R.id.gridview);
		etResponse.setAdapter(new TextViewAdapter(this));

		etResponse.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Toast.makeText(getBaseContext(),
						"pic" + (position + 1) + " selected",
						Toast.LENGTH_SHORT).show();
				Intent iinent= new Intent(MainActivity.this,SubActivity.class);
				startActivity(iinent);
				
			}
		});*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
	private void loadData() {
		// get reference to the views
		// etResponse = (EditText) findViewById(R.id.etResponse);
		etResponse = (GridView) findViewById(R.id.gridview2);
		
		
		//etResponse.setBackgroundColor(Color.WHITE);
		/*etResponse.setVerticalSpacing(1);
		etResponse.setHorizontalSpacing(1);*/
		// tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);

		// check if you are connected or not
		if (isConnected()) {
			/*
			 * tvIsConnected.setBackgroundColor(0xFF00CC00);
			 * tvIsConnected.setText("You are conncted");
			 */

			// call AsynTask to perform network operation on separate thread
			/*new HttpAsyncTask()
					.execute("https://spreadsheets.google.com/feeds/list/1CF_HXYy0D691jgBpsEwhV2qzsR4BXBnISL7ovznE3p4/od6/public/basic?alt=json");*/
			
			new HttpAsyncTask()
			.execute("https://spreadsheets.google.com/feeds/list/"+sheetId.trim()+"/od6/public/basic?alt=json");
		} else {
			/* tvIsConnected.setText("You are NOT conncted"); */

			Toast.makeText(getBaseContext(), "You are NOT conncted",
					Toast.LENGTH_LONG).show();
		}

	}

	public static String GET(String url) {
		InputStream inputStream = null;
		String result = "";
		try {

			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

			
			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}

	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}

	public boolean isConnected() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected())
			return true;
		else
			return false;
	}

	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {

			return getGridData();
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			ArrayList<String> dataList = new ArrayList<String>();
			StringBuilder sb = new StringBuilder();
			String[][] dataArray = null;
			try {
				JSONObject json = new JSONObject(result);
				JSONArray entries = json.getJSONObject("feed").getJSONArray(
						"entry");

				dataArray = new String[entries.length()][];
				for (int i = 0; i < entries.length(); i++) {
					String[] dataItems = entries.getJSONObject(i)
							.getJSONObject("content").getString("$t")
							.split(",");
					int j = 0;
					for (String dataItem : dataItems) {
						String data = dataItem.split(":")[1];
						sb.append("|" + data);
						dataList.add(data);
					}
					sb.append("| \n");
					/*
					 * String s =
					 * entries.getJSONObject(i).getJSONObject("content"
					 * ).getString("$t"); String str = "| "+ s; sb.append(str);
					 */
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			final String[] dataArr = new String[dataList.size()];
			dataList.toArray(dataArr);

			spinner.setVisibility(View.GONE);
			Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG)
					.show();
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					getBaseContext(), android.R.layout.simple_list_item_1,
					dataArr);

			TextViewAdapter textViewAdapter = new TextViewAdapter(
					getBaseContext(), dataArr);

			/* etResponse.setText(sb.toString()); */
			etResponse.setAdapter(textViewAdapter);
			/*etResponse.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View v,
						int position, long id) {
					
					try {
						Toast.makeText(
								getBaseContext(),
								dataArr[position].subSequence(0,
										dataArr[position].length()),
								Toast.LENGTH_SHORT).show();
						
						Intent i = new Intent(getBaseContext(), SubActivity.class);                      
						i.putExtra("id", "");
						startActivity(i);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					

				}
			});*/

		}

		private String getGridData() {
			StringBuilder stringBuilder = null;
			StringBuffer sb = null;
			try {
				final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
					@Override
					public void checkClientTrusted(
							final X509Certificate[] chain, final String authType) {
					}

					@Override
					public void checkServerTrusted(
							final X509Certificate[] chain, final String authType) {
					}

					@Override
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}
				} };

				// Install the all-trusting trust manager
				final SSLContext sslContext = SSLContext.getInstance("TLS");
				sslContext.init(null, trustAllCerts,
						new java.security.SecureRandom());
				// Create an ssl socket factory with our all-trusting manager
				final SSLSocketFactory sslSocketFactory = sslContext
						.getSocketFactory();

				URL url = new URL(
						"https://spreadsheets.google.com/feeds/list/"+sheetId.trim()+"/od6/public/basic?alt=json");
				URLConnection connection = (HttpURLConnection) url
						.openConnection();
				((HttpsURLConnection) connection)
						.setSSLSocketFactory(sslSocketFactory);

				BufferedReader in = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				sb = new StringBuffer("");
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					sb.append(inputLine);

				}

				JSONObject jsonObject = new JSONObject(sb.toString());
				stringBuilder = new StringBuilder();

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return sb.toString();

		}

	}

	public void onClickLoad(View view) {
		loadData();
	}
}

