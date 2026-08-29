
package org.drip.state.municipal;

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
 * <i>ProxyBondSensitivity</i> contains the Duration and the Convexity of a Bond using the Kalotay, Williams,
 *  and Fabozzi (1993) Tree-based Model for valuing bonds with Embedded Options. The References are:
 *  
 * 	<br>
 *  <ul>
 * 		<li>
 * 			Black, F., E. Derman, and W. Toy (1990): A One-Factor Model of Interest Rates and Its Application
 * 				to Treasury Bond Options <i>Financial Analysis Journal</i> <b>46 (1)</b> 33-39
 * 		</li>
 * 		<li>
 * 			Hull, J. and A. White (1990a): Valuing Derivative Securities Using the Explicit Finite Difference
 * 				Method <i>Journal of Financial and Quantitative Analysis</i> <b>25 (1)</b> 87-100
 * 		</li>
 * 		<li>
 * 			Hull, J. and A. White (1990b): Pricing Interest-Rate-Derivative Securities <i>Review of Financial
 * 				Studies</i> <b>3 (4)</b> 573-592
 * 		</li>
 * 		<li>
 * 			Kalotay, A. J. and G. O. Williams (1992): The Valuation and Management of Bonds with Sinking Fund
 * 				Provisions <i>Financial Analysis Journal</i> <b>48 (2)</b> 59-67
 * 		</li>
 * 		<li>
 * 			Kalotay, A. J., G. O. Williams, and F. J. Fabozzi (1993): A Model for Valuing Bonds and Embedded
 * 				Options <i>Financial Analysis Journal</i> <b>49 (3)</b> 35-46
 * 		</li>
 *  </ul>
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/state/README.md">Latent State Inference and Creation Utilities</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/state/municipal/README.md">Municipal Latent State Curve Estimator</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class ProxyBondSensitivity
{
	private double _duration = Double.NaN;
	private double _convexity = Double.NaN;
	private double _yieldBasis = Double.NaN;
	private double _marketPrice = Double.NaN;
	private double _theoreticalPrice = Double.NaN;

	/**
	 * Construct the <i>ProxyBondSensitivity</i> Instance from the Market and the Yield Bump Up/Down Prices
	 * 
	 * @param theoreticalPrice Theoretical Price
	 * @param marketPrice Market Price
	 * @param yieldBasis Yield Basis
	 * @param bumpUpMarketPrice Yield Bumped Up Market Price
	 * @param bumpDownMarketPrice Yield Bumped Down Market Price
	 * @param yieldBump Yield Bump
	 * 
	 * @return <i>ProxyBondSensitivity</i> Instance
	 */

	public static final ProxyBondSensitivity FromBaseUpDown (
		final double theoreticalPrice,
		final double marketPrice,
		final double yieldBasis,
		final double bumpUpMarketPrice,
		final double bumpDownMarketPrice,
		final double yieldBump)
	{
		if (!NumberUtil.IsValid (marketPrice) ||
			!NumberUtil.IsValid (bumpUpMarketPrice) ||
			!NumberUtil.IsValid (bumpDownMarketPrice) ||
			!NumberUtil.IsValid (yieldBump) || 0. == yieldBump)
		{
			return null;
		}

		double yieldBumpReciprocal = 1. / yieldBump;

		try {
			return NumberUtil.IsValid (marketPrice) &&
				NumberUtil.IsValid (bumpUpMarketPrice) &&
				NumberUtil.IsValid (bumpDownMarketPrice) &&
				NumberUtil.IsValid (yieldBump) && 0. != yieldBump ?
				new ProxyBondSensitivity (
					theoreticalPrice,
					marketPrice,
					yieldBasis,
					0.5 * (bumpDownMarketPrice - bumpUpMarketPrice) * yieldBumpReciprocal,
					(bumpDownMarketPrice + bumpUpMarketPrice - 2. * marketPrice) * yieldBumpReciprocal *
						yieldBumpReciprocal
				) : null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * <i>ProxyBondSensitivity</i> Constructor
	 * 
	 * @param theoreticalPrice Theoretical Price
	 * @param marketPrice Market Price
	 * @param yieldBasis Yield Basis
	 * @param duration Duration
	 * @param convexity Convexity
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public ProxyBondSensitivity (
		final double theoreticalPrice,
		final double marketPrice,
		final double yieldBasis,
		final double duration,
		final double convexity)
		throws Exception
	{
		if (!NumberUtil.IsValid (_theoreticalPrice = theoreticalPrice) || 0. > _theoreticalPrice ||
			!NumberUtil.IsValid (_marketPrice = marketPrice) || 0. > _marketPrice ||
			!NumberUtil.IsValid (_yieldBasis = yieldBasis) ||
			!NumberUtil.IsValid (_duration = duration) ||
			!NumberUtil.IsValid (_convexity = convexity))
		{
			throw new Exception ("ProxyBondSensitivity Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Theoretical Price
	 * 
	 * @return Theoretical Price
	 */

	public double theoreticalPrice()
	{
		return _theoreticalPrice;
	}

	/**
	 * Retrieve the Market Price
	 * 
	 * @return Market Price
	 */

	public double marketPrice()
	{
		return _marketPrice;
	}

	/**
	 * Retrieve the Yield Basis
	 * 
	 * @return Yield Basis
	 */

	public double yieldBasis()
	{
		return _yieldBasis;
	}

	/**
	 * Retrieve the Duration
	 * 
	 * @return Duration
	 */

	public double duration()
	{
		return _duration;
	}

	/**
	 * Retrieve the Convexity
	 * 
	 * @return Convexity
	 */

	public double convexity()
	{
		return _convexity;
	}
}
