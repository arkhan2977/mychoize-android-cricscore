package com.mychoize.android.cricscore.app;

public class DataConvertor {
	public static ScoreUpdate getScoreUpdate(SimpleScore _score) {
		if (_score == null) {
			return null;
		}
		ScoreUpdate scoreUpdate = new ScoreUpdate(_score.getId(),
				_score.getTimestamp(),_score.getVersion());
		try{
			TextProcessor.processSimpleTxt(_score.getSimple(), scoreUpdate);
			TextProcessor.processDetailTxt(_score.getDetail(), scoreUpdate);	
		}catch(Exception e){
			e.printStackTrace();
			scoreUpdate.setError(true);
		}
		return scoreUpdate;
	}
}