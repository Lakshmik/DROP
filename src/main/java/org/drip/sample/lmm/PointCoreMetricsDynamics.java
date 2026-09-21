
package org.drip.sample.lmm;

import org.drip.analytics.date.DateUtil;
import org.drip.analytics.date.JulianDate;
import org.drip.analytics.definition.MarketSurface;
import org.drip.dynamics.lmm.*;
import org.drip.sequence.random.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.PolynomialFunctionSetParams;
import org.drip.spline.params.*;
import org.drip.spline.stretch.MultiSegmentSequenceBuilder;
import org.drip.state.creator.*;
import org.drip.state.discount.*;
import org.drip.state.forward.ForwardCurve;
import org.drip.state.identifier.*;

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
 * <i>PointCoreMetricsDynamics</i> demonstrates the Construction and Usage of the Point LIBOR State Evolver,
 * 	and the eventual Evolution of the related Core bDiscount/Forward Latent State Quantification Metrics. The
 * 	References are:
 *  
 * <br><br>
 *  <ul>
 *  	<li>
 *  		Goldys, B., M. Musiela, and D. Sondermann (1994): Log-normality of Rates and Term Structure
 *  			Models, The University of New South Wales.
 *  	</li>
 *  	<li>
 *  		Musiela, M. (1994): Nominal Annual Rates and Log-normal Volatility Structure, The University of
 *  			New South Wales.
 *  	</li>
 *  	<li>
 * 			Brace, A., D. Gatarek, and M. Musiela (1997): The Market Model of Interest Rate Dynamics,
 * 				Mathematical Finance 7 (2), 127-155.
 *  	</li>
 *  </ul>
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/lmm/README.md">LMM Multi-Factor Monte Carlo</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class PointCoreMetricsDynamics
{

	private static final MarketSurface FlatVolatilitySurface (
		final JulianDate startDate,
		final String currency,
		final double flatVolatility)
		throws Exception
	{
		double[] nodeDateArray =
		{
			startDate.julian(),
			startDate.addYears (2).julian(),
			startDate.addYears (4).julian(),
			startDate.addYears (6).julian(),
			startDate.addYears (8).julian(),
			startDate.addYears (10).julian()
		};

		return ScenarioMarketSurfaceBuilder.CustomSplineWireSurface (
			"VIEW_TARGET_VOLATILITY_SURFACE",
			startDate,
			currency,
			nodeDateArray,
			nodeDateArray,
			new double[][]
			{
				{
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility
				},
				{
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility
				},
				{
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility
				},
				{
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility
				},
				{
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility
				},
				{
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility,
					flatVolatility
				},
			},
			new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
				new PolynomialFunctionSetParams (4),
				SegmentInelasticDesignControl.Create (2, 2),
				null,
				null
			),
			new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
				new PolynomialFunctionSetParams (4),
				SegmentInelasticDesignControl.Create (2, 2),
				null,
				null
			)
		);
	}

	private static final LognormalLIBORVolatility LLVInstance (
		final int spotDate,
		final ForwardLabel forwardLabel,
		final MarketSurface[] marketSurfaceArray,
		final double[][] correlationMatrix,
		final int factorCount)
		throws Exception
	{
		UnivariateSequenceGenerator[] univariateSequenceGeneratorArray =
			new UnivariateSequenceGenerator[marketSurfaceArray.length];

		for (int sequenceIndex = 0; sequenceIndex < univariateSequenceGeneratorArray.length; ++sequenceIndex)
		{
			univariateSequenceGeneratorArray[sequenceIndex] = new BoxMullerGaussian (0., 1.);
		}

		return new LognormalLIBORVolatility (
			spotDate,
			forwardLabel,
			marketSurfaceArray,
			new PrincipalFactorSequenceGenerator (
				univariateSequenceGeneratorArray,
				correlationMatrix,
				factorCount
			)
		);
	}

	private static final void DisplayRunSnap (
		final BGMPointUpdate bgmPointUpdate)
		throws Exception
	{
		System.out.println (
			"\t| [" + new JulianDate (bgmPointUpdate.evolutionStartDate()) + " -> " +
				new JulianDate (bgmPointUpdate.evolutionFinishDate()) + "]  => " + FormatUtil.FormatDouble (
					bgmPointUpdate.libor(),
					1,
					2,
					100.
				) + "% | " + FormatUtil.FormatDouble (
					bgmPointUpdate.liborIncrement(),
					2,
					0,
					10000.
				) + " | " + FormatUtil.FormatDouble (
					bgmPointUpdate.continuousForwardRate(),
					1,
					2,
					100.
				) + "% | " + FormatUtil.FormatDouble (
					bgmPointUpdate.continuousForwardRateIncrement(),
					2,
					0,
					10000.
				) + " | " + FormatUtil.FormatDouble (
					bgmPointUpdate.spotRate(),
					1,
					2,
					100.
				) + "% | " + FormatUtil.FormatDouble (
					bgmPointUpdate.spotRateIncrement(),
					2,
					0,
					10000.
				) + " | " + FormatUtil.FormatDouble (
					bgmPointUpdate.discountFactor(),
					1,
					2,
					100.
				) + " | " + FormatUtil.FormatDouble (
					bgmPointUpdate.discountFactorIncrement(),
					2,
					0,
					10000.
				) + " | " + FormatUtil.FormatDouble (
					bgmPointUpdate.lognormalLIBORVolatility(),
					2,
					0,
					100.
				) + "% | " + FormatUtil.FormatDouble (
					bgmPointUpdate.continuouslyCompoundedForwardVolatility(),
					1,
					2,
					100.
				) + "% | "
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

		int runCount = 20;
		String tenor = "3M";
		double zeroRate = 0.02;
		String currency = "USD";
		double flatVolatility1 = 0.35;
		double flatVolatility2 = 0.42;
		double flatVolatility3 = 0.27;
		double flatForwardRate = 0.02;
		int[] factorCountArray =
		{
			1,
			2,
			3
		};
		double[][] correlationMatrix =
		{
			{
				1.0,
				0.1,
				0.2
			},
			{
				0.1,
				1.0,
				0.2
			},
			{
				0.2,
				0.1,
				1.0
			}
		};

		JulianDate spotDate = DateUtil.Today();

		int spotDateJulian = spotDate.julian();

		FundingLabel fundingLabel = FundingLabel.Standard (currency);

		ForwardLabel forwardLabel = ForwardLabel.Create (currency, tenor);

		MarketSurface[] marketSurfaceArray =
		{
			FlatVolatilitySurface (spotDate, currency, flatVolatility1),
			FlatVolatilitySurface (spotDate, currency, flatVolatility2),
			FlatVolatilitySurface (spotDate, currency, flatVolatility3)
		};

		ForwardCurve forwardCurve = ScenarioForwardCurveBuilder.FlatForwardForwardCurve (
			spotDate,
			forwardLabel,
			flatForwardRate
		);

		MergedDiscountForwardCurve discountCurve =
			ScenarioDiscountCurveBuilder.ExponentiallyCompoundedFlatRate (spotDate, currency, zeroRate);

		int viewDateJulian = spotDate.addTenor ("1Y").julian();

		int viewTimeIncrement = 1;

		for (int factorCount : factorCountArray) {
			System.out.println (
				"\n\n\t|----------------------------------------------------------------------------------------------------------|"
			);

			System.out.println (
				"\t|                                                                                                          |"
			);

			System.out.println (
				"\t|                             LOG-NORMAL LIBOR EVOLVER                                                     |"
			);

			System.out.println (
				"\t|                             ---------- ----- -------                                                     |"
			);

			System.out.println (
				"\t|                                                                                                          |"
			);

			System.out.println (
				"\t|       Num Factors: " + factorCount + "                                                                                     |"
			);

			System.out.println (
				"\t|       Start Date                                                                                         |"
			);

			System.out.println (
				"\t|       End Date                                                                                           |"
			);

			System.out.println (
				"\t|       Adjacent Step LIBOR (%)                                                                            |"
			);

			System.out.println (
				"\t|       Adjacent Step LIBOR Increment (bp)                                                                 |"
			);

			System.out.println (
				"\t|       Adjacent Step Continuously Compounded Forward Rate (%)                                             |"
			);

			System.out.println (
				"\t|       Adjacent Step Continuously Compounded Forward Rate Increment (bp)                                  |"
			);

			System.out.println (
				"\t|       Adjacent Step Spot Rate (%)                                                                        |"
			);

			System.out.println (
				"\t|       Adjacent Step Spot Rate Increment (bp)                                                             |"
			);

			System.out.println (
				"\t|       Adjacent Step Discount Function                                                                    |"
			);

			System.out.println (
				"\t|       Adjacent Step Discount Function Increment (c)                                                      |"
			);

			System.out.println (
				"\t|       Log-normal LIBOR Rate Volatility (%)                                                               |"
			);

			System.out.println (
				"\t|       Continuously Compounded Forward Rate Volatility (%)                                                |"
			);

			System.out.println (
				"\t|                                                                                                          |"
			);

			System.out.println (
				"\t|----------------------------------------------------------------------------------------------------------|"
			);

			LognormalLIBORPointEvolver lognormalLIBORPointEvolver = new LognormalLIBORPointEvolver (
				fundingLabel,
				forwardLabel,
				LLVInstance (
					spotDateJulian,
					forwardLabel,
					marketSurfaceArray,
					correlationMatrix,
					factorCount
				),
				forwardCurve,
				discountCurve
			);

			for (int runIndex = 0; runIndex < runCount; ++runIndex) {
				DisplayRunSnap (
					lognormalLIBORPointEvolver.evolve (
						spotDateJulian,
						viewDateJulian,
						viewTimeIncrement,
						null
					)
				);
			}

			System.out.println (
				"\t|----------------------------------------------------------------------------------------------------------|"
			);
		}

		EnvManager.TerminateEnv();
	}
}
