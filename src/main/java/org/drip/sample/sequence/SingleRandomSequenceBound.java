
package org.drip.sample.sequence;

import org.drip.function.definition.R1ToR1;
import org.drip.function.r1tor1.*;
import org.drip.sequence.metrics.*;
import org.drip.sequence.random.BoundedUniform;
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
 * <i>SingleRandomSequenceBound</i> demonstrates the Computation of the Probabilistic Bounds for a Sample
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

public class SingleRandomSequenceBound
{

	private static final void MarkovBound (
		final SingleSequenceAgnosticMetrics singleSequenceAgnosticMetrics,
		final double level,
		final R1ToR1 function)
		throws Exception
	{
		System.out.println (
			(null == function ? "\tMarkov Base Bound        [" :  "\tMarkov Function Bound    [") +
			FormatUtil.FormatDouble (
				level,
				1,
				2,
				1.
			) + "] : " + FormatUtil.FormatDouble (
				singleSequenceAgnosticMetrics.markovUpperProbabilityBound (level, function),
				1,
				4,
				1.
			) + " |" + FormatUtil.FormatDouble (
				1. - level,
				1,
				4,
				1.
			)
		);
	}

	private static final void ChebyshevBound (
		final SingleSequenceAgnosticMetrics singleSequenceAgnosticMetrics,
		final double level)
		throws Exception
	{
		PivotedDepartureBounds chebyshevBound = singleSequenceAgnosticMetrics.chebyshevBound (level);

		System.out.println (
			"\tChebyshev Bound          [" + FormatUtil.FormatDouble (level, 1, 2, 1.) + "] : " +
			FormatUtil.FormatDouble (chebyshevBound.lower(), 1, 4, 1.) + " |" +
			FormatUtil.FormatDouble (chebyshevBound.upper(), 1, 4, 1.) + " |" +
			FormatUtil.FormatDouble (1. - 2. * level, 1, 4, 1.)
		);
	}

	private static final void ChebyshevCantelliBound (
		final SingleSequenceAgnosticMetrics singleSequenceAgnosticMetrics,
		final double level)
		throws Exception
	{
		System.out.println (
			"\tChebyshev Cantelli Bound [" + FormatUtil.FormatDouble (
				level,
				1,
				2,
				1.
			) + "] : " + "        |" + FormatUtil.FormatDouble (
				singleSequenceAgnosticMetrics.chebyshevCantelliBound (level).upper(),
				1,
				4,
				1.
			) + " |" + FormatUtil.FormatDouble (
				1. - level,
				1,
				4,
				1.
			)
		);
	}

	private static final void CentralMomentBound (
		final SingleSequenceAgnosticMetrics singleSequenceAgnosticMetrics,
		final double level,
		final int moment)
		throws Exception
	{
		PivotedDepartureBounds centralMomentBound =
			singleSequenceAgnosticMetrics.centralMomentBound (level, moment);

		System.out.println (
			"\t|| Moment #" + moment + " Bound          [" + FormatUtil.FormatDouble (level, 1, 2, 1.) +
			"] : " + FormatUtil.FormatDouble (centralMomentBound.lower(), 1, 4, 1.) + " |" +
			FormatUtil.FormatDouble (centralMomentBound.upper(), 1, 4, 1.) + " |" +
			FormatUtil.FormatDouble (1. - 2. * level, 1, 4, 1.)
		);
	}

	private static final void MarkovBound (
		final SingleSequenceAgnosticMetrics singleSequenceAgnosticMetrics,
		final R1ToR1 function)
		throws Exception
	{
		MarkovBound (singleSequenceAgnosticMetrics, 0.20, function);

		MarkovBound (singleSequenceAgnosticMetrics, 0.40, function);

		MarkovBound (singleSequenceAgnosticMetrics, 0.59, function);

		MarkovBound (singleSequenceAgnosticMetrics, 0.79, function);

		MarkovBound (singleSequenceAgnosticMetrics, 0.99, function);
	}

