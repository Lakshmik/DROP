
package org.drip.dynamics.meanreverting;

import org.drip.numerical.common.NumberUtil;

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
 * <i>CKLSParameters</i> contains the Parameters for the R<sup>1</sup> Chan-Karolyi-Longstaff-Sanders 1992
 * 	Stochastic Evolver. The References are:
 *  
 * 	<br><br>
 *  <ul>
 * 		<li>
 * 			Doob, J. L. (1942): The Brownian Movement and Stochastic Equations <i>Annals of Mathematics</i>
 * 				<b>43 (2)</b> 351-369
 * 		</li>
 * 		<li>
 * 			Gardiner, C. W. (2009): <i>Stochastic Methods: A Handbook for the Natural and Social Sciences
 * 				4<sup>th</sup> Edition</i> <b>Springer-Verlag</b>
 * 		</li>
 * 		<li>
 * 			Kadanoff, L. P. (2000): <i>Statistical Physics: Statics, Dynamics, and Re-normalization</i>
 * 				<b>World Scientific</b>
 * 		</li>
 * 		<li>
 * 			Karatzas, I., and S. E. Shreve (1991): <i>Brownian Motion and Stochastic Calculus 2<sup>nd</sup>
 * 				Edition</i> <b>Springer-Verlag</b>
 * 		</li>
 * 		<li>
 * 			Risken, H., and F. Till (1996): <i>The Fokker-Planck Equation – Methods of Solution and
 * 				Applications</i> <b>Springer</b>
 * 		</li>
 *  </ul>
 *
 * 	It provides the following Functions:
 *
 *  <ul>
 * 		<li>Construct the Vasicek Instance of the CKLS Parameters</li>
 * 		<li>Construct the Ornstein-Uhlenbeck Instance of the CKLS Parameters</li>
 * 		<li>Construct the Cox-Ingersoll-Ross Instance of the CKLS Parameters</li>
 * 		<li><i>CKLSParameters</i> Constructor</li>
 * 		<li>Retrieve the Mean Reversion Speed</li>
 * 		<li>Retrieve the Mean Reversion Level</li>
 * 		<li>Retrieve the Volatility Coefficient</li>
 * 		<li>Retrieve the CKLS Volatility Exponent</li>
 *	</ul>
 *
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/dynamics/README.md">HJM, Hull White, LMM, and SABR Dynamic Evolution Models</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/dynamics/meanreverting/README.md">Mean Reverting Stochastic Process Dynamics</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class CKLSParameters
{
	private double _meanReversionLevel = Double.NaN;
	private double _meanReversionSpeed = Double.NaN;
	private double _volatilityExponent = Double.NaN;
	private double _volatilityCoefficient = Double.NaN;

	/**
	 * Construct the Vasicek Instance of the CKLS Parameters
	 * 
	 * @param meanReversionSpeed The Mean Reversion Speed
	 * @param meanReversionLevel The Mean Reversion Level
	 * @param volatility The Volatility
	 * 
	 * @return The Vasicek Instance of the CKLS Parameters
	 */

	public static final CKLSParameters Vasicek (
		final double meanReversionSpeed,
		final double meanReversionLevel,
		final double volatility)
	{
		try {
			return new CKLSParameters (meanReversionSpeed, meanReversionLevel, volatility, 0.);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct the Ornstein-Uhlenbeck Instance of the CKLS Parameters
	 * 
	 * @param meanReversionSpeed The Mean Reversion Speed
	 * @param volatility The Volatility
	 * 
	 * @return The Ornstein-Uhlenbeck Instance of the CKLS Parameters
	 */

	public static final CKLSParameters OrnsteinUhlenbeck (
		final double meanReversionSpeed,
		final double volatility)
	{
		try {
			return new CKLSParameters (meanReversionSpeed, 0., volatility, 0.);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct the Cox-Ingersoll-Ross Instance of the CKLS Parameters
	 * 
	 * @param meanReversionSpeed The Mean Reversion Speed
	 * @param meanReversionLevel The Mean Reversion Level
	 * @param volatilityCoefficient The Volatility Coefficient
	 * 
	 * @return The Cox-Ingersoll-Ross Instance of the CKLS Parameters
	 */

	public static final CKLSParameters CoxIngersollRoss (
		final double meanReversionSpeed,
		final double meanReversionLevel,
		final double volatilityCoefficient)
	{
		try {
			return new CKLSParameters (meanReversionSpeed, meanReversionLevel, volatilityCoefficient, 0.5);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * <i>CKLSParameters</i> Constructor
	 * 
	 * @param meanReversionSpeed The Mean Reversion Speed
	 * @param meanReversionLevel The Mean Reversion Level
	 * @param volatilityCoefficient The Volatility Coefficient
	 * @param volatilityExponent The Volatility Exponent
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public CKLSParameters (
		final double meanReversionSpeed,
		final double meanReversionLevel,
		final double volatilityCoefficient,
		final double volatilityExponent)
		throws Exception
	{
		if (!NumberUtil.IsValid (_meanReversionSpeed = meanReversionSpeed) || 0. > _meanReversionSpeed ||
			!NumberUtil.IsValid (_meanReversionLevel = meanReversionLevel) || 0. > _meanReversionLevel ||
			!NumberUtil.IsValid (_volatilityCoefficient = volatilityCoefficient) ||
				0. > _volatilityCoefficient ||
			!NumberUtil.IsValid (_volatilityExponent = volatilityExponent) ||
				0. > _volatilityExponent)
		{
			throw new Exception ("CKLSParameters Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Mean Reversion Speed
	 * 
	 * @return The Mean Reversion Speed
	 */

	public double meanReversionSpeed()
	{
		return _meanReversionSpeed;
	}

	/**
	 * Retrieve the Mean Reversion Level
	 * 
	 * @return The Mean Reversion Level
	 */

	public double meanReversionLevel()
	{
		return _meanReversionLevel;
	}

	/**
	 * Retrieve the Volatility Coefficient
	 * 
	 * @return The Volatility Coefficient
	 */

	public double volatilityCoefficient()
	{
		return _volatilityCoefficient;
	}

	/**
	 * Retrieve the CKLS Volatility Exponent
	 * 
	 * @return The CKLS Volatility Exponent
	 */

	public double volatilityExponent()
	{
		return _volatilityExponent;
	}
}
