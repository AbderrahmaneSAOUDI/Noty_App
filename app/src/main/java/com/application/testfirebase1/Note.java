package com.application.testfirebase1;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Note {
	private String id = null, title = null, text = null, date = null;

	public Note () {
	}

	public Note (String _id, String _title, String _text) {
		this.id = _id;
		this.title = _title;
		this.date = getCurrentDate ();
		this.text = _text;
	}

	public String getId () {
		return id;
	}

	public String getTitle () {
		return title;
	}

	public String getDate () {
		return date;
	}

	public String getText () {
		return text;
	}

	private String getCurrentDate () {
		Date date = Calendar.getInstance ().getTime ();
		@SuppressLint ("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat ("dd/MM/yyyy [HH:mm]");
		return dateFormat.format (date);
	}
}