
package org.drip.regression.nonlinear;

import org.drip.function.definition.RdToR1;
import org.drip.numerical.linearalgebra.R1MatrixUtil;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2030 Lakshmi Krishnamurthy
 * Copyright (C) 2029 Lakshmi Krishnamurthy
 * Copyright (C) 2028 Lakshmi Krishnamurthy
 * Copyright (C) 2027 Lakshmi Krishnamurthy
 * Copyright (C) 2026 Lakshmi Krishnamurthy
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
 * <i>R1ToR1EnsembleResidualSquared</i> holds the Squared Residuals corresponding to an Ensemble of (x, y)
 *  Pairs for a given Parameterized Objective Function. The References are:
 *
 *  <br><br>
 *  <ul>
 *  	<li>
 *  		Bjorck, A. (1996): <i>Numerical Methods for Least Squares Problems</i> <b>SIAM</b> Philadelphia
 *  			PA
 *  	</li>
 *  	<li>
 *  		Dennis, J. E., and R. B. Schnabel (1983): <i>Numerical Methods for Unconstrained Optimization</i>
 *  			<b>Prentice-Hall</b> Hoboken NJ
 *  	</li>
 *  	<li>
 *  		Mascarenhas, W. F. (2013): The Divergence of the BGFS and the Gauss Newton Methods
 *  			<i>Mathematical Programming</i> <b>147 (1)</b> 253-276
 *  	</li>
 *  	<li>
 *  		Nocedal, J., and S. Wright (1999): <i>Numerical Optimization</i> <b>Springer</b> New York NY
 *  	</li>
 *  	<li>
 *  		Wikipedia (2025): Gauss-Newton Method
 *  			https://en.wikipedia.org/wiki/Gauss%E2%80%93Newton_algorithm
 *  	</li>
 *  </ul>
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationSupportLibrary.md">Computation Support</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/regression/README.md">Regression Engine Core and the Unit Regressors</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/regression/nonlinear/README.md">Non-linear Least Squares Regression</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class R1ToR1EnsembleResidualSquared
	extends RdToR1
{
	private R1ToR1Residual[] _r1ToR1ResidualArray = null;

	/**
	 * <i>R1ToR1EnsembleResidualSquared</i> Constructor
	 * 
	 * @param r1ToR1ResidualArray Array of <i>R1ToR1Residual<i>'s
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public R1ToR1EnsembleResidualSquared (
		final R1ToR1Residual[] r1ToR1ResidualArray)
		throws Exception
	{
		super (null);

		if (null == (_r1ToR1ResidualArray = r1ToR1ResidualArray) || 0 == _r1ToR1ResidualArray.length) {
			throw new Exception ("R1ToR1EnsembleResidualSquared Constructor => Invalid Inputs");
		}

		for (int sampleIndex = 0; sampleIndex < _r1ToR1ResidualArray.length; ++sampleIndex) {
			if (null == _r1ToR1ResidualArray[sampleIndex]) {
				throw new Exception ("R1ToR1EnsembleResidualSquared Constructor => Invalid Inputs");
			}
		}
	}

	/**
	 * Retrieve the Array of <i>R1ToR1Residual<i>'s
	 * 
	 * @return Array of <i>R1ToR1Residual<i>'s
	 */

	public R1ToR1Residual[] r1ToR1ResidualArray()
	{
		return _r1ToR1ResidualArray;
	}

	/**
	 * Retrieve the Array of Residual Values
	 * 
	 * @param parameterArray Parameter Array
	 * 
	 * @return Array of Residual Values
	 */

	public double[] residualValueArray (
		final double[] parameterArray)
	{
		double[] residualValueArray = new double[_r1ToR1ResidualArray.length];

		for (int sampleIndex = 0; sampleIndex < _r1ToR1ResidualArray.length; ++sampleIndex) {
			try {
				residualValueArray[sampleIndex] = _r1ToR1ResidualArray[sampleIndex].value (parameterArray);
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		return residualValueArray;
	}

	/**
	 * Evaluate the Hessian Correction for the given Input Variates
	 * 
	 * @param parameterArray Array of Parameters
	 *  
	 * @return The Hessian Correction
	 */

	public double[][] hessianCorrection (
		final double[] parameterArray)
	{
		int dimension = dimension();

		double[][] hessianCorrection = new double[dimension][dimension];

		for (int parameterI = 0; parameterI < dimension; ++parameterI) {
			for (int parameterJ = 0; parameterJ < dimension; ++parameterJ) {
				hessianCorrection[parameterI][parameterJ] = 0.;
			}
		}

		for (int sampleIndex = 0; sampleIndex < _r1ToR1ResidualArray.length; ++sampleIndex) {
			double residualValue = Double.NaN;

			try {
				residualValue = _r1ToR1ResidualArray[sampleIndex].value (parameterArray);
			} catch (Exception e) {
				return null;
			}

			double[][] residualHessian = _r1ToR1ResidualArray[sampleIndex].hessian (parameterArray);

			if (null == residualHessian) {
				return null;
			}

			for (int parameterI = 0; parameterI < dimension; ++parameterI) {
				for (int parameterJ = 0; parameterJ < dimension; ++parameterJ) {
					hessianCorrection[parameterI][parameterJ] = 2. * (
						residualValue * residualHessian[parameterI][parameterJ]
					);
				}
			}
		}

		return hessianCorrection;
	}

	/**
	 * Compute the Sample Residual Jacobian Array
	 * 
	 * @param parameterArray Array of Parameters
	 * 
	 * @return Sample Residual Jacobian Array
	 */

	public double[][] sampleResidualJacobianArray (
		final double[] parameterArray)
	{
		double[][] sampleResidualJacobianArray = new double[_r1ToR1ResidualArray.length][];

		for (int sampleIndex = 0; sampleIndex < _r1ToR1ResidualArray.length; ++sampleIndex) {
			if (null == (
				sampleResidualJacobianArray[sampleIndex] =
					_r1ToR1ResidualArray[sampleIndex].jacobian (parameterArray)
			))
			{
				return null;
			}
		}

		return sampleResidualJacobianArray;
	}

	/**
	 * Compute <i>SampleResidualJacobian</i> Instance
	 * 
	 * @param parameterArray Array of Parameters
	 * 
	 * @return <i>SampleResidualJacobian</i> Instance
	 */

	public SampleResidualJacobian sampleResidualJacobian (
		final double[] parameterArray)
	{
		try {
			return new SampleResidualJacobian (sampleResidualJacobianArray (parameterArray));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Compute the Sum-of-Squares Sample Hessian Array
	 * 
	 * @param parameterArray Array of Parameters
	 * 
	 * @return Sum-of-Squares Sample Hessian Array
	 */

	public double[][][] hessianArray (
		final double[] parameterArray)
	{
		double[][][] hessianArray = new double[_r1ToR1ResidualArray.length][][];

		for (int sampleIndex = 0; sampleIndex < _r1ToR1ResidualArray.length; ++sampleIndex) {
			double doubleResidualValue = Double.NaN;

			try {
				doubleResidualValue = 2. * _r1ToR1ResidualArray[sampleIndex].value (parameterArray);
			} catch (Exception e) {
				return null;
			}

			double[][] sampleHessian = _r1ToR1ResidualArray[sampleIndex].hessian (parameterArray);

			if (null == sampleHessian) {
				return null;
			}

			for (double[] sampleHessianRow : sampleHessian) {
				for (int rowIndex = 0; rowIndex < sampleHessianRow.length; ++rowIndex) {
					sampleHessianRow[rowIndex] *= doubleResidualValue;
				}
			}

			hessianArray[sampleIndex] = sampleHessian;
		}

		return hessianArray;
	}

	/**
	 * Retrieve the Transpose of the Array of Sample Residual Jacobians
	 * 
	 * @param parameterArray Array of Parameters
	 * 
	 * @return The Transpose of the Array of Sample Residual Jacobians
	 */

	public double[][] sampleResidualJacobianTranspose (
		final double[] parameterArray)
	{
		return R1MatrixUtil.Transpose (sampleResidualJacobianArray (parameterArray));
	}

	/**
	 * Evaluate the Hessian Proxy for the given Input Variates
	 * 
	 * @param parameterArray Array of Parameters
	 *  
	 * @return The Hessian Proxy
	 */

	public double[][] hessianProxy (
		final double[] parameterArray)
	{
		double[][] sampleResidualJacobianArray = sampleResidualJacobianArray (parameterArray);

		return R1MatrixUtil.Scale2D (
			R1MatrixUtil.Product (
				R1MatrixUtil.Transpose (sampleResidualJacobianArray),
				sampleResidualJacobianArray
			),
			2.
		);
	}

	/**
	 * Return the Dimension of the Parameter Array
	 * 
	 * @return Dimension of the Parameter Array
	 */

	@Override public int dimension()
	{
		return _r1ToR1ResidualArray[0].parametricFunction().dimension();
	}

	/**
	 * Retrieve the Sum of Residual Squares
	 * 
	 * @param parameterArray Array of Parameters
	 * 
	 * @return Sum of Residual Squares
	 * 
	 * @throws Exception Thrown if the Residual cannot be calculated
	 */

	@Override public double evaluate (
		final double[] parameterArray)
		throws Exception
	{
		double sumOfSquares = 0.;

		for (int i = 0; i < _r1ToR1ResidualArray.length; ++i) {
			double residualValue = _r1ToR1ResidualArray[i].value (parameterArray);

			sumOfSquares += residualValue * residualValue;
		}

		return sumOfSquares;
	}

	/**
	 * Evaluate the Jacobian for the given Parameter Array
	 * 
	 * @param parameterArray Array of Parameters
	 *  
	 * @return The Jacobian
	 */

	@Override public double[] jacobian (
		final double[] parameterArray)
	{
		double[] jacobian = new double[dimension()];

		for (int parameterIndex = 0; parameterIndex < jacobian.length; ++parameterIndex) {
			jacobian[parameterIndex] = 0.;
		}

		for (int sampleIndex = 0; sampleIndex < _r1ToR1ResidualArray.length; ++sampleIndex) {
			double residualValue = Double.NaN;

			try {
				residualValue = _r1ToR1ResidualArray[sampleIndex].value (parameterArray);
			} catch (Exception e) {
				return null;
			}

			double[] residualJacobian = _r1ToR1ResidualArray[sampleIndex].jacobian (parameterArray);

			if (null == residualJacobian) {
				return null;
			}

			for (int parameterIndex = 0; parameterIndex < residualJacobian.length; ++parameterIndex) {
				jacobian[parameterIndex] += 2. * residualValue * residualJacobian[parameterIndex];
			}
		}

		return jacobian;
	}

	/**
	 * Evaluate the Hessian for the given Input Variates
	 * 
	 * @param parameterArray Array of Parameters
	 *  
	 * @return The Hessian
	 */

	@Override public double[][] hessian (
		final double[] parameterArray)
	{
		int dimension = dimension();

		double[][] hessian = new double[dimension][dimension];

		for (int parameterI = 0; parameterI < dimension; ++parameterI) {
			for (int parameterJ = 0; parameterJ < dimension; ++parameterJ) {
				hessian[parameterI][parameterJ] = 0.;
			}
		}

		for (int sampleIndex = 0; sampleIndex < _r1ToR1ResidualArray.length; ++sampleIndex) {
			double residualValue = Double.NaN;

			try {
				residualValue = _r1ToR1ResidualArray[sampleIndex].value (parameterArray);
			} catch (Exception e) {
				return null;
			}

			double[] residualJacobian = _r1ToR1ResidualArray[sampleIndex].jacobian (parameterArray);

			if (null == residualJacobian) {
				return null;
			}

			double[][] residualHessian = _r1ToR1ResidualArray[sampleIndex].hessian (parameterArray);

			if (null == residualHessian) {
				return null;
			}

			for (int parameterI = 0; parameterI < dimension; ++parameterI) {
				for (int parameterJ = 0; parameterJ < dimension; ++parameterJ) {
					hessian[parameterI][parameterJ] += 2. * (
						residualJacobian[parameterI] * residualJacobian[parameterJ] +
							residualValue * residualHessian[parameterI][parameterJ]
					);
				}
			}
		}

		return hessian;
	}
}
