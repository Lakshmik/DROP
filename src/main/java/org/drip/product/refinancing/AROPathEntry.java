
package org.drip.product.refinancing;

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
 * <i>AROPathEntry</i> holds the Entries corresponding to the Single Path/Date Realizations incurred by the
 * 	ARO Process using Muni Sub-markets, i.e., Tax-exempt, and Taxable Path Yield Curves. The References are:
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
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/product/refinancing/README.md">Evaluation of Product Re-financing PnL</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class AROPathEntry
{
	private double _issueAccrual = Double.NaN;
	private double _escrowAccrual = Double.NaN;
	private double _refinancingCharge = Double.NaN;
	private double _refinancingIssuePrice = Double.NaN;
	private double _refinancingEscrowPrice = Double.NaN;
	private double _embeddedOptionExerciseIssuePrice = Double.NaN;
	private double _embeddedOptionExerciseEscrowPrice = Double.NaN;

	/**
	 * <i>AROPathEntry</i> Constructor
	 * 
	 * @param refinancingIssuePrice Issue Price at the Re-financing Date
	 * @param embeddedOptionExerciseIssuePrice ARO Embedded Option Exercise Issue Price
	 * @param issueAccrual Issue Accrual between the Re-financing and the Embedded Option Exercise Dates
	 * @param refinancingEscrowPrice Escrow Price at the Re-financing Date
	 * @param embeddedOptionExerciseEscrowPrice ARO Embedded Option Exercise Escrow Price
	 * @param escrowAccrual Escrow Accrual between the Re-financing and the Embedded Option Exercise Dates
	 * @param refinancingCharge Re-financing Charge
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public AROPathEntry (
		final double refinancingIssuePrice,
		final double embeddedOptionExerciseIssuePrice,
		final double issueAccrual,
		final double refinancingEscrowPrice,
		final double embeddedOptionExerciseEscrowPrice,
		final double escrowAccrual,
		final double refinancingCharge)
		throws Exception
	{
		if (!NumberUtil.IsValid (_refinancingIssuePrice = refinancingIssuePrice) ||
				0. > _refinancingIssuePrice ||
			!NumberUtil.IsValid (_embeddedOptionExerciseIssuePrice = embeddedOptionExerciseIssuePrice) ||
				0. > _embeddedOptionExerciseIssuePrice ||
			!NumberUtil.IsValid (_issueAccrual = issueAccrual) || 0. > _issueAccrual ||
			!NumberUtil.IsValid (_refinancingEscrowPrice = refinancingEscrowPrice) ||
				0. > _refinancingEscrowPrice ||
			!NumberUtil.IsValid (_embeddedOptionExerciseEscrowPrice = embeddedOptionExerciseEscrowPrice) ||
				0. > _embeddedOptionExerciseEscrowPrice ||
			!NumberUtil.IsValid (_escrowAccrual = escrowAccrual) || 0. > _escrowAccrual ||
			!NumberUtil.IsValid (_refinancingCharge = refinancingCharge) || 0. >= _refinancingCharge)
		{
			throw new Exception ("AROPathEntry Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Issue Price at the Re-financing Date
	 * 
	 * @return Issue Price at the Re-financing Date
	 */

	public double refinancingIssuePrice()
	{
		return _refinancingIssuePrice;
	}

	/**
	 * Retrieve the ARO Embedded Option Exercise Issue Price
	 * 
	 * @return ARO Embedded Option Exercise IssuePrice
	 */

	public double embeddedOptionExerciseIssuePrice()
	{
		return _embeddedOptionExerciseIssuePrice;
	}

	/**
	 * Retrieve the Issue Accrual between the Re-financing and the Embedded Option Exercise Dates
	 * 
	 * @return Issue Accrual between the Re-financing and the Embedded Option Exercise Dates
	 */

	public double issueAccrual()
	{
		return _issueAccrual;
	}

	/**
	 * Retrieve the Escrow Price at the Re-financing Date
	 * 
	 * @return Escrow Price at the Re-financing Date
	 */

	public double refinancingEscrowPrice()
	{
		return _refinancingEscrowPrice;
	}

	/**
	 * Retrieve the Escrow Price at the Embedded Option Exercise Date
	 * 
	 * @return Escrow Price at the Embedded Option Exercise Date
	 */

	public double embeddedOptionExerciseEscrowPrice()
	{
		return _embeddedOptionExerciseEscrowPrice;
	}

	/**
	 * Retrieve the Escrow Accrual between the Re-financing and the Embedded Option Exercise Dates
	 * 
	 * @return Escrow Accrual between the Re-financing and the Embedded Option Exercise Dates
	 */

	public double escrowAccrual()
	{
		return _escrowAccrual;
	}

	/**
	 * Retrieve the ARO Re-financing Charge
	 * 
	 * @return ARO Re-financing Charge
	 */

	public double refinancingCharge()
	{
		return _refinancingCharge;
	}

	/**
	 * Retrieve the PnL of the Issue Bond
	 * 
	 * @return PnL of the Issue Bond
	 */

	public double issuePnL()
	{
		return _refinancingIssuePrice - _issueAccrual - _embeddedOptionExerciseIssuePrice;
	}

	/**
	 * Retrieve the PnL of the Escrow Bond
	 * 
	 * @return PnL of the Escrow Bond
	 */

	public double escrowPnL()
	{
		return _escrowAccrual + _embeddedOptionExerciseEscrowPrice - _refinancingEscrowPrice;
	}

	/**
	 * Retrieve the Total Path PnL
	 * 
	 * @return Total Path PnL
	 */

	public double pnL()
	{
		return _refinancingIssuePrice - _issueAccrual - _embeddedOptionExerciseIssuePrice +
			_escrowAccrual + _embeddedOptionExerciseEscrowPrice - _refinancingEscrowPrice -
			_refinancingCharge;
	}
}
