package com.mychoize.android.cricscore;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class DataProvider {
	public static List<Match> getMatches() {
		List<Match> matches = new ArrayList<Match>();

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			URL url = new URL("http://mychoize.com/A/CricScore/i.php");
			Document doc = db.parse(new InputSource(url.openStream()));
//			Document doc = db.parse(new InputSource("E:/Projects/CricScore/resources/list.xml"));
			doc.getDocumentElement().normalize();
			NodeList nodeList = doc.getElementsByTagName("m");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				Match match = new Match(node.getFirstChild().getFirstChild().getNodeValue(), 
						node.getFirstChild().getNextSibling().getFirstChild().getNodeValue(), 
						Integer.parseInt(node.getLastChild().getFirstChild().getNodeValue()));
				matches.add(match);
			}
		} catch (Exception e) {

		}

		return matches;
	}

	public static ScoreUpdate getLiveScore(int id) {
		ScoreUpdate scoreUpdate = new ScoreUpdate(id);

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			URL url = new URL("http://mychoize.com/A/CricScore/i.php?id="+id);
			Document doc = db.parse(new InputSource(url.openStream()));
//			Document doc = db.parse(new InputSource("E:/Projects/CricScore/resources/"+id+".xml"));
			
			doc.getDocumentElement().normalize();
			NodeList nodeList = doc.getElementsByTagName("info");
			Node node = nodeList.item(0);
			String simple = node.getFirstChild().getFirstChild().getNodeValue();
			String detail = node.getLastChild().getFirstChild().getNodeValue();
			TextProcessor.processSimpleTxt(simple, scoreUpdate);
			TextProcessor.processDetailTxt(detail, scoreUpdate);
		} catch (Exception e) {
			scoreUpdate.setError(true);
		}

		return scoreUpdate;
	}

}
