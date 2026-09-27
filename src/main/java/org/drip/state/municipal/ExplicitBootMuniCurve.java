
package org.drip.state.municipal;

import java.util.TreeMap;

import org.drip.analytics.definition.ExplicitBootCurve;
import org.drip.numerical.differentiation.WengertJacobian;
import org.drip.state.identifier.LatentStateLabel;
import org.drip.state.identifier.MuniLabel;

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
 * <i>ExplicitBootMuniCurve</i> implements the Bootstrapped Muni Curve. The References are:
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

public class ExplicitBootMuniCurve
	extends MuniCurve
	implements ExplicitBootCurve
{
	private String _code = "";
	private KalotayWilliamsFabozzi _kalotayWilliamsFabozzi = null;

	/**
	 * <i>ExplicitBootMuniCurve</i> Constructor
	 * 
	 * @param epochDate Epoch Date
	 * @param code Muni Code
	 * @param currency Currency
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public ExplicitBootMuniCurve (
		final int epochDate,
		final String code,
		final String currency)
		throws Exception
	{
		super (epochDate, code, currency);

		if (null == (_code = code) || _code.isEmpty()) {
			throw new Exception ("ExplicitBootMuniCurve Constructor => Invalid Inputs");
		}

		_kalotayWilliamsFabozzi = new KalotayWilliamsFabozzi();
	}

	/**
	 * Retrieve the Muni Code
	 * 
	 * @return Muni Code
	 */

	public String code()
	{
		return _code;
	}

	/**
	 * Retrieve the <i>KalotayWilliamsFabozzi</i> Grid Instance
	 * 
	 * @return <i>KalotayWilliamsFabozzi</i> Grid Instance
	 */

	public KalotayWilliamsFabozzi _kalotayWilliamsFabozzi()
	{
		return _kalotayWilliamsFabozzi;
	}

	/**
	 * Retrieve the Manifest Measure Jacobian of the Forward Rate to the given date
	 * 
	 * @return The Manifest Measure Jacobian of the Forward Rate to the given date
	 */

	public WengertJacobian jackDForwardDManifestMeasure()
	{
		WengertJacobian wengertJacobian = null;

		int segmentCount = _kalotayWilliamsFabozzi.segmentCount();

		try {
			wengertJacobian = new WengertJacobian (segmentCount, segmentCount);
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}

		TreeMap<Double, ZeroVolatilityPeriodState> timeToZeroVolatilityPeriodStateMap =
			_kalotayWilliamsFabozzi.timeToZeroVolatilityPeriodStateMap();

		for (double endTime : _kalotayWilliamsFabozzi.timeKeySet()) {
			if (1. == endTime) {
				wengertJacobian.accumulatePartialFirstDerivative (0, 0, 1.);
			} else {
				int wengertIndex = (int) endTime - 1;

				double currentToPreviousDirtyParFloaterRatio = (
					1. + timeToZeroVolatilityPeriodStateMap.get (endTime).cumulativeMarketYield()
				) / (
					1. + timeToZeroVolatilityPeriodStateMap.get (endTime - 1.).cumulativeMarketYield()
				);

				if (!wengertJacobian.accumulatePartialFirstDerivative (
					wengertIndex,
					wengertIndex,
					endTime * Math.pow (currentToPreviousDirtyParFloaterRatio, endTime - 1.)
				))
				{
					return null;
				}

				if (0 != wengertIndex && !wengertJacobian.accumulatePartialFirstDerivative (
					wengertIndex,
					wengertIndex - 1,
					-1. * (endTime - 1.) * Math.pow (currentToPreviousDirtyParFloaterRatio, endTime)
				))
				{
					return null;
				}
			}
		}

		return wengertJacobian;
	}

	/**
	 * Get the Muni Latent State Identifier Label
	 * 
	 * @return The Muni Latent State Identifier Label
	 */

	@Override public LatentStateLabel label()
	{
		return MuniLabel.Standard (_code);
	}

	/**
	 * Calculate the Yield to the given Date
	 * 
	 * @param date Date
	 * 
	 * @return The Yield
	 * 
	 * @throws Exception Thrown if the Yield cannot be calculated
	 */

	@Override public double yld (
		final int date)
		throws Exception
	{
		double t = ((double) (date - epoch().julian())) / 365.25;

		TreeMap<Double, ZeroVolatilityPeriodState> timeToZeroVolatilityPeriodStateMap =
			_kalotayWilliamsFabozzi.timeToZeroVolatilityPeriodStateMap();

		ZeroVolatilityPeriodState leftSegmentState =
			timeToZeroVolatilityPeriodStateMap.firstEntry().getValue();

		if (t <= leftSegmentState.period().endTime()) {
			return leftSegmentState.forwardYield();
		}

		ZeroVolatilityPeriodState rightSegmentState =
			timeToZeroVolatilityPeriodStateMap.lastEntry().getValue();

		if (t >= rightSegmentState.period().endTime()) {
			return rightSegmentState.cumulativeMarketYield();
		}

		for (ZeroVolatilityPeriodState zeroVolatilityPeriodState :
			timeToZeroVolatilityPeriodStateMap.values())
		{
			KalotayWilliamsFabozziPeriod period = zeroVolatilityPeriodState.period();

			if (zeroVolatilityPeriodState.period().in11 (t)) {
				double terminalDiscountFactor = zeroVolatilityPeriodState.cumulativeBeginDiscountFactor() *
					Math.pow (1. + zeroVolatilityPeriodState.forwardYield(), period.startTime() - t);

				return Math.pow (terminalDiscountFactor, -1. / t) - 1.;
			}
		}

		return Double.NaN;
	}

	/**
	 * Set the Value/Slope at the Node specified by the Index
	 * 
	 * @param index Node Index
	 * @param value Node Value
	 * 
	 * @return Success (true), failure (false)
	 */

	@Override public boolean setNodeValue (
		final int index,
		final double value)
	{
		return _kalotayWilliamsFabozzi.augmentTimeToZeroVolatilityPeriodStateMap (index, value);
	}

	/**
	 * Bump the Node Value at the Node specified the Index by the Value
	 * 
	 * @param index Node Index
	 * @param value Node Bump Value
	 * 
	 * @return Success (true), failure (false)
	 */

	@Override public boolean bumpNodeValue (
		final int index,
		final double value)
	{
		return _kalotayWilliamsFabozzi.bumpZeroVolatilityPeriodState (index, value);
	}

	/**
	 * Set the Flat Value across all the Nodes
	 * 
	 * @param value Node Value
	 * 
	 * @return Success (true), failure (false)
	 */

	@Override public boolean setFlatValue (
		final double value)
	{
		return (
			_kalotayWilliamsFabozzi = new KalotayWilliamsFabozzi()
		).augmentTimeToZeroVolatilityPeriodStateMap (
			1.,
			value
		);
	}

	/**
	 * Retrieve the Manifest Measure Jacobian of the Forward Rate to the given date
	 * 
	 * @param manifestMeasure Manifest Measure
	 * @param date Date
	 * 
	 * @return The Manifest Measure Jacobian of the Forward Rate to the given date
	 */

	@Override public WengertJacobian jackDForwardDManifestMeasure (
		final String manifestMeasure,
		final int date)
	{
		double t = ((double) (date - epoch().julian())) / 365.25;

		WengertJacobian jackDForwardDManifestMeasure = jackDForwardDManifestMeasure();

		TreeMap<Double, ZeroVolatilityPeriodState> timeToZeroVolatilityPeriodStateMap =
			_kalotayWilliamsFabozzi.timeToZeroVolatilityPeriodStateMap();

		for (double endTime : _kalotayWilliamsFabozzi.timeKeySet()) {
			try {
				int segmentCount = _kalotayWilliamsFabozzi.segmentCount();

				if (timeToZeroVolatilityPeriodStateMap.get (endTime).period().in11 (t)) {
					WengertJacobian datedJackDForwardDManifestMeasure = new WengertJacobian (
						1,
						segmentCount
					);

					for (int parameterIndex = 0; parameterIndex < segmentCount; ++parameterIndex) {
						if (!datedJackDForwardDManifestMeasure.accumulatePartialFirstDerivative (
							0,
							parameterIndex,
							jackDForwardDManifestMeasure.firstDerivative ((int) endTime - 1, parameterIndex)
						))
						{
							return null;
						}
					}

					return datedJackDForwardDManifestMeasure;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}
}
