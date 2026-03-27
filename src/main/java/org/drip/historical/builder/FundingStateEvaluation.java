
package org.drip.historical.builder;

import org.drip.historical.calibration.FundingMarkSuite;
import org.drip.product.rates.FixFloatComponent;
import org.drip.product.rates.SingleStreamComponent;
import org.drip.state.discount.MergedDiscountForwardCurve;

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
 * <i>FundingStateEvaluation</i> contains the Instruments and the Funding State evaluated at a Time Node,
 *  i.e., SOD/EOD. It provides the following Functionality:
 *
 *  <ul>
 * 		<li><i>FundingStateEvaluation</i> Constructor</li>
 * 		<li>Retrieve the Calibrated Funding State</li>
 * 		<li>Retrieve the Array of Deposit Components</li>
 * 		<li>Retrieve the Array of Futures Components</li>
 * 		<li>Retrieve the Array of Fix-Float Components</li>
 * 		<li>Retrieve the Suite of Funding Instrument Calibration Quotes</li>
 *  </ul>
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationSupportLibrary.md">Computation Support</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/historical/README.md">Historical State Processing Utilities</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/historical/builder/README.md">Latent State T1/T2 Builders</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class FundingStateEvaluation
{
	private FundingMarkSuite _fundingMarkSuite = null;
	private FixFloatComponent[] _fixFloatComponentArray = null;
	private SingleStreamComponent[] _depositComponentArray = null;
	private SingleStreamComponent[] _futuresComponentArray = null;
	private MergedDiscountForwardCurve _mergedDiscountForwardCurve = null;

	/**
	 * <i>FundingStateEvaluation</i> Constructor
	 * 
	 * @param mergedDiscountForwardCurve Calibrated Funding State
	 * @param depositComponentArray Array of Deposit Components
	 * @param futuresComponentArray Array of Futures Components
	 * @param fixFloatComponentArray Array of Fix-Float Components
	 * @param fundingMarkSuite Suite of Funding Instrument Calibration Marks
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public FundingStateEvaluation (
		final MergedDiscountForwardCurve mergedDiscountForwardCurve,
		final SingleStreamComponent[] depositComponentArray,
		final SingleStreamComponent[] futuresComponentArray,
		final FixFloatComponent[] fixFloatComponentArray,
		final FundingMarkSuite fundingMarkSuite)
		throws Exception
	{
		if (null == (_mergedDiscountForwardCurve = mergedDiscountForwardCurve) ||
			null == (_fundingMarkSuite = fundingMarkSuite))
		{
			throw new Exception ("FundingStateEvaluation Constructor => Invalid Inputs");
		}

		_depositComponentArray = depositComponentArray;
		_futuresComponentArray = futuresComponentArray;
		_fixFloatComponentArray = fixFloatComponentArray;

		if (null == _depositComponentArray &&
			null == _fixFloatComponentArray &&
			null == _futuresComponentArray)
		{
			throw new Exception ("FundingStateEvaluation Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Calibrated Funding State
	 * 
	 * @return Calibrated Funding State
	 */

	public MergedDiscountForwardCurve mergedDiscountForwardCurve()
	{
		return _mergedDiscountForwardCurve;
	}

	/**
	 * Retrieve the Array of Deposit Components
	 * 
	 * @return Array of Deposit Components
	 */

	public SingleStreamComponent[] depositComponentArray()
	{
		return _depositComponentArray;
	}

	/**
	 * Retrieve the Array of Futures Components
	 * 
	 * @return Array of Futures Components
	 */

	public SingleStreamComponent[] futuresComponentArray()
	{
		return _futuresComponentArray;
	}

	/**
	 * Retrieve the Array of Fix-Float Components
	 * 
	 * @return Array of Fix-Float Components
	 */

	public FixFloatComponent[] fixFloatComponentArray()
	{
		return _fixFloatComponentArray;
	}

	/**
	 * Retrieve the Suite of Funding Instrument Calibration Marks
	 * 
	 * @return Suite of Funding Instrument Calibration Marks
	 */

	public FundingMarkSuite fundingMarkSuite()
	{
		return _fundingMarkSuite;
	}
}
