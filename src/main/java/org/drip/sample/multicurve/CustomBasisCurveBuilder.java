
package org.drip.sample.multicurve;

import org.drip.analytics.date.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.state.basis.BasisCurve;
import org.drip.state.creator.ScenarioBasisCurveBuilder;
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
 * <i>CustomBasisCurveBuilder</i> contains the sample demonstrating the full functionality behind creating
 * 	highly customized spline based Basis curves.
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/multicurve/README.md">Multi-Curve Construction and Valuation</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class CustomBasisCurveBuilder
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

		JulianDate today = DateUtil.Today();

		String[] tenorArray =
		{
			"1Y",
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
		double[] basisQuoteArray =
		{
			0.00186,    //  1Y
			0.00127,    //  2Y
			0.00097,    //  3Y
			0.00080,    //  4Y
			0.00067,    //  5Y
			0.00058,    //  6Y
			0.00051,    //  7Y
			0.00046,    //  8Y
			0.00042,    //  9Y
			0.00038,    // 10Y
			0.00035,    // 11Y
			0.00033,    // 12Y
			0.00028,    // 15Y
			0.00022,    // 20Y
			0.00020,    // 25Y
			0.00018     // 30Y
		};

		ForwardLabel forwardLabel3M = ForwardLabel.Create ("USD", "3M");

		ForwardLabel forwardLabel6M = ForwardLabel.Create ("USD", "6M");

		BasisCurve cubicPolynomialBasisCurve = ScenarioBasisCurveBuilder.CubicPolynomialBasisCurve (
			"USD3M6MBasis_CubicPolynomial",
			today,
			forwardLabel6M,
			forwardLabel3M,
			false,
			tenorArray,
			basisQuoteArray
		);

		BasisCurve quinticPolynomialBasisCurve = ScenarioBasisCurveBuilder.QuarticPolynomialBasisCurve (
			"USD3M6MBasis_QuinticPolynomial",
			today,
			forwardLabel6M,
			forwardLabel3M,
			false,
			tenorArray,
			basisQuoteArray
		);

		BasisCurve kaklisPandelisBasisCurve = ScenarioBasisCurveBuilder.KaklisPandelisBasisCurve (
			"USD3M6MBasis_KaklisPandelis",
			today,
			forwardLabel6M,
			forwardLabel3M,
			false,
			tenorArray,
			basisQuoteArray
		);

		BasisCurve klkHyperbolicBasisCurve = ScenarioBasisCurveBuilder.KLKHyperbolicBasisCurve (
			"USD3M6MBasis_KLKHyperbolic",
			today,
			forwardLabel6M,
			forwardLabel3M,
			false,
			tenorArray,
			basisQuoteArray,
			1.
		);

		BasisCurve klkRationalLinearBasisCurve = ScenarioBasisCurveBuilder.KLKRationalLinearBasisCurve (
			"USD3M6MBasis_KLKRationalLinear",
			today,
			forwardLabel6M,
			forwardLabel3M,
			false,
			tenorArray,
			basisQuoteArray,
			0.1
		);

		BasisCurve klkRationalQuadraticBasisCurve = ScenarioBasisCurveBuilder.KLKRationalLinearBasisCurve (
			"USD3M6MBasis_KLKRationalQuadratic",
			today,
			forwardLabel6M,
			forwardLabel3M,
			false,
			tenorArray,
			basisQuoteArray,
			2.
		);

		System.out.println ("\t||-------------------------------------------------------------|");

		System.out.println ("\t||Printing the Basis Node Values in Order (Left -> Right):");

		System.out.println ("\t||\tCalculated Cubic Polynomial Basis (%)");

		System.out.println ("\t||\tCalculated Quintic Polynomial Basis (%)");

		System.out.println ("\t||\tCalculated Kaklis Pandelis Basis (%)");

		System.out.println ("\t||\tCalculated KLK Hyperbolic Basis (%)");

		System.out.println ("\t||\tCalculated KLK Rational Linear Basis (%)");

		System.out.println ("\t||\tCalculated KLK Rational Quadratic Basis (%)");

		System.out.println ("\t||\tInput Quote (bp)");

		System.out.println ("\t||-------------------------------------------------------------|");

		System.out.println ("\t||-------------------------------------------------------------|");

		for (int basisQuoteIndex = 0; basisQuoteIndex < basisQuoteArray.length; ++basisQuoteIndex) {
			System.out.println (
				"\t|| " + tenorArray[basisQuoteIndex] + " => " + FormatUtil.FormatDouble (
					cubicPolynomialBasisCurve.basis (tenorArray[basisQuoteIndex]),
					1,
					2,
					10000.
				) + " | " + FormatUtil.FormatDouble (
					quinticPolynomialBasisCurve.basis (tenorArray[basisQuoteIndex]),
					1,
					2,
					10000.
				) + " | " + FormatUtil.FormatDouble (
					kaklisPandelisBasisCurve.basis (tenorArray[basisQuoteIndex]),
					1,
					2,
					10000.
				) + " | " + FormatUtil.FormatDouble (
					klkHyperbolicBasisCurve.basis (tenorArray[basisQuoteIndex]),
					1,
					2,
					10000.
				) + " | " + FormatUtil.FormatDouble (
					klkRationalLinearBasisCurve.basis (tenorArray[basisQuoteIndex]),
					1,
					2,
					10000.
				) + " | " + FormatUtil.FormatDouble (
					klkRationalQuadraticBasisCurve.basis (tenorArray[basisQuoteIndex]),
					1,
					2,
					10000.
				) + " | " + FormatUtil.FormatDouble (
					basisQuoteArray[basisQuoteIndex],
					1,
					2,
					10000.
				)
			);
		}

		System.out.println (
			"\n\t||------------------------------------------------------------------------------|"
		);

		System.out.println (
			"\t|| DATE    =>  CUBIC | QUINTIC  | KAKPAND | KLKHYPER | KLKRATLNR | KLKRATQUA    |"
		);

		System.out.println (
			"\t||------------------------------------------------------------------------------|"
		);

		for (int monthIndex = 3; monthIndex < 30; ++monthIndex) {
			JulianDate date = today.addTenor (monthIndex + "Y");

			System.out.println (
				"\t|| " + date + " => " + FormatUtil.FormatDouble (
					cubicPolynomialBasisCurve.basis (date),
					1,
					2,
					10000.
				) + "  |  " + FormatUtil.FormatDouble (
					quinticPolynomialBasisCurve.basis (date),
					1,
					2,
					10000.
				) + "   |  " + FormatUtil.FormatDouble (
					kaklisPandelisBasisCurve.basis (date),
					1,
					2,
					10000.
				) + "  |  " + FormatUtil.FormatDouble (
					klkHyperbolicBasisCurve.basis (date),
					1,
					2,
					10000.
				) + "   |  " + FormatUtil.FormatDouble (
					klkRationalLinearBasisCurve.basis (date),
					1,
					2,
					10000.
				) + "    |  " + FormatUtil.FormatDouble (
					klkRationalQuadraticBasisCurve.basis (date),
					1,
					2,
					10000.
				) + "    |  "
			);
		}

		System.out.println (
			"\t||-------------------------------------------------------------------------------|"
		);

		EnvManager.TerminateEnv();
	}
}
