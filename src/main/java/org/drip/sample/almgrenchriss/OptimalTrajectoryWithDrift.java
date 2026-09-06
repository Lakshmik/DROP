
package org.drip.sample.almgrenchriss;

import org.drip.execution.capture.*;
import org.drip.execution.dynamics.*;
import org.drip.execution.impact.*;
import org.drip.execution.nonadaptive.DiscreteAlmgrenChrissDrift;
import org.drip.execution.optimum.AlmgrenChrissDriftDiscrete;
import org.drip.execution.parameters.*;
import org.drip.execution.profiletime.UniformParticipationRateLinear;
import org.drip.function.r1tor1operator.Flat;
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
 * <i>OptimalTrajectoryWithDrift</i> demonstrates the Generation of the Optimal Trading Trajectory in
 * 	accordance with the Specification of Almgren and Chriss (2000) for the given Risk Aversion Parameter
 * 	inclusive of the Asset Drift. The References are:
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

public class OptimalTrajectoryWithDrift
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
		double bidAsk = 0.125;
		double lambdaU = 1.e-06;
		double annualReturns = 0.1;
		double dailyVolume = 5.e06;
		double annualVolatility = 0.3;
		double dailyVolumePermanentImpact = 0.1;
		double dailyVolumeTemporaryImpact = 0.01;

		ArithmeticPriceDynamicsSettings arithmeticPriceDynamicsSettings =
			ArithmeticPriceDynamicsSettings.FromAnnualReturnsSettings (
				annualReturns,
				annualVolatility,
				0.,
				s0
			);

		double alpha = arithmeticPriceDynamicsSettings.drift();

		double sigma = arithmeticPriceDynamicsSettings.epochVolatility();

		PriceMarketImpactLinear priceMarketImpactLinear = new PriceMarketImpactLinear (
			new AssetTransactionSettings (s0, dailyVolume, bidAsk),
			dailyVolumePermanentImpact,
			dailyVolumeTemporaryImpact
		);

		ParticipationRateLinear permanentTransactionFunction =
			(ParticipationRateLinear) priceMarketImpactLinear.permanentTransactionFunction();

		ParticipationRateLinear temporaryTransactionFunction =
			(ParticipationRateLinear) priceMarketImpactLinear.temporaryTransactionFunction();

		LinearPermanentExpectationParameters linearPermanentExpectationParameters =
			ArithmeticPriceEvolutionParametersBuilder.LinearExpectation (
				new ArithmeticPriceDynamicsSettings (alpha, new Flat (sigma), 0.),
				new UniformParticipationRateLinear (permanentTransactionFunction),
				new UniformParticipationRateLinear (temporaryTransactionFunction)
			);

		AlmgrenChrissDriftDiscrete almgrenChrissDriftDiscrete =
			(AlmgrenChrissDriftDiscrete) DiscreteAlmgrenChrissDrift.Standard (
				x,
				t,
				n,
				linearPermanentExpectationParameters,
				lambdaU
			).generate();

		double[] tradeListDriftAdjustmentArray = almgrenChrissDriftDiscrete.tradeListDriftAdjustment();

		double[] holdingsDriftAdjustmentArray = almgrenChrissDriftDiscrete.holdingsDriftAdjustment();

		double[] executionTimeNodeArray = almgrenChrissDriftDiscrete.executionTimeNode();

		double[] holdingsArray = almgrenChrissDriftDiscrete.holdings();

		double[] tradeArray = almgrenChrissDriftDiscrete.tradeList();

		LinearImpactTrajectoryEstimator linearImpactTrajectoryEstimator =
			new LinearImpactTrajectoryEstimator (almgrenChrissDriftDiscrete);

		TrajectoryShortfallAggregate trajectoryShortfallAggregate =
			linearImpactTrajectoryEstimator.totalCostDistributionDetail (
				linearPermanentExpectationParameters
			);

		double[] incrementalPermanentImpactExpectationArray =
			trajectoryShortfallAggregate.incrementalPermanentImpactExpectation();

		double[] incrementalTemporaryImpactExpectationArray =
			trajectoryShortfallAggregate.incrementalTemporaryImpactExpectation();

		double[] cumulativePermanentImpactExpectationArray =
			trajectoryShortfallAggregate.cumulativePermanentImpactExpectation();

		double[] cumulativeTemporaryImpactExpectationArray =
			trajectoryShortfallAggregate.cumulativeTemporaryImpactExpectation();

		double[] incrementalShortfallVarianceArray = trajectoryShortfallAggregate.incrementalVariance();

		double[] incrementalShortfallMeanArray = trajectoryShortfallAggregate.incrementalExpectation();

		double[] cumulativeShortfallVarianceArray = trajectoryShortfallAggregate.cumulativeVariance();

		double[] cumulativeShortfallMeanArray = trajectoryShortfallAggregate.cumulativeExpectation();

		R1UnivariateNormal r1UnivariateNormal =
			linearImpactTrajectoryEstimator.totalCostDistributionSynopsis (
				linearPermanentExpectationParameters
			);

		double permanentTransactionFunctionSlope = permanentTransactionFunction.slope();

		System.out.println ("\n\t|---------------------------------------------||");

		System.out.println ("\t| ALMGREN-CHRISS TRAJECTORY GENERATOR INPUTS  ||");

		System.out.println ("\t|---------------------------------------------||");

		System.out.println ("\t| Initial Stock Price           : " + s0);

		System.out.println ("\t| Initial Holdings              : " + x);

		System.out.println ("\t| Liquidation Time              : " + t);

		System.out.println ("\t| Number of Time Periods        : " + n);

		System.out.println (
			"\t| Annual Volatility             :" +
				FormatUtil.FormatDouble (annualVolatility, 1, 0, 100.) + "%"
		);

		System.out.println (
			"\t| Annual Growth                 :" + FormatUtil.FormatDouble (annualReturns, 1, 0, 100.) + "%"
		);

		System.out.println ("\t| Bid-Ask Spread                : " + bidAsk);

		System.out.println ("\t| Daily Volume                  : " + dailyVolume);

		System.out.println ("\t| Daily Volume Temporary Impact : " + dailyVolumeTemporaryImpact);

		System.out.println ("\t| Daily Volume Permanent Impact : " + dailyVolumePermanentImpact);

		System.out.println ("\t| Daily Volume 5 million Shares : " + permanentTransactionFunctionSlope);

		System.out.println ("\t| Static Holdings 11,000 Shares : " + lambdaU);

		System.out.println ("\t|");

		System.out.println (
			"\t| Daily Volatility              : " +
			FormatUtil.FormatDouble (sigma, 1, 4, 1.)
		);

		System.out.println (
			"\t| Daily Returns                 : " +
			FormatUtil.FormatDouble (alpha, 1, 4, 1.)
		);

		System.out.println ("\t| Temporary Impact Fixed Offset :  " + temporaryTransactionFunction.offset());

		System.out.println ("\t| Eta                           :  " + temporaryTransactionFunction.slope());

		System.out.println ("\t| Gamma                         :  " + permanentTransactionFunctionSlope);

		System.out.println ("\t|---------------------------------------------||");

		System.out.println ("\n\t|-------------------------------------------------||");

		System.out.println ("\t|           AC2000 Optimal Trajectory             ||");

		System.out.println ("\t|           ------ ------- ----------             ||");

		System.out.println ("\t|     L -> R:                                     ||");

		System.out.println ("\t|             Time Node                           ||");

		System.out.println ("\t|             Holdings                            ||");

		System.out.println ("\t|             Trade Amount                        ||");

		System.out.println ("\t|             Holdings Drift Adjustment           ||");

		System.out.println ("\t|             Trade Amount Drift Adjustment       ||");

		System.out.println ("\t|-------------------------------------------------||");

		for (int executionTimeIndex = 0; executionTimeIndex <= n; ++executionTimeIndex) {
			System.out.println (
				"\t|" + FormatUtil.FormatDouble (
					executionTimeNodeArray[executionTimeIndex],
					1,
					0,
					1.
				) + " => " + FormatUtil.FormatDouble (
					holdingsArray[executionTimeIndex],
					7,
					1,
					1.
				) + " | " + FormatUtil.FormatDouble (
					0 == executionTimeIndex ? 0. : tradeArray[executionTimeIndex - 1],
					6,
					1,
					1.
				) + " | " + FormatUtil.FormatDouble (
					holdingsDriftAdjustmentArray[executionTimeIndex],
					4,
					1,
					1.
				) + " | " + FormatUtil.FormatDouble (
					0 == executionTimeIndex ? 0. : tradeListDriftAdjustmentArray[executionTimeIndex - 1],
					4,
					1,
					1.
				) + " ||"
			);
		}

		System.out.println ("\t|-------------------------------------------------||");

		System.out.println ("\n\t|-----------------------------------------------------------||");

		System.out.println ("\t|               PERIOD LEVEL COST DISTRIBUTION              ||");

		System.out.println ("\t|-----------------------------------------------------------||");

		System.out.println ("\t|  PERIOD   |          MEAN         |        VARIANCE       ||");

		System.out.println ("\t|-----------------------------------------------------------||");

		System.out.println ("\t|  PERIOD   |    INCR   |    CUML   |    INCR   |    CUML   ||");

		System.out.println ("\t|-----------------------------------------------------------||");

		for (int shortfallIndex = 0; shortfallIndex < incrementalShortfallMeanArray.length; ++shortfallIndex)
		{
			System.out.println (
				"\t| PERIOD #" + (shortfallIndex + 1) + " | " + FormatUtil.FormatDouble (
					incrementalShortfallMeanArray[shortfallIndex],
					6,
					1,
					1.
				) + " | " + FormatUtil.FormatDouble (
					cumulativeShortfallMeanArray[shortfallIndex],
					6,
					1,
					1.
				) + " | " + FormatUtil.FormatDouble (
					incrementalShortfallVarianceArray[shortfallIndex],
					6,
					1,
					1.e-06
				) + " | " + FormatUtil.FormatDouble (
					cumulativeShortfallVarianceArray[shortfallIndex],
					6,
					1,
					1.e-06
				) + " ||"
			);
		}

		System.out.println ("\t|-----------------------------------------------------------||");

		System.out.println ("\n\t|-----------------------------------------------------------||");

		System.out.println ("\t|            PERIOD LEVEL COST IMPACT CONTRIBUTION          ||");

		System.out.println ("\t|-----------------------------------------------------------||");

		System.out.println ("\t|  PERIOD   |       PERMANENT       |       TEMPORARY       ||");

		System.out.println ("\t|-----------------------------------------------------------||");

		System.out.println ("\t|  PERIOD   |    INCR   |    CUML   |    INCR   |    CUML   ||");

		System.out.println ("\t|-----------------------------------------------------------||");

		for (int impactExpectationIndex = 0;
			impactExpectationIndex < incrementalPermanentImpactExpectationArray.length;
			++impactExpectationIndex)
		{
			System.out.println (
				"\t| PERIOD #" + (impactExpectationIndex + 1) + " | " + FormatUtil.FormatDouble (
					incrementalPermanentImpactExpectationArray[impactExpectationIndex],
					6,
					1,
					1.
				) + " | " + FormatUtil.FormatDouble (
					cumulativePermanentImpactExpectationArray[impactExpectationIndex],
					6,
					1,
					1.
				) + " | " + FormatUtil.FormatDouble (
					incrementalTemporaryImpactExpectationArray[impactExpectationIndex],
					6,
					1,
					1.
				) + " | " + FormatUtil.FormatDouble (
					cumulativeTemporaryImpactExpectationArray[impactExpectationIndex],
					6,
					1,
					1.
				) + " ||"
			);
		}

		System.out.println ("\t|-----------------------------------------------------------||");

		System.out.println ("\n\t|--------------------------------------------------------------||");

		System.out.println ("\t| TRANSACTION COST RECONCILIATION: AC2000 vs. EXPLICIT LINEAR  ||");

		System.out.println ("\t|--------------------------------------------------------------||");

		System.out.println (
			"\t| Transaction Cost Expectation         : " +
			FormatUtil.FormatDouble (r1UnivariateNormal.mean(), 6, 1, 1.) + " | " +
			FormatUtil.FormatDouble (almgrenChrissDriftDiscrete.transactionCostExpectation(), 6, 1, 1.) +
				" ||"
		);

		System.out.println (
			"\t| Transaction Cost Variance (X 10^-06) : " +
			FormatUtil.FormatDouble (r1UnivariateNormal.variance(), 6, 1, 1.e-06) + " | " +
			FormatUtil.FormatDouble (almgrenChrissDriftDiscrete.transactionCostVariance(), 6, 1, 1.e-06) +
				" ||"
		);

		System.out.println ("\t|--------------------------------------------------------------||");

		System.out.println ("\n\t|---------------------------------||");

		System.out.println ("\t|       AC2000 METRICS DUMP       ||");

		System.out.println ("\t|---------------------------------||");

		System.out.println (
			"\t| Kappa                  : " +
				FormatUtil.FormatDouble (almgrenChrissDriftDiscrete.kappa(), 1, 3, 1.) + " ||"
		);

		System.out.println (
			"\t| Kappa Tilda            : " +
				FormatUtil.FormatDouble (almgrenChrissDriftDiscrete.kappaTilda(), 1, 3, 1.) + " ||"
		);

		System.out.println (
			"\t| Half Life              : " +
				FormatUtil.FormatDouble (almgrenChrissDriftDiscrete.halfLife(), 1, 3, 1.) + " ||"
		);

		/* System.out.println (
		 * 	"\t| Market Power: " + FormatUtil.FormatDouble (acdd.marketPower(), 1, 4, 1.) + " ||"
		 * );
		 */

		System.out.println (
			"\t| Residual Holdings      : " +
				FormatUtil.FormatDouble (almgrenChrissDriftDiscrete.residualHolding(), 5, 0, 1.) + " ||"
		);

		System.out.println (
			"\t| Drift Gain Upper Bound : " +
				FormatUtil.FormatDouble (almgrenChrissDriftDiscrete.driftGainUpperBound(), 2, 1, 1.) + " ||"
		);

		System.out.println ("\t|---------------------------------||");

		EnvManager.TerminateEnv();
	}
}
