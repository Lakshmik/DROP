
package org.drip.function.rdtor1solver;

import org.drip.function.definition.RdToR1;
import org.drip.function.rdtor1.BoundMultivariate;
import org.drip.numerical.common.NumberUtil;
import org.drip.numerical.linearalgebra.LinearizationOutput;
import org.drip.numerical.linearsolver.LinearSystem;

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
 * <i>InteriorFixedPointFinder</i> generates the Iterators for solving R<sup>d</sup> To R<sup>1</sup>
 * 	Convex/Non-Convex Functions Under Inequality Constraints loaded using a Barrier Coefficient. It exposes
 * 	the following Functions:
 *
 *  <ul>
 * 		<li><i>InteriorFixedPointFinder</i> Constructor
 * 		<li>Retrieve the Array of Inequality Constraint Function
 * 		<li>Retrieve the Barrier Strength
 * 		<li>Produce the Incremental Variate-Constraint Multiplier
 * 		<li>Iterate Over to the Next Variate-Constraint Multiplier Tuple
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

public class InteriorFixedPointFinder
	extends FixedRdFinder
{
	private double _barrierStrength = Double.NaN;
	private BoundMultivariate[] _boundMultivariateFunctionArray = null;
	private RdToR1[] _inequalityConstraintMultivariateFunctionArray = null;

	private VariateInequalityConstraintMultiplier incremental (
		final ObjectiveFunctionPointMetrics objectiveFunctionPointMetrics,
		final ConstraintFunctionPointMetrics inequalityConstraintFunctionPointMetrics)
	{
		if (null == objectiveFunctionPointMetrics || null == inequalityConstraintFunctionPointMetrics) {
			return null;
		}

		int objectiveFunctionDimension = objectiveFunctionPointMetrics.dimension();

		double[] objectiveFunctionJacobian = objectiveFunctionPointMetrics.jacobian();

		double[][] objectiveFunctionHessian = objectiveFunctionPointMetrics.hessian();

		int inequalityConstraintCount = inequalityConstraintFunctionPointMetrics.count();

		double[] problemVariableIncrementArray = new double[objectiveFunctionDimension];
		double[] inequalityConstraintIncrementArray = new double[inequalityConstraintCount];
		int problemVariableKKTCoefficientDimension = objectiveFunctionDimension + inequalityConstraintCount;
		double[][] problemVariableKKTCoefficientJacobianArray =
			new double[problemVariableKKTCoefficientDimension][problemVariableKKTCoefficientDimension];
		double[] problemVariableKKTCoefficientRHSArray = new double[problemVariableKKTCoefficientDimension];

		if (0 == objectiveFunctionDimension ||
			objectiveFunctionDimension != inequalityConstraintFunctionPointMetrics.dimension())
		{
			return null;
		}

		double[] inequalityConstraintFunctionMultiplierArray =
			inequalityConstraintFunctionPointMetrics.constraintFunctionMultiplierArray();

		double[][] inequalityConstraintFunctionJacobianArray =
			inequalityConstraintFunctionPointMetrics.constraintFunctionJacobianArray();

		double[] inequalityConstraintFunctionValueArray =
			inequalityConstraintFunctionPointMetrics.constraintFunctionValueArray();

		for (int objectiveFunctionDimensionIndexI = 0;
			objectiveFunctionDimensionIndexI < objectiveFunctionDimension;
			++objectiveFunctionDimensionIndexI)
		{
			for (int objectiveFunctionDimensionIndexJ = 0;
				objectiveFunctionDimensionIndexJ < objectiveFunctionDimension;
				++objectiveFunctionDimensionIndexJ)
			{
				problemVariableKKTCoefficientJacobianArray[objectiveFunctionDimensionIndexI][objectiveFunctionDimensionIndexJ]
					= objectiveFunctionHessian[objectiveFunctionDimensionIndexI][objectiveFunctionDimensionIndexJ];
			}

			for (int inequalityConstraintIndex = 0;
				inequalityConstraintIndex < inequalityConstraintCount;
				++inequalityConstraintIndex)
			{
				problemVariableKKTCoefficientJacobianArray[objectiveFunctionDimensionIndexI][inequalityConstraintIndex + objectiveFunctionDimension]
					= -1. *
						inequalityConstraintFunctionJacobianArray[objectiveFunctionDimensionIndexI][inequalityConstraintIndex];
			}
		}

		for (int inequalityConstraintIndexI = 0;
			inequalityConstraintIndexI < inequalityConstraintCount;
			++inequalityConstraintIndexI)
		{
			for (int inequalityConstraintIndexJ = 0;
				inequalityConstraintIndexJ < inequalityConstraintCount;
				++inequalityConstraintIndexJ)
			{
				problemVariableKKTCoefficientJacobianArray[inequalityConstraintIndexI + objectiveFunctionDimension][inequalityConstraintIndexJ + objectiveFunctionDimension]
					= inequalityConstraintIndexI == inequalityConstraintIndexJ ?
						inequalityConstraintFunctionValueArray[inequalityConstraintIndexI] : 0.;
			}

			for (int objectiveFunctionIndex = 0;
				objectiveFunctionIndex < objectiveFunctionDimension;
				++objectiveFunctionIndex)
			{
				problemVariableKKTCoefficientJacobianArray[inequalityConstraintIndexI + objectiveFunctionDimension][objectiveFunctionIndex] =
					inequalityConstraintFunctionMultiplierArray[inequalityConstraintIndexI] *
					inequalityConstraintFunctionJacobianArray[objectiveFunctionIndex][inequalityConstraintIndexI];
			}
		}

		for (int constrainedObjectiveFunctionIndex = 0;
			constrainedObjectiveFunctionIndex < problemVariableKKTCoefficientDimension;
			++constrainedObjectiveFunctionIndex)
		{
			if (constrainedObjectiveFunctionIndex < objectiveFunctionDimension)
			{
				problemVariableKKTCoefficientRHSArray[constrainedObjectiveFunctionIndex] =
					-1. * objectiveFunctionJacobian[constrainedObjectiveFunctionIndex];

				for (int inequalityConstraintIndex = 0;
					inequalityConstraintIndex < inequalityConstraintCount;
					++inequalityConstraintIndex)
				{
					problemVariableKKTCoefficientRHSArray[constrainedObjectiveFunctionIndex] +=
						inequalityConstraintFunctionJacobianArray[constrainedObjectiveFunctionIndex][inequalityConstraintIndex]
						* inequalityConstraintFunctionMultiplierArray[inequalityConstraintIndex];
				}
			} else {
				int constraintIndex = constrainedObjectiveFunctionIndex - objectiveFunctionDimension;
				problemVariableKKTCoefficientRHSArray[constrainedObjectiveFunctionIndex] =
					_barrierStrength - inequalityConstraintFunctionValueArray[constraintIndex] *
					inequalityConstraintFunctionMultiplierArray[constraintIndex];
			}
		}

		LinearizationOutput linearizationOutput = LinearSystem.SolveUsingMatrixInversion (
			problemVariableKKTCoefficientJacobianArray,
			problemVariableKKTCoefficientRHSArray
		);

		if (null == linearizationOutput) {
			return null;
		}

		double[] problemVariableKKTCoefficientIncrementArray = linearizationOutput.getTransformedRHS();

		if (null == problemVariableKKTCoefficientIncrementArray ||
			problemVariableKKTCoefficientIncrementArray.length != problemVariableKKTCoefficientDimension)
		{
			return null;
		}

		for (int problemVariableKKTCoefficientIndex = 0;
			problemVariableKKTCoefficientIndex < problemVariableKKTCoefficientDimension;
			++problemVariableKKTCoefficientIndex)
		{
			if (problemVariableKKTCoefficientIndex < objectiveFunctionDimension) {
				problemVariableIncrementArray[problemVariableKKTCoefficientIndex] =
					problemVariableKKTCoefficientIncrementArray[problemVariableKKTCoefficientIndex];
			} else {
				inequalityConstraintIncrementArray[problemVariableKKTCoefficientIndex - objectiveFunctionDimension]
					= problemVariableKKTCoefficientIncrementArray[problemVariableKKTCoefficientIndex];
			}
		}

		try {
			return new VariateInequalityConstraintMultiplier (
				true,
				problemVariableIncrementArray,
				inequalityConstraintIncrementArray
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * <i>InteriorFixedPointFinder</i> Constructor
	 * 
	 * @param rdToR1ObjectiveFunction The Objective Function
	 * @param inequalityConstraintMultivariateFunctionArray Array of Inequality Constraints
	 * @param lineStepEvolutionControl The Line Step Evolution Control
	 * @param convergenceControl Convergence Control Parameters
	 * @param barrierStrength Barrier Strength
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public InteriorFixedPointFinder (
		final RdToR1 rdToR1ObjectiveFunction,
		final RdToR1[] inequalityConstraintMultivariateFunctionArray,
		final org.drip.function.rdtor1descent.LineStepEvolutionControl lineStepEvolutionControl,
		final ConvergenceControl convergenceControl,
		final double barrierStrength)
		throws Exception
	{
		super (rdToR1ObjectiveFunction, lineStepEvolutionControl, convergenceControl);

		if (null == (
			_inequalityConstraintMultivariateFunctionArray = inequalityConstraintMultivariateFunctionArray
			) || !NumberUtil.IsValid (_barrierStrength = barrierStrength)
		)
		{
			throw new Exception ("InteriorFixedPointFinder Constructor => Invalid Inputs");
		}

		_boundMultivariateFunctionArray =
			0 == _inequalityConstraintMultivariateFunctionArray.length ?
			null : new BoundMultivariate[_inequalityConstraintMultivariateFunctionArray.length];

		if (0 == _inequalityConstraintMultivariateFunctionArray.length) {
			throw new Exception ("InteriorFixedPointFinder Constructor => Invalid Inputs");
		}

		for (int inequalityConstraintIndex = 0;
			inequalityConstraintIndex < _inequalityConstraintMultivariateFunctionArray.length;
			++inequalityConstraintIndex)
		{
			if (null == _inequalityConstraintMultivariateFunctionArray[inequalityConstraintIndex]) {
				throw new Exception ("InteriorFixedPointFinder Constructor => Invalid Inputs");
			}

			if (_inequalityConstraintMultivariateFunctionArray[inequalityConstraintIndex] instanceof
				BoundMultivariate)
			{
				_boundMultivariateFunctionArray[inequalityConstraintIndex] = (BoundMultivariate)
					_inequalityConstraintMultivariateFunctionArray[inequalityConstraintIndex];
			}
		}
	}

	/**
	 * Retrieve the Array of Inequality Constraint Function
	 * 
	 * @return The Array of Inequality Constraint Function
	 */

	public RdToR1[] inequalityConstraintMultivariateFunctionArray()
	{
		return _inequalityConstraintMultivariateFunctionArray;
	}

	/**
	 * Retrieve the Barrier Strength
	 * 
	 * @return The Barrier Strength
	 */

	public double barrierStrength()
	{
		return _barrierStrength;
	}

	/**
	 * Produce the Incremental Variate-Constraint Multiplier
	 * 
	 * @param currentVariateInequalityConstraintMultiplier The Current Variate-Constraint Multiplier Tuple
	 * 
	 * @return The Incremental Variate-Constraint Multiplier
	 */

	@Override public VariateInequalityConstraintMultiplier increment (
		final VariateInequalityConstraintMultiplier currentVariateInequalityConstraintMultiplier)
	{
		if (null == currentVariateInequalityConstraintMultiplier ||
			0 == _inequalityConstraintMultivariateFunctionArray.length)
		{
			return null;
		}

		double[] problemVariableArray = currentVariateInequalityConstraintMultiplier.problemVariableArray();

		double[] constraintValueArray = new double[_inequalityConstraintMultivariateFunctionArray.length];
		double[][] constraintJacobianArray =
			new double[problemVariableArray.length][_inequalityConstraintMultivariateFunctionArray.length];

		for (int constraintIndex = 0;
			constraintIndex < _inequalityConstraintMultivariateFunctionArray.length;
			++constraintIndex)
		{
			try {
				constraintValueArray[constraintIndex] =
					_inequalityConstraintMultivariateFunctionArray[constraintIndex].evaluate (
						problemVariableArray
					);
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}

			double[] constraintJacobian =
				_inequalityConstraintMultivariateFunctionArray[constraintIndex].jacobian (
					problemVariableArray
				);

			if (null == constraintJacobian) {
				return null;
			}

			for (int problemVariableIndex = 0;
				problemVariableIndex < problemVariableArray.length;
				++problemVariableIndex)
			{
				constraintJacobianArray[problemVariableIndex][constraintIndex] =
					constraintJacobian[problemVariableIndex];
			}
		}

		RdToR1 objectiveFunction = objectiveFunction();

		try {
			return incremental (
				new ObjectiveFunctionPointMetrics (
					objectiveFunction.jacobian (problemVariableArray),
					objectiveFunction.hessian (problemVariableArray)
				),
				new ConstraintFunctionPointMetrics (
					constraintValueArray,
					constraintJacobianArray,
					currentVariateInequalityConstraintMultiplier.kktCoefficientArray()
				)
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Iterate Over to the Next Variate-Constraint Multiplier Tuple
	 * 
	 * @param currentVariateInequalityConstraintMultiplier Current Variate-Constraint Multiplier Tuple
	 * @param incrementVariateInequalityConstraintMultiplier Incremental Variate-Constraint Multiplier Tuple
	 * @param incrementFraction The Incremental Fraction to be applied
	 * 
	 * @return The Next Variate-Constraint Multiplier Set
	 */

	@Override public VariateInequalityConstraintMultiplier next (
		final VariateInequalityConstraintMultiplier currentVariateInequalityConstraintMultiplier,
		final VariateInequalityConstraintMultiplier incrementVariateInequalityConstraintMultiplier,
		final double incrementFraction)
	{
		return VariateInequalityConstraintMultiplier.Add (
			currentVariateInequalityConstraintMultiplier,
			incrementVariateInequalityConstraintMultiplier,
			incrementFraction,
			_boundMultivariateFunctionArray
		);
	}
}
