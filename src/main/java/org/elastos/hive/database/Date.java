package org.elastos.hive.database;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class Date extends CustomSerializedObject {
	private java.util.Date date;
	private static final String DATE_FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	private static final SimpleDateFormat isoDateFormat =
			new SimpleDateFormat(DATE_FORMAT_ISO_8601);

	public Date() {
		this(Calendar.getInstance());
	}

	public Date(java.util.Date date) {
		this.date = date;
	}

	public Date(Calendar cal) {
		this.date = cal.getTime();
	}

	public java.util.Date getDate() {
		return date;
	}

	@Override
	public void serialize(JsonGenerator gen, SerializerProvider provider)
			throws IOException {
		gen.writeStartObject();
		gen.writeFieldName("$data");
		if (date == null)
			gen.writeNull();
		else
			gen.writeString(isoDateFormat.format(date));
		gen.writeEndObject();
	}
}
