
package org.drip.function.r1tor1solver;

import org.drip.function.definition.R1ToR1;
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
 * <i>VariateIteratorPrimitive</i> implements the various Primitive Variate Iterator routines. It implements
 * 	 the following iteration primitives:
 * 	<br>
 * 	<ul>
 * 		<li>Bisection</li>
 * 		<li>False Position</li>
 * 		<li>Quadratic</li>
 * 		<li>Inverse Quadratic</li>
 * 		<li>Ridder</li>
 * </ul>
 * <br>
 * 
 * 	It may be readily enhanced to accommodate additional primitives.It exposes the following Functions:
 *
 *  <ul>
 * 		<li>Bisection</li>
 * 		<li>False Position</li>
 * 		<li>Quadratic Interpolation</li>
 * 		<li>Inverse Quadratic Interpolation</li>
 * 		<li>Ridder's Method</li>
 * 		<li>Iterate for the Next Variate using Bisection</li>
 * 		<li>Iterate for the Next Variate using False Position</li>
 * 		<li>Iterate for the Next Variate using Quadratic Interpolation</li>
 * 		<li>Iterate for the Next Variate using Inverse Quadratic Interpolation</li>
 * 		<li>Iterate for the Next Variate using Ridder's Method</li>
 * 		<li>Iterate for the Next Variate using the Multi-function Method</li>
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

public class VariateIteratorPrimitive
{

	/**
	 * Bisection
	 */

	public static int BISECTION = 0;

	/**
	 * False Position
	 */

	public static int FALSE_POSITION = 1;

	/**
	 * Quadratic Interpolation
	 */

	public static int QUADRATIC_INTERPOLATION = 2;

	/**
	 * Inverse Quadratic Interpolation
	 */

	public static int INVERSE_QUADRATIC_INTERPOLATION = 3;

	/**
	 * Ridder's Method
	 */

	public static int RIDDER = 4;

	/**
	 * Iterate for the Next Variate using Bisection
	 * 
	 * @param x1 Left variate
	 * @param x2 Right variate
	 * 
	 * @return The Next Variate
	 * 
	 * @throws Exception Thrown if Inputs are Invalid
	 */

	public static final double Bisection (
		final double x1,
		final double x2)
		throws Exception
	{
		if (!NumberUtil.IsValid (x1) || !NumberUtil.IsValid (x2)) {
			throw new Exception ("VariateIteratorPrimitive::Bisection => Invalid Inputs " + x2);
		}

		return 0.5 * (x1 + x2);
	}

	/**
	 * Iterate for the Next Variate using False Position
	 * 
	 * @param x1 Left variate
	 * @param x2 Right variate
	 * @param y1 Left OF value
	 * @param y2 Right OF value
	 * 
	 * @return The Next Variate
	 * 
	 * @throws Exception Thrown if Inputs are Invalid
	 */

	public static final double FalsePosition (
		final double x1,
		final double x2,
		final double y1,
		final double y2)
		throws Exception
	{
		if (!NumberUtil.IsValid (x1) ||
			!NumberUtil.IsValid (x2) ||
			!NumberUtil.IsValid (y1) ||
			!NumberUtil.IsValid (y2))
		{
			throw new Exception ("VariateIteratorPrimitive::FalsePosition => Invalid Inputs");
		}

		return x1 + ((x1 - x2) / (y2 - y1) * y1);
	}

	/**
	 * Iterate for the Next Variate using Quadratic Interpolation
	 * 
	 * @param x1 Left variate
	 * @param x2 Intermediate variate
	 * @param x3 Right variate
	 * @param y1 Left OF value
	 * @param y2 Intermediate OF value
	 * @param y3 Right OF value
	 * 
	 * @return The Next Variate
	 * 
	 * @throws Exception Thrown if Inputs are Invalid
	 */

