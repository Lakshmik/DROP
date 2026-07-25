
package org.drip.sample.optimizer;

import org.drip.function.rdtor1.Himmelblau;
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
 * <i>Himmelblau</i> illustrates the Construction and Usage of the Himmelblau Function. The References are:
 *
 *  <br>
 *  <ul>
 *  	<li>
 *  		Himmelblau, D. (1972): <i>Applied Non-linear Programming</i> <b>McGraw-Hill</b> Columbus OH
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

public class HimmelblauFunction
{

	private static final void Evaluate (
		final Himmelblau himmelblau,
		final double x,
		final double y,
		final int xDecimals,
		final int yDecimals,
		final int himmelblauDecimals)
		throws Exception
	{
		System.out.println (
			"\t|| {" + FormatUtil.FormatDouble (x, 1, xDecimals, 1.) + ", " +
			FormatUtil.FormatDouble (y, 1, yDecimals, 1.) + "} => " +
			FormatUtil.FormatDouble (himmelblau.evaluate (x, y), 3, himmelblauDecimals, 1.) + " ||"
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

		double xBegin = -4.;
		double xFinish = 4.;
		double yBegin = -3.5;
		double yFinish = 3.5;
		double xIncrement = 0.5;
		double yIncrement = 0.5;
		double x = xBegin;

		Himmelblau himmelblau = new Himmelblau();

		System.out.println ("\t||---------------------------||");

		System.out.println ("\t|| HIMMELBLAU TEST FUNCTION  ||");

		System.out.println ("\t||---------------------------||");

		while (x < xFinish) {
			double y = yBegin;

			while (y < yFinish) {
				Evaluate (himmelblau, x, y, 6, 6, 3);

				y += yIncrement;
			}

			x += xIncrement;
		}

		System.out.println ("\t||---------------------------||");

		System.out.println ("\n\t||------------------------------------||");

		Evaluate (himmelblau, -3.779310, -3.283186, 6, 6, 3);

		Evaluate (himmelblau, -2.805118, 3.131312, 6, 6, 3);

		Evaluate (himmelblau, -0.270845, -0.923039, 6, 6, 3);

		Evaluate (himmelblau, 3.584428, -1.848126, 6, 6, 3);

		System.out.println ("\t||------------------------------------||");

		EnvManager.TerminateEnv();
	}
}
