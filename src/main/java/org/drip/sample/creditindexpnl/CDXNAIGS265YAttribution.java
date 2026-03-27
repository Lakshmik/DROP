
package org.drip.sample.creditindexpnl;

import java.util.List;

import org.drip.analytics.date.JulianDate;
import org.drip.feed.loader.*;
import org.drip.historical.attribution.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.service.product.CreditIndexAPI;

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
 * <i>CDXNAIGS265YAttribution</i> contains the Functionality associated with the Attribution of the CDX NA IG
 * 	5Y S26 Index.
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/sample/creditindexpnl/README.md">CDX NA IG PnL Attribution</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class CDXNAIGS265YAttribution
{

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

		int series = 26;
		int horizonGap = 1;
		String closesLocation = "C:\\DROP\\Daemons\\Transforms\\CreditCDXMarks\\CDXNAIGS" + series +
			"5YReconstitutor.csv";
		String[] fundingFixingMaturityTenorArray = new String[] {
			"1Y",
			"2Y",
			"3Y",
			"4Y",
			"5Y",
			"6Y",
			"7Y",
			"8Y",
			"9Y",
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

		CSVGrid csvGrid = CSVParser.StringGrid (closesLocation, true);

		JulianDate[] closeDateArray = csvGrid.dateArrayAtColumn (0);

		double[] fundingFixingQuote1Y = csvGrid.doubleArrayAtColumn (1);

		double[] fundingFixingQuote2Y = csvGrid.doubleArrayAtColumn (2);

		double[] fundingFixingQuote3Y = csvGrid.doubleArrayAtColumn (3);

		double[] fundingFixingQuote4Y = csvGrid.doubleArrayAtColumn (4);

		double[] fundingFixingQuote5Y = csvGrid.doubleArrayAtColumn (5);

		double[] fundingFixingQuote6Y = csvGrid.doubleArrayAtColumn (6);

		double[] fundingFixingQuote7Y = csvGrid.doubleArrayAtColumn (7);

		double[] fundingFixingQuote8Y = csvGrid.doubleArrayAtColumn (8);

		double[] fundingFixingQuote9Y = csvGrid.doubleArrayAtColumn (9);

		double[] fundingFixingQuote10Y = csvGrid.doubleArrayAtColumn (10);

		double[] fundingFixingQuote11Y = csvGrid.doubleArrayAtColumn (11);

		double[] fundingFixingQuote12Y = csvGrid.doubleArrayAtColumn (12);

		double[] fundingFixingQuote15Y = csvGrid.doubleArrayAtColumn (13);

		double[] fundingFixingQuote20Y = csvGrid.doubleArrayAtColumn (14);

		double[] fundingFixingQuote25Y = csvGrid.doubleArrayAtColumn (15);

		double[] fundingFixingQuote30Y = csvGrid.doubleArrayAtColumn (16);

		double[] fundingFixingQuote40Y = csvGrid.doubleArrayAtColumn (17);

		double[] fundingFixingQuote50Y = csvGrid.doubleArrayAtColumn (18);

		String[] fullCreditIndexNameArray = csvGrid.stringArrayAtColumn (19);

		double[] creditIndexQuotedSpreadArray = csvGrid.doubleArrayAtColumn (20);

		int closeDateCount = closeDateArray.length;
		JulianDate[] spotDateArray = new JulianDate[closeDateCount];
		double[][] fundingFixingQuoteGrid = new double[closeDateCount][18];

		for (int closeDateIndex = 0; closeDateIndex < closeDateCount; ++closeDateIndex) {
			spotDateArray[closeDateIndex] = closeDateArray[closeDateIndex];
			fundingFixingQuoteGrid[closeDateIndex][0] = fundingFixingQuote1Y[closeDateIndex];
			fundingFixingQuoteGrid[closeDateIndex][1] = fundingFixingQuote2Y[closeDateIndex];
			fundingFixingQuoteGrid[closeDateIndex][2] = fundingFixingQuote3Y[closeDateIndex];
			fundingFixingQuoteGrid[closeDateIndex][3] = fundingFixingQuote4Y[closeDateIndex];
			fundingFixingQuoteGrid[closeDateIndex][4] = fundingFixingQuote5Y[closeDateIndex];
			fundingFixingQuoteGrid[closeDateIndex][5] = fundingFixingQuote6Y[closeDateIndex];
			fundingFixingQuoteGrid[closeDateIndex][6] = fundingFixingQuote7Y[closeDateIndex];
			fundingFixingQuoteGrid[closeDateIndex][7] = fundingFixingQuote8Y[closeDateIndex];
			fundingFixingQuoteGrid[closeDateIndex][8] = fundingFixingQuote9Y[closeDateIndex];
			fundingFixingQuoteGrid[closeDateIndex][9] = fundingFixingQuote10Y[closeDateIndex];
			fundingFixingQuoteGrid[closeDateIndex][10] = fundingFixingQuote11Y[closeDateIndex];
			fundingFixingQuoteGrid[closeDateIndex][11] = fundingFixingQuote12Y[closeDateIndex];
			fundingFixingQuoteGrid[closeDateIndex][12] = fundingFixingQuote15Y[closeDateIndex];
			fundingFixingQuoteGrid[closeDateIndex][13] = fundingFixingQuote20Y[closeDateIndex];
			fundingFixingQuoteGrid[closeDateIndex][14] = fundingFixingQuote25Y[closeDateIndex];
			fundingFixingQuoteGrid[closeDateIndex][15] = fundingFixingQuote30Y[closeDateIndex];
			fundingFixingQuoteGrid[closeDateIndex][16] = fundingFixingQuote40Y[closeDateIndex];
			fundingFixingQuoteGrid[closeDateIndex][17] = fundingFixingQuote50Y[closeDateIndex];
			creditIndexQuotedSpreadArray[closeDateIndex] *= 10000.;
		}

		List<PositionChangeComponents> positionChangeComponentsList =
			CreditIndexAPI.HorizonChangeAttribution (
				spotDateArray,
				1,
				fundingFixingMaturityTenorArray,
				fundingFixingQuoteGrid,
				fullCreditIndexNameArray,
				creditIndexQuotedSpreadArray
			);

		System.out.println (
			"FirstDate,SecondDate,CreditLabel,Horizon,TotalPnL,MarketShiftPnL,RollDownPnL,AccrualPnL,ExplainedPnL,UnexplainedPnL,FixedCoupon,FirstFairPremium,SecondFairPremium,RollDownFairPremium,CleanFixedDV01"
		);

		for (PositionChangeComponents positionChangeComponents : positionChangeComponentsList) {
			if (null == positionChangeComponents) {
				continue;
			}

			CDSMarketSnap t1PositionMarketSnap =
				(CDSMarketSnap) positionChangeComponents.t1PositionMarketSnap();

			CDSMarketSnap t2PositionMarketSnap =
				(CDSMarketSnap) positionChangeComponents.t2PositionMarketSnap();

			System.out.println (
				positionChangeComponents.t1() + ", " +
				positionChangeComponents.t2() + ", " +
				t1PositionMarketSnap.creditLabel() + ", " +
				horizonGap + "," +
				FormatUtil.FormatDouble (positionChangeComponents.grossChange(), 2, 4, 10000.) + ", " +
				FormatUtil.FormatDouble (positionChangeComponents.marketRealizationChange(), 2, 4, 10000.) +
					", " +
				FormatUtil.FormatDouble (positionChangeComponents.marketRollDownChange(), 1, 4, 10000.) +
					", " +
				FormatUtil.FormatDouble (positionChangeComponents.accrualChange(), 1, 4, 10000.) + ", " +
				FormatUtil.FormatDouble (positionChangeComponents.explainedChange(), 2, 4, 10000.) + ", " +
				FormatUtil.FormatDouble (positionChangeComponents.unexplainedChange(), 1, 4, 10000.) + ", " +
				FormatUtil.FormatDouble (t1PositionMarketSnap.fixedCoupon(), 1, 2, 10000.) + ", " +
				FormatUtil.FormatDouble (t1PositionMarketSnap.currentFairPremium(), 1, 4, 10000.) + ", " +
				FormatUtil.FormatDouble (t2PositionMarketSnap.currentFairPremium(), 1, 4, 10000.) + ", " +
				FormatUtil.FormatDouble (t1PositionMarketSnap.rollDownFairPremium(), 1, 4, 10000.) + ", " +
				FormatUtil.FormatDouble (t1PositionMarketSnap.cleanDV01(), 1, 4, 1.)
			);
		}

		EnvManager.TerminateEnv();
	}
}
