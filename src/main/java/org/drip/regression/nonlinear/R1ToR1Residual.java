
package org.drip.regression.nonlinear;

import org.drip.numerical.common.NumberUtil;
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
 * <i>R1ToR1Residual</i> holds the Residual corresponding to a (x, y) Pair for a given Parameterized
 * 	Objective Function. The References are:
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

public class R1ToR1Residual
{
	private double _x = Double.NaN;
	private double _y = Double.NaN;
	private R1ToR1Parametric _parametricFunction = null;

	/**
	 * <i>R1ToR1Residual</i> Constructor
	 * 
	 * @param x X
	 * @param y Y
	 * @param parametricFunction <i>R1ToR1Parametric</i> Instance
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public R1ToR1Residual (
		final double x,
		final double y,
		final R1ToR1Parametric parametricFunction)
		throws Exception
	{
		if (!NumberUtil.IsValid (_x = x) ||
			!NumberUtil.IsValid (_y = y) ||
			null == (_parametricFunction = parametricFunction))
		{
			throw new Exception ("R1ToR1Residual Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve X
	 * 
	 * @return X
	 */

	public double x()
	{
		return _x;
	}

	/**
	 * Retrieve Y
	 * 
	 * @return Y
	 */

	public double y()
	{
		return _y;
	}

	/**
	 * Retrieve the <i>R1ToR1Parametric</i> Instance
	 * 
	 * @return <i>R1ToR1Parametric</i> Instance
	 */

	public R1ToR1Parametric parametricFunction()
	{
		return _parametricFunction;
	}

	/**
	 * Retrieve the Residual Value
	 * 
	 * @param parameterArray Array of Parameters
	 * 
	 * @return Residual Value
	 * 
	 * @throws Exception Thrown if the Residual cannot be calculated
	 */

	public double value (
		final double[] parameterArray)
		throws Exception
	{
		return _y - _parametricFunction.objectiveFunctionValue (parameterArray, _x);
	}

	/**
	 * Retrieve the Residual Jacobian given the Parameter Array
	 * 
	 * @param parameterArray Parameter Array
	 * 
	 * @return The Residual Jacobian
	 */

	public double[] jacobian (
		final double[] parameterArray)
	{
		double[] parameterJacobian = _parametricFunction.parameterJacobian (parameterArray, _x);

		if (null == parameterJacobian) {
			return null;
		}

		for (int parameterIndex = 0; parameterIndex < parameterJacobian.length; ++parameterIndex) {
			parameterJacobian[parameterIndex] *= -1;
		}

		return parameterJacobian;
	}

	/**
	 * Calculate the Residual Hessian given the Parameter Array
	 * 
	 * @param parameterArray Parameter Array
	 * 
	 * @return Residual Hessian given the Parameter Array
	 */

	public double[][] hessian (
		final double[] parameterArray)
	{
		double[][] parameterHessian = _parametricFunction.parameterHessian (parameterArray, _x);

		if (null == parameterHessian) {
			return null;
		}

		for (double[] parameterHessianRow : parameterHessian) {
			for (int rowIndex = 0; rowIndex < parameterHessianRow.length; ++rowIndex) {
				parameterHessianRow[rowIndex] *= -1.;
			}
		}

		return parameterHessian;
	}
}
