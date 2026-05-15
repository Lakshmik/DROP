
package org.drip.sample.sabr;

import org.drip.dynamics.sabr.CFunctionClassical;
import org.drip.dynamics.sabr.EuropeanOptionSetting;
import org.drip.dynamics.sabr.ForwardProcessSetting;
import org.drip.dynamics.sabr.HaganKumarLesniewskiWoodward2002;
import org.drip.dynamics.sabr.StartingStateRealization;
import org.drip.dynamics.sabr.VolatilityImplication;
import org.drip.service.common.FormatUtil;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2026 Lakshmi Krishnamurthy
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
 * <i>HaganKumarLesniewskiWoodward2002ArithmeticLognormal</i> executes a Log-normal Volatility Implication
 *  Run using the Hagan, Kumar, Lesniewski, and Woodward (2002) using Arithmetic Averaging. The References
 *  are:
 *  
 * <br><br>
 *  <ul>
 *  	<li>
 *  		Choi, J., and L. Wu (2021): The Equivalent Constant Elasticity-of-Variance (CEV) Volatility of
 *  			the Stochastic Alpha-Beta-Rho (SABR) Model <i>Journal of Economic Dynamics and Control</i>
 *  			<b>128</b> 104143
 *  	</li>
 *  	<li>
 *  		Grzelak, L. A., and C. W. Oosterlee (2016): From Arbitrage to Arbitrage-free Implied Volatilities
 *  			<i>Journal of Computational Finance</i> <b>20 (3)</b> 31-49
 *  	</li>
 *  	<li>
 *  		Guerrero, J., and G. Orlando (2021): Stochastic Local Volatility Models and the Wei-Normal
 *  			Factorization Method <i>Discrete and Continuous Dynamical Systems – S</i> <b>15 (12)</b>
 *  			3699-3722
 *  	</li>
 *  	<li>
 *  		Hagan, P. S., D. Kumar, A. S. Lesniewski, and D. E. Woodward (2002): Managing Smile Risk
 *  			<i>Wilmott</i> <b>1</b> 84-108
 *  	</li>
 *  	<li>
 *  		Wikipedia (2026): SABR Volatility Model https://en.wikipedia.org/wiki/SABR_volatility_model
 *  	</li>
 *  </ul>
 *
 *  <ul>
 *		<li><b>Module </b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ProductCore.md">Product Core Module</a></li>
 *		<li><b>Library</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></li>
 *		<li><b>Project</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/dynamics/README.md">HJM, Hull White, LMM, and SABR Dynamic Evolution Models</a></li>
 *		<li><b>Package</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/dynamics/sabr/README.md">SABR Based Latent State Evolution</a></li>
 *  </ul>
 *
 * @author Lakshmi Krishnamurthy
 */

public class HaganKumarLesniewskiWoodward2002ArithmeticLognormal
{

