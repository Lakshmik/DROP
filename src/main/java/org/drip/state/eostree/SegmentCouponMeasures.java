
package org.drip.state.eostree;

import java.util.List;
import java.util.TreeMap;

import org.drip.numerical.common.NumberUtil;
import org.drip.state.municipal.KalotayWilliamsFabozziPeriodState;

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
 * <i>SegmentCouponMeasures</i> holds Measures associated with a Single Coupon/Exercise Segment built
 * 	according to the Kalotay, Williams, and Fabozzi (1993) Tree-based Model for valuing bonds with Embedded
 * 	Options. The References are:
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

public class SegmentCouponMeasures
{
	private boolean _isPut = false;
	private SegmentConfine _confine = null;
	private double[] _forwardYieldArray = null;
	private double[] _forwardRightPVArray = null;
	private TreeMap<Double, Double> _exerciseTimeToPriceMap = null;

	/**
	 * Construct the Standard Instance of <i>SegmentCouponMeasures</i>
	 * 
	 * @param leftTime Left Time
	 * @param rightTime Right Time
	 * @param amount Period Amount
	 * @param kalotayWilliamsFabozziPeriodStateList List of <i>KalotayWilliamsFabozziPeriodState</i>
	 * 												Instances
	 * @param isPut Put Indicator
	 * @param exerciseTimeToPriceMap Exercise Time To Price Map
	 * 
	 * @return Standard Instance of <i>SegmentCouponMeasures</i>
	 */

