
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
 * <i>IIDSequenceSumBound</i> demonstrates the Computation of the Different Probabilistic Bounds for Sums of
 * 	i.i.d. Random Sequences.
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

public class IIDSequenceSumBound
{

	private static final void Head (
		final String header)
	{
		System.out.println();

		System.out.println (
			"\t|---------------------------------------------------------------------------------------|"
		);

		System.out.println (header);

		System.out.println (
			"\t|---------------------------------------------------------------------------------------|"
		);

		System.out.println (
			"\t|   SAMPLE  ||               <-               TOLERANCES               ->               |"
		);

		System.out.println (
			"\t|---------------------------------------------------------------------------------------|"
		);
	}

	private static final void WeakLawBounds (
		final UnivariateSequenceGenerator univariateSequenceGenerator,
		final R1Continuous r1ContinuousDistribution,
		final int[] sampleSizeArray,
		final double[] toleranceArray)
		throws Exception
	{
		for (int sampleSize : sampleSizeArray) {
			SingleSequenceAgnosticMetrics singleSequenceAgnosticMetrics =
				univariateSequenceGenerator.sequence (sampleSize, r1ContinuousDistribution);

			String dump = "\t| " + FormatUtil.FormatDouble (sampleSize, 8, 0, 1) + " => ";

			for (double tolerance : toleranceArray) {
				dump += FormatUtil.FormatDouble (
					singleSequenceAgnosticMetrics.weakLawAverageBounds (tolerance).upper(),
					1,
					9,
					1.
				) + " | ";
			}

			System.out.println (dump);
		}
	}

	private static final void ChernoffHoeffdingBounds (
		final UnivariateSequenceGenerator univariateSequenceGenerator,
		final R1Continuous r1ContinuousDistribution,
		final double support,
		final int[] sampleSizeArray,
		final double[] toleranceArray)
		throws Exception
	{
		for (int sampleSize : sampleSizeArray) {
			BoundedSequenceAgnosticMetrics boundedSequenceAgnosticMetrics =
				(BoundedSequenceAgnosticMetrics) univariateSequenceGenerator.sequence (
					sampleSize,
					r1ContinuousDistribution
				);

			String dump = "\t| " + FormatUtil.FormatDouble (sampleSize, 8, 0, 1) + " => ";

			for (double tolerance : toleranceArray) {
				dump += FormatUtil.FormatDouble (
					boundedSequenceAgnosticMetrics.chernoffHoeffdingAverageBounds (tolerance).upper(),
					1,
					9,
					1.
				) + " | ";
			}

			System.out.println (dump);
		}
	}

	private static final void BennettBounds (
		final UnivariateSequenceGenerator univariateSequenceGenerator,
		final R1Continuous r1ContinuousDistribution,
		final double support,
		final int[] sampleSizeArray,
		final double[] toleranceArray)
		throws Exception
	{
		for (int sampleSize : sampleSizeArray) {
			BoundedSequenceAgnosticMetrics singleSequenceAgnosticMetrics =
				(BoundedSequenceAgnosticMetrics) univariateSequenceGenerator.sequence (
					sampleSize,
					r1ContinuousDistribution
				);

			String dump = "\t| " + FormatUtil.FormatDouble (sampleSize, 8, 0, 1) + " => ";

			for (double dblTolerance : toleranceArray) {
				dump += FormatUtil.FormatDouble (
					singleSequenceAgnosticMetrics.bennettAverageBounds (dblTolerance).upper(),
					1,
					9,
					1.
				) + " | ";
			}

			System.out.println (dump);
		}
	}

