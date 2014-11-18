package com.digdream.tasbook.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

import com.digdream.tasbook.bean.Book;

/**
 * ½âÎöxmlÎÄ¼þ¡£
 */
public class UpdateParseXmlService {
	HashMap<String, String> hashMap;
	public HashMap<String, String> parseXml(InputStream inStream)
			throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		
		/*
		 * int i = -1; //org.apache.commons.io.output.ByteArrayOutputStream
		 * ByteArrayOutputStream baos = new ByteArrayOutputStream(); try { while
		 * ((i = is.read()) != -1) { baos.write(i); } } catch (IOException e1) {
		 * // TODO Auto-generated catch block e1.printStackTrace(); } String
		 * content = baos.toString(); Log.d("tt", content);
		 */
		try {
			parser.setInput(inStream, "UTF-8");
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					hashMap = new HashMap<String, String>();

					if (parser.getName().equals("update")) {

					} else if (parser.getName().equals("version")) {
						hashMap.put("version", parser.getText());
						eventType = parser.next();
					} else if (parser.getName().equals("name")) {
						hashMap.put("name", parser.getText());
						eventType = parser.next();
					} else if (parser.getName().equals("url")) {
						hashMap.put("name", parser.getText());
						eventType = parser.next();

					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				eventType = parser.next();
			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return hashMap;
	}
}
