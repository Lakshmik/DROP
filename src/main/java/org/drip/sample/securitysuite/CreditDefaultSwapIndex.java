
package org.drip.sample.securitysuite;

import java.util.Map;

import org.drip.analytics.cashflow.CompositePeriod;
import org.drip.analytics.date.*;
import org.drip.param.creator.MarketParamsBuilder;
import org.drip.param.pricer.CreditPricerParams;
import org.drip.param.valuation.ValuationParams;
import org.drip.product.creator.CDSBuilder;
import org.drip.product.definition.CreditDefaultSwap;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.service.template.LatentMarketStateBuilder;
import org.drip.state.credit.CreditCurve;
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
 * <i>CreditDefaultSwapIndex</i> demonstrates the Analytics Calculation/Reconciliation for a CDX.
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

public class CreditDefaultSwapIndex
{

	private static final MergedDiscountForwardCurve FundingCurve (
		final JulianDate spotDate,
		final String currency,
		final double bump)
		throws Exception
	{
		return LatentMarketStateBuilder.SmoothFundingCurve (
			spotDate,
			currency,
			new String[]
			{
				"2D"
			},
			new double[]
			{
				0.013161 + bump // 2D
			},
			"ForwardRate",
			 new double[]
			{
				0.013225 + bump,	// 98.6775
				0.014250 + bump,	// 98.5750
				0.014750 + bump,	// 98.5250
				0.015250 + bump,	// 98.4750
				0.015750 + bump,  	// 98.4250
				0.016500 + bump   	// 98.3500
			},
			"ForwardRate",
			new String[]
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
			},
			new double[]
			{
				0.015540 + bump, //  2Y
				0.016423 + bump, //  3Y
				0.017209 + bump, //  4Y
				0.017980 + bump, //  5Y
				0.018743 + bump, //  6Y
				0.019455 + bump, //  7Y
				0.020080 + bump, //  8Y
				0.020651 + bump, //  9Y
				0.021195 + bump, // 10Y
				0.021651 + bump, // 11Y
				0.022065 + bump, // 12Y
				0.022952 + bump, // 15Y
				0.023825 + bump, // 20Y
				0.024175 + bump, // 25Y
				0.024347 + bump, // 30Y
				0.024225 + bump, // 40Y
				0.023968 + bump  // 50Y
			},
			"SwapRate"
		);
	}

	private static final CreditCurve CreditCurve (
		final JulianDate spotDate,
		final String creditCurve,
		final MergedDiscountForwardCurve mergedDiscountForwardCurve,
		final double bump)
		throws Exception
	{
		return LatentMarketStateBuilder.CreditCurve (
			spotDate,
			creditCurve,
			new String[]
			{
				 "6M",
				 "1Y",
				 "2Y",
				 "3Y",
				 "4Y",
				 "5Y",
				 "7Y",
				"10Y",
				"20Y",
				"30Y",
			},
			new double[]
			{
				392.509,	//  6M
				320.707,	//  1Y
				393.624,	//  2Y
				472.869,	//  3Y
				570.360,	//  4Y
				663.920,	//  5Y
				779.463,	//  7Y
				957.555, 	// 10Y
				908.712, 	// 20Y
				900.297, 	// 30Y
			},
			new double[]
			{
				392.509,	//  6M
				320.707,	//  1Y
				393.624,	//  2Y
				472.869,	//  3Y
				570.360,	//  4Y
				663.920,	//  5Y
				779.463,	//  7Y
				957.555, 	// 10Y
				908.712, 	// 20Y
				900.297, 	// 30Y
			},
			"FairPremium",
			mergedDiscountForwardCurve
		);
	}

	/**
	 * Entry Point
	 * 
	 * @param argumentArray Command Line Argument Array
	 * 
	 * @throws Exception Thrown on Error/Exception Situation
	 */

	public static final void main (
		final String[] argumentArray)
		throws Exception
	{
		EnvManager.InitEnv ("");

		JulianDate spotDate = DateUtil.CreateFromYMD (2017, DateUtil.SEPTEMBER, 17);

		JulianDate issueDate = DateUtil.CreateFromYMD (2017, DateUtil.JUNE, 20);

		String cdxTenor = "5Y";
		String currency = "USD";
		String cdxName = "CDXNAHY";
		double cdxFixedCoupon = 0.05;

		CreditDefaultSwap cdx = CDSBuilder.CreateSNAC (issueDate, cdxTenor, cdxFixedCoupon, cdxName);

		MergedDiscountForwardCurve discountCurve = FundingCurve (spotDate, currency, 0.);

		CreditCurve creditCurve = CreditCurve (spotDate, cdxName, discountCurve, 0.);

		System.out.println ("");

		System.out.println ("\t |-----------------------------------------------|");

		for (Map.Entry<String, Double> mapEntry : cdx.value (
				new ValuationParams (spotDate, spotDate, currency),
				CreditPricerParams.Standard(),
				MarketParamsBuilder.Credit (discountCurve, creditCurve),
				null
			).entrySet()
		)
		{
			System.out.println ("\t | " + mapEntry.getKey() + " => " + mapEntry.getValue());
		}

		System.out.println ("\t |-----------------------------------------------|");

		System.out.println ("");

		System.out.println (
			"\t |---------------------------------------------------------------------------||"
		);

		for (CompositePeriod compositePeriod : cdx.couponPeriods()) {
			System.out.println (
				"\t | " + DateUtil.YYYYMMDD (compositePeriod.startDate()) + " | " +
				DateUtil.YYYYMMDD (compositePeriod.endDate()) + " | " +
				DateUtil.YYYYMMDD (compositePeriod.payDate()) + " | " +
				FormatUtil.FormatDouble (compositePeriod.couponDCF(), 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (compositePeriod.couponDCF(), 1, 2, 0.01 * 1.) + " | " +
				FormatUtil.FormatDouble (discountCurve.df (compositePeriod.payDate()), 1, 4, 1.) + " | " +
				FormatUtil.FormatDouble (creditCurve.survival (compositePeriod.payDate()), 1, 4, 1.) + " ||"
			);
		}

		System.out.println (
			"\t |---------------------------------------------------------------------------||"
		);

		EnvManager.TerminateEnv();
	}
}
