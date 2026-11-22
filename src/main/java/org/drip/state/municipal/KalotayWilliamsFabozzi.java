
package org.drip.state.municipal;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
	 * Segment Yield Unadjusted by Evolution Mean
	 */

	public static final int EVOLUTION_MEAN_UNADJUSTED_SEGMENT_YIELD = 0;

	/**
	 * Segment Yield Adjusted by Evolution Mean
	 */

	public static final int EVOLUTION_MEAN_ADJUSTED_SEGMENT_YIELD = 1;

	private TreeMap<Double, Double> _pillarTimeToCalibrationYieldMap = null;
	private TreeMap<Double, List<Double>> _pillarTimeToYieldScalingListMap = null;
	private TreeMap<Double, List<SegmentElastic>> _pillarTimeToSegmentElasticListMap = null;
	private TreeMap<Double, CalibrationSegmentElastic> _pillarTimeToCalibrationSegmentElasticMap = null;

	private boolean setupCalibrationSegmentElasticMap (
		final double yieldBasis)
	{
		double startingTime = 0.;
		double startingDiscountFactor = 1.;

		_pillarTimeToCalibrationSegmentElasticMap = new TreeMap<Double, CalibrationSegmentElastic>();

		for (double pillarTime : _pillarTimeToCalibrationYieldMap.keySet()) {
			double pillarYield = _pillarTimeToCalibrationYieldMap.get (pillarTime) + yieldBasis;

			double pillarDiscountFactor = Math.pow (1. + pillarYield, -1. * pillarTime);

			double segmentDiscountFactor = pillarDiscountFactor / startingDiscountFactor;

			try {
				_pillarTimeToCalibrationSegmentElasticMap.put (
					pillarTime,
					new CalibrationSegmentElastic (
						new SegmentInelastic (startingTime, pillarTime),
						((1. / segmentDiscountFactor) - 1.) / (pillarTime - startingTime),
						segmentDiscountFactor,
						pillarDiscountFactor,
						pillarYield
					)
				);
			} catch (Exception e) {
				e.printStackTrace();

				return false;
			}

			startingDiscountFactor = pillarDiscountFactor;
			startingTime = pillarTime;
		}

		return true;
	}

	/**
	 * Empty <i>KalotayWilliamsFabozzi</i> Constructor
	 */

	public KalotayWilliamsFabozzi()
	{
		_pillarTimeToCalibrationYieldMap = new TreeMap<Double, Double>();

		_pillarTimeToCalibrationSegmentElasticMap = new TreeMap<Double, CalibrationSegmentElastic>();
	}

	/**
	 * Retrieve the Pillar Time Map of Calibration Yields
	 * 
	 * @return Pillar Time Map of Calibration Yields
	 */

	public TreeMap<Double, Double> pillarTimeToCalibrationYieldMap()
	{
		return _pillarTimeToCalibrationYieldMap;
	}

	/**
	 * Retrieve the Map of Pillar Time to <i>CalibrationSegmentElastic</i> Instances
	 * 
	 * @return Map of Pillar Time to <i>CalibrationSegmentElastic</i> Instances
	 */

	public TreeMap<Double, CalibrationSegmentElastic> pillarTimeToCalibrationSegmentElasticMap()
	{
		return _pillarTimeToCalibrationSegmentElasticMap;
	}

	/**
	 * Retrieve the Map of Pillar Time to Yield Scaling List
	 * 
	 * @return Map of Pillar Time to Yield Scaling List
	 */

	public TreeMap<Double, List<Double>> pillarTimeToYieldScalingListMap()
	{
		return _pillarTimeToYieldScalingListMap;
	}

	/**
	 * Retrieve the Map of Pillar Time to Segment Elastic List
	 * 
	 * @return Map of Pillar Time to Segment Elastic List
	 */

	public TreeMap<Double, List<SegmentElastic>> pillarTimeToSegmentElasticListMap()
	{
		return _pillarTimeToSegmentElasticListMap;
	}

	/**
	 * Retrieve the Count of Calibration Segments
	 * 
	 * @return Count of Calibration Segments
	 */

	public int segmentCount()
	{
		return _pillarTimeToCalibrationYieldMap.size();
	}

	/**
	 * Retrieve the Set of Calibration Time Pillars
	 * 
	 * @return Set of Calibration Time Pillars
	 */

	public Set<Double> timeKeySet()
	{
		return _pillarTimeToCalibrationYieldMap.keySet();
	}

	/**
	 * Update the <i>CalibrationSegmentElastic</i> Map with a Market Yield
	 * 
	 * @param pillarTime Pillar Time
	 * @param marketYield Market Yield
	 * 
	 * @return TRUE - <i>CalibrationSegmentElastic</i> Map successfully updated
	 */

	public boolean updateCalibrationSegmentElasticMap (
		final double pillarTime,
		final double marketYield)
	{
		if (!NumberUtil.IsValid (pillarTime) || 0. >= pillarTime || !NumberUtil.IsValid (marketYield)) {
			return false;
		}

		_pillarTimeToCalibrationYieldMap.put (pillarTime, marketYield);

		double segmentDiscountFactor = 1. / (1. + marketYield);
		double pillarDiscountFactor = segmentDiscountFactor;
		double startTime = 0.;

		if (!_pillarTimeToCalibrationSegmentElasticMap.isEmpty()) {
			CalibrationSegmentElastic calibrationSegmentElastic =
				_pillarTimeToCalibrationSegmentElasticMap.lastEntry().getValue();

			segmentDiscountFactor = (
				pillarDiscountFactor = Math.pow (1. + marketYield, -1. * pillarTime)
			) / calibrationSegmentElastic.endDiscountFactor();

			startTime = calibrationSegmentElastic.inelastic().endTime();
		}

		try {
			_pillarTimeToCalibrationSegmentElasticMap.put (
				pillarTime,
				new CalibrationSegmentElastic (
					new SegmentInelastic (startTime, pillarTime),
					((1. / segmentDiscountFactor) - 1.) / (pillarTime - startTime),
					segmentDiscountFactor,
					pillarDiscountFactor,
					marketYield
				)
			);
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}

		return true;
	}

	/**
	 * Bump the Market Yield for the Pillar Time
	 * 
	 * @param candidatePillarTime Candidate Pillar Time
	 * @param yieldBump Yield Bump
	 * 
	 * @return TRUE - The Market Yield successfully set for the Pillar Time
	 */

	public boolean bumpMarketYield (
		final double candidatePillarTime,
		final double yieldBump)
	{
		if (!NumberUtil.IsValid (candidatePillarTime) || 0. >= candidatePillarTime ||
			!NumberUtil.IsValid (yieldBump))
		{
			return false;
		}

		double startTime = 0.;
		double startDiscountFactor = 1.;

		for (double pillarTime : _pillarTimeToCalibrationYieldMap.keySet()) {
			if (candidatePillarTime < pillarTime) {
				continue;
			}

			double marketYield = _pillarTimeToCalibrationYieldMap.get (pillarTime) +
				(candidatePillarTime == pillarTime ? yieldBump : 0.);

			double pillarDiscountFactor = Math.pow (1. + marketYield, -1. * pillarTime);

			double segmentDiscountFactor = pillarDiscountFactor / startDiscountFactor;

			try {
				_pillarTimeToCalibrationSegmentElasticMap.put (
					pillarTime,
					new CalibrationSegmentElastic (
						new SegmentInelastic (startTime, pillarTime),
						((1. / segmentDiscountFactor) - 1.) / (pillarTime - startTime),
						segmentDiscountFactor,
						pillarDiscountFactor,
						marketYield
					)
				);
			} catch (Exception e) {
				e.printStackTrace();

				return false;
			}

			startDiscountFactor = pillarDiscountFactor;
			startTime = pillarTime;
		}

		return true;
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
		return NumberUtil.IsValid (basisYield) && setupCalibrationSegmentElasticMap (basisYield);
	}

	/**
	 * Remove the Basis Yield
	 * 
	 * @return TRUE - Basis Yield successfully removed
	 */

	public boolean removeBasisYield()
	{
		return setupCalibrationSegmentElasticMap (0.);
	}

	/**
	 * Apply Segment Yield Volatility
	 * 
	 * @param segmentYieldVolatility Segment Yield Volatility
	 * 
	 * @return TRUE - The Segment Yield Volatility successfully applied
	 */

	public boolean applySegmentYieldVolatility (
		final double segmentYieldVolatility)
	{
		if (!NumberUtil.IsValid (segmentYieldVolatility) || 0. > segmentYieldVolatility) {
			return false;
		}

		int segmentIndex = 0;

		_pillarTimeToYieldScalingListMap = new TreeMap<Double, List<Double>>();

		double pillarYieldScalingFactor = Math.exp (2. * segmentYieldVolatility);

		for (double pillarTime : _pillarTimeToCalibrationSegmentElasticMap.keySet()) {
			double pillarYieldScaling = 1.;

			List<Double> pillarYieldScalingList = new ArrayList<Double>();

			for (int projectionIndex = 0; projectionIndex <= segmentIndex; ++projectionIndex) {
				pillarYieldScalingList.add (pillarYieldScaling);

				pillarYieldScaling *= pillarYieldScalingFactor;
			}

			_pillarTimeToYieldScalingListMap.put (pillarTime, pillarYieldScalingList);

			++segmentIndex;
		}

		return true;
	}

	/**
	 * Apply the Map of Pillar Time to Base Segment Yield #1
	 * 
	 * @param pillarTimeToSegmentBaseYieldMap Map of Pillar Time to Segment Base Yield
	 * 
	 * @return TRUE - Map of Pillar Time to Segment Base Yield successfully applied
	 */

	public boolean applySegmentBaseYield (
		final TreeMap<Double, Double> pillarTimeToSegmentBaseYieldMap)
	{
		if (null == pillarTimeToSegmentBaseYieldMap || 0 == pillarTimeToSegmentBaseYieldMap.size()) {
			return false;
		}

		double startTime = 0.;
		List<SegmentElastic> previousSegmentElasticList = null;

		_pillarTimeToSegmentElasticListMap = new TreeMap<Double, List<SegmentElastic>>();

		for (double pillarTime : _pillarTimeToCalibrationSegmentElasticMap.keySet()) {
			double segmentBaseYield = pillarTimeToSegmentBaseYieldMap.containsKey (pillarTime) ?
				pillarTimeToSegmentBaseYieldMap.get (pillarTime) :
				_pillarTimeToCalibrationSegmentElasticMap.get (pillarTime).forwardYield();

			int previousSegmentVertexCount = null == previousSegmentElasticList ?
				0 : previousSegmentElasticList.size();

			List<SegmentElastic> segmentElasticList = new ArrayList<SegmentElastic>();

			double segmentTime = pillarTime - startTime;
			int segmentVertexCount = 0;

			for (double pillarYieldScaling : _pillarTimeToYieldScalingListMap.get (pillarTime)) {
				double segmentYield = segmentBaseYield * pillarYieldScaling;

				double segmentDiscountFactor = Math.pow (1. + segmentYield, -1. * segmentTime);

				double segmentEndDiscountFactor = segmentDiscountFactor;

				if (segmentVertexCount < previousSegmentVertexCount) {
					segmentEndDiscountFactor *=
						previousSegmentElasticList.get (segmentVertexCount).endDiscountFactor();
				} else if (0 != previousSegmentVertexCount) {
					segmentEndDiscountFactor *= previousSegmentElasticList.get (
						previousSegmentVertexCount - 1
					).endDiscountFactor();
				}

				try {
					segmentElasticList.add (
						new SegmentElastic (
							new SegmentInelastic (startTime, pillarTime),
							segmentYield,
							segmentDiscountFactor,
							segmentEndDiscountFactor
						)
					);
				} catch (Exception e) {
					e.printStackTrace();

					return false;
				}

				++segmentVertexCount;
			}

			_pillarTimeToSegmentElasticListMap.put (pillarTime, segmentElasticList);

			previousSegmentElasticList = segmentElasticList;
			startTime = pillarTime;
		}

		return true;
	}

	/**
	 * Apply the Map of Pillar Time to Segment Base Yield #2
	 * 
	 * @param segmentYieldVolatility Segment Yield Volatility
	 * @param yieldScalingScheme Yield Scaling Scheme
	 * 
	 * @return TRUE - The Map of Pillar Time to Segment Base Yield successfully applied
	 */

	public boolean applySegmentBaseYield (
		final double segmentYieldVolatility,
		final int yieldScalingScheme)
	{
		if (!applySegmentYieldVolatility (segmentYieldVolatility)) {
			return false;
		}

		TreeMap<Double, Double> pillarTimeToSegmentYieldMap = new TreeMap<Double, Double>();

		for (Double pillarTime : _pillarTimeToCalibrationSegmentElasticMap.keySet()) {
			double scaleDownFactor = 1.;

			if (EVOLUTION_MEAN_ADJUSTED_SEGMENT_YIELD == yieldScalingScheme) {
				scaleDownFactor = 0.;

				List<Double> segmentYieldScalingList = _pillarTimeToYieldScalingListMap.get (pillarTime);

				for (Double segmentYieldScaling : segmentYieldScalingList) {
					scaleDownFactor += segmentYieldScaling;
				}

				scaleDownFactor /= segmentYieldScalingList.size();
			}

			pillarTimeToSegmentYieldMap.put (
				pillarTime,
				_pillarTimeToCalibrationSegmentElasticMap.get (pillarTime).forwardYield() / scaleDownFactor
			);
		}

		return applySegmentBaseYield (pillarTimeToSegmentYieldMap);
	}

	/**
	 * Apply the Pillar Time Map of Segment Base Yield
	 * 
	 * @param segmentYieldVolatility Annualized Segment Yield Volatility
	 * @param pillarTimeToSegmentBaseYieldMap Map of Segment Base Yield
	 * 
	 * @return TRUE - The Pillar Time Map of Segment Base Yield successfully applied
	 */

	public boolean applyBaseForwardYield (
		final double segmentYieldVolatility,
		final TreeMap<Double, Double> pillarTimeToSegmentBaseYieldMap)
	{
		return applySegmentYieldVolatility (segmentYieldVolatility) &&
			applySegmentBaseYield (pillarTimeToSegmentBaseYieldMap);
	}

	/**
	 * Retrieve the List of <i>SegmentElastic</i> Instances at the Bespoke Time
	 * 
	 * @param bespokeTime Bespoke Time
	 * 
	 * @return List of <i>SegmentElastic</i> Instances
	 */

	public List<SegmentElastic> bespokeSegmentElasticList (
		final double bespokeTime)
	{
		if (!NumberUtil.IsValid (bespokeTime) || 0. >= bespokeTime ||
			_pillarTimeToSegmentElasticListMap.isEmpty())
		{
			return null;
		}

		double segmentStartTime = 0.;
		double segmentEndTime = Double.NaN;

		double lastPillarTime = _pillarTimeToSegmentElasticListMap.lastKey();

		if (bespokeTime > lastPillarTime) {
			List<SegmentElastic> segmentElasticList = new ArrayList<SegmentElastic>();

			for (SegmentElastic segmentElastic : _pillarTimeToSegmentElasticListMap.get (lastPillarTime)) {
				double segmentYield = segmentElastic.forwardYield();

				double segmentDiscountFactor = 1. / (1. + segmentYield * (bespokeTime - lastPillarTime));

				try {
					segmentElasticList.add (
						new SegmentElastic (
							new SegmentInelastic (lastPillarTime, bespokeTime),
							segmentYield,
							segmentDiscountFactor,
							segmentElastic.endDiscountFactor() * segmentDiscountFactor
						)
					);
				} catch (Exception e) {
					e.printStackTrace();

					return null;
				}
			}

			return segmentElasticList;
		}

		for (double pillarTime : _pillarTimeToSegmentElasticListMap.keySet()) {
			if (pillarTime > bespokeTime) {
				segmentEndTime = pillarTime;
				break;
			}

			if (pillarTime == bespokeTime) {
				return _pillarTimeToSegmentElasticListMap.get (pillarTime);
			}

			segmentStartTime = pillarTime;
		}

		if (Double.isNaN (segmentEndTime)) {
			return null;
		}

		double timeShift = segmentEndTime - bespokeTime;

		double frontalDiscountFactorAdjustment =
			1. + _pillarTimeToCalibrationSegmentElasticMap.firstEntry().getValue().marketYield() *
				(bespokeTime - segmentStartTime);

		List<SegmentElastic> segmentElasticList = new ArrayList<SegmentElastic>();

		for (SegmentElastic segmentElastic : _pillarTimeToSegmentElasticListMap.get (segmentEndTime)) {
			SegmentInelastic inelastic = segmentElastic.inelastic();

			double startTime = inelastic.startTime() - timeShift;

			try {
				segmentElasticList.add (
					new SegmentElastic (
						new SegmentInelastic (
							0. > startTime ? 0. : startTime,
							inelastic.endTime() - timeShift
						),
						segmentElastic.forwardYield(),
						segmentElastic.forwardDiscountFactor(),
						segmentElastic.endDiscountFactor() * frontalDiscountFactorAdjustment
					)
				);
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		return segmentElasticList;
	}

	/**
	 * Generate a String Version of the State
	 * 
	 * @param prefix Prefix
	 * 
	 * @return String Version of the State
	 */

	public String toString (
		final String prefix)
	{
		return prefix + "Pillar Time To Calibration Yield Map => " + _pillarTimeToCalibrationYieldMap +
			"; Pillar Time To Yield Scaling List Map => " + _pillarTimeToYieldScalingListMap +
			"; Pillar Time To Calibration Segment Elastic Map => " +
				_pillarTimeToCalibrationSegmentElasticMap +
			"; Pillar Time To Segment Elastic List Map => " + _pillarTimeToSegmentElasticListMap;
	}

	/**
	 * Generate a String Version of the State
	 * 
	 * @return String Version of the State
	 */

	@Override public String toString()
	{
		return toString ("");
	}
}
