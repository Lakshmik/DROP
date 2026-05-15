
package org.drip.sample.sabr;

import org.drip.analytics.date.*;
import org.drip.dynamics.sabr.*;
import org.drip.sequence.random.BoxMullerGaussian;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.state.identifier.ForwardLabel;

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
 * <i>ClassicalForwardRateEvolution</i> demonstrates the Construction and Usage of the Classical SABR Model
 *  Dynamics for the Evolution of Forward Rate.
 *
 * <br><br>
 *  <ul>
 *		<li><b>Module </b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ProductCore.md">Product Core Module</a></li>
 *		<li><b>Library</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></li>
 *		<li><b>Project</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></li>
 *		<li><b>Package</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/sample/preferred/README.md">SABR Forward Evolution Black Volatility</a></li>
 *  </ul>
 * <br><br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class ClassicalForwardRateEvolution
{

	private static StochasticVolatilityStateEvolver SABREvolver (
		final double beta,
		final double rho,
		final double volatilityOfVolatility)
		throws Exception
	{
		return new StochasticVolatilityStateEvolver (
			ForwardLabel.Create ("USD", "6M"),
			new ForwardProcessSetting (rho, volatilityOfVolatility, 0., new CFunctionClassical (beta)),
			new BoxMullerGaussian (0., 1.),
			new BoxMullerGaussian (0., 1.)
		);
	}

	private static void SABREvolution (
		final StochasticVolatilityStateEvolver stochasticVolatilityStateEvolver1,
		final StochasticVolatilityStateEvolver stochasticVolatilityStateEvolver2,
		final StochasticVolatilityStateEvolver stochasticVolatilityStateEvolver3,
		final int spotDate,
		final int terminalDate,
		final ForwardUpdate initialForwardUpdate1,
		final ForwardUpdate initialForwardUpdate2,
		final ForwardUpdate initialForwardUpdate3)
		throws Exception
	{
		int dayStep = 2;
		int currentDate = spotDate;
		ForwardUpdate forwardUpdate1 = initialForwardUpdate1;
		ForwardUpdate forwardUpdate2 = initialForwardUpdate2;
		ForwardUpdate forwardUpdate3 = initialForwardUpdate3;

		System.out.println (
			"\n\t||----------------------------------------------------------------------------------||"
		);

		System.out.println (
			"\t||     SABR  EVOLUTION  DYNAMICS                                                    ||"
		);

		System.out.println (
			"\t||----------------------------------------------------------------------------------||"
		);

		System.out.println (
			"\t||    L -> R:                                                                       ||"
		);

		System.out.println (
			"\t||        Forward Rate (%)  - Gaussian (beta = 0.0)                                 ||"
		);

		System.out.println (
			"\t||        Forward Rate Vol (%)  - Gaussian (beta = 0.0)                             ||"
		);

		System.out.println (
			"\t||        Forward Rate (%)  - beta = 0.5                                            ||"
		);

		System.out.println (
			"\t||        Forward Rate Vol (%)  - beta = 0.5                                        ||"
		);

		System.out.println (
			"\t||        Forward Rate (%)  - Lognormal (beta = 1.0)                                ||"
		);

		System.out.println (
			"\t||        Forward Rate Vol (%)  - Lognormal (beta = 1.0)                            ||"
		);

		System.out.println (
			"\t||----------------------------------------------------------------------------------||"
		);

		while (currentDate < terminalDate) {
			forwardUpdate1 = (ForwardUpdate) stochasticVolatilityStateEvolver1.evolve (
				spotDate,
				currentDate,
				dayStep,
				forwardUpdate1
			);

			forwardUpdate2 = (ForwardUpdate) stochasticVolatilityStateEvolver2.evolve (
				spotDate,
				currentDate,
				dayStep,
				forwardUpdate2
			);

			forwardUpdate3 = (ForwardUpdate) stochasticVolatilityStateEvolver3.evolve (
				spotDate,
				currentDate,
				dayStep,
				forwardUpdate3
			);

			System.out.println (
				"\t|| " + new JulianDate (currentDate) + " => " +
				FormatUtil.FormatDouble (forwardUpdate1.forward(), 1, 4, 100.) + " % | " +
				FormatUtil.FormatDouble (forwardUpdate1.forwardVolatility(), 1, 2, 100.) + " % || " +
				FormatUtil.FormatDouble (forwardUpdate2.forward(), 1, 4, 100.) + " % | " +
				FormatUtil.FormatDouble (forwardUpdate2.forwardVolatility(), 1, 1, 100.) + " % || " +
				FormatUtil.FormatDouble (forwardUpdate3.forward(), 1, 4, 100.) + " % | " +
				FormatUtil.FormatDouble (forwardUpdate3.forwardVolatility(), 1, 1, 100.) + " % ||"
			);

			currentDate += dayStep;
		}

		System.out.println (
			"\t||----------------------------------------------------------------------------------||"
		);
	}

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
		EnvManager.InitEnv ("");

		JulianDate spotDate = DateUtil.Today();

		double rho = 0.1;
		double alpha = 0.59;
		double forward = 0.04;
		String viewTenor = "3M";
		double[] betaArray = {
			0.0,
			0.5,
			1.0
		};
		double[] forwardVolatilityArray = {
			0.03,
			0.26,
			0.51
		};

		int viewDate = spotDate.addTenor (viewTenor).julian();

		StochasticVolatilityStateEvolver stochasticVolatilityStateEvolver1 = SABREvolver (
			betaArray[0],
			rho,
			alpha
		);

		StochasticVolatilityStateEvolver stochasticVolatilityStateEvolver2 = SABREvolver (
			betaArray[1],
			rho,
			alpha
		);

		StochasticVolatilityStateEvolver stochasticVolatilityStateEvolver3 = SABREvolver (
			betaArray[2],
			rho,
			alpha
		);

		int spotDateInteger = spotDate.julian();

		ForwardLabel forwardLabel = ForwardLabel.Create ("USD", "6M");

		ForwardUpdate forwardUpdate1 = ForwardUpdate.Create (
			forwardLabel,
			spotDateInteger,
			spotDateInteger,
			viewDate,
			forward,
			0.,
			forwardVolatilityArray[0],
			0.
		);

		ForwardUpdate forwardUpdate2 = ForwardUpdate.Create (
			forwardLabel,
			spotDateInteger,
			spotDateInteger,
			viewDate,
			forward,
			0.,
			forwardVolatilityArray[1],
			0.
		);

		ForwardUpdate forwardUpdate3 = ForwardUpdate.Create (
			forwardLabel,
			spotDateInteger,
			spotDateInteger,
			viewDate,
			forward,
			0.,
			forwardVolatilityArray[2],
			0.
		);

		SABREvolution (
			stochasticVolatilityStateEvolver1,
			stochasticVolatilityStateEvolver2,
			stochasticVolatilityStateEvolver3,
			spotDateInteger,
			viewDate,
			forwardUpdate1,
			forwardUpdate2,
			forwardUpdate3
		);

		EnvManager.TerminateEnv();
	}
}