	public static final double QuadraticInterpolation (
		final double x1,
		final double x2,
		final double x3,
		final double y1,
		final double y2,
		final double y3)
		throws Exception
	{
		if (!NumberUtil.IsValid (x1) ||
			!NumberUtil.IsValid (x2) ||
			!NumberUtil.IsValid (x3) ||
			!NumberUtil.IsValid (y1) ||
			!NumberUtil.IsValid (y2) ||
			!NumberUtil.IsValid (y3))
		{
			throw new Exception ("VariateIteratorPrimitive.QuadraticInterpolation => Invalid Inputs!");
		}

		double a = y1 / (x1 - x2) / (x1 - x3);
		a       += y2 / (x2 - x3) / (x2 - x1);
		a       += y3 / (x3 - x1) / (x3 - x2);
		double b = -1. * (x2 + x3) * y1 / (x1 - x2) / (x1 - x3);
		b       -=       (x3 + x1) * y2 / (x2 - x3) / (x2 - x1);
		b       -=       (x1 + x2) * y3 / (x3 - x1) / (x3 - x2);
		double c = x2 * x3 * y1 / (x1 - x2) / (x1 - x3);
		c       += x3 * x1 * y2 / (x2 - x3) / (x2 - x1);
		c       += x1 * x2 * y3 / (x3 - x1) / (x3 - x2);
		double sqrtArguments = b * b - 4. * a * c;

		if (0. > sqrtArguments) {
			throw new Exception ("VariateIteratorPrimitive::QuadraticInterpolation => No real roots!");
		}

		double sqrt = Math.sqrt (sqrtArguments);

		double root1 = (-1. * b + sqrt) / 2. / a;
		double root2 = (-1. * b - sqrt) / 2. / a;

		if (x1 > root1 || x3 < root1) {
			return root2;
		}

		if (x1 > root2 || x3 < root2) {
			return root1;
		}

		return Math.abs (x2 - root1) < Math.abs (x2 - root2) ? root1 : root2;
	}

	/**
	 * Iterate for the Next Variate using Inverse Quadratic Interpolation
	 * 
	 * @param x1 Left variate
	 * @param x2 Intermediate variate
	 * @param x3 Right variate
	 * @param y1 Left OF value
	 * @param y2 Intermediate OF value
	 * @param y3 Right OF value
	 * 
	 * @return The Next Variate
	 * 
	 * @throws Exception Thrown if Inputs are Invalid
	 */

	public static final double InverseQuadraticInterpolation (
		final double x1,
		final double x2,
		final double x3,
		final double y1,
		final double y2,
		final double y3)
		throws Exception
	{
		if (!NumberUtil.IsValid (x1) ||
			!NumberUtil.IsValid (x2) ||
			!NumberUtil.IsValid (x3) ||
			!NumberUtil.IsValid (y1) ||
			!NumberUtil.IsValid (y2) ||
			!NumberUtil.IsValid (y3))
		{
			throw new Exception (
				"VariateIteratorPrimitive::InverseQuadraticInterpolation => Invalid Inputs!"
			);
		}

		double nextRoot = (y2 * y3 * x1 / (y1 - y2) / (y1 - y3));
		nextRoot       += (y3 * y1 * x2 / (y2 - y3) / (y2 - y1));
		nextRoot       += (y1 * y2 * x3 / (y3 - y1) / (y3 - y2));
		return nextRoot;
	}

	/**
	 * Iterate for the Next Variate using Ridder's Method
	 * 
	 * @param x1 Left variate
	 * @param x2 Intermediate variate
	 * @param x3 Right variate
	 * @param y1 Left OF value
	 * @param y2 Intermediate OF value
	 * @param y3 Right OF value
	 * 
	 * @return The Next Variate
	 * 
	 * @throws Exception Thrown if inputs are invalid
	 */

	public static final double Ridder (
		final double x1,
		final double x2,
		final double x3,
		final double y1,
		final double y2,
		final double y3)
		throws Exception
	{
		if (!NumberUtil.IsValid (x1) ||
			!NumberUtil.IsValid (x2) ||
			!NumberUtil.IsValid (x3) ||
			!NumberUtil.IsValid (y1) ||
			!NumberUtil.IsValid (y2) ||
			!NumberUtil.IsValid (y3))
		{
			throw new Exception ("VariateIteratorPrimitive::Ridder => Invalid inputs!");
		}

		double sqrtArgument = y3 * y3 - y1 * y2;

		if (0. > sqrtArgument) {
			throw new Exception ("VariateIteratorPrimitive::Ridder => No real roots!");
		}

		return x3 + (x3 - x1) * y3 * Math.signum (y1 - y2) / Math.sqrt (sqrtArgument);
	}