	private static final void ChebyshevBound (
		final SingleSequenceAgnosticMetrics singleSequenceAgnosticMetrics)
		throws Exception
	{
		ChebyshevBound (singleSequenceAgnosticMetrics, 0.20);

		ChebyshevBound (singleSequenceAgnosticMetrics, 0.25);

		ChebyshevBound (singleSequenceAgnosticMetrics, 0.30);

		ChebyshevBound (singleSequenceAgnosticMetrics, 0.35);

		ChebyshevBound (singleSequenceAgnosticMetrics, 0.40);
	}

	private static final void ChebyshevCantelliBound (
		final SingleSequenceAgnosticMetrics singleSequenceAgnosticMetrics)
		throws Exception
	{
		ChebyshevCantelliBound (singleSequenceAgnosticMetrics, 0.20);

		ChebyshevCantelliBound (singleSequenceAgnosticMetrics, 0.25);

		ChebyshevCantelliBound (singleSequenceAgnosticMetrics, 0.30);

		ChebyshevCantelliBound (singleSequenceAgnosticMetrics, 0.35);

		ChebyshevCantelliBound (singleSequenceAgnosticMetrics, 0.40);
	}

	private static final void CentralMomentBound (
		final SingleSequenceAgnosticMetrics singleSequenceAgnosticMetrics,
		final int moment)
		throws Exception
	{
		CentralMomentBound (singleSequenceAgnosticMetrics, 0.20, moment);

		CentralMomentBound (singleSequenceAgnosticMetrics, 0.25, moment);

		CentralMomentBound (singleSequenceAgnosticMetrics, 0.30, moment);

		CentralMomentBound (singleSequenceAgnosticMetrics, 0.35, moment);

		CentralMomentBound (singleSequenceAgnosticMetrics, 0.40, moment);
	}

	private static final void SequenceGenerationRun (
		final SingleSequenceAgnosticMetrics singleSequenceAgnosticMetrics)
		throws Exception
	{
		System.out.println (
			"\t|| Expectation                      : " +
				FormatUtil.FormatDouble (singleSequenceAgnosticMetrics.empiricalExpectation(), 1, 4, 1.)
		);

		System.out.println (
			"\t|| Variance                         : " +
				FormatUtil.FormatDouble (singleSequenceAgnosticMetrics.empiricalVariance(), 1, 4, 1.)
		);

		System.out.println ("\t||---------------------------------------------------");

		MarkovBound (singleSequenceAgnosticMetrics, new ExponentialTension (Math.E, 0.1));

		System.out.println ("\t||---------------------------------------------------");

		MarkovBound (singleSequenceAgnosticMetrics, new ExponentialTension (Math.E, 1.0));

		System.out.println ("\t||---------------------------------------------------");

		MarkovBound (singleSequenceAgnosticMetrics, new ExponentialTension (Math.E, 5.0));

		System.out.println ("\t||---------------------------------------------------");

		MarkovBound (singleSequenceAgnosticMetrics, null);

		System.out.println ("\t||---------------------------------------------------");

		ChebyshevBound (singleSequenceAgnosticMetrics);

		System.out.println ("\t||---------------------------------------------------");

		ChebyshevCantelliBound (singleSequenceAgnosticMetrics);

		System.out.println ("\t||---------------------------------------------------");

		CentralMomentBound (singleSequenceAgnosticMetrics, 3);

		System.out.println ("\t||---------------------------------------------------");

		CentralMomentBound (singleSequenceAgnosticMetrics, 4);

		System.out.println ("\t||---------------------------------------------------");

		CentralMomentBound (singleSequenceAgnosticMetrics, 5);
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

		System.out.println ("\t||--------------------------------------------------|");

		System.out.println ("\t||              BOUNDED UNIFORM RUN                 |");

		System.out.println ("\t||--------------------------------------------------|");

		SequenceGenerationRun (new BoundedUniform (0., 1.).sequence (50000, null));

		System.out.println ("\t||--------------------------------------------------|");

		EnvManager.TerminateEnv();
	}
}