	public static final void main (
		final String[] argumentArray)
		throws Exception
	{
		double alpha = 1.;
		double shift = 0.;
		double rho = -0.10;
		double beta = 0.25;
		double timeToExpiry = 1.;
		double initialForward = 1.;
		boolean arithmeticMid = true;
		boolean normalImplication = false;
		double initialForwardVolatility = 0.35;

		double[] strikeArray = {
			0.10,
			0.20,
			0.30,
			0.40,
			0.50,
			0.60,
			0.70,
			0.80,
			0.90,
			0.999999999992,
			1.000000000008,
			1.10,
			1.20,
			1.30,
			1.40,
			1.50,
			1.60,
			1.70,
			1.80,
			1.90,
			2.00,
			2.10,
			2.20,
			2.30,
			2.40,
			2.50,
			2.60,
			2.70,
			2.80,
			2.90,
			3.00
		};

		StartingStateRealization startingStateRealization = new StartingStateRealization (
			initialForward,
			initialForwardVolatility
		);

		ForwardProcessSetting forwardProcessParameters = new ForwardProcessSetting (
			alpha,
			rho,
			shift,
			new CFunctionClassical (beta)
		);

		System.out.println (
			"\n\t|-------------------------------------------------------------------------------------------|"
		);

		System.out.println (
			"\t|                              SABR IMPLIED BLACK VOLATILITY                                |"
		);

		System.out.println (
			"\t|-------------------------------------------------------------------------------------------|"
		);

		System.out.println (
			"\t|  - Inputs:                                                                                |"
		);

		System.out.println ("\t|      - SABR Alpha  => " + FormatUtil.FormatDouble (alpha, 1, 2, 1.));

		System.out.println ("\t|      - SABR Beta   => " + FormatUtil.FormatDouble (beta, 1, 2, 1.));

		System.out.println ("\t|      - SABR Rho    => " + FormatUtil.FormatDouble (rho, 1, 2, 1.));

		System.out.println ("\t|      - SABR Shift  => " + FormatUtil.FormatDouble (shift, 1, 2, 1.));

		System.out.println ("\t|      - SABR Rho    => " + FormatUtil.FormatDouble (rho, 1, 2, 1.));

		System.out.println (
			"\t|      - SABR f0     => " + FormatUtil.FormatDouble (initialForward, 1, 2, 1.)
		);

		System.out.println (
			"\t|      - SABR sigma0 => " + FormatUtil.FormatDouble (initialForwardVolatility, 1, 2, 1.)
		);

		System.out.println (
			"\t|-------------------------------------------------------------------------------------------|"
		);

		System.out.println (
			"\t|  - Outputs L -> R:                                                                        |"
		);

		System.out.println (
			"\t|      - Strike                                                                             |"
		);

		System.out.println (
			"\t|      - Gamma2                                                                             |"
		);

		System.out.println (
			"\t|      - Gamma1                                                                             |"
		);

		System.out.println (
			"\t|      - Zeta                                                                               |"
		);

		System.out.println (
			"\t|      - D (Zeta)                                                                           |"
		);

		System.out.println (
			"\t|      - Forward Mid                                                                        |"
		);

		System.out.println (
			"\t|      - C (Forward Mid)                                                                    |"
		);

		System.out.println (
			"\t|      - Epsilon                                                                            |"
		);

		System.out.println (
			"\t|      - Implied Volatility                                                                 |"
		);

		System.out.println (
			"\t|-------------------------------------------------------------------------------------------|"
		);

		for (double strike : strikeArray) {
			HaganKumarLesniewskiWoodward2002 haganKumarLesniewskiWoodward2002 =
				new HaganKumarLesniewskiWoodward2002 (
					forwardProcessParameters,
					new EuropeanOptionSetting (strike, timeToExpiry),
					arithmeticMid,
					normalImplication
				);

			VolatilityImplication volatilityImplication =
				haganKumarLesniewskiWoodward2002.imply (startingStateRealization);

			System.out.println (
				"\t|" + FormatUtil.FormatDouble (strike, 1, 12, 1.) + " => " +
					FormatUtil.FormatDouble (volatilityImplication.gamma2(), 1, 4, 1.) + " |" +
					FormatUtil.FormatDouble (volatilityImplication.gamma1(), 1, 4, 1.) + " | " +
					FormatUtil.FormatDouble (volatilityImplication.zeta(), 1, 4, 1.) + " | " +
					FormatUtil.FormatDouble (volatilityImplication.dOfZeta(), 1, 4, 1.) + " |" +
					FormatUtil.FormatDouble (volatilityImplication.forwardMid(), 1, 4, 1.) + " |" +
					FormatUtil.FormatDouble (volatilityImplication.cOfForwardMid(), 1, 4, 1.) + " |" +
					FormatUtil.FormatDouble (volatilityImplication.epsilon(), 1, 4, 1.) + " |" +
					FormatUtil.FormatDouble (volatilityImplication.implied(), 2, 1, 100.) + "% |"
			);
		}

		System.out.println (
			"\t|-------------------------------------------------------------------------------------------|"
		);
	}
}
