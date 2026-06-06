
package org.drip.sample.xva;

import org.drip.analytics.date.*;
import org.drip.exposure.mpor.CollateralAmountEstimator;
import org.drip.exposure.mpor.CollateralAmountEstimatorOutput;
import org.drip.measure.bridge.BrokenDateInterpolatorLinearT;
import org.drip.measure.crng.RandomSequenceGenerator;
import org.drip.measure.dynamics.DiffusionEvaluatorLinear;
import org.drip.measure.realization.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.xva.proto.*;
import org.drip.xva.settings.*;

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
 * <i>PortfolioCollateralEstimate</i> illustrates the Estimation of the Collateral Amount on a Single Trade
 * 	Collateral Portfolio. The References are:
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

public class PortfolioCollateralEstimate
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

		int stepCount = 40;

		double time = 10.;
		double portfolioDrift = 0.;
		double portfolioValueStart = 0.;
		double portfolioVolatility = 0.15;

		double bankThreshold = -0.1;
		double counterPartyThreshold = 0.1;

		JulianDate spotDate = DateUtil.Today();

		JulianDate startDate = spotDate;
		double timeWidth =  time / stepCount;
		double[] timeWidthArray = new double[stepCount];

		for (int stepIndex = 0; stepIndex < stepCount; ++stepIndex) {
			timeWidthArray[stepIndex] = timeWidth;
		}

		PositionGroupSpecification positionGroupSpecification = PositionGroupSpecification.FixedThreshold (
			"FIXEDTHRESHOLD",
			counterPartyThreshold,
			bankThreshold,
			PositionReplicationScheme.ALBANESE_ANDERSEN_VERTEX,
			BrokenDateScheme.SQUARE_ROOT_OF_TIME,
			0.,
			CloseOutScheme.ISDA_92
		);

		JumpDiffusionEdge[] jumpDiffusionEdgeArray = new DiffusionEvolver (
			DiffusionEvaluatorLinear.Standard (portfolioDrift, portfolioVolatility)
		).incrementSequence (
			new JumpDiffusionVertex (time, portfolioValueStart, 0., false),
			JumpDiffusionEdgeUnit.Diffusion (timeWidthArray, RandomSequenceGenerator.Gaussian (stepCount)),
			timeWidth
		);

		System.out.println();

		System.out.println (
			"\t||--------------------------------------------------------------------------------------------------------------------------||"
		);

		System.out.println (
			"\t||                                       COLLATERAL AMOUNT ESTIMATION OUTPUT METRICS                                        ||"
		);

		System.out.println (
			"\t||--------------------------------------------------------------------------------------------------------------------------||"
		);

		System.out.println (
			"\t||    L -> R:                                                                                                               ||"
		);

		System.out.println (
			"\t||            - Forward Date                                                                                                ||"
		);

		System.out.println (
			"\t||            - Forward Value                                                                                               ||"
		);

		System.out.println (
			"\t||            - Bank Margin Date                                                                                            ||"
		);

		System.out.println (
			"\t||            - Counter Party Margin Date                                                                                   ||"
		);

		System.out.println (
			"\t||            - Bank Window Margin Value                                                                                    ||"
		);

		System.out.println (
			"\t||            - Counter Party Window Margin Value                                                                           ||"
		);

		System.out.println (
			"\t||            - Bank Collateral Threshold                                                                                   ||"
		);

		System.out.println (
			"\t||            - Counter Party Collateral Threshold                                                                          ||"
		);

		System.out.println (
			"\t||            - Bank Posting Requirement                                                                                    ||"
		);

		System.out.println (
			"\t||            - Counter Party Posting Requirement                                                                           ||"
		);

		System.out.println (
			"\t||            - Gross Posting Requirement                                                                                   ||"
		);

		System.out.println (
			"\t||--------------------------------------------------------------------------------------------------------------------------||"
		);

		for (int stepIndex = 0; stepIndex < stepCount; ++stepIndex) {
			JulianDate endDate = startDate.addMonths (3);

			double portfolioValueFinish =
				timeWidth * (stepCount - stepIndex) * jumpDiffusionEdgeArray[stepIndex].finish();

			CollateralAmountEstimatorOutput collateralAmountEstimatorOutput = new CollateralAmountEstimator (
				positionGroupSpecification,
				new BrokenDateInterpolatorLinearT (
					startDate.julian(),
					endDate.julian(),
					portfolioValueStart,
					portfolioValueFinish
				),
				Double.NaN
			).output (
				endDate
			);

			System.out.println (
				"\t|| " +
				endDate + " => " +
				FormatUtil.FormatDouble (portfolioValueFinish, 1, 4, 1.) + " | " +
				collateralAmountEstimatorOutput.dealerMarginDate() + " | " +
				collateralAmountEstimatorOutput.clientMarginDate() + " | " +
				FormatUtil.FormatDouble (
					collateralAmountEstimatorOutput.dealerWindowMarginValue(),
					1,
					4,
					1.
				) + " | " + FormatUtil.FormatDouble (
					collateralAmountEstimatorOutput.clientWindowMarginValue(),
					1,
					4,
					1.
				) + " | " + FormatUtil.FormatDouble (
					collateralAmountEstimatorOutput.dealerCollateralThreshold(),
					1,
					4,
					1.
				) + " | " + FormatUtil.FormatDouble (
					collateralAmountEstimatorOutput.clientCollateralThreshold(),
					1,
					4,
					1.
				) + " | " + FormatUtil.FormatDouble (
					collateralAmountEstimatorOutput.dealerPostingRequirement(),
					1,
					4,
					1.
				) + " | " + FormatUtil.FormatDouble (
					collateralAmountEstimatorOutput.clientPostingRequirement(),
					1,
					4,
					1.
				) + " | " + FormatUtil.FormatDouble (
					collateralAmountEstimatorOutput.postingRequirement(),
					1,
					4,
					1.
				) + " ||"
			);

			startDate = endDate;
			portfolioValueStart = portfolioValueFinish;
		}

		System.out.println (
			"\t||--------------------------------------------------------------------------------------------------------------------------||"
		);

		System.out.println();

		EnvManager.TerminateEnv();
	}
}
