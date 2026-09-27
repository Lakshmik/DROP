
package org.drip.sample.dual;

import org.drip.analytics.date.*;
import org.drip.function.r1tor1custom.QuadraticRationalShapeControl;
import org.drip.sample.forward.*;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.PolynomialFunctionSetParams;
import org.drip.spline.params.*;
import org.drip.spline.stretch.*;
import org.drip.state.discount.*;
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
 * Copyright (C) 2016 Lakshmi Krishnamurthy
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
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
 * <i>CAD3M6MUSD3M6M</i> demonstrates the setup and construction of the USD 3M Forward Curve from
 * 	CAD3M6MUSD3M6M CCBS, CAD 3M, CAD 6M, and USD 6M Quotes.
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/capfloor/README.md">FRA Standard Cap Floor Valuation</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class CAD3M6MUSD3M6M
{
	private static final double FX_CADUSD = 0.9168;

	private static final int[] USD_OIS_DEPOSIT_MATURITY_DAYS_ARRAY =
	{
		1,
		2,
		3
	};
	private static final int[] CAD_OIS_DEPOSIT_MATURITY_DAYS_ARRAY =
	{
		1,
		2,
		3
	};
	private static final double[] USD_OIS_DEPOSIT_QUOTE_ARRAY =
	{
		0.0004,	// 1D
		0.0004,	// 2D
		0.0004	// 3D
	};
	private static final double[] CAD_OIS_DEPOSIT_QUOTE_ARRAY =
	{
		0.0004,	// 1D
		0.0004,	// 2D
		0.0004	// 3D
	};
	private static final String[] USD_SHORT_END_OIS_MATURITY_TENOR_ARRAY =
	{
		"1W",
		"2W",
		"3W",
		"1M"
	};
	private static final String[] CAD_SHORT_END_OIS_MATURITY_TENOR_ARRAY =
	{
		"1W",
		"2W",
		"3W",
		"1M"
	};
	private static final double[] USD_SHORT_END_OIS_MATURITY_QUOTE_ARRAY =
	{
		0.00070,    //   1W
		0.00069,    //   2W
		0.00078,    //   3W
		0.00074     //   1M
	};
	private static final double[] CAD_SHORT_END_OIS_MATURITY_QUOTE_ARRAY =
	{
		0.00070,    //   1W
		0.00069,    //   2W
		0.00078,    //   3W
		0.00074     //   1M
	};
	private static final String[] USD_OIS_FUTURE_TENOR_ARRAY =
	{
		"1M",
		"1M",
		"1M",
		"1M",
		"1M"
	};
	private static final String[] CAD_OIS_FUTURE_TENOR_ARRAY =
	{
		"1M",
		"1M",
		"1M",
		"1M",
		"1M"
	};
	private static final String[] USD_OIS_FUTURE_MATURITY_TENOR_ARRAY =
	{
		"1M",
		"2M",
		"3M",
		"4M",
		"5M"
	};
	private static final String[] CAD_OIS_FUTURE_MATURITY_TENOR_ARRAY =
	{
		"1M",
		"2M",
		"3M",
		"4M",
		"5M"
	};
	private static final double[] USD_OIS_FUTURE_QUOTE_ARRAY =
	{
		 0.00046,    //   1M x 1M
		 0.00016,    //   2M x 1M
		-0.00007,    //   3M x 1M
		-0.00013,    //   4M x 1M
		-0.00014     //   5M x 1M
	};
	private static final double[] CAD_OIS_FUTURE_QUOTE_ARRAY =
	{
		 0.00046,    //   1M x 1M
		 0.00016,    //   2M x 1M
		-0.00007,    //   3M x 1M
		-0.00013,    //   4M x 1M
		-0.00014     //   5M x 1M
	};
	private static final String[] USD_LONG_END_OIS_MATURITY_TENOR_ARRAY =
	{
		"15M",
		"18M",
		"21M",
		"2Y",
		"3Y",
		"4Y",
		"5Y",
		"6Y",
		"7Y",
		"8Y",
		"9Y",
		"10Y",
		"11Y",
		"12Y",
		"15Y",
		"20Y",
		"25Y",
		"30Y"
	};
	private static final String[] CAD_LONG_END_OIS_MATURITY_TENOR_ARRAY =
	{
		"15M",
		"18M",
		"21M",
		"2Y",
		"3Y",
		"4Y",
		"5Y",
		"6Y",
		"7Y",
		"8Y",
		"9Y",
		"10Y",
		"11Y",
		"12Y",
		"15Y",
		"20Y",
		"25Y",
		"30Y"
	};
	private static final double[] USD_LONG_END_OIS_MATURITY_QUOTE_ARRAY =
	{
		0.00002,    //  15M
		0.00008,    //  18M
		0.00021,    //  21M
		0.00036,    //   2Y
		0.00127,    //   3Y
		0.00274,    //   4Y
		0.00456,    //   5Y
		0.00647,    //   6Y
		0.00827,    //   7Y
		0.00996,    //   8Y
		0.01147,    //   9Y
		0.01280,    //  10Y
		0.01404,    //  11Y
		0.01516,    //  12Y
		0.01764,    //  15Y
		0.01939,    //  20Y
		0.02003,    //  25Y
		0.02038     //  30Y
	};
	private static final double[] CAD_LONG_END_OIS_MATURITY_QUOTE_ARRAY =
	{
		0.00002,    //  15M
		0.00008,    //  18M
		0.00021,    //  21M
		0.00036,    //   2Y
		0.00127,    //   3Y
		0.00274,    //   4Y
		0.00456,    //   5Y
		0.00647,    //   6Y
		0.00827,    //   7Y
		0.00996,    //   8Y
		0.01147,    //   9Y
		0.01280,    //  10Y
		0.01404,    //  11Y
		0.01516,    //  12Y
		0.01764,    //  15Y
		0.01939,    //  20Y
		0.02003,    //  25Y
		0.02038     //  30Y
	};
	private static final String[] USD_6M_DEPOSIT_TENOR_ARRAY =
	{
		"1D",
		"1W",
		"2W",
		"3W",
		"1M",
		"2M",
		"3M",
		"4M",
		"5M"
	};
	private static final String[] CAD_6M_DEPOSIT_TENOR_ARRAY =
	{
		"1D",
		"1W",
		"2W",
		"3W",
		"1M",
		"2M",
		"3M",
		"4M",
		"5M"
	};
	private static final double[] USD_6M_DEPOSIT_QUOTE_ARRAY =
	{
		0.003565,	// 1D
		0.003858,	// 1W
		0.003840,	// 2W
		0.003922,	// 3W
		0.003869,	// 1M
		0.003698,	// 2M
		0.003527,	// 3M
		0.003342,	// 4M
		0.003225	// 5M
	};
	private static final double[] CAD_6M_DEPOSIT_QUOTE_ARRAY =
	{
		0.003565,	// 1D
		0.003858,	// 1W
		0.003840,	// 2W
		0.003922,	// 3W
		0.003869,	// 1M
		0.003698,	// 2M
		0.003527,	// 3M
		0.003342,	// 4M
		0.003225	// 5M
	};
	private static final String[] USD_6M_FRA_TENOR_ARRAY =
	{
		 "0D",
		 "1M",
		 "2M",
		 "3M",
		 "4M",
		 "5M",
		 "6M",
		 "7M",
		 "8M",
		 "9M",
		"10M",
		"11M",
		"12M",
		"13M",
		"14M",
		"15M",
		"16M",
		"17M",
		"18M"
	};
	private static final String[] CAD_6M_FRA_TENOR_ARRAY =
	{
		 "0D",
		 "1M",
		 "2M",
		 "3M",
		 "4M",
		 "5M",
		 "6M",
		 "7M",
		 "8M",
		 "9M",
		"10M",
		"11M",
		"12M",
		"13M",
		"14M",
		"15M",
		"16M",
		"17M",
		"18M"
	};
	private static final double[] USD_6M_FRA_QUOTE_ARRAY =
	{
		0.003120,	//  0D
		0.002930,	//  1M
		0.002720,	//  2M
		0.002600,	//  3M
		0.002560,	//  4M
		0.002520,	//  5M
		0.002480,	//  6M
		0.002540,	//  7M
		0.002610,	//  8M
		0.002670,	//  9M
		0.002790,	// 10M
		0.002910,	// 11M
		0.003030,	// 12M
		0.003180,	// 13M
		0.003350,	// 14M
		0.003520,	// 15M
		0.003710,	// 16M
		0.003890,	// 17M
		0.004090	// 18M
	};
	private static final double[] CAD_6M_FRA_QUOTE_ARRAY =
	{
		0.003120,	//  0D
		0.002930,	//  1M
		0.002720,	//  2M
		0.002600,	//  3M
		0.002560,	//  4M
		0.002520,	//  5M
		0.002480,	//  6M
		0.002540,	//  7M
		0.002610,	//  8M
		0.002670,	//  9M
		0.002790,	// 10M
		0.002910,	// 11M
		0.003030,	// 12M
		0.003180,	// 13M
		0.003350,	// 14M
		0.003520,	// 15M
		0.003710,	// 16M
		0.003890,	// 17M
		0.004090	// 18M
	};
	private static final String[] USD_6M_FIX_FLOAT_TENOR_ARRAY =
	{
		 "3Y",
		 "4Y",
		 "5Y",
		 "6Y",
		 "7Y",
		 "8Y",
		 "9Y",
		"10Y",
		"12Y",
		"15Y",
		"20Y",
		"25Y",
		"30Y",
		"35Y",
		"40Y",
		"50Y",
		"60Y"
	};
	private static final String[] CAD_6M_FIX_FLOAT_TENOR_ARRAY =
	{
		 "3Y",
		 "4Y",
		 "5Y",
		 "6Y",
		 "7Y",
		 "8Y",
		 "9Y",
		"10Y",
		"12Y",
		"15Y",
		"20Y",
		"25Y",
		"30Y",
		"35Y",
		"40Y",
		"50Y",
		"60Y"
	};
	private static final double[] USD_6M_FIX_FLOAT_QUOTE_ARRAY =
	{
		0.004240,	//  3Y
		0.005760,	//  4Y			
		0.007620,	//  5Y
		0.009540,	//  6Y
		0.011350,	//  7Y
		0.013030,	//  8Y
		0.014520,	//  9Y
		0.015840,	// 10Y
		0.018090,	// 12Y
		0.020370,	// 15Y
		0.021870,	// 20Y
		0.022340,	// 25Y
		0.022560,	// 30Y
		0.022950,	// 35Y
		0.023480,	// 40Y
		0.024210,	// 50Y
		0.024630	// 60Y
	};
	private static final double[] CAD_6M_FIX_FLOAT_QUOTE_ARRAY =
	{
		0.004240,	//  3Y
		0.005760,	//  4Y			
		0.007620,	//  5Y
		0.009540,	//  6Y
		0.011350,	//  7Y
		0.013030,	//  8Y
		0.014520,	//  9Y
		0.015840,	// 10Y
		0.018090,	// 12Y
		0.020370,	// 15Y
		0.021870,	// 20Y
		0.022340,	// 25Y
		0.022560,	// 30Y
		0.022950,	// 35Y
		0.023480,	// 40Y
		0.024210,	// 50Y
		0.024630	// 60Y
	};
	private static final String[] USD_3M_DEPOSIT_TENOR_ARRAY =
	{
		"2W",
		"3W",
		"1M",
		"2M"
	};
	private static final double[] s_adblUSD3MDepositQuote =
	{
		0.001865,
		0.001969,
		0.001951,
		0.001874
	};
	private static final String[] USD_3M_FRA_TENOR_ARRAY =
	{
		 "0D",
		 "1M",
		 "3M",
		 "6M",
		 "9M",
		"12M",
		"15M",
		"18M",
		"21M"
	};
	private static final double[] USD_3M_FRA_QUOTE_ARRAY =
	{
		0.001790,
		0.001775,
		0.001274,
		0.001222,
		0.001269,
		0.001565,
		0.001961,
		0.002556,
		0.003101
	};
	private static final String[] USD_3M_FIX_FLOAT_TENOR_ARRAY =
	{
		 "3Y",
		 "4Y",
		 "5Y",
		 "6Y",
		 "7Y",
		 "8Y",
		 "9Y",
		"10Y",
		"12Y",
		"15Y",
		"20Y",
		"25Y",
		"30Y"
	};
	private static final double[] USD_3M_FIX_FLOAT_QUOTE_ARRAY =
	{
		0.002850,	//  3Y
		0.004370,	//  4Y
		0.006230,	//  5Y
		0.008170,	//  6Y
		0.010000,	//  7Y
		0.011710,	//  8Y
		0.013240,	//  9Y
		0.014590,	// 10Y
		0.016920,	// 12Y
		0.019330,	// 15Y
		0.020990,	// 20Y
		0.021560,	// 25Y
		0.021860 	// 30Y
	};
	private static final String[] USD_3M_SYNTHETIC_FLOAT_FLOAT_TENOR_ARRAY =
	{
		"35Y",
		"40Y",
		"50Y",
		"60Y"
	};
	private static final double[] USD_3M_SYNTHETIC_FLOAT_FLOAT_QUOTE_ARRAY =
	{
		0.00065,
		0.00060,
		0.00054,
		0.00050
	};
	private static final String[] CCBS_TENOR_ARRAY =
	{
		"1Y",
		"2Y",
		"3Y",
		"4Y",
		"5Y",
		"7Y",
		"10Y",
		"15Y",
		"20Y"
	};
	private static final double[] CCBS_QUOTE_ARRAY =
	{
		0.001050, //  1Y
		0.001150, //  2Y
		0.001225, //  3Y
		0.001325, //  4Y
		0.001425, //  5Y
		0.001500, //  7Y
		0.001500, // 10Y
		0.001525, // 15Y
		0.001525  // 20Y
	};
	private static final double[] IRS_QUOTE_ARRAY =
	{
		0.01050, //  1Y
		0.01150, //  2Y
		0.01225, //  3Y
		0.01325, //  4Y
		0.01425, //  5Y
		0.01500, //  7Y
		0.01500, // 10Y
		0.01525, // 15Y
		0.01525  // 20Y
	};

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

		String derivedCurrency = "CAD";
		String referenceCurrency = "USD";

		JulianDate valueDate = DateUtil.CreateFromYMD (2012, DateUtil.DECEMBER, 11);

		SegmentCustomBuilderControl cubicSegmentCustomBuilderControl = new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
			new PolynomialFunctionSetParams (4),
			SegmentInelasticDesignControl.Create (2, 2),
			new ResponseScalingShapeControl (true, new QuadraticRationalShapeControl (0.)),
			null
		);

		MergedDiscountForwardCurve referenceDiscountCurve = OvernightIndexCurve.MakeDC (
			referenceCurrency,
			valueDate,
			USD_OIS_DEPOSIT_MATURITY_DAYS_ARRAY,
			USD_OIS_DEPOSIT_QUOTE_ARRAY,
			USD_SHORT_END_OIS_MATURITY_TENOR_ARRAY,
			USD_SHORT_END_OIS_MATURITY_QUOTE_ARRAY,
			USD_OIS_FUTURE_TENOR_ARRAY,
			USD_OIS_FUTURE_MATURITY_TENOR_ARRAY,
			USD_OIS_FUTURE_QUOTE_ARRAY,
			USD_LONG_END_OIS_MATURITY_TENOR_ARRAY,
			USD_LONG_END_OIS_MATURITY_QUOTE_ARRAY,
			cubicSegmentCustomBuilderControl,
			null
		);

		MergedDiscountForwardCurve derivedDiscountCurve = OvernightIndexCurve.MakeDC (
			derivedCurrency,
			valueDate,
			CAD_OIS_DEPOSIT_MATURITY_DAYS_ARRAY,
			CAD_OIS_DEPOSIT_QUOTE_ARRAY,
			CAD_SHORT_END_OIS_MATURITY_TENOR_ARRAY,
			CAD_SHORT_END_OIS_MATURITY_QUOTE_ARRAY,
			CAD_OIS_FUTURE_TENOR_ARRAY,
			CAD_OIS_FUTURE_MATURITY_TENOR_ARRAY,
			CAD_OIS_FUTURE_QUOTE_ARRAY,
			CAD_LONG_END_OIS_MATURITY_TENOR_ARRAY,
			CAD_LONG_END_OIS_MATURITY_QUOTE_ARRAY,
			cubicSegmentCustomBuilderControl,
			null
		);

		ForwardCurve reference6MForwardCurve = IBORCurve.CustomIBORBuilderSample (
			referenceDiscountCurve,
			null,
			ForwardLabel.Create (referenceCurrency, "6M"),
			cubicSegmentCustomBuilderControl,
			USD_6M_DEPOSIT_TENOR_ARRAY,
			USD_6M_DEPOSIT_QUOTE_ARRAY,
			"ForwardRate",
			USD_6M_FRA_TENOR_ARRAY,
			USD_6M_FRA_QUOTE_ARRAY,
			"ParForwardRate",
			USD_6M_FIX_FLOAT_TENOR_ARRAY,
			USD_6M_FIX_FLOAT_QUOTE_ARRAY,
			"SwapRate",
			null,
			null,
			"DerivedParBasisSpread",
			null,
			null,
			"DerivedParBasisSpread",
			"---- USD LIBOR 6M VANILLA CUBIC POLYNOMIAL FORWARD CURVE ---",
			false
		);

		ForwardCurve derived6MForwardCurve = IBORCurve.CustomIBORBuilderSample (
			derivedDiscountCurve,
			null,
			ForwardLabel.Create (derivedCurrency, "6M"),
			cubicSegmentCustomBuilderControl,
			CAD_6M_DEPOSIT_TENOR_ARRAY,
			CAD_6M_DEPOSIT_QUOTE_ARRAY,
			"ForwardRate",
			CAD_6M_FRA_TENOR_ARRAY,
			CAD_6M_FRA_QUOTE_ARRAY,
			"ParForwardRate",
			CAD_6M_FIX_FLOAT_TENOR_ARRAY,
			CAD_6M_FIX_FLOAT_QUOTE_ARRAY,
			"SwapRate",
			null,
			null,
			"DerivedParBasisSpread",
			null,
			null,
			"DerivedParBasisSpread",
			"---- CAD LIBOR 6M VANILLA CUBIC POLYNOMIAL FORWARD CURVE ---",
			false
		);

		ForwardCurve reference3MForwardCurve = IBORCurve.CustomIBORBuilderSample (
			referenceDiscountCurve,
			reference6MForwardCurve,
			ForwardLabel.Create (referenceCurrency, "3M"),
			cubicSegmentCustomBuilderControl,
			USD_3M_DEPOSIT_TENOR_ARRAY,
			s_adblUSD3MDepositQuote,
			"ForwardRate",
			USD_3M_FRA_TENOR_ARRAY,
			USD_3M_FRA_QUOTE_ARRAY,
			"ParForwardRate",
			USD_3M_FIX_FLOAT_TENOR_ARRAY,
			USD_3M_FIX_FLOAT_QUOTE_ARRAY,
			"SwapRate",
			null,
			null,
			"DerivedParBasisSpread",
			USD_3M_SYNTHETIC_FLOAT_FLOAT_TENOR_ARRAY,
			USD_3M_SYNTHETIC_FLOAT_FLOAT_QUOTE_ARRAY,
			"DerivedParBasisSpread",
			"---- VANILLA CUBIC POLYNOMIAL FORWARD CURVE ---",
			false
		);

		CCBSForwardCurve.ForwardCurveReferenceComponentBasis (
			referenceCurrency,
			derivedCurrency,
			valueDate,
			referenceDiscountCurve,
			reference6MForwardCurve,
			reference3MForwardCurve,
			derivedDiscountCurve,
			derived6MForwardCurve,
			FX_CADUSD,
			cubicSegmentCustomBuilderControl,
			CCBS_TENOR_ARRAY,
			CCBS_QUOTE_ARRAY,
			true
		);

		CCBSForwardCurve.ForwardCurveReferenceComponentBasis (
			referenceCurrency,
			derivedCurrency,
			valueDate,
			referenceDiscountCurve,
			reference6MForwardCurve,
			reference3MForwardCurve,
			derivedDiscountCurve,
			derived6MForwardCurve,
			FX_CADUSD,
			cubicSegmentCustomBuilderControl,
			CCBS_TENOR_ARRAY,
			CCBS_QUOTE_ARRAY,
			false
		);

		CCBSDiscountCurve.MakeDiscountCurve (
			referenceCurrency,
			derivedCurrency,
			valueDate,
			referenceDiscountCurve,
			reference6MForwardCurve,
			reference3MForwardCurve,
			FX_CADUSD,
			cubicSegmentCustomBuilderControl,
			CCBS_TENOR_ARRAY,
			CCBS_QUOTE_ARRAY,
			IRS_QUOTE_ARRAY,
			true
		);

		CCBSDiscountCurve.MakeDiscountCurve (
			referenceCurrency,
			derivedCurrency,
			valueDate,
			referenceDiscountCurve,
			reference6MForwardCurve,
			reference3MForwardCurve,
			FX_CADUSD,
			cubicSegmentCustomBuilderControl,
			CCBS_TENOR_ARRAY,
			CCBS_QUOTE_ARRAY,
			IRS_QUOTE_ARRAY,
			false
		);

		EnvManager.TerminateEnv();
	}
}
