
package org.drip.sample.securitysuite;

import org.drip.analytics.date.*;
import org.drip.analytics.daycount.*;
import org.drip.product.creator.BondBuilder;
import org.drip.product.credit.BondComponent;
import org.drip.service.env.EnvManager;
import org.drip.service.scenario.*;

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
 * <i>Berhampur</i> generates the Full Suite of Replication Metrics for Bond Berhampur.
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/securitysuite/README.md">Custom Security Relative Value Demonstration</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class Berhampur
{

	/**
	 * Entry Point
	 * 
	 * @param astArgs Command Line Argument Array
	 * 
	 * @throws Exception Thrown on Error/Exception Situation
	 */

	public static final void main (
		final String[] astArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		JulianDate spotDate = DateUtil.CreateFromYMD (2017, DateUtil.JULY, 10);

		String[] depositTenorArray = new String[]
		{
			"2D"
		};
		double[] depositQuoteArray = new double[]
		{
			0.0130411 // 2D
		};
		double[] futuresQuoteArray = new double[]
		{
			0.01345,	// 98.655
			0.01470,	// 98.530
			0.01575,	// 98.425
			0.01660,	// 98.340
			0.01745,    // 98.255
			0.01845     // 98.155
		};
		String[] fixFloatTenorArray = new String[]
		{
			"02Y",
			"03Y",
			"04Y",
			"05Y",
			"06Y",
			"07Y",
			"08Y",
			"09Y",
			"10Y",
			"11Y",
			"12Y",
			"15Y",
			"20Y",
			"25Y",
			"30Y",
			"40Y",
			"50Y"
		};
		String[] govvieTenorArray = new String[]
		{
			"1Y",
			"2Y",
			"3Y",
			"5Y",
			"7Y",
			"10Y",
			"20Y",
			"30Y"
		};
		double[] fixFloatQuoteArray = new double[]
		{
			0.016410, //  2Y
			0.017863, //  3Y
			0.019030, //  4Y
			0.020035, //  5Y
			0.020902, //  6Y
			0.021660, //  7Y
			0.022307, //  8Y
			0.022879, //  9Y
			0.023363, // 10Y
			0.023820, // 11Y
			0.024172, // 12Y
			0.024934, // 15Y
			0.025581, // 20Y
			0.025906, // 25Y
			0.025973, // 30Y
			0.025838, // 40Y
			0.025560  // 50Y
		};

		double[] adblGovvieYield = new double[] {
			0.01219, //  1Y
			0.01391, //  2Y
			0.01590, //  3Y
			0.01937, //  5Y
			0.02200, //  7Y
			0.02378, // 10Y
			0.02677, // 20Y
			0.02927  // 30Y
		};
		String[] creditTenorArray = new String[]
		{
			"06M",
			"01Y",
			"02Y",
			"03Y",
			"04Y",
			"05Y",
			"07Y",
			"10Y"
		};
		double[] creditQuoteArray = new double[]
		{
			 60.,	//  6M
			 68.,	//  1Y
			 88.,	//  2Y
			102.,	//  3Y
			121.,	//  4Y
			138.,	//  5Y
			168.,	//  7Y
			188.	// 10Y
		};
		double fx = 1.;
		int settleLag = 3;
		int fixedFrequency = 2;
		int floatFrequency = 4;
		String name = "Berhampur";
		double cleanPrice = 1.;
		double issuePrice = 1.;
		String currency = "USD";
		double spreadBump = 20.;
		double fixedCoupon = 0.0625;
		double issueAmount = 3.6e08;
		String treasuryCode = "UST";
		String floatIndex = "USD-3M";
		double floatSpread = 0.03899;
		String fixedDayCount = "30/360";
		double spreadDurationMultiplier = 5.;

		JulianDate effectiveDate = DateUtil.CreateFromYMD (2017, DateUtil.FEBRUARY, 28);

		JulianDate fixedFirstCouponDate = DateUtil.CreateFromYMD (2017, DateUtil.AUGUST, 28);

		JulianDate fixedEndDate = DateUtil.CreateFromYMD (2027, DateUtil.AUGUST, 28);

		JulianDate penultimateFloatCouponDate = DateUtil.CreateFromYMD (2056, DateUtil.AUGUST, 28);

		JulianDate maturityDate = DateUtil.CreateFromYMD (2057, DateUtil.FEBRUARY, 28);

		JulianDate penultimateFixedCouponDate = DateUtil.CreateFromYMD (2027, DateUtil.FEBRUARY,28);

		JulianDate firstFloatCouponDate = DateUtil.CreateFromYMD (2028, DateUtil.FEBRUARY, 28);

		BondComponent bond = BondBuilder.FixedFPToFloatFP (
			name,
			name,
			effectiveDate.julian(),
			fixedEndDate.julian(),
			fixedFirstCouponDate.julian(),
			penultimateFixedCouponDate.julian(),
			fixedFrequency,
			fixedCoupon,
			fixedDayCount,
			fixedDayCount,
			maturityDate.julian(),
			firstFloatCouponDate.julian(),
			penultimateFloatCouponDate.julian(),
			floatFrequency,
			floatSpread,
			floatIndex,
			new DateAdjustParams (Convention.DATE_ROLL_FOLLOWING, 0, currency),
			null,
			null,
			null,
			null,
			null,
			null,
			null
		);

		BondReplicator bondReplicator = BondReplicator.CorporateSenior (
			cleanPrice,
			issuePrice,
			issueAmount,
			spotDate,
			depositTenorArray,
			depositQuoteArray,
			futuresQuoteArray,
			fixFloatTenorArray,
			fixFloatQuoteArray,
			spreadBump,
			spreadDurationMultiplier,
			treasuryCode,
			govvieTenorArray,
			adblGovvieYield,
			creditTenorArray,
			creditQuoteArray,
			fx,
			Double.NaN,
			settleLag,
			bond
		);

		System.out.println (bondReplicator.generateRun().display());

		EnvManager.TerminateEnv();
	}
}
