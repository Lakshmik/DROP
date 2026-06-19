
package org.drip.dynamics.hjm;

import org.drip.analytics.definition.MarketSurface;
import org.drip.function.definition.R1ToR1;
import org.drip.sequence.random.PrincipalFactorSequenceGenerator;

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
 * <i>MultiFactorVolatility</i> implements the Volatility of the Multi-factor Stochastic Evolution Process.
 * 	The Factors may come from the Underlying Stochastic Variables, or from Principal Components. It provides
 * 	the following Functions:
 *
 *  <ul>
 * 		<li><i>MultiFactorVolatility</i> Constructor</li>
 * 		<li>Retrieve the Array of Volatility Surfaces</li>
 * 		<li>Retrieve the Principal Factor Sequence Generator</li>
 * 		<li>Retrieve the Factor-Specific Univariate Volatility Function for the Specified Date</li>
 * 		<li>Compute the Factor Volatility Integral</li>
 * 		<li>Compute the Factor Point Volatility</li>
 * 		<li>Compute the Array of Factor Point Volatilities</li>
 * 		<li>Compute the Weighted Factor Point Volatility</li>
 * 		<li>Compute the Point Volatility Modulus</li>
 * 		<li>Compute the Point Volatility Modulus Derivative</li>
 *  </ul>
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/dynamics/README.md">HJM, Hull White, LMM, and SABR Dynamic Evolution Models</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/dynamics/hjm/README.md">HJM Based Latent State Evolution</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class MultiFactorVolatility
{
	private MarketSurface[] _volatilityMarketSurfaceArray = null;
	private PrincipalFactorSequenceGenerator _principalFactorSequenceGenerator = null;

	/**
	 * <i>MultiFactorVolatility</i> Constructor
	 * 
	 * @param volatilityMarketSurfaceArray Array of the Multi-Factor Volatility Surfaces
	 * @param principalFactorSequenceGenerator Principal Factor Sequence Generator
	 * 
	 * @throws Exception Thrown if Inputs are Invalid
	 */

	public MultiFactorVolatility (
		final MarketSurface[] volatilityMarketSurfaceArray,
		final PrincipalFactorSequenceGenerator principalFactorSequenceGenerator)
		throws Exception
	{
		if (null == (_volatilityMarketSurfaceArray = volatilityMarketSurfaceArray) ||
			null == (_principalFactorSequenceGenerator = principalFactorSequenceGenerator))
		{
			throw new Exception ("MultiFactorVolatility Constructor: Invalid Inputs");
		}

		int factorCount = _principalFactorSequenceGenerator.numFactor();

		if (0 == factorCount || _volatilityMarketSurfaceArray.length < factorCount) {
			throw new Exception ("MultiFactorVolatility Constructor: Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Array of Volatility Surfaces
	 * 
	 * @return The Array of Volatility Surfaces
	 */

	public MarketSurface[] volatilityMarketSurfaceArray()
	{
		return _volatilityMarketSurfaceArray;
	}

	/**
	 * Retrieve the Principal Factor Sequence Generator
	 * 
	 * @return The Principal Factor Sequence Generator
	 */

	public PrincipalFactorSequenceGenerator principalFactorSequenceGenerator()
	{
		return _principalFactorSequenceGenerator;
	}

	/**
	 * Retrieve the Factor-Specific Univariate Volatility Function for the Specified Date
	 * 
	 * @param factorIndex The Factor Index
	 * @param xDate The X Date
	 * 
	 * @return The Factor-Specific Univariate Volatility Function for the Specified Date
	 */

	public R1ToR1 xDateVolatilityFunction (
		final int factorIndex,
		final int xDate)
	{
		if (factorIndex >= _principalFactorSequenceGenerator.numFactor()) {
			return null;
		}

		return new R1ToR1 (null)
		{
			@Override public double evaluate (
				final double x)
				throws Exception
			{
				double multiFactorVolatility = 0.;

				double[] factorArray = _principalFactorSequenceGenerator.factors()[factorIndex];

				for (int volatilityMarketSurfaceIndex = 0;
					volatilityMarketSurfaceIndex < _volatilityMarketSurfaceArray.length;
					++volatilityMarketSurfaceIndex)
				{
					multiFactorVolatility += factorArray[volatilityMarketSurfaceIndex] *
						_volatilityMarketSurfaceArray[factorIndex].xAnchorTermStructure (
							xDate
						).node (
							(int) x
						);
				}

				return _principalFactorSequenceGenerator.factorWeight()[factorIndex] * multiFactorVolatility;
			}
		};
	}

	/**
	 * Compute the Factor Volatility Integral
	 * 
	 * @param factorIndex The Factor Index
	 * @param xDate The X Date
	 * @param yDate The Y Date
	 * 
	 * @return The Factor Volatility Integral
	 * 
	 * @throws Exception Thrown if the Factor Volatility Integral cannot be computed
	 */

	public double volatilityIntegral (
		final int factorIndex,
		final int xDate,
		final int yDate)
		throws Exception
	{
		R1ToR1 volatilityFunction = xDateVolatilityFunction (factorIndex, xDate);

		if (null == volatilityFunction) {
			throw new Exception (
				"MultiFactorVolatility::volatilityIntegral => Cannot extract X Date Volatility Function"
			);
		}

		return volatilityFunction.integrate (xDate, yDate) / 365.25;
	}

	/**
	 * Compute the Factor Point Volatility
	 * 
	 * @param factorIndex The Factor Index
	 * @param xDate The X Date
	 * @param yDate The Y Date
	 * 
	 * @return The Factor Point Volatility
	 * 
	 * @throws Exception Thrown if the Factor Point Volatility cannot be computed
	 */

	public double factorPointVolatility (
		final int factorIndex,
		final int xDate,
		final int yDate)
		throws Exception
	{
		if (factorIndex >= _principalFactorSequenceGenerator.numFactor()) {
			throw new Exception ("MultiFactorVolatility::factorPointVolatility => Invalid Factor Index");
		}

		double[] factorArray = _principalFactorSequenceGenerator.factors()[factorIndex];

		double factorPointVolatility = 0.;

		for (int index = 0; index < factorArray.length; ++index) {
			factorPointVolatility +=
				factorArray[index] * _volatilityMarketSurfaceArray[index].node (xDate, yDate);
		}

		return factorPointVolatility;
	}

	/**
	 * Compute the Array of Factor Point Volatilities
	 * 
	 * @param xDate The X Date
	 * @param yDate The Y Date
	 * 
	 * @return The Array of Factor Point Volatilities
	 */

	public double[] factorPointVolatility (
		final int xDate,
		final int yDate)
	{
		int factorCount = _principalFactorSequenceGenerator.numFactor();

		double[][] factorGrid = _principalFactorSequenceGenerator.factors();

		int variateCount = factorGrid[0].length;
		double[] variateVolatilityArray = new double[variateCount];
		double[] factorPointVolatilityArray = new double[factorCount];

		for (int variateIndex = 0; variateIndex < variateCount; ++variateIndex) {
			try {
				variateVolatilityArray[variateIndex] =
					_volatilityMarketSurfaceArray[variateIndex].node (xDate, yDate);
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		for (int factorIndex = 0; factorIndex < factorCount; ++factorIndex) {
			factorPointVolatilityArray[factorIndex] = 0.;
			double[] factorArray = factorGrid[factorIndex];

			for (int variateIndex = 0; variateIndex < variateCount; ++variateIndex) {
				factorPointVolatilityArray[factorIndex] +=
					factorArray[variateIndex] * variateVolatilityArray[variateIndex];
			}
		}

		return factorPointVolatilityArray;
	}

	/**
	 * Compute the Weighted Factor Point Volatility
	 * 
	 * @param factorIndex The Factor Index
	 * @param xDate The X Date
	 * @param yDate The Y Date
	 * 
	 * @return The Weighted Factor Point Volatility
	 * 
	 * @throws Exception Thrown if the Weighted Factor Point Volatility cannot be computed
	 */

	public double weightedFactorPointVolatility (
		final int factorIndex,
		final int xDate,
		final int yDate)
		throws Exception
	{
		if (factorIndex >= _principalFactorSequenceGenerator.numFactor()) {
			throw new Exception (
				"MultiFactorVolatility::weightedFactorPointVolatility => Invalid Factor Index"
			);
		}

		double[] factorArray = _principalFactorSequenceGenerator.factors()[factorIndex];

		double factorPointVolatility = 0.;

		for (int index = 0; index < factorArray.length; ++index) {
			factorPointVolatility +=
				factorArray[index] * _volatilityMarketSurfaceArray[index].node (xDate, yDate);
		}

		return _principalFactorSequenceGenerator.factorWeight()[factorIndex] * factorPointVolatility;
	}

	/**
	 * Compute the Point Volatility Modulus
	 * 
	 * @param xDate The X Date
	 * @param yDate The Y Date
	 * 
	 * @return The Point Volatility Modulus
	 * 
	 * @throws Exception Thrown if the Point Volatility Modulus cannot be computed
	 */

	public double pointVolatilityModulus (
		final int xDate,
		final int yDate)
		throws Exception
	{
		double pointVolatilityModulus = 0.;

		for (int factorIndex = 0;
			factorIndex < _principalFactorSequenceGenerator.numFactor();
			++factorIndex)
		{
			double weightedFactorPointVolatility = weightedFactorPointVolatility (factorIndex, xDate, yDate);

			pointVolatilityModulus += weightedFactorPointVolatility * weightedFactorPointVolatility;
		}

		return pointVolatilityModulus;
	}

	/**
	 * Compute the Point Volatility Modulus Derivative
	 * 
	 * @param xDate The X Date
	 * @param yDate The Y Date
	 * @param order The Derivative Order
	 * @param terminal TRUE - Derivative off of the Y Date; FALSE - Derivative off of the X Date
	 * 
	 * @return The Point Volatility Modulus Derivative
	 * 
	 * @throws Exception Thrown if the Point Volatility Modulus Derivative cannot be computed
	 */

	public double pointVolatilityModulusDerivative (
		final int xDate,
		final int yDate,
		final int order,
		final boolean terminal)
		throws Exception
	{
		R1ToR1 pointVolatilityFunction = new R1ToR1 (null)
		{
			@Override public double evaluate (
				final double variate)
				throws Exception
			{
				return terminal ? pointVolatilityModulus (xDate, (int) variate) :
					pointVolatilityModulus ((int) variate, yDate);
			}
		};

		return terminal ? pointVolatilityFunction.derivative (xDate, order) :
			pointVolatilityFunction.derivative (xDate, order);
	}
}
