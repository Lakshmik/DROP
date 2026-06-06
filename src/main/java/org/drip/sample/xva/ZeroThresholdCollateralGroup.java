
package org.drip.sample.xva;

import org.drip.analytics.date.*;
import org.drip.exposure.evolver.LatentStateVertexContainer;
import org.drip.exposure.mpor.CollateralAmountEstimator;
import org.drip.exposure.universe.*;
import org.drip.measure.bridge.BrokenDateInterpolatorLinearT;
import org.drip.measure.crng.RandomSequenceGenerator;
import org.drip.measure.dynamics.DiffusionEvaluatorLinear;
import org.drip.measure.realization.*;
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
 * <i>ZeroThresholdCollateralGroup</i> illustrates the Sample Run of a Single Partially Collateralized
 * 	Collateral Group under Zero Bank/Counter Party Threshold with several Fix-Float Swaps. The References
 * 	are:
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
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/xva/README.md">XVA Collateralized Uncollateralized Zero Threshold</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class ZeroThresholdCollateralGroup
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
			double[] atmSwapRateOffsetRealizationArray = ATMSwapRateOffsetRealization (
				atmSwapRateDiffusionEvolver,
				initialATMSwapRate,
				time,
				timeWidth,
				stepCount
			);

			for (int stepIndex = 0; stepIndex <= stepCount; ++stepIndex) {
				swapPortfolioValueRealizationArray[stepIndex] +=
					timeWidth * (stepCount - stepIndex) * atmSwapRateOffsetRealizationArray[stepIndex];
			}
		}

		return swapPortfolioValueRealizationArray;
	}

	private static final double[][] SwapPortfolioValueRealization (
		final DiffusionEvolver atmSwapRateDiffusionEvolver,
		final double swapPortfolioValueStart,
		final int stepCount,
		final double time,
		final double timeWidth,
		final int swapCount,
		final int simulationCount)
		throws Exception
	{
		double[][] swapPortfolioValueRealizationGrid = new double[simulationCount][];

		for (int simulationIndex = 0; simulationIndex < simulationCount; ++simulationIndex)
			swapPortfolioValueRealizationGrid[simulationIndex] = SwapPortfolioValueRealization (
				atmSwapRateDiffusionEvolver,
				swapPortfolioValueStart,
				stepCount,
				time,
				timeWidth,
				swapCount
			);

		return swapPortfolioValueRealizationGrid;
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

		double atmSwapRateDrift = 0.;
		double atmSwapRateStart = 0.;
		double atmSwapRateVolatility = 0.25;

		double csaDrift = 0.01;
		double overnightNumeraireDrift = 0.004;

		double bankHazardRate = 0.015;
		double bankRecoveryRate = 0.4;

		double counterPartyHazardRate = 0.03;
		double counterPartyRecoveryRate = 0.3;

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
			0.,
			0.,
			PositionReplicationScheme.ALBANESE_ANDERSEN_VERTEX,
			BrokenDateScheme.LINEAR_TIME,
			0.,
			CloseOutScheme.ISDA_92
		);

		double[][] swapPortfolioValueRealizationGrid = SwapPortfolioValueRealization (
			new DiffusionEvolver (
				DiffusionEvaluatorLinear.Standard (atmSwapRateDrift, atmSwapRateVolatility)
			),
			atmSwapRateStart,
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
				Math.exp (-0.5 * csaDrift * stepCount),
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
			double startingValue = time * atmSwapRateStart;
			AlbaneseAndersen[] albaneseAndersenArray = new AlbaneseAndersen[stepCount + 1];

			for (int stepIndex = 0; stepIndex <= stepCount; ++stepIndex) {
				JulianDate endDate = vertexDateArray[stepIndex];
				double endingValue = swapPortfolioValueRealizationGrid[pathIndex][stepIndex];

				albaneseAndersenArray[stepIndex] = new AlbaneseAndersen (
					vertexDateArray[stepIndex],
					swapPortfolioValueRealizationGrid[pathIndex][stepIndex],
					0.,
					0 == stepIndex ? 0. : new CollateralAmountEstimator (
						positionGroupSpecification,
						new BrokenDateInterpolatorLinearT (
							startDate.julian(),
							endDate.julian(),
							startingValue,
							endingValue
						),
						Double.NaN
					).postingRequirement (
						endDate
					) 
				);

				startDate = endDate;
				startingValue = endingValue;
			}

			monoPathExposureAdjustmentArray[pathIndex] = new MonoPathExposureAdjustment (
				new AlbaneseAndersenFundingGroupPath[] {
					new AlbaneseAndersenFundingGroupPath (
						new AlbaneseAndersenNettingGroupPath[] {
							new AlbaneseAndersenNettingGroupPath (
								new CollateralGroupPath[] {
									new CollateralGroupPath (albaneseAndersenArray, marketPath)
								},
								marketPath
							)
						},
						marketPath
					)
				}
			);
		}

		ExposureAdjustmentAggregator exposureAdjustmentAggregator =
			new ExposureAdjustmentAggregator (monoPathExposureAdjustmentArray);

		JulianDate[] vertexNodeArray = exposureAdjustmentAggregator.vertexDates();

		System.out.println();

		System.out.println (
			"\t|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|"
		);

		String dump = "\t|         DATE         =>" ;

		for (int vertexNodeIndex = 0; vertexNodeIndex < vertexNodeArray.length; ++vertexNodeIndex) {
			dump = dump + " " + vertexNodeArray[vertexNodeIndex] + " |";
		}

		System.out.println (dump);

		System.out.println (
			"\t|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|"
		);

		double[] collateralizedExposureArray = exposureAdjustmentAggregator.collateralizedExposure();

		dump = "\t|       EXPOSURE       =>";

		for (int collateralizedExposureIndex = 0;
			collateralizedExposureIndex < collateralizedExposureArray.length;
			++collateralizedExposureIndex)
		{
			dump = dump + "   " + FormatUtil.FormatDouble (
				collateralizedExposureArray[collateralizedExposureIndex],
				1,
				4,
				1.
			) + "   |";
		}

		System.out.println (dump);

		double[] collateralizedPositiveExposureArray =
			exposureAdjustmentAggregator.collateralizedPositiveExposure();

		dump = "\t|  POSITIVE EXPOSURE   =>";

		for (int collateralizedPositiveExposureIndex = 0;
			collateralizedPositiveExposureIndex < collateralizedPositiveExposureArray.length;
			++collateralizedPositiveExposureIndex)
		{
			dump = dump + "   " + FormatUtil.FormatDouble (
				collateralizedPositiveExposureArray[collateralizedPositiveExposureIndex],
				1,
				4,
				1.
			) + "   |";
		}

		System.out.println (dump);

		double[] collateralizedNegativeExposureArray =
			exposureAdjustmentAggregator.collateralizedNegativeExposure();

		dump = "\t|  NEGATIVE EXPOSURE   =>";

		for (int collateralizedNegativeExposureIndex = 0;
			collateralizedNegativeExposureIndex < collateralizedNegativeExposureArray.length;
			++collateralizedNegativeExposureIndex)
		{
			dump = dump + "   " + FormatUtil.FormatDouble (
				collateralizedNegativeExposureArray[collateralizedNegativeExposureIndex],
				1,
				4,
				1.
			) + "   |";
		}

		System.out.println (dump);

		double[] collateralizedExposurePVArray = exposureAdjustmentAggregator.collateralizedExposurePV();

		dump = "\t|      EXPOSURE PV     =>";

		for (int collateralizedExposurePVIndex = 0;
			collateralizedExposurePVIndex < collateralizedExposurePVArray.length;
			++collateralizedExposurePVIndex)
		{
			dump = dump + "   " + FormatUtil.FormatDouble (
				collateralizedExposurePVArray[collateralizedExposurePVIndex],
				1,
				4,
				1.
			) + "   |";
		}

		System.out.println (dump);

		double[] collateralizedPositiveExposurePVArray =
			exposureAdjustmentAggregator.collateralizedPositiveExposurePV();

		dump = "\t| POSITIVE EXPOSURE PV =>";

		for (int collateralizedPositiveExposurePVIndex = 0;
			collateralizedPositiveExposurePVIndex < collateralizedPositiveExposurePVArray.length;
			++collateralizedPositiveExposurePVIndex)
		{
			dump = dump + "   " + FormatUtil.FormatDouble (
				collateralizedPositiveExposurePVArray[collateralizedPositiveExposurePVIndex],
				1,
				4,
				1.
			) + "   |";
		}

		System.out.println (dump);

		double[] collateralizedNegativeExposurePVArray =
			exposureAdjustmentAggregator.collateralizedNegativeExposurePV();

		dump = "\t| NEGATIVE EXPOSURE PV =>";

		for (int collateralizedNegativeExposurePVIndex = 0;
			collateralizedNegativeExposurePVIndex < collateralizedNegativeExposurePVArray.length;
			++collateralizedNegativeExposurePVIndex)
		{
			dump = dump + "   " + FormatUtil.FormatDouble (
				collateralizedNegativeExposurePVArray[collateralizedNegativeExposurePVIndex],
				1,
				4,
				1.
			) + "   |";
		}

		System.out.println (dump);

		System.out.println (
			"\t|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|"
		);

		System.out.println();

		System.out.println ("\t||-------------------||");

		System.out.println (
			"\t||  UCVA  => " + FormatUtil.FormatDouble (
				exposureAdjustmentAggregator.ucva().amount(),
				2,
				2,
				100.
			) + "% ||"
		);

		System.out.println (
			"\t|| FTDCVA => " + FormatUtil.FormatDouble (
				exposureAdjustmentAggregator.ftdcva().amount(),
				2,
				2,
				100.
			) + "% ||"
		);

		System.out.println (
			"\t||  CVA   => " + FormatUtil.FormatDouble (
				exposureAdjustmentAggregator.cva().amount(),
				2,
				2,
				100.
			) + "% ||"
		);

		System.out.println (
			"\t||  CVACL => " + FormatUtil.FormatDouble (
				exposureAdjustmentAggregator.cvacl().amount(),
				2,
				2,
				100.
			) + "% ||"
		);

		System.out.println (
			"\t||  DVA   => " + FormatUtil.FormatDouble (
				exposureAdjustmentAggregator.dva().amount(),
				2,
				2,
				100.
			) + "% ||"
		);

		System.out.println (
			"\t||  FVA   => " + FormatUtil.FormatDouble (
				exposureAdjustmentAggregator.fva().amount(),
				2,
				2,
				100.
			) + "% ||"
		);

		System.out.println (
			"\t||  FDA   => " + FormatUtil.FormatDouble (
				exposureAdjustmentAggregator.fda().amount(),
				2,
				2,
				100.
			) + "% ||"
		);

		System.out.println (
			"\t||  FCA   => " + FormatUtil.FormatDouble (
				exposureAdjustmentAggregator.fca().amount(),
				2,
				2,
				100.
			) + "% ||"
		);

		System.out.println (
			"\t||  FBA   => " + FormatUtil.FormatDouble (
				exposureAdjustmentAggregator.fba().amount(),
				2,
				2,
				100.
			) + "% ||"
		);

		System.out.println (
			"\t||  SFVA  => " + FormatUtil.FormatDouble (
				exposureAdjustmentAggregator.sfva().amount(),
				2,
				2,
				100.
			) + "% ||"
		);

		System.out.println ("\t||-------------------||");

		EnvManager.TerminateEnv();
	}
}
