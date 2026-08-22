
package org.drip.sample.option;

import org.drip.analytics.date.*;
import org.drip.analytics.definition.MarketSurface;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.*;
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
 * <i>CustomVolSurfaceBuilder</i> contains an Comparison of the Construction of the Volatility Surface using
 * 	different Splining Techniques.
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

public class CustomVolSurfaceBuilder
{

	private static final SegmentCustomBuilderControl CubicPolySCBC()
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

	private static final SegmentCustomBuilderControl QuarticPolySCBC()
		throws Exception
	{
		return new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
			new PolynomialFunctionSetParams (5),
			SegmentInelasticDesignControl.Create (2, 2),
			null,
			null
		);
	}

	private static final SegmentCustomBuilderControl KaklisPandelisSCBC()
		throws Exception
	{
		return new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_KAKLIS_PANDELIS,
			new KaklisPandelisSetParams (2),
			SegmentInelasticDesignControl.Create (2, 2),
			null,
			null
		);
	}

	private static final SegmentCustomBuilderControl KLKHyperbolicSCBC(
		final double tension)
		throws Exception
	{
		return new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_KLK_HYPERBOLIC_TENSION,
			new ExponentialTensionSetParams (tension),
			SegmentInelasticDesignControl.Create (2, 2),
			null,
			null
		);
	}

	private static final SegmentCustomBuilderControl KLKRationalLinearSCBC(
		final double tension)
		throws Exception
	{
		return new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_KLK_RATIONAL_LINEAR_TENSION,
			new ExponentialTensionSetParams (tension),
			SegmentInelasticDesignControl.Create (2, 2),
			null,
			null
		);
	}

	private static final SegmentCustomBuilderControl KLKRationalQuadraticSCBC(
		final double tension)
		throws Exception
	{
		return new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_KLK_RATIONAL_QUADRATIC_TENSION,
			new ExponentialTensionSetParams (tension),
			SegmentInelasticDesignControl.Create (2, 2),
			null,
			null
		);
	}

	private static final void EvaluateSplineSurface (
		final MarketSurface volSurface,
		final double[] strikeATMFactorArray,
		final String[] maturityTenorArray)
		throws Exception
	{
		System.out.println ("\t|------------------------------------------------------------|");

		System.out.print (
			"\t|------------------------------------------------------------|\n\t|  ATM/TTE  =>"
		);

		for (String maturityTenor : maturityTenorArray) {
			System.out.print ("    " + maturityTenor + "  ");
		}

		System.out.println ("  |\n\t|------------------------------------------------------------|");

		for (double strikeATMFactor : strikeATMFactorArray) {
			System.out.print ("\t|  " + FormatUtil.FormatDouble (strikeATMFactor, 1, 2, 1.) + "    =>");

			for (String maturityTenor : maturityTenorArray) {
				System.out.print (
					"  " + FormatUtil.FormatDouble (
						volSurface.node (strikeATMFactor, maturityTenor),
						2,
						2,
						100.
					) + "%"
				);
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

		double[] calibrationStrikeATMFactorArray =
		{
			0.8,
			0.9,
			1.0,
			1.1,
			1.2
		};
		String[] calibrationMaturityTenorArray =
		{
			"1Y",
			"2Y",
			"3Y",
			"4Y",
			"5Y"
		};
		double[][] impliedVolatilityGrid =
		{
			{0.44, 0.38, 0.33, 0.27, 0.25},
			{0.41, 0.34, 0.30, 0.22, 0.27},
			{0.36, 0.31, 0.28, 0.30, 0.37},
			{0.38, 0.31, 0.34, 0.40, 0.47},
			{0.43, 0.46, 0.48, 0.52, 0.57}
		};
		double[] calculationStrikeATMFactorArray =
		{
			0.70,
			0.85,
			1.00,
			1.15,
			1.30
		};
		String[] calculationMaturityTenorArray =
		{
			"06M",
			"21M",
			"36M",
			"51M",
			"66M"
		};

		EvaluateSplineSurface (
			ScenarioMarketSurfaceBuilder.CubicPolynomialWireSurface (
				"CUBIC_POLY_VOL_SURFACE",
				startDate,
				"USD",
				calibrationStrikeATMFactorArray,
				calibrationMaturityTenorArray,
				impliedVolatilityGrid
			),
			calculationStrikeATMFactorArray,
			calculationMaturityTenorArray
		);

		EvaluateSplineSurface (
			ScenarioMarketSurfaceBuilder.QuarticPolynomialWireSurface (
				"QUARTIC_POLY_VOL_SURFACE",
				startDate,
				"USD",
				calibrationStrikeATMFactorArray,
				calibrationMaturityTenorArray,
				impliedVolatilityGrid
			),
			calculationStrikeATMFactorArray,
			calculationMaturityTenorArray
		);

		EvaluateSplineSurface (
			ScenarioMarketSurfaceBuilder.KaklisPandelisWireSurface (
				"KAKLIS_PANDELIS_VOL_SURFACE",
				startDate,
				"USD",
				calibrationStrikeATMFactorArray,
				calibrationMaturityTenorArray,
				impliedVolatilityGrid
			),
			calculationStrikeATMFactorArray,
			calculationMaturityTenorArray
		);

		EvaluateSplineSurface (
			ScenarioMarketSurfaceBuilder.KLKHyperbolicWireSurface (
				"KLK_HYPERBOLIC_VOL_SURFACE",
				startDate,
				"USD",
				calibrationStrikeATMFactorArray,
				calibrationMaturityTenorArray,
				impliedVolatilityGrid,
				1.
			),
			calculationStrikeATMFactorArray,
			calculationMaturityTenorArray
		);

		EvaluateSplineSurface (
			ScenarioMarketSurfaceBuilder.KLKRationalLinearWireSurface (
				"KLK_RATIONAL_LINEAR_VOL_SURFACE",
				startDate,
				"USD",
				calibrationStrikeATMFactorArray,
				calibrationMaturityTenorArray,
				impliedVolatilityGrid,
				1.
			),
			calculationStrikeATMFactorArray,
			calculationMaturityTenorArray
		);

		EvaluateSplineSurface (
			ScenarioMarketSurfaceBuilder.KLKRationalQuadraticWireSurface (
				"KLK_RATIONAL_QUADRATIC_VOL_SURFACE",
				startDate,
				"USD",
				calibrationStrikeATMFactorArray,
				calibrationMaturityTenorArray,
				impliedVolatilityGrid,
				1.
			),
			calculationStrikeATMFactorArray,
			calculationMaturityTenorArray
		);

		EvaluateSplineSurface (
			ScenarioMarketSurfaceBuilder.CustomWireSurface (
				"CUBIC_WIRESPAN_QUARTIC_SURFACE_VOL_SURFACE",
				startDate,
				"USD",
				calibrationStrikeATMFactorArray,
				calibrationMaturityTenorArray,
				impliedVolatilityGrid,
				CubicPolySCBC(),
				QuarticPolySCBC()
			),
			calculationStrikeATMFactorArray,
			calculationMaturityTenorArray
		);

		EvaluateSplineSurface (
			ScenarioMarketSurfaceBuilder.CustomWireSurface (
				"KAKLISPANDELIS_WIRESPAN_KLKHYPERBOLIC_SURFACE_VOL_SURFACE",
				startDate,
				"USD",
				calibrationStrikeATMFactorArray,
				calibrationMaturityTenorArray,
				impliedVolatilityGrid,
				KaklisPandelisSCBC(),
				KLKHyperbolicSCBC (2.)
			),
			calculationStrikeATMFactorArray,
			calculationMaturityTenorArray
		);

		EvaluateSplineSurface (
			ScenarioMarketSurfaceBuilder.CustomWireSurface (
				"KLKRATIONALLINEAR_WIRESPAN_KLKRATIONALQUADRATIC_SURFACE_VOL_SURFACE",
				startDate,
				"USD",
				calibrationStrikeATMFactorArray,
				calibrationMaturityTenorArray,
				impliedVolatilityGrid,
				KLKRationalLinearSCBC (3.),
				KLKRationalQuadraticSCBC (1.)
			),
			calculationStrikeATMFactorArray,
			calculationMaturityTenorArray
		);

		EnvManager.TerminateEnv();
	}
}
