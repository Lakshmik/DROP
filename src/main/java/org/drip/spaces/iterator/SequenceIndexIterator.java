
package org.drip.spaces.iterator;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2025 Lakshmi Krishnamurthy
 * Copyright (C) 2024 Lakshmi Krishnamurthy
 * Copyright (C) 2023 Lakshmi Krishnamurthy
 * Copyright (C) 2022 Lakshmi Krishnamurthy
 * Copyright (C) 2021 Lakshmi Krishnamurthy
 * Copyright (C) 2020 Lakshmi Krishnamurthy
 * Copyright (C) 2019 Lakshmi Krishnamurthy
 * Copyright (C) 2018 Lakshmi Krishnamurthy
 * Copyright (C) 2017 Lakshmi Krishnamurthy
 * Copyright (C) 2016 Lakshmi Krishnamurthy
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * 
 *  This file is part of DROP, an open-source library targeting analytics/risk, transaction cost analytics,
 *  	asset liability management analytics, capital, exposure, and margin analytics, valuation adjustment
 *  	analytics, and portfolio construction analytics within and across fixed income, credit, commodity,
 *  	equity, FX, and structured products. It also includes auxiliary libraries for algorithm support,
 *  	numerical analysis, numerical optimization, spline builder, model validation, statistical learning,
 *  	graph builder/navigator, and computational support.
 *  
 *  	https://lakshmidrip.github.io/DROP/
 *  
 *  DROP is composed of three modules:
 *  
 *  - DROP Product Core - https://lakshmidrip.github.io/DROP-Product-Core/
 *  - DROP Portfolio Core - https://lakshmidrip.github.io/DROP-Portfolio-Core/
 *  - DROP Computational Core - https://lakshmidrip.github.io/DROP-Computational-Core/
 * 
 * 	DROP Product Core implements libraries for the following:
 * 	- Fixed Income Analytics
 * 	- Loan Analytics
 * 	- Transaction Cost Analytics
 * 
 * 	DROP Portfolio Core implements libraries for the following:
 * 	- Asset Allocation Analytics
 *  - Asset Liability Management Analytics
 * 	- Capital Estimation Analytics
 * 	- Exposure Analytics
 * 	- Margin Analytics
 * 	- XVA Analytics
 * 
 * 	DROP Computational Core implements libraries for the following:
 * 	- Algorithm Support
 * 	- Computation Support
 * 	- Function Analysis
 *  - Graph Algorithm
 *  - Model Validation
 * 	- Numerical Analysis
 * 	- Numerical Optimizer
 * 	- Spline Builder
 *  - Statistical Learning
 * 
 * 	Documentation for DROP is Spread Over:
 * 
 * 	- Main                     => https://lakshmidrip.github.io/DROP/
 * 	- Wiki                     => https://github.com/lakshmiDRIP/DROP/wiki
 * 	- GitHub                   => https://github.com/lakshmiDRIP/DROP
 * 	- Repo Layout Taxonomy     => https://github.com/lakshmiDRIP/DROP/blob/master/Taxonomy.md
 * 	- Javadoc                  => https://lakshmidrip.github.io/DROP/Javadoc/index.html
 * 	- Technical Specifications => https://github.com/lakshmiDRIP/DROP/tree/master/Docs/Internal
 * 	- Release Versions         => https://lakshmidrip.github.io/DROP/version.html
 * 	- Community Credits        => https://lakshmidrip.github.io/DROP/credits.html
 * 	- Issues Catalog           => https://github.com/lakshmiDRIP/DROP/issues
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   	you may not use this file except in compliance with the License.
 *   
 *  You may obtain a copy of the License at
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  	distributed under the License is distributed on an "AS IS" BASIS,
 *  	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  
 *  See the License for the specific language governing permissions and
 *  	limitations under the License.
 */

