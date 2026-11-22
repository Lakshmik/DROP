
package org.drip.function.definition;

import org.drip.function.r1tor1solver.FixedPointFinder;
import org.drip.function.r1tor1solver.FixedPointFinderOutput;
import org.drip.function.r1tor1solver.FixedPointFinderZheng;
import org.drip.numerical.common.NumberUtil;
import org.drip.numerical.differentiation.DerivativeControl;
import org.drip.numerical.differentiation.Differential;
import org.drip.numerical.r1integration.Integrator;

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
 * <i>R1ToR1</i> provides the evaluation of the objective function and its derivatives for a specified
 * 	variate. Default implementation of the derivatives are for non-analytical black box objective functions.
 *  It provides the following Functions:
 *
 *  <ul>
 * 		<li>Evaluate for the given variate</li>
 * 		<li>Calculate the Differential</li>
 * 		<li>Calculate the Ordered Derivative</li>
 * 		<li>Calculate the derivative as a double</li>
 * 		<li>Integrate over the given range</li>
 * 		<li>Compute the Maximal Variate and the Corresponding Function Value</li>
 * 		<li>Compute the Maximum VOP within the Variate Range</li>
 * 		<li>Compute the Minimal Variate and the Corresponding Function Value</li>
 * 		<li>Compute the Minimum VOP within the Variate Range</li>
 * 		<li>Compute the Anti-Derivative Function</li>
 * 		<li>Compute the Residue if the Variate is a Pole</li>
 * 		<li>Compute the Condition Number at the specified Variate</li>
 *  </ul>
 * 
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/function/README.md">R<sup>d</sup> To R<sup>d</sup> Function Analysis</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/function/definition/README.md">Function Implementation Ancillary Support Objects</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public abstract class R1ToR1
{
	protected DerivativeControl _derivativeControl = null;

	protected R1ToR1 (
		final DerivativeControl derivativeControl)
	{
		if (null == (_derivativeControl = derivativeControl)) {
			_derivativeControl = new DerivativeControl();
		}
	}

	/**
	 * Evaluate for the given variate
	 * 
	 * @param variate Variate
	 *  
	 * @return Returns the calculated value
	 * 
	 * @throws Exception Thrown if evaluation cannot be done
	 */

	public abstract double evaluate (
		final double variate)
		throws Exception;

	/**
	 * Calculate the Differential
	 * 
	 * @param variate Variate at which the derivative is to be calculated
	 * @param objectiveFunctionBase Base Value for the Objective Function
	 * @param order Order of the derivative to be computed
	 * 
	 * @return The Derivative
	 */

	public Differential differential (
		final double variate,
		final double objectiveFunctionBase,
		final int order)
	{
		if (!NumberUtil.IsValid (variate) || 0 >= order) {
			return null;
		}

		double derivative = 0.;
		double orderedVariateInfinitesimal = 1.;
		double variateInfinitesimal = Double.NaN;

		try {
			variateInfinitesimal = _derivativeControl.getVariateInfinitesimal (variate);
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}

		for (int orderIndex = 0; orderIndex <= order; ++orderIndex) {
			if (0 != orderIndex) {
				orderedVariateInfinitesimal *= (2. * variateInfinitesimal);
			}

			try {
				derivative += (orderIndex % 2 == 0 ? 1 : -1) * NumberUtil.NCK (order, orderIndex) *
					evaluate (variate + variateInfinitesimal * (order - 2. * orderIndex));
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		try {
			return new Differential (orderedVariateInfinitesimal, derivative);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Calculate the Ordered Derivative
	 * 
	 * @param variate Variate at which the derivative is to be calculated
	 * @param order Order of the derivative to be computed
	 * 
	 * @return The Derivative
	 */

	public Differential differential (
		final double variate,
		final int order)
	{
		try {
			return differential (variate, evaluate (variate), order);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Calculate the derivative as a double
	 * 
	 * @param variate Variate at which the derivative is to be calculated
	 * @param order Order of the derivative to be computed
	 * 
	 * @return The Derivative
	 * 
	 * @throws Exception Thrown if Inputs are Invalid
	 */

	public double derivative (
		final double variate,
		final int order)
		throws Exception
	{
		return differential (variate, evaluate (variate), order).calcSlope (true);
	}

	/**
	 * Integrate over the given range
	 * 
	 * @param begin Range Begin 
	 * @param end Range End 
	 *  
	 * @return The Integrated Value
	 * 
	 * @throws Exception Thrown if evaluation cannot be done
	 */

	public double integrate (
		final double begin,
		final double end)
		throws Exception
	{
		return Integrator.Boole (this, begin, end);
	}

	/**
	 * Compute the Maximal Variate and the Corresponding Function Value
	 * 
	 * @return The Maximal Variate and the Corresponding Function Value
	 */

	public VariateOutputPair maxima()
	{
		try {
			FixedPointFinder fixedPointFinder = new FixedPointFinderZheng (
				0.,
				new R1ToR1 (null)
				{
					@Override public double evaluate (
						final double x)
						throws Exception
					{
						return derivative (x, 1);
					}
				},
				false
			);

			FixedPointFinderOutput fixedPointFinderOutput = fixedPointFinder.findRoot();

			if (null == fixedPointFinderOutput) {
				return null;
			}

			double extrema = fixedPointFinderOutput.root();

			return 0. <= derivative (extrema, 2) ? null : new VariateOutputPair (
				new double[]
				{
					extrema
				},
				new double[] {
					evaluate (extrema)
				}
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Compute the Maximum VOP within the Variate Range
	 * 
	 * @param variateLeft The Range Left End
	 * @param variateRight The Range Right End
	 * 
	 * @return The Maximum VOP
	 */

	public VariateOutputPair maxima (
		final double variateLeft,
		final double variateRight)
	{
		if (!NumberUtil.IsValid (variateLeft) ||
			!NumberUtil.IsValid (variateRight) || variateLeft >= variateRight)
		{
			return null;
		}

		VariateOutputPair variateOutputPair = maxima();

		if (null != variateOutputPair) {
			double root = variateOutputPair.variates()[0];

			if (variateLeft <= root && variateRight >= root) {
				return variateOutputPair;
			}
		}

		try {
			double leftOutput = evaluate (variateLeft);

			double rightOutput = evaluate (variateRight);

			return leftOutput > rightOutput ? new VariateOutputPair (
				new double[]
				{
					variateLeft
				},
				new double[]
				{
					leftOutput
				}
			) : new VariateOutputPair (
				new double[]
				{
					variateRight
				},
				new double[]
				{
					rightOutput
				}
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Compute the Minimal Variate and the Corresponding Function Value
	 * 
	 * @return The Minimal Variate and the Corresponding Function Value
	 */

	public VariateOutputPair minima()
	{
		try {
			FixedPointFinder fixedPointFinder = new FixedPointFinderZheng (
				0.,
				new R1ToR1 (null)
				{
					@Override public double evaluate (
						final double x)
						throws Exception
					{
						return derivative (x, 1);
					}
				},
				false
			);

			FixedPointFinderOutput fixedPointFinderOutput = fixedPointFinder.findRoot();

			if (null == fixedPointFinderOutput) {
				return null;
			}

			double extrema = fixedPointFinderOutput.root();

			return 0. >= derivative (extrema, 2) ? null : new VariateOutputPair (
				new double[]
				{
					extrema
				},
				new double[]
				{
					evaluate (extrema)
				}
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Compute the Minimum VOP within the Variate Range
	 * 
	 * @param variateLeft The Range Left End
	 * @param variateRight The Range Right End
	 * 
	 * @return The Minimum VOP
	 */

	public VariateOutputPair minima (
		final double variateLeft,
		final double variateRight)
	{
		if (!NumberUtil.IsValid (variateLeft) ||
			!NumberUtil.IsValid (variateRight) || variateLeft >= variateRight)
		{
			return null;
		}

		VariateOutputPair variateOutputPair = minima();

		if (null != variateOutputPair) {
			double root = variateOutputPair.variates()[0];

			if (variateLeft <= root && variateRight >= root) {
				return variateOutputPair;
			}
		}

		try {
			double leftOutput = evaluate (variateLeft);

			double rightOutput = evaluate (variateRight);

			return leftOutput < rightOutput ? new VariateOutputPair (
				new double[]
				{
					variateLeft
				},
				new double[]
				{
					leftOutput
				}
			) : new VariateOutputPair (
				new double[]
				{
					variateRight
				},
				new double[]
				{
					rightOutput
				}
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Compute the Anti-Derivative Function
	 * 
	 * @return The Anti-Derivative Function
	 */

	public R1ToR1 antiDerivative()
	{
		return null;
	}

	/**
	 * Compute the Residue if the Variate is a Pole
	 * 
	 * @param x Variate
	 * 
	 * @return The Residue if the Variate is a Pole
	 */

	public PoleResidue poleResidue (
		final double x)
	{
		return null;
	}

	/**
	 * Compute the Condition Number at the specified Variate
	 * 
	 * @param x Variate
	 * 
	 * @return The Condition Number
	 * 
	 * @throws Exception Thrown if the Condition Number cannot be computed
	 */

	public double conditionNumber (
		final double x)
		throws Exception
	{
		if (!NumberUtil.IsValid (x)) {
			throw new Exception ("R1ToR1::conditionNumber=> Invalid Inputs");
		}

		return x * derivative (x, 1) / evaluate (x);
	}
}
