
package org.drip.function.r1tor1solver;

import org.drip.function.definition.R1ToR1;
import org.drip.numerical.common.NumberUtil;

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
 * Copyright (C) 2014 Lakshmi Krishnamurthy
 * Copyright (C) 2013 Lakshmi Krishnamurthy
 * Copyright (C) 2012 Lakshmi Krishnamurthy
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
 * <i>FixedPointFinder</i> is the base abstract class that is implemented by customized invocations, e.g.,
 * 	Newton's method, or any of the bracketing methodologies. <i>FixedPointFinder</i> invokes the core routine
 *  for determining the fixed point from the goal. <i>ExecutionControl</i> determines the execution
 *  termination. The initialization heuristics implements targeted customization of the search.
 *  <i>FixedPointFinder</i> main flow comprises of the following steps:
 * 	<br>
 * 	<ul>
 * 		<li>Initialize the fixed point search zone by determining either a) the brackets, or b) the starting
 * 			variate.</li>
 * 		<li>Compute the absolute OF tolerance that establishes the attainment of the fixed point.</li>
 * 		<li>Launch the variate iterator that iterates the variate.</li>
 * 		<li>Iterate until the desired tolerance has been attained</li>
 * 		<li>Return the fixed point output.</li>
 * 	</ul>
 * 
 * 	Fixed point finders that derive from this provide implementations for the following:
 * 	<br>
 * 	<ul>
 * 		<li>- Variate initialization: They may choose either bracketing initializer, or the convergence
 * 			initializer - functionality is provided for both in this module.</li>
 * 		<li>- Variate Iteration: Variates are iterated using a) any of the standard primitive built-in
 * 			variate iterators (or custom ones), or b) a variate selector scheme for each iteration.</li>
 * 	</ul>
 * 
 *  It exposes the following Functions:
 *
 *  <ul>
 * 		<li>Invoke the Solution 1D Root Finding Sequence #1</li>
 * 		<li>Invoke the Solution 1D Root Finding Sequence #2</li>
 *  </ul>
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/function/README.md">R<sup>d</sup> To R<sup>d</sup> Function Analysis</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/function/r1tor1solver/README.md">Built-in R<sup>1</sup> To R<sup>1</sup> Solvers</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public abstract class FixedPointFinder
{
	protected boolean _whine = false;
	protected R1ToR1 _objectiveFunction = null;
	protected ExecutionControl _executionControl = null;
	protected double _objectiveFunctionValueGoal = Double.NaN;

	protected FixedPointFinder (
		final double objectiveFunctionValueGoal,
		final R1ToR1 objectiveFunction,
		final ExecutionControl executionControl,
		final boolean whine)
		throws Exception
	{
		if (!NumberUtil.IsValid (_objectiveFunctionValueGoal = objectiveFunctionValueGoal) ||
			null == (_objectiveFunction = objectiveFunction))
		{
			throw new Exception ("FixedPointFinder constructor: Invalid inputs");
		}

		_executionControl = new ExecutionControl (null);

		_whine = whine;
	}

	protected abstract boolean iterateVariate (
		final IteratedVariate iteratedVariate,
		final FixedPointFinderOutput fixedPointFinderOutput
	);

	protected abstract ExecutionInitializationOutput initializeVariateZone (
		final InitializationHeuristics initializationHeuristics
	);

	/**
	 * Invoke the Solution 1D Root Finding Sequence #1
	 * 
	 * @param initializationHeuristics Optional Initialization Heuristics
	 * 
	 * @return Root Finder Solution Object for the Variate
	 */

	public FixedPointFinderOutput findRoot (
		final InitializationHeuristics initializationHeuristics)
	{
		FixedPointFinderOutput fixedPointFinderOutput = null;

		ExecutionInitializationOutput executionInitializationOutput =
			initializeVariateZone (initializationHeuristics);

		if (null == executionInitializationOutput || !executionInitializationOutput.isDone()) {
			return null;
		}

		try {
			fixedPointFinderOutput = new FixedPointFinderOutput (executionInitializationOutput);

			if (!fixedPointFinderOutput.incrementObjectiveFunctionCalculations()) {
				return fixedPointFinderOutput;
			}

			double objectiveFunctionValue =
				_objectiveFunction.evaluate (executionInitializationOutput.startingVariate());

			double absoluteTolerance =
				_executionControl.calculateAbsoluteObjectiveFunctionTolerance (objectiveFunctionValue);

			double absoluteConvergence = _executionControl.calculateAbsoluteVariateConvergence (
				executionInitializationOutput.startingVariate()
			);

			IteratedVariate iteratedVariate =
				new IteratedVariate (executionInitializationOutput, objectiveFunctionValue);

			int pendingIterationCount = _executionControl.maximumIterationCount();

			while (!_executionControl.objectiveFunctionGoalReached (
				absoluteTolerance,
				iteratedVariate.objectiveFunctionValue(),
				_objectiveFunctionValueGoal
			))
			{
				double previousVariate = iteratedVariate.x();

				if (!fixedPointFinderOutput.incrementIterationCount() ||
					0 == --pendingIterationCount ||
					!iterateVariate (iteratedVariate, fixedPointFinderOutput))
				{
					return fixedPointFinderOutput;
				}

				if (_executionControl.variateConvergenceCheckEnabled() &&
					Math.abs (previousVariate - iteratedVariate.x()) < absoluteConvergence)
				{
					break;
				}
			}

			fixedPointFinderOutput.setRoot (iteratedVariate.x());
		} catch (Exception e) {
			if (_whine) {
				e.printStackTrace();
			}
		}

		return fixedPointFinderOutput;
	}

	/**
	 * Invoke the Solution 1D Root Finding Sequence #2
	 * 
	 * @return Root finder Solution Object for the variate
	 */

	public FixedPointFinderOutput findRoot()
	{
		return findRoot (null);
	}
}
