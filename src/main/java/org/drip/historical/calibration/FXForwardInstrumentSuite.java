
package org.drip.historical.calibration;

import org.drip.product.params.CurrencyPair;

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
 * <i>FXForwardInstrumentSuite</i> contains the Configuration Settings to generate the FX Forward Calibration
 * 	Suite. It contains the following Functionality:
 *
 *  <ul>
 * 		<li><i>FXForwardInstrumentSuite</i> Constructor</li>
 * 		<li>Retrieve the Array of Tenors</li>
 * 		<li>Retrieve the Currency Pair</li>
 * 		<li>Retrieve the Calibration Component Count</li>
 *  </ul>
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationSupportLibrary.md">Computation Support</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/historical/README.md">Historical State Processing Utilities</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/historical/calibration/README.md">Calibration Instruments, Codes, and Quotes</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class FXForwardInstrumentSuite
	extends InstrumentSuite
{
	private String[] _tenorArray = null;
	private CurrencyPair _currencyPair = null;

	/**
	 * <i>FXForwardInstrumentSuite</i> Constructor
	 * 
	 * @param currencyPair Currency Pair
	 * @param calibrationMetric Deposit Calibration Metric
	 * @param tenorArray Array of Maturity Tenors
	 * 
	 * @throws Exception Thrown if Inputs are Invalid
	 */

	public FXForwardInstrumentSuite (
		final CurrencyPair currencyPair,
		final String calibrationMetric,
		final String[] tenorArray)
		throws Exception
	{
		super (currencyPair.code(), currencyPair.denomCcy(), calibrationMetric);

		if (null == (_tenorArray = tenorArray) || 0 == _tenorArray.length) {
			throw new Exception ("FXForwardInstrumentSuite Constructor => Invalid Inputs");
		}

		_currencyPair = currencyPair;
	}

	/**
	 * Retrieve the Array of Tenors
	 * 
	 * @return Array of Tenors
	 */

	public String[] tenorArray()
	{
		return _tenorArray;
	}

	/**
	 * Retrieve the Currency Pair
	 * 
	 * @return Currency Pair
	 */

	public CurrencyPair currencyPair()
	{
		return _currencyPair;
	}

	/**
	 * Retrieve the Calibration Component Count
	 * 
	 * @return Calibration Component Count
	 */

	public int componentCount()
	{
		return _tenorArray.length;
	}
}
