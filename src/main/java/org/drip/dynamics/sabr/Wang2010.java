
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
 * <i>Wang2010</i> implements Beta Estimation using Linear Regression from ATM Implied Volatilities to
 * 	Initial Forward Rates Time Series. The References are:
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
 * 		<li>Construct a Standard Instance of <i>Wang2020</i> Beta Calibrator</li>
 * 		<li><i>Wang2010</i> Constructor</li>
 * 		<li>Retrieve the Array of Log Implied ATM Volatilities</li>
 * 		<li>Retrieve the Array of Log Shifted Initial Forwards</li>
 * 		<li>Estimate SABR Beta using Linear Regression</li>
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

public class Wang2010
{
	private double[] _logATMImpliedVolatilityArray = null;
	private double[] _logShiftedInitialForwardArray = null;

	/**
	 * Construct a Standard Instance of <i>Wang2020</i> Beta Calibrator
	 * 
	 * @param atmImpliedVolatilityArray Array of Implied ATM Volatilities
	 * @param initialForwardArray Array of Initial Forwards
	 * @param shift Shift
	 * 
	 * @return <i>Wang2020</i> Beta Calibrator
	 */

	public static final Wang2010 Standard (
		final double[] atmImpliedVolatilityArray,
		final double[] initialForwardArray,
		final double shift)
	{
		if (null == atmImpliedVolatilityArray || 0 == atmImpliedVolatilityArray.length ||
			null == initialForwardArray || initialForwardArray.length != atmImpliedVolatilityArray.length ||
			!NumberUtil.IsValid (shift))
		{
			return null;
		}

		double[] logATMImpliedVolatilityArray = new double[atmImpliedVolatilityArray.length];
		double[] logShiftedInitialForwardArray = new double[atmImpliedVolatilityArray.length];

		for (int index = 0; index < atmImpliedVolatilityArray.length; ++index) {
			if (!NumberUtil.IsValid (atmImpliedVolatilityArray[index]) ||
				0. >= atmImpliedVolatilityArray[index] ||
				!NumberUtil.IsValid (initialForwardArray[index]))
			{
				return null;
			}

			double shiftedInitialForward = initialForwardArray[index] + shift;

			if (0. >= shiftedInitialForward) {
				return null;
			}

			logShiftedInitialForwardArray[index] = Math.log (shiftedInitialForward);

			logATMImpliedVolatilityArray[index] = Math.log (atmImpliedVolatilityArray[index]);
		}

		try {
			return new Wang2010 (logATMImpliedVolatilityArray, logShiftedInitialForwardArray);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * <i>Wang2010</i> Constructor
	 * 
	 * @param logATMImpliedVolatilityArray Array of Log Implied ATM Volatilities
	 * @param logShiftedInitialForwardArray Array of Log Shifted Initial Forwards
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public Wang2010 (
		final double[] logATMImpliedVolatilityArray,
		final double[] logShiftedInitialForwardArray)
		throws Exception
	{
		if (null == (_logATMImpliedVolatilityArray = logATMImpliedVolatilityArray) ||
				0 == _logATMImpliedVolatilityArray.length ||
				!NumberUtil.IsValid (_logATMImpliedVolatilityArray) ||
			null == (_logShiftedInitialForwardArray = logShiftedInitialForwardArray) ||
				_logShiftedInitialForwardArray.length != _logATMImpliedVolatilityArray.length ||
				!NumberUtil.IsValid (_logShiftedInitialForwardArray))
		{
			throw new Exception ("Wang2010 Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Array of Log Implied ATM Volatilities
	 * 
	 * @return Array of Log Implied ATM Volatilities
	 */

	public double[] logATMImpliedVolatilityArray()
	{
		return _logATMImpliedVolatilityArray;
	}

	/**
	 * Retrieve the Array of Log Shifted Initial Forwards
	 * 
	 * @return Array of Log Shifted Initial Forwards
	 */

	public double[] logShiftedInitialForwardArray()
	{
		return _logShiftedInitialForwardArray;
	}

	/**
	 * Estimate SABR Beta using Linear Regression
	 * 
	 * @return SABR Beta
	 */

	public double estimateBeta()
	{
		double sumXX = 0.;
		double sumXY = 0.;

		for (int index = 0; index < _logATMImpliedVolatilityArray.length; ++ index) {
			sumXX += _logShiftedInitialForwardArray[index] * _logShiftedInitialForwardArray[index];
			sumXY += _logATMImpliedVolatilityArray[index] * _logShiftedInitialForwardArray[index];
		}

		return 1. + sumXY / sumXX;
	}
}
