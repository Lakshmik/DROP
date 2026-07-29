
package org.drip.sample.optimizer;

import org.drip.function.rdtor1.Rosenbrock;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;

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
 * <i>Rosenbrock</i> illustrates the Construction and Usage of the Rosenbrock Function. The References are:
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
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/optimizer/README.md">Lagrangian/KKT Necessary Sufficient Conditions</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class RosenbrockFunction
{

	private static final void Evaluate (
		final Rosenbrock rosenbrock,
		final double x,
		final double y,
		final int xDecimals,
		final int yDecimals,
		final int rosenbrockDecimals)
		throws Exception
	{
		System.out.println (
			"\t|| {" + FormatUtil.FormatDouble (x, 1, xDecimals, 1.) + ", " +
			FormatUtil.FormatDouble (y, 1, yDecimals, 1.) + "} => " +
			FormatUtil.FormatDouble (rosenbrock.evaluate (x, y), 4, rosenbrockDecimals, 1.) + "   ||"
		);
	}

	/**
	 * Entry Point
	 * 
	 * @param argumentArray Command Line Argument Array
	 * 
	 * @throws Exception Thrown on Error/Exception Situation
	 */

	public static final void main (
		final String[] argumentArray)
		throws Exception
	{
		EnvManager.InitEnv ("");

		double xBegin = -2.;
		double xFinish = 2.;
		double yBegin = -1.;
		double yFinish = 3.;
		double xIncrement = 0.5;
		double yIncrement = 0.5;
		double x = xBegin;

		Rosenbrock rosenbrock = Rosenbrock.Standard();

		System.out.println ("\t||---------------------------||");

		System.out.println ("\t|| ROSENBROCK TEST FUNCTION  ||");

		System.out.println ("\t||---------------------------||");

		while (x < xFinish) {
			double y = yBegin;

			while (y < yFinish) {
				Evaluate (rosenbrock, x, y, 1, 1, 1);

				y += yIncrement;
			}

			x += xIncrement;
		}

		System.out.println ("\t||---------------------------||");

		EnvManager.TerminateEnv();
	}
}
