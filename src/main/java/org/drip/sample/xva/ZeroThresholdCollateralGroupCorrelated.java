
package org.drip.sample.xva;

import org.drip.analytics.date.*;
import org.drip.exposure.evolver.LatentStateVertexContainer;
import org.drip.exposure.mpor.CollateralAmountEstimator;
import org.drip.exposure.universe.*;
import org.drip.measure.bridge.BrokenDateInterpolatorLinearT;
import org.drip.measure.crng.RandomSequenceGenerator;
import org.drip.measure.dynamics.*;
import org.drip.measure.realization.*;
import org.drip.numerical.linearalgebra.R1MatrixUtil;
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
 * <i>ZeroThresholdCollateralGroupCorrelated</i> illustrates the Sample Run of a Single Partially
 * 	Collateralized Collateral Group under Zero Bank/Counter Party Threshold with several Fix-Float Swaps, and
 * 	with built in Factor Correlations across the Numeraires. The References are:
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

public class ZeroThresholdCollateralGroupCorrelated
{

	private static final double[] NumeraireValueRealization (
		final DiffusionEvolver numeraireValueDiffusionEvolver,
		final double initialNumeraireValue,
		final double time,
		final double timeWidth,
		final double[] randomArray,
		final int stepCount)
		throws Exception
	{
		double[] numeraireValueArray = new double[stepCount + 1];
		double[] timeWidthArray = new double[stepCount];
		numeraireValueArray[0] = initialNumeraireValue;

		for (int stepIndex = 0; stepIndex < stepCount; ++stepIndex) {
			timeWidthArray[stepIndex] = timeWidth;
		}

		JumpDiffusionEdge[] jumpDiffusionEdgeArray = numeraireValueDiffusionEvolver.incrementSequence (
			new JumpDiffusionVertex (time, initialNumeraireValue, 0., false),
			JumpDiffusionEdgeUnit.Diffusion (timeWidthArray, randomArray),
			timeWidth
		);

		for (int stepIndex = 1; stepIndex <= stepCount; ++stepIndex) {
			numeraireValueArray[stepIndex] = jumpDiffusionEdgeArray[stepIndex - 1].finish();
		}

		return numeraireValueArray;
	}

	private static final double[] VertexNumeraireRealization (
		final DiffusionEvolver numeraireValueDiffusionEvolver,
		final double numeraireValueInitial,
		final double time,
		final double timeWidth,
		final double[] randomArray,
		final int stepCount)
		throws Exception
	{
		double[] numeraireValueArray = new double[stepCount + 1];
		double[] timeWidthArray = new double[stepCount];

		for (int stepIndex = 0; stepIndex < stepCount; ++stepIndex) {
			timeWidthArray[stepIndex] = timeWidth;
		}

		JumpDiffusionVertex[] jumpDiffusionVertexArray =
			numeraireValueDiffusionEvolver.vertexSequenceReverse (
				new JumpDiffusionVertex (time, numeraireValueInitial, 0., false),
				JumpDiffusionEdgeUnit.Diffusion (timeWidthArray, randomArray),
				timeWidthArray
			);

		for (int stepIndex = 0; stepIndex <= stepCount; ++stepIndex) {
			numeraireValueArray[stepIndex] = jumpDiffusionVertexArray[stepIndex].value();
		}

		return numeraireValueArray;
	}

	private static final double[] ATMSwapRateOffsetRealization (
		final DiffusionEvolver atmSwapRateOffsetDiffusionEvolver,
		final double initialATMSwapRateOffset,
		final double[] randomArray,
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

		JumpDiffusionEdge[] aJDE = atmSwapRateOffsetDiffusionEvolver.incrementSequence (
			new JumpDiffusionVertex (time, initialATMSwapRateOffset, 0., false),
			JumpDiffusionEdgeUnit.Diffusion (timeWidthArray, randomArray),
			timeWidth
		);

		for (int stepIndex = 1; stepIndex <= stepCount; ++stepIndex) {
			atmSwapRateOffsetArray[stepIndex] = aJDE[stepIndex - 1].finish();
		}

		return atmSwapRateOffsetArray;
	}

	private static final double[] SwapPortfolioValueRealization (
		final DiffusionEvolver atmSwapRateDiffusionEvolver,
		final double atmSwapRateStart,
		final double[] randomArray,
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
			double[] adblATMSwapRateOffsetRealization = ATMSwapRateOffsetRealization (
				atmSwapRateDiffusionEvolver,
				atmSwapRateStart,
				randomArray,
				time,
				timeWidth,
				stepCount
			);

			for (int stepIndex = 0; stepIndex <= stepCount; ++stepIndex) {
				swapPortfolioValueRealizationArray[stepIndex] +=
					timeWidth * (stepCount - stepIndex) * adblATMSwapRateOffsetRealization[stepIndex];
			}
		}

		return swapPortfolioValueRealizationArray;
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

		double atmSwapRateOffsetDrift = 0.;
		double atmSwapRateOffsetStart = 0.;
		double atmSwapRateOffsetVolatility = 0.25;

		double overnightNumeraireInitial = 1.;
		double overnightNumeraireDrift = 0.004;
		double overnightNumeraireVolatility = 0.02;

