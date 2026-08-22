
package org.drip.function.r1tor1solver;

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
 * <i>ExecutionControl</i> implements the core fixed point search execution control and customization
 * 	functionality. <i>ExecutionControl</i> is used for a) calculating the absolute tolerance, and b)
 *  determining whether the OF has reached the goal. <i>ExecutionControl</i> determines the execution
 *  termination using its ExecutionControlParams instance. It exposes the following Functions:
 *
 *  <ul>
 * 		<li><i>ExecutionControl</i> Constructor</li>
 * 		<li>Retrieve the Number of Iterations</li>
 * 		<li>Calculate the Absolute Objective Function Tolerance using the Initial Objective Function Value</li>
 * 		<li>Calculate the Absolute Variate Convergence Amount using the Initial Variate</li>
 * 		<li>Check to see if the Objective Function has reached the Goal</li>
 * 		<li>Indicate if the Variate Convergence Check has been Turned On</li>
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

public class ExecutionControl
{
	private ExecutionControlParams _params = null;

	/**
	 * <i>ExecutionControl</i> Constructor
	 * 
	 * @param params Execution Control Parameters
	 * 
	 * @throws Exception Thrown if inputs are invalid
	 */

	public ExecutionControl (
		final ExecutionControlParams params)
		throws Exception
	{
		if (null == (_params = params)) {
			_params = new ExecutionControlParams();
		}
	}

	/**
	 * Retrieve the Number of Iterations
	 * 
	 * @return Number of Solver iterations
	 */

	public int maximumIterationCount()
	{
		return _params.maximumIterationCount();
	}

	/**
	 * Calculate the Absolute Objective Function Tolerance using the Initial Objective Function Value
	 * 
	 * @param initialObjectiveFunctionValue Initial Objective Function Value
	 * 
	 * @return The Absolute Objective Function Tolerance
	 * 
	 * @throws Exception Thrown if the Absolute Tolerance cannot be Calculated
	 */

	public double calculateAbsoluteObjectiveFunctionTolerance (
		final double initialObjectiveFunctionValue)
		throws Exception
	{
		if (!NumberUtil.IsValid (initialObjectiveFunctionValue)) {
			throw new Exception (
				"ExecutionControl::calculateAbsoluteObjectiveFunctionTolerance => Invalid inputs!"
			);
		}

		double absoluteTolerance =
			Math.abs (initialObjectiveFunctionValue) * _params.objectiveFunctionGoalToleranceFactor();

		return !NumberUtil.IsValid (absoluteTolerance) ||
			absoluteTolerance < _params.absoluteObjectiveFunctionToleranceFallback() ? 
			_params.absoluteObjectiveFunctionToleranceFallback() : absoluteTolerance;
	}

	/**
	 * Calculate the Absolute Variate Convergence Amount using the Initial Variate
	 * 
	 * @param initialVariate Initial Variate
	 * 
	 * @return The Absolute Variate Convergence Amount
	 * 
	 * @throws Exception Thrown if Absolute Variate Convergence Amount cannot be calculated
	 */

	public double calculateAbsoluteVariateConvergence (
		final double initialVariate)
		throws Exception
	{
		if (!NumberUtil.IsValid (initialVariate)) {
			throw new Exception ("ExecutionControl::calculateAbsoluteVariateConvergence => Invalid inputs!");
		}

		double absoluteConvergence = Math.abs (initialVariate) * _params.variateConvergenceFactor();

		return !NumberUtil.IsValid (absoluteConvergence) ||
			absoluteConvergence < _params.absoluteVariateConvergenceFallback() ?
			_params.absoluteVariateConvergenceFallback() : absoluteConvergence;
	}

	/**
	 * Check to see if the Objective Function has reached the Goal
	 * 
	 * @param absoluteTolerance Absolute Tolerance
	 * @param objectiveFunctionValue Objective Function Value
	 * @param objectiveFunctionGoal Objective Function Goal
	 * 
	 * @return TRUE - If the Objective Function has reached the Goal
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public boolean objectiveFunctionGoalReached (
		final double absoluteTolerance,
		final double objectiveFunctionValue,
		final double objectiveFunctionGoal)
		throws Exception
	{
		if (!NumberUtil.IsValid (absoluteTolerance) ||
			!NumberUtil.IsValid (objectiveFunctionValue) ||
			!NumberUtil.IsValid (objectiveFunctionGoal))
		{
			throw new Exception ("ExecutionControl::objectiveFunctionGoalReached => Invalid inputs!");
		}

		return absoluteTolerance > Math.abs (objectiveFunctionValue - objectiveFunctionGoal);
	}

	/**
	 * Indicate if the Variate Convergence Check has been Turned On
	 * 
	 * @return TRUE - Variate Convergence Check has been Turned On
	 */

	public boolean variateConvergenceCheckEnabled()
	{
		return _params.variateConvergenceCheckEnabled();
	}
}
