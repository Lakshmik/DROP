
package org.drip.sample.lmm;

import org.drip.analytics.date.DateUtil;
import org.drip.analytics.date.JulianDate;
import org.drip.analytics.definition.MarketSurface;
import org.drip.dynamics.lmm.LognormalLIBORVolatility;
import org.drip.sequence.random.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.PolynomialFunctionSetParams;
import org.drip.spline.params.*;
import org.drip.spline.stretch.MultiSegmentSequenceBuilder;
import org.drip.state.creator.*;
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
 * <i>ContinuousForwardRateVolatility</i> demonstrates the Implying of the Volatility of the Continuously
 * 	Compounded Forward Rate from the Corresponding LIBOR Forward Rate Volatility. The References are:
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

public class ContinuousForwardRateVolatility
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

	private static final void DisplayVolArray (
		final String tenor,
		final double[] volatilityArray)
	{
		String dump = "\t | " + tenor + " => ";

		for (int volatilityIndex = 0; volatilityIndex < volatilityArray.length; ++volatilityIndex) {
			dump += FormatUtil.FormatDouble (volatilityArray[volatilityIndex], 1, 2, 100.) + "% |";
		}

		System.out.println (dump);
	}

	/**
	 * Entry Point
	 * 
	 * @param argumentArray Command Line Argument Array
	 * 
	 * @throws Exception Thrown on Error/Exception Situation
	 */

	public static void main (
		final String[] argumentArray)
		throws Exception
	{
		EnvManager.InitEnv ("");

		String tenor = "3M";
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
		String[] forwardTenorArray =
		{
			"1Y",
			"2Y",
			"3Y",
			"4Y",
			"5Y",
			"6Y",
			"7Y",
			"8Y"
		};
		double[][] factorCorrelationMatrix =
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

		ForwardLabel forwardLabel = ForwardLabel.Create (currency, tenor);

		JulianDate spotDate = DateUtil.Today();

		MarketSurface flatVolatilityMarketSurface1 = FlatVolatilitySurface (
			spotDate,
			currency,
			flatVolatility1
		);

		MarketSurface flatVolatilityMarketSurface2 = FlatVolatilitySurface (
			spotDate,
			currency,
			flatVolatility2
		);

		MarketSurface flatVolatilityMarketSurface3 = FlatVolatilitySurface (
			spotDate,
			currency,
			flatVolatility3
		);

		ForwardCurve forwardCurve = ScenarioForwardCurveBuilder.FlatForwardForwardCurve (
			spotDate,
			forwardLabel,
			flatForwardRate
		);

		UnivariateSequenceGenerator univariateSequenceGenerator = new BoxMullerGaussian (0., 1.);

		UnivariateSequenceGenerator[] univariateSequenceGeneratorArray =
		{
			univariateSequenceGenerator,
			univariateSequenceGenerator,
			univariateSequenceGenerator
		};
		MarketSurface[] marketSurfaceArray =
		{
			flatVolatilityMarketSurface1,
			flatVolatilityMarketSurface2,
			flatVolatilityMarketSurface3
		};

		for (int factorCount : factorCountArray) {
			LognormalLIBORVolatility lognormalLIBORVolatility = new LognormalLIBORVolatility (
				spotDate.julian(),
				forwardLabel,
				marketSurfaceArray,
				new PrincipalFactorSequenceGenerator (
					univariateSequenceGeneratorArray,
					factorCorrelationMatrix,
					factorCount
				)
			);

			System.out.println ("\n\t |------------------------------|");

			System.out.println ("\t |  CONTINUOUS FORWARD RATE VOL |");

			System.out.println ("\t |   Factor Count: " + factorCount + "            |");

			System.out.println ("\t |------------------------------|");

			for (String forwardTenor : forwardTenorArray) {
				DisplayVolArray (
					forwardTenor,
					lognormalLIBORVolatility.continuousForwardVolatility (
						spotDate.addTenor (forwardTenor).julian(),
						forwardCurve
					)
				);
			}

			System.out.println ("\t |------------------------------|");
		}

		EnvManager.TerminateEnv();
	}
}
