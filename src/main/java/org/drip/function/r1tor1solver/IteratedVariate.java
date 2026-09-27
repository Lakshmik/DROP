
package org.drip.function.r1tor1solver;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

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
 * <i>IteratedVariate</i> holds the variate and the corresponding value for the objective function during
 * 	each iteration. It exposes the following Functions:
 *
 *  <ul>
 * 		<li><i>IteratedVariate</i> Constructor</li>
 * 		<li>Retrieve the Variate</li>
 * 		<li>Set the Variate</li>
 * 		<li>Retrieve the Objective Function Value</li>
 * 		<li>Set the Objective Function Value</li>
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

public class IteratedVariate
{
	private static final List<Double> ITERATED_X_LIST = new ArrayList<Double>();

	private static final List<Double> ITERATED_OBJECTIVE_FUNCTION_VALUE_LIST = new ArrayList<Double>();

	private double _x = Double.NaN;
	private double _objectiveFunctionValue = Double.NaN;

	/**
	 * <i>IteratedVariate</i> Constructor
	 * 
	 * @param executionInitializationOutput Execution Initialization Output
	 * @param objectiveFunctionValue Objective Function Value
	 * 
	 * @throws Exception Thrown if Inputs are Invalid
	 */

	public IteratedVariate (
		final ExecutionInitializationOutput executionInitializationOutput,
		final double objectiveFunctionValue)
		throws Exception
	{
		if (null == executionInitializationOutput ||
			!NumberUtil.IsValid (_objectiveFunctionValue = objectiveFunctionValue))
		{
			throw new Exception ("IteratedVariate Constructor: Invalid Inputs");
		}

		ITERATED_X_LIST.clear();

		ITERATED_OBJECTIVE_FUNCTION_VALUE_LIST.clear();

		ITERATED_X_LIST.add (_x = executionInitializationOutput.startingVariate());

		ITERATED_OBJECTIVE_FUNCTION_VALUE_LIST.add (objectiveFunctionValue);
	}

	/**
	 * Retrieve the Variate
	 * 
	 * @return Variate
	 */

	public double x()
	{
		return _x;
	}

	/**
	 * Set the Variate
	 * 
	 * @param x Variate
	 * 
	 * @return TRUE - Variate set successfully
	 */

	public boolean setX (
		final double x)
	{
		if (!NumberUtil.IsValid (x)) {
			return false;
		}

		ITERATED_X_LIST.add (_x = x);

		return true;
	}

	/**
	 * Retrieve the Objective Function Value
	 * 
	 * @return The Objective Function Value
	 */

	public double objectiveFunctionValue()
	{
		return _objectiveFunctionValue;
	}

	/**
	 * Set the Objective Function Value
	 * 
	 * @param objectiveFunctionValue Objective Function Value
	 * 
	 * @return TRUE - Objective Function Value set successfully
	 */

	public boolean setObjectiveFunctionValue (
		final double objectiveFunctionValue)
	{
		if (!NumberUtil.IsValid (objectiveFunctionValue)) {
			return false;
		}

		ITERATED_OBJECTIVE_FUNCTION_VALUE_LIST.add (_objectiveFunctionValue = objectiveFunctionValue);

		return true;
	}

	/**
	 * Compute the First and the Second Derivatives
	 * 
	 * @return The First and the Second Derivatives
	 */

	public double[] firstAndSecondDerivative()
	{
		double[] firstAndSecondDerivativeArray = new double[2];
		firstAndSecondDerivativeArray[1] = Double.NaN;
		firstAndSecondDerivativeArray[0] = Double.NaN;

		int size = ITERATED_OBJECTIVE_FUNCTION_VALUE_LIST.size();

		if (1 >= size) {
			return firstAndSecondDerivativeArray;
		}

		double xSizeMinus1 = ITERATED_X_LIST.get (size - 1);

		double xSizeMinus2 = ITERATED_X_LIST.get (size - 2);

		double ySizeMinus1 = ITERATED_OBJECTIVE_FUNCTION_VALUE_LIST.get (size - 1);

		double ySizeMinus2 = ITERATED_OBJECTIVE_FUNCTION_VALUE_LIST.get (size - 2);

		firstAndSecondDerivativeArray[0] = (ySizeMinus2 - ySizeMinus1) / (xSizeMinus2 - xSizeMinus1);

		if (2 == size) {
			return firstAndSecondDerivativeArray;
		}

		TreeMap<Double, Double> xObjectiveFunctionValueMap = new TreeMap<Double, Double>();

		xObjectiveFunctionValueMap.put (xSizeMinus1, ySizeMinus1);

		xObjectiveFunctionValueMap.put (xSizeMinus2, ySizeMinus2);

		xObjectiveFunctionValueMap.put (
			ITERATED_X_LIST.get (size - 3),
			ITERATED_OBJECTIVE_FUNCTION_VALUE_LIST.get (size - 3)
		);

		int index = 0;
		double xLast = Double.NaN;
		double xFirst = Double.NaN;
		double xPrevious = Double.NaN;
		double yPrevious = Double.NaN;
		firstAndSecondDerivativeArray[1] = 0.;

		for (double x : xObjectiveFunctionValueMap.keySet()) {
			if (2 < index) {
				break;
			}

			double y = xObjectiveFunctionValueMap.get (x);

			if (0 == index) {
				xFirst = x;
			} else {
				firstAndSecondDerivativeArray[1] += ((y - yPrevious) / ((xLast = x) - xPrevious));
			}

			xPrevious = x;
			yPrevious = y;
			++index;
		}

		firstAndSecondDerivativeArray[1] *= 2. / (xLast - xFirst);
		return firstAndSecondDerivativeArray;
	}
}
