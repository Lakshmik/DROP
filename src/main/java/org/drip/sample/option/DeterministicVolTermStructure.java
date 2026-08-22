
package org.drip.sample.option;

import org.drip.analytics.date.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.state.creator.ScenarioDeterministicVolatilityBuilder;
import org.drip.state.volatility.VolatilityCurve;

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
 * <i>DeterministicVolatilityTermStructure</i> contains an illustration of the Calibration and Extraction of
 * 	the Implied and the Deterministic Volatility Term Structures. This does not deal with Local Volatility
 * 	Surfaces.
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/option/README.md">Deterministic (Black) / Stochastic (Heston) Options</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class DeterministicVolTermStructure
{

	private static final void OnGrid (
		final VolatilityCurve volatilityCurve,
		final String[] maturityTenorArray,
		final double[] nodeInputArray)
		throws Exception
	{
		System.out.println ("\n\t|------------------------------------|");

		System.out.println ("\t| TNR =>   CALC  |   IMPL  |  INPUT  |");

		System.out.println ("\t|------------------------------------|");

		for (int maturityTenorIndex = 0;
			maturityTenorIndex < maturityTenorArray.length;
			++maturityTenorIndex)
		{
			System.out.println (
				"\t| " + maturityTenorArray[maturityTenorIndex] + " =>" + FormatUtil.FormatDouble (
					volatilityCurve.node (maturityTenorArray[maturityTenorIndex]),
					2,
					2,
					100.
				) + "% | " + FormatUtil.FormatDouble (
					volatilityCurve.impliedVol (maturityTenorArray[maturityTenorIndex]),
					2,
					2,
					100.
				) + "% | " + FormatUtil.FormatDouble (
					nodeInputArray[maturityTenorIndex],
					2,
					2,
					100.
				) + "% |"
			);
		}

		System.out.println ("\t|------------------------------------|");
	}

	private static final void OffGrid (
		final String header,
		final String[] labelArray,
		final VolatilityCurve[] volatilityCurveArray,
		final String[] maturityTenorArray)
		throws Exception
	{
		System.out.println ("\n\n\t\t" + header + "\n");

		System.out.print ("\t| TNR =>");

		for (int volatilityCurveIndex = 0;
			volatilityCurveIndex < volatilityCurveArray.length;
			++volatilityCurveIndex)
		{
			System.out.print (" " + labelArray[volatilityCurveIndex] + " | ");
		}

		System.out.println ("\n");

		for (int maturityTenorIndex = 0;
			maturityTenorIndex < maturityTenorArray.length;
			++maturityTenorIndex)
		{
			System.out.print ("\t| " + maturityTenorArray[maturityTenorIndex] + " =>");

			for (int volatilityCurveIndex = 0;
				volatilityCurveIndex < volatilityCurveArray.length;
				++volatilityCurveIndex)
			{
				System.out.print (
					"  " + FormatUtil.FormatDouble (
						volatilityCurveArray[volatilityCurveIndex].node (
							maturityTenorArray[maturityTenorIndex]
						),
						2,
						2,
						100.
					) + "%   | "
				);
			}

			System.out.print ("\n");
		}

		System.out.println ("\n");
	}

	/**
	 * Entry Point
	 * 
	 * @param astrArgs Command Line Argument Array
	 * 
	 * @throws Exception Thrown on Error/Exception Situation
	 */

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		JulianDate today = DateUtil.Today();

		String[] maturityTenorArray =
		{
			"01Y",
			"02Y",
			"03Y",
			"04Y",
			"05Y",
			"06Y",
			"07Y",
			"08Y",
			"09Y"
		};
		double[] impliedCallVolatilityArray =
		{
			0.700,
			0.672,
			0.661,
			0.596,
			0.551,
			0.518,
			0.492,
			0.471,
			0.452
		};

		String[] offGridTenorArray =
		{
			"18M",
			"30M",
			"42M",
			"54M",
			"06Y",
			"09Y"
		};

		VolatilityCurve callVolatilityKLKHyperbolicTermStructure =
			ScenarioDeterministicVolatilityBuilder.KLKHyperbolicTermStructure (
				"KLK_HYPERBOLIC_CALLVOL_TERMSTRUCTURE",
				today,
				"USD",
				maturityTenorArray,
				impliedCallVolatilityArray,
				1.
			);

		OnGrid (callVolatilityKLKHyperbolicTermStructure, maturityTenorArray, impliedCallVolatilityArray);

		OffGrid (
			"ATM_CALLVOL_TERM_STRUCTURE",
			new String[]
			{
				"Cubic Poly",
				"Quart Poly",
				"KaklisPand",
				"KLKHyperbl",
				"KLKRatlLin",
				"KLKRatlQua"
			},
			new VolatilityCurve[]
			{
				ScenarioDeterministicVolatilityBuilder.CubicPolynomialTermStructure (
					"CUBIC_POLY_CALLVOL_TERMSTRUCTURE",
					today,
					"USD",
					maturityTenorArray,
					impliedCallVolatilityArray
				),
				ScenarioDeterministicVolatilityBuilder.QuarticPolynomialTermStructure (
					"QUARTIC_POLY_CALLVOL_TERMSTRUCTURE",
					today,
					"USD",
					maturityTenorArray,
					impliedCallVolatilityArray
				),
				ScenarioDeterministicVolatilityBuilder.KaklisPandelisTermStructure (
					"KAKLIS_PANDELIS_CALLVOL_TERMSTRUCTURE",
					today,
					"USD",
					maturityTenorArray,
					impliedCallVolatilityArray
				),
				callVolatilityKLKHyperbolicTermStructure,
				ScenarioDeterministicVolatilityBuilder.KLKRationalLinearTermStructure (
					"KLK_RAtodayTIONAL_LINEAR_CALLVOL_TERMSTRUCTURE",
					today,
					"USD",
					maturityTenorArray,
					impliedCallVolatilityArray,
					1.
				),
				ScenarioDeterministicVolatilityBuilder.KLKRationalQuadraticTermStructure (
					"KLK_RATIONAL_QUADRATIC_CALLVOL_TERMSTRUCTURE",
					today,
					"USD",
					maturityTenorArray,
					impliedCallVolatilityArray,
					0.0001
				)
			},
			offGridTenorArray
		);

		EnvManager.TerminateEnv();
	}
}
