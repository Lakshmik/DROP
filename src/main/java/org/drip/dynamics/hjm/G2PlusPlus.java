
package org.drip.dynamics.hjm;

import org.drip.function.definition.R1ToR1;
import org.drip.numerical.common.NumberUtil;
import org.drip.sequence.random.UnivariateSequenceGenerator;

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
 * <i>G2PlusPlus</i> provides the Hull-White-type, but 2F Gaussian HJM Short Rate Dynamics Implementation. It
 *  provides the following Functions:
 *
 *  <ul>
 * 		<li><i>G2PlusPlus</i> Constructor</li>
 * 		<li>Retrieve Sigma</li>
 * 		<li>Retrieve A</li>
 * 		<li>Retrieve Eta</li>
 * 		<li>Retrieve B</li>
 * 		<li>Retrieve the Initial Instantaneous Forward Rate Term Structure</li>
 * 		<li>Retrieve the Random Sequence Generator Array</li>
 * 		<li>Retrieve Rho</li>
 * 		<li>Compute the G2++ Phi</li>
 * 		<li>Compute the X Increment</li>
 * 		<li>Compute the Y Increment</li>
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

public class G2PlusPlus
{
	private double _a = Double.NaN;
	private double _b = Double.NaN;
	private double _eta = Double.NaN;
	private double _rho = Double.NaN;
	private double _sigma = Double.NaN;
	private R1ToR1 _initialInstantaneousForwardFunction = null;
	private UnivariateSequenceGenerator[] _univariateSequenceGeneratorArray = null;

	/**
	 * <i>G2PlusPlus</i> Constructor
	 * 
	 * @param sigma Sigma
	 * @param a A
	 * @param eta Eta
	 * @param b B
	 * @param univariateSequenceGeneratorArray Array of the Random Sequence Generators
	 * @param rho Rho
	 * @param initialInstantaneousForwardFunction The Initial Instantaneous Forward Rate Term Structure
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public G2PlusPlus (
		final double sigma,
		final double a,
		final double eta,
		final double b,
		final UnivariateSequenceGenerator[] univariateSequenceGeneratorArray,
		final double rho,
		final R1ToR1 initialInstantaneousForwardFunction)
		throws Exception
	{
		if (!NumberUtil.IsValid (_sigma = sigma) ||
			!NumberUtil.IsValid (_a = a) ||
			!NumberUtil.IsValid (_eta = eta) ||
			!NumberUtil.IsValid (_b = b) ||
			null == (_univariateSequenceGeneratorArray = univariateSequenceGeneratorArray) ||
				2 != _univariateSequenceGeneratorArray.length ||
			!NumberUtil.IsValid (_rho = rho) ||
			null == (_initialInstantaneousForwardFunction = initialInstantaneousForwardFunction))
		{
			throw new Exception ("G2PlusPlus Constructor: Invalid Inputs");
		}
	}

	/**
	 * Retrieve Sigma
	 * 
	 * @return Sigma
	 */

	public double sigma()
	{
		return _sigma;
	}

	/**
	 * Retrieve A
	 * 
	 * @return A
	 */

	public double a()
	{
		return _a;
	}

	/**
	 * Retrieve Eta
	 * 
	 * @return Eta
	 */

	public double eta()
	{
		return _eta;
	}

	/**
	 * Retrieve B
	 * 
	 * @return B
	 */

	public double b()
	{
		return _b;
	}

	/**
	 * Retrieve the Initial Instantaneous Forward Rate Term Structure
	 * 
	 * @return The Initial Instantaneous Forward Rate Term Structure
	 */

	public R1ToR1 initialInstantaneousForwardFunction()
	{
		return _initialInstantaneousForwardFunction;
	}

	/**
	 * Retrieve the Random Sequence Generator Array
	 * 
	 * @return The Random Sequence Generator Array
	 */

	public UnivariateSequenceGenerator[] univariateSequenceGeneratorArray()
	{
		return _univariateSequenceGeneratorArray;
	}

	/**
	 * Retrieve Rho
	 * 
	 * @return Rho
	 */

	public double rho()
	{
		return _rho;
	}

	/**
	 * Compute the G2++ Phi
	 * 
	 * @param spotDate The Spot Date
	 * @param viewDate The View Date
	 * 
	 * @return The G2++ Phi
	 * 
	 * @throws Exception Thrown if the G2++ Phi cannot be computed
	 */

	public double phi (
		final int spotDate,
		final int viewDate)
		throws Exception
	{
		if (spotDate > viewDate) {
			throw new Exception ("G2PlusPlus::phi => Invalid Inputs");
		}

		double spotViewDCF = 1. * (viewDate - spotDate) / 365.25;

		double factor2Phi = _eta / _b * (1. - Math.exp (-1. * _b * spotViewDCF));

		double factor1Phi = _sigma / _a * (1. - Math.exp (-1. * _a * spotViewDCF));

		return _initialInstantaneousForwardFunction.evaluate (viewDate) +
			0.5 * factor1Phi * factor1Phi + 0.5 * factor2Phi * factor2Phi;
	}

	/**
	 * Compute the X Increment
	 * 
	 * @param spotDate The Spot Date
	 * @param viewDate The View Date
	 * @param x The X Value
	 * @param spotTimeIncrement The Spot Time Increment
	 * 
	 * @return The X Increment
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public double deltaX (
		final int spotDate,
		final int viewDate,
		final double x,
		final int spotTimeIncrement)
		throws Exception
	{
		if (spotDate > viewDate || !NumberUtil.IsValid (x)) {
			throw new Exception ("G2PlusPlus::deltaX => Invalid Inputs");
		}

		double annualizedIncrement = 1. * spotTimeIncrement / 365.25;

		return -1. * _a * x * annualizedIncrement +
			_sigma * Math.sqrt (annualizedIncrement) * _univariateSequenceGeneratorArray[0].random();
	}

	/**
	 * Compute the Y Increment
	 * 
	 * @param spotDate The Spot Date
	 * @param viewDate The View Date
	 * @param y The Y Value
	 * @param spotTimeIncrement The Spot Time Increment
	 * 
	 * @return The Y Increment
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public double deltaY (
		final int spotDate,
		final int viewDate,
		final double y,
		final int spotTimeIncrement)
		throws Exception
	{
		if (spotDate > viewDate || !NumberUtil.IsValid (y)) {
			throw new Exception ("G2PlusPlus::deltaY => Invalid Inputs");
		}

		double annualizedIncrement = 1. * spotTimeIncrement / 365.25;

		return -1. * _b * y * annualizedIncrement +
			_eta * Math.sqrt (annualizedIncrement) * _univariateSequenceGeneratorArray[1].random();
	}
}
