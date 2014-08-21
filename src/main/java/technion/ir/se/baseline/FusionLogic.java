package technion.ir.se.baseline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import technion.ir.se.dao.ResultFormat;

public class FusionLogic {
	private static final int MAX_RESULTS = 5;

	/**
	 * @param resultFormat
	 *            - for only 1 queryID variation return normalised ResultFormat
	 *            for each queryID
	 */
	private void normalizeMaxMin(List<ResultFormat> resultFormat) {
		if (!resultFormat.isEmpty()) {
			double maxScore = resultFormat.get(0).getScore();
			double minScore = resultFormat.get(resultFormat.size() - 1).getScore();
			double normalisedScore;
			//prevent NaN option
			if (resultFormat.size() > 1)
			{
				for (ResultFormat resFormat : resultFormat) {
					normalisedScore = (resFormat.getScore() - minScore)/ (maxScore - minScore);
					resFormat.setScore(normalisedScore);
				}
			}
		}
	}

	public List<ResultFormat> mergeResults(
			List<List<ResultFormat>> queryVariantsResults) {
		List<ResultFormat> mergedResult = new ArrayList<ResultFormat>();

		// size = 0 || 1 - no merge is needed
		// There aren't any results for any of the original query alternatives
		if (queryVariantsResults.size() == 0)
			return null;
		// Only one alternative of the original query has results
		if (queryVariantsResults.size() == 1)
			return queryVariantsResults.get(0);

		for (List<ResultFormat> resFormatList : queryVariantsResults) {
			normalizeMaxMin(resFormatList);
		}

		// size > 1
		// More than query alternatives has results
		// first element in the HashMap starts with 1 as key
		for (ResultFormat resFormat : queryVariantsResults.get(0)) {
			mergedResult.add(resFormat);
		}
		// handling to remain Lists;
		for (int i = 1; i < queryVariantsResults.size(); i++) {
			for (ResultFormat resFormat : queryVariantsResults.get(i)) {
				boolean isExist = false;
				for (int iMerged = 0; iMerged < mergedResult.size(); iMerged++) {
					if (checkDocumentIDEquivalence(mergedResult, resFormat,
							iMerged)) {
						isExist = true;
						double newScore = resFormat.getScore()
								+ mergedResult.get(iMerged).getScore();
						mergedResult.get(iMerged).setScore(newScore);
						break;
					}
				}
				if (!isExist)// if not exist add new row
				{
					mergedResult.add(resFormat);// the queryId must be the same
												// for all variants
				}
			}
		}

		mergedResult = SortAndTrimResultsSize(mergedResult);
		mergedResult = ArrangeRankOrder(mergedResult);
		return mergedResult;
	}

	private boolean checkDocumentIDEquivalence(List<ResultFormat> mergedResult,
			ResultFormat resFormat, int iMerged) {
		return resFormat.getDocumentID().equals(
				mergedResult.get(iMerged).getDocumentID());
	}

	private List<ResultFormat> SortAndTrimResultsSize(
			List<ResultFormat> mergedResult) {
		Collections.sort(mergedResult);
		Collections.reverse(mergedResult);
		if (mergedResult.size() > MAX_RESULTS) {
			mergedResult = mergedResult.subList(0, MAX_RESULTS);
		}
		return mergedResult;
	}

	private List<ResultFormat> ArrangeRankOrder(List<ResultFormat> mergedResult) {
		for (int i = 1; i <= mergedResult.size(); i++) {
			mergedResult.get(i - 1).setRank(i);
		}

		return mergedResult;
	}
}
