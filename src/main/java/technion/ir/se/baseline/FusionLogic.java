package technion.ir.se.baseline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import technion.ir.se.dao.ResultFormat;


public class FusionLogic
	{
		
		/**
		 * @param resultFormat - for only 1 queryID variation
		 * return normalised ResultFormat for each queryID
		 */
		public void NormalizeMaxMin(List<ResultFormat> resultFormat) 
		{
			double maxScore = resultFormat.get(0).getScore();
			double minScore = resultFormat.get(resultFormat.size() - 1).getScore();
			double normalisedScore;
			
			for (ResultFormat resFormat : resultFormat) 
			{
				normalisedScore = (resFormat.getScore() - minScore) / (maxScore - minScore);
				resFormat.setScore(normalisedScore);
			}
		}
		
		public List<ResultFormat> MergeResults(HashMap<Integer, List<ResultFormat>> queryVariantsResults)
		{
			List<ResultFormat> mergedResult = new ArrayList<ResultFormat>();
			
			// size = 0 || 1 - no merge is needed
			if (queryVariantsResults.size() == 0) return null;
			if (queryVariantsResults.size() == 1) return queryVariantsResults.get(0);
			
			//size > 1
			for (ResultFormat resFormat : queryVariantsResults.get(0))
			{
				mergedResult.add(resFormat);
			}
			//handling to remain Lists;
			for (int i = 1; i < queryVariantsResults.size(); i++) 
			{
				for (ResultFormat resFormat : queryVariantsResults.get(i)) 
				{
					boolean isExist = false;
					for (int iMerged = 0; iMerged < mergedResult.size(); iMerged++) 
					{
						if (resFormat.getDocumentID() == mergedResult.get(iMerged).getDocumentID())
						{
							isExist = true;
							double newScore = resFormat.getScore() + mergedResult.get(iMerged).getScore();
							mergedResult.get(iMerged).setScore(newScore);
							break;
						}
					}
					if (!isExist)//if not exist add new row
					{
						mergedResult.add(resFormat);//the queryId must be the same for all variants
					}
				}
			}
			
			Collections.sort(mergedResult);
			return mergedResult;
				
		}
		
		
	}
