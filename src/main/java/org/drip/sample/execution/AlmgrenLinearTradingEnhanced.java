
package org.drip.sample.execution;

import org.drip.execution.capture.TrajectoryShortfallEstimator;
import org.drip.execution.dynamics.*;
import org.drip.execution.impact.ParticipationRateLinear;
import org.drip.execution.nonadaptive.*;
import org.drip.execution.optimum.*;
import org.drip.execution.profiletime.UniformParticipationRateLinear;
import org.drip.execution.risk.MeanVarianceObjectiveUtility;
import org.drip.execution.strategy.*;
import org.drip.measure.gaussian.R1UnivariateNormal;
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
 * <i>AlmgrenLinearTradingEnhanced</i> demonstrates the Generation of the Optimal Trading Trajectory under
 * 	the Condition of Linear Trading Enhanced Volatility using a Numerical Optimization Technique. The
 * 	References are:
 * 
 * <br><br>
 *  <ul>
 *  	<li>
 * 			Almgren, R., and N. Chriss (1999): Value under Liquidation <i>Risk</i> <b>12 (12)</b>
 *  	</li>
 *  	<li>
 * 			Almgren, R., and N. Chriss (2000): Optimal Execution of Portfolio Transactions <i>Journal of
 * 				Risk</i> <b>3 (2)</b> 5-39
 *  	</li>
 *  	<li>
 * 			Almgren, R. (2003): Optimal Execution with Nonlinear Impact Functions and Trading-Enhanced Risk
 * 				<i>Applied Mathematical Finance</i> <b>10 (1)</b> 1-18
 *  	</li>
 *  	<li>
 * 			Almgren, R., and N. Chriss (2003): Bidding Principles <i>Risk</i> 97-102
 *  	</li>
 *  	<li>
 * 			Bertsimas, D., and A. W. Lo (1998): Optimal Control of Execution Costs <i>Journal of Financial
 * 				Markets</i> <b>1</b> 1-50
 *  	</li>
 *  </ul>
 * 
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/TransactionCostAnalyticsLibrary.md">Transaction Cost Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/execution/README.md">Nonlinear Trading Enhanced Market Impact</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class AlmgrenLinearTradingEnhanced
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

		double t = 5.;
		double sigma = 1.;
		double x = 100000.;
		double eta = 5.e-06;
		double beta = 2.e-06;
		double lambda = 1.e-05;
		int intervalCount = 20;

		ArithmeticPriceEvolutionParameters arithmeticPriceEvolutionParameters =
			ArithmeticPriceEvolutionParametersBuilder.TradingEnhancedVolatility (
				sigma,
				new UniformParticipationRateLinear (ParticipationRateLinear.SlopeOnly (eta)),
				new UniformParticipationRateLinear (new ParticipationRateLinear (0., beta))
			);

		EfficientTradingTrajectoryDiscrete efficientTradingTrajectoryDiscrete =
			(EfficientTradingTrajectoryDiscrete) new StaticOptimalSchemeDiscrete (
				DiscreteTradingTrajectoryControl.FixedInterval (
					new OrderSpecification (x, t),
					intervalCount
				),
				arithmeticPriceEvolutionParameters,
				new MeanVarianceObjectiveUtility (lambda)
			).generate();

		double[] executionTimeNodeArray = efficientTradingTrajectoryDiscrete.executionTimeNode();

		double[] holdingsArray = efficientTradingTrajectoryDiscrete.holdings();

		double[] tradeArray = efficientTradingTrajectoryDiscrete.tradeList();

		TradingEnhancedDiscrete tradingEnhancedDiscrete =
			(TradingEnhancedDiscrete) DiscreteLinearTradingEnhanced.Standard (
				x,
				t,
				intervalCount,
				arithmeticPriceEvolutionParameters,
				lambda
			).generate();

		double[] tradingEnhancedDiscreteTradeArray = tradingEnhancedDiscrete.tradeList();

		double[] tradingEnhancedDiscreteHoldingsArray = tradingEnhancedDiscrete.holdings();

		R1UnivariateNormal costDistribution = new TrajectoryShortfallEstimator (
			efficientTradingTrajectoryDiscrete
		).totalCostDistributionSynopsis (
			arithmeticPriceEvolutionParameters
		);

		System.out.println ("\n\t|------------------------------------------------||");

		System.out.println ("\t| NUMERICAL - CLOSED FORM CONTINUOUS TRAJECTORY  ||");

		System.out.println ("\t|------------------------------------------------||");

		System.out.println ("\t|    L -> R:                                     ||");

		System.out.println ("\t|          - Execution Time Node                 ||");

		System.out.println ("\t|          - Holdings (Numerical)                ||");

		System.out.println ("\t|          - Holdings (Continuous Closed Form)   ||");

		System.out.println ("\t|          - Trade List (Numerical)              ||");

		System.out.println ("\t|          - Trade List (Continuous Closed Form) ||");

		System.out.println ("\t|------------------------------------------------||");

		for (int executionTimeIndex = 1;
			executionTimeIndex < executionTimeNodeArray.length;
			++executionTimeIndex)
		{
			System.out.println (
				"\t| " + FormatUtil.FormatDouble (
					executionTimeNodeArray[executionTimeIndex],
					1,
					2,
					1.
				) + " => " + FormatUtil.FormatDouble (
					holdingsArray[executionTimeIndex] / x,
					2,
					2,
					100.
				) + "% | " + FormatUtil.FormatDouble (
					tradingEnhancedDiscreteHoldingsArray[executionTimeIndex] / x,
					2,
					2,
					100.
				) + "% | " + FormatUtil.FormatDouble (
					tradeArray[executionTimeIndex - 1] / x,
					2,
					2,
					100.
				) + "% | " + FormatUtil.FormatDouble (
					tradingEnhancedDiscreteTradeArray[executionTimeIndex - 1] / x,
					2,
					2,
					100.
				) + "% ||"
			);
		}

		System.out.println ("\t|------------------------------------------------||");

		System.out.println (
			"\n\t|--------------------------------------------------------------------------||"
		);

		System.out.println (
			"\t|  TRANSACTION COST RECONCILIATION: EXPLICIT vs. NUMERICAL vs. CLOSED FORM ||"
		);

		System.out.println (
			"\t|--------------------------------------------------------------------------||"
		);

		System.out.println (
			"\t| Transaction Cost Expectation         : " + FormatUtil.FormatDouble (
				costDistribution.mean(),
				6,
				1,
				1.
			) + " | " + FormatUtil.FormatDouble (
				efficientTradingTrajectoryDiscrete.transactionCostExpectation(),
				6,
				1,
				1.
			) + " | " + FormatUtil.FormatDouble (
				tradingEnhancedDiscrete.transactionCostExpectation(),
				6,
				1,
				1.
			) + " ||"
		);

		System.out.println (
			"\t| Transaction Cost Variance (X 10^-06) : " + FormatUtil.FormatDouble (
				costDistribution.variance(),
				6,
				1,
				1.e-06
			) + " | " + FormatUtil.FormatDouble (
				efficientTradingTrajectoryDiscrete.transactionCostVariance(),
				6,
				1,
				1.e-06
			) + " | " + FormatUtil.FormatDouble (
				tradingEnhancedDiscrete.transactionCostVariance(),
				6,
				1,
				1.e-06
			) + " ||"
		);

		System.out.println (
			"\t|--------------------------------------------------------------------------||"
		);

		EnvManager.TerminateEnv();
	}
}
