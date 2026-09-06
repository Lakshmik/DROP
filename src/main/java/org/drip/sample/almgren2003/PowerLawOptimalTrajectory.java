
package org.drip.sample.almgren2003;

import org.drip.execution.dynamics.*;
import org.drip.execution.impact.*;
import org.drip.execution.nonadaptive.ContinuousPowerImpact;
import org.drip.execution.optimum.PowerImpactContinuous;
import org.drip.execution.parameters.ArithmeticPriceDynamicsSettings;
import org.drip.execution.profiletime.*;
import org.drip.function.definition.R1ToR1;
import org.drip.function.r1tor1operator.Flat;
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
 * <i>PowerLawOptimalTrajectory</i> sketches out the Optimal Trajectories for 3 different values of k -
 * 	representing Concave, Linear, and Convex Power's respectively. The References are:
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

public class PowerLawOptimalTrajectory
{

	private static final void RiskAversionRun (
		final double lambda)
		throws Exception
	{
		double drift = 0.;
		double gamma = 0.;
		double hRef = 0.50;
		double x = 100000.;
		double vRef = 100000.;
		double volatility = 1.;
		int intervalCount = 10;
		double finishTime = 10.;
		double serialCorrelation = 0.;

		double[] kArray =
		{
			0.25,
			0.50,
			0.75,
			1.00,
			1.25,
			1.50,
			1.75,
			2.00,
			2.25,
			2.50,
			2.75,
			3.00
		};

		System.out.println (
			"\n\t|------------------------------------------------------------------------------------------------------------------------------------||"
		);

		System.out.println (
			"\t|\tPOWER LAW OPTIMAL TRAJECTORY; RISK TOLERANCE (thousands) => " +
				FormatUtil.FormatDouble (1. / lambda, 1, 0, 1.e-03)
		);

		System.out.println ("\t|");

		System.out.println ("\t|\t\tL -> R:");

		System.out.println ("\t|\t\t\tTime Node Trajectory Realization (Percent)");

		System.out.println ("\t|\t\t\tCharacteristic Time (Days)");

		System.out.println ("\t|\t\t\tMaximum Execution Time (Days)");

		System.out.println ("\t|\t\t\tTransaction Cost Expectation (Thousands)");

		System.out.println ("\t|\t\t\tTransaction Cost Variance (Thousands)");

		System.out.println (
			"\t|------------------------------------------------------------------------------------------------------------------------------------||"
		);

		ArithmeticPriceDynamicsSettings arithmeticPriceDynamicsSettings =
			new ArithmeticPriceDynamicsSettings (drift, new Flat (volatility), serialCorrelation);

		ParticipationRateLinear permanentParticipationRateLinear = new ParticipationRateLinear (0., gamma);

		double inverseX = 1. / x;
		double inverseIntervalCount = 1. / intervalCount;
		double[] executionTimeArray = new double[intervalCount];

		for (int intervalIndex = 1; intervalIndex <= intervalCount; ++intervalIndex) {
			executionTimeArray[intervalIndex - 1] = inverseIntervalCount * intervalIndex;
		}

		for (int i = 0; i < kArray.length; ++i) {
			PowerImpactContinuous powerImpactContinuous =
				(PowerImpactContinuous) ContinuousPowerImpact.Standard (
					x,
					finishTime,
					ArithmeticPriceEvolutionParametersBuilder.Almgren2003 (
						arithmeticPriceDynamicsSettings,
						new UniformParticipationRateLinear (permanentParticipationRateLinear),
						new UniformParticipationRate (
							new ParticipationRatePower (hRef / Math.pow (vRef, kArray[i]), kArray[i])
						)
					),
					lambda
				).generate();

			if (0 == i) {
				String executionTime = "\t|          |  ";

				for (int executionTimeIndex = 0;
					executionTimeIndex < executionTimeArray.length;
					++executionTimeIndex)
				{
					executionTime +=
						"   " + FormatUtil.FormatDouble (executionTimeArray[executionTimeIndex], 1, 2, 1.);
				}

				System.out.println (executionTime);

				System.out.println (
					"\t|------------------------------------------------------------------------------------------------------------------------------------||"
				);
			}

			R1ToR1 holdingsFunction = powerImpactContinuous.holdings();

			String holdingsDump = "\t| k =" + FormatUtil.FormatDouble (kArray[i], 1, 2, 1.) + " | ";

			for (int intervalIndex = 0; intervalIndex < intervalCount; ++intervalIndex) {
				holdingsDump += "  " + FormatUtil.FormatDouble (
					holdingsFunction.evaluate (executionTimeArray[intervalIndex]) * inverseX,
					2,
					2,
					100.
				);
			}

			double executionTimeUpperBound = powerImpactContinuous.executionTimeUpperBound();

			System.out.println (
				holdingsDump + " | " + FormatUtil.FormatDouble (
					powerImpactContinuous.characteristicTime(),
					2,
					1,
					1.
				) + " | " + FormatUtil.FormatDouble (
					Double.isNaN (executionTimeUpperBound) ? 0. : executionTimeUpperBound,
					2,
					1,
					1.
				) + " | " + FormatUtil.FormatDouble (
					powerImpactContinuous.transactionCostExpectation(),
					3,
					0,
					1.e-03
				) + " | " + FormatUtil.FormatDouble (
					Math.sqrt (powerImpactContinuous.transactionCostVariance()),
					3,
					0,
					1.e-03
				) + " ||"
			);
		}

		System.out.println ("\t|------------------------------------------------------------------------------------------------------------------------------------||");
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

		double[] lambdaArray =
		{
			1.e-04,
			5.e-06,
			5.e-07
		};

		for (double lambda : lambdaArray) {
			RiskAversionRun (lambda);
		}

		EnvManager.TerminateEnv();
	}
}
