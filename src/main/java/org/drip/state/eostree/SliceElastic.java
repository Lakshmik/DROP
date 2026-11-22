
package org.drip.state.eostree;

import java.util.List;
import java.util.TreeMap;

import org.drip.numerical.common.NumberUtil;
import org.drip.state.municipal.SegmentElastic;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2030 Lakshmi Krishnamurthy
 * Copyright (C) 2029 Lakshmi Krishnamurthy
 * Copyright (C) 2028 Lakshmi Krishnamurthy
 * Copyright (C) 2027 Lakshmi Krishnamurthy
 * Copyright (C) 2026 Lakshmi Krishnamurthy
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
 * <i>SliceElastic</i> holds the Path-dependent Properties of a Single Cross-vertex Slice built according to
 * 	the Kalotay, Williams, and Fabozzi (1993) Tree-based Model for valuing bonds with Embedded Options. The
 * 	References are:
 *  
 * 	<br>
 *  <ul>
 * 		<li>
 * 			Black, F., E. Derman, and W. Toy (1990): A One-Factor Model of Interest Rates and Its Application
 * 				to Treasury Bond Options <i>Financial Analysis Journal</i> <b>46 (1)</b> 33-39
 * 		</li>
 * 		<li>
 * 			Hull, J. and A. White (1990a): Valuing Derivative Securities Using the Explicit Finite Difference
 * 				Method <i>Journal of Financial and Quantitative Analysis</i> <b>25 (1)</b> 87-100
 * 		</li>
 * 		<li>
 * 			Hull, J. and A. White (1990b): Pricing Interest-Rate-Derivative Securities <i>Review of Financial
 * 				Studies</i> <b>3 (4)</b> 573-592
 * 		</li>
 * 		<li>
 * 			Kalotay, A. J. and G. O. Williams (1992): The Valuation and Management of Bonds with Sinking Fund
 * 				Provisions <i>Financial Analysis Journal</i> <b>48 (2)</b> 59-67
 * 		</li>
 * 		<li>
 * 			Kalotay, A. J., G. O. Williams, and F. J. Fabozzi (1993): A Model for Valuing Bonds and Embedded
 * 				Options <i>Financial Analysis Journal</i> <b>49 (3)</b> 35-46
 * 		</li>
 *  </ul>
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/state/README.md">Latent State Inference and Creation Utilities</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/state/eostree/README.md">EOS Bond Tree Based Valuation</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class SliceElastic
{

	class LeftPVArray
	{
		double[] _preExerciseDirtyPrice = null;
		double[] _postExerciseDirtyPrice = null;
	}

	private boolean _isPut = false;
	private double[] _flatYieldArray = null;
	private VertexElastic _rightVertexElastic = null;
	private VertexInelastic _leftVertexInelastic = null;
	private TreeMap<Double, Double> _exerciseTimeToPriceMap = null;

	/**
	 * Construct the Standard Instance of <i>SliceElastic</i>
	 * 
	 * @param leftVertexInelastic Left <i>VertexInelastic</i> Instance
	 * @param rightVertexElastic Right <i>VertexElastic</i> Instance
	 * @param segmentElasticList List of <i>SegmentElastic</i> Instances
	 * @param isPut Put Indicator
	 * @param exerciseTimeToPriceMap Exercise Time To Price Map
	 * 
	 * @return Standard Instance of <i>SliceElastic</i>
	 */

	public static final SliceElastic Standard (
		final VertexInelastic leftVertexInelastic,
		final VertexElastic rightVertexElastic,
		final List<SegmentElastic> segmentElasticList,
		final boolean isPut,
		final TreeMap<Double, Double> exerciseTimeToPriceMap)
	{
		if (null == segmentElasticList) {
			return null;
		}

		int segmentElasticCount = segmentElasticList.size();

		if (0 == segmentElasticCount) {
			return null;
		}

		double[] flatYieldArray = new double[segmentElasticCount];

		for (int segmentElasticIndex = 0; segmentElasticIndex < segmentElasticCount; ++segmentElasticIndex) {
			flatYieldArray[segmentElasticIndex] =
				segmentElasticList.get (segmentElasticIndex).forwardYield();
		}

		try {
			return new SliceElastic (
				leftVertexInelastic,
				rightVertexElastic,
				flatYieldArray,
				isPut,
				exerciseTimeToPriceMap
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private LeftPVArray leftPVArray()
	{
		double[] rightPostExerciseDirtyPVArray = _rightVertexElastic.postExerciseDirtyPVArray();

		VertexInelastic rightVertexInelastic = _rightVertexElastic.inelastic();

		LeftPVArray leftPVArray = new LeftPVArray();

		double leftTime = _leftVertexInelastic.time();

		double timeGap = rightVertexInelastic.time() - leftTime;

		double leftPayAmount = _leftVertexInelastic.payAmount();

		leftPVArray._preExerciseDirtyPrice = new double[_flatYieldArray.length];
		leftPVArray._postExerciseDirtyPrice = new double[_flatYieldArray.length];

		double leftDirtyExercisePrice = _exerciseTimeToPriceMap.containsKey (leftTime) ? 
			_exerciseTimeToPriceMap.get (leftTime) + _leftVertexInelastic.accrual().amount() : Double.NaN;

		if (_flatYieldArray.length == rightPostExerciseDirtyPVArray.length) {
			for (int rightDiscountFactorIndex = 0;
				rightDiscountFactorIndex < leftPVArray._postExerciseDirtyPrice.length;
				++rightDiscountFactorIndex)
			{
				leftPVArray._preExerciseDirtyPrice[rightDiscountFactorIndex] = leftPayAmount + (
					rightPostExerciseDirtyPVArray[rightDiscountFactorIndex] /
						(1. + _flatYieldArray[rightDiscountFactorIndex] * timeGap)
					);

				if (Double.isNaN (leftDirtyExercisePrice)) {
					leftPVArray._postExerciseDirtyPrice[rightDiscountFactorIndex] =
						leftPVArray._preExerciseDirtyPrice[rightDiscountFactorIndex];
				} else {
					leftPVArray._postExerciseDirtyPrice[rightDiscountFactorIndex] = _isPut ? Math.max (
						leftPVArray._preExerciseDirtyPrice[rightDiscountFactorIndex],
						leftDirtyExercisePrice
					) : Math.min (
						leftPVArray._preExerciseDirtyPrice[rightDiscountFactorIndex],
						leftDirtyExercisePrice
					);
				}
			}
		} else {
			for (int rightDiscountFactorIndex = 0;
				rightDiscountFactorIndex < leftPVArray._postExerciseDirtyPrice.length;
				++rightDiscountFactorIndex)
			{
				leftPVArray._preExerciseDirtyPrice[rightDiscountFactorIndex] = leftPayAmount + (
					0.5 * (
						rightPostExerciseDirtyPVArray[rightDiscountFactorIndex] +
							rightPostExerciseDirtyPVArray[rightDiscountFactorIndex + 1]
					) / (1. + _flatYieldArray[rightDiscountFactorIndex] * timeGap)
				);

				if (Double.isNaN (leftDirtyExercisePrice)) {
					leftPVArray._postExerciseDirtyPrice[rightDiscountFactorIndex] =
						leftPVArray._preExerciseDirtyPrice[rightDiscountFactorIndex];
				} else {
					leftPVArray._postExerciseDirtyPrice[rightDiscountFactorIndex] = _isPut ? Math.max (
						leftPVArray._preExerciseDirtyPrice[rightDiscountFactorIndex],
						leftDirtyExercisePrice
					) : Math.min (
						leftPVArray._preExerciseDirtyPrice[rightDiscountFactorIndex],
						leftDirtyExercisePrice
					);
				}
			}
		}

		return leftPVArray;
	}

	/**
	 * <i>SliceElastic</i> Constructor
	 * 
	 * @param leftVertexInelastic Left <i>VertexInelastic</i> Instance
	 * @param rightVertexElastic Right <i>VertexElastic</i> Instance
	 * @param flatYieldArray Segment's Flat Yield Array
	 * @param isPut Put Indicator
	 * @param exerciseTimeToPriceMap Map of Time to Exercise Price
	 * 
	 * @throws Exception Thrown if the Inputs are invalid
	 */

	public SliceElastic (
		final VertexInelastic leftVertexInelastic,
		final VertexElastic rightVertexElastic,
		final double[] flatYieldArray,
		final boolean isPut,
		final TreeMap<Double, Double> exerciseTimeToPriceMap)
		throws Exception
	{
		if (null == (_leftVertexInelastic = leftVertexInelastic) ||
			null == (_rightVertexElastic = rightVertexElastic) ||
			null == (_flatYieldArray = flatYieldArray) || 0 == _flatYieldArray.length)
		{
			throw new Exception ("SliceElastic Constructor => Invlid Inputs");
		}

		_isPut = isPut;
		_exerciseTimeToPriceMap = exerciseTimeToPriceMap;
	}

	/**
	 * Retrieve the Left <i>VertexInelastic</i> Instance
	 * 
	 * @return Left <i>VertexInelastic</i> Instance
	 */

	public VertexInelastic leftVertexInelastic()
	{
		return _leftVertexInelastic;
	}

	/**
	 * Retrieve the Right <i>VertexElastic</i> Instance
	 * 
	 * @return Right <i>VertexElastic</i> Instance
	 */

	public VertexElastic rightVertexElastic()
	{
		return _rightVertexElastic;
	}

	/**
	 * Retrieve the Put Indicator
	 * 
	 * @return Put Indicator
	 */

	public boolean isPut()
	{
		return _isPut;
	}

	/**
	 * Retrieve the Segment's Flat Yield Array
	 * 
	 * @return Segment's Flat Yield Array
	 */

	public double[] flatYieldArray()
	{
		return _flatYieldArray;
	}

	/**
	 * Retrieve the Map of Time to Exercise Price
	 * 
	 * @return Map of Time to Exercise Price
	 */

	public TreeMap<Double, Double> exerciseTimeToPriceMap()
	{
		return _exerciseTimeToPriceMap;
	}

	/**
	 * Construct the Left <i>VertexElastic</i> Instance
	 * 
	 * @return Left <i>VertexElastic</i> Instance
	 */

	public VertexElastic leftVertexElastic()
	{
		try {
			LeftPVArray leftPVArray = leftPVArray();

			return null == leftPVArray ? null : new VertexElastic (
				_leftVertexInelastic,
				leftPVArray._postExerciseDirtyPrice,
				leftPVArray._preExerciseDirtyPrice
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 'JSON-ize' the State
	 * 
	 * @param prefix The JSON Prefix
	 * 
	 * @return The 'JSON-ize'd State
	 */

	public String toString (
		final String prefix)
	{
		return prefix + " {Right Vertex Elastic: " + _rightVertexElastic.toString() +
			"; Left Vertex Elastic: " + _leftVertexInelastic.toString() +
			"; Flat Yield Array: " + NumberUtil.ArrayRow (_flatYieldArray, 1, 6, false) +
			"; Is Put? " + _isPut + "; Exercise Time To Price Map: " + _exerciseTimeToPriceMap + "}";
	}

	/**
	 * 'JSON-ize' the State
	 * 
	 * @return The 'JSON-ize'd State
	 */

	public @Override String toString()
	{
		return toString ("");
	}
}
