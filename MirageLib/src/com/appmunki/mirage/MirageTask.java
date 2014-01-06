package com.appmunki.mirage;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.util.Log;

import com.appmunki.mirage.MirageMultiPartEntity.ProgressListener;
import com.appmunki.mirage.Utils.METHOD;

public class MirageTask extends AsyncTask<Object, Object, Object> {

	private String api_key;
	private METHOD methodSelected;
	private long totalSize = 0;
	private String response = "";
	private Context context;
	private boolean showDialog;
	private ProgressDialog loadingDialog;

	public MirageTask(String api_key, METHOD method, Context context, boolean showDialog) {
		this.api_key = api_key;
		this.methodSelected = method;
		this.context = context;
		this.showDialog = showDialog;
		if (showDialog && context != null) {
			createDialog();
		}
	}

	public void createDialog() {
		loadingDialog = ProgressDialog.show(context, "", "Loading. Please wait...", true);
		loadingDialog.dismiss();
	}

	@Override
	protected void onPreExecute() {
		if (showDialog && loadingDialog != null)
			loadingDialog.show();
		super.onPreExecute();
	}

	@Override
	protected Object doInBackground(Object... params) {

		try {

			Utils.busy = true;
			boolean exist = false;
			switch (methodSelected) {
			case CREATE:
				exist = checkFileExist(params[0] + "");
				if (exist) {
					response = executeCreatePattern(params[0] + "", params[1] + "", params[2] + "", (UploadProgressListener) params[3]);
				}
				break;
			case EDIT:
				response = executeEdit(params[0] + "", params[1] + "", params[2] + "");
				break;
			case DELETE:
				response = executeDelete(params[0] + "");
				break;
			case MATCH:
				exist = checkFileExist(params[0] + "");
				if (exist) {
					response = executeMatch(params[0] + "", (UploadProgressListener) params[1]);
				}
				break;
			default:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.v("MIRAGE", response);

		return response;
	}

	@Override
	protected void onPostExecute(Object result) {
		Utils.busy = false;
		if (showDialog && loadingDialog != null)
			loadingDialog.dismiss();
		showResult((String) result);
		super.onPostExecute(result);
	}

	public String executeCreatePattern(String imagePath, String name, String description, final UploadProgressListener listener) throws Exception {

		if (imagePath != null && name != null && description != null) {

			Bitmap bm = BitmapFactory.decodeFile(imagePath);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bm.compress(CompressFormat.JPEG, 75, bos);
			byte[] data = bos.toByteArray();
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(Utils.URL + "patterns.json");

			postRequest.addHeader("accept", "application/json");

			ByteArrayBody bab = new ByteArrayBody(data, "newPattern.jpg");

			MirageMultiPartEntity multipartContent = new MirageMultiPartEntity(new ProgressListener() {
				@Override
				public void transferred(long num) {
					int progress = ((int) ((num / (float) totalSize) * 100));
					listener.progress(progress);
				}
			});

			multipartContent.addPart("r_image", bab);
			multipartContent.addPart("r_image_cache", new StringBody(""));
			multipartContent.addPart("_name", new StringBody(name));
			multipartContent.addPart("_description", new StringBody(description));
			multipartContent.addPart("api_key", new StringBody(api_key));

			totalSize = multipartContent.getContentLength();
			Log.v("MIRAGE", "TAMANO TOTAL " + totalSize);

			postRequest.setEntity(multipartContent);
			HttpResponse response = httpClient.execute(postRequest);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			String sResponse;
			StringBuilder s = new StringBuilder();

			while ((sResponse = reader.readLine()) != null) {
				s = s.append(sResponse);
			}
			Log.v("MIRAGE", "Response: " + s);
			return s.toString();
		} else {
			return "Parameters incomplete";
		}

	}

	public String executeMatch(String imagePath, final UploadProgressListener listener) throws Exception {

		Bitmap bm = BitmapFactory.decodeFile(imagePath);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bm.compress(CompressFormat.JPEG, 75, bos);
		byte[] data = bos.toByteArray();
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost(Utils.URL + "uploads.json");

		postRequest.addHeader("accept", "application/json");

		ByteArrayBody bab = new ByteArrayBody(data, "test.jpg");

		MirageMultiPartEntity multipartContent = new MirageMultiPartEntity(new ProgressListener() {
			@Override
			public void transferred(long num) {
				int progress = ((int) ((num / (float) totalSize) * 100));
				listener.progress(progress);
			}
		});

		multipartContent.addPart("upload", bab);
		multipartContent.addPart("api_key", new StringBody(api_key));
		totalSize = multipartContent.getContentLength();

		postRequest.setEntity(multipartContent);
		HttpResponse response = httpClient.execute(postRequest);
		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		String sResponse;
		StringBuilder s = new StringBuilder();

		while ((sResponse = reader.readLine()) != null) {
			s = s.append(sResponse);
		}
		Log.v("MIRAGE", "Response: " + s);
		return s.toString();

	}

	public String executeDelete(String id) throws Exception {

		if (id != null) {

			HttpClient httpClient = new DefaultHttpClient();
			HttpDelete deleteRequest = new HttpDelete(Utils.URL + "patterns/" + id + ".json");

			// deleteRequest.addHeader("content-type", "application/json");

			HttpResponse response = httpClient.execute(deleteRequest);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			String sResponse;
			StringBuilder s = new StringBuilder();

			while ((sResponse = reader.readLine()) != null) {
				s = s.append(sResponse);
			}
			Log.v("MIRAGE", "Response: " + s);
			return s.toString();
		} else {
			return null;
		}

	}

	public String executeEdit(String id, String name, String description) throws Exception {

		if (id != null) {

			URL url = new URL(Utils.URL + "patterns/" + id + ".json");

			// --This code works for updating a record from the feed--
			HttpPut httpPut = new HttpPut(url.toString());

			httpPut.addHeader("content-type", "application/json");

			JSONObject json = new JSONObject();
			if (name != null && !name.equals("")) {
				json.put("_name", name);
			}

			if (description != null && !description.equals("")) {
				json.put("_description", description);
			}

			json.put("api_key", api_key);

			StringEntity entity = new StringEntity(json.toString());
			entity.setContentType("application/json;charset=UTF-8");// text/plain;charset=UTF-8
			entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
			httpPut.setEntity(entity);

			// Send request to WCF service
			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpResponse response = httpClient.execute(httpPut);
			// HttpEntity entity1 = response.getEntity();

			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			String sResponse;
			StringBuilder s = new StringBuilder();

			while ((sResponse = reader.readLine()) != null) {
				s = s.append(sResponse);
			}
			Log.v("MIRAGE", "Response json: " + s);
			return s.toString();

		} else {
			return null;
		}

	}

	public void showResult(String message) {
		JSONObject jsonResponse = null;
		String messageToShow = "The response can not be parsed";
		try {
			jsonResponse = new JSONObject(message);
			if (jsonResponse.has("Response")) {
				messageToShow = jsonResponse.getString("Response");
			} else {
				messageToShow = "Error: "+jsonResponse.getString("error");
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		if (context != null) {
			new AlertDialog.Builder(context).setTitle("Mirage Response").setMessage(messageToShow).setPositiveButton("Close", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// continue with delete
				}
			}).show();
		}

	}

	private boolean checkFileExist(String pathFile) {
		try {
			File file = new File(pathFile);
			if (file.exists()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
