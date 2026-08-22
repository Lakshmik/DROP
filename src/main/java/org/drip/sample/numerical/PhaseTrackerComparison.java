
package org.drip.sample.numerical;

import java.util.Map;

import org.drip.numerical.fourier.PhaseAdjuster;
import org.drip.param.pricer.HestonOptionPricerParams;
import org.drip.pricer.option.HestonStochasticVolatilityAlgorithm;
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
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
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
 * <i>PhaseTrackerComparison</i> demonstrates the Log + Power Complex Number Phase Correction Functionality
 * 	implemented by three different ways for the calculation of the Inverse Fourier Transforms. The sample
 * 	problem chosen is the stochastic volatility evolution using the Heston Method.
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/numerical/README.md">Search, Quadratures, Fourier Phase Tracker</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class PhaseTrackerComparison
{

	private static final Map<Double, Double> PhaseSet (
		final double rho,
		final double kappa,
		final double sigma,
		final double theta,
		final double lambda,
		final double strike,
		final double timeToExpiry,
		final double riskFreeRate,
		final double spot,
		final double spotVolatility,
		final int phaseTrackerType)
		throws Exception
	{
		return new HestonStochasticVolatilityAlgorithm (
			new HestonOptionPricerParams (
				HestonStochasticVolatilityAlgorithm.PAYOFF_TRANSFORM_SCHEME_HESTON_1993,
				rho,
				kappa,
				sigma,
				theta,
				lambda,
				phaseTrackerType
			)
		).recordPhase (
			strike,
			timeToExpiry,
			riskFreeRate,
			spot,
			spotVolatility,
			true
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

		double rho = 0.3;
		double kappa = 1.;
		double lambda = 0.;
		double sigma = 0.5;
		double theta = 0.2;

		double spot = 1.;
		double strike = 1.;
		double riskFreeRate = 0.;
		double timeToExpiry = 0.5;
		double spotVolatility = 0.1;

		Map<Double, Double> frequencyPhaseNoAdjustmentMap = PhaseSet (
			rho,
			kappa,
			sigma,
			theta,
			lambda,
			strike,
			timeToExpiry,
			riskFreeRate,
			spot,
			spotVolatility,
			PhaseAdjuster.MULTI_VALUE_BRANCH_PHASE_TRACKER_NONE
		);

		Map<Double, Double> frequencyPhaseRotationCountMap = PhaseSet (
			rho,
			kappa,
			sigma,
			theta,
			lambda,
			strike,
			timeToExpiry,
			riskFreeRate,
			spot,
			spotVolatility,
			PhaseAdjuster.MULTI_VALUE_BRANCH_PHASE_TRACKER_ROTATION_COUNT
		);

		Map<Double, Double> frequencyPhaseKahlJackelMap = PhaseSet (
			rho,
			kappa,
			sigma,
			theta,
			lambda,
			strike,
			timeToExpiry,
			riskFreeRate,
			spot,
			spotVolatility,
			PhaseAdjuster.MULTI_VALUE_BRANCH_POWER_PHASE_TRACKER_KAHL_JACKEL
		);

		System.out.println ("\t|--------------------------------------------|");

		System.out.println ("\t|  u =>  NO CORECT | ROT COUNT | KAHL JACKEL |");

		System.out.println ("\t|--------------------------------------------|");

		for (Map.Entry<Double, Double> mapEntry : frequencyPhaseKahlJackelMap.entrySet()) {
			Double key = mapEntry.getKey();

			System.out.println (
				"\t|" + FormatUtil.FormatDouble (key, 2, 0, 1.) + " =>  " +
				FormatUtil.FormatDouble (frequencyPhaseNoAdjustmentMap.get (key), 1, 6, 1.)  + " | " +
				FormatUtil.FormatDouble (frequencyPhaseRotationCountMap.get (key), 1, 6, 1.)  + " | " +
				FormatUtil.FormatDouble (frequencyPhaseKahlJackelMap.get (key), 1, 6, 1.) + "   |"
			);
		}

		System.out.println ("\t|--------------------------------------------|");

		EnvManager.TerminateEnv();
	}
}
