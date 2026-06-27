
package org.drip.dynamics.lmm;

import org.drip.numerical.common.NumberUtil;
import org.drip.service.common.FormatUtil;

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
 * <i>BGMForwardTenorSnap</i> contains the Absolute and the Incremental Latent State Quantifier Snapshot
 * 	traced from the Evolution of the LIBOR Forward Rate as formulated in:
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
 * 		<li><i>BGMForwardTenorSnap</i> Constructor</li>
 * 		<li>Retrieve the Tenor Date</li>
 * 		<li>Retrieve the LIBOR Rate</li>
 * 		<li>Retrieve the LIBOR Rate Increment</li>
 * 		<li>Retrieve the Discount Factor</li>
 * 		<li>Retrieve the Discount Factor Increment</li>
 * 		<li>Retrieve the Continuously Compounded Forward Rate Increment</li>
 * 		<li>Retrieve the Spot Rate Increment</li>
 * 		<li>Retrieve the Instantaneous Effective Annual Forward Rate</li>
 * 		<li>Retrieve the Instantaneous Nominal Annual Forward Rate</li>
 * 		<li>Retrieve the Log-normal LIBOR Volatility</li>
 * 		<li>Retrieve the Continuously Compounded Forward Rate Volatility</li>
 * 		<li>Retrieve the JSON-ized Version of the State</li>
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

public class BGMForwardTenorSnap
{
	private double _libor = Double.NaN;
	private int _date = Integer.MIN_VALUE;
	private double _discountFactor = Double.NaN;
	private double _liborIncrement = Double.NaN;
	private double _spotRateIncrement = Double.NaN;
	private double _discountFactorIncrement = Double.NaN;
	private double _lognormalLIBORVolatility = Double.NaN;
	private double _instantaneousNominalForwardRate = Double.NaN;
	private double _instantaneousEffectiveForwardRate = Double.NaN;
	private double _continuouslyCompoundedForwardIncrement = Double.NaN;
	private double _continuouslyCompoundedForwardVolatility = Double.NaN;

	/**
	 * <i>BGMForwardTenorSnap</i> Constructor
	 * 
	 * @param date The Date corresponding to the Tenor
	 * @param libor The LIBOR Rate
	 * @param liborIncrement The LIBOR Rate Increment
	 * @param discountFactor The Discount Factor
	 * @param discountFactorIncrement The Discount Factor Increment
	 * @param continuouslyCompoundedForwardIncrement Continuously Compounded Forward Rate Increment
	 * @param spotRateIncrement Spot Rate Increment
	 * @param instantaneousEffectiveForwardRate Instantaneous Effective Annual Forward Rate
	 * @param instantaneousNominalForwardRate Instantaneous Nominal Annual Forward Rate
	 * @param lognormalLIBORVolatility The Log-normal LIBOR Rate Volatility
	 * @param continuouslyCompoundedForwardVolatility The Continuously Compounded Forward Rate Volatility
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public BGMForwardTenorSnap (
		final int date,
		final double libor,
		final double liborIncrement,
		final double discountFactor,
		final double discountFactorIncrement,
		final double continuouslyCompoundedForwardIncrement,
		final double spotRateIncrement,
		final double instantaneousEffectiveForwardRate,
		final double instantaneousNominalForwardRate,
		final double lognormalLIBORVolatility,
		final double continuouslyCompoundedForwardVolatility)
		throws Exception
	{
		if (!NumberUtil.IsValid (_libor = libor) ||
			!NumberUtil.IsValid (_liborIncrement = liborIncrement) ||
			!NumberUtil.IsValid (_discountFactor = discountFactor) ||
			!NumberUtil.IsValid (_discountFactorIncrement = discountFactorIncrement) ||
			!NumberUtil.IsValid (
				_continuouslyCompoundedForwardIncrement = continuouslyCompoundedForwardIncrement
			) ||
			!NumberUtil.IsValid (_spotRateIncrement = spotRateIncrement) ||
			!NumberUtil.IsValid (_instantaneousEffectiveForwardRate = instantaneousEffectiveForwardRate) ||
			!NumberUtil.IsValid (_instantaneousNominalForwardRate = instantaneousNominalForwardRate) ||
			!NumberUtil.IsValid (_lognormalLIBORVolatility = lognormalLIBORVolatility) ||
			!NumberUtil.IsValid (
				_continuouslyCompoundedForwardVolatility = continuouslyCompoundedForwardVolatility
			)
		)
		{
			throw new Exception ("BGMForwardTenorSnap Constructor: Invalid Inputs");
		}

		_date = date;
	}

	/**
	 * Retrieve the Tenor Date
	 * 
	 * @return The Tenor Date
	 */

