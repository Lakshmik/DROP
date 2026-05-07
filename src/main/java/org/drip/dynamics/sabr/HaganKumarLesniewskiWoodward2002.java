
package org.drip.dynamics.sabr;

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
 * <i>HaganKumarLesniewskiWoodward2002</i> executes a Volatility Implication Run using the Hagan, Kumar,
 * 	Lesniewski, and Woodward (2002). The References are:
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

public class HaganKumarLesniewskiWoodward2002
{
	private boolean _arithmeticMid = false;
	private boolean _normalImplication = false;
	private EuropeanOptionSetting _europeanOptionSetting = null;
	private ForwardProcessParameters _forwardProcessParameters = null;

	/**
	 * <i>HaganKumarLesniewskiWoodward2002</i> Constructor
	 * 
	 * @param forwardProcessParameters <i>ForwardProcessParameters</i> Instance
	 * @param europeanOptionSetting <i>EuropeanOptionSetting</i> Instance
	 * @param arithmeticMid TRUE - Arithmetic; FALSE - Geometric
	 * @param normalImplication TRUE - Normal/Bachelor; FALSE - Log-normal/Black
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public HaganKumarLesniewskiWoodward2002 (
		final ForwardProcessParameters forwardProcessParameters,
		final EuropeanOptionSetting europeanOptionSetting,
		final boolean arithmeticMid,
		final boolean normalImplication)
		throws Exception
	{
		if (null == (_forwardProcessParameters = forwardProcessParameters) ||
			null == (_europeanOptionSetting = europeanOptionSetting))
		{
			throw new Exception ("HaganKumarLesniewskiWoodward2002 Constructor => Invalid Inputs");
		}

		_arithmeticMid = arithmeticMid;
		_normalImplication = normalImplication;
	}

	/**
	 * Retrieve the <i>ForwardProcessParameters</i> Instance
	 * 
	 * @return <i>ForwardProcessParameters</i> Instance
	 */

	public ForwardProcessParameters forwardProcessParameters()
	{
		return _forwardProcessParameters;
	}

	/**
	 * Retrieve the <i>EuropeanOptionSetting</i> Instance
	 * 
	 * @return <i>EuropeanOptionSetting</i> Instance
	 */

	public EuropeanOptionSetting europeanOptionSetting()
	{
		return _europeanOptionSetting;
	}

	/**
	 * Indicate whether the Implication is based on Normal/Log-normal Scheme
	 * 
	 * @return TRUE - Normal/Bachelor; FALSE - Log-normal/Black
	 */

	public boolean normalImplication()
	{
		return _normalImplication;
	}

	/**
	 * Indicate if the Mid is Arithmetic/Geometric
	 * 
	 * @return TRUE - Arithmetic; FALSE - Geometric
	 */

	public boolean arithmeticMid()
	{
		return _arithmeticMid;
	}

	/**
	 * Imply the Volatility using the Mid-Price and the SABR Dynamics Implication Schemes
	 * 
	 * @param startingStateRealization Starting State Realization Instance
	 * 
	 * @return The <i>VolatilityImplication</i> Instance
	 */

	public VolatilityImplication imply (
		final StartingStateRealization startingStateRealization)
	{
		if (null == startingStateRealization) {
			return null;
		}

		double rho = _forwardProcessParameters.rho();

		double beta = _forwardProcessParameters.beta();

		double strike = _europeanOptionSetting.strike();

		double alpha = _forwardProcessParameters.alpha();

		double startingForward = startingStateRealization.forward();

		double startingForwardVolatility = startingStateRealization.forwardVolatility();

		double oneMinusBeta = 1. - beta;

		double zeta = alpha * (
			Math.pow (startingForward, oneMinusBeta) - Math.pow (strike, oneMinusBeta)
		) / (startingForwardVolatility * oneMinusBeta);

		double dOfZeta = Math.log (
			(Math.sqrt (1. - 2. * rho * zeta + zeta * zeta) + zeta - rho) / (1. - rho)
		);

		double inTheMoneyScaler = strike == startingForward ? 1. : _normalImplication ?
			(startingForward - strike) / dOfZeta : Math.log (startingForward / strike) / dOfZeta;

		double forwardMid = _arithmeticMid ? 0.5 * (strike + startingForward) :
			Math.sqrt (strike * startingForward);

		double cOfForwardMid = Math.pow (forwardMid, beta);

		double oneOverForwardMid = 1. / forwardMid;
		double gamma1 = beta * oneOverForwardMid;
		double oneOverForwardMidSquared = oneOverForwardMid * oneOverForwardMid;
		double gamma2 = -1. * beta * oneMinusBeta * oneOverForwardMidSquared;
		double twoGamma2MinusGamma1Squared = 2. + gamma2 - gamma1 * gamma1;
		double twoGamma2MinusGamma1SquaredPlus1OverFMidSquaredOver24 =
			(twoGamma2MinusGamma1Squared + oneOverForwardMidSquared) / 24.;
		double sigma0COfForwardMidOverAlpha = startingForwardVolatility * cOfForwardMid / alpha;
		double sigma0COfForwardMidOverAlphaSquared =
			sigma0COfForwardMidOverAlpha * sigma0COfForwardMidOverAlpha;
		double rhoGamma1Sigma0COfForwardMidOver4Alpha =
			0.25 * rho * gamma1 * sigma0COfForwardMidOverAlpha;
		double twoMinus3RhoSquaredOver24 = (2. - 3. * rho * rho) / 24.;

		double epsilon = _europeanOptionSetting.tte() * alpha * alpha;

		try {
			return new VolatilityImplication (
				zeta,
				dOfZeta,
				gamma2,
				gamma1,
				forwardMid,
				cOfForwardMid,
				epsilon,
				!_normalImplication ? alpha * inTheMoneyScaler * (
					1. + epsilon * (
						twoGamma2MinusGamma1SquaredPlus1OverFMidSquaredOver24 *
							sigma0COfForwardMidOverAlphaSquared +
							rhoGamma1Sigma0COfForwardMidOver4Alpha + twoMinus3RhoSquaredOver24
					)
				) : alpha * inTheMoneyScaler * (
					1. + epsilon * (
						twoGamma2MinusGamma1Squared * sigma0COfForwardMidOverAlphaSquared / 24. +
							rhoGamma1Sigma0COfForwardMidOver4Alpha + twoMinus3RhoSquaredOver24
					)
				)
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static final void main (
		final String[] argumentArray)
		throws Exception
	{
		double shift = 0.;
		double rho = -0.10;
		double beta = 0.25;
		double alpha = 1.;
		double initialForward = 1.;
		double initialForwardVolatility = 0.35;

		double timeToExpiry = 1.;
		double strike = initialForward * 2.;

		StartingStateRealization startingStateRealization = new StartingStateRealization (
			initialForward,
			initialForwardVolatility
		);

		HaganKumarLesniewskiWoodward2002 haganKumarLesniewskiWoodward2002 =
			new HaganKumarLesniewskiWoodward2002 (
				new ForwardProcessParameters (alpha, beta, rho, shift),
				new EuropeanOptionSetting (strike, timeToExpiry),
				false,
				true
			);

		VolatilityImplication volatilityImplication =
			haganKumarLesniewskiWoodward2002.imply (startingStateRealization);

		System.out.println (volatilityImplication.implied());
	}
}
