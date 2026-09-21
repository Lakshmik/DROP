
package org.drip.sample.fixfloat;

import org.drip.market.otc.IBORFixedFloatContainer;
import org.drip.service.env.EnvManager;

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
 * <i>JurisdictionOTCIndexDefinitions</i> contains all the pre-fixed definitions of the Jurisdiction-specific
 * 	OTC Fix-Float IRS contracts.
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/fixfloat/README.md">Coupon, Floater, Amortizing IRS Variants</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class JurisdictionOTCIndexDefinitions
{

	private static final void DisplayIRSOTCInfo (
		String currency,
		String location,
		String maturityTenor,
		String index)
	{
		System.out.println (
			"\t|| " + currency + "-" + location + "-" + maturityTenor + "-" + index + " => " +
				IBORFixedFloatContainer.ConventionFromJurisdiction (currency, location, maturityTenor, index)
		);
	}

	/**
	 * Entry Point
	 * 
	 * @param argumentArray Command Line Argument Array
	 */

	public static final void main (
		final String[] argumentArray)
	{
		EnvManager.InitEnv ("");

		System.out.println (
			"\n\t||--------------------------------------------------------------------------------------------------------"
		);

		DisplayIRSOTCInfo ("AUD", "ALL", "1Y", "MAIN");

		DisplayIRSOTCInfo ("AUD", "ALL", "5Y", "MAIN");

		DisplayIRSOTCInfo ("BRL", "ALL", "1Y", "MAIN");

		DisplayIRSOTCInfo ("BRL", "ALL", "5Y", "MAIN");

		DisplayIRSOTCInfo ("CAD", "ALL", "1Y", "MAIN");

		DisplayIRSOTCInfo ("CAD", "ALL", "5Y", "MAIN");

		DisplayIRSOTCInfo ("CHF", "ALL", "1Y", "MAIN");

		DisplayIRSOTCInfo ("CHF", "ALL", "5Y", "MAIN");

		DisplayIRSOTCInfo ("CNY", "ALL", "1Y", "MAIN");

		DisplayIRSOTCInfo ("CNY", "ALL", "5Y", "MAIN");

		DisplayIRSOTCInfo ("DKK", "ALL", "1Y", "MAIN");

		DisplayIRSOTCInfo ("DKK", "ALL", "5Y", "MAIN");

		DisplayIRSOTCInfo ("EUR", "ALL", "1Y", "MAIN");

		DisplayIRSOTCInfo ("EUR", "ALL", "5Y", "MAIN");

		DisplayIRSOTCInfo ("GBP", "ALL", "1Y", "MAIN");

		DisplayIRSOTCInfo ("GBP", "ALL", "5Y", "MAIN");

		DisplayIRSOTCInfo ("HKD", "ALL", "1Y", "MAIN");

		DisplayIRSOTCInfo ("HKD", "ALL", "5Y", "MAIN");

		DisplayIRSOTCInfo ("INR", "ALL", "1Y", "MAIN");

		DisplayIRSOTCInfo ("INR", "ALL", "5Y", "MAIN");

		DisplayIRSOTCInfo ("JPY", "ALL", "1Y", "MAIN");

		DisplayIRSOTCInfo ("JPY", "ALL", "5Y", "MAIN");

		DisplayIRSOTCInfo ("JPY", "ALL", "1Y", "TIBOR");

		DisplayIRSOTCInfo ("JPY", "ALL", "5Y", "TIBOR");

		DisplayIRSOTCInfo ("KRW", "ALL", "1Y", "MAIN");

		DisplayIRSOTCInfo ("KRW", "ALL", "5Y", "MAIN");

		DisplayIRSOTCInfo ("MYR", "ALL", "1Y", "MAIN");

		DisplayIRSOTCInfo ("MYR", "ALL", "5Y", "MAIN");

		DisplayIRSOTCInfo ("NOK", "ALL", "1Y", "MAIN");

		DisplayIRSOTCInfo ("NOK", "ALL", "5Y", "MAIN");

		DisplayIRSOTCInfo ("NZD", "ALL", "1Y", "MAIN");

		DisplayIRSOTCInfo ("NZD", "ALL", "5Y", "MAIN");

		DisplayIRSOTCInfo ("PLN", "ALL", "1Y", "MAIN");

		DisplayIRSOTCInfo ("PLN", "ALL", "5Y", "MAIN");

		DisplayIRSOTCInfo ("SEK", "ALL", "1Y", "MAIN");

		DisplayIRSOTCInfo ("SEK", "ALL", "5Y", "MAIN");

		DisplayIRSOTCInfo ("SGD", "ALL", "1Y", "MAIN");

		DisplayIRSOTCInfo ("SGD", "ALL", "5Y", "MAIN");

		DisplayIRSOTCInfo ("THB", "ALL", "1Y", "MAIN");

		DisplayIRSOTCInfo ("THB", "ALL", "5Y", "MAIN");

		DisplayIRSOTCInfo ("TWD", "ALL", "1Y", "MAIN");

		DisplayIRSOTCInfo ("TWD", "ALL", "5Y", "MAIN");

		DisplayIRSOTCInfo ("USD", "LON", "1Y", "MAIN");

		DisplayIRSOTCInfo ("USD", "LON", "5Y", "MAIN");

		DisplayIRSOTCInfo ("USD", "NYC", "1Y", "MAIN");

		DisplayIRSOTCInfo ("USD", "NYC", "5Y", "MAIN");

		DisplayIRSOTCInfo ("ZAR", "ALL", "1Y", "MAIN");

		DisplayIRSOTCInfo ("ZAR", "ALL", "5Y", "MAIN");

		System.out.println (
			"\t||--------------------------------------------------------------------------------------------------------"
		);

		EnvManager.TerminateEnv();
	}
}
