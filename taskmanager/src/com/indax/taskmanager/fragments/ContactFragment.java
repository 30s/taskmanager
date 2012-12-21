package com.indax.taskmanager.fragments;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.indax.taskmanager.R;
import com.indax.taskmanager.api.ITaskManagerAPI;
import com.indax.taskmanager.api.TaskManagerAPI;
import com.indax.taskmanager.models.Contact;

public class ContactFragment extends SherlockFragment implements
		OnClickListener {

	private String mContact;
	private ITaskManagerAPI api_client;
	private EditText edit_name_en;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_contact, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		api_client = TaskManagerAPI.getInstance(getActivity()
				.getApplicationContext());
		Button btn_save = (Button) getActivity().findViewById(R.id.btn_save);
		btn_save.setOnClickListener(this);
		edit_name_en = (EditText) getActivity().findViewById(
				R.id.edit_name_en);		
		if (mContact != null) {
			updateContact();
		}
	}

	private void updateContact() {
		TextView txt_name = (TextView) getActivity()
				.findViewById(R.id.txt_name);
		txt_name.setText(mContact);
		new GetContactTask().execute(mContact);
	}

	public void setContact(String name) {
		mContact = name;
		updateContact();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_save) {
			if (edit_name_en.getText().length() != 0) {
				new ContactInsertTask().execute(mContact, edit_name_en
						.getText().toString());
			} else {
				new ContactInsertTask().execute(mContact);
			}
		}
	}

	private class ContactInsertTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			JSONObject ret = null;
			try {
				if (params.length == 1) {
					ret = api_client.contact_insert(params[0], null, null);
				} else if (params.length == 2) {
					ret = api_client.contact_insert(params[0], params[1], null);
				}
				if (ret != null && ret.has("status")) {
					return true;
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			Toast.makeText(getActivity().getApplicationContext(),
					result ? "Contact saved!" : "Insert contact failed!",
					Toast.LENGTH_SHORT).show();
		}
	}

	private class GetContactTask extends AsyncTask<String, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... params) {
			JSONObject ret = null;
			try {
				ret = api_client.contact(mContact, null);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return ret;
		}
	
		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			if ( result == null ) {
				Toast.makeText(getActivity().getApplicationContext(),
						"Get contact failed!",
						Toast.LENGTH_SHORT).show();
				return;
			}
			JSONArray jContacts;
			try {
				jContacts = result.getJSONArray("objects");
				Contact contact = new Contact(mContact, jContacts.getJSONObject(0));
				if (contact.getNameEn() != null) {
					edit_name_en.setText(contact.getNameEn());
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
