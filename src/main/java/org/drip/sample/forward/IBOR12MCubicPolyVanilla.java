
package org.drip.sample.forward;

import org.drip.analytics.date.*;
import org.drip.function.r1tor1custom.QuadraticRationalShapeControl;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.PolynomialFunctionSetParams;
import org.drip.spline.params.*;
import org.drip.spline.stretch.MultiSegmentSequenceBuilder;
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
 * <i>IBOR12MCubicPolyVanilla</i> illustrates the Construction and Usage of the IBOR 12M Forward Curve Using
 * 	Vanilla Cubic Polynomial.
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

public class IBOR12MCubicPolyVanilla
{

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

		JulianDate valueDate = DateUtil.CreateFromYMD (2012, DateUtil.DECEMBER, 11);

		String tenor = "12M";
		String currency = "USD";
		double[] depositQuoteArray =
		{
			0.006537,
			0.006187,
			0.005772,
			0.005563,
			0.005400
		};
		String[] depositTenorArray =
		{
			 "1M",
			 "3M",
			 "6M",
			 "9M",
			"12M"
		};
		double[] fraQuoteArray =
		{
			0.004974,
			0.004783,
			0.004822,
			0.005070,
			0.005481,
			0.006025
		};
		String[] fraTenor =
		{
			 "3M",
			 "6M",
			 "9M",
			"12M",
			"15M",
			"18M",
		};
		double[] floatFloatQuoteArray =
		{
			-0.002070,	//  3Y
			-0.001640,	//  4Y
			-0.001510,	//  5Y
			-0.001390,	//  6Y
			-0.001300,	//  7Y
			-0.001230,	//  8Y
			-0.001180,	//  9Y
			-0.001130,	// 10Y
			-0.001090,	// 11Y
			-0.001060,	// 12Y
			-0.000930,	// 15Y
			-0.000800,	// 20Y
			-0.000720,	// 25Y
			-0.000660	// 30Y
		};
		String[] floatFloatTenorArray =
		{
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
		String[] syntheticFloatFloatTenorArray =
		{
			"35Y",
			"40Y",
			"50Y",
			"60Y"
		};
		double[] syntheticFloatFloatQuoteArray =
		{
			-0.000660,
			-0.000660,
			-0.000660,
			-0.000660
		};

		SegmentCustomBuilderControl quarticSegmentCustomBuilderControl = new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
			new PolynomialFunctionSetParams (4),
			SegmentInelasticDesignControl.Create (2, 2),
			new ResponseScalingShapeControl (true, new QuadraticRationalShapeControl (0.)),
			null
		);

		IBORCurve.ForwardJack (
			valueDate,
			"---- VANILLA QUARTIC POLYNOMIAL FORWARD CURVE SENSITIVITY ---",
			IBORCurve.CustomIBORBuilderSample (
				OvernightIndexCurve.MakeDC (valueDate, currency),
				IBOR6MCubicPolyVanilla.Make6MForward (valueDate, currency, "6M", true),
				ForwardLabel.Create (currency, tenor),
				quarticSegmentCustomBuilderControl,
				depositTenorArray,
				depositQuoteArray,
				"ForwardRate",
				fraTenor,
				fraQuoteArray,
				"ParForwardRate",
				null,
				null,
				"SwapRate",
				floatFloatTenorArray,
				floatFloatQuoteArray,
				"ReferenceParBasisSpread",
				syntheticFloatFloatTenorArray,
				syntheticFloatFloatQuoteArray,
				"ReferenceParBasisSpread",
				"---- VANILLA QUARTIC POLYNOMIAL FORWARD CURVE ---",
				true
			),
			"DerivedParBasisSpread"
		);

		EnvManager.TerminateEnv();
	}
}
