
package org.drip.sample.hullwhite;

import java.util.Map;

import org.drip.analytics.date.*;
import org.drip.dynamics.hullwhite.*;
import org.drip.function.r1tor1operator.Flat;
import org.drip.sequence.random.BoxMullerGaussian;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.state.identifier.FundingLabel;

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
 * <i>TrinomialTreeCalibration</i> demonstrates the Construction and Calibration of the Hull-White Trinomial
 * 	Tree and the Eventual Evolution of the Short Rate on it.
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/hullwhite/README.md">Hull White Trinomial Tree Dynamics</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class TrinomialTreeCalibration
{

	private static final String SourceToTarget (
		final String key)
	{
		String[] nodeArray = key.split ("#");

		String[] sourceNodeArray = nodeArray[0].split (",");

		String[] targetNodeArray = nodeArray[1].split (",");

		return "[" + sourceNodeArray[0] + "," +
			FormatUtil.FormatDouble (Double.parseDouble (sourceNodeArray[1]), 1, 0, 1.) + "] => [" +
			targetNodeArray[0] + "," +
			FormatUtil.FormatDouble (Double.parseDouble (targetNodeArray[1]), 1, 0, 1.) + "]";
	}

	private static final SingleFactorStateEvolver HullWhiteEvolver (
		final String currency,
		final double sigma,
		final double a,
		final double startingForwardRate)
		throws Exception
	{
		return new SingleFactorStateEvolver (
			FundingLabel.Standard (currency),
			sigma,
			a,
			new Flat (startingForwardRate),
			new BoxMullerGaussian (0., 1.)
		);
	}

	private static void EmitNodeDetails (
		final TrinomialTreeTransitionMetrics trinomialTreeTransitionMetrics,
		final TrinomialTreeNodeMetrics trinomialTreeNodeMetrics)
		throws Exception
	{
		System.out.println ("\n\n\t|----------------------------------------------------------|");

		System.out.println (
			"\t|    NODE [" + trinomialTreeNodeMetrics.timeIndex() + ", " +
				trinomialTreeNodeMetrics.xStochasticIndex() + "]                                           |"
		);

		System.out.println ("\t|----------------------------------------------------------|");

		System.out.println (
			"\t|        Expected Terminal X                  :  " +
				FormatUtil.FormatDouble (trinomialTreeTransitionMetrics.expectedTerminalX(), 1, 6, 1.) + " |"
		);

		System.out.println (
			"\t|        X Variance                           :  " +
				FormatUtil.FormatDouble (trinomialTreeTransitionMetrics.xVariance(), 1, 6, 1.) + " |"
		);

		System.out.println (
			"\t|        X Stochastic Volatility Shift        :  " +
				FormatUtil.FormatDouble (trinomialTreeTransitionMetrics.xStochasticShift(), 1, 6, 1.) + " |"
		);

		System.out.println (
			"\t|        X Tree Stochastic Displacement Index :   " +
				trinomialTreeTransitionMetrics.treeStochasticDisplacementIndex() + "        |"
			);

		System.out.println (
			"\t|        Probability Up                       :  " +
				FormatUtil.FormatDouble (trinomialTreeTransitionMetrics.probabilityUp(), 1, 6, 1.) + " |"
		);

		System.out.println (
			"\t|        Probability Stay                     :  " +
				FormatUtil.FormatDouble (trinomialTreeTransitionMetrics.probabilityStay(), 1, 6, 1.) + " |"
		);

		System.out.println (
			"\t|        Probability Down                     :  " +
				FormatUtil.FormatDouble (trinomialTreeTransitionMetrics.probabilityDown(), 1, 6, 1.) + " |"
		);

		System.out.println (
			"\t|        Node X Value                         :  " +
				FormatUtil.FormatDouble (trinomialTreeNodeMetrics.x(), 1, 6, 1.) + " |"
		);

		System.out.println (
			"\t|        Node Alpha                           :  " +
				FormatUtil.FormatDouble (trinomialTreeNodeMetrics.alpha(), 1, 6, 1.) + " |"
		);

		System.out.println (
			"\t|        Node Short Rate                      :  " +
				FormatUtil.FormatDouble (trinomialTreeNodeMetrics.shortRate(), 1, 6, 1.) + " |"
		);

		System.out.println ("\t|----------------------------------------------------------|");
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

		JulianDate spotDate = DateUtil.CreateFromYMD (2011, DateUtil.MAY, 18);

		double a = 0.1;
		double sigma = 0.01;
		String currency = "USD";
		String[] tenorArray = {
			"3M",
			"6M",
			"9M"
		};
		double[] quoteArray = {
			0.0026,
			0.00412,
			0.00572
		};

		SingleFactorStateEvolver singleFactorStateEvolver = HullWhiteEvolver (
			currency,
			sigma,
			a,
			quoteArray[0]
		);

		TrinomialTreeTransitionMetrics p0p0TrinomialTreeTransitionMetrics =
			singleFactorStateEvolver.evolveTrinomialTree (
				spotDate.julian(),
				spotDate.julian(),
				spotDate.addTenor (tenorArray[0]).julian(),
				null
			);

		EmitNodeDetails (
			p0p0TrinomialTreeTransitionMetrics,
			p0p0TrinomialTreeTransitionMetrics.downNodeMetrics()
		);

		EmitNodeDetails (
			p0p0TrinomialTreeTransitionMetrics,
			p0p0TrinomialTreeTransitionMetrics.stayNodeMetrics()
		);

		EmitNodeDetails (
			p0p0TrinomialTreeTransitionMetrics,
			p0p0TrinomialTreeTransitionMetrics.upNodeMetrics()
		);

		TrinomialTreeTransitionMetrics p1n1TrinomialTreeTransitionMetrics =
			singleFactorStateEvolver.evolveTrinomialTree (
				spotDate.julian(),
				spotDate.addTenor (tenorArray[0]).julian(),
				spotDate.addTenor (tenorArray[1]).julian(),
				p0p0TrinomialTreeTransitionMetrics.downNodeMetrics()
			);

		EmitNodeDetails (
			p1n1TrinomialTreeTransitionMetrics,
			p1n1TrinomialTreeTransitionMetrics.downNodeMetrics()
		);

		EmitNodeDetails (
			p1n1TrinomialTreeTransitionMetrics,
			p1n1TrinomialTreeTransitionMetrics.stayNodeMetrics()
		);

		EmitNodeDetails (
			p1n1TrinomialTreeTransitionMetrics,
			p1n1TrinomialTreeTransitionMetrics.upNodeMetrics()
		);

		TrinomialTreeTransitionMetrics p1n0TrinomialTreeTransitionMetrics =
			singleFactorStateEvolver.evolveTrinomialTree (
				spotDate.julian(),
				spotDate.addTenor (tenorArray[0]).julian(),
				spotDate.addTenor (tenorArray[1]).julian(),
				p0p0TrinomialTreeTransitionMetrics.stayNodeMetrics()
			);

		EmitNodeDetails (
			p1n0TrinomialTreeTransitionMetrics,
			p1n0TrinomialTreeTransitionMetrics.downNodeMetrics()
		);

		EmitNodeDetails (
			p1n0TrinomialTreeTransitionMetrics,
			p1n0TrinomialTreeTransitionMetrics.stayNodeMetrics()
		);

		EmitNodeDetails (
			p1n0TrinomialTreeTransitionMetrics,
			p1n0TrinomialTreeTransitionMetrics.upNodeMetrics()
		);

		TrinomialTreeTransitionMetrics p1p1TrinomialTreeTransitionMetrics =
			singleFactorStateEvolver.evolveTrinomialTree (
				spotDate.julian(),
				spotDate.addTenor (tenorArray[0]).julian(),
				spotDate.addTenor (tenorArray[1]).julian(),
				p0p0TrinomialTreeTransitionMetrics.upNodeMetrics()
			);

		EmitNodeDetails (
			p1p1TrinomialTreeTransitionMetrics,
			p1p1TrinomialTreeTransitionMetrics.downNodeMetrics()
		);

		EmitNodeDetails (
			p1p1TrinomialTreeTransitionMetrics,
			p1p1TrinomialTreeTransitionMetrics.stayNodeMetrics()
		);

		EmitNodeDetails (
			p1p1TrinomialTreeTransitionMetrics,
			p1p1TrinomialTreeTransitionMetrics.upNodeMetrics()
		);

		TrinomialTreeSequenceMetrics trinomialTreeSequenceMetrics =
			singleFactorStateEvolver.evolveTrinomialTreeSequence (spotDate.julian(), 30, 2);

		System.out.println ("\n\t|-----------------------------------|");

		System.out.println ("\t| SOURCE TARGET PROBABILITY METRICS |");

		System.out.println ("\t|-----------------------------------|");

		Map<String, Double> sourceTargetProbabilityMap =
			trinomialTreeSequenceMetrics.sourceTargetTransitionProbability();

		for (Map.Entry<String, Double> sourceTargetProbabilityMapEntry :
			sourceTargetProbabilityMap.entrySet())
		{
			System.out.println (
				"\t|    " + SourceToTarget (sourceTargetProbabilityMapEntry.getKey()) + ": " +
					FormatUtil.FormatDouble (sourceTargetProbabilityMapEntry.getValue(), 1, 6, 1.) + "    |"
			);
		}

		System.out.println ("\t|-----------------------------------|");

		System.out.println ("\n\t|-----------------------------------|");

		System.out.println ("\t| TARGET SOURCE PROBABILITY METRICS |");

		System.out.println ("\t|-----------------------------------|");

		Map<String, Double> targetSourceProbabilityMap =
			trinomialTreeSequenceMetrics.targetSourceTransitionProbability();

		for (Map.Entry<String, Double> targetSourceProbabilityMapEntry :
			targetSourceProbabilityMap.entrySet())
		{
			System.out.println (
				"\t|    " + SourceToTarget (targetSourceProbabilityMapEntry.getKey()) + ": " +
					FormatUtil.FormatDouble (targetSourceProbabilityMapEntry.getValue(), 1, 6, 1.) + "    |"
			);
		}

		System.out.println ("\t|-----------------------------------|");

		EnvManager.TerminateEnv();
	}
}
