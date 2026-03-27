
package org.drip.sample.burgard2013;

import org.drip.analytics.date.*;
import org.drip.exposure.evolver.LatentStateVertexContainer;
import org.drip.exposure.mpor.CollateralAmountEstimator;
import org.drip.exposure.universe.*;
import org.drip.measure.bridge.BrokenDateInterpolatorLinearT;
import org.drip.measure.crng.CorrelatedFactorsPathVertexRealization;
import org.drip.measure.crng.RandomNumberGenerator;
import org.drip.measure.dynamics.*;
import org.drip.measure.realization.*;
import org.drip.measure.statistics.UnivariateDiscreteThin;
import org.drip.numerical.linearalgebra.R1MatrixUtil;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.state.identifier.OTCFixFloatLabel;
import org.drip.xva.basel.*;
import org.drip.xva.definition.*;
import org.drip.xva.gross.*;
import org.drip.xva.hypothecation.*;
import org.drip.xva.netting.CollateralGroupPath;
import org.drip.xva.proto.*;
import org.drip.xva.settings.*;
import org.drip.xva.strategy.*;
import org.drip.xva.vertex.BurgardKjaerBuilder;

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
 * <i>PerfectReplicationCollateralizedFundingStochastic</i> examines the Basel BCBS 2012 OTC Accounting
 * 	Impact to a Portfolio of 10 Swaps resulting from the Addition of a New Swap - Comparison via both FVA/FDA
 * 	and FCA/FBA Schemes. Simulation is carried out under the following Criteria using one of the Generalized
 * 	Burgard Kjaer (2013) Scheme.
 *  
 * <br><br>
 *  <ul>
 *  	<li>
 *    		Collateralization Status - Collateralized
 *  	</li>
 *  	<li>
 *    		Aggregation Unit         - Funding Group
 *  	</li>
 *  	<li>
 *    		Added Swap Type          - Zero Upfront Par Swap (Neutral)
 *  	</li>
 *  	<li>
 *    		Market Dynamics          - Stochastic (Dynamic Market Evolution)
 *  	</li>
 *  	<li>
 *    		Funding Strategy         - Perfect Replication
 *  	</li>
 *  </ul>
 *  
 * The References are:
 *  
 * <br><br>
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
 * <br><br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/sample/burgard2013/README.md">Burgard Kjaer (2013) Valuation Adjustments</a></td></tr>
 *  </table>
 * <br><br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class PerfectReplicationCollateralizedFundingStochastic
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
		final double initialNumeraireValue,
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
				new JumpDiffusionVertex (time, initialNumeraireValue, 0., false),
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
		final double atmSwapRateStart,
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
			atmSwapRateStart,
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

	private static final double[][] Path (
		final double[][] correlationMatrix,
		final int vertexCount)
		throws Exception
	{
		return new CorrelatedFactorsPathVertexRealization (
			new RandomNumberGenerator(),
			correlationMatrix,
			vertexCount,
			1,
			false,
			null
		).multiTrajectoryNodeRd()[0].flatform();
	}

	private static final ExposureAdjustmentAggregator[] Mix (
		final double timeMaturity1,
		final double atmSwapRateOffsetStart1,
		final double swapNotional1,
		final double timeMaturity2,
		final double atmSwapRateOffsetStart2,
		final double swapNotional2)
		throws Exception
	{
		int stepCount = 10;
		int pathCount = 60000;
		int vertexCount = 10;

		double time = 5.;

		double atmSwapRateOffsetDrift = 0.;
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

		double bankSeniorRecoveryRateDrift = 0.002;
		double bankSeniorRecoveryRateInitial = 0.4;
		double bankSeniorRecoveryRateVolatility = 0.02;

		double bankSubordinateRecoveryRateDrift = 0.001;
		double bankSubordinateRecoveryRateInitial = 0.15;
		double bankSubordinateRecoveryRateVolatility = 0.01;

		double counterPartyHazardRateDrift = 0.002;
		double counterPartyHazardRateInitial = 0.03;
		double counterPartyHazardRateVolatility = 0.3;

		double counterPartyRecoveryRateDrift = 0.002;
		double counterPartyRecoveryRateInitial = 0.3;
		double counterPartyRecoveryRateVolatility = 0.02;

		double bankSeniorFundingSpreadDrift = 0.00002;
		double bankSeniorFundingSpreadVolatility = 0.002;

		double bankSubordinateFundingSpreadDrift = 0.00001;
		double bankSubordinateFundingSpreadVolatility = 0.001;

		double counterPartyFundingSpreadDrift = 0.000022;
		double counterPartyFundingSpreadVolatility = 0.0022;

		double bankThreshold = -0.1;
		double counterPartyThreshold = 0.1;

		double[][] correlationMatrix = new double[][] {
			{1.00,  0.00,  0.03,  0.07,  0.04,  0.05,  0.00,  0.08,  0.00,  0.00,  0.00},  // PORTFOLIO
			{0.00,  1.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00},  // OVERNIGHT
			{0.03,  0.00,  1.00,  0.26,  0.33,  0.21,  0.00,  0.35,  0.13,  0.00,  0.00},  // CSA
			{0.07,  0.00,  0.26,  1.00,  0.45, -0.17,  0.00,  0.07,  0.77,  0.00,  0.00},  // BANK HAZARD
			{0.04,  0.00,  0.33,  0.45,  1.00, -0.22,  0.00, -0.54,  0.58,  0.00,  0.00},  // COUNTER PARTY HAZARD
			{0.05,  0.00,  0.21, -0.17, -0.22,  1.00,  0.00,  0.47, -0.23,  0.00,  0.00},  // BANK SENIOR RECOVERY
			{0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  1.00,  0.00,  0.00,  0.00,  0.00},  // BANK SUBORDINATE RECOVERY
			{0.08,  0.00,  0.35,  0.07, -0.54,  0.47,  0.00,  1.00,  0.01,  0.00,  0.00},  // COUNTER PARTY RECOVERY
			{0.00,  0.00,  0.13,  0.77,  0.58, -0.23,  0.00,  0.01,  1.00,  0.00,  0.00},  // BANK SENIOR FUNDING SPREAD
			{0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  1.00,  0.00},  // BANK SUBORDINATE FUNDING SPREAD
			{0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  1.00}   // COUNTER PARTY FUNDING SPREAD
		};

		PositionGroupSpecification positionGroupSpecification = PositionGroupSpecification.FixedThreshold (
			"FIXEDTHRESHOLD",
			counterPartyThreshold,
			bankThreshold,
			PositionReplicationScheme.BURGARD_KJAER_HEDGE_ERROR_DUAL_BOND_VERTEX,
			BrokenDateScheme.LINEAR_TIME,
			0.,
			CloseOutScheme.BILATERAL
		);

		JulianDate spotDate = DateUtil.Today();

		double timeWidth = time / stepCount;
		JulianDate[] vertexDateArray = new JulianDate[stepCount + 1];
		double[][] portfolio1ValueGrid = new double[pathCount][stepCount + 1];
		double[][] portfolio2ValueGrid = new double[pathCount][stepCount + 1];
		MonoPathExposureAdjustment[] groundMonoPathExposureAdjustmentArray =
			new MonoPathExposureAdjustment[pathCount];
		MonoPathExposureAdjustment[] extendedMonoPathExposureAdjustmentArray =
			new MonoPathExposureAdjustment[pathCount];
		double bankSeniorFundingSpreadInitial = bankHazardRateInitial / (1. - bankSeniorRecoveryRateInitial);
		double bankSubordinateFundingSpreadInitial =
			bankHazardRateInitial / (1. - bankSubordinateRecoveryRateInitial);
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

		DiffusionEvolver bankSeniorRecoveryRateDiffusionEvolver = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				bankSeniorRecoveryRateDrift,
				bankSeniorRecoveryRateVolatility
			)
		);

		DiffusionEvolver bankSubordinateRecoveryRateDiffusionEvolver = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				bankSubordinateRecoveryRateDrift,
				bankSubordinateRecoveryRateVolatility
			)
		);

		DiffusionEvolver counterPartyHazardRateDiffusionEvolver = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				counterPartyHazardRateDrift,
				counterPartyHazardRateVolatility
			)
		);

		DiffusionEvolver counterPartyRecoveryRateDiffusionEvolver = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				counterPartyRecoveryRateDrift,
				counterPartyRecoveryRateVolatility
			)
		);

		DiffusionEvolver bankSeniorFundingSpreadDiffusionEvolver = new DiffusionEvolver (
			DiffusionEvaluatorLinear.Standard (
				bankSeniorFundingSpreadDrift,
				bankSeniorFundingSpreadVolatility
			)
		);

		DiffusionEvolver bankSubordinateFundingSpreadDiffusionEvolver = new DiffusionEvolver (
			DiffusionEvaluatorLinear.Standard (
				bankSubordinateFundingSpreadDrift,
				bankSubordinateFundingSpreadVolatility
			)
		);

		DiffusionEvolver counterPartyFundingSpreadDiffusionEvolver = new DiffusionEvolver (
			DiffusionEvaluatorLinear.Standard (
				counterPartyFundingSpreadDrift,
				counterPartyFundingSpreadVolatility
			)
		);

		for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex) {
			double[][] numeraireGrid = R1MatrixUtil.Transpose (Path (correlationMatrix, vertexCount));

			portfolio1ValueGrid[pathIndex] = SwapPortfolioValueRealization (
				atmSwapRateOffsetDiffusionEvolver,
				atmSwapRateOffsetStart1,
				numeraireGrid[0],
				vertexCount,
				time,
				timeWidth,
				timeMaturity1,
				swapNotional1
			);

			portfolio2ValueGrid[pathIndex] = SwapPortfolioValueRealization (
				atmSwapRateOffsetDiffusionEvolver,
				atmSwapRateOffsetStart2,
				numeraireGrid[0],
				vertexCount,
				time,
				timeWidth,
				timeMaturity2,
				swapNotional2
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

			double[] bankSeniorRecoveryRateArray = NumeraireValueRealization (
				bankSeniorRecoveryRateDiffusionEvolver,
				bankSeniorRecoveryRateInitial,
				time,
				timeWidth,
				numeraireGrid[5],
				stepCount
			);

			double[] bankSubordinateRecoveryRateArray = NumeraireValueRealization (
				bankSubordinateRecoveryRateDiffusionEvolver,
				bankSubordinateRecoveryRateInitial,
				time,
				timeWidth,
				numeraireGrid[6],
				stepCount
			);

			double[] counterPartyRecoveryRateArray = NumeraireValueRealization (
				counterPartyRecoveryRateDiffusionEvolver,
				counterPartyRecoveryRateInitial,
				time,
				timeWidth,
				numeraireGrid[7],
				stepCount
			);

			double[] bankSeniorFundingSpreadArray = NumeraireValueRealization (
				bankSeniorFundingSpreadDiffusionEvolver,
				bankSeniorFundingSpreadInitial,
				time,
				timeWidth,
				numeraireGrid[8],
				stepCount
			);

			double[] bankSubordinateFundingSpreadArray = NumeraireValueRealization (
				bankSubordinateFundingSpreadDiffusionEvolver,
				bankSubordinateFundingSpreadInitial,
				time,
				timeWidth,
				numeraireGrid[9],
				stepCount
			);

			double[] counterPartyFundingSpreadArray = NumeraireValueRealization (
				counterPartyFundingSpreadDiffusionEvolver,
				counterPartyFundingSpreadInitial,
				time,
				timeWidth,
				numeraireGrid[10],
				stepCount
			);

			JulianDate startDate = spotDate;
			double valueStart1 = time * atmSwapRateOffsetStart1;
			double valueStart2 = time * atmSwapRateOffsetStart2;
			MarketVertex[] marketVertexArray = new MarketVertex [stepCount + 1];
			CollateralGroupVertex[] collateralGroupVertex1Array = new CollateralGroupVertex[stepCount + 1];
			CollateralGroupVertex[] collateralGroupVertex2Array = new CollateralGroupVertex[stepCount + 1];

			for (int stepIndex = 0; stepIndex <= stepCount; ++stepIndex) {
				JulianDate endDate = (vertexDateArray[stepIndex] = spotDate.addMonths (6 * stepIndex + 6));

				double collateralBalance1 = 0.;
				double collateralBalance2 = 0.;
				double dblValueEnd1 = portfolio1ValueGrid[pathIndex][stepIndex];
				double dblValueEnd2 = portfolio2ValueGrid[pathIndex][stepIndex];

				CloseOut closeOut = new CloseOutBilateral (
					bankSeniorRecoveryRateArray[stepIndex],
					counterPartyRecoveryRateArray[stepIndex]
				);

				LatentStateVertexContainer latentStateVertexContainer = new LatentStateVertexContainer();

				latentStateVertexContainer.add (OTCFixFloatLabel.Standard ("USD-3M-10Y"), Double.NaN);

				marketVertexArray[stepIndex] = MarketVertex.Nodal (
					vertexDateArray[stepIndex] = spotDate.addMonths (6 * stepIndex),
					overnightNumeraireDrift,
					overnightNumeraireArray[stepIndex],
					csaDrift,
					csaArray[stepIndex],
					new MarketVertexEntity (
						Math.exp (-0.5 * bankHazardRateArray[stepIndex] * stepIndex),
						bankHazardRateArray[stepIndex],
						bankSeniorRecoveryRateArray[stepIndex],
						bankSeniorFundingSpreadArray[stepIndex],
						Math.exp (-0.5 * bankHazardRateArray[stepIndex] *
							(1. - bankSeniorRecoveryRateArray[stepIndex]) * stepCount),
						bankSubordinateRecoveryRateArray[stepIndex],
						bankSubordinateFundingSpreadArray[stepIndex],
						Math.exp (-0.5 * bankHazardRateArray[stepIndex] *
							(1. - counterPartyRecoveryRateArray[stepIndex]) * stepCount)
					),
					new MarketVertexEntity (
						Math.exp (-0.5 * counterPartyHazardRateArray[stepIndex] * stepIndex),
						counterPartyHazardRateArray[stepIndex],
						counterPartyRecoveryRateArray[stepIndex],
						counterPartyFundingSpreadArray[stepIndex],
						Math.exp (-0.5 * counterPartyHazardRateArray[stepIndex] *
							(1. - counterPartyRecoveryRateArray[stepIndex]) * stepCount),
						Double.NaN,
						Double.NaN,
						Double.NaN
					),
					latentStateVertexContainer
				);

				if (0 != stepIndex) {
					collateralBalance1 = new CollateralAmountEstimator (
						positionGroupSpecification,
						new BrokenDateInterpolatorLinearT (
							startDate.julian(),
							endDate.julian(),
							valueStart1,
							dblValueEnd1
						),
						Double.NaN
					).postingRequirement (
						endDate
					);

					collateralBalance2 = new CollateralAmountEstimator (
						positionGroupSpecification,
						new BrokenDateInterpolatorLinearT (
							startDate.julian(),
							endDate.julian(),
							valueStart2,
							dblValueEnd2
						),
						Double.NaN
					).postingRequirement (
						endDate
					);

					collateralGroupVertex1Array[stepIndex] = BurgardKjaerBuilder.HedgeErrorDualBond (
						vertexDateArray[stepIndex],
						portfolio1ValueGrid[pathIndex][stepIndex],
						0.,
						collateralBalance1,
						0.,
						new MarketEdge (marketVertexArray[stepIndex - 1], marketVertexArray[stepIndex]),
						closeOut
					);

					collateralGroupVertex2Array[stepIndex] = BurgardKjaerBuilder.HedgeErrorDualBond (
						vertexDateArray[stepIndex],
						portfolio2ValueGrid[pathIndex][stepIndex],
						0.,
						collateralBalance2,
						0.,
						new MarketEdge (marketVertexArray[stepIndex - 1], marketVertexArray[stepIndex]),
						closeOut
					);
				} else {
					collateralGroupVertex1Array[stepIndex] = BurgardKjaerBuilder.Initial (
						vertexDateArray[stepIndex],
						portfolio1ValueGrid[pathIndex][0],
						marketVertexArray[stepIndex],
						closeOut
					);

					collateralGroupVertex2Array[stepIndex] = BurgardKjaerBuilder.Initial (
						vertexDateArray[stepIndex],
						portfolio2ValueGrid[pathIndex][0],
						marketVertexArray[stepIndex],
						closeOut
					);
				}
			}

			MarketPath marketPath = MarketPath.FromMarketVertexArray (marketVertexArray);

			CollateralGroupPath[] collateralGroupPathArray = new CollateralGroupPath[] {
				new CollateralGroupPath (collateralGroupVertex1Array, marketPath)
			};

			groundMonoPathExposureAdjustmentArray[pathIndex] = new MonoPathExposureAdjustment (
				new AlbaneseAndersenFundingGroupPath[] {
					new AlbaneseAndersenFundingGroupPath (
						new AlbaneseAndersenNettingGroupPath[] {
							new AlbaneseAndersenNettingGroupPath (collateralGroupPathArray, marketPath)
						},
						marketPath
					)
				}
			);

			CollateralGroupPath[] extendedCollateralGroupPathArray = new CollateralGroupPath[] {
				new CollateralGroupPath (collateralGroupVertex1Array, marketPath),
				new CollateralGroupPath (collateralGroupVertex2Array, marketPath)
			};

			extendedMonoPathExposureAdjustmentArray[pathIndex] = new MonoPathExposureAdjustment (
				new AlbaneseAndersenFundingGroupPath[] {
					new AlbaneseAndersenFundingGroupPath (
						new AlbaneseAndersenNettingGroupPath[] {
							new AlbaneseAndersenNettingGroupPath (
								extendedCollateralGroupPathArray,
								marketPath
							)
						},
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

		OTCAccountingPolicy fvafbaOTCAccountingPolicy =
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
				fvafbaOTCAccountingPolicy.fundingTransferPricing(),
				3,
				0,
				10000.
			) + " | " + FormatUtil.FormatDouble (
				fvafbaOTCAccountingPolicy.cet1Change(),
				3,
				0,
				10000.
			) + " | " + FormatUtil.FormatDouble (
				fvafbaOTCAccountingPolicy.contraLiabilityChange(),
				3,
				0,
				10000.
			) + " | " + FormatUtil.FormatDouble (
				fvafbaOTCAccountingPolicy.portfolioValueChange(),
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

		ExposureAdjustmentAggregator[] exposureAdjustmentAggregatorArray = Mix (5., 0., 100., 5., 0., 1.);

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
