
package org.drip.dynamics.lmm;

import org.drip.analytics.date.JulianDate;
import org.drip.analytics.definition.MarketSurface;
import org.drip.analytics.support.Helper;
import org.drip.dynamics.hjm.MultiFactorVolatility;
import org.drip.function.definition.R1ToR1;
import org.drip.sequence.random.PrincipalFactorSequenceGenerator;
import org.drip.state.discount.MergedDiscountForwardCurve;
import org.drip.state.forward.ForwardCurve;
import org.drip.state.identifier.ForwardLabel;

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
 * <i>LognormalLIBORVolatility</i> implements the Multi-Factor Log-normal LIBOR Volatility as formulated in:
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
 * 		<li><i>LognormalLIBORVolatility</i> Constructor</li>
 * 		<li>Retrieve the Spot Date</li>
 * 		<li>Retrieve the Forward Label</li>
 * 		<li>Compute the Constraint in the Difference in the Volatility of the Continuously Compounded Forward Rate between the Target Date and the Target Date + Forward Tenor</li>
 * 		<li>Compute the Volatility of the Continuously Compounded Forward Rate Up to the Target Date #1</li>
 * 		<li>Compute the Volatility of the Continuously Compounded Forward Rate Up to the Target Date #2</li>
 * 		<li>Multi-Factor Cross Volatility Integral</li>
 *	<br>
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

