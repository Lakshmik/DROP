
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
 * <i>FixedPointFinderOutput</i> holds the result of the fixed point search. It contains the following
 * 	 fields:
 * 	<br>
 * 	<ul>
 * 		<li>Whether the search completed successfully</li>
 * 		<li>The number of iterations, the number of objective function base/derivative calculations, and the
 * 			time taken for the search</li>
 * 		<li>The output from initialization</li>
 * 	</ul>
 *
 * 	It exposes the following Functions:
 *
 *  <ul>
 * 		<li><i>FixedPointFinderOutput</i> Constructor</li>
 * 		<li>Set the Root</li>
 * 		<li>Indicate whether the root is present in the output, i.e., if the finder has successfully completed</li>
 * 		<li>Return the time elapsed for the the full root finding operation</li>
 * 		<li>Return the Root</li>
 * 		<li>Increment the Number of Iterations</li>
 * 		<li>Return the Number of Iterations taken</li>
 * 		<li>Increment the Number of Objective Function Evaluations</li>
 * 		<li>Retrieve the Number of Objective Function Calculations needed</li>
 * 		<li>Increment the Number of Objective Function Derivative Evaluations</li>
 * 		<li>Retrieve the Number of Objective Function Derivative Calculations needed</li>
 * 		<li>Retrieve the Execution Initialization Output</li>
 * 		<li>Return a String Form of the Root Finder Output</li>
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

public class FixedPointFinderOutput
{
	private long _startTime = 0L;
	private int _iterationCount = 0;
	private boolean _hasRoot = false;
	private double _root = Double.NaN;
	private double _rootFindingTime = Double.NaN;
	private int _numberOfObjectiveFunctionCalculations = 0;
	private int _numberOfObjectiveFunctionDerivativesCalculations = 0;
	private ExecutionInitializationOutput _executionInitializationOutput = null;

	/**
	 * <i>FixedPointFinderOutput</i> Constructor
	 * 
	 * @param executionInitializationOutput Execution Initialization Output 1D
	 * 
	 * @throws Exception Thrown if inputs are invalid
	 */

	public FixedPointFinderOutput (
		final ExecutionInitializationOutput executionInitializationOutput)
		throws Exception
	{
		if (null == (_executionInitializationOutput = executionInitializationOutput)) {
			throw new Exception ("FixedPointFinderOutput constructor: Invalid inputs!");
		}

		_startTime = System.nanoTime();
	}

	/**
	 * Set the Root
	 * 
	 * @param root Root
	 * 
	 * @return TRUE - Successfully set
	 */

	public boolean setRoot (
		final double root)
	{
		_rootFindingTime = (System.nanoTime() - _startTime) * 0.000001;

		if (!NumberUtil.IsValid (_root = root)) {
			return false;
		}

		return _hasRoot = true;
	}

	/**
	 * Indicate whether the root is present in the output, i.e., if the finder has successfully completed
	 * 
	 * @return TRUE - Root exists in the output
	 */

	public boolean containsRoot()
	{
		return _hasRoot;
	}

	/**
	 * Return the time elapsed for the the full root finding operation
	 * 
	 * @return Time taken for root finding
	 */

	public double time()
	{
		return _rootFindingTime;
	}

	/**
	 * Return the Root
	 * 
	 * @return Root
	 */

	public double root()
	{
		return _root;
	}

	/**
	 * Increment the Number of Iterations
	 * 
	 * @return TRUE - Successfully Incremented
	 */

	public boolean incrementIterationCount()
	{
		++_iterationCount;
		return true;
	}

	/**
	 * Return the Number of Iterations taken
	 * 
	 * @return Number of Iterations taken
	 */

	public int iterationCount()
	{
		return _iterationCount;
	}

	/**
	 * Increment the Number of Objective Function Evaluations
	 * 
	 * @return TRUE - Successfully incremented
	 */

	public boolean incrementObjectiveFunctionCalculations()
	{
		++_numberOfObjectiveFunctionCalculations;
		return true;
	}

	/**
	 * Retrieve the Number of Objective Function Calculations needed
	 * 
	 * @return Number of Objective Function Calculations needed
	 */

	public int umberOfObjectiveFunctionCalculations()
	{
		return _numberOfObjectiveFunctionCalculations;
	}

	/**
	 * Increment the Number of Objective Function Derivative Evaluations
	 * 
	 * @return TRUE - Successfully incremented
	 */

	public boolean incrementNumberOfObjectiveFunctionDerivativesCalculations()
	{
		++_numberOfObjectiveFunctionDerivativesCalculations;
		return true;
	}

	/**
	 * Retrieve the Number of Objective Function Derivative Calculations needed
	 * 
	 * @return Number of Objective Function Derivative Calculations needed
	 */

	public int numberOfObjectiveFunctionDerivativesCalculations()
	{
		return _numberOfObjectiveFunctionDerivativesCalculations;
	}

	/**
	 * Retrieve the Execution Initialization Output
	 * 
	 * @return Execution Initialization Output
	 */

	public ExecutionInitializationOutput executionInitializationOutput()
	{
		return _executionInitializationOutput;
	}

	/**
	 * Return a String Form of the Root Finder Output
	 * 
	 * @return String Form of the Root Finder Output
	 */

	public String displayString()
	{
		StringBuffer stringBuffer = new StringBuffer();

		stringBuffer.append (_executionInitializationOutput.displayString());

		stringBuffer.append ("\n\tRoot finding Done? " + _hasRoot + " [" + _rootFindingTime + " msec]");

		stringBuffer.append ("\n\tRoot: " + _root);

		stringBuffer.append ("\n\tNum Iterations: " + _iterationCount);

		stringBuffer.append ("\n\tNum OF Calculations: " + _numberOfObjectiveFunctionCalculations);

		stringBuffer.append (
			"\n\tNum OF Derivative Calculations: " + _numberOfObjectiveFunctionDerivativesCalculations
		);

		return stringBuffer.toString();
	}
}
