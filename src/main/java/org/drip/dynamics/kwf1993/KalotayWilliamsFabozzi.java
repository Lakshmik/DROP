
package org.drip.dynamics.kwf1993;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

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
 * <i>KalotayWilliamsFabozzi</i> implements the Controller of the Kalotay, Williams, and Fabozzi (1993)
 * 	Tree-based Model for valuing bonds with Embedded Options. The References are:
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
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/dynamics/README.md">HJM, Hull White, LMM, and SABR Dynamic Evolution Models</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/dynamics/kwf1993/README.md">Kalotay, Williams, Fabozzi (1993) Grid</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class KalotayWilliamsFabozzi
{
	/**
	 * Empty <i>KalotayWilliamsFabozzi</i> Constructor
	 */

	public KalotayWilliamsFabozzi()
	{
	}

	/**
	 * Calibrate the Time Map of <i>ZeroVolatilityPeriodState</i> Instances
	 * 
	 * @param timeCalibrationYieldMap Time Map of Calibration Yields
	 * 
	 * @return Time Map of <i>ZeroVolatilityPeriodState</i> Instances
	 */

	public TreeMap<Double, ZeroVolatilityPeriodState> timeZeroVolatilityPeriodStateMap (
		final TreeMap<Double, Double> timeCalibrationYieldMap)
	{
		if (null == timeCalibrationYieldMap || 0 == timeCalibrationYieldMap.size()) {
			return null;
		}

		TreeMap<Double, ZeroVolatilityPeriodState> timeZeroVolatilityPeriodStateMap =
			new TreeMap<Double, ZeroVolatilityPeriodState>();

		double startTime = 0.;
		double startDiscountFactor = 1.;

		for (double endTime : timeCalibrationYieldMap.keySet()) {
			double timeGap = endTime - startTime;

			double cumulativeMarketYield = timeCalibrationYieldMap.get (endTime);

			double cumulativeDiscountFactor = Math.pow (1. + cumulativeMarketYield, -1. * endTime);

			double forwardDiscountFactor = cumulativeDiscountFactor / startDiscountFactor;

			try {
				timeZeroVolatilityPeriodStateMap.put (
					endTime,
					new ZeroVolatilityPeriodState (
						new KalotayWilliamsFabozziPeriod (startTime, endTime),
						((1. / forwardDiscountFactor) - 1.) / timeGap,
						forwardDiscountFactor,
						cumulativeDiscountFactor,
						cumulativeMarketYield
					)
				);
			} catch (Exception e) {
				e.printStackTrace();
			}

			startDiscountFactor = cumulativeDiscountFactor;
			startTime = endTime;
		}

		return timeZeroVolatilityPeriodStateMap;
	}

	/**
	 * Construct the Time Map of Projected Period Yield Scaling
	 * 
	 * @param kalotayWilliamsFabozziMarket <i>KalotayWilliamsFabozziMarket</i> Instance
	 * 
	 * @return Time Map of Projected Period Yield Scaling
	 */

	public TreeMap<Double, List<Double>> timeProjectedPeriodYieldScalingMap (
		final KalotayWilliamsFabozziMarket kalotayWilliamsFabozziMarket)
	{
		if (null == kalotayWilliamsFabozziMarket) {
			return null;
		}

		int segmentIndex = 0;

		TreeMap<Double, List<Double>> timeProjectedPeriodYieldScalingMap =
			new TreeMap<Double, List<Double>>();

		double periodYieldScalingFactor =
			Math.exp (2. * kalotayWilliamsFabozziMarket.annualizedForwardYieldVolatility());

		for (double endTime : kalotayWilliamsFabozziMarket.timeCalibrationYieldMap().keySet()) {
			double periodYieldScaling = 1.;

			List<Double> projectedPeriodYieldScalingList = new ArrayList<Double>();

			for (int projectionIndex = 0; projectionIndex <= segmentIndex; ++projectionIndex) {
				projectedPeriodYieldScalingList.add (periodYieldScaling);

				periodYieldScaling *= periodYieldScalingFactor;
			}

			timeProjectedPeriodYieldScalingMap.put (endTime, projectedPeriodYieldScalingList);

			++segmentIndex;
		}

		return timeProjectedPeriodYieldScalingMap;
	}

	/**
	 * Construct the Time Map of the Projected Period State List
	 * 
	 * @param timeZeroVolatilityPeriodStateMap Time Map of <i>ZeroVolatilityPeriodState</i> Instances
	 * @param timeProjectedPeriodYieldScalingMap Time Map of Projected Period Yield Scaling
	 * @param projectedBaseForwardYieldMap Map of Projected Base Forward Yield
	 * 
	 * @return Time Map of the Projected Period State List
	 */

	public TreeMap<Double, List<KalotayWilliamsFabozziPeriodState>> timeProjectedPeriodStateMap (
		final TreeMap<Double, ZeroVolatilityPeriodState> timeZeroVolatilityPeriodStateMap,
		final TreeMap<Double, List<Double>> timeProjectedPeriodYieldScalingMap,
		final TreeMap<Double, Double> projectedBaseForwardYieldMap)
	{
		if (null == timeProjectedPeriodYieldScalingMap || 0 == timeProjectedPeriodYieldScalingMap.size() ||
			null == projectedBaseForwardYieldMap || 0 == projectedBaseForwardYieldMap.size())
		{
			return null;
		}

		double startTime = 0.;
		List<KalotayWilliamsFabozziPeriodState> previousKalotayWilliamsFabozziPeriodStateList = null;

		TreeMap<Double, List<KalotayWilliamsFabozziPeriodState>> timeProjectedPeriodStateMap =
			new TreeMap<Double, List<KalotayWilliamsFabozziPeriodState>>();

		for (double endTime : timeZeroVolatilityPeriodStateMap.keySet()) {
			List<KalotayWilliamsFabozziPeriodState> kalotayWilliamsFabozziPeriodStateList =
				new ArrayList<KalotayWilliamsFabozziPeriodState>();

			double baseForwardYield = projectedBaseForwardYieldMap.containsKey (endTime) ?
				projectedBaseForwardYieldMap.get (endTime) :
				timeZeroVolatilityPeriodStateMap.get (endTime).forwardYield();

			double timeGap = endTime - startTime;
			int segmentProjectionListIndex = 0;

			for (double periodYieldScaling : timeProjectedPeriodYieldScalingMap.get (endTime)) {
				double forwardYield = baseForwardYield * periodYieldScaling;

				double forwardDiscountFactor = Math.pow (1. + forwardYield, -1. * timeGap);

				int previousSegmentProjectionCount = null == previousKalotayWilliamsFabozziPeriodStateList ?
					0 : previousKalotayWilliamsFabozziPeriodStateList.size();

				double endDiscountFactor = forwardDiscountFactor;

				if (segmentProjectionListIndex < previousSegmentProjectionCount) {
					endDiscountFactor *= previousKalotayWilliamsFabozziPeriodStateList.get (
						segmentProjectionListIndex
					).cumulativeEndDiscountFactor();
				} else if (0 != previousSegmentProjectionCount) {
					endDiscountFactor *= previousKalotayWilliamsFabozziPeriodStateList.get (
						previousSegmentProjectionCount - 1
					).cumulativeEndDiscountFactor();
				}

				try {
					kalotayWilliamsFabozziPeriodStateList.add (
						new ZeroVolatilityPeriodState (
							new KalotayWilliamsFabozziPeriod (startTime, endTime),
							forwardYield,
							forwardDiscountFactor,
							endDiscountFactor,
							Math.pow (endDiscountFactor, -1. * (1 / endTime)) - 1.
						)
					);
				} catch (Exception e) {
					e.printStackTrace();

					return null;
				}

				++segmentProjectionListIndex;
			}

			timeProjectedPeriodStateMap.put (endTime, kalotayWilliamsFabozziPeriodStateList);

			previousKalotayWilliamsFabozziPeriodStateList = kalotayWilliamsFabozziPeriodStateList;
			startTime = endTime;
		}

		return timeProjectedPeriodStateMap;
	}

	/**
	 * Build the <i>KalotayWilliamsFabozziTree</i> Instance from the Market Inputs
	 * 
	 * @param kalotayWilliamsFabozziMarket <i>KalotayWilliamsFabozziMarket</i> Instance
	 * @param projectedBaseForwardYieldMap Map of Projected Base Forward Yield
	 * 
	 * @return <i>KalotayWilliamsFabozziTree</i> Instance
	 */

	public KalotayWilliamsFabozziTree tree (
		final KalotayWilliamsFabozziMarket kalotayWilliamsFabozziMarket,
		final TreeMap<Double, Double> projectedBaseForwardYieldMap)
	{
		if (null == kalotayWilliamsFabozziMarket) {
			return null;
		}

		TreeMap<Double, ZeroVolatilityPeriodState> timeZeroVolatilityPeriodStateMap =
			timeZeroVolatilityPeriodStateMap (kalotayWilliamsFabozziMarket.timeCalibrationYieldMap()
		);

		if (null == timeZeroVolatilityPeriodStateMap) {
			return null;
		}

		TreeMap<Double, List<Double>> timeProjectedPeriodYieldScalingMap =
			timeProjectedPeriodYieldScalingMap (kalotayWilliamsFabozziMarket);

		TreeMap<Double, List<KalotayWilliamsFabozziPeriodState>> timeProjectedPeriodStateMap =
			timeProjectedPeriodStateMap (
				timeZeroVolatilityPeriodStateMap,
				timeProjectedPeriodYieldScalingMap,
				projectedBaseForwardYieldMap
			);

		try {
			return new KalotayWilliamsFabozziTree (
				timeZeroVolatilityPeriodStateMap,
				timeProjectedPeriodYieldScalingMap,
				timeProjectedPeriodStateMap
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
