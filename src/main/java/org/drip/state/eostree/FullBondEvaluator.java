
package org.drip.state.eostree;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.drip.analytics.date.JulianDate;
import org.drip.numerical.common.NumberUtil;
import org.drip.product.credit.BondComponent;
import org.drip.state.municipal.KalotayWilliamsFabozzi;
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
 * <i>FullBondEvaluator</i> implements the DRIP Bond Evaluator to be used in the Kalotay, Williams, and
 * 	Fabozzi (1993) Tree-based Model for valuing bonds with Embedded Options. The References are:
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

public class FullBondEvaluator
	extends BondEvaluator
{
	private BondComponent _underlying = null;

	private static final VertexInelastic MakeVertexInelastic (
		final double time,
		final JulianDate spotDate,
		final VertexEventComposite vertexEventComposite,
		final boolean isMaturity,
		final boolean isExercisable)
	{
		TreeMap<Double, Double> timeToPrincipalPaydownMap = vertexEventComposite.timeToPrincipalPaydownMap();

		TreeMap<Double, Double> payTimeToFullCouponMap = vertexEventComposite.payTimeToFullCouponMap();

		TreeMap<Double, VertexAccrual> timeToAccrualMap = vertexEventComposite.timeToAccrualMap();

		boolean isPrincipalPayDown = timeToPrincipalPaydownMap.containsKey (time);

		boolean isFullCoupon = payTimeToFullCouponMap.containsKey (time);

		boolean isAccrual = timeToAccrualMap.containsKey (time);

		String eventFlags = "[";

		if (isMaturity) {
			eventFlags += "MATURITY, ";
		}

		if (isPrincipalPayDown) {
			eventFlags += "PRINCIPALPAYDOWN, ";
		}

		if (isFullCoupon) {
			eventFlags += "FULLCOUPON, ";
		}

		if (isAccrual) {
			eventFlags += "ACCRUAL, ";
		}

		if (isExercisable) {
			eventFlags += "EXERCISABLE, ";
		}

		if (vertexEventComposite.isCalibrationPillar (time)) {
			eventFlags += "CALIBRATIONPILLAR, ";
		}

		try {
			return new VertexInelastic (
				time,
				spotDate.addDays ((int) (365.25 * time + 0.5)),
				(isPrincipalPayDown ? timeToPrincipalPaydownMap.get (time) : (isMaturity ? 1. : 0.)) +
					(isFullCoupon ? payTimeToFullCouponMap.get (time) : 0.),
				isAccrual ? timeToAccrualMap.get (time) : VertexAccrual.Zero(),
				eventFlags + "]"
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * <i>FullBondEvaluator</i> Constructor
	 * 
	 * @param underlying Underlying Calibration Bond
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public FullBondEvaluator (
		final BondComponent underlying)
		throws Exception
	{
		if (null == (_underlying = underlying)) {
			throw new Exception ("FullBondEvaluator Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Underlying Calibration Bond
	 * 
	 * @return Underlying Calibration Bond
	 */

	public BondComponent underlying()
	{
		return _underlying;
	}

	/**
	 * Roll the List of <i>SliceElastic</i> Instances Backwards
	 * 
	 * @param spotDate Spot Date
	 * @param kalotayWilliamsFabozzi Calibrated <i>KalotayWilliamsFabozzi</i> Instance
	 * 
	 * @return List of <i>SliceElastic</i> Instances Backwards
	 */

	public List<SliceElastic> backwardsInduction (
		final JulianDate spotDate,
		final KalotayWilliamsFabozzi kalotayWilliamsFabozzi)
	{
		VertexEventComposite vertexEventComposite =
			VertexEventComposite.Standard (spotDate, _underlying, kalotayWilliamsFabozzi);

		if (null == vertexEventComposite) {
			return null;
		}

		double rightVertexTime = Double.NaN;

		boolean isPut = _underlying.putable();

		List<SliceElastic> sliceElasticList = new ArrayList<SliceElastic>();

		double maturityTime = ((double) (_underlying.maturityDate().julian() - spotDate.julian())) / 365.25;

		TreeMap<Double, Double> exerciseTimeToPriceMap = vertexEventComposite.exerciseTimeToCleanPriceMap();

		TreeMap<Double, List<SegmentElastic>> timeToSegmentElasticListMap =
			vertexEventComposite.timeToSegmentElasticListMap();

		double peakPillarTime = timeToSegmentElasticListMap.lastKey();

		VertexElastic rightVertexElastic = maturityTime <= peakPillarTime ? null :
			VertexElastic.Terminal (
				MakeVertexInelastic (
					rightVertexTime = maturityTime,
					spotDate,
					vertexEventComposite,
					true,
					exerciseTimeToPriceMap.containsKey (rightVertexTime)
				),
				timeToSegmentElasticListMap.get (
					peakPillarTime
				).size()
			);

		for (double leftVertexTime : timeToSegmentElasticListMap.descendingKeySet()) {
			if (maturityTime <= leftVertexTime) {
				rightVertexTime = maturityTime;
				continue;
			}

			List<SegmentElastic> segmentElasticList =
				timeToSegmentElasticListMap.containsKey (rightVertexTime) ?
				timeToSegmentElasticListMap.get (rightVertexTime) :
				kalotayWilliamsFabozzi.bespokeSegmentElasticList (rightVertexTime);

			SliceElastic sliceElastic = SliceElastic.Standard (
				MakeVertexInelastic (
					leftVertexTime,
					spotDate,
					vertexEventComposite,
					false,
					exerciseTimeToPriceMap.containsKey (leftVertexTime)
				),
				null == rightVertexElastic ? VertexElastic.Terminal (
					MakeVertexInelastic (
						rightVertexTime,
						spotDate,
						vertexEventComposite,
						true,
						exerciseTimeToPriceMap.containsKey (rightVertexTime)
					),
					segmentElasticList.size()
				) : rightVertexElastic,
				segmentElasticList,
				isPut,
				exerciseTimeToPriceMap
			);

			rightVertexElastic = sliceElastic.leftVertexElastic();

			sliceElasticList.add (sliceElastic);

			rightVertexTime = leftVertexTime;
		}

		return sliceElasticList;
	}

	/**
	 * Retrieve the Bond Maturity Time from Spot
	 * 
	 * @param spotTime Spot Time
	 * 
	 * @return Bond Maturity Time from Spot
	 * 
	 * @throws Thrown if the Maturity Time cannot be calculated
	 */

	@Override public double maturityTime (
		final double spotTime)
		throws Exception
	{
		if (!NumberUtil.IsValid (spotTime)) {
			throw new Exception ("FullBondEvaluator::maturityTime => Invalid Inputs");
		}

		return ((double) _underlying.maturityDate().julian()) / 365.25 - spotTime;
	}

	/**
	 * Construct the Bond Value Grid using the Calibrated <i>KalotayWilliamsFabozzi</i> Instance
	 * 
	 * @param spotDate Spot Date
	 * @param kalotayWilliamsFabozzi Calibrated <i>KalotayWilliamsFabozzi</i> Instance
	 * 
	 * @return Bond Value Grid
	 */

	@Override public ValueGrid valueGrid (
		final JulianDate spotDate,
		final KalotayWilliamsFabozzi kalotayWilliamsFabozzi)
	{
		try {
			return new FullBondValueGrid (backwardsInduction (spotDate, kalotayWilliamsFabozzi));
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
		return prefix + " {Underlying Bond: " + _underlying.name() + "}";
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
