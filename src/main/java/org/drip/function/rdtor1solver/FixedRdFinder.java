
package org.drip.function.rdtor1solver;

import org.drip.function.definition.RdToR1;
import org.drip.function.definition.UnitVector;
import org.drip.function.rdtor1.LagrangianMultivariate;
import org.drip.function.rdtor1descent.LineEvolutionVerifier;
import org.drip.function.rdtor1descent.LineEvolutionVerifierMetrics;
import org.drip.function.rdtor1descent.LineStepEvolutionControl;

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
 * <i>FixedRdFinder</i> exports the Methods needed for the locating a Fixed R<sup>d</sup> Point. It exposes
 * 	the following Functions:
 *
 *  <ul>
 * 		<li>Flag Indicating whether the Verifier Increment Metrics are to be Traced</li>
 * 		<li>Retrieve the Objective Function</li>
 * 		<li>Retrieve the Line Step Evolution Control</li>
 * 		<li>Retrieve the Convergence Control Parameters</li>
 * 		<li>Solve for the Optimal Variate-Inequality Constraint Multiplier Tuple Using the Variate/Inequality Constraint Tuple Convergence</li>
 * 		<li>Solve for the Optimal Variate-Inequality Constraint Multiplier Tuple Using the Objective Function Convergence</li>
 * 		<li>Find the Optimal Variate-Inequality Constraint Multiplier Tuple using the Iteration Parameters provided by the Convergence Control Instance</li>
 * 		<li>Retrieve the Incremental Step Length Fraction</li>
 * 		<li>Produce the Incremental Variate-Constraint Multiplier</li>
 * 		<li>Iterate Over to the Next Variate-Constraint Multiplier Tuple</li>
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

public abstract class FixedRdFinder
{

	/**
	 * Flag Indicating whether the Verifier Increment Metrics are to be Traced
	 */

	public static boolean s_VerifierIncrementBlog = false;

	private RdToR1 _objectiveFunction = null;
	private ConvergenceControl _convergenceControl = null;
	private LineStepEvolutionControl _lineStepEvolutionControl = null;

	protected FixedRdFinder (
		final RdToR1 objectiveFunction,
		final LineStepEvolutionControl lineStepEvolutionControl,
		final ConvergenceControl convergenceControl)
		throws Exception
	{
		if (null == (_objectiveFunction = objectiveFunction) ||
			null == (_convergenceControl = convergenceControl))
		{
			throw new Exception ("FixedRdFinder Constructor => Invalid Inputs");
		}

		_lineStepEvolutionControl = lineStepEvolutionControl;
	}

	/**
	 * Retrieve the Objective Function
	 * 
	 * @return The Objective Function
	 */

	public RdToR1 objectiveFunction()
	{
		return _objectiveFunction;
	}

	/**
	 * Retrieve the Line Step Evolution Control
	 * 
	 * @return The Line Step Evolution Control
	 */

	public LineStepEvolutionControl lineStepEvolutionControl()
	{
		return _lineStepEvolutionControl;
	}

	/**
	 * Retrieve the Convergence Control Parameters
	 * 
	 * @return The Convergence Control Parameters
	 */

	public ConvergenceControl convergenceControl()
	{
		return _convergenceControl;
	}

	/**
	 * Solve for the Optimal Variate-Inequality Constraint Multiplier Tuple Using the Variate/Inequality
	 *  Constraint Tuple Convergence
	 *  
	 * @param startingVariateInequalityConstraintMultiplier The Starting Variate/Inequality Constraint Tuple
	 * 
	 * @return The Optimal Variate-Inequality Constraint Multiplier Tuple
	 */

