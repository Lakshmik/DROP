
package org.drip.sample.xvabasel;

import org.drip.analytics.date.*;
import org.drip.exposure.evolver.LatentStateVertexContainer;
import org.drip.exposure.universe.*;
import org.drip.measure.crng.RandomSequenceGenerator;
import org.drip.measure.dynamics.*;
import org.drip.measure.realization.*;
import org.drip.measure.statistics.UnivariateDiscreteThin;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.state.identifier.OTCFixFloatLabel;
import org.drip.xva.basel.*;
import org.drip.xva.gross.*;
import org.drip.xva.netting.CollateralGroupPath;
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
 * <i>UncollateralizedNettingReceivable</i> examines the Basel BCBS 2012 OTC Accounting Impact to a Portfolio
 * 	of 10 Swaps resulting from the Addition of a New Swap - Comparison via both FVA/FDA and FCA/FBA Schemes.
 *  Simulation is carried out under the following Criteria:
 *  
 *    - Collateralization Status - Uncollateralized
 *    - Aggregation Unit         - Netting Group
 *    - Added Swap Type          - Negative Upfront Swap (Receivable)
 *    - Market Dynamics          - Deterministic (Static Market Evolution)
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
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/xvabasel/README.md">Basel XVA Accounting Metrics Scheme</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class UncollateralizedNettingReceivable
{

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

		JumpDiffusionEdge[] jumpDiffusionEdgeArray = atmSwapRateOffsetDiffusionEvolver.incrementSequence (
			new JumpDiffusionVertex (time, initialATMSwapRateOffset, 0., false),
			JumpDiffusionEdgeUnit.Diffusion (timeWidthArray, randomArray),
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
		final double[] randomArray,
		final int stepCount,
		final double time,
		final double timeWidth,
		final double timeToMaturity,
		final double swapNotional)
		throws Exception
	{
		double[] swapPortfolioValueRealizationArray = new double[stepCount + 1];
		int maturityStepSize = (int) (timeToMaturity / timeWidth);

		for (int stepIndex = 0; stepIndex < stepCount; ++stepIndex) {
			swapPortfolioValueRealizationArray[stepIndex] = 0.;
		}

		double[] atmSwapRateOffsetRealizationArray = ATMSwapRateOffsetRealization (
			atmSwapRateDiffusionEvolver,
			initialATMSwapRate,
			randomArray,
			time,
			timeWidth,
			stepCount
		);

		for (int stepIndex = 0; stepIndex <= stepCount; ++stepIndex) {
			swapPortfolioValueRealizationArray[stepIndex] = stepIndex > maturityStepSize ? 0. :
				swapNotional * timeWidth * (maturityStepSize - stepIndex) *
				atmSwapRateOffsetRealizationArray[stepIndex];
		}

		return swapPortfolioValueRealizationArray;
	}

	private static final ExposureAdjustmentAggregator[] Mix (
		final double timeToMaturity1,
		final double initialATMSwapRateOffset1,
		final double swapNotional1,
		final double timeToMaturity2,
		final double initialATMSwapRateOffset2,
		final double swapNotional2)
		throws Exception
	{
		int stepCount = 10;
		int vertexCount = 10;
		int pathCount = 100000;

		double time = 5.;

		double atmSwapRateOffsetDrift = 0.;
		double atmSwapRateOffsetVolatility = 0.25;

		double overnightNumeraireDrift = 0.004;
		double csaDrift = 0.01;

		double bankHazardRate = 0.015;
		double bankRecoveryRate = 0.4;

		double counterPartyHazardRate = 0.03;
		double counterPartyRecoveryRate = 0.3;

		JulianDate spotDate = DateUtil.Today();

		double timeWidth = time / stepCount;
		JulianDate[] vertexDateArray = new JulianDate[stepCount + 1];
		MarketVertex[] marketVertexArray = new MarketVertex[stepCount + 1];
		double bankFundingSpread = bankHazardRate / (1. - bankRecoveryRate);
		double[][] portfolio1ValueGrid = new double[pathCount][stepCount + 1];
		double[][] portfolio2ValueGrid = new double[pathCount][stepCount + 1];
		double counterPartyFundingSpread = counterPartyHazardRate / (1. - counterPartyRecoveryRate);
		MonoPathExposureAdjustment[] groundMonoPathExposureAdjustmentArray =
			new MonoPathExposureAdjustment[pathCount];
		MonoPathExposureAdjustment[] extendedMonoPathExposureAdjustmentArray =
			new MonoPathExposureAdjustment[pathCount];

		DiffusionEvolver atmSwapRateOffsetDiffusionEvolver = new DiffusionEvolver (
			DiffusionEvaluatorLinear.Standard (atmSwapRateOffsetDrift, atmSwapRateOffsetVolatility)
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

		for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex) {
			portfolio1ValueGrid[pathIndex] = SwapPortfolioValueRealization (
				atmSwapRateOffsetDiffusionEvolver,
				initialATMSwapRateOffset1,
				RandomSequenceGenerator.Gaussian (stepCount),
				vertexCount,
				time,
				timeWidth,
				timeToMaturity1,
				swapNotional1
			);

			portfolio2ValueGrid[pathIndex] = SwapPortfolioValueRealization (
				atmSwapRateOffsetDiffusionEvolver,
				initialATMSwapRateOffset2,
				RandomSequenceGenerator.Gaussian (stepCount),
				vertexCount,
				time,
				timeWidth,
				timeToMaturity2,
				swapNotional2
			);
			AlbaneseAndersen[] albaneseAndersen1Array = new AlbaneseAndersen[stepCount + 1];
			AlbaneseAndersen[] albaneseAndersen2Array = new AlbaneseAndersen[stepCount + 1];

			for (int stepIndex = 0; stepIndex <= stepCount; ++stepIndex) {
				albaneseAndersen1Array[stepIndex] = new AlbaneseAndersen (
					vertexDateArray[stepIndex],
					portfolio1ValueGrid[pathIndex][stepIndex],
					0.,
					0.
				);

				albaneseAndersen2Array[stepIndex] = new AlbaneseAndersen (
					vertexDateArray[stepIndex],
					portfolio2ValueGrid[pathIndex][stepIndex],
					0.,
					0.
				);
			}

			MarketPath marketPath = MarketPath.FromMarketVertexArray (marketVertexArray);

			CollateralGroupPath[] collateralGroupPath1Array = new CollateralGroupPath[] {
				new CollateralGroupPath (albaneseAndersen1Array, marketPath)
			};

			CollateralGroupPath[] collateralGroupPath2Array = new CollateralGroupPath[] {
				new CollateralGroupPath (albaneseAndersen2Array, marketPath)
			};

			groundMonoPathExposureAdjustmentArray[pathIndex] = new MonoPathExposureAdjustment (
				new AlbaneseAndersenFundingGroupPath[] {
					new AlbaneseAndersenFundingGroupPath (
						new AlbaneseAndersenNettingGroupPath[] {
							new AlbaneseAndersenNettingGroupPath (collateralGroupPath1Array, marketPath)
						},
						marketPath
					)
				}
			);

			extendedMonoPathExposureAdjustmentArray[pathIndex] = new MonoPathExposureAdjustment (
				new AlbaneseAndersenFundingGroupPath[] {
					AlbaneseAndersenFundingGroupPath.Mono (
						new AlbaneseAndersenNettingGroupPath (collateralGroupPath1Array, marketPath),
						marketPath
					),
					AlbaneseAndersenFundingGroupPath.Mono (
						new AlbaneseAndersenNettingGroupPath (collateralGroupPath2Array, marketPath),
						marketPath
					)
				}
			);
		}

		return new ExposureAdjustmentAggregator[] {
			new ExposureAdjustmentAggregator (groundMonoPathExposureAdjustmentArray),
			new ExposureAdjustmentAggregator (extendedMonoPathExposureAdjustmentArray)
		};
	}

	private static final void CPGDDump (
		final String header,
		final ExposureAdjustmentDigest exposureAdjustmentDigest)
		throws Exception
	{
		System.out.println();

		UnivariateDiscreteThin cvaUnivariateDiscreteThin = exposureAdjustmentDigest.cva();

		UnivariateDiscreteThin dvaUnivariateDiscreteThin = exposureAdjustmentDigest.dva();

		UnivariateDiscreteThin fbaUnivariateDiscreteThin = exposureAdjustmentDigest.fba();

		UnivariateDiscreteThin fcaUnivariateDiscreteThin = exposureAdjustmentDigest.fca();

		UnivariateDiscreteThin fdaUnivariateDiscreteThin = exposureAdjustmentDigest.fda();

		UnivariateDiscreteThin fvaUnivariateDiscreteThin = exposureAdjustmentDigest.fva();

		UnivariateDiscreteThin sfvaUnivariateDiscreteThin = exposureAdjustmentDigest.sfva();

		UnivariateDiscreteThin ucvaUnivariateDiscreteThin = exposureAdjustmentDigest.ucva();

		UnivariateDiscreteThin cvaclUnivariateDiscreteThin = exposureAdjustmentDigest.cvacl();

		UnivariateDiscreteThin ftdcvaUnivariateDiscreteThin = exposureAdjustmentDigest.ftdcva();

		UnivariateDiscreteThin ucolvaUnivariateDiscreteThin = exposureAdjustmentDigest.ucolva();

		UnivariateDiscreteThin ftdcolvaUnivariateDiscreteThin = exposureAdjustmentDigest.ftdcolva();

		System.out.println (
			"\t||-----------------------------------------------------------------------------------------------------------------------------------||"
		);

		System.out.println (header);

		System.out.println (
			"\t||-----------------------------------------------------------------------------------------------------------------------------------||"
		);

		System.out.println (
			"\t||  OODLE  => UCOLVA  | FTDCOLVA |  UCVA   | FTDCVA  |  CVACL  |   CVA   |   DVA   |   FVA   |   FDA   |   FCA   |   FBA   |   SFVA  ||"
		);

		System.out.println (
			"\t||-----------------------------------------------------------------------------------------------------------------------------------||"
		);

		System.out.println (
			"\t|| Average => " +
			FormatUtil.FormatDouble (ucolvaUnivariateDiscreteThin.average(), 2, 2, 1.) + "  |  " +
			FormatUtil.FormatDouble (ftdcolvaUnivariateDiscreteThin.average(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (ucvaUnivariateDiscreteThin.average(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (ftdcvaUnivariateDiscreteThin.average(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (cvaclUnivariateDiscreteThin.average(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (cvaUnivariateDiscreteThin.average(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (dvaUnivariateDiscreteThin.average(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (fvaUnivariateDiscreteThin.average(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (fdaUnivariateDiscreteThin.average(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (fcaUnivariateDiscreteThin.average(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (fbaUnivariateDiscreteThin.average(), 2, 2, 1.) + "  | " + 
			FormatUtil.FormatDouble (sfvaUnivariateDiscreteThin.average(), 2, 2, 1.) + "  ||"
		);

		System.out.println (
			"\t|| Minimum => " +
			FormatUtil.FormatDouble (ucolvaUnivariateDiscreteThin.minimum(), 2, 2, 1.) + "  |  " +
			FormatUtil.FormatDouble (ftdcolvaUnivariateDiscreteThin.minimum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (ucvaUnivariateDiscreteThin.minimum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (ftdcvaUnivariateDiscreteThin.minimum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (cvaclUnivariateDiscreteThin.minimum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (cvaUnivariateDiscreteThin.minimum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (dvaUnivariateDiscreteThin.minimum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (fvaUnivariateDiscreteThin.minimum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (fdaUnivariateDiscreteThin.minimum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (fcaUnivariateDiscreteThin.minimum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (fbaUnivariateDiscreteThin.minimum(), 2, 2, 1.) + "  | " + 
			FormatUtil.FormatDouble (sfvaUnivariateDiscreteThin.minimum(), 2, 2, 1.) + "  ||"
		);

		System.out.println (
			"\t|| Maximum => " +
			FormatUtil.FormatDouble (ucolvaUnivariateDiscreteThin.maximum(), 2, 2, 1.) + "  |  " +
			FormatUtil.FormatDouble (ftdcolvaUnivariateDiscreteThin.maximum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (ucvaUnivariateDiscreteThin.maximum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (ftdcvaUnivariateDiscreteThin.maximum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (cvaclUnivariateDiscreteThin.maximum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (cvaUnivariateDiscreteThin.maximum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (dvaUnivariateDiscreteThin.maximum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (fvaUnivariateDiscreteThin.maximum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (fdaUnivariateDiscreteThin.maximum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (fcaUnivariateDiscreteThin.maximum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (fbaUnivariateDiscreteThin.maximum(), 2, 2, 1.) + "  | " + 
			FormatUtil.FormatDouble (sfvaUnivariateDiscreteThin.maximum(), 2, 2, 1.) + "  ||"
		);

		System.out.println (
			"\t||  Error  => " +
			FormatUtil.FormatDouble (ucolvaUnivariateDiscreteThin.error(), 2, 2, 1.) + "  |  " +
			FormatUtil.FormatDouble (ftdcolvaUnivariateDiscreteThin.error(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (ucvaUnivariateDiscreteThin.error(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (ftdcvaUnivariateDiscreteThin.error(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (cvaclUnivariateDiscreteThin.error(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (cvaUnivariateDiscreteThin.error(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (dvaUnivariateDiscreteThin.error(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (fvaUnivariateDiscreteThin.error(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (fdaUnivariateDiscreteThin.error(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (fcaUnivariateDiscreteThin.error(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (fbaUnivariateDiscreteThin.error(), 2, 2, 1.) + "  | " + 
			FormatUtil.FormatDouble (sfvaUnivariateDiscreteThin.error(), 2, 2, 1.) + "  ||"
		);

		System.out.println (
			"\t||-----------------------------------------------------------------------------------------------------------------------------------||"
		);
	}

	private static final void CPGDDiffDump (
		final String header,
		final ExposureAdjustmentDigest groundExposureAdjustmentDigest,
		final ExposureAdjustmentDigest expandedExposureAdjustmentDigest)
		throws Exception
	{
		System.out.println();

		System.out.println (
			"\t||-----------------------------------------------------------------------------------------------------------------------------------||"
		);

		System.out.println (header);

		System.out.println (
			"\t||-----------------------------------------------------------------------------------------------------------------------------------||"
		);

		System.out.println (
			"\t||  OODLE  => UCOLVA  | FTDCOLVA |  UCVA   | FTDCVA  |  CVACL  |   CVA   |   DVA   |   FVA   |   FDA   |   FCA   |   FBA   |   SFVA  ||"
		);

		System.out.println (
			"\t||-----------------------------------------------------------------------------------------------------------------------------------||"
		);

		System.out.println (
			"\t|| Average => " + FormatUtil.FormatDouble (
				expandedExposureAdjustmentDigest.ucolva().average() -
					groundExposureAdjustmentDigest.ucolva().average(),
				3,
				1,
				10000.
			) + "  |  " + FormatUtil.FormatDouble (
				expandedExposureAdjustmentDigest.ftdcolva().average() -
					groundExposureAdjustmentDigest.ftdcolva().average(),
				3,
				1,
				10000.
			) + "  | " + FormatUtil.FormatDouble (
				expandedExposureAdjustmentDigest.ucva().average() -
					groundExposureAdjustmentDigest.ucva().average(),
				3,
				1,
				10000.
			) + "  | " + FormatUtil.FormatDouble (
				expandedExposureAdjustmentDigest.ftdcva().average() -
					groundExposureAdjustmentDigest.ftdcva().average(),
				3,
				1,
				10000.
			) + "  | " + FormatUtil.FormatDouble (
				expandedExposureAdjustmentDigest.cvacl().average() -
					groundExposureAdjustmentDigest.cvacl().average(),
				3,
				1,
				10000.
			) + "  | " + FormatUtil.FormatDouble (
				expandedExposureAdjustmentDigest.cva().average() -
					groundExposureAdjustmentDigest.cva().average(),
				3,
				1,
				10000.
			) + "  | " + FormatUtil.FormatDouble (
				expandedExposureAdjustmentDigest.dva().average() -
					groundExposureAdjustmentDigest.dva().average(),
				3,
				1,
				10000.
			) + "  | " + FormatUtil.FormatDouble (
				expandedExposureAdjustmentDigest.fva().average() -
					groundExposureAdjustmentDigest.fva().average(),
				3,
				1,
				10000.
			) + "  | " + FormatUtil.FormatDouble (
				expandedExposureAdjustmentDigest.fda().average() -
					groundExposureAdjustmentDigest.fda().average(),
				3,
				1,
				10000.
			) + "  | " + FormatUtil.FormatDouble (
				expandedExposureAdjustmentDigest.fca().average() -
					groundExposureAdjustmentDigest.fca().average(),
				3,
				1,
				10000.
			) + "  | " + FormatUtil.FormatDouble (
				expandedExposureAdjustmentDigest.fba().average() -
					groundExposureAdjustmentDigest.fba().average(),
				3,
				1,
				10000.
			) + "  | " + FormatUtil.FormatDouble (
				expandedExposureAdjustmentDigest.sfva().average() -
					groundExposureAdjustmentDigest.sfva().average(),
				3,
				1,
				10000.
			) + "  ||"
		);

		System.out.println (
			"\t||-----------------------------------------------------------------------------------------------------------------------------------||"
		);
	}

	private static final void BaselAccountingMetrics (
		final String header,
		final ExposureAdjustmentAggregator groundExposureAdjustmentAggregator,
		final ExposureAdjustmentAggregator expandedExposureAdjustmentAggregator)
		throws Exception
	{
		OTCAccountingModus otcAccountingModusFCAFBA =
			new OTCAccountingModusFCAFBA (groundExposureAdjustmentAggregator);

		OTCAccountingModus otcAccountingModusFVAFDA =
			new OTCAccountingModusFVAFDA (groundExposureAdjustmentAggregator);

		OTCAccountingPolicy fcafbaOTCAccountingPolicy =
			otcAccountingModusFCAFBA.feePolicy (expandedExposureAdjustmentAggregator);

		OTCAccountingPolicy fvafdaOTCAccountingPolicy =
			otcAccountingModusFVAFDA.feePolicy (expandedExposureAdjustmentAggregator);

		System.out.println();

		System.out.println ("\t||---------------------------------------------------------------------||");

		System.out.println (header);

		System.out.println ("\t||---------------------------------------------------------------------||");

		System.out.println ("\t|| L -> R:                                                             ||");

		System.out.println ("\t||         - Accounting Type (FCA/FBA vs. FVA/FDA)                     ||");

		System.out.println ("\t||         - Contra Asset Adjustment                                   ||");

		System.out.println ("\t||         - Contra Liability Adjustment                               ||");

		System.out.println ("\t||         - FTP (Funding Transfer Pricing) (bp)                       ||");

		System.out.println ("\t||         - CET1 (Common Equity Tier I) Change (bp)                   ||");

		System.out.println ("\t||         - CL (Contra Liability) Change (bp)                         ||");

		System.out.println ("\t||         - PFV (Porfolio Value) Change (Income) (bp)                 ||");

		System.out.println ("\t||---------------------------------------------------------------------||");

		System.out.println (
			"\t|| FCA/FBA Accounting => " + FormatUtil.FormatDouble (
				otcAccountingModusFCAFBA.contraAssetAdjustment(),
				1,
				4,
				1.
			) + " | " + FormatUtil.FormatDouble (
				otcAccountingModusFCAFBA.contraLiabilityAdjustment(),
				1,
				4,
				1.
			) + " | " + FormatUtil.FormatDouble (
				fcafbaOTCAccountingPolicy.fundingTransferPricing(),
				3,
				0,
				10000.
			) + " | " + FormatUtil.FormatDouble (
				fcafbaOTCAccountingPolicy.cet1Change(),
				3,
				0,
				10000.
			) + " | " + FormatUtil.FormatDouble (
				fcafbaOTCAccountingPolicy.contraLiabilityChange(),
				3,
				0,
				10000.
			) + " | " + FormatUtil.FormatDouble (
				fcafbaOTCAccountingPolicy.portfolioValueChange(),
				3,
				0,
				10000.
			) + " || "
		);

		System.out.println (
			"\t|| FVA/FDA Accounting => " + FormatUtil.FormatDouble (
				otcAccountingModusFVAFDA.contraAssetAdjustment(),
				1,
				4,
				1.
			) + " | " + FormatUtil.FormatDouble (
				otcAccountingModusFVAFDA.contraLiabilityAdjustment(),
				1,
				4,
				1.
			) + " | " + FormatUtil.FormatDouble (
				fvafdaOTCAccountingPolicy.fundingTransferPricing(),
				3,
				0,
				10000.
			) + " | " + FormatUtil.FormatDouble (
				fvafdaOTCAccountingPolicy.cet1Change(),
				3,
				0,
				10000.
			) + " | " + FormatUtil.FormatDouble (
				fvafdaOTCAccountingPolicy.contraLiabilityChange(),
				3,
				0,
				10000.
			) + " | " + FormatUtil.FormatDouble (
				fvafdaOTCAccountingPolicy.portfolioValueChange(),
				3,
				0,
				10000.
			) + " || "
		);

		System.out.println ("\t||---------------------------------------------------------------------||");

		System.out.println();
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

		ExposureAdjustmentAggregator[] exposureAdjustmentAggregatorArray = Mix (5., 0., 100., 5., -0.05, 1.);

		ExposureAdjustmentAggregator extendedExposureAdjustmentAggregator =
			exposureAdjustmentAggregatorArray[1];
		ExposureAdjustmentAggregator groundExposureAdjustmentAggregator =
			exposureAdjustmentAggregatorArray[0];

		ExposureAdjustmentDigest groundExposureAdjustmentDigest =
			groundExposureAdjustmentAggregator.digest();

		ExposureAdjustmentDigest extendedExposureAdjustmentDigest =
			extendedExposureAdjustmentAggregator.digest();

		CPGDDump (
			"\t||                                                  GROUND BOOK ADJUSTMENT METRICS                                                   ||",
			groundExposureAdjustmentDigest
		);

		CPGDDump (
			"\t||                                                 EXTENDED BOOK ADJUSTMENT METRICS                                                  ||",
			extendedExposureAdjustmentDigest
		);

		CPGDDiffDump (
			"\t||                                             TRADE INCREMENT ADJUSTMENT METRICS (bp)                                               ||",
			groundExposureAdjustmentDigest,
			extendedExposureAdjustmentDigest
		);

		BaselAccountingMetrics (
			"\t||           ALBANESE & ANDERSEN (2015) BCBS OTC ACCOUNTING            ||",
			groundExposureAdjustmentAggregator,
			extendedExposureAdjustmentAggregator
		);

		EnvManager.TerminateEnv();
	}
}