		double csaDrift = 0.01;
		double csaInitial = 1.;
		double csaVolatility = 0.05;

		double bankHazardRateDrift = 0.002;
		double bankHazardRateInitial = 0.015;
		double bankHazardRateVolatility = 0.2;

		double bankRecoveryRateDrift = 0.002;
		double bankRecoveryRateInitial = 0.4;
		double bankRecoveryRateVolatility = 0.02;

		double counterPartyHazardRateDrift = 0.002;
		double counterPartyHazardRateInitial = 0.03;
		double counterPartyHazardRateVolatility = 0.3;

		double counterPartyRecoveryRateDrift = 0.002;
		double counterPartyRecoveryRateInitial = 0.3;
		double counterPartyRecoveryRateVolatility = 0.02;

		double bankFundingSpreadDrift = 0.00002;
		double bankFundingSpreadVolatility = 0.002;

		double counterPartyFundingSpreadDrift = 0.000022;
		double counterPartyFundingSpreadVolatility = 0.0022;

		double[][] correlationMatrix = new double[][] {
			{1.00,  0.00,  0.03,  0.07,  0.04,  0.05,  0.08,  0.00,  0.00},  // PORTFOLIO
			{0.00,  1.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  1.00},  // OVERNIGHT
			{0.03,  0.00,  1.00,  0.26,  0.33,  0.21,  0.35,  0.13,  0.00},  // CSA
			{0.07,  0.00,  0.26,  1.00,  0.45, -0.17,  0.07,  0.77,  0.00},  // BANK HAZARD
			{0.04,  0.00,  0.33,  0.45,  1.00, -0.22, -0.54,  0.58,  0.00},  // COUNTER PARTY HAZARD
			{0.05,  0.00,  0.21, -0.17, -0.22,  1.00,  0.47, -0.23,  0.00},  // BANK RECOVERY
			{0.08,  0.00,  0.35,  0.07, -0.54,  0.47,  1.00,  0.01,  0.00},  // COUNTER PARTY RECOVERY
			{0.00,  0.00,  0.13,  0.77,  0.58, -0.23,  0.01,  1.00,  0.00},  // BANK FUNDING SPREAD
			{0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  1.00}   // COUNTER PARTY FUNDING SPREAD
		};

		JulianDate spotDate = DateUtil.Today();

		PositionGroupSpecification positionGroupSpecification = PositionGroupSpecification.FixedThreshold (
			"FIXEDTHRESHOLD",
			0.,
			0.,
			PositionReplicationScheme.ALBANESE_ANDERSEN_VERTEX,
			BrokenDateScheme.SQUARE_ROOT_OF_TIME,
			0.,
			CloseOutScheme.ISDA_92
		);

		double timeWidth = time / stepCount;
		JulianDate[] vertexDateArray = new JulianDate[stepCount + 1];
		double[][] portfolioValueGrid = new double[pathCount][stepCount + 1];
		double bankFundingSpreadInitial = bankHazardRateInitial / (1. - bankRecoveryRateInitial);
		MonoPathExposureAdjustment[] monoPathExposureAdjustmentArray =
			new MonoPathExposureAdjustment[pathCount];
		double counterPartyFundingSpreadInitial =
			counterPartyHazardRateInitial / (1. - counterPartyRecoveryRateInitial);

		DiffusionEvolver atmSwapRateOffsetDiffusionEvolver = new DiffusionEvolver (
			DiffusionEvaluatorLinear.Standard (atmSwapRateOffsetDrift, atmSwapRateOffsetVolatility)
		);

