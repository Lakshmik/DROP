
package org.drip.function.r1tor1solver;

import org.drip.function.definition.R1ToR1;

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
 * <i>FixedPointFinderBracketing</i> customizes the FixedPointFinder for bracketing based fixed point finder
 * 	functionality. It applies the following customization:
 * 	<br>
 * 	<ul>
 * 		<li>Initializes the fixed point finder by computing the starting brackets</li>
 * 		<li>Iterating the next search variate using one of the specified variate iterator primitives.</li>
 * 	</ul>
 * 	<br>
 * 
 * 	By default, <i>FixedPointFinderBracketing</i> does not do compound iterations of the variate using any
 * 		schemes - that is done by classes that extend it. It exposes the following Functions:
 *
 *  <ul>
 * 		<li><i>FixedPointFinderBracketing</i> Constructor</li>
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

public class FixedPointFinderBracketing
	extends FixedPointFinder
{
	protected int _iteratorPrimitive = -1;
	protected IteratedBracket _iteratedBracket = null;
	private ExecutionInitializer _executionInitializer = null;

	protected final double nextVariate (
		final double currentVariate,
		final double contraVariate,
		final double currentObjectiveFunctionValue,
		final double contraObjectiveFunctionValue,
		final int iteratorPrimitive,
		final FixedPointFinderOutput fixedPointFinderOutput)
		throws Exception
	{
		if (VariateIteratorPrimitive.BISECTION == iteratorPrimitive) {
			return VariateIteratorPrimitive.Bisection (currentVariate, contraVariate);
		}

		if (VariateIteratorPrimitive.FALSE_POSITION == iteratorPrimitive) {
			return VariateIteratorPrimitive.FalsePosition (
				currentVariate,
				contraVariate,
				currentObjectiveFunctionValue,
				contraObjectiveFunctionValue
			);
		}

		double intermediateVariate = VariateIteratorPrimitive.Bisection (currentVariate, contraVariate);

		if (!fixedPointFinderOutput.incrementObjectiveFunctionCalculations()) {
			throw new Exception ("FixedPointFinderBracketing::nextVariate => Cannot increment rfop!");
		}

		if (VariateIteratorPrimitive.QUADRATIC_INTERPOLATION == iteratorPrimitive) {
			return VariateIteratorPrimitive.QuadraticInterpolation (
				currentVariate,
				intermediateVariate,
				contraVariate,
				currentObjectiveFunctionValue,
				_objectiveFunction.evaluate (intermediateVariate),
				contraObjectiveFunctionValue
			);
		}

		if (VariateIteratorPrimitive.INVERSE_QUADRATIC_INTERPOLATION == iteratorPrimitive) {
			return VariateIteratorPrimitive.InverseQuadraticInterpolation (
				currentVariate,
				intermediateVariate,
				contraVariate,
				currentObjectiveFunctionValue,
				_objectiveFunction.evaluate (intermediateVariate),
				contraObjectiveFunctionValue
			);
		}

		if (VariateIteratorPrimitive.RIDDER == iteratorPrimitive) {
			return VariateIteratorPrimitive.Ridder (
				currentVariate,
				intermediateVariate,
				contraVariate,
				currentObjectiveFunctionValue,
				_objectiveFunction.evaluate (intermediateVariate),
				contraObjectiveFunctionValue
			);
		}

		throw new Exception ("FixedPointFinderBracketing.calcNextVariate => Unknown Iterator Primitive");
	}

	protected double iterateCompoundVariate (
		final double currentVariate,
		final double contraVariate,
		final double currentObjectiveFunctionValue,
		final double contraObjectiveFunctionValue,
		final FixedPointFinderOutput fixedPointFinderOutput)
		throws Exception
	{
		return nextVariate (
			currentVariate,
			contraVariate,
			currentObjectiveFunctionValue,
			contraObjectiveFunctionValue,
			_iteratorPrimitive,
			fixedPointFinderOutput
		);
	}

	@Override protected boolean iterateVariate (
		final IteratedVariate iteratedVariate,
		final FixedPointFinderOutput fixedPointFinderOutput)
	{
		if (null == iteratedVariate || null == fixedPointFinderOutput) {
			return false;
		}

		double contraRoot = Double.NaN;
		double contraObjectiveFunctionValue = Double.NaN;

		double variate = iteratedVariate.x();

		double leftVariate = _iteratedBracket.leftVariate();

		double rightVariate = _iteratedBracket.rightVariate();

		double objectiveFunctionValue = iteratedVariate.objectiveFunctionValue();

		double leftObjectiveFunctionValue = _iteratedBracket.leftObjectiveFunctionValue();

		double rightObjectiveFunctionValue = _iteratedBracket.rightObjectiveFunctionValue();

		if (0. < (
			(leftObjectiveFunctionValue - _objectiveFunctionValueGoal) *
				(objectiveFunctionValue - _objectiveFunctionValueGoal)
		))
		{
			if (!_iteratedBracket.setLeftObjectiveFunctionValue (objectiveFunctionValue) ||
				!_iteratedBracket.setVariateLeft (variate))
			{
				return false;
			}

			contraObjectiveFunctionValue = rightObjectiveFunctionValue;
			contraRoot = rightVariate;
		} else if (0. < (
			(rightObjectiveFunctionValue - _objectiveFunctionValueGoal) *
				(objectiveFunctionValue - _objectiveFunctionValueGoal)
		))
		{
			if (!_iteratedBracket.setRightObjectiveFunctionValue (objectiveFunctionValue) ||
				!_iteratedBracket.setVariateRight (variate))
			{
				return false;
			}

			contraObjectiveFunctionValue = leftObjectiveFunctionValue;
			contraRoot = leftVariate;
		}

		try {
			variate = iterateCompoundVariate (
				variate,
				contraRoot,
				objectiveFunctionValue,
				contraObjectiveFunctionValue,
				fixedPointFinderOutput
			);

			return iteratedVariate.setX (variate) &&
				iteratedVariate.setObjectiveFunctionValue (_objectiveFunction.evaluate (variate)) &&
				fixedPointFinderOutput.incrementObjectiveFunctionCalculations();
		} catch (Exception e) {
			if (_whine) e.printStackTrace();
		}

		return false;
	}

	@Override protected ExecutionInitializationOutput initializeVariateZone (
		final InitializationHeuristics initializationHeuristics)
	{
		BracketingOutput bracketingOutput = null != initializationHeuristics &&
			InitializationHeuristics.SEARCH_HARD_BRACKETS == initializationHeuristics.determinant() ?
			_executionInitializer.verifyHardSearchEdges (
				initializationHeuristics,
				_objectiveFunctionValueGoal
			) : _executionInitializer.initializeBracket (
				initializationHeuristics,
				_objectiveFunctionValueGoal
			);

		if (null == bracketingOutput || !bracketingOutput.isDone()) {
			return null;
		}

		try {
			_iteratedBracket = new IteratedBracket (bracketingOutput);

			return bracketingOutput;
		} catch (Exception e) {
			if (_whine) e.printStackTrace();
		}

		return null;
	}

	/**
	 * <i>FixedPointFinderBracketing</i> Constructor
	 * 
	 * @param objectiveFunctionValueGoal Objective Function Value Goal
	 * @param objectiveFunction Objective Function
	 * @param executionControl Execution Control
	 * @param iteratorPrimitive Iterator Primitive
	 * @param whine TRUE - Balk on Encountering Exception
	 * 
	 * @throws Exception Thrown if inputs are invalid
	 */

	public FixedPointFinderBracketing (
		final double objectiveFunctionValueGoal,
		final R1ToR1 objectiveFunction,
		final ExecutionControl executionControl,
		final int iteratorPrimitive,
		final boolean whine)
		throws Exception
	{
		super (objectiveFunctionValueGoal, objectiveFunction, executionControl, whine);

		if (VariateIteratorPrimitive.BISECTION != (_iteratorPrimitive = iteratorPrimitive) &&
			VariateIteratorPrimitive.FALSE_POSITION != _iteratorPrimitive &&
			VariateIteratorPrimitive.QUADRATIC_INTERPOLATION != _iteratorPrimitive &&
			VariateIteratorPrimitive.INVERSE_QUADRATIC_INTERPOLATION != _iteratorPrimitive &&
			VariateIteratorPrimitive.RIDDER != _iteratorPrimitive)
		{
			throw new Exception ("FixedPointFinderBracketing constructor: Invalid inputs!");
		}

		_executionInitializer = new ExecutionInitializer (_objectiveFunction, null, true);
	}
}
