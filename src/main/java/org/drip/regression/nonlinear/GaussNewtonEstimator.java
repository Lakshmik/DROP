
package org.drip.regression.nonlinear;

import org.drip.numerical.linearalgebra.R1MatrixUtil;
import org.drip.regression.function.R1ToR1Parametric;

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
 * <i>GaussNewtonEstimator</i> implements the Non-linear Least-Squares Regression using the Gauss-Newton
 * 	Algorithm. The References are:
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

public class GaussNewtonEstimator
	implements LeastSquaresEstimator
{
	private static final double ABSOLUTE_CONVERGENCE_RATIO = 1.0e-07;
	private static final double RELATIVE_CONVERGENCE_RATIO = 1.0e-05;

	private boolean _diagnosticsOn = false;
	private R1ToR1EnsembleResidualSquared _r1ToR1EnsembleResidualSquared = null;

	private double[] parameterDeltaArray (
		final LeastSquaresRun leastSquaresRun,
		final int iterationIndex,
		final double[] parameterArray,
		final double[] residualValueArray)
	{
		SampleResidualJacobian sampleResidualJacobian =
			_r1ToR1EnsembleResidualSquared.sampleResidualJacobian (parameterArray);

		if (null == sampleResidualJacobian) {
			return null;
		}

		if (leastSquaresRun instanceof LeastSquaresRunDiagnostics) {
			((LeastSquaresRunDiagnostics) leastSquaresRun).setHessianProxy (
				iterationIndex,
				sampleResidualJacobian.jTransposeJ()
			);

			((LeastSquaresRunDiagnostics) leastSquaresRun).setHessian (
				iterationIndex,
				_r1ToR1EnsembleResidualSquared.hessian (parameterArray)
			);

			((LeastSquaresRunDiagnostics) leastSquaresRun).setHessianCorrection (
				iterationIndex,
				_r1ToR1EnsembleResidualSquared.hessianCorrection (parameterArray)
			);
		}

		return R1MatrixUtil.Product (
			R1MatrixUtil.InvertUsingGaussianElimination (sampleResidualJacobian.jTransposeJ()),
			R1MatrixUtil.Scale1D (
				R1MatrixUtil.Product (sampleResidualJacobian.jTranspose(), residualValueArray),
				-1.
			) 
		);
	}

	/**
	 * <i>GaussNewtonEstimator</i> Constructor
	 * 
	 * @param parametricFunction <i>R1ToR1Parametric</i> Instance
	 * @param sample <i>R1R1Sample</i> Instance
	 * @param diagnosticsOn TRUE - Diagnostics has been Turned On
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public GaussNewtonEstimator (
		final R1ToR1Parametric parametricFunction,
		final R1R1Sample sample,
		final boolean diagnosticsOn)
		throws Exception
	{
		if (null == sample) {
			throw new Exception ("GaussNewtonEstimator Constructor => Invalid Inputs");
		}

		double[] xArray = sample.xArray();

		double[] yArray = sample.yArray();

		_diagnosticsOn = diagnosticsOn;
		R1ToR1Residual[] residualArray = new R1ToR1Residual[xArray.length];

		for (int sampleIndex = 0; sampleIndex < xArray.length; ++sampleIndex) {
			residualArray[sampleIndex] = new R1ToR1Residual (
				xArray[sampleIndex],
				yArray[sampleIndex],
				parametricFunction
			);
		}

		_r1ToR1EnsembleResidualSquared = new R1ToR1EnsembleResidualSquared (residualArray);
	}

	/**
	 * Retrieve the <i>R1ToR1EnsembleResidualSquared</i> Instance
	 * 
	 * @return <i>R1ToR1EnsembleResidualSquared</i> Instance
	 */

	public R1ToR1EnsembleResidualSquared r1ToR1EnsembleResidualSquared()
	{
		return _r1ToR1EnsembleResidualSquared;
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
	 * Estimate the Parameters using Least-Squares Method
	 * 
	 * @param initialParameterArray Initial Parameter Array
	 * 
	 * @return Parameters estimated using Least-Squares Method
	 */

	@Override public LeastSquaresRun estimationRun (
		final double[] initialParameterArray)
	{
		double[] residualValueArray =
			_r1ToR1EnsembleResidualSquared.residualValueArray (initialParameterArray);

		if (null == residualValueArray) {
			return null;
		}

		double currentSumOfResidualSquares = Double.NaN;

		try {
			currentSumOfResidualSquares = _r1ToR1EnsembleResidualSquared.evaluate (initialParameterArray);

			if (currentSumOfResidualSquares <= ABSOLUTE_CONVERGENCE_RATIO) {
				return LeastSquaresRun.Standard (
					initialParameterArray,
					currentSumOfResidualSquares
				);
			}
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}

		int iterationIndex = 0;
		boolean terminateIteration = false;
		double newSumOfResidualSquares = 0.;
		double sumOfResidualSquaresChange = RELATIVE_CONVERGENCE_RATIO * currentSumOfResidualSquares;

		LeastSquaresRun leastSquaresRun = _diagnosticsOn ?
			new LeastSquaresRunDiagnostics() : new LeastSquaresRun();

		double[] parameterArray = new double[initialParameterArray.length];

		for (int parameterIndex = 0; parameterIndex < initialParameterArray.length; ++parameterIndex) {
			parameterArray[parameterIndex] = initialParameterArray[parameterIndex];
		}

		while (!terminateIteration) {
			double[] parameterDeltaArray = parameterDeltaArray (
				leastSquaresRun,
				iterationIndex,
				parameterArray,
				residualValueArray
			);

			if (null == parameterDeltaArray) {
				return leastSquaresRun;
			}

			if (leastSquaresRun instanceof LeastSquaresRunDiagnostics) {
				((LeastSquaresRunDiagnostics) leastSquaresRun).setParameterDeltaArray (
					iterationIndex,
					parameterDeltaArray
				);
			}

			for (int parameterIndex = 0; parameterIndex < parameterDeltaArray.length; ++parameterIndex) {
				parameterArray[parameterIndex] += parameterDeltaArray[parameterIndex];
			}

			if (leastSquaresRun instanceof LeastSquaresRunDiagnostics) {
				((LeastSquaresRunDiagnostics) leastSquaresRun).setParameterArray (
					iterationIndex,
					parameterArray
				);
			}

			if (null == (
				residualValueArray = _r1ToR1EnsembleResidualSquared.residualValueArray (parameterArray)
			))
			{
				return null;
			}

			if (leastSquaresRun instanceof LeastSquaresRunDiagnostics) {
				((LeastSquaresRunDiagnostics) leastSquaresRun).setResidualValueArray (
					iterationIndex,
					residualValueArray
				);
			}

			try {
				newSumOfResidualSquares = _r1ToR1EnsembleResidualSquared.evaluate (parameterArray);
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}

			terminateIteration = Math.abs (newSumOfResidualSquares - currentSumOfResidualSquares) <
				sumOfResidualSquaresChange;

			if (leastSquaresRun instanceof LeastSquaresRunDiagnostics) {
				((LeastSquaresRunDiagnostics) leastSquaresRun).setSumOfResidualSquares (
					iterationIndex,
					newSumOfResidualSquares
				);
			}

			currentSumOfResidualSquares = newSumOfResidualSquares;
			++iterationIndex;
		}

		return leastSquaresRun.setParameterArray (parameterArray) &&
			leastSquaresRun.setSumOfResidualSquares (currentSumOfResidualSquares) ? leastSquaresRun : null;
	}
}