	public int date()
	{
		return _date;
	}

	/**
	 * Retrieve the LIBOR Rate
	 * 
	 * @return The LIBOR Rate
	 */

	public double libor()
	{
		return _libor;
	}

	/**
	 * Retrieve the LIBOR Rate Increment
	 * 
	 * @return The LIBOR Rate Increment
	 */

	public double liborIncrement()
	{
		return _liborIncrement;
	}

	/**
	 * Retrieve the Discount Factor
	 * 
	 * @return The Discount Factor
	 */

	public double discountFactor()
	{
		return _discountFactor;
	}

	/**
	 * Retrieve the Discount Factor Increment
	 * 
	 * @return The Discount Factor Increment
	 */

	public double discountFactorIncrement()
	{
		return _discountFactorIncrement;
	}

	/**
	 * Retrieve the Continuously Compounded Forward Rate Increment
	 * 
	 * @return The Continuously Compounded Forward Rate Increment
	 */

	public double continuouslyCompoundedForwardIncrement()
	{
		return _continuouslyCompoundedForwardIncrement;
	}

	/**
	 * Retrieve the Spot Rate Increment
	 * 
	 * @return The Spot Rate Increment
	 */

	public double spotRateIncrement()
	{
		return _spotRateIncrement;
	}

	/**
	 * Retrieve the Instantaneous Effective Annual Forward Rate
	 * 
	 * @return The Instantaneous Effective Annual Forward Rate
	 */

	public double instantaneousEffectiveForwardRate()
	{
		return _instantaneousEffectiveForwardRate;
	}

	/**
	 * Retrieve the Instantaneous Nominal Annual Forward Rate
	 * 
	 * @return The Instantaneous Nominal Annual Forward Rate
	 */

	public double instantaneousNominalForwardRate()
	{
		return _instantaneousNominalForwardRate;
	}

	/**
	 * Retrieve the Log-normal LIBOR Volatility
	 * 
	 * @return The Log-normal LIBOR Volatility
	 */

	public double lognormalLIBORVolatility()
	{
		return _lognormalLIBORVolatility;
	}

	/**
	 * Retrieve the Continuously Compounded Forward Rate Volatility
	 * 
	 * @return The Continuously Compounded Forward Rate Volatility
	 */

	public double continuouslyCompoundedForwardVolatility()
	{
		return _continuouslyCompoundedForwardVolatility;
	}

	/**
	 * Retrieve the JSON-ized Version of the State
	 * 
	 * @return JSON-ized Version of the State
	 */

	@Override public String toString()
	{
		return FormatUtil.FormatDouble (_libor, 1, 2, 100.) + "% | " +
			FormatUtil.FormatDouble (_liborIncrement, 2, 2, 10000.) + " | " +
			FormatUtil.FormatDouble (_discountFactor, 1, 4, 1.) + " | " +
			FormatUtil.FormatDouble (_discountFactorIncrement, 2, 2, 10000.) + " | " +
			FormatUtil.FormatDouble (_continuouslyCompoundedForwardIncrement, 2, 2, 10000.) + " | " +
			FormatUtil.FormatDouble (_spotRateIncrement, 2, 2, 10000.) + " | " +
			FormatUtil.FormatDouble (_instantaneousEffectiveForwardRate, 2, 2, 10000.) + " | " +
			FormatUtil.FormatDouble (_instantaneousNominalForwardRate, 2, 2, 10000.) + " ||";
	}
}
