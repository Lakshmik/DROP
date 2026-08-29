
package org.drip.state.municipal;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.drip.numerical.common.NumberUtil;

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
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/state/README.md">Latent State Inference and Creation Utilities</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/state/municipal/README.md">Municipal Latent State Curve Estimator</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class KalotayWilliamsFabozzi
{

	/**
	 * Base Forward Node Off of Cumulative Calibration Yield
	 */

	public static final int BASE_FORWARD_NODE_UNADJUSTED_CUMULATIVE_YIELD = 0;

	/**
	 * Base Forward Node Off of Mean-evolved Calibration Yield
	 */

	public static final int BASE_FORWARD_NODE_ADJUSTED_CUMULATIVE_YIELD = 1;

	private TreeMap<Double, Double> _timeToCalibrationYieldMap = null;
	private TreeMap<Double, List<Double>> _timeToProjectedPeriodYieldScalingMap = null;
	private TreeMap<Double, ZeroVolatilityPeriodState> _timeToZeroVolatilityPeriodStateMap = null;
	private TreeMap<Double, List<KalotayWilliamsFabozziPeriodState>> _timeToProjectedPeriodStateMap = null;

	private boolean buildTimeToZeroVolatilityPeriodStateMap (
		final double yieldBasis)
	{
		_timeToZeroVolatilityPeriodStateMap = new TreeMap<Double, ZeroVolatilityPeriodState>();

		double startTime = 0.;
		double startDiscountFactor = 1.;

		for (double endTime : _timeToCalibrationYieldMap.keySet()) {
			double timeGap = endTime - startTime;

			double cumulativeMarketYield = _timeToCalibrationYieldMap.get (endTime) + yieldBasis;

			double cumulativeDiscountFactor = Math.pow (1. + cumulativeMarketYield, -1. * endTime);

			double forwardDiscountFactor = cumulativeDiscountFactor / startDiscountFactor;

			try {
				_timeToZeroVolatilityPeriodStateMap.put (
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

				return false;
			}

			startDiscountFactor = cumulativeDiscountFactor;
			startTime = endTime;
		}

		return true;
	}

	/**
	 * <i>KalotayWilliamsFabozzi</i> Constructor
	 * 
	 * @param timeToCalibrationYieldMap Time Map of Calibration Yields
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public KalotayWilliamsFabozzi (
		final TreeMap<Double, Double> timeToCalibrationYieldMap)
		throws Exception
	{
		if (null == (_timeToCalibrationYieldMap = timeToCalibrationYieldMap) ||
				0 == _timeToCalibrationYieldMap.size() ||
			!buildTimeToZeroVolatilityPeriodStateMap (0.))
		{
			throw new Exception ("KalotayWilliamsFabozzi Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Time Map of Calibration Yields
	 * 
	 * @return Time Map of Calibration Yields
	 */

	public TreeMap<Double, Double> timeToCalibrationYieldMap()
	{
		return _timeToCalibrationYieldMap;
	}

	/**
	 * Retrieve the Time Map of <i>ZeroVolatilityPeriodState</i> Instances
	 * 
	 * @return Time Map of <i>ZeroVolatilityPeriodState</i> Instances
	 */

	public TreeMap<Double, ZeroVolatilityPeriodState> timeToZeroVolatilityPeriodStateMap()
	{
		return _timeToZeroVolatilityPeriodStateMap;
	}

	/**
	 * Retrieve the Time Map of Projected Period Yield Scaling
	 * 
	 * @return Time Map of Projected Period Yield Scaling
	 */

	public TreeMap<Double, List<Double>> timeToProjectedPeriodYieldScalingMap()
	{
		return _timeToProjectedPeriodYieldScalingMap;
	}

	/**
	 * Retrieve the Time Map of the Projected Period State List
	 * 
	 * @return Time Map of the Projected Period State List
	 */

	public TreeMap<Double, List<KalotayWilliamsFabozziPeriodState>> timeToProjectedPeriodStateMap()
	{
		return _timeToProjectedPeriodStateMap;
	}

	/**
	 * Apply the Basis Yield
	 * 
	 * @param basisYield Basis Yield
	 * 
	 * @return TRUE - Basis Yield successfully applied
	 */

	public boolean applyBasisYield (
		final double basisYield)
	{
		return NumberUtil.IsValid (basisYield) && buildTimeToZeroVolatilityPeriodStateMap (basisYield);
	}

	/**
	 * Remove the Basis Yield
	 * 
	 * @return TRUE - Basis Yield successfully removed
	 */

	public boolean removeBasisYield()
	{
		return buildTimeToZeroVolatilityPeriodStateMap (0.);
	}

	/**
	 * Apply Annualized Forward Yield Volatility
	 * 
	 * @param annualizedForwardYieldVolatility Annualized Forward Yield Volatility
	 * 
	 * @return TRUE - The Annualized Forward Yield Volatility successfully applied
	 */

	public boolean applyAnnualizedForwardYieldVolatility (
		final double annualizedForwardYieldVolatility)
	{
		if (!NumberUtil.IsValid (annualizedForwardYieldVolatility) || 0. > annualizedForwardYieldVolatility)
		{
			return false;
		}

		int segmentIndex = 0;

		_timeToProjectedPeriodYieldScalingMap = new TreeMap<Double, List<Double>>();

		double periodYieldScalingFactor = Math.exp (2. * annualizedForwardYieldVolatility);

		for (double endTime : _timeToZeroVolatilityPeriodStateMap.keySet()) {
			double periodYieldScaling = 1.;

			List<Double> projectedPeriodYieldScalingList = new ArrayList<Double>();

			for (int projectionIndex = 0; projectionIndex <= segmentIndex; ++projectionIndex) {
				projectedPeriodYieldScalingList.add (periodYieldScaling);

				periodYieldScaling *= periodYieldScalingFactor;
			}

			_timeToProjectedPeriodYieldScalingMap.put (endTime, projectedPeriodYieldScalingList);

			++segmentIndex;
		}

		return true;
	}

	/**
	 * Apply the Time Map of Projected Base Forward Yield
	 * 
	 * @param projectedBaseForwardYieldMap Map of Projected Base Forward Yield
	 * 
	 * @return TRUE - The Time Map of Projected Base Forward Yield successfully applied
	 */

	public boolean applyProjectedBaseForwardYield (
		final TreeMap<Double, Double> projectedBaseForwardYieldMap)
	{
		if (null == projectedBaseForwardYieldMap || 0 == projectedBaseForwardYieldMap.size()) {
			return false;
		}

		double startTime = 0.;
		List<KalotayWilliamsFabozziPeriodState> previousKalotayWilliamsFabozziPeriodStateList = null;

		_timeToProjectedPeriodStateMap = new TreeMap<Double, List<KalotayWilliamsFabozziPeriodState>>();

		for (double endTime : _timeToZeroVolatilityPeriodStateMap.keySet()) {
			List<KalotayWilliamsFabozziPeriodState> kalotayWilliamsFabozziPeriodStateList =
				new ArrayList<KalotayWilliamsFabozziPeriodState>();

			double baseForwardYield = projectedBaseForwardYieldMap.containsKey (endTime) ?
				projectedBaseForwardYieldMap.get (endTime) :
				_timeToZeroVolatilityPeriodStateMap.get (endTime).forwardYield();

			double timeGap = endTime - startTime;
			int segmentProjectionListIndex = 0;

			for (double periodYieldScaling : _timeToProjectedPeriodYieldScalingMap.get (endTime)) {
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

					return false;
				}

				++segmentProjectionListIndex;
			}

			_timeToProjectedPeriodStateMap.put (endTime, kalotayWilliamsFabozziPeriodStateList);

			previousKalotayWilliamsFabozziPeriodStateList = kalotayWilliamsFabozziPeriodStateList;
			startTime = endTime;
		}

		return true;
	}

	/**
	 * Apply the Time Map of Projected Base Forward Yield
	 * 
	 * @param annualizedForwardYieldVolatility Annualized Forward Yield Volatility
	 * @param timeToProjectedBaseForwardYieldMap Map of Projected Base Forward Yield
	 * 
	 * @return TRUE - The Time Map of Projected Base Forward Yield successfully applied
	 */

	public boolean applyProjectedBaseForwardYield (
		final double annualizedForwardYieldVolatility,
		final TreeMap<Double, Double> timeToProjectedBaseForwardYieldMap)
	{
		return applyAnnualizedForwardYieldVolatility (annualizedForwardYieldVolatility) &&
			applyProjectedBaseForwardYield (timeToProjectedBaseForwardYieldMap);
	}

	/**
	 * Apply the Time Map of Projected Base Forward Yield
	 * 
	 * @param annualizedForwardYieldVolatility Annualized Forward Yield Volatility
	 * @param forwardNodeProjectionScheme Forward Node Projection Scheme
	 * 
	 * @return TRUE - The Time Map of Projected Base Forward Yield successfully applied
	 */

	public boolean applyProjectedBaseForwardYield (
		final double annualizedForwardYieldVolatility,
		final int forwardNodeProjectionScheme)
	{
		if (!applyAnnualizedForwardYieldVolatility (annualizedForwardYieldVolatility)) {
			return false;
		}

		TreeMap<Double, Double> timeToProjectedBaseForwardYieldMap = new TreeMap<Double, Double>();

		for (Double endTime : _timeToZeroVolatilityPeriodStateMap.keySet()) {
			double scaleDownFactor = 1.;

			if (BASE_FORWARD_NODE_ADJUSTED_CUMULATIVE_YIELD == forwardNodeProjectionScheme) {
				scaleDownFactor = 0.;

				List<Double> projectedPeriodYieldScalingList =
					_timeToProjectedPeriodYieldScalingMap.get (endTime);

				for (Double scalingFactor : projectedPeriodYieldScalingList) {
					scaleDownFactor += scalingFactor;
				}

				scaleDownFactor /= projectedPeriodYieldScalingList.size();
			}

			timeToProjectedBaseForwardYieldMap.put (
				endTime,
				_timeToZeroVolatilityPeriodStateMap.get (endTime).forwardYield() / scaleDownFactor
			);
		}

		return applyProjectedBaseForwardYield (timeToProjectedBaseForwardYieldMap);
	}
}
