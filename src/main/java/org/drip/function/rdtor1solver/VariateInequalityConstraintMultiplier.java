
package org.drip.function.rdtor1solver;

import org.drip.function.definition.SizedVector;
import org.drip.function.rdtor1.BoundMultivariate;
import org.drip.numerical.common.NumberUtil;
import org.drip.service.common.FormatUtil;

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
 * <i>VariateInequalityConstraintMultiplier</i> holds the Variates and their Inequality Constraint
 * 	Multipliers in either the Absolute or the Incremental Forms. It exposes the following Functions:
 *
 *  <ul>
 * 		<li>Add the Specified <i>VariateInequalityConstraintMultiplier</i> Instances together #1</li>
 * 		<li>Add the Specified <i>VariateInequalityConstraintMultiplier</i> Instances together #2</li>
 * 		<li>Subtract the Second <i>VariateInequalityConstraintMultiplier</i> Instance from the First #1</li>
 * 		<li>Subtract the Second <i>VariateInequalityConstraintMultiplier</i> Instance from the First #2</li>
 * 		<li>Compare the Specified <i>VariateInequalityConstraintMultiplier</i> Instances</li>
 * 		<li><i>VariateInequalityConstraintMultiplier</i> Constructor</li>
 * 		<li>Retrieve the Incremental Flag</li>
 * 		<li>Retrieve the Array of Problem Variables</li>
 * 		<li>Retrieve the Constraint Multipliers</li>
 * 		<li>Retrieve the Consolidated Variate/Constraint Multiplier Array</li>
 * 		<li>Retrieve the Sized Vector Instance corresponding to the Increment</li>
 * 		<li>Retrieve the Sized Vector Instance corresponding to the Variate Increment</li>
 *  </ul>
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/function/README.md">R<sup>d</sup> To R<sup>d</sup> Function Analysis</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/function/rdtor1solver/README.md">R<sup>d</sup> To R<sup>1</sup> Solver</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class VariateInequalityConstraintMultiplier
{

	/**
	 * Flag Indicating whether the Variate Contents are to be Logged "Before" Bounding
	 */

	public static boolean s_preBoundBlog = false;

	/**
	 * Flag Indicating whether the Variate Contents are to be Logged "After" Bounding
	 */

	public static boolean s_postBoundBlog = false;

	private boolean _incremental = false;
	private double[] _kktCoefficientArray = null;
	private double[] _problemVariableArray = null;

	/**
	 * Add the Specified <i>VariateInequalityConstraintMultiplier</i> Instances together #1
	 * 
	 * @param baseVariateInequalityConstriantMultiplier
	 * 		<i>VariateInequalityConstraintMultiplier</i> Instance Base
	 * @param incrementVariateInequalityConstriantMultiplier
	 * 		<i>VariateInequalityConstraintMultiplier</i> Instance Increment
	 * @param incrementFactor The Increment Factor - 1. corresponds to Full Increment
	 * @param boundMultivariateFunctionArray Array of Bounded Multivariate Stubs
	 * 
	 * @return The Added <i>VariateInequalityConstraintMultiplier</i> Instance
	 */

	public static final VariateInequalityConstraintMultiplier Add (
		final VariateInequalityConstraintMultiplier baseVariateInequalityConstriantMultiplier,
		final VariateInequalityConstraintMultiplier incrementVariateInequalityConstriantMultiplier,
		final double incrementFactor,
		final BoundMultivariate[] boundMultivariateFunctionArray)
	{
		if (null == baseVariateInequalityConstriantMultiplier ||
			null == incrementVariateInequalityConstriantMultiplier ||
			baseVariateInequalityConstriantMultiplier.incremental() ||
			!incrementVariateInequalityConstriantMultiplier.incremental() ||
			!NumberUtil.IsValid (incrementFactor) || 1. < incrementFactor)
		{
			return null;
		}

		double[] baseKKTCoefficientArray = baseVariateInequalityConstriantMultiplier.kktCoefficientArray();

		double[] baseProblemVariableArray = baseVariateInequalityConstriantMultiplier.problemVariableArray();

		double[] incrementKKTCoefficientArray =
			incrementVariateInequalityConstriantMultiplier.kktCoefficientArray();

		double[] incrementProblemVariableArray =
			incrementVariateInequalityConstriantMultiplier.problemVariableArray();

		double[] problemVariableArray = new double[baseProblemVariableArray.length];
		int baseKKTCoefficientCount = null == baseKKTCoefficientArray ? 0 : baseKKTCoefficientArray.length;
		double[] kktCoefficientArray = 0 == baseKKTCoefficientCount ?
			null : new double[baseKKTCoefficientCount];
		int incrementKKTCoefficientCount = null == incrementKKTCoefficientArray ?
			0 : incrementKKTCoefficientArray.length;
		int boundMultivariateFunctionCount = null == boundMultivariateFunctionArray ?
			0 : boundMultivariateFunctionArray.length;

		if (baseProblemVariableArray.length != incrementProblemVariableArray.length ||
			baseKKTCoefficientCount != incrementKKTCoefficientCount)
		{
			return null;
		}

		for (int problemVariableIndex = 0;
			problemVariableIndex < baseProblemVariableArray.length;
			++problemVariableIndex)
		{
			problemVariableArray[problemVariableIndex] = baseProblemVariableArray[problemVariableIndex] +
				incrementFactor * incrementProblemVariableArray[problemVariableIndex];
		}

		if (s_preBoundBlog) {
			String dump = "\tB";

			for (int problemVariableIndex = 0;
				problemVariableIndex < baseProblemVariableArray.length;
				++problemVariableIndex)
			{
				dump += " " + FormatUtil.FormatDouble (
					problemVariableArray[problemVariableIndex],
					2,
					2,
					100.
				) + " |";
			}

			System.out.println (dump);
		}

		for (int kktCoefficientIndex = 0;
			kktCoefficientIndex < baseKKTCoefficientCount;
			++kktCoefficientIndex)
		{
			if (0. > (
				kktCoefficientArray[kktCoefficientIndex] = baseKKTCoefficientArray[kktCoefficientIndex] +
					incrementFactor * incrementKKTCoefficientArray[kktCoefficientIndex]
			))
			{
				kktCoefficientArray[kktCoefficientIndex] = 0.;
			}

			if (boundMultivariateFunctionCount <= kktCoefficientIndex ||
				null == boundMultivariateFunctionArray[kktCoefficientIndex])
			{
				continue;
			}

			int boundVariateIndex = boundMultivariateFunctionArray[kktCoefficientIndex].boundVariateIndex();

			try {
				if (boundMultivariateFunctionArray[kktCoefficientIndex].violated (
					problemVariableArray[boundVariateIndex]
				))
				{
					problemVariableArray[boundVariateIndex] =
						boundMultivariateFunctionArray[kktCoefficientIndex].boundValue();
				}
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		if (s_postBoundBlog) {
			String dump = "\tA";

			for (int problemVariableIndex = 0;
				problemVariableIndex < baseProblemVariableArray.length;
				++problemVariableIndex)
			{
				dump += " " + FormatUtil.FormatDouble (
					problemVariableArray[problemVariableIndex],
					2,
					2,
					100.
				) + " |";
			}

			System.out.println (dump);
		}

		try {
			return new VariateInequalityConstraintMultiplier (
				false,
				problemVariableArray,
				kktCoefficientArray
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Add the Specified <i>VariateInequalityConstraintMultiplier</i> Instances together #2
	 * 
	 * @param baseVariateInequalityConstriantMultiplier
	 * 		<i>VariateInequalityConstraintMultiplier</i> Instance Base
	 * @param incrementVariateInequalityConstriantMultiplier
	 *  	<i>VariateInequalityConstraintMultiplier</i> Instance Increment
	 * @param boundMultivariateFunctionArray Array of Bounded Multivariate Stubs
	 * 
	 * @return The Added <i>VariateInequalityConstraintMultiplier</i> Instance
	 */

	public static final VariateInequalityConstraintMultiplier Add (
		final VariateInequalityConstraintMultiplier baseVariateInequalityConstriantMultiplier,
		final VariateInequalityConstraintMultiplier incrementVariateInequalityConstriantMultiplier,
		final BoundMultivariate[] boundMultivariateFunctionArray)
	{
		return Add (
			baseVariateInequalityConstriantMultiplier,
			incrementVariateInequalityConstriantMultiplier,
			1.,
			boundMultivariateFunctionArray
		);
	}

	/**
	 * Subtract the Second <i>VariateInequalityConstraintMultiplier</i> Instance from the First #1
	 * 
	 * @param baseVariateInequalityConstraintMultiplier
	 * 		<i>VariateInequalityConstraintMultiplier</i> Instance Base
	 * @param incrementVariateInequalityConstraintMultiplier
	 * 		<i>VariateInequalityConstraintMultiplier</i> Instance Increment
	 * @param incrementFactor The Increment Factor - 1. corresponds to Full Increment
	 * @param boundMultivariateFunctionArray Array of Bounded Multivariate Stubs
	 * 
	 * @return The Subtracted <i>VariateInequalityConstraintMultiplier</i> Instance
	 */

	public static final VariateInequalityConstraintMultiplier Subtract (
		final VariateInequalityConstraintMultiplier baseVariateInequalityConstraintMultiplier,
		final VariateInequalityConstraintMultiplier incrementVariateInequalityConstraintMultiplier,
		final double incrementFactor,
		final BoundMultivariate[] boundMultivariateFunctionArray)
	{
		if (null == baseVariateInequalityConstraintMultiplier ||
			null == incrementVariateInequalityConstraintMultiplier ||
			baseVariateInequalityConstraintMultiplier.incremental() ||
			!incrementVariateInequalityConstraintMultiplier.incremental() ||
			!NumberUtil.IsValid (incrementFactor) || 1. < incrementFactor)
		{
			return null;
		}

		double[] baseKKTCoefficientArray = baseVariateInequalityConstraintMultiplier.kktCoefficientArray();

		double[] baseProblemVariableArray = baseVariateInequalityConstraintMultiplier.problemVariableArray();

		double[] incrementKKTCoefficientArray =
			incrementVariateInequalityConstraintMultiplier.kktCoefficientArray();

		double[] incrementProblemVariableArray =
			incrementVariateInequalityConstraintMultiplier.problemVariableArray();

		double[] problemVariableArray = new double[baseProblemVariableArray.length];
		int kktCoefficientCount = null == baseKKTCoefficientArray ? 0 : baseKKTCoefficientArray.length;
		double[] kktCoefficientArray = 0 == kktCoefficientCount ? null : new double[kktCoefficientCount];
		int incrementKKTCoefficientCount = null == incrementKKTCoefficientArray ? 0 :
			incrementKKTCoefficientArray.length;
		int boundMultivariateFunctionCount = null == boundMultivariateFunctionArray ?
			0 : boundMultivariateFunctionArray.length;

		if (baseProblemVariableArray.length != incrementProblemVariableArray.length ||
			kktCoefficientCount != incrementKKTCoefficientCount)
		{
			return null;
		}

		for (int problemVariableIndex = 0;
			problemVariableIndex < baseProblemVariableArray.length;
			++problemVariableIndex)
		{
			problemVariableArray[problemVariableIndex] =
				baseProblemVariableArray[problemVariableIndex] -
				incrementFactor * incrementProblemVariableArray[problemVariableIndex];
		}

		if (s_preBoundBlog) {
			String dump = "\tB";

			for (int problemVariableIndex = 0;
				problemVariableIndex < baseProblemVariableArray.length;
				++problemVariableIndex)
			{
				dump += " " + FormatUtil.FormatDouble (
					problemVariableArray[problemVariableIndex],
					2,
					2,
					100.
				) + " |";
			}

			System.out.println (dump);
		}

		for (int kktCoefficientIndex = 0; kktCoefficientIndex < kktCoefficientCount; ++kktCoefficientIndex) {
			if (0. > (
				kktCoefficientArray[kktCoefficientIndex] = baseKKTCoefficientArray[kktCoefficientIndex] -
					incrementFactor * incrementKKTCoefficientArray[kktCoefficientIndex]
			))
			{
				kktCoefficientArray[kktCoefficientIndex] = 0.;
			}

			if (boundMultivariateFunctionCount <= kktCoefficientIndex ||
				null == boundMultivariateFunctionArray[kktCoefficientIndex])
			{
				continue;
			}

			int boundVariateIndex = boundMultivariateFunctionArray[kktCoefficientIndex].boundVariateIndex();

			try {
				if (boundMultivariateFunctionArray[kktCoefficientIndex].violated (
					problemVariableArray[boundVariateIndex]
				))
				{
					problemVariableArray[boundVariateIndex] =
						boundMultivariateFunctionArray[kktCoefficientIndex].boundValue();
				}
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		if (s_postBoundBlog) {
			String dump = "\tA";

			for (int problemVariableIndex = 0;
				problemVariableIndex < baseProblemVariableArray.length;
				++problemVariableIndex)
			{
				dump += " " + FormatUtil.FormatDouble (
					problemVariableArray[problemVariableIndex],
					2,
					2,
					100.
				) + " |";
			}

			System.out.println (dump);
		}

		try {
			return new VariateInequalityConstraintMultiplier (
				false,
				problemVariableArray,
				kktCoefficientArray
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Subtract the Second <i>VariateInequalityConstraintMultiplier</i> Instance from the First #2
	 * 
	 * @param baseVariateInequalityConstriantMultiplier
	 * 		<i>VariateInequalityConstraintMultiplier</i> Instance Base
	 * @param incrementVariateInequalityConstriantMultiplier
	 * 		<i>VariateInequalityConstraintMultiplier</i> Instance Increment
	 * @param boundMultivariateFunctionArray Array of Bounded Multivariate Stubs
	 * 
	 * @return The Subtracted <i>VariateInequalityConstraintMultiplier</i> Instance
	 */

	public static final VariateInequalityConstraintMultiplier Subtract (
		final VariateInequalityConstraintMultiplier baseVariateInequalityConstriantMultiplier,
		final VariateInequalityConstraintMultiplier incrementVariateInequalityConstriantMultiplier,
		final BoundMultivariate[] boundMultivariateFunctionArray)
	{
		return Subtract (
			baseVariateInequalityConstriantMultiplier,
			incrementVariateInequalityConstriantMultiplier,
			1.,
			boundMultivariateFunctionArray
		);
	}

	/**
	 * Compare the Specified <i>VariateInequalityConstraintMultiplier</i> Instances
	 * 
	 * @param variateInequalityConstraintMultiplier1
	 * 		<i>VariateInequalityConstraintMultiplier</i> Instance #1
	 * @param variateInequalityConstraintMultiplier2
	 * 		<i>VariateInequalityConstraintMultiplier</i> Instance #2
	 * @param relativeTolerance The Relative Tolerance Between the Variates
	 * @param absoluteToleranceFallback The Absolute Tolerance Fall-back Between the Variates
	 * @param comparisonVariateCount The Number of Variates to Compare
	 * 
	 * @return TRUE - The <i>VariateInequalityConstraintMultiplier</i> Instances are Close (Enough)
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public static final boolean Compare (
		final VariateInequalityConstraintMultiplier variateInequalityConstraintMultiplier1,
		final VariateInequalityConstraintMultiplier variateInequalityConstraintMultiplier2,
		final double relativeTolerance,
		final double absoluteToleranceFallback,
		final int comparisonVariateCount)
		throws Exception
	{
		if (null == variateInequalityConstraintMultiplier1 ||
				variateInequalityConstraintMultiplier1.incremental() ||
			null == variateInequalityConstraintMultiplier2 ||
				variateInequalityConstraintMultiplier2.incremental() ||
			!NumberUtil.IsValid (relativeTolerance) ||
			!NumberUtil.IsValid (absoluteToleranceFallback) || 0. > absoluteToleranceFallback)
		{
			throw new Exception ("VariateInequalityConstraintMultiplier::Compare => Invalid Inputs");
		}

		double[] problemVariableArray1 = variateInequalityConstraintMultiplier1.problemVariableArray();

		double[] problemVariableArray2 = variateInequalityConstraintMultiplier2.problemVariableArray();

		if (problemVariableArray1.length != problemVariableArray2.length ||
			comparisonVariateCount > problemVariableArray1.length)
		{
			throw new Exception ("VariateInequalityConstraintMultiplier::Compare => Invalid Inputs");
		}

		for (int comparisonVariateIndex = 0;
			comparisonVariateIndex < comparisonVariateCount;
			++comparisonVariateIndex)
		{
			if (!NumberUtil.IsValid (problemVariableArray1[comparisonVariateIndex]) ||
				!NumberUtil.IsValid (problemVariableArray2[comparisonVariateIndex]))
			{
				throw new Exception ("VariateInequalityConstraintMultiplier::Compare => Invalid Inputs");
			}

			double absoluteTolerance =
				Math.abs (problemVariableArray1[comparisonVariateIndex] * relativeTolerance);

			if (absoluteTolerance < absoluteToleranceFallback) {
				absoluteTolerance = absoluteToleranceFallback;
			}

			if (absoluteTolerance < Math.abs (
				problemVariableArray1[comparisonVariateIndex] - problemVariableArray2[comparisonVariateIndex]
			))
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * <i>VariateInequalityConstraintMultiplier</i> Constructor
	 * 
	 * @param incremental TRUE - Tuple represents an Incremental Unit
	 * @param problemVariableArray Array of Problem Variables
	 * @param kktCoefficientArray Array of KKT Coefficients
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public VariateInequalityConstraintMultiplier (
		final boolean incremental,
		final double[] problemVariableArray,
		final double[] kktCoefficientArray)
		throws Exception
	{
		if (null == (_problemVariableArray = problemVariableArray) || 0 == _problemVariableArray.length) {
			throw new Exception ("VariateInequalityConstraintMultiplier Constructor => Invalid Inputs");
		}

		_incremental = incremental;
		_kktCoefficientArray = kktCoefficientArray;
	}

	/**
	 * Retrieve the Incremental Flag
	 * 
	 * @return TRUE - Tuple is Incremental
	 */

	public boolean incremental()
	{
		return _incremental;
	}

	/**
	 * Retrieve the Array of Problem Variables
	 * 
	 * @return Array of Problem Variables
	 */

	public double[] problemVariableArray()
	{
		return _problemVariableArray;
	}

	/**
	 * Retrieve the Constraint Multipliers
	 * 
	 * @return Array of Constraint Multipliers
	 */

	public double[] kktCoefficientArray()
	{
		return _kktCoefficientArray;
	}

	/**
	 * Retrieve the Consolidated Variate/Constraint Multiplier Array
	 * 
	 * @return The Consolidated Variate/Constraint Multiplier Array
	 */

	public double[] problemVariableKKTCoefficientArray()
	{
		int problemVariableKKTCoefficientCount = _problemVariableArray.length +
			(null == _kktCoefficientArray ? 0 : _kktCoefficientArray.length);
		double[] problemVariableKKTCoefficientArray = new double[problemVariableKKTCoefficientCount];

		for (int problemVariableKKTCoefficientIndex = 0;
			problemVariableKKTCoefficientIndex < problemVariableKKTCoefficientCount;
			++problemVariableKKTCoefficientIndex)
		{
			problemVariableKKTCoefficientArray[problemVariableKKTCoefficientIndex] =
				problemVariableKKTCoefficientIndex < _problemVariableArray.length ?
					_problemVariableArray[problemVariableKKTCoefficientIndex] :
					_kktCoefficientArray[problemVariableKKTCoefficientIndex - _problemVariableArray.length];
		}

		return problemVariableKKTCoefficientArray;
	}

	/**
	 * Retrieve the Sized Vector Instance corresponding to the Increment
	 * 
	 * @return The Sized Vector Instance corresponding to the Increment
	 */

	public SizedVector problemVariableKKTCoefficientIncrementVector()
	{
		return _incremental ? SizedVector.Standard (problemVariableKKTCoefficientArray()) : null;
	}

	/**
	 * Retrieve the Sized Vector Instance corresponding to the Variate Increment
	 * 
	 * @return The Sized Vector Instance corresponding to the Variate Increment
	 */

	public SizedVector problemVariableIncrementVector()
	{
		return _incremental ? SizedVector.Standard (_problemVariableArray) : null;
	}
}
