
package org.drip.function.rdtor1;

import org.drip.function.definition.RdToR1;
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
 * <i>RiskObjectiveUtilityMultivariate</i> implements the Risk Objective R<sup>d</sup> To R<sup>1</sup>
 * 	Multivariate Function used in Portfolio Allocation. It accommodates both the Risk Tolerance and Risk
 * 	Aversion Variants. It exposes the following Functions:
 *
 *  <ul>
 * 		<li><i>RiskObjectiveUtilityMultivariate</i> Constructor</li>
 * 		<li>Retrieve the Input Variate Dimension</li>
 * 		<li>Retrieve the Co-variance Matrix</li>
 * 		<li>Retrieve the Array of Expected Returns</li>
 * 		<li>Retrieve the Risk Aversion Factor</li>
 * 		<li>Retrieve the Risk Tolerance Factor</li>
 * 		<li>Retrieve the Risk Free Rate</li>
 * 		<li>Evaluate for the given Input Variates</li>
 * 		<li>Evaluate the Jacobian for the given Input Variate Array</li>
 * 		<li>Evaluate The Hessian for the given Input Variate Array</li>
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

public class RiskObjectiveUtilityMultivariate
	extends RdToR1
{
	private double _riskAversion = Double.NaN;
	private double _riskFreeRate = Double.NaN;
	private double _riskTolerance = Double.NaN;
	private double[][] _covarianceMatrix = null;
	private double[] _expectedReturnsArray = null;

	/**
	 * <i>RiskObjectiveUtilityMultivariate</i> Constructor
	 * 
	 * @param covarianceMatrix The Co-variance Matrix Double Array
	 * @param expectedReturnsArray Array of Expected Returns
	 * @param riskAversion The Risk Aversion Parameter
	 * @param riskTolerance The Risk Tolerance Parameter
	 * @param riskFreeRate The Risk Free Rate
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public RiskObjectiveUtilityMultivariate (
		final double[][] covarianceMatrix,
		final double[] expectedReturnsArray,
		final double riskAversion,
		final double riskTolerance,
		final double riskFreeRate)
		throws Exception
	{
		super (null);

		if (null == (_covarianceMatrix = covarianceMatrix) ||
			null == (_expectedReturnsArray = expectedReturnsArray) ||
			!NumberUtil.IsValid (_riskAversion = riskAversion) ||
			!NumberUtil.IsValid (_riskTolerance = riskTolerance) ||
			!NumberUtil.IsValid (_riskFreeRate = riskFreeRate))
		{
			throw new Exception ("RiskObjectiveUtilityMultivariate Constructor => Invalid Inputs");
		}

		if (0 == _covarianceMatrix.length || _covarianceMatrix.length != _expectedReturnsArray.length) {
			throw new Exception ("RiskObjectiveUtilityMultivariate Constructor => Invalid Inputs");
		}

		for (int index = 0; index < _covarianceMatrix.length; ++index) {
			if (null == _covarianceMatrix[index] ||
				_covarianceMatrix.length != _covarianceMatrix[index].length ||
				!NumberUtil.IsValid (_covarianceMatrix[index]) ||
				!NumberUtil.IsValid (_expectedReturnsArray[index]))
			{
				throw new Exception ("RiskObjectiveUtilityMultivariate Constructor => Invalid Inputs");
			}
		}
	}

	/**
	 * Retrieve the Input Variate Dimension
	 * 
	 * @return The Input Variate Dimension
	 */

	public int dimension()
	{
		return _covarianceMatrix.length;
	}

	/**
	 * Retrieve the Co-variance Matrix
	 * 
	 * @return The Co-variance Matrix
	 */

	public double[][] covarianceMatrix()
	{
		return _covarianceMatrix;
	}

	/**
	 * Retrieve the Array of Expected Returns
	 * 
	 * @return The Array of Expected Returns
	 */

	public double[] expectedReturns()
	{
		return _expectedReturnsArray;
	}

	/**
	 * Retrieve the Risk Aversion Factor
	 * 
	 * @return The Risk Aversion Factor
	 */

	public double riskAversion()
	{
		return _riskAversion;
	}

	/**
	 * Retrieve the Risk Tolerance Factor
	 * 
	 * @return The Risk Tolerance Factor
	 */

	public double riskTolerance()
	{
		return _riskTolerance;
	}

	/**
	 * Retrieve the Risk Free Rate
	 * 
	 * @return The Risk Free Rate
	 */

	public double riskFreeRate()
	{
		return _riskFreeRate;
	}

	/**
	 * Evaluate for the given Input Variates
	 * 
	 * @param variateArray Array of Input Variates
	 *  
	 * @return The Calculated Value
	 * 
	 * @throws Exception Thrown if the Evaluation cannot be done
	 */

	@Override public double evaluate (
		final double[] variateArray)
		throws Exception
	{
		if (null == variateArray || !NumberUtil.IsValid (variateArray)) {
			throw new Exception ("RiskObjectiveUtilityMultivariate::evaluate => Invalid Inputs");
		}

		double value = 0.;

		if (variateArray.length != dimension()) {
			throw new Exception ("RiskObjectiveUtilityMultivariate::evaluate => Invalid Inputs");
		}

		for (int variateIndexI = 0; variateIndexI < variateArray.length; ++variateIndexI) {
			value -=
				_riskTolerance * variateArray[variateIndexI] *
				(_expectedReturnsArray[variateIndexI] - _riskFreeRate);

			for (int variateIndexJ = 0; variateIndexJ < variateArray.length; ++variateIndexJ) {
				value +=
					0.5 * _riskAversion * variateArray[variateIndexI] *
					_covarianceMatrix[variateIndexI][variateIndexJ] * variateArray[variateIndexJ];
			}
		}

		return value;
	}

	/**
	 * Evaluate the Jacobian for the given Input Variate Array
	 * 
	 * @param variateArray Array of Input Variate Array
	 *  
	 * @return The Jacobian Array
	 */

	@Override public double[] jacobian (
		final double[] variateArray)
	{
		if (null == variateArray || !NumberUtil.IsValid (variateArray)) {
			return null;
		}

		double[] jacobian = new double[variateArray.length];

		if (variateArray.length != dimension()) {
			return null;
		}

		for (int variateIndexI = 0; variateIndexI < variateArray.length; ++variateIndexI) {
			jacobian[variateIndexI] =
				-1. * _riskTolerance * (_expectedReturnsArray[variateIndexI] - _riskFreeRate);

			for (int variateIndexJ = 0; variateIndexJ < variateArray.length; ++variateIndexJ) {
				jacobian[variateIndexI] +=
					_riskAversion * _covarianceMatrix[variateIndexI][variateIndexJ] *
					variateArray[variateIndexJ];
			}
		}

		return jacobian;
	}

	/**
	 * Evaluate The Hessian for the given Input Variate Array
	 * 
	 * @param variateArray Array of Input Variate Array
	 *  
	 * @return The Hessian Matrix
	 */

	@Override public double[][] hessian (
		final double[] variateArray)
	{
		int dimension = dimension();

		double[][] hessian = new double[dimension][dimension];

		for (int variateIndexI = 0; variateIndexI < dimension; ++variateIndexI) {
			for (int variateIndexJ = 0; variateIndexJ < dimension; ++variateIndexJ) {
				hessian[variateIndexI][variateIndexJ] +=
					_riskAversion * _covarianceMatrix[variateIndexI][variateIndexJ];
			}
		}

		return hessian;
	}
}
