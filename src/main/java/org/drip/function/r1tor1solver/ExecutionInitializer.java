
package org.drip.function.r1tor1solver;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.drip.function.definition.R1ToR1;
import org.drip.numerical.common.NumberUtil;
import org.drip.numerical.differentiation.Differential;

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
 * <i>ExecutionInitializer</i> implements the initialization execution and customization functionality.
 * 	<i>ExecutionInitializer</i> performs two types of variate initialization:
 * <br>
 * 	<ul>
 * 		<li>Bracketing initialization: This brackets the fixed point using the bracketing algorithm described
 * 			in https://lakshmidrip.github.io/DROP-Numerical-Core/. If successful, a pair of variate/OF
 * 			coordinate nodes that bracket the fixed point are generated. These brackets are eventually used
 * 			by routines that iteratively determine the fixed point. Bracketing initialization is controlled
 * 			by the parameters in BracketingControlParams.</li>
 * 		<li>Convergence Zone initialization: This generates a variate that lies within the convergence zone
 * 			for the iterative determination of the fixed point using the Newton's method. Convergence Zone
 * 			Determination is controlled by the parameters in ConvergenceControlParams.</li>
 * 	</ul>
 *
 * 	<i>ExecutionInitializer</i> behavior can be customized/optimized through several of the initialization
 * 		heuristics techniques implemented in the InitializationHeuristics class. It exposes the following
 * 		Functions:
 *
 *  <ul>
 * 		<li><i>ExecutionInitializer</i> Constructor</li>
 * 		<li>Set up the bracket to be used for the eventual search kick-off</li>
 * 		<li>Initialize the starting variate to within the fixed point convergence zone</li>
 * 		<li>Initialize the starting bracket within the specified boundary</li>
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

public class ExecutionInitializer
{

	class StartingVariateObjectiveFunction
	{
		public double _variate = Double.NaN;
		public double _response = Double.NaN;

		public StartingVariateObjectiveFunction (
			final double variate,
			final double response)
			throws Exception
		{
			if (!NumberUtil.IsValid (_response = response) || !NumberUtil.IsValid (_variate = variate)) {
				throw new Exception ("StartingVariateObjectiveFunction Constructor: Invalid inputs!");
			}
		}
	}

	private R1ToR1 _objectiveFunction = null;
	private boolean _trendBracketRight = false;
	private ConvergenceControlParams _convergenceControlParams = null;

	private SortedMap<Double, Double> _variateObjectiveFunctionValueMap = new TreeMap<Double, Double>();

	private double evaluateObjectiveFunction (
		final double variate)
		throws Exception
	{
		if (_variateObjectiveFunctionValueMap.containsKey (variate)) {
			return _variateObjectiveFunctionValueMap.get (variate);
		}

		double objectiveFunctionValue = _objectiveFunction.evaluate (variate);

		if (NumberUtil.IsValid (objectiveFunctionValue)) {
			_variateObjectiveFunctionValueMap.put (variate, objectiveFunctionValue);
		}

		return objectiveFunctionValue;
	}

