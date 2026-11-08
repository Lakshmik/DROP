
package org.drip.sample.sequence;

import org.drip.measure.distribution.R1Continuous;
import org.drip.measure.distribution.R1ContinuousUniform;
import org.drip.sequence.metrics.*;
import org.drip.sequence.random.*;
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
 * <i>UnitRandomSequenceBound</i> demonstrates the Computation of the Probabilistic Bounds for a Sample
 * 	Random Sequence.
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/StatisticalLearningLibrary.md">Statistical Learning</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/sequence/README.md">IID Dual Poisson Sequence Bound</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class UnitRandomSequenceBound
{

	private static final void Head (
		final String strHeader)
	{
		System.out.println();

		System.out.println (
			"\t|----------------------------------------------------------------------------------|"
		);

		System.out.println (strHeader);

		System.out.println (
			"\t|----------------------------------------------------------------------------------|"
		);

		System.out.println (
			"\t| SIZE ||               <-               TOLERANCES               ->               |"
		);

		System.out.println (
			"\t|----------------------------------------------------------------------------------|"
		);
	}

	private static final void ChernoffBinomialBounds (
		final UnivariateSequenceGenerator univariateSequenceGenerator,
		final R1Continuous r1ContinuousDistribution,
		final int[] sampleSizeArray,
		final double[] toleranceArray)
		throws Exception
	{
		for (int sampleSize : sampleSizeArray) {
			UnitSequenceAgnosticMetrics unitSequenceAgnosticMetrics =
				(UnitSequenceAgnosticMetrics) univariateSequenceGenerator.sequence (
					sampleSize,
					r1ContinuousDistribution
				);

			String dump = "\t| " + FormatUtil.FormatDouble (sampleSize, 3, 0, 1) + " => ";

			for (double tolerance : toleranceArray) {
				dump += FormatUtil.FormatDouble (
					unitSequenceAgnosticMetrics.chernoffBinomialUpperBound (tolerance),
					1,
					9,
					1.
				) + " | ";
			}

			System.out.println (dump);
		}
	}

	private static final void PoissonChernoffBinomialBounds (
		final UnivariateSequenceGenerator univariateSequenceGenerator,
		final R1Continuous r1ContinuousDistribution,
		final int[] sampleSizeArray,
		final double[] toleranceArray)
		throws Exception
	{
		for (int sampleSize : sampleSizeArray) {
			UnitSequenceAgnosticMetrics unitSequenceAgnosticMetrics =
				(UnitSequenceAgnosticMetrics) univariateSequenceGenerator.sequence (
				sampleSize,
				r1ContinuousDistribution
			);

			String dump = "\t| " + FormatUtil.FormatDouble (sampleSize, 3, 0, 1) + " => ";

			for (double tolerance : toleranceArray) {
				dump += FormatUtil.FormatDouble (
					unitSequenceAgnosticMetrics.chernoffPoissonUpperBound (tolerance),
					1,
					9,
					1.
				) + " | ";
			}

			System.out.println (dump);
		}
	}

	private static final void KarpHagerupRubUpperBounds (
		final UnivariateSequenceGenerator univariateSequenceGenerator,
		final R1Continuous r1ContinuousDistribution,
		final int[] sampleSizeArray,
		final double[] toleranceArray)
		throws Exception
	{
		for (int sampleSize : sampleSizeArray) {
			UnitSequenceAgnosticMetrics unitSequenceAgnosticMetrics =
				(UnitSequenceAgnosticMetrics) univariateSequenceGenerator.sequence (
					sampleSize,
					r1ContinuousDistribution
				);

			String dump = "\t| " + FormatUtil.FormatDouble (sampleSize, 3, 0, 1) + " => ";

			for (double tolerance : toleranceArray) {
				dump += FormatUtil.FormatDouble (
					unitSequenceAgnosticMetrics.karpHagerupRubBounds (tolerance).upper(),
					1,
					9,
					1.
				) + " | ";
			}

			System.out.println (dump);
		}
	}

	private static final void KarpHagerupRubLowerBounds (
		final UnivariateSequenceGenerator univariateSequenceGenerator,
		final R1Continuous r1ContinuousDistribution,
		final int[] sampleSizeArray,
		final double[] toleranceArray)
		throws Exception
	{
		for (int sampleSize : sampleSizeArray) {
			UnitSequenceAgnosticMetrics unitSequenceAgnosticMetrics =
				(UnitSequenceAgnosticMetrics) univariateSequenceGenerator.sequence (
					sampleSize,
					r1ContinuousDistribution
				);

			String dump = "\t| " + FormatUtil.FormatDouble (sampleSize, 3, 0, 1) + " => ";

			for (double tolerance : toleranceArray) {
				dump += FormatUtil.FormatDouble (
					unitSequenceAgnosticMetrics.karpHagerupRubBounds (tolerance).lower(),
					1,
					9,
					1.
				) + " | ";
			}

			System.out.println (dump);
		}
	}

	/**
	 * Entry Point
	 * 
	 * @param argumentArray Command Line Argument Array
	 * 
	 * @throws Exception Thrown on Error/Exception Situation
	 */

	public static void main (
		final String[] argumentArray)
		throws Exception
	{
		EnvManager.InitEnv ("");

		BoundedUniform boundedUniform = new BoundedUniform (0., 1.);

		R1ContinuousUniform r1ContinuousUniform = new R1ContinuousUniform (0., 1.);

		int[] sampleSizeArray =
		{
			10,
			20,
			50,
			100,
			250
		};

		double[] toleranceArray =
		{
			0.01,
			0.03,
			0.05,
			0.07,
			0.10
		};

		Head ("\t|        CHERNOFF-BINOMIAL BOUNDS    -     METRICS FROM UNDERLYING GENERATOR       |");

		ChernoffBinomialBounds (boundedUniform, r1ContinuousUniform, sampleSizeArray, toleranceArray);

		System.out.println (
			"\t|----------------------------------------------------------------------------------|"
		);

		Head ("\t|      CHERNOFF-BINOMIAL BOUNDS    -     METRICS FROM EMPIRICAL DISTRIBUTION       |");

		ChernoffBinomialBounds (boundedUniform, null, sampleSizeArray, toleranceArray);

		System.out.println (
			"\t|----------------------------------------------------------------------------------|"
		);

		Head ("\t|       POISSON CHERNOFF-BINOMIAL BOUNDS  -   METRICS FROM UNDERLYING GENERATOR    |");

		PoissonChernoffBinomialBounds (boundedUniform, r1ContinuousUniform, sampleSizeArray, toleranceArray);

		System.out.println (
			"\t|----------------------------------------------------------------------------------|"
		);

		Head ("\t|       POISSON CHERNOFF-BINOMIAL BOUNDS  -  METRICS FROM EMPIRICAL DISTRIBUTION   |");

		PoissonChernoffBinomialBounds (boundedUniform, null, sampleSizeArray, toleranceArray);

		sampleSizeArray = new int[]
		{
			100,
			200,
			300,
			500,
			999
		};

		System.out.println (
			"\t|----------------------------------------------------------------------------------|"
		);

		Head ("\t|         KARP-HAGERUP-RUB UPPER BOUNDS  -   METRICS FROM UNDERLYING GENERATOR     |");

		KarpHagerupRubUpperBounds (boundedUniform, null, sampleSizeArray, toleranceArray);

		System.out.println (
			"\t|----------------------------------------------------------------------------------|"
		);

		Head ("\t|        KARP-HAGERUP-RUB UPPER BOUNDS  -   METRICS FROM EMPIRICAL DISTRIBUTION    |");

		KarpHagerupRubUpperBounds (boundedUniform, null, sampleSizeArray, toleranceArray);

		System.out.println (
			"\t|----------------------------------------------------------------------------------|"
		);

		Head ("\t|         KARP-HAGERUP-RUB LOWER BOUNDS  -   METRICS FROM UNDERLYING GENERATOR     |");

		KarpHagerupRubLowerBounds (boundedUniform, null, sampleSizeArray, toleranceArray);

		System.out.println (
			"\t|----------------------------------------------------------------------------------|"
		);

		Head ("\t|        KARP-HAGERUP-RUB LOWER BOUNDS  -   METRICS FROM EMPIRICAL DISTRIBUTION    |");

		KarpHagerupRubLowerBounds (boundedUniform, null, sampleSizeArray, toleranceArray);

		System.out.println (
			"\t|----------------------------------------------------------------------------------|"
		);

		EnvManager.TerminateEnv();
	}
}
