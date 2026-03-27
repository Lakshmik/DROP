
package org.drip.sample.fundinghistorical;

import java.util.Map;

import org.drip.analytics.date.JulianDate;
import org.drip.feed.loader.*;
import org.drip.historical.state.FundingCurveMetrics;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.service.state.FundingCurveAPI;
import org.drip.service.template.LatentMarketStateBuilder;

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
 * <i>ILSShapePreserving1YStart</i> Generates the Historical ILS Shape Preserving Funding Curve Native
 * 	Compounded Forward Rate starting at 1Y Tenor.
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/sample/fundinghistorical/README.md">Smooth Shape Preserving Funding Historical</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class ILSShapePreserving1YStart
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

		String currency = "ILS";
		String closesLocation = "C:\\DROP\\Daemons\\Transforms\\FundingStateMarks\\" + currency +
			"ShapePreservingReconstitutor.csv";
		String[] inTenorArray = new String[] {
			"1Y"
		};
		String[] forTenorArray = new String[] {
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
		};
		String[] fixFloatMaturityTenorArray = new String[] {
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

		double[] fixFloat1YQuoteArray = csvGrid.doubleArrayAtColumn (1);

		double[] fixFloat2YQuoteArray = csvGrid.doubleArrayAtColumn (2);

		double[] fixFloat3YQuoteArray = csvGrid.doubleArrayAtColumn (3);

		double[] fixFloat4YQuoteArray = csvGrid.doubleArrayAtColumn (4);

		double[] fixFloat5YQuoteArray = csvGrid.doubleArrayAtColumn (5);

		double[] fixFloat6YQuoteArray = csvGrid.doubleArrayAtColumn (6);

		double[] fixFloat7YQuoteArray = csvGrid.doubleArrayAtColumn (7);

		double[] fixFloat8YQuoteArray = csvGrid.doubleArrayAtColumn (8);

		double[] fixFloat9YQuoteArray = csvGrid.doubleArrayAtColumn (9);

		double[] fixFloat10YQuoteArray = csvGrid.doubleArrayAtColumn (10);

		double[] fixFloat11YQuoteArray = csvGrid.doubleArrayAtColumn (11);

		double[] fixFloat12YQuoteArray = csvGrid.doubleArrayAtColumn (12);

		double[] fixFloat15YQuoteArray = csvGrid.doubleArrayAtColumn (13);

		double[] fixFloat20YQuoteArray = csvGrid.doubleArrayAtColumn (14);

		double[] fixFloat25YQuoteArray = csvGrid.doubleArrayAtColumn (15);

		double[] fixFloat30YQuoteArray = csvGrid.doubleArrayAtColumn (16);

		double[] fixFloat40YQuoteArray = csvGrid.doubleArrayAtColumn (17);

		double[] fixFloat50YQuoteArray = csvGrid.doubleArrayAtColumn (18);

		int closeDateCount = closeDateArray.length;
		JulianDate[] spotDateArray = new JulianDate[closeDateCount];
		double[][] fixFloatQuoteGrid = new double[closeDateCount][18];

		for (int closeDateIndex = 0; closeDateIndex < closeDateCount; ++closeDateIndex) {
			spotDateArray[closeDateIndex] = closeDateArray[closeDateIndex];
			fixFloatQuoteGrid[closeDateIndex][0] = fixFloat1YQuoteArray[closeDateIndex];
			fixFloatQuoteGrid[closeDateIndex][1] = fixFloat2YQuoteArray[closeDateIndex];
			fixFloatQuoteGrid[closeDateIndex][2] = fixFloat3YQuoteArray[closeDateIndex];
			fixFloatQuoteGrid[closeDateIndex][3] = fixFloat4YQuoteArray[closeDateIndex];
			fixFloatQuoteGrid[closeDateIndex][4] = fixFloat5YQuoteArray[closeDateIndex];
			fixFloatQuoteGrid[closeDateIndex][5] = fixFloat6YQuoteArray[closeDateIndex];
			fixFloatQuoteGrid[closeDateIndex][6] = fixFloat7YQuoteArray[closeDateIndex];
			fixFloatQuoteGrid[closeDateIndex][7] = fixFloat8YQuoteArray[closeDateIndex];
			fixFloatQuoteGrid[closeDateIndex][8] = fixFloat9YQuoteArray[closeDateIndex];
			fixFloatQuoteGrid[closeDateIndex][9] = fixFloat10YQuoteArray[closeDateIndex];
			fixFloatQuoteGrid[closeDateIndex][10] = fixFloat11YQuoteArray[closeDateIndex];
			fixFloatQuoteGrid[closeDateIndex][11] = fixFloat12YQuoteArray[closeDateIndex];
			fixFloatQuoteGrid[closeDateIndex][12] = fixFloat15YQuoteArray[closeDateIndex];
			fixFloatQuoteGrid[closeDateIndex][13] = fixFloat20YQuoteArray[closeDateIndex];
			fixFloatQuoteGrid[closeDateIndex][14] = fixFloat25YQuoteArray[closeDateIndex];
			fixFloatQuoteGrid[closeDateIndex][15] = fixFloat30YQuoteArray[closeDateIndex];
			fixFloatQuoteGrid[closeDateIndex][16] = fixFloat40YQuoteArray[closeDateIndex];
			fixFloatQuoteGrid[closeDateIndex][17] = fixFloat50YQuoteArray[closeDateIndex];
		}

		String dump = "Date";

		for (String inTenor : inTenorArray) {
			for (String forTenor : forTenorArray) {
				dump += "," + inTenor + forTenor;
			}
		}

		System.out.println (dump);

		Map<JulianDate, FundingCurveMetrics> fundingCurveMetricsMap = FundingCurveAPI.HorizonMetrics (
			spotDateArray,
			fixFloatMaturityTenorArray,
			fixFloatQuoteGrid,
			inTenorArray,
			forTenorArray,
			currency,
			LatentMarketStateBuilder.SHAPE_PRESERVING
		);

		for (int closeDateIndex = 0; closeDateIndex < closeDateCount; ++closeDateIndex) {
			FundingCurveMetrics fundingCurveMetrics =
				fundingCurveMetricsMap.get (spotDateArray[closeDateIndex]);

			dump = spotDateArray[closeDateIndex].toString();

			for (String inTenor : inTenorArray) {
				for (String forTenor : forTenorArray) {
					dump += "," + FormatUtil.FormatDouble (
						fundingCurveMetrics.nativeForwardRate (inTenor, forTenor),
						1,
						5,
						100.
					);
				}
			}

			System.out.println (dump);
		}

		EnvManager.TerminateEnv();
	}
}
