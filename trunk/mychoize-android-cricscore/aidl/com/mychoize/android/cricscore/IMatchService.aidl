package com.mychoize.android.cricscore;

import com.mychoize.android.cricscore.Match;
import com.mychoize.android.cricscore.ScoreUpdate;

import java.util.List;

interface IMatchService {
	List<Match> getMatches();
	ScoreUpdate getScore(int id);        
}