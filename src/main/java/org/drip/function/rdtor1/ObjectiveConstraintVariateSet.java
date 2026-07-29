
package org.drip.function.rdtor1;

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
 * <i>ObjectiveConstraintVariateSet</i> holds a R<sup>d</sup> To R<sup>1</sup> Variates corresponding to the
 * 	Objective Function and the Constraint Function respectively. It exposes the following Functions:
 *
 *  <ul>
 * 		<li>Make a Unitary Variate Set</li>
 * 		<li>Make a Variate Set with/without Constraint</li>
 * 		<li>Make a Variate Set using a Pre-set Objective Variate Array with/without Constraint</li>
 * 		<li>Partition the Variate Array into the Objective Function Input Variates and the Constraint Variate</li>
 * 		<li><i>ObjectiveConstraintVariateSet</i> Constructor</li>
 * 		<li>Retrieve the Array of the Objective Function Variates</li>
 * 		<li>Retrieve the Array of the Constraint Function Variates</li>
 * 		<li>Unify the Objective Function and the Constraint Function Input Variate Set</li>
 *  </ul>
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/function/README.md">R<sup>d</sup> To R<sup>d</sup> Function Analysis</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/function/rdtor1/README.md">Built-in R<sup>d</sup> To R<sup>1</sup> Functions</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class ObjectiveConstraintVariateSet
{
	private double[] _kktCoefficientArray = null;
	private double[] _problemVariableArray = null;

	/**
	 * Make a Unitary Variate Set
	 * 
	 * @param variateCount Number of Variates
	 * 
	 * @return Unitary Variate Set
	 */

	public static final double[] Unitary (
		final int variateCount)
	{
		if (0 >= variateCount) {
			return null;
		}

		double[] variateArray = new double[variateCount];

		for (int variateIndex = 0; variateIndex < variateCount; ++variateIndex) {
			variateArray[variateIndex] = 1.;
		}

		return variateArray;
	}

	/**
	 * Make a Variate Set with/without Constraint
	 * 
	 * @param problemVariableCount Number of the Objective Function Variables
	 * @param kktCoefficientCount Number of the Constraint Function Variates
	 * 
	 * @return Variate Set with/without Constraint
	 */

	public static final double[] Uniform (
		final int problemVariableCount,
		final int kktCoefficientCount)
	{
		if (0 >= problemVariableCount) {
			return null;
		}

		double[] variateArray = new double[problemVariableCount + kktCoefficientCount];

		for (int problemVariableIndex = 0;
			problemVariableIndex < problemVariableCount;
			++problemVariableIndex)
		{
			variateArray[problemVariableIndex] = 1. / problemVariableCount;
		}

		for (int kktCoefficientIndex = 0; kktCoefficientIndex < kktCoefficientCount; ++kktCoefficientIndex) {
			variateArray[kktCoefficientIndex + problemVariableCount] = 0.;
		}

		return variateArray;
	}

	/**
	 * Make a Variate Set using a Pre-set Objective Variate Array with/without Constraint
	 * 
	 * @param problemVariableArray Array of Pre-set Objective Variates
	 * @param kktCoefficientArray Number of the Constraint Function Variates
	 * 
	 * @return Variate Set using a Pre-set Objective Variate Array with/without Constraint
	 */

	public static final double[] Preset (
		final double[] problemVariableArray,
		final int kktCoefficientArray)
	{
		if (null == problemVariableArray) {
			return null;
		}

		int problemVariableCount = problemVariableArray.length;

		if (0 >= problemVariableCount) {
			return null;
		}

		double[] variateArray = new double[problemVariableCount + kktCoefficientArray];

		for (int problemVariableIndex = 0;
			problemVariableIndex < problemVariableCount;
			++problemVariableIndex)
		{
			variateArray[problemVariableIndex] = problemVariableArray[problemVariableIndex];
		}

		for (int kktCoefficientIndex = 0; kktCoefficientIndex < kktCoefficientArray; ++kktCoefficientIndex) {
			variateArray[kktCoefficientIndex + problemVariableCount] = 0.;
		}

		return variateArray;
	}

	/**
	 * Partition the Variate Array into the Objective Function Input Variates and the Constraint Variate
	 * 
	 * @param variateArray The Input Variate Array
	 * @param problemVariableCount Number of the Objective Function Variates
	 * 
	 * @return The ObjectiveConstraintVariateSet Instance
	 */

	public static final ObjectiveConstraintVariateSet Partition (
		final double[] variateArray,
		final int problemVariableCount)
	{
		if (null == variateArray || 0 == problemVariableCount) {
			return null;
		}

		double[] problemVariableArray = new double[problemVariableCount];
		double[] kktCoefficientArray = new double[variateArray.length - problemVariableCount];

		if (problemVariableCount >= variateArray.length) {
			return null;
		}

		for (int problemVariableIndex = 0;
			problemVariableIndex < problemVariableCount;
			++problemVariableIndex)
		{
			problemVariableArray[problemVariableIndex] = variateArray[problemVariableIndex];
		}

		for (int variateIndex = problemVariableCount; variateIndex < variateArray.length; ++variateIndex) {
			kktCoefficientArray[variateIndex - problemVariableCount] = variateArray[variateIndex];
		}

		try {
			return new ObjectiveConstraintVariateSet (problemVariableArray, kktCoefficientArray);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * <i>ObjectiveConstraintVariateSet</i> Constructor
	 * 
	 * @param problemVariableArray Array of the Objective Function Variates
	 * @param kktCoefficientArray Array of the Constraint Function Variates
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public ObjectiveConstraintVariateSet (
		final double[] problemVariableArray,
		final double[] kktCoefficientArray)
		throws Exception
	{
		if (null == (_problemVariableArray = problemVariableArray) ||
				!NumberUtil.IsValid (_problemVariableArray) ||
			null == (_kktCoefficientArray = kktCoefficientArray) ||
				!NumberUtil.IsValid (kktCoefficientArray))
		{
			throw new Exception ("ObjectiveConstraintVariateSet Constructor => Invalid Inputs!");
		}
	}

	/**
	 * Retrieve the Array of the Objective Function Variates
	 * 
	 * @return The Array of the Objective Function Variates
	 */

	public double[] problemVariableArray()
	{
		return _problemVariableArray;
	}

	/**
	 * Retrieve the Array of the Constraint Function Variates
	 * 
	 * @return The Array of the Constraint Function Variates
	 */

	public double[] kktCoefficientArray()
	{
		return _kktCoefficientArray;
	}

	/**
	 * Unify the Objective Function and the Constraint Function Input Variate Set
	 * 
	 * @return The Unified Objective Function and the Constraint Function Input Variate Set
	 */

	public double[] unify()
	{
		double[] variateArray = new double[_problemVariableArray.length + _kktCoefficientArray.length];

		for (int problemVariableIndex = 0;
			problemVariableIndex < _problemVariableArray.length;
			++problemVariableIndex)
		{
			variateArray[problemVariableIndex] = _problemVariableArray[problemVariableIndex];
		}

		for (int kktCoefficientIndex = 0;
			kktCoefficientIndex < _kktCoefficientArray.length;
			++kktCoefficientIndex)
		{
			variateArray[_problemVariableArray.length + kktCoefficientIndex] =
				_kktCoefficientArray[kktCoefficientIndex];
		}

		return variateArray;
	}
}