	public VariateInequalityConstraintMultiplier convergeVariate (
		final VariateInequalityConstraintMultiplier startingVariateInequalityConstraintMultiplier)
	{
		if (null == startingVariateInequalityConstraintMultiplier) {
			return null;
		}

		RdToR1 objectiveFunction = objectiveFunction();

		boolean fixedPointFound = false;
		VariateInequalityConstraintMultiplier currentVariateInequalityConstraintMultiplier =
			startingVariateInequalityConstraintMultiplier;
		VariateInequalityConstraintMultiplier previousVariateInequalityConstraintMultiplier =
			startingVariateInequalityConstraintMultiplier;

		int comparisonVariateCount = objectiveFunction instanceof LagrangianMultivariate ?
			((LagrangianMultivariate) objectiveFunction).problemVariableCount() :
			objectiveFunction.dimension();

		double absoluteToleranceFallback = _convergenceControl.absoluteTolerance();

		double relativeTolerance = _convergenceControl.relativeTolerance();

		while (!fixedPointFound) {
			VariateInequalityConstraintMultiplier variateInequalityConstraintMultiplier =
				increment (currentVariateInequalityConstraintMultiplier);

			if (null == variateInequalityConstraintMultiplier ||
				null == (
					currentVariateInequalityConstraintMultiplier = next (
						previousVariateInequalityConstraintMultiplier,
						variateInequalityConstraintMultiplier,
						incrementFraction (
							currentVariateInequalityConstraintMultiplier,
							variateInequalityConstraintMultiplier
						)
					)
				)
			)
			{
				return null;
			}

			try {
				fixedPointFound = VariateInequalityConstraintMultiplier.Compare (
					currentVariateInequalityConstraintMultiplier,
					previousVariateInequalityConstraintMultiplier,
					relativeTolerance,
					absoluteToleranceFallback,
					comparisonVariateCount
				);
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}

			previousVariateInequalityConstraintMultiplier = currentVariateInequalityConstraintMultiplier;
		}

		return currentVariateInequalityConstraintMultiplier;
	}

	/**
	 * Solve for the Optimal Variate-Inequality Constraint Multiplier Tuple Using the Objective Function
	 *  Convergence
	 *  
	 * @param startingVariateInequalityConstraintMultiplier Starting Variate/Inequality Constraint Tuple Set
	 * 
	 * @return The Optimal Variate-Inequality Constraint Multiplier Tuple
	 */