	private static final void BernsteinBounds (
		final UnivariateSequenceGenerator univariateSequenceGenerator,
		final R1Continuous r1ContinuousDistribution,
		final double support,
		final int[] sampleSizeArray,
		final double[] toleranceArray)
		throws Exception
	{
		for (int sampleSize : sampleSizeArray) {
			BoundedSequenceAgnosticMetrics singleSequenceAgnosticMetrics =
				(BoundedSequenceAgnosticMetrics) univariateSequenceGenerator.sequence (
					sampleSize,
					r1ContinuousDistribution
				);

			String dump = "\t| " + FormatUtil.FormatDouble (sampleSize, 8, 0, 1) + " => ";

			for (double tolerance : toleranceArray) {
				dump += FormatUtil.FormatDouble (
					singleSequenceAgnosticMetrics.bernsteinAverageBounds (tolerance).upper(),
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
			50,
			500,
			5000,
			50000,
			500000,
			5000000,
			50000000
		};
		double[] toleranceArray =
		{
			0.01,
			0.03,
			0.05,
			0.07,
			0.10
		};

		Head ("\t|         WEAK LAW OF LARGE NUMBERS     -      METRICS FROM UNDERLYING GENERATOR        |");

		WeakLawBounds (boundedUniform, r1ContinuousUniform, sampleSizeArray, toleranceArray);

		System.out.println (
			"\t|---------------------------------------------------------------------------------------|"
		);

		Head ("\t|        WEAK LAW OF LARGE NUMBERS      -     METRICS FROM EMPIRICAL DISTRIBUTION       |");

		WeakLawBounds (boundedUniform, null, sampleSizeArray, toleranceArray);

		System.out.println (
			"\t|---------------------------------------------------------------------------------------|"
		);

		Head ("\t|         CHERNOFF-HOEFFDING BOUNDS      -     METRICS FROM UNDERLYING GENERATOR        |");

		ChernoffHoeffdingBounds (
			boundedUniform,
			r1ContinuousUniform,
			boundedUniform.upperBound() - boundedUniform.lowerBound(),
			sampleSizeArray,
			toleranceArray
		);

		System.out.println (
			"\t|---------------------------------------------------------------------------------------|"
		);

		Head ("\t|         CHERNOFF-HOEFFDING BOUNDS      -     METRICS FROM EMPIRICAL DISTRIBUTION      |");

		ChernoffHoeffdingBounds (
			boundedUniform,
			null,
			boundedUniform.upperBound() - boundedUniform.lowerBound(),
			sampleSizeArray,
			toleranceArray
		);

		System.out.println (
			"\t|---------------------------------------------------------------------------------------|"
		);

		Head ("\t|              BENNETT BOUNDS      -      METRICS FROM UNDERLYING GENERATOR             |");

		BennettBounds (
			boundedUniform,
			r1ContinuousUniform,
			boundedUniform.upperBound() - boundedUniform.lowerBound(),
			sampleSizeArray,
			toleranceArray
		);

		System.out.println (
			"\t|---------------------------------------------------------------------------------------|"
		);

		Head ("\t|              BENNETT BOUNDS      -      METRICS FROM EMPIRICAL DISTRIBUTION           |");

		BennettBounds (
			boundedUniform,
			null,
			boundedUniform.upperBound() - boundedUniform.lowerBound(),
			sampleSizeArray,
			toleranceArray
		);

		System.out.println (
			"\t|---------------------------------------------------------------------------------------|"
		);

		Head ("\t|             BERNSTEIN BOUNDS      -      METRICS FROM UNDERLYING GENERATOR            |");

		BernsteinBounds (
			boundedUniform,
			r1ContinuousUniform,
			boundedUniform.upperBound() - boundedUniform.lowerBound(),
			sampleSizeArray,
			toleranceArray
		);

		System.out.println (
			"\t|---------------------------------------------------------------------------------------|"
		);

		Head ("\t|            BERNSTEIN BOUNDS      -      METRICS FROM EMPIRICAL DISTRIBUTION           |");

		BernsteinBounds (
			boundedUniform,
			r1ContinuousUniform,
			boundedUniform.upperBound() - boundedUniform.lowerBound(),
			sampleSizeArray,
			toleranceArray
		);

		System.out.println (
			"\t|---------------------------------------------------------------------------------------|"
		);

		EnvManager.TerminateEnv();
	}
}
