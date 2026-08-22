
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
 * <i>ExecutionInitializationOutput</i> holds the output of the root initializer calculation. The following
 * 	are the fields held by ExecutionInitializationOutput:
 * <br>
 * 	<ul>
 * 		<li>Whether the initialization completed successfully</li>
 * 		<li>The number of iterations, the number of objective function calculations, and the time taken for
 * 			the initialization</li>
 * 		<li>The starting variate from the initialization</li>
 * </ul>
 *
 * 	It exposes the following Functions:
 *
 *  <ul>
 * 		<li>Increment the Number of Iterations</li>
 * 		<li>Return The Number of Iterations Consumed</li>
 * 		<li>Increment the Number of Objective Function Evaluations</li>
 * 		<li>Retrieve the Number of Objective Function Calculations Needed</li>
 * 		<li>Increment the Number of Objective Function Derivative Evaluations</li>
 * 		<li>Retrieve the Number of Objective Function Derivative Calculations Needed</li>
 * 		<li>Indicate if the Execution Initialization is Done</li>
 * 		<li>Return the Time Elapsed for the Execution Initialization Operation</li>
 * 		<li>Set the Starting Variate</li>
 * 		<li>Return the Starting Variate</li>
 * 		<li>Return a String Form of the Initializer output</li>
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

public abstract class ExecutionInitializationOutput
{
	private long _startTime = 0L;
	private boolean _done = false;
	private int _iterationCount = 0;
	private double _time = Double.NaN;
	private double _startingVariate = Double.NaN;
	private int _objectiveFunctionCalculationCount = 0;
	private int _objectiveFunctionDerivativeCalculationCount = 0;

	protected ExecutionInitializationOutput()
	{
		_startTime = System.nanoTime();
	}

	protected ExecutionInitializationOutput (
		final ExecutionInitializationOutput other)
		throws Exception
	{
		if (null == other) {
			throw new Exception ("ExecutionInitializationOutput constructor: Invalid inputs!");
		}

		_done = other._done;
		_time = other._time;
		_startTime = other._startTime;
		_iterationCount = other._iterationCount;
		_startingVariate = other._startingVariate;
		_objectiveFunctionCalculationCount = other._objectiveFunctionCalculationCount;
		_objectiveFunctionDerivativeCalculationCount = other._objectiveFunctionDerivativeCalculationCount;
	}

	protected boolean done()
	{
		_time = (System.nanoTime() - _startTime) * 0.000001;

		return _done = true;
	}

	/**
	 * Increment the Number of Iterations
	 * 
	 * @return TRUE - Number of Iterations Successfully Incremented
	 */

	public final boolean incrementIterationCount()
	{
		++_iterationCount;
		return true;
	}

	/**
	 * Return The Number of Iterations Consumed
	 * 
	 * @return Number of Iterations Consumed
	 */

	public final int iterationCount()
	{
		return _iterationCount;
	}

	/**
	 * Increment the Number of Objective Function Evaluations
	 * 
	 * @return TRUE - Number of Objective Function Evaluations Successfully Incremented
	 */

	public final boolean incrementObjectiveFunctionCalculationCount()
	{
		++_objectiveFunctionCalculationCount;
		return true;
	}

	/**
	 * Retrieve the Number of Objective Function Calculations Needed
	 * 
	 * @return Number of Objective Function Calculations Needed
	 */

	public final int objectiveFunctionCalculationCount()
	{
		return _objectiveFunctionCalculationCount;
	}

	/**
	 * Increment the Number of Objective Function Derivative Evaluations
	 * 
	 * @return TRUE - Number of Objective Function Derivative Evaluations Successfully Incremented
	 */

	public final boolean incrementObjectiveFunctionDerivativeCalculationCount()
	{
		++_objectiveFunctionDerivativeCalculationCount;
		return true;
	}

	/**
	 * Retrieve the Number of Objective Function Derivative Calculations Needed
	 * 
	 * @return Number of Objective Function Derivative Calculations Needed
	 */

	public final int objectiveFunctionDerivativeCalculationCount()
	{
		return _objectiveFunctionDerivativeCalculationCount;
	}

	/**
	 * Indicate if the Execution Initialization is Done
	 * 
	 * @return TRUE - Execution Initialization is Done
	 */

	public final boolean isDone()
	{
		return _done;
	}

	/**
	 * Return the Time Elapsed for the Execution Initialization Operation
	 * 
	 * @return Execution Initialization Time
	 */

	public final double time()
	{
		return _time;
	}

	/**
	 * Set the Starting Variate
	 * 
	 * @param startingVariate Starting Variate
	 * 
	 * @return TRUE - Starting Variate set successfully
	 */

	public boolean setStartingVariate (
		final double startingVariate)
	{
		if (!NumberUtil.IsValid (startingVariate)) {
			return false;
		}

		_startingVariate = startingVariate;
		return true;
	}

	/**
	 * Return the Starting Variate
	 * 
	 * @return Starting Variate
	 */

	public double startingVariate()
	{
		return _startingVariate;
	}

	/**
	 * Return a String Form of the Initializer output
	 * 
	 * @return String Form of the Initializer output
	 */

	public String displayString()
	{
		StringBuffer sb = new StringBuffer();

		sb.append ("\t\tInitialization Done? " + isDone() + " [" + time() + " msec]");

		sb.append ("\n\t\tNum Iterations: " + iterationCount());

		sb.append ("\n\t\tNum OF Calculations: " + objectiveFunctionCalculationCount());

		sb.append ("\n\t\tNum OF Derivative Calculations: " + objectiveFunctionDerivativeCalculationCount());

		sb.append ("\n\t\tStarting Variate: " + startingVariate());

		return sb.toString();
	}
}
