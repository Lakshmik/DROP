
package org.drip.sample.overnighthistorical;

import java.util.Map;

import org.drip.analytics.date.JulianDate;
import org.drip.feed.loader.*;
import org.drip.historical.state.FundingCurveMetrics;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.service.state.OvernightCurveAPI;
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
 * <i>CADSmooth1MForward</i> Generates the Historical CAD Smoothened Overnight Curve Native 1M Compounded
 * 	Forward Rate.
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/sample/overnighthistorical/README.md">G7 Smooth OIS 1M Forward</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class CADSmooth1MForward
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

		String currency = "CAD";
		String closesLocation = "C:\\DROP\\Daemons\\Transforms\\OvernightOISMarks\\" + currency +
			"OISSmoothReconstitutor.csv";
		String[] forTenorArray = new String[] {
			"1M"
		};
		String[] inTenorArray = new String[] {
			"1W",
			"2W",
			"3W",
			"1M",
			"2M",
			"3M",
			"4M",
			"5M",
			"6M",
			"9M",
			"1Y",
			"18M",
			"2Y",
			"3Y",
			"4Y",
			"5Y"
		};
		String[] oisMaturityTenorArray = new String[] {
			"1W",
			"2W",
			"3W",
			"1M",
			"2M",
			"3M",
			"4M",
			"5M",
			"6M",
			"9M",
			"1Y",
			"18M",
			"2Y",
			"3Y",
			"4Y",
			"5Y"
		};

		CSVGrid csvGrid = CSVParser.StringGrid (closesLocation, true);

		JulianDate[] closeDateArray = csvGrid.dateArrayAtColumn (0);

		double[] oisQuote1WArray = csvGrid.doubleArrayAtColumn (1);

		double[] oisQuote2WArray = csvGrid.doubleArrayAtColumn (2);

		double[] oisQuote3WArray = csvGrid.doubleArrayAtColumn (3);

		double[] oisQuote1MArray = csvGrid.doubleArrayAtColumn (4);

		double[] oisQuote2MArray = csvGrid.doubleArrayAtColumn (5);

		double[] oisQuote3MArray = csvGrid.doubleArrayAtColumn (6);

		double[] oisQuote4MArray = csvGrid.doubleArrayAtColumn (7);

		double[] oisQuote5MArray = csvGrid.doubleArrayAtColumn (8);

		double[] oisQuote6MArray = csvGrid.doubleArrayAtColumn (9);

		double[] oisQuote9MArray = csvGrid.doubleArrayAtColumn (10);

		double[] oisQuote1YArray = csvGrid.doubleArrayAtColumn (11);

		double[] oisQuote18MArray = csvGrid.doubleArrayAtColumn (12);

		double[] oisQuote2YArray = csvGrid.doubleArrayAtColumn (13);

		double[] oisQuote3YArray = csvGrid.doubleArrayAtColumn (14);

		double[] oisQuote4YArray = csvGrid.doubleArrayAtColumn (15);

		double[] oisQuote5YArray = csvGrid.doubleArrayAtColumn (16);

		int closeDateCount = closeDateArray.length;
		JulianDate[] spotDateArray = new JulianDate[closeDateCount];
		double[][] oisQuoteGrid = new double[closeDateCount][16];

		for (int closeDateIndex = 0; closeDateIndex < closeDateCount; ++closeDateIndex) {
			spotDateArray[closeDateIndex] = closeDateArray[closeDateIndex];
			oisQuoteGrid[closeDateIndex][0] = oisQuote1WArray[closeDateIndex];
			oisQuoteGrid[closeDateIndex][1] = oisQuote2WArray[closeDateIndex];
			oisQuoteGrid[closeDateIndex][2] = oisQuote3WArray[closeDateIndex];
			oisQuoteGrid[closeDateIndex][3] = oisQuote1MArray[closeDateIndex];
			oisQuoteGrid[closeDateIndex][4] = oisQuote2MArray[closeDateIndex];
			oisQuoteGrid[closeDateIndex][5] = oisQuote3MArray[closeDateIndex];
			oisQuoteGrid[closeDateIndex][6] = oisQuote4MArray[closeDateIndex];
			oisQuoteGrid[closeDateIndex][7] = oisQuote5MArray[closeDateIndex];
			oisQuoteGrid[closeDateIndex][8] = oisQuote6MArray[closeDateIndex];
			oisQuoteGrid[closeDateIndex][9] = oisQuote9MArray[closeDateIndex];
			oisQuoteGrid[closeDateIndex][10] = oisQuote1YArray[closeDateIndex];
			oisQuoteGrid[closeDateIndex][11] = oisQuote18MArray[closeDateIndex];
			oisQuoteGrid[closeDateIndex][12] = oisQuote2YArray[closeDateIndex];
			oisQuoteGrid[closeDateIndex][13] = oisQuote3YArray[closeDateIndex];
			oisQuoteGrid[closeDateIndex][14] = oisQuote4YArray[closeDateIndex];
			oisQuoteGrid[closeDateIndex][15] = oisQuote5YArray[closeDateIndex];
		}

		String dump = "Date";

		for (String inTenor : inTenorArray) {
			for (String forTenor : forTenorArray) {
				dump += "," + inTenor + forTenor;
			}
		}

		System.out.println (dump);

		Map<JulianDate, FundingCurveMetrics> fundingCurveMetricsMap = OvernightCurveAPI.HorizonMetrics (
			spotDateArray,
			oisMaturityTenorArray,
			oisQuoteGrid,
			inTenorArray,
			forTenorArray,
			currency,
			LatentMarketStateBuilder.SMOOTH
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