		DiffusionEvolver overnightNumeraireDiffusionEvolver = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (overnightNumeraireDrift, overnightNumeraireVolatility)
		);

		DiffusionEvolver csaDiffusionEvolver = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (csaDrift, csaVolatility)
		);

		DiffusionEvolver bankHazardRateDiffusionEvolver = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (bankHazardRateDrift, bankHazardRateVolatility)
		);

		DiffusionEvolver counterPartyHazardRateDiffusionEvolver = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				counterPartyHazardRateDrift,
				counterPartyHazardRateVolatility
			)
		);

		DiffusionEvolver bankRecoveryRateDiffusionEvolver = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (bankRecoveryRateDrift, bankRecoveryRateVolatility)
		);

		DiffusionEvolver counterPartyRecoveryRateDiffusionEvolver = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				counterPartyRecoveryRateDrift,
				counterPartyRecoveryRateVolatility
			)
		);

		DiffusionEvolver bankFundingSpreadDiffusionEvolver = new DiffusionEvolver (
			DiffusionEvaluatorLinear.Standard (bankFundingSpreadDrift, bankFundingSpreadVolatility)
		);

		DiffusionEvolver counterPartyFundingSpreadDiffusionEvolver = new DiffusionEvolver (
			DiffusionEvaluatorLinear.Standard (
				counterPartyFundingSpreadDrift,
				counterPartyFundingSpreadVolatility
			)
		);

		for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex) {
			double[][] numeraireGrid = R1MatrixUtil.Transpose (
				RandomSequenceGenerator.GaussianJoint (stepCount, correlationMatrix)
			);

			portfolioValueGrid[pathIndex] = SwapPortfolioValueRealization (
				atmSwapRateOffsetDiffusionEvolver,
				atmSwapRateOffsetStart,
				numeraireGrid[0],
				stepCount,
				time,
				timeWidth,
				swapCount
			);

			double[] overnightNumeraireArray = VertexNumeraireRealization (
				overnightNumeraireDiffusionEvolver,
				overnightNumeraireInitial,
				time,
				timeWidth,
				numeraireGrid[1],
				stepCount
			);

			double[] csaArray = VertexNumeraireRealization (
				csaDiffusionEvolver,
				csaInitial,
				time,
				timeWidth,
				numeraireGrid[2],
				stepCount
			);

			double[] bankHazardRateArray = NumeraireValueRealization (
				bankHazardRateDiffusionEvolver,
				bankHazardRateInitial,
				time,
				timeWidth,
				numeraireGrid[3],
				stepCount
			);

			double[] counterPartyHazardRateArray = NumeraireValueRealization (
				counterPartyHazardRateDiffusionEvolver,
				counterPartyHazardRateInitial,
				time,
				timeWidth,
				numeraireGrid[4],
				stepCount
			);

			double[] bankRecoveryRateArray = NumeraireValueRealization (
				bankRecoveryRateDiffusionEvolver,
				bankRecoveryRateInitial,
				time,
				timeWidth,
				numeraireGrid[5],
				stepCount
			);

			double[] counterPartyRecoveryRateArray = NumeraireValueRealization (
				counterPartyRecoveryRateDiffusionEvolver,
				counterPartyRecoveryRateInitial,
				time,
				timeWidth,
				numeraireGrid[6],
				stepCount
			);

			double[] bankFundingSpreadArray = NumeraireValueRealization (
				bankFundingSpreadDiffusionEvolver,
				bankFundingSpreadInitial,
				time,
				timeWidth,
				numeraireGrid[7],
				stepCount
			);

			double[] counterPartyFundingSpreadArray = NumeraireValueRealization (
				counterPartyFundingSpreadDiffusionEvolver,
				counterPartyFundingSpreadInitial,
				time,
				timeWidth,
				numeraireGrid[8],
				stepCount
			);

			JulianDate startDate = spotDate;
			double valueStart = time * atmSwapRateOffsetStart;
			MarketVertex[] marketVertexArray = new MarketVertex [stepCount + 1];
			AlbaneseAndersen[] albaneseAndersenArray = new AlbaneseAndersen[stepCount + 1];

			for (int stepIndex = 0; stepIndex <= stepCount; ++stepIndex) {
				LatentStateVertexContainer latentStateVertexContainer = new LatentStateVertexContainer();

				latentStateVertexContainer.add (OTCFixFloatLabel.Standard ("USD-3M-10Y"), Double.NaN);

				marketVertexArray[stepIndex] = MarketVertex.Nodal (
					vertexDateArray[stepIndex] = spotDate.addMonths (6 * stepIndex),
					overnightNumeraireDrift,
					overnightNumeraireArray[stepIndex],
					csaDrift,
					csaArray[stepIndex],
					new MarketVertexEntity (
						Math.exp (-0.5 * bankHazardRateArray[stepIndex] * (stepCount - stepIndex)),
						bankHazardRateArray[stepIndex],
						bankRecoveryRateArray[stepIndex],
						bankFundingSpreadArray[stepIndex],
						Math.exp (
							-0.5 * bankHazardRateArray[stepIndex] * (1. - bankRecoveryRateArray[stepIndex]) *
								(stepCount - stepIndex)
						),
						Double.NaN,
						Double.NaN,
						Double.NaN
					),
					new MarketVertexEntity (
						Math.exp (-0.5 * counterPartyHazardRateArray[stepIndex] * stepIndex),
						counterPartyHazardRateArray[stepIndex],
						counterPartyRecoveryRateArray[stepIndex],
						counterPartyFundingSpreadArray[stepIndex],
						Math.exp (
							-0.5 * counterPartyHazardRateArray[stepIndex] *
								(1. - counterPartyRecoveryRateArray[stepIndex]) * (stepCount - stepIndex)
						),
						Double.NaN,
						Double.NaN,
						Double.NaN
					),
					latentStateVertexContainer
				);

				JulianDate endDate = vertexDateArray[stepIndex];
				double valueEnd = portfolioValueGrid[pathIndex][stepIndex];

				albaneseAndersenArray[stepIndex] = new AlbaneseAndersen (
					vertexDateArray[stepIndex],
					portfolioValueGrid[pathIndex][stepIndex],
					0.,
					0 == stepIndex ? 0. : new CollateralAmountEstimator (
						positionGroupSpecification,
						new BrokenDateInterpolatorLinearT (
							startDate.julian(),
							endDate.julian(),
							valueStart,
							valueEnd
						),
						Double.NaN
					).postingRequirement (
						endDate
					)
				);

				startDate = endDate;
				valueStart = valueEnd;
			}

			MarketPath marketPath = MarketPath.FromMarketVertexArray (marketVertexArray);

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
