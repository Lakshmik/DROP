
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
 * <i>CovarianceEllipsoidMultivariate</i> implements a R<sup>d</sup> To R<sup>1</sup> Co-variance Estimate of
 * 	the specified Distribution. It exposes the following Functions:
 *
 *  <ul>
 * 		<li><i>CovarianceEllipsoidMultivariate</i> Constructor</li>
 * 		<li>Retrieve the Input Variate Dimension</li>
 * 		<li>Retrieve the Co-variance Matrix</li>
 * 		<li>Evaluate for the given Input Variate Array</li>
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

public class CovarianceEllipsoidMultivariate
	extends RdToR1
{
	private double[][] _covarianceMatrix = null;

	/**
	 * <i>CovarianceEllipsoidMultivariate</i> Constructor
	 * 
	 * @param covarianceMatrix The Covariance Matrix
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public CovarianceEllipsoidMultivariate (
		final double[][] covarianceMatrix)
		throws Exception
	{
		super (null);

		if (null == (_covarianceMatrix = covarianceMatrix)) {
			throw new Exception ("CovarianceEllipsoidMultivariate Constructor => Invalid Inputs");
		}

		if (0 == _covarianceMatrix.length) {
			throw new Exception ("CovarianceEllipsoidMultivariate Constructor => Invalid Inputs");
		}

		for (int rowIndex = 0; rowIndex < _covarianceMatrix.length; ++rowIndex) {
			if (null == _covarianceMatrix[rowIndex] ||
				_covarianceMatrix.length != _covarianceMatrix[rowIndex].length ||
				!NumberUtil.IsValid (_covarianceMatrix[rowIndex]))
			{
				throw new Exception ("CovarianceEllipsoidMultivariate Constructor => Invalid Inputs");
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
	 * Evaluate for the given Input Variate Array
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
			throw new Exception ("CovarianceEllipsoidMultivariate::evaluate => Invalid Inputs");
		}

		double covariance = 0.;
		int dimension = variateArray.length;

		if (dimension != dimension()) {
			throw new Exception ("CovarianceEllipsoidMultivariate::evaluate => Invalid Inputs");
		}

		for (int innerDimensionIndex = 0; innerDimensionIndex < dimension; ++innerDimensionIndex) {
			for (int outerDimensionIndex = 0; outerDimensionIndex < dimension; ++outerDimensionIndex) {
				covariance +=
					variateArray[innerDimensionIndex] *
						_covarianceMatrix[innerDimensionIndex][outerDimensionIndex] *
						variateArray[outerDimensionIndex];
			}
		}

		return covariance;
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

		double[] jacobianArray = new double[variateArray.length];

		if (variateArray.length != dimension()) {
			return null;
		}

		for (int innerDimensionIndex = 0; innerDimensionIndex < variateArray.length; ++innerDimensionIndex) {
			jacobianArray[innerDimensionIndex] = 0.;

			for (int outerDimensionIndex = 0;
				outerDimensionIndex < variateArray.length;
				++outerDimensionIndex)
			{
				jacobianArray[innerDimensionIndex] +=
					2. * _covarianceMatrix[innerDimensionIndex][outerDimensionIndex] *
						variateArray[outerDimensionIndex];
			}
		}

		return jacobianArray;
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

		double[][] hessianGrid = new double[dimension][dimension];

		for (int innerDimensionIndex = 0; innerDimensionIndex < dimension; ++innerDimensionIndex) {
			for (int outerDimensionIndex = 0; outerDimensionIndex < dimension; ++outerDimensionIndex) {
				hessianGrid[innerDimensionIndex][outerDimensionIndex] +=
					2. * _covarianceMatrix[innerDimensionIndex][outerDimensionIndex];
			}
		}

		return hessianGrid;
	}
}