	public VariateInequalityConstraintMultiplier convergeObjectiveFunction (
		final VariateInequalityConstraintMultiplier startingVariateInequalityConstraintMultiplier)
	{
		if (null == startingVariateInequalityConstraintMultiplier) {
			return null;
		}

		boolean fixedPointFound = false;
		double previousObjectiveFunctionValue = Double.NaN;
		VariateInequalityConstraintMultiplier variateInequalityConstraintMultiplier =
			startingVariateInequalityConstraintMultiplier;

		try {
			previousObjectiveFunctionValue =
				_objectiveFunction.evaluate (variateInequalityConstraintMultiplier.problemVariableArray());
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}

		double convergenceControlAbsoluteTolerance = _convergenceControl.absoluteTolerance();

		double objectiveFunctionAbsoluteTolerance =
			Math.abs (previousObjectiveFunctionValue * _convergenceControl.relativeTolerance());

		double absoluteTolerance = convergenceControlAbsoluteTolerance <
			objectiveFunctionAbsoluteTolerance ?
			convergenceControlAbsoluteTolerance : objectiveFunctionAbsoluteTolerance;

		while (!fixedPointFound) {
			VariateInequalityConstraintMultiplier incrementVariateInequalityConstraintMultiplier =
				increment (variateInequalityConstraintMultiplier);

			if (null == incrementVariateInequalityConstraintMultiplier ||
				null == (
					variateInequalityConstraintMultiplier = next (
						variateInequalityConstraintMultiplier,
						incrementVariateInequalityConstraintMultiplier,
						incrementFraction (
							variateInequalityConstraintMultiplier,
							incrementVariateInequalityConstraintMultiplier
						)
					)
				)
			)
			{
				return null;
			}

			try {
				double objectiveFunctionValue = _objectiveFunction.evaluate (
					variateInequalityConstraintMultiplier.problemVariableArray()
				);

				if (Math.abs (previousObjectiveFunctionValue - objectiveFunctionValue) < absoluteTolerance) {
					fixedPointFound = true;
				}

				previousObjectiveFunctionValue = objectiveFunctionValue;
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		return variateInequalityConstraintMultiplier;
	}

	/**
	 * Find the Optimal Variate-Inequality Constraint Multiplier Tuple using the Iteration Parameters
	 *  provided by the Convergence Control Instance
	 *  
	 * @param startingVariateInequalityConstraintMultiplier
	 * 		Starting Variate-Inequality Constraint Multiplier Tuple
	 * 
	 * @return The Optimal Variate-Inequality Constraint Multiplier Tuple
	 */

	public VariateInequalityConstraintMultiplier find (
		final VariateInequalityConstraintMultiplier startingVariateInequalityConstraintMultiplier)
	{
		int convergenceType = _convergenceControl.convergenceType();

		if (InteriorPointBarrierControl.OBJECTIVE_FUNCTION_SEQUENCE_CONVERGENCE == convergenceType) {
			return convergeObjectiveFunction (startingVariateInequalityConstraintMultiplier);
		}

		if (InteriorPointBarrierControl.VARIATE_CONSTRAINT_SEQUENCE_CONVERGENCE == convergenceType) {
			return convergeVariate (startingVariateInequalityConstraintMultiplier);
		}

		return null;
	}

	/**
	 * Retrieve the Incremental Step Length Fraction
	 * 
	 * @param baseVariateInequalityConstraintMultiplier
	 * 		<i>VariateInequalityConstraintMultiplier</i> Base Instance
	 * @param incrementVariateInequalityConstraintMultiplier
	 * 		<i>VariateInequalityConstraintMultiplier</i> Instance Increment
	 * 
	 * @return The VariateInequalityConstraintMultiplier Incremental Step Length Fraction
	 */

	public double incrementFraction (
		final VariateInequalityConstraintMultiplier baseVariateInequalityConstraintMultiplier,
		final VariateInequalityConstraintMultiplier incrementVariateInequalityConstraintMultiplier)
	{
		if (null == _lineStepEvolutionControl ||
			null == baseVariateInequalityConstraintMultiplier ||
				baseVariateInequalityConstraintMultiplier.incremental() ||
			null == incrementVariateInequalityConstraintMultiplier ||
				!incrementVariateInequalityConstraintMultiplier.incremental())
		{
			return 1.;
		}

		UnitVector variateIncrementDirectionVector =
			incrementVariateInequalityConstraintMultiplier.problemVariableIncrementVector().direction();

		double[] problemVariableArray = baseVariateInequalityConstraintMultiplier.problemVariableArray();

		LineEvolutionVerifier lineEvolutionVerifier = _lineStepEvolutionControl.lineEvolutionVerifier();

		int reductionStepCount = _lineStepEvolutionControl.reductionStepCount();

		double reductionFactor = _lineStepEvolutionControl.reductionFactor();

		double factorStepLength = 1.;

		while (0 <= --reductionStepCount) {
			LineEvolutionVerifierMetrics lineEvolutionVerifierMetrics = lineEvolutionVerifier.metrics (
				variateIncrementDirectionVector,
				problemVariableArray,
				_objectiveFunction,
				factorStepLength
			);

			if (null == lineEvolutionVerifierMetrics) {
				return 1.;
			}

			if (s_VerifierIncrementBlog) {
				System.out.println (lineEvolutionVerifierMetrics);
			}

			if (lineEvolutionVerifierMetrics.verify()) {
				return factorStepLength;
			}

			factorStepLength *= reductionFactor;
		}

		return 1.;
	}

	/**
	 * Produce the Incremental Variate-Constraint Multiplier
	 * 
	 * @param currentVariateInequalityConstraintMultiplier The Current Variate-Constraint Multiplier Tuple
	 * 
	 * @return The Incremental Variate-Constraint Multiplier
	 */

	public abstract VariateInequalityConstraintMultiplier increment (
		final VariateInequalityConstraintMultiplier currentVariateInequalityConstraintMultiplier
	);

	/**
	 * Iterate Over to the Next Variate-Constraint Multiplier Tuple
	 * 
	 * @param currentVariateInequalityConstraintMultiplier Current Variate-Constraint Multiplier Tuple
	 * @param incrementVariateInequalityConstraintMultiplier Incremental Variate-Constraint Multiplier Tuple
	 * @param incrementFraction The Incremental Fraction to be applied
	 * 
	 * @return The Next Variate-Constraint Multiplier Set
	 */

	public abstract VariateInequalityConstraintMultiplier next (
		final VariateInequalityConstraintMultiplier currentVariateInequalityConstraintMultiplier,
		final VariateInequalityConstraintMultiplier incrementVariateInequalityConstraintMultiplier,
		final double incrementFraction
	);
}
