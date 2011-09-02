package com.mychoize.android.cricscore.app;

import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.content.Context;
import android.util.Log;

public class DataProvider {

	public List<Match> getMatches(Context c) throws DataNotFormedException,
			VersionNotSupportedException, NoMatchesRunningException,
			InvalidProcessingException {
		List<Match> matches = new ArrayList<Match>();

		Document doc = null;
		int size = 0;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			URL url = new URL(GenericProperties.BASE_URL);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			String str  = urlConnection.getHeaderField("reply-size");
			InputSource source = new InputSource(urlConnection.getInputStream());
			if(str != null){
				size = Integer.parseInt(str);
				RunningInfo.addRequest(size, c);
			}
			doc = db.parse(source);
			doc.getDocumentElement().normalize();
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataNotFormedException();
		}
		
		NodeList errorNodeList = doc.getElementsByTagName("ns");
		if (errorNodeList != null && errorNodeList.getLength() > 0) {
			Log.v(GenericProperties.TAG, "Version Not supported");
			throw new VersionNotSupportedException();
		}

		NodeList nodeList = doc.getElementsByTagName("m");
		if (nodeList == null || nodeList.getLength() == 0) {
			Log.v(GenericProperties.TAG, "No Matches running");
			throw new NoMatchesRunningException();
		}

		try {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				Match match = new Match(node.getFirstChild().getFirstChild()
						.getNodeValue(), node.getFirstChild().getNextSibling()
						.getFirstChild().getNodeValue(), Integer.parseInt(node
						.getLastChild().getFirstChild().getNodeValue()));
				matches.add(match);
			}
		} catch (Exception e) {
			throw new InvalidProcessingException();
		}

		return matches;
	}

	public SimpleScore getLiveScore(int _matchId, int _version, Context c) {

		SimpleScore simpleScore = null;
		int size = 0;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			String urlStr = GenericProperties.BASE_URL + "?id=" + _matchId
			+ "&v=" + _version;
			Log.v(GenericProperties.TAG, urlStr);
			URL url = new URL(urlStr);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			String str  = urlConnection.getHeaderField("reply-size");
			InputSource source = new InputSource(urlConnection.getInputStream());
			if(str != null){
				size = Integer.parseInt(str);
				RunningInfo.addRequest(size, c);	
			}
			
			Document doc = db.parse(source);
			doc.getDocumentElement().normalize();
			Node nodeNoUpdate = doc.getElementsByTagName("nu").item(0);
			
			if (nodeNoUpdate != null) {
				return new SimpleScore(null, null, _matchId,
						GenericProperties.INT_NO_UPDATE);
			}
			Node nodeInvalidMatch = doc.getElementsByTagName("im").item(0);
			if (nodeInvalidMatch != null) {
				return new SimpleScore(null, null, _matchId,
						GenericProperties.INT_INVALID_MATCH);
			}
			String detail = doc.getElementsByTagName("d").item(0)
					.getFirstChild().getNodeValue();
			String simple = doc.getElementsByTagName("s").item(0)
					.getFirstChild().getNodeValue();
			String version = doc.getElementsByTagName("v").item(0)
					.getFirstChild().getNodeValue();
			int ver = Integer.parseInt(version);
	
			Log.i(GenericProperties.TAG, simple + " " + detail + " " + ver);
			simpleScore = new SimpleScore(simple, detail, _matchId, ver);
			
		}catch(SocketException e){
			e.printStackTrace();
			simpleScore = new SimpleScore(null, null, _matchId,
					GenericProperties.INT_NO_INTERNET);
		}catch(UnknownHostException e){
			e.printStackTrace();
			simpleScore = new SimpleScore(null, null, _matchId,
					GenericProperties.INT_NO_INTERNET);
		}
		catch (Exception e) {
			e.printStackTrace();
			simpleScore = new SimpleScore(null, null, _matchId,
					GenericProperties.INT_ERROR);
		}
		return simpleScore;
	}
}