	/**
	 * Iterate for the Next Variate using the Multi-function Method
	 * 
	 * @param x1 Left variate
	 * @param x2 Intermediate variate
	 * @param x3 Right variate
	 * @param y1 Left OF value
	 * @param y2 Intermediate OF value
	 * @param y3 Right OF value
	 * @param objectiveFunction Objective Function
	 * @param objectiveFunctionValueTarget Objective Function Value Target
	 * @param fixedPointFinderOutput Root Finder Output
	 * 
	 * @return The Next Variate
	 * 
	 * @throws Exception Thrown if Inputs are Invalid
	 */

	public static final double MultiFunction (
		final double x1,
		final double x2,
		final double x3,
		final double y1,
		final double y2,
		final double y3,
		final R1ToR1 objectiveFunction,
		final double objectiveFunctionValueTarget,
		final FixedPointFinderOutput fixedPointFinderOutput)
		throws Exception
	{
		if (!NumberUtil.IsValid (x1) ||
			!NumberUtil.IsValid (x2) ||
			!NumberUtil.IsValid (x3) ||
			!NumberUtil.IsValid (y1) ||
			!NumberUtil.IsValid (y2) ||
			!NumberUtil.IsValid (y3) ||
			!NumberUtil.IsValid (objectiveFunctionValueTarget) ||
			null == fixedPointFinderOutput ||
			null == objectiveFunction)
		{
			throw new Exception ("VariateIteratorPrimitive::MultiFunction => Invalid inputs!");
		}

		double nextRoot = Bisection (x1, x2);

		if (!fixedPointFinderOutput.incrementObjectiveFunctionCalculations()) {
			throw new Exception ("VariateIteratorPrimitive::MultiFunction => Cannot increment rfop!");
		}

		double targetDifference =
			Math.abs (objectiveFunction.evaluate (nextRoot) - objectiveFunctionValueTarget);

		try {
			double rootSecant = FalsePosition (x1, x2, y1, y2);

			if (!fixedPointFinderOutput.incrementObjectiveFunctionCalculations()) {
				throw new Exception ("VariateIteratorPrimitive::MultiFunction => Cannot increment rfop!");
			}

			double targetDifferenceSecant =
				Math.abs (objectiveFunction.evaluate (rootSecant) - objectiveFunctionValueTarget);

			if (targetDifferenceSecant < targetDifference) {
				nextRoot = rootSecant;
				targetDifference = targetDifferenceSecant;
			}
		} catch (Exception e) {
		}

		try {
			double rootQuadraticInterpolation = QuadraticInterpolation (x1, x2, x3, y1, y2, y3);

			if (!fixedPointFinderOutput.incrementObjectiveFunctionCalculations()) {
				throw new Exception ("VariateIteratorPrimitive::MultiFunction => Cannot increment rfop!");
			}

			double targetDifferenceQuadraticInterpolation = Math.abs (
				objectiveFunction.evaluate (rootQuadraticInterpolation) - objectiveFunctionValueTarget
			);

			if (targetDifferenceQuadraticInterpolation < targetDifference) {
				nextRoot = rootQuadraticInterpolation;
				targetDifference = targetDifferenceQuadraticInterpolation;
			}
		} catch (Exception e) {
		}

		try {
			double rootInverseQuadraticInterpolation = QuadraticInterpolation (x1, x2, x3, y1, y2, y3);

			if (!fixedPointFinderOutput.incrementObjectiveFunctionCalculations()) {
				throw new Exception ("VariateIteratorPrimitive::MultiFunction => Cannot increment rfop!");
			}

			double targetDifferenceInverseQuadraticInterpolation = Math.abs (
				objectiveFunction.evaluate (rootInverseQuadraticInterpolation) - objectiveFunctionValueTarget
			);

			if (targetDifferenceInverseQuadraticInterpolation < targetDifference) {
				nextRoot = rootInverseQuadraticInterpolation;
				targetDifference = targetDifferenceInverseQuadraticInterpolation;
			}
		} catch (Exception e) {
		}

		try {
			double rootRidder = Ridder (x1, x2, x3, y1, y2, y3);

			if (!fixedPointFinderOutput.incrementObjectiveFunctionCalculations()) {
				throw new Exception ("VariateIteratorPrimitive::MultiFunction => Cannot increment rfop!");
			}

			double targetDifferenceRidder =
				Math.abs (objectiveFunction.evaluate (rootRidder) - objectiveFunctionValueTarget);

			if (targetDifferenceRidder < targetDifference) {
				nextRoot = rootRidder;
				targetDifference = targetDifferenceRidder;
			}
		} catch (Exception e) {
		}

		return nextRoot;
	}
}
