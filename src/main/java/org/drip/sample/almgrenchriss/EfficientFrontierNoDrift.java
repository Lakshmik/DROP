
package org.drip.sample.almgrenchriss;

import org.drip.execution.dynamics.*;
import org.drip.execution.impact.*;
import org.drip.execution.nonadaptive.DiscreteAlmgrenChriss;
import org.drip.execution.optimum.AlmgrenChrissDiscrete;
import org.drip.execution.parameters.ArithmeticPriceDynamicsSettings;
import org.drip.execution.profiletime.UniformParticipationRateLinear;
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
 * <i>EfficientFrontierNoDrift</i> constructs the Efficient Frontier over a Sequence of Risk Aversion
 * 	Parameters for Optimal Trading Trajectories computed in accordance with the Specification of Almgren and
 * 	Chriss (2000), and calculates the corresponding Execution Half Life and the Trajectory Penalty without
 * 	regard to the Drift. The References are:
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
 * 			Bertsimas, D., and A. W. Lo (1998): Optimal Control of Execution Costs <i>Journal of Financial
 * 				Markets</i> <b>1</b> 1-50
 *  	</li>
 *  	<li>
 * 			Chan, L. K. C., and J. Lakonishak (1995): The Behavior of Stock Prices around Institutional
 * 				Trades <i>Journal of Finance</i> <b>50</b> 1147-1174
 *  	</li>
 *  	<li>
 * 			Keim, D. B., and A. Madhavan (1997): Transaction Costs and Investment Style: An Inter-exchange
 * 				Analysis of Institutional Equity Trades <i>Journal of Financial Economics</i> <b>46</b>
 * 				265-292
 *  	</li>
 *  </ul>
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/TransactionCostAnalyticsLibrary.md">Transaction Cost Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/almgrenchriss/README.md">Almgren Chriss Efficient Frontier Trajectories</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class EfficientFrontierNoDrift
{

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
		EnvManager.InitEnv ("", true);

		int n = 5;
		double t = 5.;
		double s0 = 50.;
		double x = 1000000.;
		double alpha = 0.02;
		double sigma = 0.95;
		double eta = 2.5e-06;
		double gamma = 2.5e-07;
		double epsilon = 0.0625;
		double[] lambdaLongEndUArray =
		{
			0.250e-06,
			0.500e-06,
			0.750e-06,
			1.000e-06,
			1.250e-06,
			1.500e-06,
			1.750e-06,
			2.000e-06,
			2.250e-06,
			2.500e-06,
			2.750e-06,
			3.000e-06,
			3.250e-06,
			3.500e-06,
			3.750e-06,
			4.000e-06
		};
		double[] lambdaShortEndUArray =
		{
			0.001e-06,
			0.002e-06,
			0.003e-06,
			0.004e-06,
			0.005e-06,
			0.006e-06,
			0.007e-06,
			0.008e-06,
			0.009e-06
		};

		LinearPermanentExpectationParameters linearPermanentExpectationParameters =
			ArithmeticPriceEvolutionParametersBuilder.LinearExpectation (
				new ArithmeticPriceDynamicsSettings (alpha, new Flat (sigma), 0.),
				new UniformParticipationRateLinear (new ParticipationRateLinear (0., gamma)),
				new UniformParticipationRateLinear (new ParticipationRateLinear (epsilon, eta))
			);

		System.out.println ("\n\t|---------------------------------------------||");

		System.out.println ("\t| ALMGREN-CHRISS TRAJECTORY GENERATOR INPUTS  ||");

		System.out.println ("\t|---------------------------------------------||");

		System.out.println ("\t| Initial Stock Price           : " + s0);

		System.out.println ("\t| Initial Holdings              : " + x);

		System.out.println ("\t| Liquidation Time              : " + t);

		System.out.println ("\t| Number of Time Periods        : " + n);

		System.out.println ("\t| 30% Annual Volatility         : " + sigma);

		System.out.println ("\t| 10% Annual Growth             : " + alpha);

		System.out.println ("\t| Bid-Ask Spread = 1/8          : " + epsilon);

		System.out.println ("\t| Daily Volume 5 million Shares : " + gamma);

		System.out.println ("\t| Impact at 1% of Market        : " + eta);

		System.out.println ("\t|---------------------------------------------||");

		System.out.println ("\n\t|------------------------------------------------------------||");

		System.out.println ("\t|      SHORT END COST DISTRIBUTION, PENALTY, AND DECAY       ||");

		System.out.println ("\t|------------------------------------------------------------||");

		System.out.println ("\t|       LAMBDA      |   MEAN  | SIGMA^2 | PENALTY | HALFLIFE ||");

		System.out.println ("\t|------------------------------------------------------------||");

		for (double lambdaShortEndU : lambdaShortEndUArray) {
			AlmgrenChrissDiscrete almgrenChrissDiscrete =
				(AlmgrenChrissDiscrete) DiscreteAlmgrenChriss.Standard (
					x,
					t,
					n,
					linearPermanentExpectationParameters,
					lambdaShortEndU
				).generate();

			double transactionCostExpectation = almgrenChrissDiscrete.transactionCostExpectation();

			double transactionCostVariance = almgrenChrissDiscrete.transactionCostVariance();

			System.out.println (
				"\t| [LAMBDA = " + FormatUtil.FormatDouble (
						lambdaShortEndU,
						1,
						3,
						x
					) + "] | " + FormatUtil.FormatDouble (
						transactionCostExpectation / x,
						1,
						4,
						1.
					) + " | " + FormatUtil.FormatDouble (
						transactionCostVariance / x / x,
						1,
						4,
						1.
					) + " | " + FormatUtil.FormatDouble (
						transactionCostExpectation + lambdaShortEndU * transactionCostVariance / x,
						1,
						4,
						1.
					) + " | " + FormatUtil.FormatDouble (
						almgrenChrissDiscrete.halfLife(),
						2,
						2,
						1.
					) + "   ||"
			);
		}

		System.out.println ("\t|------------------------------------------------------------||");

		System.out.println ("\n\t|------------------------------------------------------------||");

		System.out.println ("\t|       LONG END COST DISTRIBUTION, PENALTY, AND DECAY       ||");

		System.out.println ("\t|------------------------------------------------------------||");

		System.out.println ("\t|       LAMBDA      |   MEAN  | SIGMA^2 | PENALTY | HALFLIFE ||");

		System.out.println ("\t|------------------------------------------------------------||");

		for (double lambdaLongEndU : lambdaLongEndUArray) {
			AlmgrenChrissDiscrete acd = (AlmgrenChrissDiscrete) DiscreteAlmgrenChriss.Standard (
				x,
				t,
				n,
				linearPermanentExpectationParameters,
				lambdaLongEndU
			).generate();

			double transactionCostExpectation = acd.transactionCostExpectation();

			double transactionCostVariance = acd.transactionCostVariance();

			System.out.println (
				"\t| [LAMBDA = " + FormatUtil.FormatDouble (
					lambdaLongEndU,
					1,
					3,
					x
				) + "] | " + FormatUtil.FormatDouble (
					transactionCostExpectation / x,
					1,
					4,
					1.
				) + " | " + FormatUtil.FormatDouble (
					transactionCostVariance / x / x,
					1,
					4,
					1.
				) + " | " + FormatUtil.FormatDouble (
					transactionCostExpectation + lambdaLongEndU * transactionCostVariance / x,
					1,
					4,
					1.
				) + " |  " + FormatUtil.FormatDouble (
					acd.halfLife(),
					1,
					2,
					1.
				) + "   ||"
			);
		}

		System.out.println ("\t|------------------------------------------------------------||");

		EnvManager.TerminateEnv();
	}
}
