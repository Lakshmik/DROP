
package org.drip.dynamics.kwf1993;

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
 * <i>KalotayWilliamsFabozziTree</i> implements the Kalotay, Williams, and Fabozzi (1993) calibrated Tree
 * 	Grid for valuing bonds with Embedded Options. The References are:
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

public class KalotayWilliamsFabozziTree
{
	private TreeMap<Double, List<Double>> _timeProjectedPeriodYieldScalingMap = null;
	private TreeMap<Double, ZeroVolatilityPeriodState> _timeZeroVolatilityPeriodStateMap = null;
	private TreeMap<Double, List<KalotayWilliamsFabozziPeriodState>> _timeProjectedPeriodStateMap = null;

	/**
	 * <i>KalotayWilliamsFabozziTree</i> Constructor
	 * 
	 * @param timeZeroVolatilityPeriodStateMap Time Map of <i>ZeroVolatilityPeriodState</i> Instances
	 * @param timeProjectedPeriodYieldScalingMap Time Map of the Projected List of Yield Scalings
	 * @param timeProjectedPeriodStateMap Time Map of the Projected List of
	 * 		<i>KalotayWilliamsFabozziPeriodState</i> Instances
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public KalotayWilliamsFabozziTree (
		final TreeMap<Double, ZeroVolatilityPeriodState> timeZeroVolatilityPeriodStateMap,
		final TreeMap<Double, List<Double>> timeProjectedPeriodYieldScalingMap,
		final TreeMap<Double, List<KalotayWilliamsFabozziPeriodState>> timeProjectedPeriodStateMap)
		throws Exception
	{
		if (null == (_timeZeroVolatilityPeriodStateMap = timeZeroVolatilityPeriodStateMap) ||
				0 == timeZeroVolatilityPeriodStateMap.size() ||
			null == (_timeProjectedPeriodYieldScalingMap = timeProjectedPeriodYieldScalingMap) ||
				0 == _timeProjectedPeriodYieldScalingMap.size() ||
			null == (_timeProjectedPeriodStateMap = timeProjectedPeriodStateMap) ||
				0 == _timeProjectedPeriodStateMap.size())
		{
			throw new Exception ("KalotayWilliamsFabozziTree Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Time Map of <i>ZeroVolatilityPeriodState</i> Instances
	 * 
	 * @return Time Map of <i>ZeroVolatilityPeriodState</i> Instances
	 */

	public TreeMap<Double, ZeroVolatilityPeriodState> timeZeroVolatilityPeriodStateMap()
	{
		return _timeZeroVolatilityPeriodStateMap;
	}

	/**
	 * Retrieve the Time Map of the Projected List of Yield Scalings
	 * 
	 * @return Time Map of the Projected List of Yield Scalings
	 */

	public TreeMap<Double, List<Double>> timeProjectedPeriodYieldScalingMap()
	{
		return _timeProjectedPeriodYieldScalingMap;
	}

	/**
	 * Retrieve the Time Map of the Projected List of <i>KalotayWilliamsFabozziPeriodState</i> Instances
	 * 
	 * @return Time Map of the Projected List of <i>KalotayWilliamsFabozziPeriodState</i> Instances
	 */

	public TreeMap<Double, List<KalotayWilliamsFabozziPeriodState>> timeProjectedPeriodStateMap()
	{
		return _timeProjectedPeriodStateMap;
	}

	/**
	 * Add a <i>ZeroVolatilityPeriodState</i> Instance
	 * 
	 * @param zeroVolatilityPeriodState <i>ZeroVolatilityPeriodState</i> Instance
	 * 
	 * @return TRUE - The <i>ZeroVolatilityPeriodState</i> Instance successfully Added
	 */

	public boolean addZeroVolatilityPeriodState (
		final ZeroVolatilityPeriodState zeroVolatilityPeriodState)
	{
		if (null == zeroVolatilityPeriodState) {
			return false;
		}

		_timeZeroVolatilityPeriodStateMap.put (
			zeroVolatilityPeriodState.period().startTime(),
			zeroVolatilityPeriodState
		);

		return true;
	}

	/**
	 * Add a Projected Yield Scaling
	 * 
	 * @param startTime Period Start Time
	 * @param projectedYieldScaling Projected Yield Scaling
	 * 
	 * @return TRUE - The Projected Yield Scaling successfully Added
	 */

	public boolean addProjectedYieldScaling (
		final double startTime,
		final double projectedYieldScaling)
	{
		if (!NumberUtil.IsValid (startTime) || 0. >= startTime ||
			!NumberUtil.IsValid (projectedYieldScaling) || 1. > projectedYieldScaling)
		{
			return false;
		}

		if (_timeProjectedPeriodYieldScalingMap.containsKey (startTime)) {
			_timeProjectedPeriodYieldScalingMap.get (startTime).add (projectedYieldScaling);
		} else {
			List<Double> projectedYieldScalingList = new ArrayList<Double>();

			projectedYieldScalingList.add (projectedYieldScaling);

			_timeProjectedPeriodYieldScalingMap.put (startTime, projectedYieldScalingList);
		}

		return true;
	}

	/**
	 * Add a Projected <i>KalotayWilliamsFabozziPeriodState</i> Instance
	 * 
	 * @param projectedPeriodState Projected <i>KalotayWilliamsFabozziPeriodState</i> Instance
	 * 
	 * @return TRUE - The Projected <i>KalotayWilliamsFabozziPeriodState</i> Instance successfully Added
	 */

	public boolean addProjectedPeriodState (
		final KalotayWilliamsFabozziPeriodState projectedPeriodState)
	{
		if (null == projectedPeriodState) {
			return false;
		}

		double startTime = projectedPeriodState.period().startTime();

		if (_timeProjectedPeriodStateMap.containsKey (startTime)) {
			_timeProjectedPeriodStateMap.get (startTime).add (projectedPeriodState);
		} else {
			List<KalotayWilliamsFabozziPeriodState> projectedPeriodStateList =
				new ArrayList<KalotayWilliamsFabozziPeriodState>();

			projectedPeriodStateList.add (projectedPeriodState);

			_timeProjectedPeriodStateMap.put (startTime, projectedPeriodStateList);
		}

		return true;
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
		return prefix + "Zero Volatility Period State Map => " + _timeZeroVolatilityPeriodStateMap +
			"; Projected Period Yield Scaling Map => " + _timeProjectedPeriodYieldScalingMap +
			"; Projected Period State Map => " + _timeProjectedPeriodStateMap;
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
