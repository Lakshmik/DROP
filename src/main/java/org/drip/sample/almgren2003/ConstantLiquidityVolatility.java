
package org.drip.sample.almgren2003;

import org.drip.execution.dynamics.*;
import org.drip.execution.impact.ParticipationRateLinear;
import org.drip.execution.nonadaptive.ContinuousConstantTradingEnhanced;
import org.drip.execution.optimum.EfficientTradingTrajectoryContinuous;
import org.drip.execution.profiletime.UniformParticipationRateLinear;
import org.drip.function.definition.R1ToR1;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;

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
 * <i>ConstantLiquidityVolatility</i> demonstrates the Dependence of the Optimal Trading Trajectory as a
 * 	Function of Constant Trading Enhanced Volatilities. The References are:
 * 
 * <br><br>
 *  <ul>
 *  	<li>
 * 			Almgren, R., and N. Chriss (1999): Value under Liquidation <i>Risk</i> <b>12 (12)</b>
 *  	</li>
 * 
 *  	<li>
 * 			Almgren, R., and N. Chriss (2000): Optimal Execution of Portfolio Transactions <i>Journal of
 * 				Risk</i> <b>3 (2)</b> 5-39
 *  	</li>
 * 
 *  	<li>
 * 			Almgren, R. (2003): Optimal Execution with Nonlinear Impact Functions and Trading-Enhanced Risk
 * 				<i>Applied Mathematical Finance</i> <b>10 (1)</b> 1-18.
 *  	</li>
 * 
 *  	<li>
 * 			Almgren, R., and N. Chriss (2003): Bidding Principles <i>Risk</i> 97-102
 *  	</li>
 * 
 *  	<li>
 * 			Bertsimas, D., and A. W. Lo (1998): Optimal Control of Execution Costs <i>Journal of Financial
 * 				Markets</i> <b>1</b> 1-50
 *  	</li>
 *  </ul>
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/TransactionCostAnalyticsLibrary.md">Transaction Cost Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/almgren2003/README.md">Almgren (2003) Power Law Liquidity</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class ConstantLiquidityVolatility
{

	private static final void AlphaRun (
		final double alpha,
		final double t,
		final int intervalCount)
		throws Exception
	{
		double sigma = 1.;
		double x = 100000.;
		double eta = 5.e-06;
		double lambda = 1.e-05;

		double inverseX = 1. / x;

		EfficientTradingTrajectoryContinuous efficientTradingTrajectoryContinuous =
			(EfficientTradingTrajectoryContinuous) ContinuousConstantTradingEnhanced.Standard (
				x,
				t,
				ArithmeticPriceEvolutionParametersBuilder.TradingEnhancedVolatility (
					sigma,
					new UniformParticipationRateLinear (ParticipationRateLinear.SlopeOnly (eta)),
					new UniformParticipationRateLinear (new ParticipationRateLinear (alpha, 0.))
				),
				lambda
			).generate();

		R1ToR1 holdingsFunction = efficientTradingTrajectoryContinuous.holdings();

		double[] executionTimeArray = new double[intervalCount];
		double[] holdingsArray = new double[intervalCount];

		for (int intervalIndex = 1; intervalIndex <= intervalCount; ++intervalIndex) {
			holdingsArray[intervalIndex - 1] = holdingsFunction.evaluate (
				executionTimeArray[intervalIndex - 1] = t * intervalIndex / intervalCount
			);
		}

		String dump = "\t|" + FormatUtil.FormatDouble (alpha, 1, 1, 1.) + " =>";

		for (int intervalIndex = 0; intervalIndex < executionTimeArray.length; ++intervalIndex) {
			dump += FormatUtil.FormatDouble (holdingsArray[intervalIndex] * inverseX, 2, 1, 100.) + "% |";
		}

		System.out.println (
			dump + FormatUtil.FormatDouble (
				efficientTradingTrajectoryContinuous.transactionCostExpectation(),
				5,
				0,
				1.
			) + " |" + FormatUtil.FormatDouble (
				efficientTradingTrajectoryContinuous.transactionCostVariance(),
				5,
				0,
				1.e-06
			) + " | " + FormatUtil.FormatDouble (
				efficientTradingTrajectoryContinuous.characteristicTime(),
				1,
				3,
				1.
			) + " ||"
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
		EnvManager.InitEnv ("", true);

		double t = 5.;
		int intervalCount = 10;
		double[] alphaArray =
		{
			0.0,
			0.1,
			0.2,
			0.3,
			0.4,
			0.5,
			0.6,
			0.7,
			0.8,
			0.9,
			1.0,
			1.1,
			1.2,
			1.3,
			1.4,
			1.5,
			1.6,
			1.7,
			1.8,
			1.9,
			2.0
		};

		System.out.println();

		System.out.println (
			"\t|---------------------------------------------------------------------------------------------------------------||"
		);

		System.out.println (
			"\t|                   ALMGREN (2003) CONSTANT TEMPORARY IMPACT VOLATILITY - OFFSET DEPENDENCE                     ||"
		);

		System.out.println (
			"\t|---------------------------------------------------------------------------------------------------------------||"
		);

		System.out.println (
			"\t|      L -> R:                                                                                                  ||"
		);

		System.out.println (
			"\t|              Alpha Level                                                                                      ||"
		);

		System.out.println (
			"\t|              Outstanding Trajectory (%)                                                                       ||"
		);

		System.out.println (
			"\t|              Transaction Cost Expectation                                                                     ||"
		);

		System.out.println (
			"\t|              Transaction Cost Variance (X 10^-06)                                                             ||"
		);

		System.out.println (
			"\t|              Characteristic Time (Days)                                                                       ||"
		);

		System.out.println (
			"\t|---------------------------------------------------------------------------------------------------------------||"
		);

		String timeNode = "\t|        ";

		for (int intervalIndex = 0; intervalIndex <= intervalCount; ++intervalIndex) {
			timeNode += FormatUtil.FormatDouble (t * intervalIndex / intervalCount, 1, 2, 1.) + "  |";
		}

		System.out.println (timeNode);

		System.out.println (
			"\t|---------------------------------------------------------------------------------------------------------------||"
		);

		for (double alpha : alphaArray) {
			AlphaRun (alpha, t, intervalCount);
		}

		System.out.println (
			"\t|---------------------------------------------------------------------------------------------------------------||"
		);

		EnvManager.TerminateEnv();
	}
}
