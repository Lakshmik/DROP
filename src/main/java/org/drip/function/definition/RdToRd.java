
package org.drip.function.definition;

import org.drip.numerical.common.NumberUtil;
import org.drip.numerical.differentiation.DerivativeControl;
import org.drip.numerical.differentiation.Differential;

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
 * <i>RdToRd</i> provides the evaluation of the R<sup>d</sup> To R<sup>d</sup> objective function and its
 * 	derivatives for a specified set of R<sup>d</sup> variates. Default implementation of the derivatives are
 * 	for non-analytical black box objective functions. It provides the following Functions:
 *
 *  <ul>
 * 		<li>Evaluate for the given Input R<sup>d</sup> Variates</li>
 * 		<li>Calculate the Array of Differentials</li>
 * 		<li>Calculate the Derivative Array as a Double</li>
 * 		<li>Integrate over the given Input Range Using Uniform Monte-Carlo</li>
 *  </ul>
 * 
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/function/README.md">R<sup>d</sup> To R<sup>d</sup> Function Analysis</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/function/definition/README.md">Function Implementation Ancillary Support Objects</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public abstract class RdToRd
{
	private static final int QUADRATURE_SAMPLING = 10000;

	protected DerivativeControl _derivativeControl = null;

	protected RdToRd (
		final DerivativeControl derivativeControl)
	{
		if (null == (_derivativeControl = derivativeControl)) {
			_derivativeControl = new DerivativeControl();
		}
	}

	/**
	 * Evaluate for the given Input R<sup>d</sup> Variates
	 * 
	 * @param variateArray Array of Input R<sup>d</sup> Variates
	 *  
	 * @return The Output R<sup>d</sup> Variates
	 */

	public abstract double[] evaluate (
		final double[] variateArray
	);

	/**
	 * Calculate the Array of Differentials
	 * 
	 * @param variateArray Variate Array at which the derivative is to be calculated
	 * @param derivativeVariateIndex Index of the Variate whose Derivative is to be computed
	 * @param order Order of the derivative to be computed
	 * 
	 * @return The Array of Differentials
	 */

	public Differential[] differential (
		final double[] variateArray,
		final int derivativeVariateIndex,
		final int order)
	{
		if (null == variateArray || !NumberUtil.IsValid (variateArray) ||
			0 >= order ||
			variateArray.length <= derivativeVariateIndex)
		{
			return null;
		}

		int valueDimension = -1;
		double[] derivativeArray = null;
		double orderedVariateInfinitesimal = 1.;
		double variateInfinitesimal = Double.NaN;

		try {
			variateInfinitesimal =
				_derivativeControl.getVariateInfinitesimal (variateArray[derivativeVariateIndex]);
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}

		for (int orderOrdinal = 0; orderOrdinal <= order; ++orderOrdinal) {
			if (0 != orderOrdinal) {
				orderedVariateInfinitesimal *= (2. * variateInfinitesimal);
			}

			double[] incrementalVariateArray = new double[variateArray.length];

			for (int variateIndex = 0; variateIndex < variateArray.length; ++variateIndex) {
				incrementalVariateArray[variateIndex] = variateIndex == derivativeVariateIndex ?
					variateArray[variateIndex] + variateInfinitesimal * (order - 2. * orderOrdinal) :
					variateArray[variateIndex];
			}

			double[] valueArray = evaluate (incrementalVariateArray);

			if (null == valueArray || 0 == (valueDimension = valueArray.length)) {
				return null;
			}

			if (null == derivativeArray) {
				derivativeArray = new double[valueDimension];

				for (int valueIndex = 0; valueIndex < valueDimension; ++valueIndex) {
					derivativeArray[valueIndex] = 0.;
				}
			}

			try {
				for (int valueIndex = 0; valueIndex < valueDimension; ++valueIndex) {
					derivativeArray[valueIndex] += (orderOrdinal % 2 == 0 ? 1 : -1) * NumberUtil.NCK
						(order, orderOrdinal) * valueArray[valueIndex];
				}
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		Differential[] differentialArray = new Differential[valueDimension];

		try {
			for (int valueIndex = 0; valueIndex < valueDimension; ++valueIndex) {
				differentialArray[valueIndex] =
					new Differential (orderedVariateInfinitesimal, derivativeArray[valueIndex]);
			}
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}

		return differentialArray;
	}

	/**
	 * Calculate the Derivative Array as a Double
	 * 
	 * @param variateArray Variate Array at which the derivative is to be calculated
	 * @param derivativeVariateIndex Index of the Variate whose Derivative is to be computed
	 * @param order Order of the derivative to be computed
	 * 
	 * @return The Derivative Array
	 */

	public double[] derivative (
		final double[] variateArray,
		final int derivativeVariateIndex,
		final int order)
	{
		Differential[] differentialArray = differential (variateArray, derivativeVariateIndex, order);

		if (null == differentialArray || 0 == differentialArray.length) {
			return null;
		}

		double[] derivativeArray = new double[differentialArray.length];

		for (int valueIndex = 0; valueIndex < differentialArray.length; ++valueIndex) {
			derivativeArray[valueIndex] = differentialArray[valueIndex].calcSlope (true);
		}

		return derivativeArray;
	}

	/**
	 * Integrate over the given Input Range Using Uniform Monte-Carlo
	 * 
	 * @param leftEdgeArray Array of Input Left Edge
	 * @param rightEdgeArray Array of Input Right Edge
	 *  
	 * @return The Array Containing the Result of the Integration over the specified Range
	 */

	public double[] integrate (
		final double[] leftEdgeArray,
		final double[] rightEdgeArray)
	{
		if (null == leftEdgeArray || !NumberUtil.IsValid (leftEdgeArray) ||
			null == rightEdgeArray || !NumberUtil.IsValid (rightEdgeArray) ||
			rightEdgeArray.length != leftEdgeArray.length)
		{
			return null;
		}

		int valueDimension = -1;
		double[] integrandArray = null;
		double[] variateArray = new double[leftEdgeArray.length];
		double[] variateArrayWidth = new double[leftEdgeArray.length];

		for (int variateIndex = 0; variateIndex < leftEdgeArray.length; ++variateIndex) {
			variateArrayWidth[variateIndex] = rightEdgeArray[variateIndex] - leftEdgeArray[variateIndex];
		}

		for (int samplingIndex = 0; samplingIndex < QUADRATURE_SAMPLING; ++samplingIndex) {
			for (int variateIndex = 0; variateIndex < leftEdgeArray.length; ++variateIndex) {
				variateArray[variateIndex] =
					leftEdgeArray[variateIndex] + Math.random() * variateArrayWidth[variateIndex];
			}

			double[] valueArray = evaluate (variateArray);

			if (null == valueArray || 0 == (valueDimension = valueArray.length)) {
				return null;
			}

			if (null == integrandArray) {
				integrandArray = new double[valueDimension];
			}

			for (int valueIndex = 0; valueIndex < valueDimension; ++valueIndex) {
				integrandArray[valueIndex] += valueArray[valueIndex];
			}
		}

		for (int valueIndex = 0; valueIndex < valueDimension; ++valueIndex) {
			for (int variateIndex = 0; variateIndex < leftEdgeArray.length; ++variateIndex) {
				integrandArray[valueIndex] = integrandArray[valueIndex] * variateArrayWidth[variateIndex];
			}

			integrandArray[valueIndex] /= QUADRATURE_SAMPLING;
		}

		return integrandArray;
	}
}
