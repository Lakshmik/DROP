
package org.drip.sample.treasuryfuturesrisk;

import java.util.*;

import org.drip.analytics.date.JulianDate;
import org.drip.feed.loader.*;
import org.drip.historical.sensitivity.TenorDurationNodeMetrics;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.service.product.TreasuryFuturesAPI;

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
 * <i>WN1KeyRateDuration</i> demonstrates the Computation of the Key Rate Duration for the WN1 Treasury
 * 	Futures.
 *
 * <br>
 * <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/sample/treasuryfuturesrisk/README.md">Treasury Futures Key Rate Duration</a></td></tr>
 * </table>
 * <br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class WN1KeyRateDuration
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

		String treasuryCode = "WN1";
		String[] benchmarkTenorArray = new String[] {
			"2Y",
			"3Y",
			"4Y",
			"5Y",
			"7Y",
			"10Y",
			"20Y",
			"30Y"
		};
		String treasuryMarkLocation =
			"C:\\DROP\\Daemons\\Transforms\\TreasuryYieldMarks\\USTBenchmarksReconstituted.csv";
		String printLocation = "C:\\DROP\\Daemons\\Transforms\\TreasuryFuturesCloses\\" + treasuryCode +
			"ClosesReconstitutor.csv";

		CSVGrid treasuryMarkCSVGrid = CSVParser.StringGrid (treasuryMarkLocation, true);

		JulianDate[] treasuryMarkDateArray = treasuryMarkCSVGrid.dateArrayAtColumn (0);

		double[] ust02YYieldArray = treasuryMarkCSVGrid.doubleArrayAtColumn (1);

		double[] ust03YYieldArray = treasuryMarkCSVGrid.doubleArrayAtColumn (2);

		double[] ust04YYieldArray = treasuryMarkCSVGrid.doubleArrayAtColumn (3);

		double[] ust05YYieldArray = treasuryMarkCSVGrid.doubleArrayAtColumn (4);

		double[] ust07YYieldArray = treasuryMarkCSVGrid.doubleArrayAtColumn (5);

		double[] ust10YYieldArray = treasuryMarkCSVGrid.doubleArrayAtColumn (6);

		double[] ust20YYieldArray = treasuryMarkCSVGrid.doubleArrayAtColumn (7);

		double[] ust30YYieldArray = treasuryMarkCSVGrid.doubleArrayAtColumn (8);

		Map<JulianDate, Double> treasuryMarkMap02Y = new TreeMap<JulianDate, Double>();

		Map<JulianDate, Double> treasuryMarkMap03Y = new TreeMap<JulianDate, Double>();

		Map<JulianDate, Double> treasuryMarkMap04Y = new TreeMap<JulianDate, Double>();

		Map<JulianDate, Double> treasuryMarkMap05Y = new TreeMap<JulianDate, Double>();

		Map<JulianDate, Double> treasuryMarkMap07Y = new TreeMap<JulianDate, Double>();

		Map<JulianDate, Double> treasuryMarkMap10Y = new TreeMap<JulianDate, Double>();

		Map<JulianDate, Double> treasuryMarkMap20Y = new TreeMap<JulianDate, Double>();

		Map<JulianDate, Double> treasuryMarkMap30Y = new TreeMap<JulianDate, Double>();

		for (int treasuryMarkIndex = 0;
			treasuryMarkIndex < treasuryMarkDateArray.length;
			++treasuryMarkIndex)
		{
			treasuryMarkMap02Y.put (
				treasuryMarkDateArray[treasuryMarkIndex], 
				ust02YYieldArray[treasuryMarkIndex]
			);

			treasuryMarkMap03Y.put (
				treasuryMarkDateArray[treasuryMarkIndex],
				ust03YYieldArray[treasuryMarkIndex]
			);

			treasuryMarkMap04Y.put (
				treasuryMarkDateArray[treasuryMarkIndex],
				ust04YYieldArray[treasuryMarkIndex]
			);

			treasuryMarkMap05Y.put (
				treasuryMarkDateArray[treasuryMarkIndex],
				ust05YYieldArray[treasuryMarkIndex]
			);

			treasuryMarkMap07Y.put (
				treasuryMarkDateArray[treasuryMarkIndex],
				ust07YYieldArray[treasuryMarkIndex]
			);

			treasuryMarkMap10Y.put (
				treasuryMarkDateArray[treasuryMarkIndex],
				ust10YYieldArray[treasuryMarkIndex]
			);

			treasuryMarkMap20Y.put (
				treasuryMarkDateArray[treasuryMarkIndex],
				ust20YYieldArray[treasuryMarkIndex]
			);

			treasuryMarkMap30Y.put (
				treasuryMarkDateArray[treasuryMarkIndex],
				ust30YYieldArray[treasuryMarkIndex]
			);
		}

		CSVGrid csvGrid = CSVParser.StringGrid (printLocation, true);

		JulianDate[] spotDateArray = csvGrid.dateArrayAtColumn (0);

		double[] cleanPriceArray = csvGrid.doubleArrayAtColumn (2);

		double[] couponArray = csvGrid.doubleArrayAtColumn (3);

		JulianDate[] effectiveDateArray = csvGrid.dateArrayAtColumn (4);

		JulianDate[] maturityDateArray = csvGrid.dateArrayAtColumn (5);

		JulianDate[] expiryDateArray = csvGrid.dateArrayAtColumn (6);

		int spotDateCount = spotDateArray.length;
		JulianDate[] effectiveComputeDateArray = new JulianDate[spotDateCount];
		JulianDate[] maturityComputeDateArray = new JulianDate[spotDateCount];
		double[] couponComputeArray = new double[spotDateCount];
		JulianDate[] expiryComputeDateArray = new JulianDate[spotDateCount];
		JulianDate[] spotComputeDateArray = new JulianDate[spotDateCount];
		double[] cleanPriceComputeArray = new double[spotDateCount];
		double[][] ustComputeYieldGrid = new double[spotDateCount][8];

		for (int spotDateIndex = 0; spotDateIndex < spotDateCount; ++spotDateIndex) {
			effectiveComputeDateArray[spotDateIndex] = effectiveDateArray[spotDateIndex];
			maturityComputeDateArray[spotDateIndex] = maturityDateArray[spotDateIndex];
			couponComputeArray[spotDateIndex] = couponArray[spotDateIndex];
			expiryComputeDateArray[spotDateIndex] = expiryDateArray[spotDateIndex];
			spotComputeDateArray[spotDateIndex] = spotDateArray[spotDateIndex];
			cleanPriceComputeArray[spotDateIndex] = cleanPriceArray[spotDateIndex];
			ustComputeYieldGrid[spotDateIndex][0] = ust02YYieldArray[spotDateIndex];
			ustComputeYieldGrid[spotDateIndex][1] = ust03YYieldArray[spotDateIndex];
			ustComputeYieldGrid[spotDateIndex][2] = ust04YYieldArray[spotDateIndex];
			ustComputeYieldGrid[spotDateIndex][3] = ust05YYieldArray[spotDateIndex];
			ustComputeYieldGrid[spotDateIndex][4] = ust07YYieldArray[spotDateIndex];
			ustComputeYieldGrid[spotDateIndex][5] = ust10YYieldArray[spotDateIndex];
			ustComputeYieldGrid[spotDateIndex][6] = ust20YYieldArray[spotDateIndex];
			ustComputeYieldGrid[spotDateIndex][7] = ust30YYieldArray[spotDateIndex];
		}

		List<TenorDurationNodeMetrics> tenorDurationNodeMetricsList =
			TreasuryFuturesAPI.HorizonKeyRateDuration (
				"UST",
				effectiveComputeDateArray,
				maturityComputeDateArray,
				couponComputeArray,
				expiryComputeDateArray,
				spotComputeDateArray,
				cleanPriceComputeArray,
				benchmarkTenorArray,
				ustComputeYieldGrid
			);

		System.out.println (
			"SpotDate,ExpiryDate,CTDName,SpotCTDCleanPrice,ExpiryCTDCleanPrice,SpotGSpread,ExpiryGSpread,SpotYield,ExpiryYield,Parallel,2Y,3Y,4Y,5Y,7Y,10Y,20Y,30Y"
		);

		for (TenorDurationNodeMetrics tenorDurationNodeMetrics : tenorDurationNodeMetricsList) {
			String tenorDurationNodeMetricsDump =
				tenorDurationNodeMetrics.snapDate() + "," +
				tenorDurationNodeMetrics.date ("ExpiryDate") + "," +
				tenorDurationNodeMetrics.c1 ("CTDName") + "," +
				FormatUtil.FormatDouble (tenorDurationNodeMetrics.r1 ("SpotCTDCleanPrice"), 1, 5, 100.) +
					"," +
				FormatUtil.FormatDouble (tenorDurationNodeMetrics.r1 ("ExpiryCTDCleanPrice"), 1, 5, 100.) +
					"," +
				FormatUtil.FormatDouble (tenorDurationNodeMetrics.r1 ("SpotGSpread"), 1, 1, 10000.) + "," +
				FormatUtil.FormatDouble (tenorDurationNodeMetrics.r1 ("ExpiryGSpread"), 1, 1, 10000.) + "," +
				FormatUtil.FormatDouble (tenorDurationNodeMetrics.r1 ("SpotYield"), 1, 4, 100.) + "," +
				FormatUtil.FormatDouble (tenorDurationNodeMetrics.r1 ("ExpiryYield"), 1, 4, 100.) + "," +
				FormatUtil.FormatDouble (tenorDurationNodeMetrics.r1 ("ParallelKRD"), 1, 4, 1.);

			for (Map.Entry<String, Double> krdEntry : tenorDurationNodeMetrics.krdMap().entrySet()) {
				tenorDurationNodeMetricsDump +=
					"," + FormatUtil.FormatDouble (krdEntry.getValue(), 1, 4, 1.);
			}

			System.out.println (tenorDurationNodeMetricsDump);
		}

		EnvManager.TerminateEnv();
	}
}
