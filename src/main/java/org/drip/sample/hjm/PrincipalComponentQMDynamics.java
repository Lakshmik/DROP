
package org.drip.sample.hjm;

import org.drip.analytics.date.*;
import org.drip.analytics.definition.MarketSurface;
import org.drip.analytics.support.Helper;
import org.drip.dynamics.hjm.*;
import org.drip.function.definition.R1ToR1;
import org.drip.function.r1tor1operator.Flat;
import org.drip.sequence.random.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.PolynomialFunctionSetParams;
import org.drip.spline.params.*;
import org.drip.spline.stretch.MultiSegmentSequenceBuilder;
import org.drip.state.creator.ScenarioMarketSurfaceBuilder;
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
 * <i>PrincipalComponentQMDynamics</i> demonstrates the Construction and Usage of the Principal Component
 * 	Based Gaussian Model Dynamics for the Evolution of the Discount Factor Quantification Metrics - the
 * 	Instantaneous Forward Rate, the LIBOR Forward Rate, the Shifted LIBOR Forward Rate, the Short Rate, the
 * 	Compounded Short Rate, and the Price.
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/hjm/README.md">HJM Multi-Factor Principal Dynamics</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class PrincipalComponentQMDynamics
{

	private static final MarketSurface FlatVolatilitySurface (
		final JulianDate startDate,
		final String currency,
		final double flatVolatility)
		throws Exception
	{
		JulianDate startDatePlus2Y = startDate.addYears (2);

		JulianDate startDatePlus4Y = startDate.addYears (4);

		JulianDate startDatePlus6Y = startDate.addYears (6);

		JulianDate startDatePlus8Y = startDate.addYears (8);

		JulianDate startDatePlus10Y = startDate.addYears (10);

		return ScenarioMarketSurfaceBuilder.CustomSplineWireSurface (
			"VIEW_TARGET_VOLATILITY_SURFACE",
			startDate,
			currency,
			new double[] {
				startDate.julian(),
				startDatePlus2Y.julian(),
				startDatePlus4Y.julian(),
				startDatePlus6Y.julian(),
				startDatePlus8Y.julian(),
				startDatePlus10Y.julian()
			},
			new double[] {
				startDate.julian(),
				startDatePlus2Y.julian(),
				startDatePlus4Y.julian(),
				startDatePlus6Y.julian(),
				startDatePlus8Y.julian(),
				startDatePlus10Y.julian()
			},
			new double[][] {
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

	private static final MultiFactorStateEvolver HJMInstance (
		final JulianDate startDate,
		final String currency,
		final String tenor,
		final MarketSurface flatVolatilityMarketSurface1,
		final MarketSurface flatVolatilityMarketSurface2,
		final MarketSurface flatVolatilityMarketSurface3,
		final R1ToR1 forwardRateFunction,
		final int factorCount)
		throws Exception
	{
		return new MultiFactorStateEvolver (
			FundingLabel.Standard (currency),
			ForwardLabel.Create (currency, tenor),
			new MultiFactorVolatility (
				new MarketSurface[] {
					flatVolatilityMarketSurface1,
					flatVolatilityMarketSurface2,
					flatVolatilityMarketSurface3
				},
				new PrincipalFactorSequenceGenerator (
					new UnivariateSequenceGenerator[] {
						new BoxMullerGaussian (0., 1.),
						new BoxMullerGaussian (0., 1.),
						new BoxMullerGaussian (0., 1.)
					},
					new double[][] {
						{1.0, 0.1, 0.2},
						{0.1, 1.0, 0.2},
						{0.2, 0.1, 1.0}
					},
					factorCount
				)
			),
			forwardRateFunction
		);
	}

	private static final ShortForwardRateUpdate InitQMSnap (
		final JulianDate startDate,
		final String currency,
		final String viewTenor,
		final String tenor,
		final double initialForwardRate,
		final double initialPrice)
		throws Exception
	{
		return ShortForwardRateUpdate.Create (
			FundingLabel.Standard (currency),
			ForwardLabel.Create (currency, tenor),
			startDate.julian(),
			startDate.julian(),
			startDate.addTenor (viewTenor).julian(),
			initialForwardRate,
			0.,
			initialForwardRate,
			0.,
			initialForwardRate + (365.25 / Helper.TenorToDays (tenor)),
			0.,
			initialForwardRate,
			0.,
			initialForwardRate,
			0.,
			initialPrice,
			0.
		);
	}

	private static final void QMEvolution (
		final MultiFactorStateEvolver hjmMultiFactorStateEvolver,
		final JulianDate startDate,
		final String currency,
		final String viewTenor,
		final ShortForwardRateUpdate initialShortForwardRateUpdate)
		throws Exception
	{
		int viewDate = startDate.addTenor (viewTenor).julian();

		int dayStep = 2;
		JulianDate spotDate = startDate;
		ShortForwardRateUpdate shortForwardRateUpdate = initialShortForwardRateUpdate;

		System.out.println (
			"\t|-------------------------------------------------------------------------------------------------------------------------------||"
		);

		System.out.println (
			"\t|                                                                                                                               ||"
		);

		System.out.println (
			"\t|    Multi-Factor PCA-Based Gaussian HJM Quantification Metric Run                                                              ||"
		);

		System.out.println (
			"\t|    -------------------------------------------------------------                                                              ||"
		);

		System.out.println (
			"\t|                                                                                                                               ||"
		);

		System.out.println (
			"\t|        Number of Prinicipal Components: " +
				hjmMultiFactorStateEvolver.multiFactorVolatility().principalFactorSequenceGenerator().numFactor()
				+ "                                                                                     ||"
		);

		System.out.println (
			"\t|                                                                                                                               ||"
		);

		System.out.println (
			"\t|        L->R:                                                                                                                  ||"
		);

		System.out.println (
			"\t|            Date                                                                                                               ||"
		);

		System.out.println (
			"\t|            Instantaneous Forward Rate (%)                                                                                     ||"
		);

		System.out.println (
			"\t|            Instantaneous Forward Rate - Change (%)                                                                            ||"
		);

		System.out.println (
			"\t|            LIBOR Forward Rate (%)                                                                                             ||"
		);

		System.out.println (
			"\t|            LIBOR Forward Rate - Change (%)                                                                                    ||"
		);

		System.out.println (
			"\t|            Shifted LIBOR Forward Rate (%)                                                                                     ||"
		);

		System.out.println (
			"\t|            Shifted LIBOR Forward Rate - Change (%)                                                                            ||"
		);

		System.out.println (
			"\t|            Short Rate (%)                                                                                                     ||"
			);

		System.out.println (
			"\t|            Short Rate - Change (%)                                                                                            ||"
		);

		System.out.println (
			"\t|            Continuously Compounded Short Rate (%)                                                                             ||"
		);

		System.out.println (
			"\t|            Continuously Compounded Short Rate - Change (%)                                                                    ||"
		);

		System.out.println (
			"\t|            Price                                                                                                              ||"
		);

		System.out.println (
			"\t|            Price - Change                                                                                                     ||"
		);

		System.out.println (
			"\t|-------------------------------------------------------------------------------------------------------------------------------||"
		);

		while (spotDate.julian() < viewDate) {
			int spotDateJulian = spotDate.julian();

			shortForwardRateUpdate = (ShortForwardRateUpdate) hjmMultiFactorStateEvolver.evolve (
				spotDateJulian,
				viewDate,
				dayStep,
				shortForwardRateUpdate
			);

			System.out.println (
				"\t| [" + spotDate + "] = " + FormatUtil.FormatDouble (
					shortForwardRateUpdate.instantaneousForwardRate(),
					1,
					2,
					100.
				) + "% | " + FormatUtil.FormatDouble (
					shortForwardRateUpdate.instantaneousForwardRateIncrement(),
					1,
					2,
					100.
				) + "% || " + FormatUtil.FormatDouble (
					shortForwardRateUpdate.liborForwardRate(),
					1,
					2,
					100.
				) + "% | " + FormatUtil.FormatDouble (
					shortForwardRateUpdate.liborForwardRateIncrement(),
					1,
					2,
					100.
				) + "% || " + FormatUtil.FormatDouble (
					shortForwardRateUpdate.shiftedLIBORForwardRate(),
					1,
					4,
					1.
				) + " | " + FormatUtil.FormatDouble (
					shortForwardRateUpdate.shiftedLIBORForwardRateIncrement(),
					1,
					2,
					100.
				) + "% || " + FormatUtil.FormatDouble (
					shortForwardRateUpdate.shortRate(),
					1,
					2,
					100.
				) + "% | " + FormatUtil.FormatDouble (
					shortForwardRateUpdate.shortRateIncrement(),
					1,
					2,
					100.
				) + "% || " + FormatUtil.FormatDouble (
					shortForwardRateUpdate.compoundedShortRate(),
					1,
					2,
					100.
				) + "% | " + FormatUtil.FormatDouble (
					shortForwardRateUpdate.compoundedShortRateIncrement(),
					1,
					2,
					100.
				) + "% || " + FormatUtil.FormatDouble (
					shortForwardRateUpdate.price(),
					2,
					2,
					100.
				) + " | " + FormatUtil.FormatDouble (
					shortForwardRateUpdate.priceIncrement(),
					1,
					2,
					100.
				) + " || "
			);

			spotDate = spotDate.addBusDays (dayStep, currency);
		}

		System.out.println (
			"\t|-------------------------------------------------------------------------------------------------------------------------------||"
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

		String tenor = "3M";
		String currency = "USD";
		String viewTenor = "3M";
		double initialPrice = 0.9875;
		double flatForwardRate = 0.05;
		double flatVolatility1 = 0.007;
		double flatVolatility2 = 0.009;
		double flatVolatility3 = 0.004;

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

		ShortForwardRateUpdate initialShortForwardRateUpdate = InitQMSnap (
			spotDate,
			currency,
			viewTenor,
			tenor,
			flatForwardRate,
			initialPrice
		);

		int[] factorCountArray = new int[] {
			1,
			2,
			3
		};

		for (int factorCount : factorCountArray) {
			QMEvolution (
				HJMInstance (
					spotDate,
					currency,
					tenor,
					flatVolatilityMarketSurface1,
					flatVolatilityMarketSurface2,
					flatVolatilityMarketSurface3,
					new Flat (flatForwardRate),
					factorCount
				),
				spotDate,
				currency,
				viewTenor,
				initialShortForwardRateUpdate
			);
		}

		EnvManager.TerminateEnv();
	}
}
