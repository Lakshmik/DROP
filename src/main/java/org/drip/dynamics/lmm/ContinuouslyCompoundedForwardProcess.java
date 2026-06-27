
package org.drip.dynamics.lmm;

import org.drip.analytics.date.JulianDate;
import org.drip.analytics.support.Helper;
import org.drip.function.definition.R1R1ToR1;

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
 * <i>ContinuouslyCompoundedForwardProcess</i> implements the Continuously Compounded Forward Rate Process
 * 	defined in the LIBOR Market Model. The References are:
 *
 *	<br><br>
 *  <ul>
 *  	<li>
 *  		Goldys, B., M. Musiela, and D. Sondermann (1994): <i>Log-normality of Rates and Term Structure
 *  			Models</i> <b>The University of New South Wales</b>
 *  	</li>
 *  	<li>
 *  		Musiela, M. (1994): <i>Nominal Annual Rates and Log-normal Volatility Structure</i> <b>The
 *  			University of New South Wales</b>
 *  	</li>
 *  	<li>
 * 			Brace, A., D. Gatarek, and M. Musiela (1997): The Market Model of Interest Rate Dynamics
 * 				<i>Mathematical Finance</i> <b>7 (2)</b> 127-155
 *  	</li>
 *  </ul>
 *
 * 	It provides the following Functions:
 *
 *  <ul>
 * 		<li><i>ContinuouslyCompoundedForwardProcess</i> Constructor</li>
 * 		<li>Retrieve the Spot Date</li>
 * 		<li>Retrieve the Stochastic Forward Rate Function</li>
 * 		<li>Retrieve a Realized Zero-Coupon Bond Price</li>
 * 		<li>Compute the Realized/Expected Instantaneous Forward Rate Integral to the Target Date</li>
 * 		<li>Retrieve a Realized/Expected Value of the Discount to the Target Date #1</li>
 * 		<li>Retrieve a Realized/Expected Value of the Discount to the Target Date #2</li>
 * 		<li>Retrieve a Realized/Expected Value of the LIBOR Rate at the Target Date</li>
 * </ul>
 *
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/dynamics/README.md">HJM, Hull White, LMM, and SABR Dynamic Evolution Models</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/dynamics/lmm/README.md">LMM Based Latent State Evolution</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class ContinuouslyCompoundedForwardProcess
{
	private int _spotDate = Integer.MIN_VALUE;
	private R1R1ToR1 _stochasticForwardRateFunction = null;

	/**
	 * <i>ContinuouslyCompoundedForwardProcess</i> Constructor
	 * 
	 * @param spotDate The Spot Date
	 * @param stochasticForwardRateFunction The Stochastic Forward Rate Function
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public ContinuouslyCompoundedForwardProcess (
		final int spotDate,
		final R1R1ToR1 stochasticForwardRateFunction)
		throws Exception
	{
		if (null == (_stochasticForwardRateFunction = stochasticForwardRateFunction)) {
			throw new Exception ("ContinuouslyCompoundedForwardProcess ctr: Invalid Inputs");
		}

		_spotDate = spotDate;
	}

	/**
	 * Retrieve the Spot Date
	 * 
	 * @return The Spot Date
	 */

	public int spotDate()
	{
		return _spotDate;
	}

	/**
	 * Retrieve the Stochastic Forward Rate Function
	 * 
	 * @return The Stochastic Forward Rate Function
	 */

	public R1R1ToR1 stochasticForwardRateFunction()
	{
		return _stochasticForwardRateFunction;
	}

	/**
	 * Retrieve a Realized Zero-Coupon Bond Price
	 * 
	 * @param maturityDate The Maturity Date
	 * 
	 * @return The Realized Zero-Coupon Bond Price
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public double realizedZeroCouponPrice (
		final int maturityDate)
		throws Exception
	{
		if (maturityDate <= _spotDate) {
			throw new Exception (
				"ContinuouslyCompoundedForwardProcess::realizedZeroCouponPrice => Invalid Maturity Date"
			);
		}

		return Math.exp (
			-1. * _stochasticForwardRateFunction.integralRealization (0., maturityDate - _spotDate)
		);
	}

	/**
	 * Compute the Realized/Expected Instantaneous Forward Rate Integral to the Target Date
	 * 
	 * @param targetDate The Target Date
	 * @param realized
	 * 	TRUE - Compute the Realized (TRUE) / Expected (FALSE) Instantaneous Forward Rate Integral
	 * 
	 * @return The Realized/Expected Instantaneous Forward Rate Integral
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public double instantaneousForwardRateIntegral (
		final int targetDate,
		final boolean realized)
		throws Exception
	{
		if (targetDate <= _spotDate) {
			throw new Exception (
				"ContinuouslyCompoundedForwardProcess::instantaneousForwardRateIntegral => Invalid Target Date"
			);
		}

		return realized ? Math.exp (
			-1. * _stochasticForwardRateFunction.integralRealization (0., targetDate - _spotDate)
		) : Math.exp (
			-1. * _stochasticForwardRateFunction.integralExpectation (0., targetDate - _spotDate)
		);
	}

	/**
	 * Retrieve a Realized/Expected Value of the Discount to the Target Date
	 * 
	 * @param targetDate The Target Date
	 * @param realized
	 * 	TRUE - Compute the Realized (TRUE) / Expected (FALSE) Instantaneous Forward Rate Integral
	 * 
	 * @return The Realized/Expected Value of the Discount to the Target Date
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public double discountFunctionValue (
		final int targetDate,
		final boolean realized)
		throws Exception
	{
		if (targetDate <= _spotDate) {
			throw new Exception (
				"ContinuouslyCompoundedForwardProcess::discountFunctionValue => Invalid Target Date"
			);
		}

		return realized ? Math.exp (
			-1. * _stochasticForwardRateFunction.integralRealization (0., targetDate - _spotDate)
		) : Math.exp (
			-1. * _stochasticForwardRateFunction.integralExpectation (0., targetDate - _spotDate)
		);
	}

	/**
	 * Retrieve a Realized/Expected Value of the LIBOR Rate at the Target Date
	 * 
	 * @param targetDate The Target Date
	 * @param tenor The LIBOR Tenor
	 * @param realized TRUE - Compute the Realized (TRUE) / Expected (FALSE) LIBOR Rate
	 * 
	 * @return The Realized/Expected Value of the LIBOR Rate at the Target Date
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public double liborRate (
		final int targetDate,
		final String tenor,
		final boolean realized)
		throws Exception
	{
		if (targetDate <= _spotDate) {
			throw new Exception ("ContinuouslyCompoundedForwardProcess::liborRate => Invalid Inputs");
		}

		return ((
			discountFunctionValue (new JulianDate (targetDate).addTenor (tenor).julian(), realized) /
				discountFunctionValue (targetDate, realized)
			) - 1.
		) / Helper.TenorToYearFraction (tenor);
	}
}
