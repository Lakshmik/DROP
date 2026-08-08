
package org.drip.regression.nonlinear;

import java.util.HashMap;

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
 * <i>GaussNewtonRunDiagnostics</i> holds the Results of a Gauss-Newton Least-Squares Calibration Diagnostic
 * 	Run. The References are:
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

public class GaussNewtonRunDiagnostics
	extends GaussNewtonRun
{
	private HashMap<Integer, GaussNewtonIterationDiagnostics> _gaussNewtonIterationDiagnosticsMap = null;

	/**
	 * Empty <i>GaussNewtonRunDiagnostics</i> Constructor
	 */

	public GaussNewtonRunDiagnostics()
	{
		_gaussNewtonIterationDiagnosticsMap = new HashMap<Integer, GaussNewtonIterationDiagnostics>();
	}

	/**
	 * Retrieve the Map of <i>GaussNewtonIterationDiagnostics</i> Instances
	 * 
	 * @return Map of <i>GaussNewtonIterationDiagnostics</i> Instances
	 */

	public HashMap<Integer, GaussNewtonIterationDiagnostics> gaussNewtonIterationDiagnosticsMap()
	{
		return _gaussNewtonIterationDiagnosticsMap;
	}

	/**
	 * Set the Array of the Calibrated Parameter Changes
	 * 
	 * @param iterationIndex Iteration Index
	 * @param parameterDeltaArray Array of the Calibrated Parameter Changes
	 * 
	 * @return TRUE - The Array of the Calibrated Parameter Changes Successfully Set
	 */

	public boolean setParameterDeltaArray (
		final int iterationIndex,
		final double[] parameterDeltaArray)
	{
		if (_gaussNewtonIterationDiagnosticsMap.containsKey (iterationIndex)) {
			return _gaussNewtonIterationDiagnosticsMap.get (
				iterationIndex
			).setParameterDeltaArray (
				parameterDeltaArray
			);
		}

		GaussNewtonIterationDiagnostics gaussNewtonIterationDiagnostics =
			new GaussNewtonIterationDiagnostics();

		_gaussNewtonIterationDiagnosticsMap.put (iterationIndex, gaussNewtonIterationDiagnostics);

		return gaussNewtonIterationDiagnostics.setParameterDeltaArray (parameterDeltaArray);
	}

	/**
	 * Set the Array of the Calibrated Parameters
	 * 
	 * @param iterationIndex Iteration Index
	 * @param parameterArray Array of the Calibrated Parameters
	 * 
	 * @return TRUE - The Array of the Calibrated Parameters Successfully Set
	 */

	public boolean setParameterArray (
		final int iterationIndex,
		final double[] parameterArray)
	{
		if (_gaussNewtonIterationDiagnosticsMap.containsKey (iterationIndex)) {
			return _gaussNewtonIterationDiagnosticsMap.get (
				iterationIndex
			).setParameterArray (
				parameterArray
			);
		}

		GaussNewtonIterationDiagnostics gaussNewtonIterationDiagnostics =
			new GaussNewtonIterationDiagnostics();

		_gaussNewtonIterationDiagnosticsMap.put (iterationIndex, gaussNewtonIterationDiagnostics);

		return gaussNewtonIterationDiagnostics.setParameterArray (parameterArray);
	}

	/**
	 * Set the Array of the Residual Values
	 * 
	 * @param iterationIndex Iteration Index
	 * @param residualValueArray Array of the Residual Values
	 * 
	 * @return TRUE - The Array of the Residual Values Successfully Set
	 */

	public boolean setResidualValueArray (
		final int iterationIndex,
		final double[] residualValueArray)
	{
		if (_gaussNewtonIterationDiagnosticsMap.containsKey (iterationIndex)) {
			return _gaussNewtonIterationDiagnosticsMap.get (
				iterationIndex
			).setResidualValueArray (
				residualValueArray
			);
		}

		GaussNewtonIterationDiagnostics gaussNewtonIterationDiagnostics =
			new GaussNewtonIterationDiagnostics();

		_gaussNewtonIterationDiagnosticsMap.put (iterationIndex, gaussNewtonIterationDiagnostics);

		return gaussNewtonIterationDiagnostics.setResidualValueArray (residualValueArray);
	}

	/**
	 * Set the Sum of Residual Squares
	 * 
	 * @param iterationIndex Iteration Index
	 * @param sumOfResidualSquares Sum of Residual Squares
	 * 
	 * @return TRUE - The Sum of Residual Squares Successfully Set
	 */

	public boolean setSumOfResidualSquares (
		final int iterationIndex,
		final double sumOfResidualSquares)
	{
		if (_gaussNewtonIterationDiagnosticsMap.containsKey (iterationIndex)) {
			return _gaussNewtonIterationDiagnosticsMap.get (
				iterationIndex
			).setSumOfResidualSquares (
				sumOfResidualSquares
			);
		}

		GaussNewtonIterationDiagnostics gaussNewtonIterationDiagnostics =
			new GaussNewtonIterationDiagnostics();

		_gaussNewtonIterationDiagnosticsMap.put (iterationIndex, gaussNewtonIterationDiagnostics);

		return gaussNewtonIterationDiagnostics.setSumOfResidualSquares (sumOfResidualSquares);
	}

	/**
	 * 'JSON-ize' the State
	 * 
	 * @param prefix The JSON Prefix
	 * 
	 * @return The 'JSON-ize'd State
	 */

	public String toString (
		final String prefix)
	{
		return prefix + "(" +
			"Run: " + super.toString ("") + "; " +
			"Diagnostics Entry: " + _gaussNewtonIterationDiagnosticsMap +
		")";
	}

	/**
	 * 'JSON-ize' the State
	 * 
	 * @return The 'JSON-ize'd State
	 */

	public @Override String toString()
	{
		return toString ("");
	}
}