public class LognormalLIBORVolatility
	extends MultiFactorVolatility
{
	private ForwardLabel _forwardLabel = null;
	private int _spotDate = Integer.MIN_VALUE;

	/**
	 * <i>LognormalLIBORVolatility</i> Constructor
	 * 
	 * @param spotDate The Spot Date
	 * @param forwardLabel The Forward Label
	 * @param marketSurfaceVolatilityArray Array of the Multi-Factor Volatility Surfaces
	 * @param principalFactorSequenceGenerator Principal Factor Sequence Generator
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public LognormalLIBORVolatility (
		final int spotDate,
		final ForwardLabel forwardLabel,
		final MarketSurface[] marketSurfaceVolatilityArray,
		final PrincipalFactorSequenceGenerator principalFactorSequenceGenerator)
		throws Exception
	{
		super (marketSurfaceVolatilityArray, principalFactorSequenceGenerator);

		if (null == (_forwardLabel = forwardLabel)) {
			throw new Exception ("LognormalLIBORVolatility Constructor: Invalid Inputs");
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
	 * Retrieve the Forward Label
	 * 
	 * @return The Forward Label
	 */

	public ForwardLabel forwardLabel()
	{
		return _forwardLabel;
	}

	/**
	 * Compute the Constraint in the Difference in the Volatility of the Continuously Compounded Forward Rate
	 * 	between the Target Date and the Target Date + Forward Tenor
	 * 
	 * @param forwardCurve The Forward Curve Instance
	 * @param targetDate The Target Date
	 * 
	 * @return The Constraint in the Difference in the Volatility of the Continuously Compounded Forward Rate
	 */

	public double[] continuousForwardVolatilityConstraint (
		final ForwardCurve forwardCurve,
		final int targetDate)
	{
		if (null == forwardCurve || targetDate <= _spotDate) {
			return null;
		}

		String tenor = _forwardLabel.tenor();

		MarketSurface[] marketSurfaceArray = volatilityMarketSurfaceArray();

		try {
			double liborDCF = forwardCurve.forward (
				new JulianDate (targetDate).addTenor (tenor)
			) * Helper.TenorToYearFraction (tenor);

			double constraintWeight = liborDCF / (1. + liborDCF);
			double[] continuousForwardVolatilityConstraintArray = new double[marketSurfaceArray.length];

			for (int marketSurfaceIndex = 0;
				marketSurfaceIndex < marketSurfaceArray.length;
				++marketSurfaceIndex)
			{
				continuousForwardVolatilityConstraintArray[marketSurfaceIndex] =
					constraintWeight * marketSurfaceArray[marketSurfaceIndex].node (_spotDate, targetDate);
			}

			return continuousForwardVolatilityConstraintArray;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Compute the Volatility of the Continuously Compounded Forward Rate Up to the Target Date
	 * 
	 * @param targetDate The Target Date
	 * @param forwardCurve The Forward Curve Instance
	 * 
	 * @return The Volatility of the Continuously Compounded Forward Rate Up to the Target Date
	 */

	public double[] continuousForwardVolatility (
		final int targetDate,
		final ForwardCurve forwardCurve)
	{
		if (targetDate <= _spotDate || null == forwardCurve) {
			return null;
		}

		int factorCount = principalFactorSequenceGenerator().numFactor();

		boolean loop = true;
		int endDate = _spotDate;
		double tenorDCF = Double.NaN;
		double[] continuousForwardVolatilityArray = new double[factorCount];

		String tenor = _forwardLabel.tenor();

		try {
			tenorDCF = Helper.TenorToYearFraction (tenor);
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}

		for (int factorIndex = 0; factorIndex < factorCount; ++factorIndex) {
			continuousForwardVolatilityArray[factorIndex] = 0.;
		}

		double[] factorPointVolatilityArray = factorPointVolatility (_spotDate, endDate);

		while (loop) {
			try {
				if ((endDate = new JulianDate (endDate).addTenor (tenor).julian()) > targetDate) {
					loop = false;
				}

				double liborTenorDCF = forwardCurve.forward (endDate) * tenorDCF;

				double liborLognormalVolatilityScaler = liborTenorDCF / (1. + liborTenorDCF);

				for (int factorIndex = 0; factorIndex < factorCount; ++factorIndex) {
					continuousForwardVolatilityArray[factorIndex] +=
						liborLognormalVolatilityScaler * factorPointVolatilityArray[factorIndex];
				}
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		return continuousForwardVolatilityArray;
	}

	/**
	 * Compute the Volatility of the Continuously Compounded Forward Rate Up to the Target Date
	 * 
	 * @param targetDate The Target Date
	 * @param discountCurve The Discount Curve Instance
	 * 
	 * @return The Volatility of the Continuously Compounded Forward Rate Up to the Target Date
	 */

	public double[] continuousForwardVolatility (
		final int targetDate,
		final MergedDiscountForwardCurve discountCurve)
	{
		if (targetDate <= _spotDate || null == discountCurve) {
			return null;
		}

		int factorCount = principalFactorSequenceGenerator().numFactor();

		boolean loop = true;
		int startDate = _spotDate;
		double tenorDCF = Double.NaN;
		double[] continuousForwardVolatilityArray = new double[factorCount];

		String tenor = _forwardLabel.tenor();

		try {
			tenorDCF = Helper.TenorToYearFraction (tenor);
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}

		for (int factorIndex = 0; factorIndex < factorCount; ++factorIndex) {
			continuousForwardVolatilityArray[factorIndex] = 0.;
		}

		double[] factorPointVolatilityArray = factorPointVolatility (_spotDate, startDate);

		while (loop) {
			try {
				double liborTenorDCF = discountCurve.libor (startDate, tenor) * tenorDCF;

				double liborLognormalVolatilityScaler = liborTenorDCF / (1. + liborTenorDCF);

				for (int factorIndex = 0; factorIndex < factorCount; ++factorIndex) {
					continuousForwardVolatilityArray[factorIndex] +=
						liborLognormalVolatilityScaler * factorPointVolatilityArray[factorIndex];
				}

				if ((startDate = new JulianDate (startDate).addTenor (tenor).julian()) > targetDate) {
					loop = false;
				}
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		return continuousForwardVolatilityArray;
	}

	/**
	 * Multi-Factor Cross Volatility Integral
	 * 
	 * @param forwardDate1 Forward Date #1
	 * @param forwardDate2 Forward Date #2
	 * @param terminalDate The Terminal Date
	 * 
	 * @return The Multi-Factor Cross Volatility Integral
	 * 
	 * @throws Exception Thrown if the Multi-Factor Cross Volatility Integral cannot be computed
	 */

	public double crossVolatilityIntegralProduct (
		final int forwardDate1,
		final int forwardDate2,
		final int terminalDate)
		throws Exception
	{
		if (forwardDate1 < terminalDate || forwardDate2 < terminalDate) {
			throw new Exception (
				"LognormalLIBORVolatility::crossVolatilityIntegralProduct => Invalid Inputs"
			);
		}

		return new R1ToR1 (null) {
			@Override public double evaluate (
				final double dblDate)
				throws Exception
			{
				double crossVolProduct = 0.;

				for (int iFactorIndex = 0;
					iFactorIndex < principalFactorSequenceGenerator().numFactor();
					++iFactorIndex)
				{
					crossVolProduct += factorPointVolatility (iFactorIndex, (int) dblDate, forwardDate1)
						* factorPointVolatility (iFactorIndex, (int) dblDate, forwardDate2);
				}

				return crossVolProduct;
			}
		}.integrate (_spotDate, terminalDate);
	}
}
