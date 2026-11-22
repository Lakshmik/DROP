
package org.drip.state.municipal;

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
 * <i>SegmentElastic</i> holds the Segment's Inferred Path Metrics. The References are:
 *  
 * 	<br><br>
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

public class SegmentElastic
{
	private double _forwardYield = Double.NaN;
	private SegmentInelastic _inelastic = null;
	private double _endDiscountFactor = Double.NaN;
	private double _forwardDiscountFactor = Double.NaN;

	/**
	 * <i>SegmentElastic</i> Constructor
	 * 
	 * @param inelastic Underlying <i>SegmentInelastic</i> Instance
	 * @param forwardYield Forward Yield
	 * @param forwardDiscountFactor Forward Discount Factor
	 * @param endDiscountFactor Segment End Discount Factor
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public SegmentElastic (
		final SegmentInelastic inelastic,
		final double forwardYield,
		final double forwardDiscountFactor,
		final double endDiscountFactor)
		throws Exception
	{
		if (null == (_inelastic = inelastic) ||
			!NumberUtil.IsValid (_forwardYield = forwardYield) || 
			!NumberUtil.IsValid (_forwardDiscountFactor = forwardDiscountFactor) ||
				0. > _forwardDiscountFactor || 
			!NumberUtil.IsValid (_endDiscountFactor = endDiscountFactor) || 0. > _endDiscountFactor)
		{
			throw new Exception ("SegmentElastic Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the <i>SegmentInelastic</i> Instance
	 * 
	 * @return <i>SegmentInelastic</i> Instance
	 */

	public SegmentInelastic inelastic()
	{
		return _inelastic;
	}

	/**
	 * Retrieve the Forward Yield
	 * 
	 * @return Forward Yield
	 */

	public double forwardYield()
	{
		return _forwardYield;
	}

	/**
	 * Retrieve the Forward Discount Factor
	 * 
	 * @return Forward Discount Factor
	 */

	public double forwardDiscountFactor()
	{
		return _forwardDiscountFactor;
	}

	/**
	 * Retrieve Segment End Discount Factor
	 * 
	 * @return Segment End Discount Factor
	 */

	public double endDiscountFactor()
	{
		return _endDiscountFactor;
	}

	/**
	 * Retrieve the Segment Begin Discount Factor
	 * 
	 * @return Segment Begin Discount Factor
	 */

	public double beginDiscountFactor()
	{
		return _endDiscountFactor / _forwardDiscountFactor;
	}

	/**
	 * Clone the Instance
	 * 
	 * @return The Cloned Instance
	 */

	@Override public SegmentElastic clone()
	{
		try {
			return new SegmentElastic (
				_inelastic.clone(),
				_forwardYield,
				_forwardDiscountFactor,
				_endDiscountFactor
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate a String Version of the State
	 * 
	 * @param prefix Prefix
	 * 
	 * @return String Version of the State
	 */

	public String toString (
		final String prefix)
	{
		return prefix + "Segment Inelastic => " + _inelastic +
			"; Forward Yield => " + FormatUtil.FormatDouble (_forwardYield, 1, 6, 1.) +
			"; Forward Discount Factor => " + FormatUtil.FormatDouble (_forwardDiscountFactor, 1, 6, 1.) +
			"; Segment End Discount Factor => " + FormatUtil.FormatDouble (_endDiscountFactor, 1, 6, 1.);
	}

	/**
	 * Generate a String Version of the State
	 * 
	 * @return String Version of the State
	 */

	@Override public String toString()
	{
		return toString ("");
	}
}
