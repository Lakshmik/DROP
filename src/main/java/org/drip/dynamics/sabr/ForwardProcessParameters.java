
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
 * <i>ForwardProcessParameters</i> contains the Settings that determine the SABR Dynamics. The References
 * 	are:
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

public class ForwardProcessParameters
{
	private double _rho = Double.NaN;
	private double _beta = Double.NaN;
	private double _alpha = Double.NaN;
	private double _shift = Double.NaN;

	/**
	 * Construct a CEV Instance of <i>ForwardProcessParameters</i>
	 * 
	 * @param beta SABR Beta
	 * 
	 * @return CEV Instance of <i>ForwardProcessParameters</i>
	 */

	public static final ForwardProcessParameters CEV (
		final double beta)
	{
		try {
			return new ForwardProcessParameters (0., beta, 0., 0.);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct a Normal Instance of <i>ForwardProcessParameters</i>
	 * 
	 * @param alpha SABR Alpha
	 * @param rho SABR Rho
	 * 
	 * @return Normal Instance of <i>ForwardProcessParameters</i>
	 */

	public static final ForwardProcessParameters Normal (
		final double alpha,
		final double rho)
	{
		try {
			return new ForwardProcessParameters (alpha, 0., rho, 0.);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct a Shifted Normal Instance of <i>ForwardProcessParameters</i>
	 * 
	 * @param alpha SABR Alpha
	 * @param rho SABR Rho
	 * @param shift SABR Shift
	 * 
	 * @return Shifted Normal Instance of <i>ForwardProcessParameters</i>
	 */

	public static final ForwardProcessParameters ShiftedNormal (
		final double alpha,
		final double rho,
		final double shift)
	{
		try {
			return new ForwardProcessParameters (alpha, 0., rho, shift);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct a Log-normal Instance of <i>ForwardProcessParameters</i>
	 * 
	 * @param alpha SABR Alpha
	 * @param rho SABR Rho
	 * 
	 * @return Log-normal Instance of <i>ForwardProcessParameters</i>
	 */

	public static final ForwardProcessParameters Lognormal (
		final double alpha,
		final double rho)
	{
		try {
			return new ForwardProcessParameters (alpha, 1., rho, 0.);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct a Shifted Log-normal Instance of <i>ForwardProcessParameters</i>
	 * 
	 * @param alpha SABR Alpha
	 * @param rho SABR Rho
	 * @param shift SABR Shift
	 * 
	 * @return Shifted Log-normal Instance of <i>ForwardProcessParameters</i>
	 */

	public static final ForwardProcessParameters ShiftedLognormal (
		final double alpha,
		final double rho,
		final double shift)
	{
		try {
			return new ForwardProcessParameters (alpha, 1., rho, shift);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * <i>ForwardProcessParameters</i> Constructor
	 * 
	 * @param alpha SABR Alpha
	 * @param beta SABR Beta
	 * @param rho SABR Rho
	 * @param shift SABR Shift
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public ForwardProcessParameters (
		final double alpha,
		final double beta,
		final double rho,
		final double shift)
		throws Exception
	{
		if (!NumberUtil.IsValid (_alpha = alpha) || 0. > _alpha ||
			!NumberUtil.IsValid (_beta = beta) || 0. > _beta || 1. < _beta ||
			!NumberUtil.IsValid (_rho = rho) || -1. > _rho || 1. < _rho ||
			!NumberUtil.IsValid (_shift = shift))
		{
			throw new Exception ("ForwardProcessParameters Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the SABR Alpha
	 * 
	 * @return SABR Alpha
	 */

	public double alpha()
	{
		return _alpha;
	}

	/**
	 * Retrieve the SABR Beta
	 * 
	 * @return SABR Beta
	 */

	public double beta()
	{
		return _beta;
	}

	/**
	 * Retrieve the SABR Rho
	 * 
	 * @return SABR Rho
	 */

	public double rho()
	{
		return _rho;
	}

	/**
	 * Retrieve the SABR Vol-of-Vol
	 * 
	 * @return SABR Vol-of-Vol
	 */

	public double volVol()
	{
		return _alpha;
	}

	/**
	 * Retrieve the SABR Shift
	 * 
	 * @return SABR Shift
	 */

	public double shift()
	{
		return _shift;
	}
}
