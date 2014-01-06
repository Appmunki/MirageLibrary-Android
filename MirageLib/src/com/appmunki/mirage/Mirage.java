package com.appmunki.mirage;

import android.content.Context;
import android.widget.Toast;

import com.appmunki.mirage.Utils.METHOD;

public class Mirage {

	private String api_key = "";
	private Context context;
	private boolean showDialog;

	public Mirage(String api_key) {
		this.api_key = api_key;
		context = null;
		showDialog = false;
	}

	public Mirage(String api_key, Context context, boolean showDialog) {
		this.api_key = api_key;
		this.context = context;
		this.showDialog = showDialog;
	}

	public void createPattern(String imagePath, String name, String description, UploadProgressListener listener) {
		if (!Utils.busy)
			new MirageTask(api_key, METHOD.CREATE,context,showDialog).execute(new Object[] { imagePath, name, description, listener });
	}

	public void editPattern(String id, String name, String description) {
		if(id==null){
			Toast.makeText(context, "The ID cann't be empty or be null", Toast.LENGTH_SHORT).show();
		}
		if(name==null){
			name = "";
		}
		if(description==null){
			description = "";
		}
		if (!Utils.busy)
			new MirageTask(api_key, METHOD.EDIT,context,showDialog).execute(new Object[] { id, name, description });
	}

	public void deletePattern(String id) {
		if (!Utils.busy)
			new MirageTask(api_key, METHOD.DELETE,context,showDialog).execute(new Object[] { id, api_key });
	}

	public void doMatch(String imagePath, UploadProgressListener listener) {
		if (!Utils.busy)
			new MirageTask(api_key, METHOD.MATCH,context,showDialog).execute(new Object[] { imagePath, listener });
	}

}
