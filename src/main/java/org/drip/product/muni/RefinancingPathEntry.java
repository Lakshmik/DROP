
package org.drip.product.muni;

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
 * <i>RefinancingPathEntry</i> contains the Entries corresponding to the Single Path/Date Bond PnL incurred
 *  by re-financing using Muni Sub-markets, i.e., Tax-exempt, and Taxable Path Yield Curves. The References
 *  are:
 *
 *  <br><br>
 *  <ul>
 *  	<li>
 *  		Ang, A., R. C. Green, and Y. Xing (2013): <i>Advance Re-fundings of Municipal Bonds</i>
 *  			https://www.nber.org/papers/w19459
 *  	</li>
 *  	<li>
 *  		de Guillaume, N., R. Rebonato, and A. Pogudin (2013): The Nature of the Dependence of the
 *  			Magnitude of the Rates Moves on the Rates Levels: A Universal Relationship <i>Quantitative
 *  			Finance</i> <b>13 (3)</b> 351-367
 *  	</li>
 *  	<li>
 *  		Orr, P., and D. de la Nuez (2013): <i>The Right and Wrong Models for Evaluating Callable
 *  			Municipal Bonds</i> <b>eSSRN</b>
 *  	</li>
 *  	<li>
 *  		Rebonato, R. (2003): <i>Term Structure Models: A Review</i>
 *  			https://dept.math.lsa.umich.edu/~conlon/math623/rebonato_review.pdf
 *  	</li>
 *  	<li>
 *  		Rebonato, R., and S. K. Nawalkha (2011): <i>What Interest Rate Models to Use? Buy Side versus
 *  			Sell Side</i> <b>eSSRN</b>
 *  	</li>
 *  </ul>
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/product/README.md">Product Components/Baskets for Credit, FRA, FX, Govvie, Rates, and Option Asset Classes</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/product/muni/README.md">Refunding and Optimal Exercise Mechanics</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class RefinancingPathEntry
{
	private double _taxableGovvieCurveCleanPrice = Double.NaN;
	private double _taxExemptGovvieCurveCleanPrice = Double.NaN;
	private double _initialTaxableGovvieCurveCleanPrice = Double.NaN;
	private double _initialTaxExemptGovvieCurveCleanPrice = Double.NaN;

	/**
	 * <i>RefinancingPathEntry</i> Constructor
	 * 
	 * @param initialTaxExemptGovvieCurveCleanPrice Price generated from the Initial Tax-exempt Govvie Clean
	 * 												Curve
	 * @param initialTaxableGovvieCurveCleanPrice Price generated from the Initial Taxable Govvie Clean Curve
	 * @param taxExemptGovvieCurveCleanPrice Price generated from the Tax-exempt Govvie Clean Curve
	 * @param taxableGovvieCurveCleanPrice Price generated from the Taxable Govvie Clean Curve
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public RefinancingPathEntry (
		final double initialTaxExemptGovvieCurveCleanPrice,
		final double initialTaxableGovvieCurveCleanPrice,
		final double taxExemptGovvieCurveCleanPrice,
		final double taxableGovvieCurveCleanPrice)
		throws Exception
	{
		if (!NumberUtil.IsValid (
				_initialTaxExemptGovvieCurveCleanPrice = initialTaxExemptGovvieCurveCleanPrice
			) || 0. > _initialTaxExemptGovvieCurveCleanPrice ||
			!NumberUtil.IsValid (
				_initialTaxableGovvieCurveCleanPrice = initialTaxableGovvieCurveCleanPrice
			) || 0. > _initialTaxableGovvieCurveCleanPrice ||
			!NumberUtil.IsValid (_taxExemptGovvieCurveCleanPrice = taxExemptGovvieCurveCleanPrice) ||
				0. > _taxExemptGovvieCurveCleanPrice ||
			!NumberUtil.IsValid (_taxableGovvieCurveCleanPrice = taxableGovvieCurveCleanPrice) ||
				0. > _taxableGovvieCurveCleanPrice)
		{
			throw new Exception ("RefinancingPathEntry Constructor => Invalid Inputs");
		}
	}

	/**
	 * Return the Price generated from the Initial Tax-exempt Govvie Clean Curve
	 * 
	 * @return Price generated from the Initial Tax-exempt Govvie Clean Curve
	 */

	public double initialTaxExemptGovvieCurveCleanPrice()
	{
		return _initialTaxExemptGovvieCurveCleanPrice;
	}

	/**
	 * Return the Price generated from the Initial Taxable Govvie Clean Curve
	 * 
	 * @return Price generated from the Initial Taxable Govvie Clean Curve
	 */

	public double initialTaxableGovvieCurveCleanPrice()
	{
		return _initialTaxableGovvieCurveCleanPrice;
	}

	/**
	 * Return the Price generated from the Tax-exempt Govvie Clean Curve
	 * 
	 * @return Price generated from the Tax-exempt Govvie Clean Curve
	 */

	public double taxExemptGovvieCurveCleanPrice()
	{
		return _taxExemptGovvieCurveCleanPrice;
	}

	/**
	 * Return the Price generated from the Taxable Govvie Clean Curve
	 * 
	 * @return Price generated from the Taxable Govvie Clean Curve
	 */

	public double taxableGovvieCurveCleanPrice()
	{
		return _taxableGovvieCurveCleanPrice;
	}

	/**
	 * Retrieve the Tax-exempt PnL
	 * 
	 * @return Tax-exempt PnL
	 */

	public double taxExemptPnL()
	{
		return _taxExemptGovvieCurveCleanPrice - _initialTaxExemptGovvieCurveCleanPrice;
	}

	/**
	 * Retrieve the Taxable PnL
	 * 
	 * @return Taxable PnL
	 */

	public double taxablePnL()
	{
		return _taxableGovvieCurveCleanPrice - _initialTaxableGovvieCurveCleanPrice;
	}
}
