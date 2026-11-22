
package org.drip.function.definition;

import org.drip.numerical.common.NumberUtil;
import org.drip.numerical.differentiation.DerivativeControl;
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
 * <i>RdToR1</i> provides the evaluation of the R<sup>d</sup> To R<sup>1</sup> objective function and its
 * 	derivatives for a specified set of R<sup>d</sup> variates. Default implementation of the derivatives are
 * 	for non-analytical lack box objective functions. It provides the following Functions:
 *
 *  <ul>
 * 		<li>Validate the Input Double Array</li>
 * 		<li>Retrieve the Dimension of the Input Variate</li>
 * 		<li>Calculate the Differential</li>
 * 		<li>Calculate the Derivative as a Double</li>
 * 		<li>Evaluate the Jacobian for the given Input Variates</li>
 * 		<li>Construct an Instance of the Unit Gradient Vector at the given Input Variates</li>
 * 		<li>Evaluate The Hessian for the given Input Variates</li>
 * 		<li>Integrate over the given Input Range Using Uniform Monte-Carlo</li>
 * 		<li>Compute the Maximum <i>VariateOutputPair</i> within the Variate Array Range Using Uniform Monte-Carlo</li>
 * 		<li>Compute the Minimum <i>VariateOutputPair</i> within the Variate Array Range Using Uniform Monte-Carlo</li>
 * 		<li>Compute the Modulus of the Gradient at the Specified Variate</li>
 * 		<li>Generate the Gradient Modulus Function</li>
 * 		<li>Retrieve the L<sub>2</sub> Condition Number for the Function at the Domain Value</li>
 * 		<li>Retrieve the L<sub>p</sub> Condition Number for the Function at the Domain Value</li>
 * 		<li>Retrieve the L<sub>Infinity</sub> Condition Number for the Function at the Domain Value</li>
 * 		<li>Retrieve the Default Condition Number for the Function at the Domain Value - this uses L<sub>2</sub> Metric</li>
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

public abstract class RdToR1
{
	private static final int EXTREMA_SAMPLING = 10000;
	private static final int QUADRATURE_SAMPLING = 10000;

	protected static final int DIMENSION_NOT_FIXED = -1;

	protected DerivativeControl _derivativeControl = null;

	/**
	 * Validate the Input Double Array
	 * 
	 * @param variateArray The Input Double Array
	 * 
	 * @return The Input Double Array consists of valid Values
	 */

	public static final boolean ValidateInput (
		final double[] variateArray)
	{
		if (null == variateArray || 0 == variateArray.length) {
			return false;
		}

		for (int variateIndex = 0; variateIndex < variateArray.length; ++variateIndex) {
			if (!NumberUtil.IsValid (variateArray[variateIndex])) {
				return false;
			}
		}

		return true;
	}

	private double normedVariateL2 (
		final double[] variateArray)
		throws Exception
	{
		if (null == variateArray || 0 == variateArray.length || !NumberUtil.IsValid (variateArray)) {
			throw new Exception ("RdToR1::normedVariateL2 => Cannot calculate Jacobian");
		}

		double normedVariate = 0.;

		for (double variate : variateArray) {
			normedVariate += Math.abs (variate * variate);
		}

		return Math.sqrt (normedVariate);
	}

	private double normedVariateLp (
		final double[] variateArray,
		final int p)
		throws Exception
	{
		if (null == variateArray || 0 == variateArray.length || !NumberUtil.IsValid (variateArray) ||
			2 >= p)
		{
			throw new Exception ("RdToR1::normedVariateLp => Cannot calculate Jacobian");
		}

		double normedVariate = 0.;

		for (double variate : variateArray) {
			normedVariate += Math.pow (Math.abs (variate), p);
		}

		return Math.pow (normedVariate, 1. / p);
	}

	private double normedVariateLInfinity (
		final double[] variateArray)
		throws Exception
	{
		if (null == variateArray || 0 == variateArray.length || !NumberUtil.IsValid (variateArray)) {
			throw new Exception ("RdToR1::normedVariateLInfinity => Cannot calculate Jacobian");
		}

		double normedVariate = 0.;

		for (double variate : variateArray) {
			normedVariate += Math.abs (variate);
		}

		return normedVariate;
	}