	private StartingVariateObjectiveFunction validateVariate (
		final double variate,
		final BracketingOutput bracketingOutput)
	{
		double objectiveFunctionValue = Double.NaN;

		try {
			objectiveFunctionValue = evaluateObjectiveFunction (variate);
		} catch (Exception e) {
			objectiveFunctionValue = Double.NaN;
		}

		if (!bracketingOutput.incrementObjectiveFunctionCalculationCount() ||
			!NumberUtil.IsValid (objectiveFunctionValue))
		{
			return null;
		}

		_variateObjectiveFunctionValueMap.put (variate, objectiveFunctionValue);

		try {
			return new StartingVariateObjectiveFunction (variate, objectiveFunctionValue);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private StartingVariateObjectiveFunction initializeBracketingVariate (
		final int expansionCount,
		final double bracketVariateStart,
		final double bracketWidthStart,
		final double bracketWidthExpansionFactor,
		final BracketingOutput bracketingOutput)
	{
		StartingVariateObjectiveFunction startingVariateObjectiveFunction =
			validateVariate (bracketVariateStart, bracketingOutput);

		if (null != startingVariateObjectiveFunction) {
			return startingVariateObjectiveFunction;
		}

		double variate = bracketVariateStart;
		double bracketWidth = bracketWidthStart;
		int currentExpansionCount = expansionCount;
		double bracketLeft = variate - bracketWidth;
		double bracketRight = variate + bracketWidth;

		while (0 <= currentExpansionCount--) {
			if (_trendBracketRight) {
				if (null != (
					startingVariateObjectiveFunction = validateVariate (bracketRight, bracketingOutput)
				))
				{
					return startingVariateObjectiveFunction;
				}

				if (null != (
					startingVariateObjectiveFunction = validateVariate (bracketLeft, bracketingOutput)
				))
				{
					return startingVariateObjectiveFunction;
				}
			} else {
				if (null != (
					startingVariateObjectiveFunction = validateVariate (bracketLeft, bracketingOutput)
				))
				{
					return startingVariateObjectiveFunction;
				}

				if (null != (
					startingVariateObjectiveFunction = validateVariate (bracketRight, bracketingOutput)
				))
				{
					return startingVariateObjectiveFunction;
				}
			}

			bracketWidth *= bracketWidthExpansionFactor;
			bracketRight = variate + bracketWidth;
			bracketLeft = variate - bracketWidth;
		}

		return null;
	}

	private boolean bracketingDone (
		final double variateLeft,
		final double variateRight,
		final double objectiveFunctionValueLeft,
		final double objectiveFunctionValueRight,
		final double objectiveFunctionValueGoal,
		final BracketingOutput bracketingOutput)
	{
		if (!NumberUtil.IsValid (objectiveFunctionValueLeft) ||
			!NumberUtil.IsValid (objectiveFunctionValueRight))
		{
			return false;
		}

		if (0. < (
			(objectiveFunctionValueLeft - objectiveFunctionValueGoal) *
				(objectiveFunctionValueRight - objectiveFunctionValueGoal)
		))
		{
			return false;
		}

		double variate = Double.NaN;
		double previousVariate = Double.NaN;
		double objectiveFunctionValue = Double.NaN;
		double previousObjectiveFunctionValue = Double.NaN;

		for (Map.Entry<Double, Double> mapEntry : _variateObjectiveFunctionValueMap.entrySet()) {
			variate = mapEntry.getKey();

			objectiveFunctionValue = mapEntry.getValue();

			if (NumberUtil.IsValid (previousVariate) &&
				NumberUtil.IsValid (previousObjectiveFunctionValue) && (
					0. > (
						(objectiveFunctionValue - objectiveFunctionValueGoal) *
							(previousObjectiveFunctionValue - objectiveFunctionValueGoal)
					)
				))
			{
				try {
					bracketingOutput.done (
						previousVariate,
						variate,
						previousObjectiveFunctionValue,
						objectiveFunctionValue,
						VariateIteratorPrimitive.Bisection (previousVariate, variate)
					);
				} catch (Exception e) {
				}

				return true;
			}

			previousObjectiveFunctionValue = objectiveFunctionValue;
			previousVariate = variate;
		}

		try {
			bracketingOutput.done (
				variateLeft,
				variateRight,
				objectiveFunctionValueLeft,
				objectiveFunctionValueRight,
				VariateIteratorPrimitive.Bisection (variateLeft, variateRight)
			);
		} catch (Exception e) {
		}

		return true;
	}

	private boolean inConvergenceZone (
		final double variate,
		final double objectiveFunctionValueGoal,
		final ConvergenceOutput output)
		throws Exception
	{
		if (!output.incrementObjectiveFunctionCalculationCount()) {
			throw new Exception (
				"ExecutionInitializer::inConvergenceZone => Cannot increment OF in the output"
			);
		}

		double objectiveFunctionValue = evaluateObjectiveFunction (variate) - objectiveFunctionValueGoal;

		if (!NumberUtil.IsValid (objectiveFunctionValue)) {
			throw new Exception (
				"ExecutionInitializer::inConvergenceZone => Cannot evaluate OF for variate " + variate
			);
		}

		if (!output.incrementObjectiveFunctionDerivativeCalculationCount()) {
			throw new Exception (
				"ExecutionInitializer::inConvergenceZone => Cannot increment OF deriv count in the output"
			);
		}

		Differential firstOrderDifferential = _objectiveFunction.differential (variate, 1);

		if (null == firstOrderDifferential) {
			throw new Exception (
				"ExecutionInitializer::inConvergenceZone => Cannot evaluate OF first deriv for variate " +
					variate
			);
		}

		if (!output.incrementObjectiveFunctionDerivativeCalculationCount() &&
			!output.incrementObjectiveFunctionDerivativeCalculationCount())
		{
			throw new Exception (
				"ExecutionInitializer::inConvergenceZone => Cannot increment OF deriv in the output"
			);
		}

		Differential secondOrderDifferential = _objectiveFunction.differential (variate, 2);

		if (null == secondOrderDifferential) {
			throw new Exception (
				"ExecutionInitializer::inConvergenceZone => Cannot evaluate OF second deriv for variate " +
					variate
			);
		}

		double firstOrderSlope = firstOrderDifferential.calcSlope (false);

		return Math.abs (objectiveFunctionValue * secondOrderDifferential.calcSlope (false)) <
			(firstOrderSlope * firstOrderSlope * _convergenceControlParams.zoneEdgeLimit());
	}

	private boolean leftObjectiveFunctionValidityEdgeReached (
		final double variateLeft,
		final double objectiveFunctionValueLeft,
		final InitializationHeuristics initializationHeuristics)
	{
		return !NumberUtil.IsValid (objectiveFunctionValueLeft) || (
			null != initializationHeuristics &&
			NumberUtil.IsValid (initializationHeuristics.bracketFloor()) &&
			variateLeft < initializationHeuristics.bracketFloor()
		);
	}

	private boolean rightObjectiveFunctionValidityEdgeReached (
		final double variateRight,
		final double objectiveFunctionValueRight,
		final InitializationHeuristics initializationHeuristics)
	{
		return !NumberUtil.IsValid (objectiveFunctionValueRight) || (
			null != initializationHeuristics &&
			NumberUtil.IsValid (initializationHeuristics.bracketCeiling()) &&
			variateRight > initializationHeuristics.bracketCeiling()
		);
	}

	private double startingBracketVariate (
		final BracketingControlParams bracketingControlParams,
		final InitializationHeuristics initializationHeuristics)
	{
		if (null != initializationHeuristics &&
			NumberUtil.IsValid (initializationHeuristics.startingBracketMid()))
		{
			return initializationHeuristics.startingBracketMid();
		}

		if (null != initializationHeuristics &&
			NumberUtil.IsValid (initializationHeuristics.startingBracketLeft()) &&
			NumberUtil.IsValid (initializationHeuristics.startingBracketRight()))
		{
			return 0.5 * (
				initializationHeuristics.startingBracketLeft() +
					initializationHeuristics.startingBracketRight()
			);
		}

		return bracketingControlParams.startingVariate();
	}

	private double startingBracketWidth (
		final BracketingControlParams bracketingControlParams,
		final InitializationHeuristics initializationHeuristics)
	{
		if (null != initializationHeuristics) {
			double bracketStartLeft = initializationHeuristics.startingBracketLeft();

			double bracketStartRight = initializationHeuristics.startingBracketRight();

			if (NumberUtil.IsValid (bracketStartLeft) &&
				NumberUtil.IsValid (bracketStartRight) &&
				bracketStartRight > bracketStartLeft)
			{
				return bracketStartRight - bracketStartLeft;
			}
		}

		return bracketingControlParams.bracketStartingWidth();
	}

	/**
	 * <i>ExecutionInitializer</i> Constructor
	 * 
	 * @param objectiveFunction Objective Function
	 * @param convergenceControlParams Convergence Control Parameters
	 * @param trendBracketRight TRUE - Start Right Trending in search of a Bracket Variate
	 * 
	 * @throws Exception Thrown if inputs are invalid
	 */

	public ExecutionInitializer (
		final R1ToR1 objectiveFunction,
		final ConvergenceControlParams convergenceControlParams,
		final boolean trendBracketRight)
		throws Exception
	{
		if (null == (_objectiveFunction = objectiveFunction))
			throw new Exception ("ExecutionInitializer constructor: Invalid inputs");

		if (null == (_convergenceControlParams = convergenceControlParams)) {
			_convergenceControlParams = new ConvergenceControlParams();
		}

		_trendBracketRight = trendBracketRight;
	}

	/**
	 * Set up the bracket to be used for the eventual search kick-off
	 * 
	 * @param initializationHeuristics Optional <i>InitializationHeuristics</i> Instance
	 * @param objectiveFunctionValueGoal The Objective Function Value Goal
	 * 
	 * @return <i>BracketingOutput</i> Instance
	 */

	public BracketingOutput initializeBracket (
		final InitializationHeuristics initializationHeuristics,
		final double objectiveFunctionValueGoal)
	{
		BracketingControlParams bracketingControlParams = (
			null != initializationHeuristics &&
				null != initializationHeuristics.customBracketingControlParams()
		) ? initializationHeuristics.customBracketingControlParams() : new BracketingControlParams();

		int numberOfExpansions = bracketingControlParams.maximumNumberOfExpansions();

		BracketingOutput bracketingOutput = new BracketingOutput();

		StartingVariateObjectiveFunction startingVariateObjectiveFunction = initializeBracketingVariate (
			numberOfExpansions,
			startingBracketVariate (bracketingControlParams, initializationHeuristics),
			startingBracketWidth (bracketingControlParams, initializationHeuristics),
			bracketingControlParams.bracketWidthExpansionFactor(),
			bracketingOutput
		);

		if (null == startingVariateObjectiveFunction) {
			return bracketingOutput;
		}

		boolean leftObjectiveFunctionValidityEdgeReached = false;
		boolean rightObjectiveFunctionValidityEdgeReached = false;
		double variateLeft = startingVariateObjectiveFunction._variate;
		double variateRight = startingVariateObjectiveFunction._variate;
		double previousVariateLeft = startingVariateObjectiveFunction._variate;
		double previousVariateRight = startingVariateObjectiveFunction._variate;
		double objectiveFunctionValueLeft = startingVariateObjectiveFunction._response;
		double objectiveFunctionValueRight = startingVariateObjectiveFunction._response;
		double previousObjectiveFunctionValueLeft = startingVariateObjectiveFunction._response;
		double previousObjectiveFunctionValueRight = startingVariateObjectiveFunction._response;

		double bracketWidth = bracketingControlParams.bracketStartingWidth();

		while (0 <= numberOfExpansions--) {
			if (!bracketingOutput.incrementIterationCount()) {
				return null;
			}

			if (leftObjectiveFunctionValidityEdgeReached && rightObjectiveFunctionValidityEdgeReached) {
				return bracketingOutput;
			}

			if (!leftObjectiveFunctionValidityEdgeReached) {
				previousObjectiveFunctionValueLeft = objectiveFunctionValueLeft;
				previousVariateLeft = variateLeft;
				variateLeft -= bracketWidth;

				try {
					if (bracketingDone (
						variateLeft,
						variateRight,
						objectiveFunctionValueLeft = evaluateObjectiveFunction (variateLeft),
						objectiveFunctionValueRight,
						objectiveFunctionValueGoal,
						bracketingOutput
					) && bracketingOutput.incrementObjectiveFunctionCalculationCount()
				)
				{
					return bracketingOutput;
				}
				} catch (Exception e) {
					objectiveFunctionValueLeft = Double.NaN;
				}

				if (leftObjectiveFunctionValidityEdgeReached = leftObjectiveFunctionValidityEdgeReached (
					variateLeft,
					objectiveFunctionValueLeft,
					initializationHeuristics
				))
				{
					objectiveFunctionValueLeft = previousObjectiveFunctionValueLeft;
					variateLeft = previousVariateLeft;
				}
			}

			if (!rightObjectiveFunctionValidityEdgeReached) {
				previousObjectiveFunctionValueRight = objectiveFunctionValueRight;
				previousVariateRight = variateRight;
				variateRight += bracketWidth;

				try {
					if (bracketingDone (
						variateLeft,
						variateRight,
						objectiveFunctionValueLeft,
						objectiveFunctionValueRight = evaluateObjectiveFunction (variateRight),
						objectiveFunctionValueGoal,
						bracketingOutput
					) && bracketingOutput.incrementObjectiveFunctionCalculationCount()
				)
				{
					return bracketingOutput;
				}
				} catch (Exception e) {
					objectiveFunctionValueRight = Double.NaN;
				}

				if (rightObjectiveFunctionValidityEdgeReached = rightObjectiveFunctionValidityEdgeReached (
					variateRight,
					objectiveFunctionValueRight,
					initializationHeuristics
				))
				{
					objectiveFunctionValueRight = previousObjectiveFunctionValueRight;
					variateRight = previousVariateRight;
				}
			}

			if (bracketingDone (
				variateLeft,
				variateRight,
				objectiveFunctionValueLeft,
				objectiveFunctionValueRight,
				objectiveFunctionValueGoal,
				bracketingOutput
			))
				return bracketingOutput;

			bracketWidth *= bracketingControlParams.bracketWidthExpansionFactor();
		}

		return null;
	}

	/**
	 * Initialize the starting variate to within the fixed point convergence zone
	 * 
	 * @param initializationHeuristics Optional <i>InitializationHeuristics<i> Instance
	 * @param objectiveFunctionValueGoal The Objective Function Value Goal
	 * 
	 * @return The Convergence Zone Output
	 */

	public ConvergenceOutput initializeVariate (
		final InitializationHeuristics initializationHeuristics,
		final double objectiveFunctionValueGoal)
	{
		if (!NumberUtil.IsValid (objectiveFunctionValueGoal)) {
			return null;
		}

		ConvergenceOutput convergenceOutput = new ConvergenceOutput();

		BracketingOutput bracketingOutput =
			initializeBracket (initializationHeuristics, objectiveFunctionValueGoal);

		if (null != bracketingOutput && bracketingOutput.done()) {
			return bracketingOutput.makeConvergenceVariate();
		}

		double convergenceZoneVariate = _convergenceControlParams.zoneVariateBegin();

		int fixedPointConvergenceIterations = _convergenceControlParams.fixedPointIterations();

		while (0 != fixedPointConvergenceIterations--) {
			if (!convergenceOutput.incrementIterationCount()) {
				return convergenceOutput;
			}

			try {
				if (inConvergenceZone (
					convergenceZoneVariate,
					objectiveFunctionValueGoal,
					convergenceOutput
				))
				{
					convergenceOutput.done (convergenceZoneVariate);

					return convergenceOutput;
				}
			} catch (Exception e) {
			}

			try {
				if (inConvergenceZone (
					-1. * convergenceZoneVariate,
					objectiveFunctionValueGoal,
					convergenceOutput
				))
				{
					convergenceOutput.done (-1. * convergenceZoneVariate);

					return convergenceOutput;
				}
			} catch (Exception e) {
			}

			convergenceZoneVariate *= _convergenceControlParams.zoneVariateBumpFactor();
		}

		return null;
	}

	/**
	 * Initialize the starting bracket within the specified boundary
	 * 
	 * @param initializationHeuristics <i>InitializationHeuristics<i> Instance containing the hard search
	 * 	Edges
	 * @param objectiveFunctionValueGoal The Objective Function Value Goal
	 * 
	 * @return Results of the Verification
	 */

	public BracketingOutput verifyHardSearchEdges (
		final InitializationHeuristics initializationHeuristics,
		final double objectiveFunctionValueGoal)
	{
		if (null == initializationHeuristics ||
			!NumberUtil.IsValid (initializationHeuristics.searchStartLeft()) ||
			!NumberUtil.IsValid (initializationHeuristics.searchStartRight()) ||
			!NumberUtil.IsValid (objectiveFunctionValueGoal))
		{
			return null;
		}

		try {
			BracketingOutput bracketingOutput = new BracketingOutput();

			double searchStartLeft = initializationHeuristics.searchStartLeft();

			double searchStartRight = initializationHeuristics.searchStartRight();

			if (bracketingDone (
					searchStartLeft,
					searchStartRight,
					evaluateObjectiveFunction (searchStartLeft),
					evaluateObjectiveFunction (searchStartRight),
					objectiveFunctionValueGoal,
					bracketingOutput
				) && bracketingOutput.incrementObjectiveFunctionCalculationCount()
			)
			{
				return bracketingOutput;
			}
		} catch (Exception e) {
		}

		return null;
	}
}
