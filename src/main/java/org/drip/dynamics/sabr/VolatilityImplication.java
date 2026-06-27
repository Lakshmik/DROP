
package org.drip.dynamics.sabr;

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
 * <i>VolatilityImplication</i> maintains the Results of Volatility Implication Run. The References are:
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
 *  It provides the following Functions:
 *
 *  <ul>
 * 		<li><i>VolatilityImplication</i> Constructor</li>
 * 		<li>Retrieve Gamma<sub>2</sub></li>
 * 		<li>Retrieve Gamma<sub>1</sub></li>
 * 		<li>Retrieve Zeta</li>
 * 		<li>Retrieve D(zeta)</li>
 * 		<li>Retrieve Forward Mid</li>
 * 		<li>Retrieve C(Forward Mid)</li>
 * 		<li>Retrieve Epsilon</li>
 * 		<li>Retrieve the Implied Volatility</li>
 *	</ul>
 *
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/dynamics/README.md">HJM, Hull White, LMM, and SABR Dynamic Evolution Models</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/dynamics/sabr/README.md">SABR Based Latent State Evolution</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class VolatilityImplication
{
	private double _zeta = Double.NaN;
	private double _gamma1 = Double.NaN;
	private double _gamma2 = Double.NaN;
	private double _dOfZeta = Double.NaN;
	private double _epsilon = Double.NaN;
	private double _implied = Double.NaN;
	private double _forwardMid = Double.NaN;
	private double _cOfForwardMid = Double.NaN;

	/**
	 * <i>VolatilityImplication</i> Constructor
	 * 
	 * @param zeta Zeta
	 * @param dOfZeta D(zeta)
	 * @param gamma2 Gamma<sub>2</sub>
	 * @param gamma1 Gamma<sub>1</sub>
	 * @param forwardMid Forward Mid
	 * @param cOfForwardMid C(Forward Mid)
	 * @param epsilon Epsilon
	 * @param implied Implied Volatility
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public VolatilityImplication (
		final double zeta,
		final double dOfZeta,
		final double gamma2,
		final double gamma1,
		final double forwardMid,
		final double cOfForwardMid,
		final double epsilon,
		final double implied)
		throws Exception
	{
		if (!NumberUtil.IsValid (_zeta = zeta) ||
			!NumberUtil.IsValid (_dOfZeta = dOfZeta) ||
			!NumberUtil.IsValid (_gamma2 = gamma2) ||
			!NumberUtil.IsValid (_gamma1 = gamma1) ||
			!NumberUtil.IsValid (_forwardMid = forwardMid) ||
			!NumberUtil.IsValid (_cOfForwardMid = cOfForwardMid) ||
			!NumberUtil.IsValid (_epsilon = epsilon) ||
			!NumberUtil.IsValid (_implied = implied))
		{
			throw new Exception ("VolatilityImplication Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve Gamma<sub>2</sub>
	 * 
	 * @return Gamma<sub>2</sub>
	 */

	public double gamma2()
	{
		return _gamma2;
	}

	/**
	 * Retrieve Gamma<sub>1</sub>
	 * 
	 * @return Gamma<sub>1</sub>
	 */

	public double gamma1()
	{
		return _gamma1;
	}

	/**
	 * Retrieve Zeta
	 * 
	 * @return Zeta
	 */

	public double zeta()
	{
		return _zeta;
	}

	/**
	 * Retrieve D(zeta)
	 * 
	 * @return D(zeta)
	 */

	public double dOfZeta()
	{
		return _dOfZeta;
	}

	/**
	 * Retrieve Forward Mid
	 * 
	 * @return Forward Mid
	 */

	public double forwardMid()
	{
		return _forwardMid;
	}

	/**
	 * Retrieve C(Forward Mid)
	 * 
	 * @return C(Forward Mid)
	 */

	public double cOfForwardMid()
	{
		return _cOfForwardMid;
	}

	/**
	 * Retrieve Epsilon
	 * 
	 * @return Epsilon
	 */

	public double epsilon()
	{
		return _epsilon;
	}

	/**
	 * Retrieve the Implied Volatility
	 * 
	 * @return Implied Volatility
	 */

	public double implied()
	{
		return _implied;
	}
}