	private double normedJacobianL2 (
		final double[] variateArray)
		throws Exception
	{
		double[] jacobianArray = jacobian (variateArray);

		if (null == jacobianArray || jacobianArray.length != variateArray.length) {
			throw new Exception ("RdToR1::normedJacobianL2 => Cannot calculate Jacobian");
		}

		double normedJacobian = 0.;

		for (double jacobianEntry : jacobianArray) {
			normedJacobian += Math.abs (jacobianEntry * jacobianEntry);
		}

		return Math.abs (normedJacobian);
	}

	private double normedJacobianLp (
		final double[] variateArray,
		final int p)
		throws Exception
	{
		double[] jacobianArray = jacobian (variateArray);

		if (null == jacobianArray || jacobianArray.length != variateArray.length ||
			!NumberUtil.IsValid (jacobianArray) ||
			2 >= p)
		{
			throw new Exception ("RdToR1::normedJacobianLp => Cannot calculate Jacobian");
		}

		double normedJacobianLp = 0.;

		for (double jacobianElement : jacobianArray) {
			normedJacobianLp += Math.pow (Math.abs (jacobianElement), p);
		}

		return Math.pow (normedJacobianLp, 1. / p);
	}

	private double normedJacobianLInfinity (
		final double[] variateArray)
		throws Exception
	{
		double[] jacobianArray = jacobian (variateArray);

		if (null == jacobianArray || jacobianArray.length != variateArray.length) {
			throw new Exception ("RdToR1::normedJacobianLInfinity => Cannot calculate Jacobian");
		}

		double normedJacobian = 0.;

		for (double jacobianEntry : jacobianArray) {
			normedJacobian += Math.abs (jacobianEntry);
		}

		return normedJacobian;
	}

	protected RdToR1 (
		final DerivativeControl derivativeControl)
	{
		if (null == (_derivativeControl = derivativeControl)) {
			_derivativeControl = new DerivativeControl();
		}
	}

	/**
	 * Retrieve the Dimension of the Input Variate
	 * 
	 * @return The Dimension of the Input Variate
	 */

	public abstract int dimension();

	/**
	 * Evaluate for the given Input Variates
	 * 
	 * @param variateArray Array of Input Variates
	 *  
	 * @return The Calculated Value
	 * 
	 * @throws Exception Thrown if the Evaluation cannot be done
	 */

	public abstract double evaluate (
		final double[] variateArray
	) throws Exception;

	/**
	 * Calculate the Differential
	 * 
	 * @param variateArray Variate Array at which the derivative is to be calculated
	 * @param derivativeVariateIndex Index of the Variate whose Derivative is to be computed
	 * @param order Order of the derivative to be computed
	 * 
	 * @return The Derivative
	 */

