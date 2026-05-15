
package org.drip.dynamics.sabr;

import org.drip.function.definition.R1ToR1;
import org.drip.numerical.common.NumberUtil;

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
 * <i>CFunction</i> exposes the Variants of the SABR C Function. The References are:
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

public abstract class CFunction
	extends R1ToR1
{

	/**
	 * Empty <i>CFunction</i> Constructor
	 */

	public CFunction()
	{
		super (null);
	}

	/**
	 * C Value for the given Shifted Forward
	 * 
	 * @param shiftedForward Shifted Forward
	 * 
	 * @return C Value
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public abstract double c (
		final double shiftedForward)
		throws Exception;

	/**
	 * Evaluate C for the given Shifted Forward
	 * 
	 * @param shiftedForward Shifted Forward
	 * 
	 * @return C Value
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	@Override public double evaluate (
		final double shiftedForward)
		throws Exception
	{
		return c (shiftedForward);
	}

	/**
	 * Compute the Reciprocal Integral between the Initial Shifted Forward and the Shifted Strike
	 * 
	 * @param initialShiftedForward Initial Shifted Forward
	 * @param shiftedStrike Shifted Strike
	 * 
	 * @return Reciprocal Integral between the Initial Shifted Forward and the Shifted Strike
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public double reciprocalIntegral (
		final double initialShiftedForward,
		final double shiftedStrike)
		throws Exception
	{
		if (!NumberUtil.IsValid (initialShiftedForward) || !NumberUtil.IsValid (shiftedStrike)) {
			throw new Exception ("CFunction::reciprocalIntegral => Invalid Inputs");
		}

		if (initialShiftedForward == shiftedStrike) {
			return 0.;
		}

		boolean initialForwardAboveStrike = initialShiftedForward > shiftedStrike;

		return (initialForwardAboveStrike ? 1. : -1.) * new R1ToR1 (null) {
			@Override public double evaluate (
				double shiftedForward)
				throws Exception
			{
				return 1. / c (shiftedForward);
			}
		}.integrate (
			initialForwardAboveStrike ? shiftedStrike : initialShiftedForward,
			initialForwardAboveStrike ? initialShiftedForward : shiftedStrike
		);
	}

	/**
	 * Evaluate Gamma1 for the Shifted Forward Mid
	 * 
	 * @param shiftedForwardMid Shifted Forward Mid
	 * 
	 * @return Gamma1
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public double gamma1 (
		final double shiftedForwardMid)
		throws Exception
	{
		if (!NumberUtil.IsValid (shiftedForwardMid)) {
			throw new Exception ("CFunction::gamma1 => Invalid Input");
		}

		return derivative (shiftedForwardMid, 1) / c (shiftedForwardMid);
	}

	/**
	 * Evaluate Gamma2 for the Shifted Forward Mid
	 * 
	 * @param shiftedForwardMid Shifted Forward Mid
	 * 
	 * @return Gamma2
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public double gamma2 (
		final double shiftedForwardMid)
		throws Exception
	{
		if (!NumberUtil.IsValid (shiftedForwardMid)) {
			throw new Exception ("CFunction::gamma2 => Invalid Input");
		}

		return derivative (shiftedForwardMid, 2) / c (shiftedForwardMid);
	}
}