/**
 * <i>SequenceIndexIterator</i> contains the Functionality to iterate through a List of Sequence Indexes.
 *
 *  It provides the following Functionality:
 *
 *  <ul>
 * 		<li>Create a Standard Sequence/Index Iterator</li>
 * 		<li><i>SequenceIndexIterator</i> Constructor</li>
 * 		<li>Retrieve the First Cursor</li>
 * 		<li>Retrieve the Next Cursor</li>
 * 		<li>Retrieve the Size of the Iterator</li>
 *  </ul>
 *
 *  <br>
 *  <style>table, td, th {
 *  	padding: 1px; border: 2px solid #008000; border-radius: 8px; background-color: #dfff00;
 *		text-align: center; color:  #0000ff;
 *  }
 *  </style>
 *  
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/StatisticalLearningLibrary.md">Statistical Learning Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/spaces/README.md">R<sup>1</sup> and R<sup>d</sup> Vector/Tensor Spaces (Validated and/or Normed), and Function Classes</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/spaces/iterator/README.md">Iterative/Exhaustive Vector Space Scanners</a></td></tr>
 *  </table>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class SequenceIndexIterator
{
	private boolean _cycle = false;
	private int _sequenceIndexCursor = -1;
	private int[] _sequenceIndexArray = null;
	private int[] _entriesPerSequenceArray = null;

	/**
	 * Create a Standard Sequence/Index Iterator
	 * 
	 * @param sequenceCount Number Variable Sequences
	 * @param indexPerVariableSequence Number of Indexes per Variable Sequence
	 * 
	 * @return The Sequence/Index Iterator Instance
	 */

	public static final SequenceIndexIterator Standard (
		final int sequenceCount,
		final int indexPerVariableSequence)
	{
		if (0 >= sequenceCount || 0 >= indexPerVariableSequence) {
			return null;
		}

		int[] maximumEntriesPerIndexArray = new int[sequenceCount];

		for (int sequenceIndex = 0; sequenceIndex < sequenceCount; ++sequenceIndex) {
			maximumEntriesPerIndexArray[sequenceIndex] = indexPerVariableSequence - 1;
		}

		try {
			return new SequenceIndexIterator (maximumEntriesPerIndexArray, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * <i>SequenceIndexIterator</i> Constructor
	 * 
	 * @param entriesPerSequenceArray Maximum Entries per Sequence
	 * @param cycle TRUE - Cycle around the Index Entries
	 * 
	 * @throws Exception Thrown if Inputs are invalid
	 */

	public SequenceIndexIterator (
		final int[] entriesPerSequenceArray,
		final boolean cycle)
		throws Exception
	{
		if (null == (_entriesPerSequenceArray = entriesPerSequenceArray) ||
			0 == _entriesPerSequenceArray.length)
		{
			throw new Exception ("SequenceIndexIterator ctr => Invalid Inputs");
		}

		_cycle = cycle;
		_sequenceIndexCursor = 0;
		_sequenceIndexArray = new int[_entriesPerSequenceArray.length];

		for (int sequenceIndex = 0; sequenceIndex < _entriesPerSequenceArray.length; ++sequenceIndex) {
			if (0 >= _entriesPerSequenceArray[sequenceIndex]) {
				throw new Exception ("SequenceIndexIterator ctr => Invalid Inputs");
			}

			_sequenceIndexArray[sequenceIndex] = 0;
		}
	}

	/**
	 * Retrieve the Dimension of the Sequences
	 * 
	 * @return Dimension of the Sequences
	 */

	public int dimension()
	{
		return _entriesPerSequenceArray.length;
	}

	/**
	 * Retrieve the First Cursor
	 * 
	 * @return The First Cursor
	 */

	public int[] first()
	{
		_sequenceIndexCursor = 0;

		for (int sequenceIndex = 0; sequenceIndex < _entriesPerSequenceArray.length; ++sequenceIndex) {
			_sequenceIndexArray[sequenceIndex] = 0;
		}

		return _sequenceIndexArray;
	}

	/**
	 * Retrieve the Next Cursor
	 * 
	 * @return The Next Cursor
	 */

	public int[] next()
	{
		if (++_sequenceIndexCursor <= _entriesPerSequenceArray[_entriesPerSequenceArray.length - 1]) {
			_sequenceIndexArray[_entriesPerSequenceArray.length - 1] = _sequenceIndexCursor;
			return _sequenceIndexArray;
		}

		_sequenceIndexCursor = 0;
		int sequenceIndex = _entriesPerSequenceArray.length - 2;

		while (sequenceIndex >= 0 &&
			_sequenceIndexArray[sequenceIndex] >= _entriesPerSequenceArray[sequenceIndex])
		{
			--sequenceIndex;
		}

		if (0 <= sequenceIndex) {
			_sequenceIndexArray[sequenceIndex] = _sequenceIndexArray[sequenceIndex] + 1;

			for (int i = sequenceIndex + 1; i < _entriesPerSequenceArray.length; ++i) {
				_sequenceIndexArray[i] = 0;
			}

			return _sequenceIndexArray;
		}

		return _cycle ? first() : null;
	}

	/**
	 * Retrieve the Size of the Iterator
	 * 
	 * @return Size of the Iterator
	 */

	public int size()
	{
		int size = 0;
		int sequenceCount = _entriesPerSequenceArray.length;

		for (int sequenceIndex = 0; sequenceIndex < sequenceCount; ++sequenceIndex) {
			size += _entriesPerSequenceArray[sequenceIndex] + 1;
		}

		return size;
	}
}