	public Differential differential (
		final double[] variateArray,
		final int derivativeVariateIndex,
		final int order)
	{
		if (!NumberUtil.IsValid (variateArray) || variateArray.length <= derivativeVariateIndex ||
			0 >= order)
		{
			return null;
		}

		double derivative = 0.;
		double orderedVariateInfinitesimal = 1.;
		double variateInfinitesimal = Double.NaN;

		try {
			variateInfinitesimal =
				_derivativeControl.getVariateInfinitesimal (variateArray[derivativeVariateIndex]);
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}

		for (int orderOrdinal = 0; orderOrdinal <= order; ++orderOrdinal) {
			if (0 != orderOrdinal) {
				orderedVariateInfinitesimal *= (2. * variateInfinitesimal);
			}

			double[] incrementalArray = new double[variateArray.length];

			for (int variateIndex = 0; variateIndex < variateArray.length; ++variateIndex)
				incrementalArray[variateIndex] = variateIndex == derivativeVariateIndex ?
					variateArray[variateIndex] + variateInfinitesimal * (order - 2. * orderOrdinal) :
					variateArray[variateIndex];

			try {
				derivative += (orderOrdinal % 2 == 0 ? 1 : -1) * NumberUtil.NCK (order, orderOrdinal) *
					evaluate (incrementalArray);
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
	 * Calculate the Derivative as a Double
	 * 
	 * @param variateArray Variate Array at which the derivative is to be calculated
	 * @param derivativeVariateIndex Index of the Variate whose Derivative is to be computed
	 * @param order Order of the derivative to be computed
	 * 
	 * @return The Derivative
	 * 
	 * @throws Exception Thrown if the Derivative cannot be calculated
	 */

	public double derivative (
		final double[] variateArray,
		final int derivativeVariateIndex,
		final int order)
		throws Exception
	{
		return differential (variateArray, derivativeVariateIndex, order).calcSlope (true);
	}

	/**
	 * Evaluate the Jacobian for the given Input Variates
	 * 
	 * @param variateArray Array of Input Variates
	 *  
	 * @return The Jacobian Array
	 */

	public double[] jacobian (
		final double[] variateArray)
	{
		if (null == variateArray || 0 == variateArray.length) {
			return null;
		}

		double[] jacobianArray = new double[variateArray.length];

		for (int variateIndex = 0; variateIndex < variateArray.length; ++variateIndex) {
			try {
				jacobianArray[variateIndex] = derivative (variateArray, variateIndex, 1);
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		return jacobianArray;
	}

	/**
	 * Construct an Instance of the Unit Gradient Vector at the given Input Variates
	 * 
	 * @param variateArray Array of Input Variates
	 *  
	 * @return Instance of the Unit Gradient Vector Array
	 */

	public UnitVector gradient (
		final double[] variateArray)
	{
		return UnitVector.Standard (jacobian (variateArray));
	}

	/**
	 * Evaluate The Hessian for the given Input Variates
	 * 
	 * @param variateArray Array of Input Variates
	 *  
	 * @return The Hessian Matrix
	 */

	public double[][] hessian (
		final double[] variateArray)
	{
		if (null == variateArray || 0 == variateArray.length) {
			return null;
		}

		double[][] hessian = new double[variateArray.length][variateArray.length];

		for (int variateIndexI = 0; variateIndexI < variateArray.length; ++variateIndexI) {
			final int derivativeVariateIndex = variateIndexI;

			RdToR1 gradientRdToR1 = new RdToR1 (null)
			{
				@Override public int dimension()
				{
					return variateArray.length;
				}

				@Override public double evaluate (
					final double[] variateArray)
					throws Exception
				{
					return derivative (variateArray, derivativeVariateIndex, 1);
				}
			};

			for (int variateIndexJ = 0; variateIndexJ < variateArray.length; ++variateIndexJ) {
				try {
					hessian[variateIndexI][variateIndexJ] =
						gradientRdToR1.derivative (variateArray, variateIndexJ, 1);
				} catch (Exception e) {
					e.printStackTrace();

					return null;
				}
			}
		}

		return hessian;
	}

	/**
	 * Integrate over the given Input Range Using Uniform Monte-Carlo
	 * 
	 * @param leftEdgeArray Array of Input Left Edge
	 * @param rightEdgeArray Array of Input Right Edge
	 *  
	 * @return The Result of the Integration over the specified Range
	 * 
	 * @throws Exception Thrown if the Integration cannot be done
	 */

	public double integrate (
		final double[] leftEdgeArray,
		final double[] rightEdgeArray)
		throws Exception
	{
		if (null == leftEdgeArray || !NumberUtil.IsValid (leftEdgeArray) ||
			null == rightEdgeArray || !NumberUtil.IsValid (rightEdgeArray))
		{
			throw new Exception ("RdToR1::integrate => Invalid Inputs");
		}

		double integrand = 0.;
		double[] variateArray = new double[leftEdgeArray.length];
		double[] variateArrayWidth = new double[leftEdgeArray.length];

		if (rightEdgeArray.length != leftEdgeArray.length) {
			throw new Exception ("RdToR1::integrate => Invalid Inputs");
		}

		for (int arrayIndex = 0; arrayIndex < leftEdgeArray.length; ++arrayIndex) {
			variateArrayWidth[arrayIndex] = rightEdgeArray[arrayIndex] - leftEdgeArray[arrayIndex];
		}

		for (int sampleIndex = 0; sampleIndex < QUADRATURE_SAMPLING; ++sampleIndex) {
			for (int arrayIndex = 0; arrayIndex < leftEdgeArray.length; ++arrayIndex) {
				variateArray[arrayIndex] =
					leftEdgeArray[arrayIndex] + Math.random() * variateArrayWidth[arrayIndex];
			}

			integrand += evaluate (variateArray);
		}

		for (int arrayIndex = 0; arrayIndex < leftEdgeArray.length; ++arrayIndex) {
			integrand = integrand * variateArrayWidth[arrayIndex];
		}

		return integrand / QUADRATURE_SAMPLING;
	}

	/**
	 * Compute the Maximum <i>VariateOutputPair</i> within the Variate Array Range Using Uniform Monte-Carlo
	 * 
	 * @param leftVariateArray The Range Left End Array
	 * @param rightVariateArray The Range Right End Array
	 * 
	 * @return The Maximum <i>VariateOutputPair</i>
	 */

	public VariateOutputPair maxima (
		final double[] leftVariateArray,
		final double[] rightVariateArray)
	{
		if (null == leftVariateArray || !NumberUtil.IsValid (leftVariateArray) ||
			null == rightVariateArray || !NumberUtil.IsValid (rightVariateArray) ||
			rightVariateArray.length != leftVariateArray.length)
		{
			return null;
		}

		double value = Double.NaN;
		double maxima = Double.NaN;
		double[] variateArray = new double[leftVariateArray.length];
		double[] variateArrayWidth = new double[leftVariateArray.length];
		double[] maximaVariateArray = new double[leftVariateArray.length];

		for (int variateIndex = 0; variateIndex < leftVariateArray.length; ++variateIndex) {
			variateArrayWidth[variateIndex] =
				rightVariateArray[variateIndex] - leftVariateArray[variateIndex];
		}

		for (int samplingIndex = 0; samplingIndex < EXTREMA_SAMPLING; ++samplingIndex) {
			for (int variateIndex = 0; variateIndex < leftVariateArray.length; ++variateIndex) {
				variateArray[variateIndex] =
					leftVariateArray[variateIndex] + Math.random() * variateArrayWidth[variateIndex];
			}

			try {
				value = evaluate (variateArray);
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}

			if (!NumberUtil.IsValid (maxima)) {
				maxima = value;

				for (int variateIndex = 0; variateIndex < leftVariateArray.length; ++variateIndex) {
					maximaVariateArray[variateIndex] = variateArray[variateIndex];
				}
			} else {
				if (maxima < value) {
					maxima = value;

					for (int variateIndex = 0; variateIndex < leftVariateArray.length; ++variateIndex) {
						maximaVariateArray[variateIndex] = variateArray[variateIndex];
					}
				}
			}
		}

		try {
			return new VariateOutputPair (
				maximaVariateArray,
				new double[]
				{
					maxima
				}
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Compute the Minimum <i>VariateOutputPair</i> within the Variate Array Range Using Uniform Monte-Carlo
	 * 
	 * @param leftVariateArray The Range Left End Array
	 * @param rightVariateArray The Range Right End Array
	 * 
	 * @return The Minimum <i>VariateOutputPair</i>
	 */

	public VariateOutputPair minima (
		final double[] leftVariateArray,
		final double[] rightVariateArray)
	{
		if (null == leftVariateArray || !NumberUtil.IsValid (leftVariateArray) ||
			null == rightVariateArray || !NumberUtil.IsValid (rightVariateArray) ||
			rightVariateArray.length != leftVariateArray.length)
		{
			return null;
		}

		double value = Double.NaN;
		double minima = Double.NaN;
		double[] variateArray = new double[leftVariateArray.length];
		double[] variateArrayWidth = new double[leftVariateArray.length];
		double[] minimaVariateArray = new double[leftVariateArray.length];

		for (int variateIndex = 0; variateIndex < leftVariateArray.length; ++variateIndex) {
			variateArrayWidth[variateIndex] =
				rightVariateArray[variateIndex] - leftVariateArray[variateIndex];
		}

		for (int samplingIndex = 0; samplingIndex < EXTREMA_SAMPLING; ++samplingIndex) {
			for (int variateIndex = 0; variateIndex < leftVariateArray.length; ++variateIndex) {
				variateArray[variateIndex] =
					leftVariateArray[variateIndex] + Math.random() * variateArrayWidth[variateIndex];
			}

			try {
				value = evaluate (variateArray);
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}

			if (!NumberUtil.IsValid (minima)) {
				minima = value;

				for (int variateIndex = 0; variateIndex < leftVariateArray.length; ++variateIndex) {
					minimaVariateArray[variateIndex] = variateArray[variateIndex];
				}
			} else {
				if (minima > value) {
					minima = value;

					for (int variateIndex = 0; variateIndex < leftVariateArray.length; ++variateIndex) {
						minimaVariateArray[variateIndex] = variateArray[variateIndex];
					}
				}
			}
		}

		try {
			return new VariateOutputPair (
				minimaVariateArray,
				new double[]
				{
					minima
				}
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Compute the Modulus of the Gradient at the Specified Variate
	 * 
	 * @param variateArray The Variate Array
	 * 
	 * @return The Modulus of the Gradient at the Specified Variate
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public double gradientModulus (
		final double[] variateArray)
		throws Exception
	{
		double[] jacobian = jacobian (variateArray);

		if (null == jacobian) {
			throw new Exception ("RdToR1::gradientModulus => Invalid Inputs!");
		}

		double gradientModulus = 0.;

		for (int variateIndex = 0; variateIndex < variateArray.length; ++variateIndex) {
			gradientModulus += jacobian[variateIndex] * jacobian[variateIndex];
		}

		return gradientModulus;
	}

	/**
	 * Generate the Gradient Modulus Function
	 * 
	 * @return The Gradient Modulus Function
	 */

	public RdToR1 gradientModulusFunction()
	{
		return new RdToR1 (null)
		{
			@Override public int dimension()
			{
				return dimension();
			}

			@Override public double evaluate (
				final double[] variateArray)
				throws Exception
			{
				return gradientModulus (variateArray);
			}

			@Override public double[] jacobian (
				final double[] variateArray)
			{
				double[] parentJacobian = jacobian (variateArray);

				double[][] parentHessian = hessian (variateArray);

				if (null == parentJacobian || null == parentHessian) {
					return null;
				}

				double[] gradientModulusJacobian = new double[parentJacobian.length];

				for (int indexI = 0; indexI < parentJacobian.length; ++indexI) {
					gradientModulusJacobian[indexI] = 0.;

					for (int indexJ = 0; indexJ < parentJacobian.length; ++indexJ) {
						gradientModulusJacobian[indexI] +=
							parentJacobian[indexJ] * parentHessian[indexJ][indexI];
					}

					gradientModulusJacobian[indexI] *= 2.;
				}

				return gradientModulusJacobian;
			}
		};
	}

	/**
	 * Retrieve the L<sub>2</sub> Condition Number for the Function at the Domain Value
	 * 
	 * @param variateArray Domain Variate Array
	 * 
	 * @return Condition Number for the Function at the Domain Value
	 * 
	 * @throws Exception Thrown if the Condition Number for the Function at the Domain Value cannot be
	 * 		   			 calculated
	 */

	public double conditionNumberL2 (
		final double[] variateArray)
		throws Exception
	{
		return Math.abs (
			normedJacobianL2 (variateArray) * normedVariateL2 (variateArray) / evaluate (variateArray)
		);
	}

	/**
	 * Retrieve the L<sub>p</sub> Condition Number for the Function at the Domain Value
	 * 
	 * @param variateArray Domain Variate Array
	 * @param p p-Norm
	 * 
	 * @return Condition Number for the Function at the Domain Value
	 * 
	 * @throws Exception Thrown if the Condition Number for the Function at the Domain Value cannot be
	 * 					 calculated
	 */

	public double conditionNumberLp (
		final double[] variateArray,
		final int p)
		throws Exception
	{
		return Math.abs (
			normedJacobianLp (variateArray, p) * normedVariateLp (variateArray, p) / evaluate (variateArray)
		);
	}

	/**
	 * Retrieve the L<sub>Infinity</sub> Condition Number for the Function at the Domain Value
	 * 
	 * @param variateArray Domain Variate Array
	 * 
	 * @return Condition Number for the Function at the Domain Value
	 * 
	 * @throws Exception Thrown if the Condition Number for the Function at the Domain Value cannot be
	 * 					 calculated
	 */

	public double conditionNumberLInfinity (
		final double[] variateArray)
		throws Exception
	{
		return Math.abs (
			normedJacobianLInfinity (variateArray) * normedVariateLInfinity (variateArray) /
				evaluate (variateArray)
		);
	}

	/**
	 * Retrieve the Default Condition Number for the Function at the Domain Value - this uses L<sub>2</sub>
	 * 	Metric
	 * 
	 * @param variateArray Domain Variate Array
	 * 
	 * @return Condition Number for the Function at the Domain Value
	 * 
	 * @throws Exception Thrown if the Condition Number for the Function at the Domain Value cannot be
	 * 					 calculated
	 */

	public double conditionNumber (
		final double[] variateArray)
		throws Exception
	{
		return conditionNumberL2 (variateArray);
	}
}
