
package org.drip.regression.nonlinear;

import org.drip.function.rdtor1solver.NewtonFixedPointFinder;
import org.drip.function.rdtor1solver.VariateInequalityConstraintMultiplier;
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
 * <i>LeastSquaresHessianEstimator</i> implements the Non-linear Least-Squares Regression using the Hessian
 * 	of the Sum of Squared Residuals. The References are:
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

public class LeastSquaresHessianEstimator
{
	private boolean _diagnosticsOn = false;
	private LeastSquaresHessianControl _control = null;
	private R1ToR1EnsembleResidualSquared _r1ToR1EnsembleResidualSquared = null;

	private double[] updatedParameterArray (
		final double[] initialParameterArray)
	{
		try {
			return new NewtonFixedPointFinder (
				_r1ToR1EnsembleResidualSquared,
				_control.lineStepEvolution(),
				_control.convergence()
			).convergeVariate (
				new VariateInequalityConstraintMultiplier (false, initialParameterArray, null)
			).problemVariableArray();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * <i>LeastSquaresHessianEstimator</i> Constructor
	 * 
	 * @param parametricFunction <i>R1ToR1Parametric</i> Instance
	 * @param sample <i>R1R1Sample</i> Instance
	 * @param control Gauss-Newton Scheme Control
	 * @param diagnosticsOn TRUE - Diagnostics has been Turned On
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public LeastSquaresHessianEstimator (
		final R1ToR1Parametric parametricFunction,
		final R1R1Sample sample,
		final LeastSquaresHessianControl control,
		final boolean diagnosticsOn)
		throws Exception
	{
		if (null == sample || null == (_control = control)) {
			throw new Exception ("LeastSquaresHessianEstimator Constructor => Invalid Inputs");
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
	 * Retrieve the <i>GaussNewtonControl</i> Instance
	 * 
	 * @return <i>GaussNewtonControl</i> Instance
	 */

	public LeastSquaresHessianControl control()
	{
		return _control;
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

	public LeastSquaresRun estimationRun (
		final double[] initialParameterArray)
	{
		LeastSquaresRun leastSquaresRun = _diagnosticsOn ?
			new LeastSquaresRunDiagnostics() : new LeastSquaresRun();

		double[] parameterArray = updatedParameterArray (initialParameterArray);

		if (null == parameterArray) {
			return leastSquaresRun;
		}

		try {
			leastSquaresRun.setParameterArray (parameterArray);

			leastSquaresRun.setSumOfResidualSquares (
				_r1ToR1EnsembleResidualSquared.evaluate (parameterArray)
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return leastSquaresRun;
	}
}
