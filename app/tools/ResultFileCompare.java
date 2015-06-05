package tools;

import java.util.Comparator;

import Model.ResultFile;

public class ResultFileCompare implements Comparator<ResultFile> {

	@Override
	public int compare(ResultFile o1, ResultFile o2) {
		// TODO Auto-generated method stub
		if(o1.rate == o2.rate)
			return 0;
		else if(o1.rate > o2.rate)
			return -1;
		else if(o2.rate < o2.rate)
			return 1;
		return 0;
	}

}
