
package org.drip.regression.nonlinear;

import org.drip.numerical.linearalgebra.R1MatrixUtil;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
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
 * <i>GaussNewton</i> implements the Non-linear Least-Squares Regression using the Gauss-Newton Algorithm.
 * 	The References are:
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
 *  <br><br>
 *  <ul>
 *		<li><b>Module </b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></li>
 *		<li><b>Library</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationSupportLibrary.md">Computation Support</a></li>
 *		<li><b>Project</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/regression/README.md">Regression Engine Core and the Unit Regressors</a></li>
 *		<li><b>Package</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/regression/nonlinear/README.md">Non-linear Least Squares Regression</a></li>
 *  </ul>
 * <br><br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class GaussNewton
{
	private static final double ABSOLUTE_CONVERGENCE_RATIO = 1.0e-07;
	private static final double RELATIVE_CONVERGENCE_RATIO = 1.0e-05;

	private boolean _diagnosticsOn = false;
	private R1ToR1Residual[] _residualArray = null;

	/**
	 * Compute the Sum of the Residual Squares
	 * 
	 * @param residualValueArray Array of Residual Values
	 * 
	 * @return Sum of the Residual Squares
	 */

	public static final double SumOfResidualSquares (
		final double[] residualValueArray)
	{
		double sumOfResidualSquares = 0.;

		for (double residualValue : residualValueArray) {
			sumOfResidualSquares += residualValue * residualValue;
		}

		return sumOfResidualSquares;
	}

	private double[] parameterDeltaArray (
		final double[] parameterArray,
		final double[] residualValueArray)
	{
		double[][] j = j (parameterArray);

		if (null == j) {
			return null;
		}

		double[][] jTranspose = R1MatrixUtil.Transpose (j);

		if (null == jTranspose) {
			return null;
		}

		double[][] jTransposeJ = R1MatrixUtil.Product (jTranspose, j);

		if (null == jTransposeJ) {
			return null;
		}

		double[] jTransposeR = R1MatrixUtil.Product (jTranspose, residualValueArray);

		if (null == jTransposeR) {
			return null;
		}

		for (int i = 0; i < jTransposeR.length; ++i) {
			jTransposeR[i] = -1. * jTransposeR[i];
		}

		return R1MatrixUtil.Product (R1MatrixUtil.InvertUsingGaussianElimination (jTransposeJ), jTransposeR);
	}

	/**
	 * <i>GaussNewton</i> Constructor
	 * 
	 * @param parametricFunction <i>R1ToR1Parametric</i> Instance
	 * @param sample <i>R1R1Sample</i> Instance
	 * @param diagnosticsOn TRUE - Diagnostics has been Turned On
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public GaussNewton (
		final R1ToR1Parametric parametricFunction,
		final R1R1Sample sample,
		final boolean diagnosticsOn)
		throws Exception
	{
		if (null == sample) {
			throw new Exception ("GaussNewton Constructor => Invalid Inputs");
		}

		double[] xArray = sample.xArray();

		double[] yArray = sample.yArray();

		_diagnosticsOn = diagnosticsOn;
		_residualArray = new R1ToR1Residual[xArray.length];

		for (int sampleIndex = 0; sampleIndex < xArray.length; ++sampleIndex) {
			_residualArray[sampleIndex] = new R1ToR1Residual (
				xArray[sampleIndex],
				yArray[sampleIndex],
				parametricFunction
			);
		}
	}

	/**
	 * Retrieve the Array of <i>R1ToR1Residual</i> Instances
	 * 
	 * @return Array of <i>R1ToR1Residual</i> Instances
	 */

	public R1ToR1Residual[] residualArray()
	{
		return _residualArray;
	}

	/**
	 * Indicate if Diagnostics has been Turned On
	 * 
	 * @return TRUE - Diagnostics has been Turned On
	 */

	public boolean diagnosticsOn()
	{
		return _diagnosticsOn;
	}

	/**
	 * Retrieve the Grid of Sample Parameter Jacobians
	 * 
	 * @param parameterArray Parameter Array
	 * 
	 * @return Grid of Sample Parameter Jacobians
	 */

	public double[][] j (
		final double[] parameterArray)
	{
		if (null == parameterArray || 0 == parameterArray.length) {
			return null;
		}

		double[][] j = new double[_residualArray.length][];

		for (int sampleIndex = 0; sampleIndex < _residualArray.length; ++sampleIndex) {
			if (null == (j[sampleIndex] = _residualArray[sampleIndex].jacobian (parameterArray))) {
				return null;
			}
		}

		return j;
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
		double[] residualValueArray = new double[_residualArray.length];

		for (int sampleIndex = 0; sampleIndex < _residualArray.length; ++sampleIndex) {
			try {
				residualValueArray[sampleIndex] = _residualArray[sampleIndex].value (parameterArray);
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		return residualValueArray;
	}

	/**
	 * Calibrate the Parameters using Least-Squares Method
	 * 
	 * @param parameterArray Initial Parameter Array
	 * 
	 * @return Parameters calibrated using Least-Squares Method
	 */

	public GaussNewtonRun leastSquaresRun (
		final double[] parameterArray)
	{
		double[] residualValueArray = residualValueArray (parameterArray);

		if (null == residualValueArray) {
			return null;
		}

		double currentSumOfResidualSquares = SumOfResidualSquares (residualValueArray);

		if (currentSumOfResidualSquares <= ABSOLUTE_CONVERGENCE_RATIO) {
			try {
				return GaussNewtonRun.Standard (parameterArray, currentSumOfResidualSquares);
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		int iterationIndex = 0;
		boolean terminateIteration = false;
		double newSumOfResidualSquares = 0.;
		double sumOfResidualSquaresChange = RELATIVE_CONVERGENCE_RATIO * currentSumOfResidualSquares;

		GaussNewtonRun run = _diagnosticsOn ? new GaussNewtonRunDiagnostics() : new GaussNewtonRun();

		while (!terminateIteration) {
			double[] parameterDeltaArray = parameterDeltaArray (parameterArray, residualValueArray);

			if (run instanceof GaussNewtonRunDiagnostics) {
				((GaussNewtonRunDiagnostics) run).setParameterDeltaArray (
					iterationIndex,
					parameterDeltaArray
				);
			}

			for (int i = 0; i < parameterDeltaArray.length; ++i) {
				parameterArray[i] += parameterDeltaArray[i];
			}

			if (run instanceof GaussNewtonRunDiagnostics) {
				((GaussNewtonRunDiagnostics) run).setParameterArray (iterationIndex, parameterArray);
			}

			if (null == (residualValueArray = residualValueArray (parameterArray))) {
				return null;
			}

			if (run instanceof GaussNewtonRunDiagnostics) {
				((GaussNewtonRunDiagnostics) run).setResidualValueArray (iterationIndex, residualValueArray);
			}

			newSumOfResidualSquares = SumOfResidualSquares (residualValueArray);

			terminateIteration = Math.abs (newSumOfResidualSquares - currentSumOfResidualSquares) <
				sumOfResidualSquaresChange;

			if (run instanceof GaussNewtonRunDiagnostics) {
				((GaussNewtonRunDiagnostics) run).setSumOfResidualSquares (
					iterationIndex,
					newSumOfResidualSquares
				);
			}

			currentSumOfResidualSquares = newSumOfResidualSquares;
			++iterationIndex;
		}

		return run.setParameterArray (parameterArray) &&
			run.setSumOfResidualSquares (currentSumOfResidualSquares) ? run : null;
	}
}
