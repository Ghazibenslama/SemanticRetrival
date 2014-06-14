package technion.ir.se.baseline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import technion.ir.se.dao.ResultFormat;


public class FusionLogic
	{
		
		/**
		 * @param resultFormat - for only 1 queryID variation
		 * return normalised ResultFormat for each queryID
		 */
		private void normalizeMaxMin(List<ResultFormat> resultFormat) {
			if (!resultFormat.isEmpty()) {
				double maxScore = resultFormat.get(0).getScore();
				double minScore = resultFormat.get(resultFormat.size() - 1).getScore();
				double normalisedScore;
				
				for (ResultFormat resFormat : resultFormat) {
					normalisedScore = (resFormat.getScore() - minScore) / (maxScore - minScore);
					resFormat.setScore(normalisedScore);
				}
			}
		}
		
		public List<ResultFormat> mergeResults(List<List<ResultFormat>> queryVariantsResults)
		{
			List<ResultFormat> mergedResult = new ArrayList<ResultFormat>();
			
			// size = 0 || 1 - no merge is needed
			if (queryVariantsResults.size() == 0) return null;
			if (queryVariantsResults.size() == 1) return queryVariantsResults.get(0);
			
			for (List<ResultFormat> resFormatList : queryVariantsResults)
			{
				normalizeMaxMin(resFormatList);
			}
			
			//size > 1
			//first element in the HashMap starts with 1 as key
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
			if (mergedResult.size() > 1000) {
				mergedResult = mergedResult.subList(0, 1000);
			}
			return mergedResult;
		}
	}
