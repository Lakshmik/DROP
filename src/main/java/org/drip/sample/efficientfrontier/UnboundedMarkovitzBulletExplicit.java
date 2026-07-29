
package org.drip.sample.efficientfrontier;

import org.drip.feed.loader.*;
import org.drip.measure.statistics.MultivariateMoments;
import org.drip.portfolioconstruction.allocator.*;
import org.drip.portfolioconstruction.asset.AssetComponent;
import org.drip.portfolioconstruction.params.AssetUniverseStatisticalProperties;
import org.drip.service.common.FormatUtil;
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
 * <i>UnboundedMarkovitzBulletExplicit</i> demonstrates the Explicit Construction of the Efficient Frontier.
 * 
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/PortfolioCore.md">Portfolio Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/AssetAllocationAnalyticsLibrary.md">Asset Allocation Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/efficientfrontier/README.md">Efficient Frontier Markovitz Bullet Variants</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class UnboundedMarkovitzBulletExplicit
{

	private static double DisplayPortfolioMetrics (
		final HoldingsAllocation holdingsAllocation)
		throws Exception
	{
		AssetComponent[] globalMinimumAssetComponentArray =
			holdingsAllocation.optimalPortfolio().assetComponentArray();

		String dump = "\t|" + FormatUtil.FormatDouble (
			holdingsAllocation.optimalMetrics().excessReturnsMean(),
			1,
			4,
			100.
		) + "% |" + FormatUtil.FormatDouble (
			holdingsAllocation.optimalMetrics().excessReturnsStandardDeviation(),
			1,
			4,
			100.
		) + " |";

		for (AssetComponent assetComponent : globalMinimumAssetComponentArray) {
			dump += " " + FormatUtil.FormatDouble (assetComponent.amount(), 3, 2, 100.) + "% |";
		}

		System.out.println (dump + "|");

		return holdingsAllocation.optimalMetrics().excessReturnsMean();
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

		int riskReturnGranularity = 40;
		double riskToleranceFactor = 0.;
		String seriesLocation = "C:\\DROP\\Daemons\\Feeds\\MeanVarianceOptimizer\\FormattedSeries1.csv";

		CSVGrid csvGrid = CSVParser.NamedStringGrid (seriesLocation);

		String[] variateHeaderArray = csvGrid.headers();

		String[] assetIDArray = new String[variateHeaderArray.length - 1];
		double[][] variateSampleGrid = new double[variateHeaderArray.length - 1][];

		for (int assetIndex = 0; assetIndex < assetIDArray.length; ++assetIndex) {
			assetIDArray[assetIndex] = variateHeaderArray[assetIndex + 1];

			variateSampleGrid[assetIndex] = csvGrid.doubleArrayAtColumn (assetIndex + 1);
		}

		AssetUniverseStatisticalProperties assetUniverseStatisticalProperties =
			AssetUniverseStatisticalProperties.FromMultivariateMetrics (
				MultivariateMoments.Standard (assetIDArray, variateSampleGrid)
			);

		HoldingsAllocationControl holdingsAllocationControl = new HoldingsAllocationControl (
			assetIDArray,
			CustomRiskUtilitySettings.RiskTolerant (riskToleranceFactor),
			new EqualityConstraintSettings (EqualityConstraintSettings.FULLY_INVESTED_CONSTRAINT, Double.NaN)
		);

		MeanVarianceOptimizer meanVarianceOptimizer = new QuadraticMeanVarianceOptimizer();

		System.out.println (
			"\n\n\t|-----------------------------------------------------------------------------------------------||"
		);

		System.out.println (
			"\t|                     GLOBAL MINIMUM VARIANCE AND MAXIMUM RETURNS PORTFOLIOS                    ||"
		);

		System.out.println (
			"\t|-----------------------------------------------------------------------------------------------||"
		);

		String header = "\t| RETURNS | RISK % |";

		for (int assetIndex = 0; assetIndex < assetIDArray.length; ++assetIndex) {
			header += "   " + assetIDArray[assetIndex] + "    |";
		}

		System.out.println (header + "|");

		System.out.println (
			"\t|-----------------------------------------------------------------------------------------------||"
		);

		double globalMinimumVarianceReturns = DisplayPortfolioMetrics (
			meanVarianceOptimizer.globalMinimumVarianceAllocate (
				holdingsAllocationControl,
				assetUniverseStatisticalProperties
			)
		);

		double maximumReturns = DisplayPortfolioMetrics (
			meanVarianceOptimizer.longOnlyMaximumReturnsAllocate (
				holdingsAllocationControl,
				assetUniverseStatisticalProperties
			)
		);

		System.out.println (
			"\t|-----------------------------------------------------------------------------------------------||\n\n\n"
		);

		System.out.println (
			"\t|-----------------------------------------------------------------------------------------------||"
		);

		System.out.println (
			"\t|         EFFICIENT FRONTIER: PORTFOLIO RISK & RETURNS + CORRESPONDING ASSET ALLOCATION         ||"
		);

		System.out.println (
			"\t|-----------------------------------------------------------------------------------------------||"
		);

		System.out.println (header + "|");

		System.out.println (
			"\t|-----------------------------------------------------------------------------------------------||"
		);

		double returnsGrain = (maximumReturns - globalMinimumVarianceReturns) / riskReturnGranularity;

		for (int returnOffset = 0; returnOffset <= riskReturnGranularity; ++returnOffset) {
			DisplayPortfolioMetrics (
				meanVarianceOptimizer.allocate (
					new HoldingsAllocationControl (
						assetIDArray,
						CustomRiskUtilitySettings.VarianceMinimizer(),
						new EqualityConstraintSettings (
							EqualityConstraintSettings.FULLY_INVESTED_CONSTRAINT |
								EqualityConstraintSettings.RETURNS_CONSTRAINT,
							globalMinimumVarianceReturns + returnOffset * returnsGrain
						)
					),
					assetUniverseStatisticalProperties
				)
			);
		}

		System.out.println (
			"\t|-----------------------------------------------------------------------------------------------||\n\n"
		);

		EnvManager.TerminateEnv();
	}
}
