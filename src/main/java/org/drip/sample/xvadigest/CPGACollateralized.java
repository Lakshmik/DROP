
package org.drip.sample.xvadigest;

import org.drip.analytics.date.*;
import org.drip.exposure.evolver.LatentStateVertexContainer;
import org.drip.exposure.mpor.CollateralAmountEstimator;
import org.drip.exposure.universe.*;
import org.drip.measure.bridge.BrokenDateInterpolatorLinearT;
import org.drip.measure.crng.RandomSequenceGenerator;
import org.drip.measure.dynamics.DiffusionEvaluatorLinear;
import org.drip.measure.realization.*;
import org.drip.measure.statistics.UnivariateCentralMeasures;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.state.identifier.OTCFixFloatLabel;
import org.drip.xva.gross.*;
import org.drip.xva.netting.CollateralGroupPath;
import org.drip.xva.proto.*;
import org.drip.xva.settings.*;
import org.drip.xva.strategy.*;
import org.drip.xva.vertex.AlbaneseAndersen;

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
 * <i>CPGACollateralized</i> illustrates the Counter Party Aggregation over Netting Groups based
 * 	Collateralized Collateral Groups with several Fix-Float Swaps. The References are:
 *
 *  <br><br>
 *  <ul>
 *  	<li>
 *  		Burgard, C., and M. Kjaer (2014): PDE Representations of Derivatives with Bilateral Counter-party
 *  			Risk and Funding Costs <i>Journal of Credit Risk</i> <b>7 (3)</b> 1-19
 *  	</li>
 *  	<li>
 *  		Burgard, C., and M. Kjaer (2014): In the Balance <i>Risk</i> <b>24 (11)</b> 72-75
 *  	</li>
 *  	<li>
 *  		Gregory, J. (2009): Being Two-faced over Counter-party Credit Risk <i>Risk</i> <b>20 (2)</b>
 *  			86-90
 *  	</li>
 *  	<li>
 *  		Li, B., and Y. Tang (2007): <i>Quantitative Analysis, Derivatives Modeling, and Trading
 *  			Strategies in the Presence of Counter-party Credit Risk for the Fixed Income Market</i>
 *  			<b>World Scientific Publishing</b> Singapore
 *  	</li>
 *  	<li>
 *  		Piterbarg, V. (2010): Funding Beyond Discounting: Collateral Agreements and Derivatives Pricing
 *  			<i>Risk</i> <b>21 (2)</b> 97-102
 *  	</li>
 *  </ul>
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/xvadigest/README.md">Basel XVA Accounting Metrics Digest</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class CPGACollateralized
{

	private static final double[] ATMSwapRateOffsetRealization (
		final DiffusionEvolver atmSwapRateOffsetDiffusionEvolver,
		final double initialATMSwapRateOffset,
		final double time,
		final double timeWidth,
		final int stepCount)
		throws Exception
	{
		double[] atmSwapRateOffsetArray = new double[stepCount + 1];
		atmSwapRateOffsetArray[0] = initialATMSwapRateOffset;
		double[] timeWidthArray = new double[stepCount];

		for (int stepIndex = 0; stepIndex < stepCount; ++stepIndex) {
			timeWidthArray[stepIndex] = timeWidth;
		}

		JumpDiffusionEdge[] jumpDiffusionEdgeArray = atmSwapRateOffsetDiffusionEvolver.incrementSequence (
			new JumpDiffusionVertex (time, initialATMSwapRateOffset, 0., false),
			JumpDiffusionEdgeUnit.Diffusion (timeWidthArray, RandomSequenceGenerator.Gaussian (stepCount)),
			timeWidth
		);

		for (int stepIndex = 1; stepIndex <= stepCount; ++stepIndex) {
			atmSwapRateOffsetArray[stepIndex] = jumpDiffusionEdgeArray[stepIndex - 1].finish();
		}

		return atmSwapRateOffsetArray;
	}

	private static final double[] SwapPortfolioValueRealization (
		final DiffusionEvolver atmSwapRateDiffusionEvolver,
		final double initialATMSwapRate,
		final int stepCount,
		final double time,
		final double timeWidth,
		final int swapCount)
		throws Exception
	{
		double[] swapPortfolioValueRealizationArray = new double[stepCount + 1];

		for (int stepIndex = 0; stepIndex < stepCount; ++stepIndex) {
			swapPortfolioValueRealizationArray[stepIndex] = 0.;
		}

		for (int swapIndex = 0; swapIndex < swapCount; ++swapIndex) {
			double[] atmSwapRateOffsetArrayRealization = ATMSwapRateOffsetRealization (
				atmSwapRateDiffusionEvolver,
				initialATMSwapRate,
				time,
				timeWidth,
				stepCount
			);

			for (int stepIndex = 0; stepIndex <= stepCount; ++stepIndex) {
				swapPortfolioValueRealizationArray[stepIndex] +=
					timeWidth * (stepCount - stepIndex) * atmSwapRateOffsetArrayRealization[stepIndex];
			}
		}

		return swapPortfolioValueRealizationArray;
	}

	private static final double[][] SwapPortfolioValueRealization (
		final DiffusionEvolver atmSwapRateDiffusionEvolver,
		final double initalSwapPortfolioValue,
		final int stepCount,
		final double time,
		final double timeWidth,
		final int swapCount,
		final int simulationCount)
		throws Exception
	{
		double[][] swapPortfolioValueRealizationArray = new double[simulationCount][];

		for (int simulationIndex = 0; simulationIndex < simulationCount; ++simulationIndex) {
			swapPortfolioValueRealizationArray[simulationIndex] = SwapPortfolioValueRealization (
				atmSwapRateDiffusionEvolver,
				initalSwapPortfolioValue,
				stepCount,
				time,
				timeWidth,
				swapCount
			);
		}

		return swapPortfolioValueRealizationArray;
	}

	private static final void univariateCentralMeasuresDump (
		final String header,
		final JulianDate[] vertexNodeArray,
		final UnivariateCentralMeasures[] univariateCentralMeasuresArray)
		throws Exception
	{
		System.out.println (
			"\t|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|"
		);

		System.out.println (header);

		System.out.println (
			"\t|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|"
		);

		String dump = "\t|       DATE      =>" ;

		for (int nodeIndex = 0; nodeIndex < vertexNodeArray.length; ++nodeIndex) {
			dump = dump + " " + vertexNodeArray[nodeIndex] + "  |";
		}

		System.out.println (dump);

		System.out.println (
			"\t|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|"
		);

		dump = "\t|     AVERAGE     =>";

		for (int univariateCentralMeasuresIndex = 0;
			univariateCentralMeasuresIndex < univariateCentralMeasuresArray.length;
			++univariateCentralMeasuresIndex)
		{
			dump = dump + "   " + FormatUtil.FormatDouble (
				univariateCentralMeasuresArray[univariateCentralMeasuresIndex].average(),
				2,
				4,
				1.
			) + "   |";
		}

		System.out.println (dump);

		dump = "\t|     MAXIMUM     =>";

		for (int univariateCentralMeasuresIndex = 0;
			univariateCentralMeasuresIndex < univariateCentralMeasuresArray.length;
			++univariateCentralMeasuresIndex)
		{
			dump = dump + "   " + FormatUtil.FormatDouble (
				univariateCentralMeasuresArray[univariateCentralMeasuresIndex].maximum(),
				2,
				4,
				1.
			) + "   |";
		}

		System.out.println (dump);

		dump = "\t|     MINIMUM     =>";

		for (int univariateCentralMeasuresIndex = 0;
			univariateCentralMeasuresIndex < univariateCentralMeasuresArray.length;
			++univariateCentralMeasuresIndex)
		{
			dump = dump + "   " + FormatUtil.FormatDouble (
				univariateCentralMeasuresArray[univariateCentralMeasuresIndex].minimum(),
				2,
				4,
				1.
			) + "   |";
		}

		System.out.println (dump);

		dump = "\t|      ERROR      =>";

		for (int univariateCentralMeasuresIndex = 0;
			univariateCentralMeasuresIndex < univariateCentralMeasuresArray.length;
			++univariateCentralMeasuresIndex)
		{
			dump = dump + "   " + FormatUtil.FormatDouble (
				univariateCentralMeasuresArray[univariateCentralMeasuresIndex].error(),
				2,
				4,
				1.
			) + "   |";
		}

		System.out.println (dump);

		System.out.println (
			"\t|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|"
		);
	}

	private static final void univariateCentralMeasuresDump (
		final String header,
		final UnivariateCentralMeasures univariateCentralMeasures)
		throws Exception
	{
		System.out.println (
			header +
			FormatUtil.FormatDouble (univariateCentralMeasures.average(), 3, 2, 100.) + "% | " +
			FormatUtil.FormatDouble (univariateCentralMeasures.maximum(), 3, 2, 100.) + "% | " +
			FormatUtil.FormatDouble (univariateCentralMeasures.minimum(), 3, 2, 100.) + "% | " +
			FormatUtil.FormatDouble (univariateCentralMeasures.error(), 3, 2, 100.) + "% ||"
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

		int stepCount = 10;
		int swapCount = 10;
		int pathCount = 10000;

		double time = 5.;

		double initialATMSwapRate = 0.;

		double atmSwapRateDrift = 0.;
		double atmSwapRateVolatility = 0.25;

		double overnightNumeraireDrift = 0.004;

		double csaDrift = 0.01;

		double bankThreshold = -0.1;
		double bankHazardRate = 0.015;
		double bankRecoveryRate = 0.40;

		double counterPartyThreshold = 0.1;
		double counterPartyHazardRate = 0.030;
		double counterPartyRecoveryRate = 0.30;

		JulianDate spotDate = DateUtil.Today();

		double timeWidth = time / stepCount;
		JulianDate[] vertexDateArray = new JulianDate[stepCount + 1];
		MarketVertex[] marketVertexArray = new MarketVertex[stepCount + 1];
		double bankFundingSpread = bankHazardRate / (1. - bankRecoveryRate);
		double counterPartyFundingSpread = counterPartyHazardRate / (1. - counterPartyRecoveryRate);
		MonoPathExposureAdjustment[] monoPathExposureAdjustmentArray =
			new MonoPathExposureAdjustment[pathCount];

		PositionGroupSpecification positionGroupSpecification = PositionGroupSpecification.FixedThreshold (
			"FIXEDTHRESHOLD",
			counterPartyThreshold,
			bankThreshold,
			PositionReplicationScheme.ALBANESE_ANDERSEN_VERTEX,
			BrokenDateScheme.LINEAR_TIME,
			0.,
			CloseOutScheme.ISDA_92
		);

		double[][] swapPortfolioValueRealizationArray = SwapPortfolioValueRealization (
			new DiffusionEvolver (
				DiffusionEvaluatorLinear.Standard (atmSwapRateDrift, atmSwapRateVolatility)
			),
			initialATMSwapRate,
			stepCount,
			time,
			timeWidth,
			swapCount,
			pathCount
		);

		for (int stepIndex = 0; stepIndex <= stepCount; ++stepIndex) {
			LatentStateVertexContainer latentStateVertexContainer = new LatentStateVertexContainer();

			latentStateVertexContainer.add (OTCFixFloatLabel.Standard ("USD-3M-10Y"), Double.NaN);

			marketVertexArray[stepIndex] = MarketVertex.Nodal (
				vertexDateArray[stepIndex] = spotDate.addMonths (6 * stepIndex),
				overnightNumeraireDrift,
				Math.exp (-0.5 * overnightNumeraireDrift * (stepCount - stepIndex)),
				csaDrift,
				Math.exp (-0.5 * csaDrift * (stepCount - stepIndex)),
				new MarketVertexEntity (
					Math.exp (-0.5 * bankHazardRate * stepIndex),
					bankHazardRate,
					bankRecoveryRate,
					bankFundingSpread,
					Math.exp (-0.5 * bankHazardRate * (1. - bankRecoveryRate) * (stepCount - stepIndex)),
					Double.NaN,
					Double.NaN,
					Double.NaN
				),
				new MarketVertexEntity (
					Math.exp (-0.5 * counterPartyHazardRate * stepIndex),
					counterPartyHazardRate,
					counterPartyRecoveryRate,
					counterPartyFundingSpread,
					Math.exp (
						-0.5 * counterPartyHazardRate * (1. - counterPartyRecoveryRate) *
							(stepCount - stepIndex)
					),
					Double.NaN,
					Double.NaN,
					Double.NaN
				),
				latentStateVertexContainer
			);
		}

		MarketPath marketPath = MarketPath.FromMarketVertexArray (marketVertexArray);

		for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex) {
			JulianDate startDate = spotDate;
			double valueStart = time * initialATMSwapRate;
			AlbaneseAndersen[] albaneseAndersenArray = new AlbaneseAndersen[stepCount + 1];

			for (int stepIndex = 0; stepIndex <= stepCount; ++stepIndex) {
				albaneseAndersenArray[stepIndex] = new AlbaneseAndersen (
					vertexDateArray[stepIndex],
					swapPortfolioValueRealizationArray[pathIndex][stepIndex],
					0.,
					0 == stepIndex ? 0. : new CollateralAmountEstimator (
						positionGroupSpecification,
						new BrokenDateInterpolatorLinearT (
							startDate.julian(),
							vertexDateArray[stepIndex].julian(),
							valueStart,
							swapPortfolioValueRealizationArray[pathIndex][stepIndex]
						),
						Double.NaN
					).postingRequirement (
						vertexDateArray[stepIndex]
					)
				);

				startDate = vertexDateArray[stepIndex];
				valueStart = swapPortfolioValueRealizationArray[pathIndex][stepIndex];
			}

			CollateralGroupPath[] collateralGroupPathArray =
			{
				new CollateralGroupPath (albaneseAndersenArray, marketPath)
			};

			monoPathExposureAdjustmentArray[pathIndex] = new MonoPathExposureAdjustment (
				new AlbaneseAndersenFundingGroupPath[]
				{
					new AlbaneseAndersenFundingGroupPath (
						new AlbaneseAndersenNettingGroupPath[]
						{
							new AlbaneseAndersenNettingGroupPath (collateralGroupPathArray, marketPath)
						},
						marketPath
					)
				}
			);
		}

		ExposureAdjustmentAggregator exposureAdjustmentAggregator =
			new ExposureAdjustmentAggregator (monoPathExposureAdjustmentArray);

		ExposureAdjustmentDigest exposureAdjustmentDigest = exposureAdjustmentAggregator.digest();

		System.out.println();

		univariateCentralMeasuresDump (
			"\t|                                                                                COLLATERALIZED EXPOSURE                                                                                |",
			exposureAdjustmentAggregator.vertexDates(),
			exposureAdjustmentDigest.collateralizedExposure()
		);

		univariateCentralMeasuresDump (
			"\t|                                                                               UNCOLLATERALIZED EXPOSURE                                                                               |",
			exposureAdjustmentAggregator.vertexDates(),
			exposureAdjustmentDigest.uncollateralizedExposure()
		);

		univariateCentralMeasuresDump (
			"\t|                                                                                COLLATERALIZED EXPOSURE PV                                                                             |",
			exposureAdjustmentAggregator.vertexDates(),
			exposureAdjustmentDigest.collateralizedExposurePV()
		);

		univariateCentralMeasuresDump (
			"\t|                                                                               UNCOLLATERALIZED EXPOSURE PV                                                                            |",
			exposureAdjustmentAggregator.vertexDates(),
			exposureAdjustmentDigest.uncollateralizedExposurePV()
		);

		univariateCentralMeasuresDump (
			"\t|                                                                            COLLATERALIZED POSITIVE EXPOSURE PV                                                                        |",
			exposureAdjustmentAggregator.vertexDates(),
			exposureAdjustmentDigest.collateralizedPositiveExposure()
		);

		univariateCentralMeasuresDump (
			"\t|                                                                           UNCOLLATERALIZED POSITIVE EXPOSURE PV                                                                       |",
			exposureAdjustmentAggregator.vertexDates(),
			exposureAdjustmentDigest.uncollateralizedPositiveExposure()
		);

		univariateCentralMeasuresDump (
			"\t|                                                                            COLLATERALIZED NEGATIVE EXPOSURE PV                                                                        |",
			exposureAdjustmentAggregator.vertexDates(),
			exposureAdjustmentDigest.collateralizedNegativeExposure()
		);

		univariateCentralMeasuresDump (
			"\t|                                                                           UNCOLLATERALIZED NEGATIVE EXPOSURE PV                                                                       |",
			exposureAdjustmentAggregator.vertexDates(),
			exposureAdjustmentDigest.uncollateralizedNegativeExposure()
		);

		System.out.println();

		System.out.println ("\t||-----------------------------------------------------||");

		System.out.println ("\t||  UCVA CVA FTDCVA DVA FCA UNIVARIATE THIN STATISTICS ||");

		System.out.println ("\t||-----------------------------------------------------||");

		System.out.println ("\t||    L -> R:                                          ||");

		System.out.println ("\t||            - Path Average                           ||");

		System.out.println ("\t||            - Path Maximum                           ||");

		System.out.println ("\t||            - Path Minimum                           ||");

		System.out.println ("\t||            - Monte Carlo Error                      ||");

		System.out.println ("\t||-----------------------------------------------------||");

		univariateCentralMeasuresDump ("\t||  UCVA  => ", exposureAdjustmentDigest.ucva());

		univariateCentralMeasuresDump ("\t|| FTDCVA => ", exposureAdjustmentDigest.ftdcva());

		univariateCentralMeasuresDump ("\t||   CVA  => ", exposureAdjustmentDigest.cva());

		univariateCentralMeasuresDump ("\t||  CVACL => ", exposureAdjustmentDigest.cvacl());

		univariateCentralMeasuresDump ("\t||   DVA  => ", exposureAdjustmentDigest.dva());

		univariateCentralMeasuresDump ("\t||   FVA  => ", exposureAdjustmentDigest.fva());

		univariateCentralMeasuresDump ("\t||   FDA  => ", exposureAdjustmentDigest.fda());

		univariateCentralMeasuresDump ("\t||   FCA  => ", exposureAdjustmentDigest.fca());

		univariateCentralMeasuresDump ("\t||   FBA  => ", exposureAdjustmentDigest.fba());

		univariateCentralMeasuresDump ("\t||  SFVA  => ", exposureAdjustmentDigest.sfva());

		System.out.println ("\t||-----------------------------------------------------||");

		univariateCentralMeasuresDump ("\t||  Total => ", exposureAdjustmentDigest.totalVA());

		System.out.println ("\t||-----------------------------------------------------||");

		System.out.println();

		EnvManager.TerminateEnv();
	}
}
