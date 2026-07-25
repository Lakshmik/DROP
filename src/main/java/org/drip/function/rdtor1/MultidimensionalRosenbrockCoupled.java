
package org.drip.function.rdtor1;

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
 * <i>MultidimensionalRosenbrockCoupled</i> implements the Sum of N Coupled 2D <i>Rosenbrock</i> Problems.
 *  The References are:
 *
 *  <br>
 *  <ul>
 *  	<li>
 *  		Dixon, L. C. W., and D. J. Mills (1994): Effect of Rounding Errors on the Variable Metric Method
 *  			<i>Journal of Optimization Theory and Applications</i> <b>80</b> 175-179
 *  	</li>
 *  	<li>
 *  		Kok, S., and C. Sandrock (2009): Locating and Characterizing the Stationary Points of the
 *  			Extended Rosenbrock Function <i>Evolutionary Computation</i> <b>17 (3)</b> 437-453
 *  	</li>
 *  	<li>
 *  		Pagani, F., M. Wiegand, and S. Nadarajah (2022): An n-dimensional Rosenbrock Distribution for
 *  			Markov Chain Monte-Carlo Testing <i>Scandinavian Journal of Statistics</i> <b>49 (2)</b>
 *  			657-680
 *  	</li>
 *  	<li>
 *  		Rosenbrock, H. H. (1960): An Automatic Method for Finding the Greatest or the Least Value of a
 *  			Function <i>Computer Journal</i> <b>3 (3)</b> 175-184
 *  	</li>
 *  	<li>
 *  		Wikipedia (2026): Rosenbrock Function https://en.wikipedia.org/wiki/Rosenbrock_function
 *  	</li>
 *  </ul>
 *
 *	<br><br>
 *  <ul>
 *		<li><b>Module </b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></li>
 *		<li><b>Library</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/NumericalOptimizerLibrary.md">Numerical Optimizer Library</a></li>
 *		<li><b>Project</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></li>
 *		<li><b>Package</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/sample/optimizer/README.md">Lagrangian/KKT Necessary Sufficient Conditions</a></li>
 *  </ul>
 *
 * @author Lakshmi Krishnamurthy
 */

public class MultidimensionalRosenbrockCoupled extends MultidimensionalRosenbrock
{

	/**
	 * Construct a Standard Instance of <i>MultidimensionalRosenbrockCoupled</i>
	 * 
	 * @param problemCount Number of Coupled 2D <i>Rosenbrock</i> Problems
	 * 
	 * @return Standard Instance of <i>MultidimensionalRosenbrockCoupled</i>
	 */

	public static MultidimensionalRosenbrockCoupled Standard (
		final int problemCount)
	{
		try {
			return new MultidimensionalRosenbrockCoupled (Rosenbrock.Standard(), problemCount);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * <i>MultidimensionalRosenbrockCoupled</i> Constructor
	 * 
	 * @param rosenbrock Underlying <i>Rosenbrock</i> Instance
	 * @param problemCount Number of Coupled 2D <i>Rosenbrock</i> Problems
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public MultidimensionalRosenbrockCoupled (
		final Rosenbrock rosenbrock,
		final int problemCount)
		throws Exception
	{
		super (rosenbrock, problemCount);
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
		if (null == variateArray || dimension() != variateArray.length || !NumberUtil.IsValid (variateArray))
		{
			throw new Exception ("MultidimensionalRosenbrockCoupled::evaluate => Invalid Inputs");
		}

		Rosenbrock rosenbrock = rosenbrock();

		double b = rosenbrock.b();

		double a = rosenbrock.a();

		double value = 0.;

		for (int i = 0; i < variateArray.length - 1; ++i) {
			double f1 = variateArray[i + 1] - variateArray[i] * variateArray[i];
			double f2 = 1. - variateArray[i];
			value += a * f1 * f1 + b * f2 * f2;
		}

		return value;
	}
}
