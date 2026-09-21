
package org.drip.sample.forward;

import org.drip.analytics.date.*;
import org.drip.function.r1tor1custom.QuadraticRationalShapeControl;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.PolynomialFunctionSetParams;
import org.drip.spline.params.*;
import org.drip.spline.stretch.MultiSegmentSequenceBuilder;
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
 * <i>IBOR6MQuarticPolyVanilla</i> illustrates the Construction and Usage of the IBOR 6M Forward Curve Using
 * 	Vanilla Quartic Polynomial Spline. It exposes the following Functions:
 *
 *  <ul>
 * 		<li>Construct the 6M Forward Curve</li>
 *  </ul>
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/forward/README.md">IBOR Spline Forward Curve Construction</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class IBOR6MQuarticPolyVanilla
{

	/**
	 * Construct the 6M Forward
	 * 
	 * @param valueDate Valuation Date
	 * @param currency Currency
	 * @param tenor Tenor
	 * 
	 * @return The Forward Curve
	 * 
	 * @throws Exception Thrown if the Forward Curve cannot be constructed
	 */

	public static final ForwardCurve Make6MForward (
		final JulianDate valueDate,
		final String currency,
		final String tenor)
		throws Exception
	{
		ForwardCurve forwardCurve = IBORCurve.CustomIBORBuilderSample (
			OvernightIndexCurve.MakeDC (valueDate, currency),
			null,
			ForwardLabel.Create (currency, tenor),
			new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
				new PolynomialFunctionSetParams (5),
				SegmentInelasticDesignControl.Create (2, 2),
				new ResponseScalingShapeControl (true, new QuadraticRationalShapeControl (0.)),
				null
			),
			new String[]
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
			},
			new double[]
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
			},
			"ForwardRate",
			new String[]
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
			},
			new double[]
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
			},
			"ParForwardRate",
			new String[]
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
			},
			new double[]
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
			},
			"SwapRate",
			null,
			null,
			"DerivedParBasisSpread",
			null,
			null,
			"DerivedParBasisSpread",
			"---- EURIBOR 6M VANILLA QUARTIC POLYNOMIAL FORWARD CURVE ---",
			true
		);

		IBORCurve.ForwardJack (
			valueDate,
			"---- EURIBOR 6M VANILLA QUARTIC POLYNOMIAL FORWARD CURVE SENSITIVITY ---",
			forwardCurve,
			"DerivedParBasisSpread"
		);

		return forwardCurve;
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

		Make6MForward (DateUtil.CreateFromYMD (2012, DateUtil.DECEMBER, 11), "EUR", "6M");

		EnvManager.TerminateEnv();
	}
}
