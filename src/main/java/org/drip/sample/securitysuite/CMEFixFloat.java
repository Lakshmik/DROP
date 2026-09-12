
package org.drip.sample.securitysuite;

import java.util.Map;

import org.drip.analytics.date.*;
import org.drip.analytics.support.CaseInsensitiveTreeMap;
import org.drip.market.otc.*;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.valuation.ValuationParams;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.service.template.LatentMarketStateBuilder;
import org.drip.state.discount.MergedDiscountForwardCurve;
import org.drip.state.forward.ForwardCurve;
import org.drip.state.identifier.ForwardLabel;

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
 * <i>CMEFixFloat</i> demonstrates the Analytics Calculation/Reconciliation for the CME Cleared Fix-Float
 * 	IRS.
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/securitysuite/README.md">Custom Security Relative Value Demonstration</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class CMEFixFloat
{

	private static final MergedDiscountForwardCurve OvernightCurve (
		final JulianDate spotDate,
		final String currency)
		throws Exception
	{
		EnvManager.InitEnv ("");

		return LatentMarketStateBuilder.SmoothOvernightCurve (
			spotDate,
			currency,
			new String[]
			{
				"1D",
			},
			new double[]
			{
				0.0116,		// 1D
			},
			"Rate",
			new String[]
			{
				 "1W",
				 "2W",
				 "3W",
				 "1M",
				 "2M",
				 "3M",
				 "4M",
				 "5M",
				 "6M",
				 "9M",
				"12M",
				"18M",
				 "2Y",
				 "3Y",
				 "4Y",
				 "5Y",
				"10Y",
			},
			new double[]
			{
				0.0117,    //   1W
				0.0115,    //   2W
				0.0116,    //   3W
				0.0116,    //   1M
				0.0120,    //   2M
				0.0125,    //   3M
				0.0128,    //   4M
				0.0131,    //   5M
				0.0133,    //   6M
				0.0139,    //   9M
				0.0146,    //  12M
				0.0154,    //  18M
				0.0161,    //   2Y
				0.0171,    //   3Y
				0.0179,    //   4Y
				0.0185,    //   5Y
				0.0206,    //  10Y
			},
			"SwapRate",
			null,
			null,
			null,
			"SwapRate",
			null,
			null,
			"SwapRate"
		);
	}

	/**
	 * Entry Point
	 * 
	 * @param argumentArray Command Line Argument Array
	 * 
	 * @throws Exception Thrown on Error/Exception Situation
	 */

	public static final void main (
		final String[] argumentArray)
		throws Exception
	{
		EnvManager.InitEnv ("");

		String currency = "USD";
		String forwardTenor = "3M";
		String maturityTenor = "7Y";
		double fixedCoupon = 0.021893;

		JulianDate spotDate = DateUtil.CreateFromYMD (2017, DateUtil.SEPTEMBER, 1);

		JulianDate effectiveDate = DateUtil.CreateFromYMD (2017, DateUtil.JULY, 8);

		MergedDiscountForwardCurve overnightCurve = OvernightCurve (spotDate, currency);

		ForwardLabel forwardLabel = ForwardLabel.Create (currency, forwardTenor);

		ForwardCurve forwardCurve = LatentMarketStateBuilder.ShapePreservingForwardCurve (
			spotDate,
			forwardLabel,
			new String[]
			{
				"1D",
			},
			new double[]
			{
				0.013161,	// 1D
			},
			"ForwardRate",
			null,
			null,
			"ParForwardRate",
			new String[]
			{
				"02Y",
				"03Y",
				"04Y",
				"05Y",
				"06Y",
				"07Y",
				"08Y",
				"09Y",
				"10Y",
				"11Y",
				"12Y",
				"15Y",
				"20Y",
				"25Y",
				"30Y",
				"40Y",
				"50Y",
			},
			new double[]
			{
				0.015540,	//  2Y
				0.016423,	//  3Y
				0.017209,	//  4Y			
				0.017980,	//  5Y
				0.018743,	//  6Y
				0.019455,	//  7Y
				0.020080,	//  8Y
				0.020651,	//  9Y
				0.021195,	// 10Y
				0.021651,	// 11Y
				0.022065,	// 12Y
				0.022952,	// 15Y
				0.023825,	// 20Y
				0.024175,	// 25Y
				0.024347,	// 30Y
				0.024225,	// 40Y
				0.023968,	// 50Y
			},
			"SwapRate",
			null,
			null,
			"DerivedParBasisSpread",
			null,
			null,
			"DerivedParBasisSpread",
			overnightCurve,
			null
		);

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = new CurveSurfaceQuoteContainer();

		curveSurfaceQuoteContainer.setFundingState (overnightCurve);

		curveSurfaceQuoteContainer.setForwardState (forwardCurve);

		CaseInsensitiveTreeMap<Double> measureMap = IBORFixedFloatContainer.ConventionFromJurisdiction (
			currency,
			"ALL",
			maturityTenor,
			"MAIN"
		).createFixFloatComponent (
			effectiveDate,
			maturityTenor,
			fixedCoupon,
			0.,
			1.
		).value (
			new ValuationParams (spotDate, spotDate, currency),
			null,
			curveSurfaceQuoteContainer,
			null
		);

		for (Map.Entry<String, Double> measureMapEntry : measureMap.entrySet()) {
			System.out.println ("\t|| " + measureMapEntry.getKey() + " => " + measureMapEntry.getValue());
		}

		System.out.println();

		System.out.println (
			"\t|| Clean Price       =>" + FormatUtil.FormatDouble (measureMap.get ("CleanPrice"), 1, 4, 1.)
		);

		System.out.println (
			"\t|| Dirty Price       =>" + FormatUtil.FormatDouble (measureMap.get ("DirtyPrice"), 1, 4, 1.)
		);

		System.out.println (
			"\t|| Fixed Stream PV   =>  " +
				FormatUtil.FormatDouble (measureMap.get ("ReferencePV"), 1, 8, 1.)
		);

		System.out.println (
			"\t|| Float Stream PV   =>  " +
				FormatUtil.FormatDouble (measureMap.get ("DerivedPV"), 1, 8, 1.)
		);

		System.out.println (
			"\t|| Fixed Stream PV   =>  " +
				FormatUtil.FormatDouble (measureMap.get ("ReferencePV"), 1, 8, 1.)
		);

		System.out.println (
			"\t|| Fixed Stream DV01 =>  " +
				FormatUtil.FormatDouble (measureMap.get ("ReferenceDV01"), 1, 8, 10000.)
		);

		System.out.println (
			"\t|| Float Stream DV01 =>  " +
				FormatUtil.FormatDouble (measureMap.get ("DerivedDV01"), 1, 8, 10000.)
		);

		System.out.println (
			"\t|| Fixing 01         =>  " +
				FormatUtil.FormatDouble (measureMap.get ("Fixing01"), 1, 8, 10000.)
		);

		System.out.println (
			"\t|| Clean PV          =>  " +
				FormatUtil.FormatDouble (measureMap.get ("CleanPV"), 1, 8, 1.)
		);

		System.out.println (
			"\t|| Dirty PV          =>  " +
				FormatUtil.FormatDouble (measureMap.get ("DirtyPV"), 1, 8, 1.)
		);

		System.out.println (
			"\t|| Fixed Accrued     =>  " +
				FormatUtil.FormatDouble (measureMap.get ("FixedAccrued"), 1, 8, 1.)
		);

		System.out.println (
			"\t|| Float Accrued     =>  " +
				FormatUtil.FormatDouble (measureMap.get ("FloatAccrued"), 1, 8, 1.)
		);

		System.out.println (
			"\t|| Accrued           =>  " +
				FormatUtil.FormatDouble (measureMap.get ("Accrued"), 1, 8, 1.)
		);

		System.out.println (
			"\t|| Par Swap Rate     =>  " +
				FormatUtil.FormatDouble (measureMap.get ("ParSwapRate"), 1, 4, 100.) + "%"
		);

		EnvManager.TerminateEnv();
 	}
}
