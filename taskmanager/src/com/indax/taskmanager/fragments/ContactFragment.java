package com.indax.taskmanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.indax.taskmanager.R;

public class ContactFragment extends SherlockFragment {

	private String mContact;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_contact, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (mContact != null) {
			updateContact();
		}
	}
	
	private void updateContact() {
		TextView txt_name = (TextView) getActivity().findViewById(R.id.txt_name);
		txt_name.setText(mContact);						
	}
	
	public void setContact(String name) {
		mContact = name;
		updateContact();
	}
}