	public static final SegmentCouponMeasures Terminal (
		final double leftTime,
		final double rightTime,
		final double amount,
		final List<KalotayWilliamsFabozziPeriodState> kalotayWilliamsFabozziPeriodStateList,
		final boolean isPut,
		final TreeMap<Double, Double> exerciseTimeToPriceMap)
	{
		if (null == kalotayWilliamsFabozziPeriodStateList) {
			return null;
		}

		int periodStateCount = kalotayWilliamsFabozziPeriodStateList.size();

		if (0 == periodStateCount) {
			return null;
		}

		double[] spotRightPVArray = new double[periodStateCount];
		double[] forwardYieldArray = new double[periodStateCount];

		for (int periodStateIndex = 0; periodStateIndex < periodStateCount; ++periodStateIndex) {
			spotRightPVArray[periodStateIndex] = 1.;

			forwardYieldArray[periodStateIndex] =
				kalotayWilliamsFabozziPeriodStateList.get (periodStateIndex).forwardYield();
		}

		try {
			return new SegmentCouponMeasures (
				new SegmentConfine (leftTime, rightTime, amount),
				forwardYieldArray,
				spotRightPVArray,
				isPut,
				exerciseTimeToPriceMap
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct the Preceding Instance of <i>SegmentCouponMeasures</i>
	 * 
	 * @param leftTime Left Time
	 * @param rightTime Right Time
	 * @param amount Period Amount
	 * @param kalotayWilliamsFabozziPeriodStateList List of <i>KalotayWilliamsFabozziPeriodState</i>
	 * 												Instances
	 * @param succeedingSegmentCouponMeasures Succeeding Instance of <i>SegmentCouponMeasures</i>
	 * @param isPut Put Indicator
	 * @param exerciseTimeToPriceMap Exercise Time To Price Map
	 * 
	 * @return Preceding Instance of <i>SegmentCouponMeasures</i>
	 */

	public static final SegmentCouponMeasures Preceding (
		final double leftTime,
		final double rightTime,
		final double amount,
		final List<KalotayWilliamsFabozziPeriodState> kalotayWilliamsFabozziPeriodStateList,
		final SegmentCouponMeasures succeedingSegmentCouponMeasures,
		final boolean isPut,
		final TreeMap<Double, Double> exerciseTimeToPriceMap)
	{
		if (null == kalotayWilliamsFabozziPeriodStateList || null == succeedingSegmentCouponMeasures) {
			return null;
		}

		int periodStateCount = kalotayWilliamsFabozziPeriodStateList.size();

		if (0 == periodStateCount) {
			return null;
		}

		double[] forwardYieldArray = new double[periodStateCount];

		for (int periodStateIndex = 0; periodStateIndex < periodStateCount; ++periodStateIndex) {
			forwardYieldArray[periodStateIndex] =
				kalotayWilliamsFabozziPeriodStateList.get (periodStateIndex).forwardYield();
		}

		try {
			return new SegmentCouponMeasures (
				new SegmentConfine (leftTime, rightTime, amount),
				forwardYieldArray,
				succeedingSegmentCouponMeasures.forwardLeftPVArray(),
				isPut,
				exerciseTimeToPriceMap
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * <i>SegmentCouponMeasures</i> Constructor
	 * 
	 * @param confine <i>SegmentConfine</i> Instance
	 * @param forwardYieldArray Forward Yield Array
	 * @param forwardRightPVArray Forward Right PV Array
	 * @param isPut Put Indicator
	 * @param exerciseTimeToPriceMap Exercise Time To Price Map
	 * 
	 * @throws Exception Thrown if Inputs are Invalid
	 */

	public SegmentCouponMeasures (
		final SegmentConfine confine,
		final double[] forwardYieldArray,
		final double[] forwardRightPVArray,
		final boolean isPut,
		final TreeMap<Double, Double> exerciseTimeToPriceMap)
		throws Exception
	{
		if (null == (_confine = confine) ||
			null == (_forwardYieldArray = forwardYieldArray) || 0 == _forwardYieldArray.length ||
				!NumberUtil.IsValid (_forwardYieldArray) ||
			null == (_forwardRightPVArray = forwardRightPVArray) || 0 == _forwardRightPVArray.length ||
				!NumberUtil.IsValid (_forwardRightPVArray))
		{
			throw new Exception ("SegmentCouponMeasures Constructor => Invalid Inputs");
		}

		if (_forwardYieldArray.length != _forwardRightPVArray.length &&
			_forwardYieldArray.length + 1 != _forwardRightPVArray.length)
		{
			throw new Exception ("SegmentCouponMeasures Constructor => Invalid Inputs");
		}

		_isPut = isPut;
		_exerciseTimeToPriceMap = exerciseTimeToPriceMap;
	}

	/**
	 * Retrieve the <i>SegmentConfine</i> Instance
	 * 
	 * @return <i>SegmentConfine</i> Instance
	 */

	public SegmentConfine confine()
	{
		return _confine;
	}

	/**
	 * Retrieve the Forward Yield Array
	 * 
	 * @return Forward Yield Array
	 */

	public double[] forwardYieldArray()
	{
		return _forwardYieldArray;
	}

	/**
	 * Retrieve the Forward Right PV Array
	 * 
	 * @return Forward Right PV Array
	 */

	public double[] forwardRightPVArray()
	{
		return _forwardRightPVArray;
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
	 * Retrieve the Map of Time to Exercise Price
	 * 
	 * @return Map of Time to Exercise Price
	 */

	public TreeMap<Double, Double> exerciseTimeToPriceMap()
	{
		return _exerciseTimeToPriceMap;
	}

	/**
	 * Retrieve the Forward Left PV Array
	 * 
	 * @return Forward Left PV Array
	 */

	public double[] forwardLeftPVArray()
	{
		double amount = _confine.amount();

		double leftTime = _confine.leftTime();

		double timeGap = _confine.rightTime() - leftTime;

		double exercisePrice = _exerciseTimeToPriceMap.containsKey (leftTime) ? 
			_exerciseTimeToPriceMap.get (leftTime) : Double.NaN;

		double[] forwardLeftPVArray = new double[_forwardYieldArray.length];

		if (_forwardYieldArray.length == _forwardRightPVArray.length) {
			for (int rightDiscountFactorIndex = 0;
				rightDiscountFactorIndex < forwardLeftPVArray.length;
				++rightDiscountFactorIndex)
			{
				double forwardLeftPV = (_forwardRightPVArray[rightDiscountFactorIndex] + amount) /
					(1. + _forwardYieldArray[rightDiscountFactorIndex] * timeGap);

				if (Double.isNaN (exercisePrice)) {
					forwardLeftPVArray[rightDiscountFactorIndex] = forwardLeftPV;
				} else {
					forwardLeftPVArray[rightDiscountFactorIndex] = _isPut ?
						Math.max (forwardLeftPV, exercisePrice) : Math.min (forwardLeftPV, exercisePrice);
				}
			}
		} else {
			for (int rightDiscountFactorIndex = 0;
				rightDiscountFactorIndex < forwardLeftPVArray.length;
				++rightDiscountFactorIndex)
			{
				double forwardLeftPV = (
					0.5 * (
						_forwardRightPVArray[rightDiscountFactorIndex] +
							_forwardRightPVArray[rightDiscountFactorIndex + 1]
					) + amount
				) / (
					1. + _forwardYieldArray[rightDiscountFactorIndex] * timeGap
				);

				if (Double.isNaN (exercisePrice)) {
					forwardLeftPVArray[rightDiscountFactorIndex] = forwardLeftPV;
				} else {
					forwardLeftPVArray[rightDiscountFactorIndex] = _isPut ?
						Math.max (forwardLeftPV, exercisePrice) : Math.min (forwardLeftPV, exercisePrice);
				}
			}
		}

		return forwardLeftPVArray;
	}
}
