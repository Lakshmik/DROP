
package org.drip.sample.option;

import org.drip.analytics.date.*;
import org.drip.analytics.definition.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.PolynomialFunctionSetParams;
import org.drip.spline.params.*;
import org.drip.spline.stretch.MultiSegmentSequenceBuilder;
import org.drip.state.creator.ScenarioMarketSurfaceBuilder;

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
 * <i>LocalVolatilityTermStructure</i> contains an illustration of the Calibration and Extraction of the
 * 	Implied and the Local Volatility Surfaces and their eventual Strike and Maturity Anchor Term Structures.
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

public class LocalVolatilityTermStructure
{

	private static final SegmentCustomBuilderControl QuadraticSegmentCustomBuilderControl()
		throws Exception
	{
		return new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
			new PolynomialFunctionSetParams (4),
			SegmentInelasticDesignControl.Create (2, 2),
			null,
			null
		);
	}

	private static final void EvaluateLocalVolSurface (
		final MarketSurface volSurface,
		final double[] strikeATMFactorArray,
		final String[] maturityTenorArray)
		throws Exception
	{
		System.out.println ("\n\t  " + volSurface.label());

		System.out.println ("\t|------------------------------------------------------------|");

		System.out.print (
			"\t|------------------------------------------------------------|\n\t|  ATM/TTE  =>"
		);

		NodeStructure[] maturityAnchorNodeStructureArray = new NodeStructure[maturityTenorArray.length];

		for (int maturityTenorIndex = 0;
			maturityTenorIndex < maturityTenorArray.length;
			++maturityTenorIndex)
		{
			maturityAnchorNodeStructureArray[maturityTenorIndex] =
				volSurface.maturityAnchorTermStructure (maturityTenorArray[maturityTenorIndex]);

			System.out.print ("    " + maturityTenorArray[maturityTenorIndex] + "  ");
		}

		System.out.println ("  |\n\t|------------------------------------------------------------|");

		for (int strikeIndex = 0; strikeIndex < strikeATMFactorArray.length; ++strikeIndex) {
			System.out.print (
				"\t|  " + FormatUtil.FormatDouble (strikeATMFactorArray[strikeIndex], 1, 2, 1.) + "    =>"
			);

			NodeStructure strikeAnchorNodeStructure =
				volSurface.xAnchorTermStructure (strikeATMFactorArray[strikeIndex]);

			for (int maturityTenorIndex = 0;
				maturityTenorIndex < maturityTenorArray.length;
				++maturityTenorIndex)
			{
				double localVol = Math.sqrt (
					2. * (
						strikeAnchorNodeStructure.nodeDerivative (maturityTenorArray[maturityTenorIndex], 1)
							+ 0. * strikeATMFactorArray[strikeIndex] *
							maturityAnchorNodeStructureArray[maturityTenorIndex].nodeDerivative (
								(int) strikeATMFactorArray[strikeIndex],
								1
							)
					) / (
						strikeATMFactorArray[strikeIndex] * strikeATMFactorArray[strikeIndex] *
							maturityAnchorNodeStructureArray[maturityTenorIndex].nodeDerivative (
								(int) strikeATMFactorArray[strikeIndex],
								2
							)
					)
				);

				System.out.print ("  " + FormatUtil.FormatDouble (localVol, 2, 2, 100.) + "%");
			}

			System.out.print ("  |\n");
		}

		System.out.println ("\t|------------------------------------------------------------|");
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

		JulianDate startDate = DateUtil.Today();

		double[] calibrationATMStrikeFactorArray =
		{
			0.8,
			0.9,
			1.0,
			1.1,
			1.2
		};
		String[] calibrationMaturityTenorArray =
		{
			"12M",
			"24M",
			"36M",
			"48M",
			"60M"
		};
		double[][] volatilityGrid =
		{
			{0.171, 0.169, 0.168, 0.168, 0.168},
			{0.159, 0.161, 0.161, 0.162, 0.164},
			{0.138, 0.145, 0.149, 0.152, 0.154},
			{0.115, 0.130, 0.137, 0.143, 0.148},
			{0.103, 0.119, 0.128, 0.135, 0.140}
		};
		double[] atmStrikeFactorArray =
		{
			0.850,
			0.925,
			1.000,
			1.075,
			1.150
		};
		String[] maturityTenorArray =
		{
			"18M",
			"27M",
			"36M",
			"45M",
			"54M"
		};

		MarketSurface cubicPolynomialPriceSurface = ScenarioMarketSurfaceBuilder.CustomWireSurface (
			"HESTON1993_CUBICPOLY_CALLPRICE_SURFACE",
			startDate,
			"USD",
			calibrationATMStrikeFactorArray,
			calibrationMaturityTenorArray,
			volatilityGrid,
			QuadraticSegmentCustomBuilderControl(),
			QuadraticSegmentCustomBuilderControl()
		);

		NodeStructure[] maturityAnchorNodeStructureArray = new NodeStructure[maturityTenorArray.length];

		for (int maturityTenorIndex = 0;
			maturityTenorIndex < maturityTenorArray.length;
			++maturityTenorIndex)
		{
			maturityAnchorNodeStructureArray[maturityTenorIndex] =
				cubicPolynomialPriceSurface.maturityAnchorTermStructure (
					maturityTenorArray[maturityTenorIndex]
				);
		}

		for (int i = 0; i < atmStrikeFactorArray.length; ++i) {
			NodeStructure tsStrikeAnchor = cubicPolynomialPriceSurface.xAnchorTermStructure (atmStrikeFactorArray[i]);

			for (int maturityTenorIndex = 0; maturityTenorIndex < maturityTenorArray.length; ++maturityTenorIndex) {
				System.out.println (
					Math.sqrt (
						2. * (
							tsStrikeAnchor.nodeDerivative (maturityTenorArray[maturityTenorIndex], 1) +
								0. * atmStrikeFactorArray[i] *
								maturityAnchorNodeStructureArray[maturityTenorIndex].nodeDerivative (
									(int) atmStrikeFactorArray[i],
									1
								)
						) / (
							atmStrikeFactorArray[i] * atmStrikeFactorArray[i] *
								maturityAnchorNodeStructureArray[maturityTenorIndex].nodeDerivative (
									(int) atmStrikeFactorArray[i],
									2
								)
						)
					) + " | " + maturityAnchorNodeStructureArray[maturityTenorIndex].nodeDerivative (
						(int) atmStrikeFactorArray[i],
						2
					)
				);
			}
		}

		EvaluateLocalVolSurface (cubicPolynomialPriceSurface, atmStrikeFactorArray, maturityTenorArray);

		EnvManager.TerminateEnv();
	}
}
