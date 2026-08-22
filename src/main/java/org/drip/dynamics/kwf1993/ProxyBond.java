
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
 * <i>ProxyBond</i> implements a Bond Proxy to be used in the Kalotay, Williams, and Fabozzi (1993)
 *  Tree-based Model for valuing bonds with Embedded Options. The References are:
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

public class ProxyBond
{
	private double _coupon = Double.NaN;
	private double _maturityTime = Double.NaN;
	private TreeMap<Double, Double> _putPriceSchedule = null;
	private TreeMap<Double, Double> _callPriceSchedule = null;

	/**
	 * Construct a Bullet Instance of <i>ProxyBond</i>
	 * 
	 * @param maturityTime Bond Maturity Time
	 * @param coupon Bond Coupon
	 * 
	 * @return Bullet Instance of <i>ProxyBond</i>
	 */

	public static final ProxyBond Bullet (
		final double maturityTime,
		final double coupon)
	{
		try {
			return new ProxyBond (maturityTime, coupon, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct a Callable Instance of <i>ProxyBond</i>
	 * 
	 * @param maturityTime Bond Maturity Time
	 * @param coupon Bond Coupon
	 * @param callPriceSchedule Call Price Schedule
	 * 
	 * @return Callable Instance of <i>ProxyBond</i>
	 */

	public static final ProxyBond Callable (
		final double maturityTime,
		final double coupon,
		final TreeMap<Double, Double> callPriceSchedule)
	{
		try {
			return new ProxyBond (maturityTime, coupon, callPriceSchedule, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct a Puttable Instance of <i>ProxyBond</i>
	 * 
	 * @param maturityTime Bond Maturity Time
	 * @param coupon Bond Coupon
	 * @param putPriceSchedule Put Price Schedule
	 * 
	 * @return Puttable Instance of <i>ProxyBond</i>
	 */

	public static final ProxyBond Puttable (
		final double maturityTime,
		final double coupon,
		final TreeMap<Double, Double> putPriceSchedule)
	{
		try {
			return new ProxyBond (maturityTime, coupon, null, putPriceSchedule);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * <i>ProxyBond</i> Constructor
	 * 
	 * @param maturityTime Bond Maturity Time
	 * @param coupon Bond Coupon
	 * @param callPriceSchedule Call Price Schedule
	 * @param putPriceSchedule Put Price Schedule
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public ProxyBond (
		final double maturityTime,
		final double coupon,
		final TreeMap<Double, Double> callPriceSchedule,
		final TreeMap<Double, Double> putPriceSchedule)
		throws Exception
	{
		if (!NumberUtil.IsValid (_maturityTime = maturityTime) || 0. >= _maturityTime ||
			!NumberUtil.IsValid (_coupon = coupon) || 0. > _coupon)
		{
			throw new Exception ("ProxyBond Constructor => Invalid Inputs");
		}

		_callPriceSchedule = callPriceSchedule;
		_putPriceSchedule = putPriceSchedule;
	}

	/**
	 * Retrieve the Bond Maturity Time
	 * 
	 * @return Bond Maturity Time
	 */

	public double maturityTime()
	{
		return _maturityTime;
	}

	/**
	 * Retrieve the Bond Coupon
	 * 
	 * @return Bond Coupon
	 */

	public double coupon()
	{
		return _coupon;
	}

	/**
	 * Retrieve the Bond Call Price Schedule
	 * 
	 * @return Bond Call Price Schedule
	 */

	public TreeMap<Double, Double> callPriceSchedule()
	{
		return _callPriceSchedule;
	}

	/**
	 * Retrieve the Bond Put Price Schedule
	 * 
	 * @return Bond Put Price Schedule
	 */

	public TreeMap<Double, Double> putPriceSchedule()
	{
		return _putPriceSchedule;
	}

	/**
	 * Construct the Bond Value Tree using the Calibrated <i>KalotayWilliamsFabozziTree</i> Instance
	 * 
	 * @param kalotayWilliamsFabozziTree Calibrated <i>KalotayWilliamsFabozziTree</i> Instance
	 * 
	 * @return Bond Value Tree
	 */

	public TreeMap<Double, List<Double>> value (
		final KalotayWilliamsFabozziTree kalotayWilliamsFabozziTree)
	{
		if (null == kalotayWilliamsFabozziTree) {
			return null;
		}

		double endTimePayoff = 1. + _coupon;
		List<Double> aboveProjectedBondValueList = null;

		TreeMap<Double, List<Double>> valueTree = new TreeMap<Double, List<Double>>();

		TreeMap<Double, List<KalotayWilliamsFabozziPeriodState>> timeProjectedPeriodStateMap =
			kalotayWilliamsFabozziTree.timeProjectedPeriodStateMap();

		for (double endTime : timeProjectedPeriodStateMap.descendingKeySet()) {
			if (endTime > _maturityTime) {
				continue;
			}

			List<Double> projectedBondValueList = new ArrayList<Double>();

			List<KalotayWilliamsFabozziPeriodState> projectedPeriodStateList =
				timeProjectedPeriodStateMap.get (endTime);

			double startTime = projectedPeriodStateList.get (0).period().startTime();

			for (int projectionIndex = 0;
				projectionIndex < projectedPeriodStateList.size();
				++projectionIndex)
			{
				double aboveNodeBondPriceAverage = null == aboveProjectedBondValueList ?
					endTimePayoff : _coupon + 0.5 * (
						aboveProjectedBondValueList.get (projectionIndex) +
							aboveProjectedBondValueList.get (projectionIndex + 1)
					);

				double discountedPrice = aboveNodeBondPriceAverage *
					projectedPeriodStateList.get (projectionIndex).forwardDiscountFactor();

				if (null != _callPriceSchedule && _callPriceSchedule.containsKey (startTime)) {
					discountedPrice = Math.min (discountedPrice, _callPriceSchedule.get (startTime));
				}

				if (null != _putPriceSchedule && _putPriceSchedule.containsKey (startTime)) {
					discountedPrice = Math.max (discountedPrice, _putPriceSchedule.get (startTime));
				}

				projectedBondValueList.add (discountedPrice);
			}

			valueTree.put (endTime, aboveProjectedBondValueList = projectedBondValueList);
		}

		return valueTree;
	}
}
