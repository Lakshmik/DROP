
package org.drip.function.r1tor1;

import org.drip.function.definition.R1ToR1;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2030 Lakshmi Krishnamurthy
 * Copyright (C) 2029 Lakshmi Krishnamurthy
 * Copyright (C) 2028 Lakshmi Krishnamurthy
 * Copyright (C) 2027 Lakshmi Krishnamurthy
 * Copyright (C) 2026 Lakshmi Krishnamurthy
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
 * <i>FunctionClassSupremum</i> implements the Univariate Function that corresponds to the Supremum among the
 *  specified Class of Functions. It exposes the following Functions:
 *
 *  <ul>
 * 		<li><i>FunctionClassSupremum</i> Constructor</li>
 * 		<li>Retrieve the Class of Functions</li>
 * 		<li>Retrieve the Supremum Function corresponding to the specified Variate</li>
 * 		<li>Evaluate for the given variate</li>
 * 		<li>Calculate the derivative as a double</li>
 * 		<li>Integrate over the given range</li>
 *  </ul>
 * 
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/function/README.md">R<sup>d</sup> To R<sup>d</sup> Function Analysis</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/function/r1tor1/README.md">Built-in R<sup>1</sup> To R<sup>1</sup> Functions</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class FunctionClassSupremum
	extends R1ToR1
{
	class IndexSupremum
	{
		int _index = -1;
		double _supremum = Double.NaN;

		IndexSupremum (
			final int index,
			final double supremum)
		{
			_index = index;
			_supremum = supremum;
		}
	};

	private R1ToR1[] _r1ToR1Array = null;

	private IndexSupremum supremum (
		final double variate)
	{
		int supremumIndex = 0;

		try {
			double supremum = _r1ToR1Array[0].evaluate (variate);

			for (int functionIndex = 1; functionIndex < _r1ToR1Array.length; ++functionIndex) {
				double value = _r1ToR1Array[functionIndex].evaluate (variate);

				if (value > supremum) {
					supremumIndex = functionIndex;
					supremum = value;
				}
			}

			return new IndexSupremum (supremumIndex, supremum);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * <i>FunctionClassSupremum</i> Constructor
	 * 
	 * @param r1ToR1Array Array of Functions in the Class
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public FunctionClassSupremum (
		final R1ToR1[] r1ToR1Array)
		throws Exception
	{
		super (null);

		if (null == (_r1ToR1Array = r1ToR1Array) || 0 == _r1ToR1Array.length) {
			throw new Exception ("FunctionClassSupremum Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Class of Functions
	 * 
	 * @return The Class of Functions
	 */

	public R1ToR1[] r1ToR1Array()
	{
		return _r1ToR1Array;
	}

	/**
	 * Retrieve the Supremum Function corresponding to the specified Variate
	 * 
	 * @param variate The Variate
	 * 
	 * @return The Supremum Function corresponding to the specified Variate
	 */

	public R1ToR1 supremumFunction (
		final double variate)
	{
		IndexSupremum indexSupremum = supremum (variate);

		return null == indexSupremum ? null : _r1ToR1Array[indexSupremum._index];
	}

	/**
	 * Evaluate for the given variate
	 * 
	 * @param variate Variate
	 *  
	 * @return Returns the calculated value
	 * 
	 * @throws Exception Thrown if evaluation cannot be done
	 */

	@Override public double evaluate (
		final double variate)
		throws Exception
	{
		IndexSupremum indexSupremum = supremum (variate);

		if (null == indexSupremum) {
			throw new Exception ("FunctionClassSupremum::evaluate => Invalid Inputs");
		}

		return indexSupremum._supremum;
	}

	/**
	 * Calculate the derivative as a double
	 * 
	 * @param variate Variate at which the derivative is to be calculated
	 * @param order Order of the derivative to be computed
	 * 
	 * @return The Derivative
	 * 
	 * @throws Exception Thrown if Inputs are Invalid
	 */

	@Override public double derivative (
		final double variate,
		final int order)
		throws Exception
	{
		IndexSupremum indexSupremum = supremum (variate);

		if (null == indexSupremum) {
			throw new Exception ("FunctionClassSupremum::derivative => Invalid Inputs");
		}

		return _r1ToR1Array[indexSupremum._index].derivative (variate, order);
	}

	/**
	 * Integrate over the given range
	 * 
	 * @param begin Range Begin 
	 * @param end Range End 
	 *  
	 * @return The Integrated Value
	 * 
	 * @throws Exception Thrown if evaluation cannot be done
	 */

	@Override public double integrate (
		final double begin,
		final double end)
		throws Exception
	{
		IndexSupremum indexSupremum = supremum (0.5 * (begin + end));

		if (null == indexSupremum) {
			throw new Exception ("FunctionClassSupremum::integrate => Invalid Inputs");
		}

		return _r1ToR1Array[indexSupremum._index].integrate (begin, end);
	}
}
